package com.enterrupt.antlr4;

import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.buffers.*;
import com.enterrupt.pt.*;
import com.enterrupt.pt.peoplecode.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.Interval;
import com.enterrupt.runtime.*;
import com.enterrupt.trace.*;
import org.apache.logging.log4j.*;
import com.enterrupt.antlr4.frontend.*;

class EvaluateConstruct {
	public PTType baseExpr;
	public boolean hasBranchBeenEmitted = false;
	public boolean trueBranchExprSeen = false;
	public boolean breakSeen = false;
	public EvaluateConstruct(PTType p) { this.baseExpr = p; }
}

enum InterruptFlag {
	BREAK
}

public class InterpreterVisitor extends PeopleCodeBaseVisitor<Void> {

	private ExecContext eCtx;
	private CommonTokenStream tokens;
	private InterpretSupervisor supervisor;

	private ParseTreeProperty<PTType> nodeData;
	private ParseTreeProperty<Callable> nodeCallables;
	private Stack<EvaluateConstruct> evalConstructStack;
	private InterruptFlag interrupt;
	private IEmission lastEmission;
	private AccessLevel blockAccessLvl;
	private boolean hasVarDeclBeenEmitted = false;

	private static Logger log = LogManager.getLogger(InterpreterVisitor.class.getName());

	public InterpreterVisitor(ExecContext e, InterpretSupervisor s) {
		this.eCtx = e;
		this.tokens = e.prog.tokenStream;
		this.supervisor = s;
		this.nodeData = new ParseTreeProperty<PTType>();
		this.nodeCallables = new ParseTreeProperty<Callable>();
		this.evalConstructStack = new Stack<EvaluateConstruct>();
	}

	private void setNodeData(ParseTree node, PTType a) {
		this.nodeData.put(node, a);
	}

	private PTType getNodeData(ParseTree node) {
		return this.nodeData.get(node);
	}

	private void setNodeCallable(ParseTree node, Callable c) {
		this.nodeCallables.put(node, c);
	}

	private Callable getNodeCallable(ParseTree node) {
		return this.nodeCallables.get(node);
	}

	// Bubble-up operations should not fail in the event of nulls, unlike normal accesses.
	private void bubbleUp(ParseTree src, ParseTree dest) {
		if(this.nodeData.get(src) != null) {
			this.nodeData.put(dest, this.nodeData.get(src));
		}
		if(this.nodeCallables.get(src) != null) {
			this.nodeCallables.put(dest, this.nodeCallables.get(src));
		}
	}

	private void emitStmt(ParserRuleContext ctx) {
		StringBuffer line = new StringBuffer();
		Interval interval = ctx.getSourceInterval();

		int i = interval.a;
		while(i <= interval.b) {
			Token t = tokens.get(i);
			if(t.getChannel() == PeopleCodeLexer.REFERENCES) { i++; continue; }
			if(t.getText().contains("\n")) { break; }
			line.append(t.getText());
			i++;
		}

		IEmission e = new PCInstruction(line.toString());
		TraceFileVerifier.submitEmission(e);
		this.lastEmission = e;
	}

	private void emitStmt(String str) {

		/**
		 * Don't emit an End-If statement after exiting a For loop
		 * or If construct.
		 */
		if(str.equals("End-If") &&
				this.lastEmission instanceof PCInstruction) {
			if(((PCInstruction)this.lastEmission).instruction
				.startsWith("For") ||
			   ((PCInstruction)this.lastEmission).instruction
				.equals("End-If")) {
				return;
			}
		}
		IEmission e = new PCInstruction(str);
		TraceFileVerifier.submitEmission(e);
		this.lastEmission = e;
	}

	private void repeatLastEmission() {
		TraceFileVerifier.submitEmission(this.lastEmission);
	}

	public Void visitProgram(PeopleCodeParser.ProgramContext ctx) {

		/**
		 * App class programs do not get a fresh scope b/c they
		 * are always loaded to manipulate an existing object's scope, which
		 * has already been placed on the scope stack for this exec context. All
		 * other programs get a fresh program-local scope.
		 */
		if(!(this.eCtx.prog instanceof AppClassPeopleCodeProg)) {
			this.eCtx.pushScope(new Scope(Scope.Lvl.PROGRAM_LOCAL));
		}

		visit(ctx.stmtList());
		this.eCtx.popScope();
		return null;
	}

