package com.enterrupt.antlr4;

import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.types.*;
import com.enterrupt.buffers.*;
import com.enterrupt.pt.*;
import com.enterrupt.pt.peoplecode.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.Interval;
import com.enterrupt.runtime.*;
import com.enterrupt.trace.*;
import com.enterrupt.scope.*;
import org.apache.logging.log4j.*;
import com.enterrupt.antlr4.frontend.*;

class EvaluateConstruct {
	public PTDataType baseExpr;
	public boolean trueBranchExprSeen = false;
	public boolean breakSeen = false;
	public EvaluateConstruct(PTDataType p) { this.baseExpr = p; }
}

enum InterruptFlag {
	BREAK
}

public class InterpreterVisitor extends PeopleCodeBaseVisitor<Void> {

	private ExecContext eCtx;
	private CommonTokenStream tokens;
	private InterpretSupervisor supervisor;

	private ParseTreeProperty<Pointer> pointers;
	private Stack<EvaluateConstruct> evalConstructStack;
	private InterruptFlag interrupt;
	private IEmission lastEmission;

	private static Logger log = LogManager.getLogger(InterpreterVisitor.class.getName());

	public InterpreterVisitor(ExecContext e, InterpretSupervisor s) {
		this.eCtx = e;
		this.tokens = e.prog.tokenStream;
		this.supervisor = s;
		this.pointers = new ParseTreeProperty<Pointer>();
		this.evalConstructStack = new Stack<EvaluateConstruct>();
	}

	private void setPointer(ParseTree node, Pointer ptr) {
		if(ptr == null) {
			throw new EntVMachRuntimeException("Attempted to set the memory pointer to null " +
				"for a node: " + node.getText());
		}
		this.pointers.put(node, ptr);
	}

	private Pointer getPointer(ParseTree node) {
		if(this.pointers.get(node) == null) {
			throw new EntVMachRuntimeException("Attempted to get the memory pointer for " +
				"for a node, encountered null: " + node.getText());
		}
		return this.pointers.get(node);
	}

