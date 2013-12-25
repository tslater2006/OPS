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

	private ParseTreeProperty<MemPointer> memPointers;
	private Stack<EvaluateConstruct> evalConstructStack;
	private InterruptFlag interrupt;
	private IEmission lastEmission;

	private static Logger log = LogManager.getLogger(InterpreterVisitor.class.getName());

	public InterpreterVisitor(ExecContext e, InterpretSupervisor s) {
		this.eCtx = e;
		this.tokens = e.prog.tokenStream;
		this.supervisor = s;
		this.memPointers = new ParseTreeProperty<MemPointer>();
		this.evalConstructStack = new Stack<EvaluateConstruct>();
	}

	private void setMemPointer(ParseTree node, MemPointer ptr) {
		if(ptr == null) {
			throw new EntVMachRuntimeException("Attempted to set the memory pointer to null " +
				"for a node: " + node.getText());
		}
		this.memPointers.put(node, ptr);
	}

	private MemPointer getMemPointer(ParseTree node) {
		if(this.memPointers.get(node) == null) {
			throw new EntVMachRuntimeException("Attempted to get the memory pointer for " +
				"for a node, encountered null: " + node.getText());
		}
		return this.memPointers.get(node);
	}

	// Bubble-up operations should not fail in the event of nulls, unlike normal accesses.
	private void bubbleUpMemPointer(ParseTree src, ParseTree dest) {
		if(this.memPointers.get(src) != null) {
			this.memPointers.put(dest, this.memPointers.get(src));
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
		boolean exprResult = ((PTBoolean) getMemPointer(ctx.ifStmt()
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
		MemPointer target = getMemPointer(ctx.expr(1));
		visit(ctx.expr(0));
		MemPointer pointer = getMemPointer(ctx.expr(0));
		pointer.assign(target.dereference());
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
		bubbleUpMemPointer(ctx.expr(), ctx);
		return null;
	}

	public Void visitExprLiteral(PeopleCodeParser.ExprLiteralContext ctx) {
		visit(ctx.literal());
		bubbleUpMemPointer(ctx.literal(), ctx);
		return null;
	}

	public Void visitExprId(PeopleCodeParser.ExprIdContext ctx) {
		visit(ctx.id());
		bubbleUpMemPointer(ctx.id(), ctx);
		return null;
	}

	public Void visitExprFnOrIdxCall(PeopleCodeParser.ExprFnOrIdxCallContext ctx) {

		visit(ctx.expr());

		PTDataType ptdt = getMemPointer(ctx.expr()).dereference();

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
				Environment.pushToCallStack(getMemPointer(argCtx));
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
	    MemPointer ptr = Environment.popFromCallStack();
		if(ptr != null) {
			setMemPointer(ctx, ptr);
		}

		if(ptr != null && (Environment.popFromCallStack() != null)) {
			throw new EntVMachRuntimeException("More than one return value was found on " +
				"the call stack.");
		}

		return null;
	}

	public Void visitExprCreate(PeopleCodeParser.ExprCreateContext ctx) {
		visit(ctx.createInvocation());
		bubbleUpMemPointer(ctx.createInvocation(), ctx);
		return null;
	}

	public Void visitExprDotAccess(
					PeopleCodeParser.ExprDotAccessContext ctx) {

		visit(ctx.expr());
		visit(ctx.id());

		MemPointer exprPtr = getMemPointer(ctx.expr());
		MemPointer accessedPtr = exprPtr.dereference().access(ctx.id().getText());
		setMemPointer(ctx, accessedPtr);

		return null;
	}

	public Void visitExprEquality(PeopleCodeParser.ExprEqualityContext ctx) {
		visit(ctx.expr(0));
		PTDataType p1 = getMemPointer(ctx.expr(0)).dereference();
		visit(ctx.expr(1));
		PTDataType p2 = getMemPointer(ctx.expr(1)).dereference();

		MemPointer result;
		if(p1.equals(p2)) {
			result = Environment.TRUE;
		} else {
			result = Environment.FALSE;
		}
		setMemPointer(ctx, result);
		log.debug("Compared for equality: {}, result={}", ctx.getText(), result.dereference());
		return null;
	}

	public Void visitExprBoolean(PeopleCodeParser.ExprBooleanContext ctx) {

		if(ctx.op.getText().equals("Or")) {
			visit(ctx.expr(0));
			PTBoolean lhs = (PTBoolean)getMemPointer(ctx.expr(0)).dereference();

			/**
			 * Short-circuit evaluation: if lhs is true, this expression is true,
			 * otherwise evaluate rhs and bubble up its value.
			 */
			if(lhs.value()) {
				setMemPointer(ctx, Environment.TRUE);
			} else {
				visit(ctx.expr(1));
				setMemPointer(ctx, getMemPointer(ctx.expr(1)));
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
			MemPointer ptr = Environment.getSystemVar(ctx.getText());
			setMemPointer(ctx, ptr);

		} else if(ctx.VAR_ID() != null) {
			MemPointer ptr = eCtx.resolveIdentifier(ctx.getText());
			setMemPointer(ctx, ptr);

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
				setMemPointer(ctx, ComponentBuffer.searchRecordPtr);

			} else if(PSDefn.defnLiteralReservedWordsTable.containsKey(
				ctx.GENERIC_ID().getText().toUpperCase())) {
				/**
				 * Detect defn literal reserved words (i.e.,
				 * "Menu" in "Menu.SA_LEARNER_SERVICES").
				 */
				setMemPointer(ctx, Environment.DEFN_LITERAL);

			} else if(Environment.getSystemFuncPtr(ctx.GENERIC_ID().getText()) != null) {
				/**
				 * Detect system function references.
				 */
				setMemPointer(ctx, Environment.getSystemFuncPtr(
						ctx.GENERIC_ID().getText()));
			}
		}
		return null;
	}

	public Void visitLiteral(PeopleCodeParser.LiteralContext ctx) {

		if(ctx.IntegerLiteral() != null) {

			MemPointer ptr = Environment.getFromLiteralPool(
				new Integer(ctx.IntegerLiteral().getText()));
			setMemPointer(ctx, ptr);

		} else if(ctx.BoolLiteral() != null) {

			String b = ctx.BoolLiteral().getText();
			if(b.equals("True") || b.equals("true")) {
				setMemPointer(ctx, Environment.TRUE);
			} else {
				setMemPointer(ctx, Environment.FALSE);
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
		String[] ids = new String[varsToDeclare.size()];
		int i = 0;
		for(PeopleCodeParser.VarDeclaratorContext idCtx : varsToDeclare) {
			ids[i] = idCtx.VAR_ID().getText();
			if(idCtx.expr() != null) {
				throw new EntVMachRuntimeException("Initialization during var declaration is " +
					"not yet supported.");
			}
			i++;
		}

		log.debug("Declaring identifiers ({}) with scope {} and type {}.",
			ids, scope, ctx.varType().getText());
		this.declareIdentifiers(scope, ids, getMemPointer(ctx.varType()));

		return null;
	}

	public Void visitVarType(PeopleCodeParser.VarTypeContext ctx) {

		if(ctx.appClassPath() != null) {
			visit(ctx.appClassPath());
			setMemPointer(ctx, getMemPointer(ctx.appClassPath()));
		} else {
			if(ctx.varType() != null) {
				visit(ctx.varType());
				throw new EntVMachRuntimeException("Need to support 'of' clauses" +
					"in varType.");
			}
			String typeStr = ctx.GENERIC_ID().getText();
			switch(typeStr) {
				/**
				 * CRITICAL: DON'T ACTUALLY ASSIGN TO PTR's TARGET:
				 * just set the flag on the MemPointer indicating what it accepts.
				 */
				default:
					throw new EntVMachRuntimeException("Unexpected data type: " +
						typeStr);
			}
		}
		return null;
	}

	public Void visitEvaluateStmt(PeopleCodeParser.EvaluateStmtContext ctx) {
		this.emitStmt(ctx);

		visit(ctx.expr());
		EvaluateConstruct evalConstruct = new EvaluateConstruct(
			getMemPointer(ctx.expr()).dereference());
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
		PTDataType p2 = getMemPointer(ctx.expr()).dereference();

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
		setMemPointer(ctx, new MemPointer(progDefn));
		return null;
	}

	public Void visitCreateInvocation(PeopleCodeParser.CreateInvocationContext ctx) {

		MemPointer ptr;
		if(ctx.appClassPath() != null) {
			visit(ctx.appClassPath());
			ptr = getMemPointer(ctx.appClassPath());
		} else {
			throw new EntVMachRuntimeException("Encountered create invocation without " +
				"app class prefix; need to support this by resolving path to class.");
		}

		PTAppClassObject newObj = new PTAppClassObject(ptr.appClassTypeProg);
		ptr.assign(newObj);
		setMemPointer(ctx, ptr);

		/**
	     * Check for constructor; call it if it exists.
		 * TODO: If issues arise with this, I probably need to check the method
		 * declaration to ensure that the constructor (if it exists) is only called
	     * when the appropriate number of arguments are supplied.
		 */
		String constructorName = ptr.appClassTypeProg.getClassName();
		if(ptr.appClassTypeProg.methodEntryPoints.containsKey(constructorName)) {

			/**
			 * Load arguments to constructor onto call stack if
			 * args have been provided.
			 */
			if(ctx.exprList() != null) {
				visit(ctx.exprList());
				for(PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
					visit(argCtx);
	                Environment.pushToCallStack(getMemPointer(argCtx));
				}
			}

			ExecContext constructorCtx = new AppClassObjExecContext(newObj,
				constructorName);
			this.supervisor.runImmediately(constructorCtx);
			this.repeatLastEmission();
		}

		return null;
	}

	private void declareIdentifiers(String scope, String[] ids, MemPointer ptr) {

		if(ids.length > 1) {
			/**
			 * TODO: Create copies of the data type for each ids
			 * in the array.
			 */
			throw new EntVMachRuntimeException("No support for declaring " +
				"multiple ids at once.");
		}

		switch(scope) {
			case "Local":
				eCtx.declareLocalVar(ids[0], ptr);
				break;
			case "Component":
				RefEnvi.declareComponentVar(ids[0], ptr);
				break;
			case "Global":
				RefEnvi.declareGlobalVar(ids[0], ptr);
				break;
			default:
				throw new EntVMachRuntimeException("Encountered unexpected variable " +
					" scope: " + scope);
		}
	}
}