	public Void visitClassDeclaration(PeopleCodeParser.ClassDeclarationContext ctx) {
		((AppClassPeopleCodeProg)this.eCtx.prog).appClassName = ctx.GENERIC_ID().getText();
		for(PeopleCodeParser.ClassBlockContext bCtx : ctx.classBlock()) {
			visit(bCtx);
		}
		return null;
	}

	public Void visitClassBlock(PeopleCodeParser.ClassBlockContext ctx) {
		if(ctx.aLvl != null) {
			switch(ctx.aLvl.getText()) {
				case "private":
					this.blockAccessLvl = AccessLevel.PRIVATE;	break;
				case "public":
					this.blockAccessLvl = AccessLevel.PUBLIC;	break;
				default:
					throw new EntVMachRuntimeException("Unknown access level modifier " +
						"encountered: " + ctx.aLvl.getText());
			}
		} else {
			// Blocks without an access level modifier are public by default.
			this.blockAccessLvl = AccessLevel.PUBLIC;
		}
		for(PeopleCodeParser.ClassBlockStmtContext cbsCtx : ctx.classBlockStmt()) {
			visit(cbsCtx);
		}
		return null;
	}

	public Void visitInstance(PeopleCodeParser.InstanceContext ctx) {
		visit(ctx.varType());

		if(this.blockAccessLvl != AccessLevel.PRIVATE) {
			throw new EntVMachRuntimeException("Expected instance declaration to be " +
				"private; actual access level is: " + this.blockAccessLvl);
		}

		PTType type = getNodeData(ctx.varType());
		for(TerminalNode varId : ctx.VAR_ID()) {
			((AppClassPeopleCodeProg)this.eCtx.prog).addInstanceIdentifier(
				varId.getText(), type);
		}
		return null;
	}

	public Void visitProperty(PeopleCodeParser.PropertyContext ctx) {
		visit(ctx.varType());

		if(this.blockAccessLvl != AccessLevel.PUBLIC) {
			throw new EntVMachRuntimeException("Expected property declaration to be " +
				"publicly accessible; actual access level is: " + this.blockAccessLvl);
		}

		String id = ctx.GENERIC_ID().getText();
		PTType type = getNodeData(ctx.varType());
		boolean hasGetter = (ctx.g != null);
		boolean hasSetter = (ctx.s != null);
		boolean isReadOnly = (ctx.r != null);

		if(hasSetter || isReadOnly) {
			throw new EntVMachRuntimeException("Need to support property setters and/or "+
				"readonly properties: " + ctx.getText());
		}

		((AppClassPeopleCodeProg)this.eCtx.prog).addPropertyIdentifier(
			id, type, hasGetter);
		return null;
	}

	public Void visitMethod(PeopleCodeParser.MethodContext ctx) {

		visit(ctx.formalParamList());
		List<FormalParam> formalParams = new ArrayList<FormalParam>();
		for(PeopleCodeParser.ParamContext pCtx : ctx.formalParamList().param()) {
			formalParams.add(new FormalParam(
				getNodeData(pCtx.varType()), pCtx.VAR_ID().getText()));
		}

		PTType rType = null;
		if(ctx.returnType() != null) {
			visit(ctx.returnType());
			rType = getNodeData(ctx.returnType().varType());
		}

		((AppClassPeopleCodeProg)this.eCtx.prog).addMethod(
			this.blockAccessLvl, ctx.GENERIC_ID().getText(), formalParams, rType);
		return null;
	}

	/**********************************************************
	 * <stmt> alternative handlers.
	 **********************************************************/

	public Void visitIfStmt(PeopleCodeParser.IfStmtContext ctx) {
		this.emitStmt(ctx);

		// Get value of conditional expression.
		visit(ctx.expr());
		boolean exprResult = ((PTBoolean)getNodeData(ctx.expr())).read();

		// If expression evaluates to true, visit the conditional body;
		// otherwise, visit the Else body if it exists.
		if(exprResult) {
			visit(ctx.stmtList(0));
			this.emitStmt("End-If");
		} else {
			if(ctx.stmtList(1) != null) {
				visit(ctx.stmtList(1));
				this.emitStmt("End-If");
			}
		}

		return null;
	}