	// Bubble-up operations should not fail in the event of nulls, unlike normal accesses.
	private void bubbleUpPointer(ParseTree src, ParseTree dest) {
		if(this.pointers.get(src) != null) {
			this.pointers.put(dest, this.pointers.get(src));
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
		IEmission e = new PCInstruction(str);
		TraceFileVerifier.submitEmission(e);
		this.lastEmission = e;
	}

	private void repeatLastEmission() {
		TraceFileVerifier.submitEmission(this.lastEmission);
	}

	public Void visitProgram(PeopleCodeParser.ProgramContext ctx) {

		/**
		 * App class programs do not get a fresh ref envi b/c they
		 * are always loaded to manipulate an existing object's ref envi, which
		 * has already been placed on the ref envi stack for this exec context. All
		 * other programs get a fresh program-local referencing environment.
		 */
		if(!(this.eCtx.prog instanceof AppClassPeopleCodeProg)) {
			this.eCtx.pushRefEnvi(new LocalRefEnvi(LocalRefEnvi.Type.PROGRAM_LOCAL));
		}

		visit(ctx.stmtList());
		this.eCtx.popRefEnvi();
		return null;
	}

	/**********************************************************
	 * <stmt> alternative handlers.
	 **********************************************************/

	public Void visitStmtIf(PeopleCodeParser.StmtIfContext ctx) {
		this.emitStmt(ctx);

		// Get value of conditional expression.
		visit(ctx.ifStmt().expr());
		boolean exprResult = ((PTBoolean) getPointer(ctx.ifStmt()
			.expr()).dereference()).value();

		// If expression evaluates to true, visit the conditional body;
		// otherwise, visit the Else body if it exists.
		if(exprResult) {
			visit(ctx.ifStmt().stmtList(0));
			this.emitStmt("End-If");
		} else {
			if(ctx.ifStmt().stmtList(1) != null) {
				visit(ctx.ifStmt().stmtList(1));
			}
		}

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
		Pointer target = getPointer(ctx.expr(1));
		visit(ctx.expr(0));
		Pointer destPtr = getPointer(ctx.expr(0));

		/**
		 * Detect assignments to component buffer fields; i.e.,
		 * RECORD.FIELD = "4" should write "4" to the data type pointed
		 * to by the field, not the field itself.
		 */
		if(destPtr instanceof CBufferPointer &&
			destPtr.dereference() instanceof PTCBufferField) {
			PTCBufferField fld = (PTCBufferField) destPtr.dereference();
			fld.valuePtr.assign(target.dereference());
		} else {
			destPtr.assign(target.dereference());
		}

		return null;
	}

	public Void visitStmtExpr(PeopleCodeParser.StmtExprContext ctx) {
		this.emitStmt(ctx);
		visit(ctx.expr());
		return null;
	}

	/**********************************************************
	 * <expr> alternative handlers.
	 **********************************************************/

	public Void visitExprParenthesized(PeopleCodeParser.ExprParenthesizedContext ctx) {
		visit(ctx.expr());
		bubbleUpPointer(ctx.expr(), ctx);
		return null;
	}

	public Void visitExprLiteral(PeopleCodeParser.ExprLiteralContext ctx) {
		visit(ctx.literal());
		bubbleUpPointer(ctx.literal(), ctx);
		return null;
	}

	public Void visitExprId(PeopleCodeParser.ExprIdContext ctx) {
		visit(ctx.id());
		bubbleUpPointer(ctx.id(), ctx);
		return null;
	}

	public Void visitExprFnOrIdxCall(PeopleCodeParser.ExprFnOrIdxCallContext ctx) {

		visit(ctx.expr());

		PTDataType ptdt = getPointer(ctx.expr()).dereference();

		if(!(ptdt instanceof PTCallable || ptdt instanceof PTSysFunc)) {
			throw new EntVMachRuntimeException("Encountered non-callable data type "
				+ "in visit to fn or idx call node; this may be an unimplemented "
				+ "rowset indexing (i.e., &rs(1)) attempt.");
		}

		// null is used to separate call frames.
		Environment.pushToCallStack(null);

		// if args exist, move args from runtime stack to call stack.
		if(ctx.exprList() != null) {
			visit(ctx.exprList());
			for(PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
				visit(argCtx);
				Environment.pushToCallStack(getPointer(argCtx));
			}
		}


		if(ptdt instanceof PTSysFunc) {
			((PTSysFunc)ptdt).invoke();
		} else {
			ExecContext eCtx = ((PTCallable)ptdt).eCtx;

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
	    Pointer ptr = Environment.popFromCallStack();
		if(ptr != null) {
			setPointer(ctx, ptr);
		}

		if(ptr != null && (Environment.popFromCallStack() != null)) {
			throw new EntVMachRuntimeException("More than one return value was found on " +
				"the call stack.");
		}

		return null;
	}

	public Void visitExprCreate(PeopleCodeParser.ExprCreateContext ctx) {
		visit(ctx.createInvocation());
		bubbleUpPointer(ctx.createInvocation(), ctx);
		return null;
	}

	public Void visitExprDotAccess(
					PeopleCodeParser.ExprDotAccessContext ctx) {

		visit(ctx.expr());
		visit(ctx.id());

		Pointer exprPtr = getPointer(ctx.expr());
		Pointer accessedPtr = exprPtr.dereference().access(ctx.id().getText());
		setPointer(ctx, accessedPtr);

		return null;
	}

	public Void visitExprEquality(PeopleCodeParser.ExprEqualityContext ctx) {
		visit(ctx.expr(0));
		PTDataType p1 = getPointer(ctx.expr(0)).dereference();
		visit(ctx.expr(1));
		PTDataType p2 = getPointer(ctx.expr(1)).dereference();

		Pointer result;
		if(p1.equals(p2)) {
			result = Environment.TRUE;
		} else {
			result = Environment.FALSE;
		}
		setPointer(ctx, result);
		log.debug("Compared for equality: {}, result={}", ctx.getText(), result.dereference());
		return null;
	}

	public Void visitExprBoolean(PeopleCodeParser.ExprBooleanContext ctx) {

		if(ctx.op.getText().equals("Or")) {
			visit(ctx.expr(0));
			PTBoolean lhs = (PTBoolean)getPointer(ctx.expr(0)).dereference();

			/**
			 * Short-circuit evaluation: if lhs is true, this expression is true,
			 * otherwise evaluate rhs and bubble up its value.
			 */
			if(lhs.value()) {
				setPointer(ctx, Environment.TRUE);
			} else {
				visit(ctx.expr(1));
				setPointer(ctx, getPointer(ctx.expr(1)));
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

		RefEnvi localRefEnvi = new LocalRefEnvi(LocalRefEnvi.Type.METHOD_LOCAL);
		List<String> formalParams = ((AppClassPeopleCodeProg)eCtx.prog).methodFormalParams.
			get(ctx.GENERIC_ID().getText());

		if(formalParams != null) {
			for(String formalParamId : formalParams) {
				localRefEnvi.declareVar(formalParamId, Environment.popFromCallStack());
			}
		}

		eCtx.pushRefEnvi(localRefEnvi);
		visit(ctx.stmtList());
		eCtx.popRefEnvi();

		this.emitStmt("end-method");
		return null;
	}

	public Void visitId(PeopleCodeParser.IdContext ctx) {

		if(ctx.SYS_VAR_ID() != null) {
			Pointer ptr = Environment.getSystemVar(ctx.getText());
			setPointer(ctx, ptr);

		} else if(ctx.VAR_ID() != null) {
			Pointer ptr = eCtx.resolveIdentifier(ctx.getText());
			setPointer(ctx, ptr);

		} else if(ctx.GENERIC_ID() != null) {

			/**
			 * IMPORTANT NOTE: I believe it is possible to override system functions.
			 * The checks on GENERIC_ID below should be run in order of lowest scope
			 * to highest scope for this reason.
			 */
			if(((PTRecord)ComponentBuffer.searchRecordPtr.dereference()).recDefn
						.RECNAME.equals(ctx.GENERIC_ID().getText())) {
				/**
				 * Detect references to search record buffer.
				 */
				setPointer(ctx, ComponentBuffer.searchRecordPtr);

			} else if(PSDefn.defnLiteralReservedWordsTable.containsKey(
				ctx.GENERIC_ID().getText().toUpperCase())) {
				/**
				 * Detect defn literal reserved words (i.e.,
				 * "Menu" in "Menu.SA_LEARNER_SERVICES").
				 */
				setPointer(ctx, Environment.DEFN_LITERAL);

			} else if(Environment.getSystemFuncPtr(ctx.GENERIC_ID().getText()) != null) {
				/**
				 * Detect system function references.
				 */
				setPointer(ctx, Environment.getSystemFuncPtr(
						ctx.GENERIC_ID().getText()));
			}
		}
		return null;
	}

	public Void visitLiteral(PeopleCodeParser.LiteralContext ctx) {

		if(ctx.IntegerLiteral() != null) {

			Pointer ptr = Environment.getFromLiteralPool(
				new Integer(ctx.IntegerLiteral().getText()));
			setPointer(ctx, ptr);

		} else if(ctx.BoolLiteral() != null) {

			String b = ctx.BoolLiteral().getText();
			if(b.equals("True") || b.equals("true")) {
				setPointer(ctx, Environment.TRUE);
			} else {
				setPointer(ctx, Environment.FALSE);
			}

		} else if(ctx.DecimalLiteral() != null) {
			throw new EntVMachRuntimeException("Encountered a decimal literal; need to create "
				+ "a BigDecimal memory pool and type.");
		} else {
			throw new EntVMachRuntimeException("Unable to resolve literal to a terminal node.");
		}

		return null;
	}

	public Void visitVarDeclaration(PeopleCodeParser.VarDeclarationContext ctx) {

		visit(ctx.varType());

		String scope = ctx.varScope.getText();

		List<PeopleCodeParser.VarDeclaratorContext> varsToDeclare = ctx.varDeclarator();
		for(PeopleCodeParser.VarDeclaratorContext idCtx : varsToDeclare) {
			if(idCtx.expr() != null) {
				throw new EntVMachRuntimeException("Initialization during var declaration is " +
					"not yet supported.");
			}

			log.debug("Declaring identifier ({}) with scope {} and type {}.",
				idCtx.VAR_ID().getText(), scope, ctx.varType().getText());
			/**
			 * IMPORTANT: The Pointer must cloned for each variable, otherwise
			 * each identifier will point to the same target.
			 */
			this.declareIdentifier(scope, idCtx.VAR_ID().getText(),
				getPointer(ctx.varType()));
		}
		return null;
	}

	public Void visitVarType(PeopleCodeParser.VarTypeContext ctx) {

		if(ctx.appClassPath() != null) {
			visit(ctx.appClassPath());
			setPointer(ctx, getPointer(ctx.appClassPath()));
		} else {

			Pointer nestedTypePtr = null;
			if(ctx.varType() != null) {
				if(!ctx.GENERIC_ID().getText().equals("array")) {
					throw new EntVMachRuntimeException("Encountered non-array var " +
						"type preceding 'of' clause: " + ctx.getText());
				}
				visit(ctx.varType());
				nestedTypePtr = getPointer(ctx.varType());
			}

			Pointer ptr;
			switch(ctx.GENERIC_ID().getText()) {
				case "array":
					ptr = new ArrayPointer(nestedTypePtr);		break;
				case "string":
					ptr = new StdPointer(MFlag.STRING);			break;
				case "date":
					ptr = new StdPointer(MFlag.DATE);			break;
				case "integer":
					ptr = new StdPointer(MFlag.INTEGER);		break;
				case "Record":
					ptr = new StdPointer(MFlag.RECORD);			break;
				case "Rowset":
					ptr = new StdPointer(MFlag.ROWSET);			break;
				default:
					throw new EntVMachRuntimeException("Unexpected data type: " +
						ctx.GENERIC_ID().getText());
			}
			setPointer(ctx, ptr);
		}
		return null;
	}

	public Void visitEvaluateStmt(PeopleCodeParser.EvaluateStmtContext ctx) {
		this.emitStmt(ctx);

		visit(ctx.expr());
		EvaluateConstruct evalConstruct = new EvaluateConstruct(
			getPointer(ctx.expr()).dereference());
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
		PTDataType p1 = evalConstruct.baseExpr;
		visit(ctx.expr());
		PTDataType p2 = getPointer(ctx.expr()).dereference();

		/**
		 * If a previous branch evaluated to true and we're here, that means
		 * execution continues to fall through all branches until either a break
		 * is seen, or the evaluate construct ends.
		 */
		if(evalConstruct.trueBranchExprSeen || p1.equals(p2)) {
			if(!evalConstruct.trueBranchExprSeen) {
				// Only emit the first true branch expr seen.
				this.emitStmt(ctx);
			}
			evalConstruct.trueBranchExprSeen = true;
			visit(ctx.stmtList());
		}

		return null;
	}

	public Void visitWhenOtherBranch(PeopleCodeParser.WhenOtherBranchContext ctx) {
		this.emitStmt(ctx);
		visit(ctx.stmtList());
		return null;
	}

	public Void visitAppClassPath(PeopleCodeParser.AppClassPathContext ctx) {
		AppClassPeopleCodeProg progDefn =
			(AppClassPeopleCodeProg)DefnCache.getProgram(("AppClassPC." +
				ctx.getText() + ".OnExecute").replaceAll(":","."));
		setPointer(ctx, new AppClassObjPointer(progDefn));
		return null;
	}

	public Void visitCreateInvocation(PeopleCodeParser.CreateInvocationContext ctx) {

		AppClassObjPointer ptr;
		if(ctx.appClassPath() != null) {
			visit(ctx.appClassPath());
			ptr = (AppClassObjPointer) getPointer(ctx.appClassPath());
		} else {
			throw new EntVMachRuntimeException("Encountered create invocation without " +
				"app class prefix; need to support this by resolving path to class.");
		}

		PTAppClassObject newObj = new PTAppClassObject(ptr.progDefn);
		ptr.assign(newObj);
		setPointer(ctx, ptr);

		/**
	     * Check for constructor; call it if it exists.
		 * TODO: If issues arise with this, I probably need to check the method
		 * declaration to ensure that the constructor (if it exists) is only called
	     * when the appropriate number of arguments are supplied.
		 */
		String constructorName = ptr.progDefn.getClassName();
		if(ptr.progDefn.methodEntryPoints.containsKey(constructorName)) {

			/**
			 * Load arguments to constructor onto call stack if
			 * args have been provided.
			 */
			if(ctx.exprList() != null) {
				visit(ctx.exprList());
				for(PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
					visit(argCtx);
	                Environment.pushToCallStack(getPointer(argCtx));
				}
			}

			ExecContext constructorCtx = new AppClassObjExecContext(newObj,
				constructorName);
			this.supervisor.runImmediately(constructorCtx);
			this.repeatLastEmission();
		}

		return null;
	}

	private void declareIdentifier(String scope, String id, Pointer ptr) {
		switch(scope) {
			case "Local":
				eCtx.declareLocalVar(id, ptr);
				break;
			case "Component":
				RefEnvi.declareComponentVar(id, ptr);
				break;
			case "Global":
				RefEnvi.declareGlobalVar(id, ptr);
				break;
			default:
				throw new EntVMachRuntimeException("Encountered unexpected variable " +
					" scope: " + scope);
		}
	}
}