	public Void visitForStmt(PeopleCodeParser.ForStmtContext ctx) {
		this.emitStmt(ctx);

		if(ctx.expr(2) != null) {
			throw new EntVMachRuntimeException("Step clause encountered " +
				"in For construct; not yet supported.");
		}

		PTType varId = eCtx.resolveIdentifier(ctx.VAR_ID().getText());

		visit(ctx.expr(0));
		PTType initialExpr = getNodeData(ctx.expr(0));

		((PTPrimitiveType)varId).copyValueFrom((PTPrimitiveType)initialExpr);

		visit(ctx.expr(1));
		PTType toExpr = getNodeData(ctx.expr(1));

		do {
			visit(ctx.stmtList());
			((PTPrimitiveType)varId).add(Environment.getFromLiteralPool(1));
			this.emitStmt("End-For");
			this.emitStmt(ctx);
		} while(((PTPrimitiveType)varId).isLessThan((PTPrimitiveType)toExpr)
				== Environment.TRUE);

		return null;
	}

	public Void visitStmtBreak(PeopleCodeParser.StmtBreakContext ctx) {
		this.emitStmt(ctx);
		this.interrupt = InterruptFlag.BREAK;
		return null;
	}

	public Void visitStmtAssign(PeopleCodeParser.StmtAssignContext ctx) {
		this.emitStmt(ctx);
		visit(ctx.expr(1));
		PTType src = getNodeData(ctx.expr(1));
		visit(ctx.expr(0));
		PTType dst = getNodeData(ctx.expr(0));

		/**
		 * primitive = primitive : write from rhs to lhs, ignore identifier
		 * primitive = object : attempt cast from src type to destination type
		 * object = primitive : attempt cast from destination type to src type
		 * object = object : get var identifier, make it point to rhs object
		 */
		if(dst instanceof PTPrimitiveType && src instanceof PTPrimitiveType) {
			((PTPrimitiveType)dst).copyValueFrom((PTPrimitiveType)src);

		} else if(dst instanceof PTPrimitiveType && src instanceof PTObjectType) {
			//i.e., &str = SSR_STDNT_TERM0.EMPLID; field must be cast to string.
			PTPrimitiveType castedSrc = ((PTObjectType)src).castTo((PTPrimitiveType)dst);
			((PTPrimitiveType)dst).copyValueFrom(castedSrc);

		} else if(dst instanceof PTObjectType && src instanceof PTPrimitiveType) {
			//i.e., SSR_STNDT_TERM0.EMPLID = "5"; field must be cast to string.
			PTPrimitiveType castedDst = ((PTObjectType)dst).castTo((PTPrimitiveType)src);
			castedDst.copyValueFrom((PTPrimitiveType)src);

		} else if(dst instanceof PTObjectType && src instanceof PTObjectType) {
			// Assuming lhs is an identifier; this may or may not hold true long term.
			eCtx.assignToIdentifier(ctx.expr(0).getText(), src);

		} else {
			throw new EntVMachRuntimeException("Assignment failed; unexpected " +
				"type combination.");
		}

		return null;
	}

	public Void visitStmtExpr(PeopleCodeParser.StmtExprContext ctx) {
		this.emitStmt(ctx);
		visit(ctx.expr());
		return null;
	}

	public Void visitStmtReturn(PeopleCodeParser.StmtReturnContext ctx) {
		this.emitStmt(ctx);
		if(ctx.expr() != null) {
			visit(ctx.expr());
			PTType retVal = getNodeData(ctx.expr());

			if(this.eCtx instanceof AppClassObjExecContext) {
				if(((AppClassObjExecContext)this.eCtx).expectedReturnType
					.typeCheck(retVal)) {
					Environment.pushToCallStack(retVal);
				} else {
					throw new EntVMachRuntimeException("Value returned in app class "+
						"obj execution context does not match the expected type.");
				}
			} else {
				throw new EntVMachRuntimeException("Must type check return values " +
					"in non-app class execution contexts.");
			}
		}
		throw new EntReturnException(ctx.getText());
	}

	/**********************************************************
	 * <expr> alternative handlers.
	 **********************************************************/

	public Void visitExprParenthesized(PeopleCodeParser.ExprParenthesizedContext ctx) {
		visit(ctx.expr());
		bubbleUp(ctx.expr(), ctx);
		return null;
	}

	public Void visitExprLiteral(PeopleCodeParser.ExprLiteralContext ctx) {
		visit(ctx.literal());
		bubbleUp(ctx.literal(), ctx);
		return null;
	}

	public Void visitExprId(PeopleCodeParser.ExprIdContext ctx) {
		visit(ctx.id());
		bubbleUp(ctx.id(), ctx);
		return null;
	}

	public Void visitExprFnOrIdxCall(PeopleCodeParser.ExprFnOrIdxCallContext ctx) {

		visit(ctx.expr());

		Callable call = getNodeCallable(ctx.expr());
		PTType t = getNodeData(ctx.expr());

		// null is used to separate call frames.
		Environment.pushToCallStack(null);

		// if args exist, push them onto the call stack.
		if(ctx.exprList() != null) {
			visit(ctx.exprList());
			for(PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
				visit(argCtx);
				Environment.pushToCallStack(getNodeData(argCtx));
			}
		}

		/**
		 * If the node represents a rowset, this expression represents
	 	 * a row indexing operation (i.e., "&rs(1)"). Call getRow() to run
		 * the indexing operation.
		 */
		if(t instanceof PTRowset) {
			((PTRowset)t).getRow();
		} else if(call.ptMethod != null) {
			call.invokePtMethod();
		} else {
			ExecContext eCtx = call.eCtx;

			/**
			 * Record field references beyond a certain recursion level
			 * in app class programs have their constituent record defns
			 * loaded lazily. Before executing the app class method, check
			 * if any of the references within programs referenced by the app class
			 * have yet to have their record defns loaded (note that they may
			 * already be loaded anyway if they were referenced elsewhere).
			 */
			for(PeopleCodeProg p : eCtx.prog.referencedProgs) {
				for(Map.Entry<Integer, Reference> cursor : p.progRefsTable.entrySet()) {
					if(cursor.getValue().isRecordFieldRef) {
						DefnCache.getRecord(cursor.getValue().RECNAME);
					}
				}
			}
			this.supervisor.runImmediately(eCtx);
		}

		/**
		 * Pop the first value from the call stack. If it's null, the function
		 * did not emit a return value. If it's non-null, the next item on the stack
		 * must be the null separator (PeopleCode funcs can only return 1 value).
		 */
	    PTType a = Environment.popFromCallStack();
		if(a != null) {
			setNodeData(ctx, a);
		}

		if(a != null && (Environment.popFromCallStack() != null)) {
			throw new EntVMachRuntimeException("More than one return value " +
				"was found on the call stack.");
		}

		return null;
	}

	public Void visitExprAddSub(PeopleCodeParser.ExprAddSubContext ctx) {

		visit(ctx.expr(0));
		PTPrimitiveType lhs = (PTPrimitiveType)getNodeData(ctx.expr(0));
		visit(ctx.expr(1));
		PTPrimitiveType rhs = (PTPrimitiveType)getNodeData(ctx.expr(1));

		if(ctx.a != null) {
			setNodeData(ctx, lhs.add(rhs));

		} else if(ctx.s != null) {
			throw new EntVMachRuntimeException("Subtraction is not supported.");

		} else {
			throw new EntVMachRuntimeException("Unsupported add/sub operation.");
		}

		return null;
	}

	public Void visitExprCreate(PeopleCodeParser.ExprCreateContext ctx) {
		visit(ctx.createInvocation());
		bubbleUp(ctx.createInvocation(), ctx);
		return null;
	}

	public Void visitExprDotAccess(
					PeopleCodeParser.ExprDotAccessContext ctx) {

		visit(ctx.expr());
		visit(ctx.id());

		PTObjectType obj = (PTObjectType)getNodeData(ctx.expr());
		PTType prop = obj.dotProperty(ctx.id().getText());
		Callable call = obj.dotMethod(ctx.id().getText());

		if(prop == null && call == null) {
			throw new EntVMachRuntimeException("Failed to resolve identifier (" +
				ctx.id().getText() + ") to a property and/or method for object: " +
				obj);
		}

		setNodeData(ctx, prop);
		setNodeCallable(ctx, call);

		/**
		 * If the identifier points to a getter on an app class object,
		 * the getter should be run immediately in order to set the node data
		 * for this context object.
		 */
		if(call != null && call.eCtx != null &&
				call.eCtx instanceof AppClassObjGetterExecContext) {
			this.supervisor.runImmediately(call.eCtx);

			List<PTType> args = Environment.getArgsFromCallStack();
			if(args.size() != 1) {
				throw new EntVMachRuntimeException("Getter should return exactly " +
					"one value.");
	        }
			setNodeData(ctx, args.get(0));
			this.repeatLastEmission();
		}

		return null;
	}

	public Void visitExprArrayIndex(PeopleCodeParser.ExprArrayIndexContext ctx) {

		visit(ctx.expr());
		PTArray arrayObj = (PTArray)getNodeData(ctx.expr());

		visit(ctx.exprList());
		PTType index = null;
		for(PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
			visit(argCtx);
			if(index != null) {
				throw new EntVMachRuntimeException("Multiple array indexes "+
					"is not yet supported.");
			}
			index = getNodeData(argCtx);
			break;
		}

		log.debug("About to index into {} with index {}.",
			arrayObj, index);

		setNodeData(ctx, arrayObj.getElement(index));
		return null;
	}

	public Void visitExprComparison(PeopleCodeParser.ExprComparisonContext ctx) {
		visit(ctx.expr(0));
		PTPrimitiveType a1 = (PTPrimitiveType)getNodeData(ctx.expr(0));
		visit(ctx.expr(1));
		PTPrimitiveType a2 = (PTPrimitiveType)getNodeData(ctx.expr(1));

		PTBoolean result;
		if(ctx.l != null) {
			throw new EntVMachRuntimeException("Less than not implemented.");

		} else if(ctx.g != null) {
			result = a1.isGreaterThan(a2);
			log.debug("isGreaterThan: {}? {}", ctx.getText(), result);

		} else if(ctx.ge != null) {
			throw new EntVMachRuntimeException("Greater or equal not implemented.");

		} else if(ctx.le != null) {
			throw new EntVMachRuntimeException("Less or equal not implemented.");

		} else {
			throw new EntVMachRuntimeException("Unknown comparison " +
				"operation encountered.");
		}
		setNodeData(ctx, result);
		return null;
	}

	public Void visitExprEquality(PeopleCodeParser.ExprEqualityContext ctx) {
		visit(ctx.expr(0));
		PTPrimitiveType a1 = (PTPrimitiveType)getNodeData(ctx.expr(0));
		visit(ctx.expr(1));
		PTPrimitiveType a2 = (PTPrimitiveType)getNodeData(ctx.expr(1));

		PTBoolean result;
		if(ctx.e != null) {
			result = a1.isEqual(a2);
			log.debug("isEqual: {}? {}", ctx.getText(), result);

		} else if(ctx.i != null) {
			throw new EntVMachRuntimeException("Inequality comparison not implemented.");

		} else {
			throw new EntVMachRuntimeException("Unknown equality " +
				"operation encountered.");
		}
		setNodeData(ctx, result);
		return null;
	}

	public Void visitExprBoolean(PeopleCodeParser.ExprBooleanContext ctx) {

		if(ctx.op.getText().equals("Or")) {
			visit(ctx.expr(0));
			PTBoolean lhs = (PTBoolean)getNodeData(ctx.expr(0));

			/**
			 * Short-circuit evaluation: if lhs is true, this expression is true,
			 * otherwise evaluate rhs and bubble up its value.
			 */
			if(lhs.read()) {
				setNodeData(ctx, Environment.TRUE);
			} else {
				visit(ctx.expr(1));
				setNodeData(ctx, getNodeData(ctx.expr(1)));
			}
		} else if(ctx.op.getText().equals("And")) {
			visit(ctx.expr(0));
			PTBoolean lhs = (PTBoolean)getNodeData(ctx.expr(0));
			visit(ctx.expr(1));
			PTBoolean rhs = (PTBoolean)getNodeData(ctx.expr(1));

			if(lhs.read() && rhs.read()) {
				setNodeData(ctx, Environment.TRUE);
			} else {
				setNodeData(ctx, Environment.FALSE);
			}
		} else {
			throw new EntInterpretException("Unsupported boolean comparison operation",
				ctx.getText(), ctx.getStart().getLine());
		}

		return null;
	}

	/**********************************************************
	 * Primary rule handlers.
	 **********************************************************/

	public Void visitMethodImpl(PeopleCodeParser.MethodImplContext ctx) {
		this.emitStmt(ctx);

		Scope localScope = new Scope(Scope.Lvl.METHOD_LOCAL);
		List<FormalParam> formalParams = ((AppClassPeopleCodeProg)eCtx.prog).methodTable.
			get(ctx.GENERIC_ID().getText()).formalParams;

		/**
		 * Ensure that each of the arguments passed to the method match
		 * the types of the formal parameters, and assign (by reference) those args
		 * to the identifiers in the formal parameter list of the method signature.
		 * If there's a mismatch, attempt to cast the argument provided to the type
		 * specified in the formal parameter definition.
		 */
		for(FormalParam fp : formalParams) {
			PTType arg = Environment.popFromCallStack();
			if(fp.type.typeCheck(arg)) {
				localScope.declareVar(fp.id, arg);
			} else {
				localScope.declareVar(fp.id,
					((PTObjectType)arg).castTo((PTPrimitiveType)fp.type));
			}
		}

		eCtx.pushScope(localScope);
		visit(ctx.stmtList());
		eCtx.popScope();

		this.emitStmt("end-method");
		return null;
	}

	public Void visitGetImpl(PeopleCodeParser.GetImplContext ctx) {
		this.emitStmt(ctx);

		Scope localScope = new Scope(Scope.Lvl.METHOD_LOCAL);
		eCtx.pushScope(localScope);
		visit(ctx.stmtList());
		eCtx.popScope();

		return null;
	}

	public Void visitId(PeopleCodeParser.IdContext ctx) {

		if(ctx.SYS_VAR_ID() != null) {
			/**
			 * Catch use of %This, which points to the app class obj
			 * loaded in the current execution context.
			 */
			if(ctx.SYS_VAR_ID().getText().equals("%This")) {
				setNodeData(ctx, ((AppClassObjExecContext)this.eCtx).appClassObj);
			} else {
				setNodeData(ctx, Environment.getSystemVar(ctx.SYS_VAR_ID().getText()));
			}

		} else if(ctx.VAR_ID() != null) {
			PTType a = eCtx.resolveIdentifier(ctx.VAR_ID().getText());
			//log.debug("Resolved {} to {}.", ctx.VAR_ID().getText(), a);
			setNodeData(ctx, a);

		} else if(ctx.GENERIC_ID() != null) {

			/**
			 * IMPORTANT NOTE: I believe it is possible to override system functions.
			 * The checks on GENERIC_ID below should be run in order of lowest scope
			 * to highest scope for this reason.
			 */
			if(ComponentBuffer.searchRecord.recDefn
						.RECNAME.equals(ctx.GENERIC_ID().getText())) {
				/**
				 * Detect references to search record buffer.
				 */
				setNodeData(ctx, ComponentBuffer.searchRecord);

			} else if(PSDefn.defnLiteralReservedWordsTable.containsKey(
				ctx.GENERIC_ID().getText().toUpperCase())) {
				/**
				 * Detect defn literal reserved words (i.e.,
				 * "Menu" in "Menu.SA_LEARNER_SERVICES").
				 */
				setNodeData(ctx, Environment.DEFN_LITERAL);

			} else if(Environment.getSystemFuncPtr(ctx.GENERIC_ID().getText()) != null) {
				/**
				 * Detect system function references.
				 */
				setNodeCallable(ctx, Environment.getSystemFuncPtr(
					ctx.GENERIC_ID().getText()));

			} else if(DefnCache.hasRecord(ctx.GENERIC_ID().getText())) {
				/**
				 * Detect references to record field literals
				 * (i.e., those passed to Sort on Rowsets).
				 */
				setNodeData(ctx, PTRecordLiteral.getSentinel().alloc(
					DefnCache.getRecord(ctx.GENERIC_ID().getText())));

			} else {
				/**
				 * At this point, assume the GENERIC_ID is a FieldLiteral.
				 */
				setNodeData(ctx, PTFieldLiteral.getSentinel().alloc(
					ctx.GENERIC_ID().getText()));
			}
		}
		return null;
	}

	public Void visitLiteral(PeopleCodeParser.LiteralContext ctx) {

		if(ctx.IntegerLiteral() != null) {

			PTType ptr = Environment.getFromLiteralPool(
				new Integer(ctx.IntegerLiteral().getText()));
			setNodeData(ctx, ptr);

		} else if(ctx.BoolLiteral() != null) {

			String b = ctx.BoolLiteral().getText();
			if(b.equals("True") || b.equals("true")) {
				setNodeData(ctx, Environment.TRUE);
			} else {
				setNodeData(ctx, Environment.FALSE);
			}

		} else if(ctx.DecimalLiteral() != null) {
			throw new EntVMachRuntimeException("Encountered a decimal literal; need to create "
				+ "a BigDecimal memory pool and type.");

		} else if(ctx.StringLiteral() != null) {
			/**
			 * Strip surrounding double quotes from literal. Note that empty
			 * strings ("") are a possibility here.
			 */
			String str = ctx.StringLiteral().getText();
				setNodeData(ctx, Environment.getFromLiteralPool(
					str.substring(1, str.length() - 1)));

		} else {
			throw new EntVMachRuntimeException("Unable to resolve literal to a terminal node.");
		}

		return null;
	}

	public Void visitVarDeclaration(PeopleCodeParser.VarDeclarationContext ctx) {

		visit(ctx.varType());

		String scope = ctx.varScope.getText();
		PTType varType = getNodeData(ctx.varType());
		boolean didInitializeAnIdentifier = false;

		List<PeopleCodeParser.VarDeclaratorContext> varsToDeclare = ctx.varDeclarator();
		for(PeopleCodeParser.VarDeclaratorContext idCtx : varsToDeclare) {

			if(idCtx.expr() != null) {
				visit(idCtx.expr());
				PTType initialValue = getNodeData(idCtx.expr());
				if(varType.typeCheck(initialValue)) {
					log.debug("Initializing identifier ({}) with scope {} and value {}.",
						idCtx.VAR_ID().getText(), scope, initialValue);
					this.declareIdentifier(scope, idCtx.VAR_ID().getText(), initialValue);
					didInitializeAnIdentifier = true;
				} else {
					throw new EntVMachRuntimeException("Type mismatch between declared " +
						"variable type ("+varType+") and its initialized value ("
						+initialValue+").");
				}
			} else {
				/**
				 * Primitive variables must have space allocated for them
				 * immediately. Object variables should simply reference
				 * the sentinel type value present in their declaration statement.
				 */
				PTType t = varType;
				if(varType instanceof PTPrimitiveType) {
					t = ((PTPrimitiveType)varType).alloc();
				}
				log.debug("Declaring identifier ({}) with scope {} and type {}.",
					idCtx.VAR_ID().getText(), scope, t);
				this.declareIdentifier(scope, idCtx.VAR_ID().getText(), t);
			}

		}

		if(this.eCtx.prog instanceof AppClassPeopleCodeProg) {
			if(!this.hasVarDeclBeenEmitted || varType instanceof PTRowset
				|| varType instanceof PTAppClassObj || varType instanceof PTRecord
				|| didInitializeAnIdentifier) {
				this.emitStmt(ctx);
				this.hasVarDeclBeenEmitted = true;
			}
		}
		return null;
	}

	public Void visitVarType(PeopleCodeParser.VarTypeContext ctx) {

		if(ctx.appClassPath() != null) {
			visit(ctx.appClassPath());
			setNodeData(ctx, getNodeData(ctx.appClassPath()));
		} else {
			PTType nestedType = null;
			if(ctx.varType() != null) {
				if(!ctx.GENERIC_ID().getText().equals("array")) {
					throw new EntVMachRuntimeException("Encountered non-array var " +
						"type preceding 'of' clause: " + ctx.getText());
				}
				visit(ctx.varType());
				nestedType = getNodeData(ctx.varType());
			}

            PTType type;
            switch(ctx.GENERIC_ID().getText()) {
                case "array":
					if(nestedType instanceof PTArray) {
						type = PTArray.getSentinel(
							((PTArray)nestedType).dimensions+1,nestedType);
					} else {
						type = PTArray.getSentinel(1, nestedType);
					}
					break;
                case "string":
                    type = PTString.getSentinel();		break;
                case "date":
                    type = PTDate.getSentinel();		break;
                case "integer":
                    type = PTInteger.getSentinel();		break;
                case "Record":
                    type = PTRecord.getSentinel();		break;
                case "Rowset":
                    type = PTRowset.getSentinel();		break;
				case "number":
					type = PTNumber.getSentinel();		break;
				case "boolean":
					type = PTBoolean.getSentinel();		break;
                default:
                    throw new EntVMachRuntimeException("Unexpected data type: " +
                        ctx.GENERIC_ID().getText());
            }
			setNodeData(ctx, type);
		}
		return null;
	}

	public Void visitEvaluateStmt(PeopleCodeParser.EvaluateStmtContext ctx) {
		this.emitStmt(ctx);

		visit(ctx.expr());
		EvaluateConstruct evalConstruct = new EvaluateConstruct(
			getNodeData(ctx.expr()));
		this.evalConstructStack.push(evalConstruct);

		List<PeopleCodeParser.WhenBranchContext> branches = ctx.whenBranch();
		for(PeopleCodeParser.WhenBranchContext branchCtx : branches) {
			visit(branchCtx);
			if(this.interrupt == InterruptFlag.BREAK) {
				evalConstruct.breakSeen = true;
				this.interrupt = null;
				break;
			}
		}

		if(!evalConstruct.breakSeen && ctx.whenOtherBranch() != null) {
			visit(ctx.whenOtherBranch());
		}

		this.evalConstructStack.pop();

		return null;
	}

	public Void visitWhenBranch(PeopleCodeParser.WhenBranchContext ctx) {

		if(ctx.op != null) {
			throw new EntVMachRuntimeException("Encountered relational op in when "+
				"branch, not yet supported.");
		}

		EvaluateConstruct evalConstruct = this.evalConstructStack.peek();
		PTType p1 = evalConstruct.baseExpr;
		visit(ctx.expr());
		PTType p2 = getNodeData(ctx.expr());

		if(!evalConstruct.hasBranchBeenEmitted || !evalConstruct.trueBranchExprSeen) {
			this.emitStmt(ctx);
			evalConstruct.hasBranchBeenEmitted = true;
		}

		/**
		 * If a previous branch evaluated to true and we're here, that means
		 * execution continues to fall through all branches until either a break
		 * is seen, or the evaluate construct ends.
		 */
		if(evalConstruct.trueBranchExprSeen || p1.equals(p2)) {
			evalConstruct.trueBranchExprSeen = true;
			visit(ctx.stmtList());
		}

		return null;
	}

	public Void visitWhenOtherBranch(PeopleCodeParser.WhenOtherBranchContext ctx) {

		EvaluateConstruct evalConstruct = this.evalConstructStack.peek();
		this.emitStmt(ctx);

		/**
	 	 * Execution cannot fall through to When-Other; if no break was seen
		 * in a true branch body, the When-Other body should not run. Only run
		 * if no true branches were seen.
		 */
		if(!evalConstruct.trueBranchExprSeen) {
			visit(ctx.stmtList());
		}
		return null;
	}

	public Void visitAppClassPath(PeopleCodeParser.AppClassPathContext ctx) {
		AppClassPeopleCodeProg progDefn =
			(AppClassPeopleCodeProg)DefnCache.getProgram(("AppClassPC." +
				ctx.getText() + ".OnExecute").replaceAll(":","."));
		setNodeData(ctx, PTAppClassObj.getSentinel(progDefn));
		return null;
	}

	public Void visitCreateInvocation(PeopleCodeParser.CreateInvocationContext ctx) {

		PTAppClassObj objType;
		if(ctx.appClassPath() != null) {
			visit(ctx.appClassPath());
			objType = (PTAppClassObj)getNodeData(ctx.appClassPath());
		} else {
			throw new EntVMachRuntimeException("Encountered create invocation without " +
				"app class prefix; need to support this by resolving path to class.");
		}

		/**
		 * Instantation of an app class object requires that the instance and method
		 * information for the class be known ahead of time. This info is in the class
		 * declaration body at the start of the program. If the class declaration has
		 * not yet been processed, do so now.
		 */
		if(!objType.progDefn.hasClassDefnBeenLoaded) {
			ExecContext classDeclCtx = new AppClassDeclExecContext(objType);
			this.supervisor.runImmediately(classDeclCtx);
		}

		PTAppClassObj newObj = objType.alloc();
		setNodeData(ctx, newObj);

		/**
	     * Check for constructor; call it if it exists.
		 * TODO: If issues arise with this, I probably need to check the method
		 * declaration to ensure that the constructor (if it exists) is only called
	     * when the appropriate number of arguments are supplied.
		 */
		String constructorName = newObj.progDefn.appClassName;
		if(newObj.progDefn.methodImplStartNodes.containsKey(constructorName)) {

			/**
			 * Load arguments to constructor onto call stack if
			 * args have been provided.
			 */
			if(ctx.exprList() != null) {
				visit(ctx.exprList());
				for(PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
					visit(argCtx);
	                Environment.pushToCallStack(getNodeData(argCtx));
				}
			}

			ExecContext constructorCtx = new AppClassObjMethodExecContext(newObj,
				constructorName, newObj.progDefn
					.methodImplStartNodes.get(constructorName), null);
			this.supervisor.runImmediately(constructorCtx);
			this.repeatLastEmission();
		}

		return null;
	}

	private void declareIdentifier(String scope, String id, PTType a) {
		switch(scope) {
			case "Local":
				eCtx.declareLocalVar(id, a);
				break;
			case "Component":
				Environment.componentScope.declareVar(id, a);
				break;
			case "Global":
				Environment.globalScope.declareVar(id, a);
				break;
			default:
				throw new EntVMachRuntimeException("Encountered unexpected variable " +
					" scope: " + scope);
		}
	}
}

