/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Stack;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.antlr4.frontend.*;
import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.trace.*;
import org.openpplsoft.types.*;

/**
 * This visitor interprets PeopleCode programs as ANTLR
 * lexes and parses them.
 */
public class InterpreterVisitor extends PeopleCodeBaseVisitor<Void> {

  private static Logger log =
      LogManager.getLogger(InterpreterVisitor.class.getName());

  private ExecContext eCtx;
  private CommonTokenStream tokens;
  private InterpretSupervisor supervisor;
  private ParseTreeProperty<PTType> nodeData;
  private ParseTreeProperty<Callable> nodeCallables;
  private ParseTreeProperty<PTTypeConstraint> nodeTypeConstraints;
  private Stack<EvaluateConstruct> evalConstructStack;
  private IEmission lastEmission;
  private AccessLevel blockAccessLvl;
  private boolean hasVarDeclBeenEmitted;

  /**
   * Creates a new interpreter instance that is aware
   * of the program's execution context and the supervisor
   * responsible for kicking off execution of the interpreter.
   * @param e the execution context for the program to interpret
   * @param s the supervisor driving the interpretation process
   */
  public InterpreterVisitor(final ExecContext e,
      final InterpretSupervisor s) {
    this.eCtx = e;
    this.tokens = e.prog.tokenStream;
    this.supervisor = s;
    this.nodeData = new ParseTreeProperty<PTType>();
    this.nodeCallables = new ParseTreeProperty<Callable>();
    this.nodeTypeConstraints = new ParseTreeProperty<PTTypeConstraint>();
    this.evalConstructStack = new Stack<EvaluateConstruct>();
  }

  /**
   * Allows a PTTypeConstraint to be associated with a ParseTree node
   * for retrieval by other parts of the interpreter. Note that you
   * cannot limit the node type to VarTypeContext nodes, as there can be
   * other contexts that need to pass around a type constraint (i.e.,
   * AppClassPathContext nodes). Also note that
   * unlike data objects (PTType objects), these should *not* be bubbled
   * up, as type constraints are only ever associated with a single var type node.
   */
  private void setNodeTypeConstraint(
      final ParseTree node,
      final PTTypeConstraint typeConstraint) {
    this.nodeTypeConstraints.put(node, typeConstraint);
  }

  private PTTypeConstraint getNodeTypeConstraint(
      final ParseTree node) {
    return this.nodeTypeConstraints.get(node);
  }

  /**
   * Associates a data value with a ParseTree node; this data
   * value can then be accessed by other parts of the interpreter.
   * @param node the ParseTree node to associate {@code a} with
   * @param a the data value to associate with {@code node}
   */
  private void setNodeData(final ParseTree node, final PTType a) {
    if (node instanceof PeopleCodeParser.VarTypeContext) {
      throw new OPSVMachRuntimeException("setNodeData called for VarTypeContext; "
          + "are you sure this is correct? (shouldn't it be setNodeTypeConstraint?)");
    }
    this.nodeData.put(node, a);
  }

  /**
   * Retrieves a data value associated with a ParseTree node,
   * assuming the value has already been associated via a call
   * to {@code setNodeData}.
   * @param node the ParseTree node to retrieve the associated
   *    data value for
   */
  private PTType getNodeData(final ParseTree node) {
    if (node instanceof PeopleCodeParser.VarTypeContext) {
      throw new OPSVMachRuntimeException("getNodeData called for VarTypeContext; "
          + "are you sure this is correct? (shouldn't it be getNodeTypeConstraint?)");
    }
    return this.nodeData.get(node);
  }

  /**
   * Associates a callable (i.e., function/method call)
   * with a ParseTree node for access by other parts of the
   * interpreter.
   * @param node the ParseTree node to associate {@code c} with
   * @param c the Callable to associate with {@code node}
   */
  private void setNodeCallable(final ParseTree node,
      final Callable c) {
    this.nodeCallables.put(node, c);
  }

  /**
   * Retrives a callable (i.e., function/method call) that
   * has been previously associated with a ParseTree node
   * via {@code setNodeCallable}.
   * @param node the ParseTree node to retrieve the associated
   *    callable for
   */
  private Callable getNodeCallable(final ParseTree node) {
    return this.nodeCallables.get(node);
  }

  /**
   * Associates the data value and callable for one ParseTree
   * node with another; typically {@code dest} is the parent
   * or an ancestor of {@code src}. Note that bubble-up operations
   * must not fail if a data and/or callable value is not associated
   * with the {@code src} node.
   * @param src the source ParseTree node to retrieve associations from
   * @param dest the destination ParseTree node to receive the
   *    associations currently attached to {@code src}.
   */
  private void bubbleUp(final ParseTree src,
      final ParseTree dest) {
    if (this.nodeData.get(src) != null) {
      this.nodeData.put(dest, this.nodeData.get(src));
    }
    if (this.nodeCallables.get(src) != null) {
      this.nodeCallables.put(dest, this.nodeCallables.get(src));
    }
  }

  private void emitStmt(final ParserRuleContext ctx) {
    final StringBuffer line = new StringBuffer();
    final Interval interval = ctx.getSourceInterval();

    int i = interval.a;
    while (i <= interval.b) {
      final Token t = this.tokens.get(i);
      if (t.getChannel() == PeopleCodeLexer.REFERENCES) {
        i++;
        continue;
      }
      if (t.getText().contains("\n")) {
        break;
      }
      line.append(t.getText());
      i++;
    }

    final IEmission e = new PCInstruction(line.toString());
    TraceFileVerifier.submitEnforcedEmission(e);
    this.lastEmission = e;
  }

  private void emitStmt(final String str) {
    /*
     * Don't emit an End-If or End-For after emitting a Break.
     */
    if((str.equals("End-If") || str.equals("End-For"))
        && ((this.lastEmission instanceof PCInstruction
              && ((PCInstruction) this.lastEmission).getInstruction()
                  .equals("Break")))) {
      return;
    }

    /*
     * Don't emit an End-If statement after exiting a For loop
     * or If construct or if Else was previously seen.
     */
    if (str.equals("End-If")
        && this.lastEmission instanceof PCInstruction) {
      if (((PCInstruction) this.lastEmission).getInstruction()
          .startsWith("For")
          || ((PCInstruction) this.lastEmission).getInstruction()
          .equals("End-If")
          || ((PCInstruction) this.lastEmission).getInstruction()
          .equals("Else")
          || ((PCInstruction) this.lastEmission).getInstruction()
          .startsWith("If")) {
        return;
      }
    }
    final IEmission e = new PCInstruction(str);
    TraceFileVerifier.submitEnforcedEmission(e);
    this.lastEmission = e;
  }

  private void repeatLastEmission() {
    TraceFileVerifier.submitEnforcedEmission(this.lastEmission);
  }

  /**
   * Called by ANTLR when a program node is being visited
   * in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitProgram(final PeopleCodeParser.ProgramContext ctx) {
    /*
     * App class programs do not get a fresh scope b/c they
     * are always loaded to manipulate an existing object's scope, which
     * has already been placed on the scope stack for this exec context. All
     * other programs get a fresh program-local scope.
     */
    if (!(this.eCtx.prog instanceof AppClassPeopleCodeProg)) {
      this.eCtx.pushScope(new Scope(Scope.Lvl.PROGRAM_LOCAL));
    }

    visit(ctx.stmtList());
    this.eCtx.popScope();
    return null;
  }

  /**
   * Called by ANTLR when a class declaration node is being visited
   * in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitClassDeclaration(
      final PeopleCodeParser.ClassDeclarationContext ctx) {
    ((AppClassPeopleCodeProg) this.eCtx.prog).appClassName =
        ctx.GENERIC_ID().getText();
    for (PeopleCodeParser.ClassBlockContext bCtx : ctx.classBlock()) {
      visit(bCtx);
    }
    return null;
  }

  /**
   * Called by ANTLR when an app class block node is being visited
   * in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitClassBlock(final PeopleCodeParser.ClassBlockContext ctx) {
    if (ctx.aLvl != null) {
      switch (ctx.aLvl.getText()) {
        case "private":
          this.blockAccessLvl = AccessLevel.PRIVATE;
          break;
        case "public":
          this.blockAccessLvl = AccessLevel.PUBLIC;
          break;
        default:
          throw new OPSVMachRuntimeException("Unknown access level modifier "
              + "encountered: " + ctx.aLvl.getText());
      }
    } else {
      // Blocks without an access level modifier are public by default.
      this.blockAccessLvl = AccessLevel.PUBLIC;
    }
    for (PeopleCodeParser.ClassBlockStmtContext cbsCtx
        : ctx.classBlockStmt()) {
      visit(cbsCtx);
    }
    return null;
  }

  /**
   * Called by ANTLR when an app class instance variable declaration
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitInstance(
      final PeopleCodeParser.InstanceContext ctx) {
    visit(ctx.varType());

    if (this.blockAccessLvl != AccessLevel.PRIVATE) {
      throw new OPSVMachRuntimeException("Expected instance declaration "
          + "to be private; actual access level is: " + this.blockAccessLvl);
    }

    final PTTypeConstraint tc = this.getNodeTypeConstraint(ctx.varType());
    for (TerminalNode varId : ctx.VAR_ID()) {
      log.debug("Adding instance identifier ({}); type constraint: {}.",
          varId.getText(), tc);
      ((AppClassPeopleCodeProg) this.eCtx.prog).addInstanceIdentifier(
          varId.getText(), tc);
    }
    return null;
  }

  /**
   * Called by ANTLR when an app class property declaration
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitProperty(final PeopleCodeParser.PropertyContext ctx) {
    visit(ctx.varType());

    if (this.blockAccessLvl != AccessLevel.PUBLIC) {
      throw new OPSVMachRuntimeException("Expected property declaration "
          + "to be publicly accessible; actual access level is: "
          + this.blockAccessLvl);
    }

    final String id = ctx.GENERIC_ID().getText();
    final boolean hasGetter = (ctx.g != null);
    final boolean hasSetter = (ctx.s != null);
    final boolean isReadOnly = (ctx.r != null);

    if (hasSetter || isReadOnly) {
      throw new OPSVMachRuntimeException("Need to support property "
          + "setters and/or readonly properties: " + ctx.getText());
    }

    final PTTypeConstraint tc = this.getNodeTypeConstraint(ctx.varType());
    ((AppClassPeopleCodeProg) this.eCtx.prog).addPropertyIdentifier(
      id, tc, hasGetter);
    return null;
  }

  /**
   * Called by ANTLR when a method declaration
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitMethod(final PeopleCodeParser.MethodContext ctx) {

    visit(ctx.formalParamList());
    final List<FormalParam> formalParams = new ArrayList<FormalParam>();
    for (PeopleCodeParser.ParamContext pCtx
        : ctx.formalParamList().param()) {
      formalParams.add(new FormalParam(pCtx.VAR_ID().getText(),
        this.getNodeTypeConstraint(pCtx.varType())));
    }

    PTTypeConstraint rTypeConstraint = null;
    if (ctx.returnType() != null) {
      visit(ctx.returnType());
      rTypeConstraint = this.getNodeTypeConstraint(ctx.returnType().varType());
    }

    ((AppClassPeopleCodeProg) this.eCtx.prog).addMethod(
      this.blockAccessLvl, ctx.GENERIC_ID().getText(), formalParams, rTypeConstraint);
    return null;
  }

  /**
   * Called by ANTLR when a function impl signature is being
   * visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitFuncSignature(final PeopleCodeParser.FuncSignatureContext ctx) {

    visit(ctx.formalParamList());
    final List<FormalParam> formalParams = new ArrayList<FormalParam>();
    for (PeopleCodeParser.ParamContext pCtx
        : ctx.formalParamList().param()) {
      formalParams.add(new FormalParam(pCtx.VAR_ID().getText(),
        this.getNodeTypeConstraint(pCtx.varType())));
    }

    PTTypeConstraint rTypeConstraint = null;
    if (ctx.returnType() != null) {
      visit(ctx.returnType());
      rTypeConstraint = this.getNodeTypeConstraint(ctx.returnType().varType());
    }

    this.eCtx.prog.addFunction(
        ctx.GENERIC_ID().getText(), formalParams, rTypeConstraint);
    return null;
  }

  /*==========================================================
   * <stmt> alternative handlers.
   *==========================================================*/

  /**
   * Called by ANTLR when an If statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitIfStmt(final PeopleCodeParser.IfStmtContext ctx) {
    this.emitStmt(ctx);

    // Get value of conditional expression.
    visit(ctx.expr());
    final boolean exprResult =
        ((PTBoolean) this.getNodeData(ctx.expr())).read();

    // If expression evaluates to true, visit the conditional body;
    // otherwise, visit the Else body if it exists.
    if (exprResult) {
      visit(ctx.stmtList(0));
      if (ctx.stmtList(1) != null) {
        this.emitStmt("Else");
      } else {
        this.emitStmt("End-If");
      }
    } else {
      if (ctx.stmtList(1) != null) {
        visit(ctx.stmtList(1));
        this.emitStmt("End-If");
      }
    }

    return null;
  }

  /**
   * Called by ANTLR when a For statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitForStmt(final PeopleCodeParser.ForStmtContext ctx) {

    if (ctx.expr(2) != null) {
      throw new OPSVMachRuntimeException("Step clause encountered "
          + "in For construct; not yet supported.");
    }

    final PTPrimitiveType varId =
        (PTPrimitiveType) this.eCtx.resolveIdentifier(
            ctx.VAR_ID().getText());

    visit(ctx.expr(0));
    final PTPrimitiveType initialExpr =
        (PTPrimitiveType) this.getNodeData(ctx.expr(0));

    // Initialize incrementing expression.
    varId.copyValueFrom(initialExpr);

    visit(ctx.expr(1));
    final PTPrimitiveType toExpr =
        (PTPrimitiveType) this.getNodeData(ctx.expr(1));

    this.emitStmt(ctx);
    while (varId.isLessThanOrEqual(toExpr) == Environment.TRUE) {

      try {
        visit(ctx.stmtList());
      } catch (OPSBreakSignalException opsbse) {
        this.emitStmt("Break");
        break;
      }

      // Increment and set new value of incrementing expression.
      final PTPrimitiveType incremented = (PTPrimitiveType) varId
        .add(Environment.getFromLiteralPool(1));
      varId.copyValueFrom(incremented);

      this.emitStmt("End-For");
      this.emitStmt(ctx);
    }

    return null;
  }

  /**
   * Called by ANTLR when a Break statement
   * is being visited in the parse tree. The Break statement
   * is *NOT* emitted by this method; it must be emitted when
   * the exception is caught.
   * @param ctx the associated ParseTree node
   * @return nothing; OPSVMachBreakSignalException is thrown
   */
  public Void visitStmtBreak(
      final PeopleCodeParser.StmtBreakContext ctx) {
    throw new OPSBreakSignalException();
  }

  /**
   * Called by ANTLR when an assignment statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitStmtAssign(
      final PeopleCodeParser.StmtAssignContext ctx) {
    this.emitStmt(ctx);
    visit(ctx.expr(1));
    final PTType src = this.getNodeData(ctx.expr(1));
    visit(ctx.expr(0));
    final PTType dst = this.getNodeData(ctx.expr(0));

    /*
     * primitive = primitive : write from rhs to lhs, ignore identifier
     * primitive = object : attempt cast from src type to destination type
     * object = primitive : attempt cast from destination type to src type
     * object = object : get var identifier, make it point to rhs object
     */
    if (dst instanceof PTPrimitiveType && src instanceof PTPrimitiveType) {
      ((PTPrimitiveType) dst).copyValueFrom((PTPrimitiveType) src);

    } else if (dst instanceof PTPrimitiveType && src instanceof PTObjectType) {
      final PTPrimitiveType unboxedSrc;
      if (src instanceof PTField) {
        //i.e., &str = SSR_STDNT_TERM0.EMPLID; field must be cast to string.
        unboxedSrc = ((PTField) src).getValue();
      } else {
        throw new OPSVMachRuntimeException("Unsupported combination of object "
            + "src:" + src + " and primitive dst:" + dst);
      }
      ((PTPrimitiveType) dst).copyValueFrom(unboxedSrc);

    } else if (dst instanceof PTObjectType && src instanceof PTPrimitiveType) {
      final PTPrimitiveType unboxedDst;
      if (dst instanceof PTField) {
        //i.e., SSR_STNDT_TERM0.EMPLID = "5"; field must be cast to string.
        unboxedDst = ((PTField) dst).getValue();
      } else {
        throw new OPSVMachRuntimeException("Unsupported combination of primitive "
            + "src:" + src + " and object dst:" + dst);
      }
      unboxedDst.copyValueFrom((PTPrimitiveType) src);

    } else if (dst instanceof PTObjectType && src instanceof PTObjectType) {
      // Assuming lhs is an identifier; this may or may not hold true long term.
      this.eCtx.assignToIdentifier(ctx.expr(0).getText(), src);

    } else {
      throw new OPSVMachRuntimeException("Assignment failed; unexpected "
          + "type combination: src is " + src + ", dst is " + dst);
    }

    return null;
  }

  /**
   * Called by ANTLR when a statement consisting solely
   * of an expression is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitStmtExpr(final PeopleCodeParser.StmtExprContext ctx) {
    this.emitStmt(ctx);
    visit(ctx.expr());
    return null;
  }

  /**
   * Called by ANTLR when a Return statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitStmtReturn(final PeopleCodeParser.StmtReturnContext ctx) {
    this.emitStmt(ctx);
    if (ctx.expr() != null) {
      visit(ctx.expr());
      final PTType retVal = this.getNodeData(ctx.expr());

      if (this.eCtx instanceof AppClassObjExecContext) {
        if (((AppClassObjExecContext) this.eCtx).expectedReturnTypeConstraint
            .typeCheck(retVal)) {
          Environment.pushToCallStack(retVal);
        } else {
          throw new OPSVMachRuntimeException("Value returned in app class "
              + "obj execution context does not match the expected type.");
        }
      } else {
        throw new OPSVMachRuntimeException("Must type check return values "
            + "in non-app class execution contexts.");
      }
    }
    throw new OPSReturnException(ctx.getText());
  }

  /*=========================================================
   * <expr> alternative handlers.
   *=========================================================*/

  /**
   * Called by ANTLR when a parenthesized expression statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprParenthesized(
      final PeopleCodeParser.ExprParenthesizedContext ctx) {
    visit(ctx.expr());
    this.bubbleUp(ctx.expr(), ctx);
    return null;
  }

  /**
   * Called by ANTLR when a literal in an expression
   * statement is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprLiteral(
      final PeopleCodeParser.ExprLiteralContext ctx) {
    visit(ctx.literal());
    this.bubbleUp(ctx.literal(), ctx);
    return null;
  }

  /**
   * Called by ANTLR when a dynamic reference ('@(...)')
   * statement is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprDynamicReference(
      final PeopleCodeParser.ExprDynamicReferenceContext ctx) {
    visit(ctx.expr());

    if(!(this.getNodeData(ctx.expr()) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Encountered input to dynamic "
          + "reference translation that is not of type PTString; need to "
          + "support this particular input type.");
    }

    String input = ((PTString) this.getNodeData(ctx.expr())).read();
    PTType output;
    if (input.startsWith("Record.")) {
      output = new PTRecordLiteral(input);
    } else if (input.startsWith("MenuName.")) {
      output = new PTMenuLiteral(input);
    } else if (input.startsWith("BarName.")) {
      output = new PTMenuBarLiteral(input);
    } else if (input.startsWith("ItemName.")) {
      output = new PTMenuItemLiteral(input);
    } else if (input.startsWith("Page.")) {
      output = new PTPageLiteral(input);
    } else if (input.startsWith("Component.")) {
      output = new PTComponentLiteral(input);
    } else {
      throw new OPSVMachRuntimeException("Unsupported dynamic reference "
          + "attempt: " + input);
    }

    log.debug("Translated dynamic reference input {} to {}",
        input, output);
    this.setNodeData(ctx, output);
    return null;
  }

  /**
   * Called by ANTLR when an identifier in an expression
   * statement is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprId(
      final PeopleCodeParser.ExprIdContext ctx) {
    visit(ctx.id());
    this.bubbleUp(ctx.id(), ctx);
    return null;
  }

  /**
   * Called by ANTLR when a function call or indexing
   * operation in an expression within a statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprFnOrIdxCall(
      final PeopleCodeParser.ExprFnOrIdxCallContext ctx) {

    //log.debug("BEGIN visitExprFnOrIdxCall; expr is {}", ctx.expr().getText());

    visit(ctx.expr());

    final Callable call = this.getNodeCallable(ctx.expr());
    final PTType t = this.getNodeData(ctx.expr());

    Environment.pushToCallStack(PTCallFrameBoundary.getSingleton());

    // if args exist, push them onto the call stack.
    if (ctx.exprList() != null) {
      for (PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
        //log.debug("VISITING arg: {}", argCtx.getText());
        visit(argCtx);
        Environment.pushToCallStack(this.getNodeData(argCtx));
      }
    }

    /*
     * If the node represents a rowset, this expression represents
     * a row indexing operation (i.e., "&rs(1)"). Call getRow() to run
     * the indexing operation.
     */
    if (t instanceof PTRowset) {
      ((PTRowset) t).PT_GetRow();
    } else if(call == null) {
      throw new OPSVMachRuntimeException("No Callable exists for fn expression: "
          + ctx.expr().getText());
    } else if (call.ptMethod != null) {
      call.invokePtMethod();
    } else {
      this.supervisor.runImmediately(call.eCtx);
      if(!(call.eCtx instanceof FunctionExecContext)) {
        this.repeatLastEmission();
      }
    }

    /*
     * Pop the first value from the call stack. If it's a call frame
     * boundary, the function
     * did not emit a return value. If it's not a call frame boundary
     * the next item on the stack
     * must be the boundary separator (PeopleCode funcs can only return 1 value).
     */
    final PTType a = Environment.popFromCallStack();
    if (!(a instanceof PTCallFrameBoundary)) {
      this.setNodeData(ctx, a);
    }

    if (!(a instanceof PTCallFrameBoundary)
         && !(Environment.popFromCallStack() instanceof PTCallFrameBoundary)) {
      throw new OPSVMachRuntimeException("More than one return value "
          + "was found on the call stack.");
    }

    return null;
  }

  /**
   * Called by ANTLR when a multiplication or division operation
   * within an expression in a statement is being visited
   * in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprMulDiv(
      final PeopleCodeParser.ExprMulDivContext ctx) {

    visit(ctx.expr(0));
    final PTPrimitiveType lhs =
        (PTPrimitiveType) this.getNodeData(ctx.expr(0));
    visit(ctx.expr(1));
    final PTPrimitiveType rhs =
        (PTPrimitiveType) this.getNodeData(ctx.expr(1));

    if (ctx.m != null) {
      this.setNodeData(ctx, lhs.mul(rhs));
    } else if (ctx.d != null) {
      this.setNodeData(ctx, lhs.div(rhs));
    } else {
      throw new OPSVMachRuntimeException("Unsupported mul/div operation.");
    }

    return null;
  }

  /**
   * Called by ANTLR when an addition or subtraction operation
   * within an expression in a statement is being visited
   * in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprAddSub(
      final PeopleCodeParser.ExprAddSubContext ctx) {

    visit(ctx.expr(0));
    final PTPrimitiveType lhs =
        (PTPrimitiveType) this.getNodeData(ctx.expr(0));
    visit(ctx.expr(1));
    final PTPrimitiveType rhs =
        (PTPrimitiveType) this.getNodeData(ctx.expr(1));

    if (ctx.a != null) {
      this.setNodeData(ctx, lhs.add(rhs));
    } else if (ctx.s != null) {
      this.setNodeData(ctx, lhs.subtract(rhs));
    } else {
      throw new OPSVMachRuntimeException("Unsupported add/sub operation.");
    }

    return null;
  }

  /**
   * Called by ANTLR when a create invocation within an expression
   * in a statement is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprCreate(
      final PeopleCodeParser.ExprCreateContext ctx) {
    visit(ctx.createInvocation());
    this.bubbleUp(ctx.createInvocation(), ctx);
    return null;
  }

  /**
   * Called by ANTLR when the dot operator in an expression within
   * a statement is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprDotAccess(
         final PeopleCodeParser.ExprDotAccessContext ctx) {

    //log.debug("BEGIN visitExprDotAccess: id={}, expr={}",
    //  ctx.id().getText(),
    //  ctx.expr().getText());

    visit(ctx.expr());
    visit(ctx.id());

    final PTObjectType obj = (PTObjectType) this.getNodeData(ctx.expr());
    final PTType prop = obj.dotProperty(ctx.id().getText());
    final Callable call = obj.dotMethod(ctx.id().getText());

    if (prop == null && call == null) {
      throw new OPSVMachRuntimeException("Failed to resolve identifier ("
          + ctx.id().getText() + ") to a property and/or method for object: "
          + obj);
    }

    this.setNodeData(ctx, prop);
    this.setNodeCallable(ctx, call);

    /*
     * If the identifier points to a getter on an app class object,
     * the getter should be run immediately in order to set the node data
     * for this context object.
     */
    if (call != null && call.eCtx != null
        && call.eCtx instanceof AppClassObjGetterExecContext) {
      this.supervisor.runImmediately(call.eCtx);

      final List<PTType> args = Environment.getArgsFromCallStack();
      if (args.size() != 1) {
        throw new OPSVMachRuntimeException("Getter should return exactly "
            + "one value.");
      }
      this.setNodeData(ctx, args.get(0));
      this.repeatLastEmission();
    }

    return null;
  }

  /**
   * Called by ANTLR when an array indexing operation within
   * an expression in a stement is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprArrayIndex(
      final PeopleCodeParser.ExprArrayIndexContext ctx) {

    //log.debug("BEGIN visitExprArrayIndex; expr is {}", ctx.expr().getText());

    visit(ctx.expr());
    final PTArray arrayObj = (PTArray) this.getNodeData(ctx.expr());

    PTType index = null;
    for (PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
      visit(argCtx);
      if (index != null) {
        throw new OPSVMachRuntimeException("Multiple array indexes "
            + "is not yet supported.");
      }
      index = this.getNodeData(argCtx);
      break;
    }

    //log.debug("About to index into {} with index {}.",
    //  arrayObj, index);

    this.setNodeData(ctx, arrayObj.getElement(index));
    return null;
  }

  /**
   * Called by ANTLR when a logical, non-equality comparison
   * operation within an expression in a statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprComparison(
      final PeopleCodeParser.ExprComparisonContext ctx) {
    visit(ctx.expr(0));
    final PTPrimitiveType a1 =
        (PTPrimitiveType) this.getNodeData(ctx.expr(0));
    visit(ctx.expr(1));
    final PTPrimitiveType a2 =
        (PTPrimitiveType) this.getNodeData(ctx.expr(1));

    PTBoolean result;
    if (ctx.l != null) {
      result = a1.isLessThan(a2);
      log.debug("isLessThan: {}? {}", ctx.getText(), result);
    } else if (ctx.g != null) {
      result = a1.isGreaterThan(a2);
      log.debug("isGreaterThan: {}? {}", ctx.getText(), result);
    } else if (ctx.ge != null) {
      result = a1.isGreaterThanOrEqual(a2);
      log.debug("isGreaterThanOrEqual: {}? {}", ctx.getText(), result);
    } else if (ctx.le != null) {
      result = a1.isLessThanOrEqual(a2);
      log.debug("isLessThanOrEqual: {}? {}", ctx.getText(), result);
    } else {
      throw new OPSVMachRuntimeException("Unknown comparison "
          + "operation encountered.");
    }
    this.setNodeData(ctx, result);
    return null;
  }

  /**
   * Called by ANTLR when a logical equality operation
   * within an expression in a statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprEquality(
      final PeopleCodeParser.ExprEqualityContext ctx) {
    visit(ctx.expr(0));
    log.debug("a1 = {}", this.getNodeData(ctx.expr(0)));
    final PTPrimitiveType a1 =
        (PTPrimitiveType) this.getNodeData(ctx.expr(0));
    visit(ctx.expr(1));
    final PTPrimitiveType a2 =
        (PTPrimitiveType) this.getNodeData(ctx.expr(1));

    PTBoolean result;
    if (ctx.e != null) {
      result = a1.isEqual(a2);
      log.debug("isEqual: {}? {}", ctx.getText(), result);

    } else if (ctx.i != null) {
      result = a1.isEqual(a2);
      if (result == Environment.TRUE) {
        result = Environment.FALSE;
      } else {
        result = Environment.TRUE;
      }
      log.debug("isNotEqual: {}? {}", ctx.getText(), result);

    } else {
      throw new OPSVMachRuntimeException("Unknown equality "
          + "operation encountered.");
    }
    this.setNodeData(ctx, result);
    return null;
  }

  /**
   * Called by ANTLR when a Boolean comparison operation
   * within an expression in a statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprBoolean(
      final PeopleCodeParser.ExprBooleanContext ctx) {

    if (ctx.op.getText().equals("Or")) {
      visit(ctx.expr(0));
      final PTBoolean lhs = (PTBoolean) this.getNodeData(ctx.expr(0));

      /*
       * Short-circuit evaluation: if lhs is true, this expression is true,
       * otherwise evaluate rhs and bubble up its value.
       */
      if (lhs.read()) {
        this.setNodeData(ctx, Environment.TRUE);
      } else {
        visit(ctx.expr(1));
        this.setNodeData(ctx, this.getNodeData(ctx.expr(1)));
      }
    } else if (ctx.op.getText().equals("And")) {
      visit(ctx.expr(0));
      final PTBoolean lhs = (PTBoolean) this.getNodeData(ctx.expr(0));
      visit(ctx.expr(1));
      final PTBoolean rhs = (PTBoolean) this.getNodeData(ctx.expr(1));

      if (lhs.read() && rhs.read()) {
        this.setNodeData(ctx, Environment.TRUE);
      } else {
        this.setNodeData(ctx, Environment.FALSE);
      }
    } else {
      throw new OPSInterpretException("Unsupported boolean comparison "
          + "operation", ctx.getText(), ctx.getStart().getLine());
    }

    return null;
  }

  /**
   * Called by ANTLR when a string concatentation operation
   * within an expression in a statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitExprConcat(
      final PeopleCodeParser.ExprConcatContext ctx) {
    visit(ctx.expr(0));
    final PTString lhs = (PTString) this.getNodeData(ctx.expr(0));
    visit(ctx.expr(1));
    final PTString rhs = (PTString) this.getNodeData(ctx.expr(1));

    this.setNodeData(ctx, lhs.concat(rhs));
    return null;
  }

  /*=========================================================
   * Primary rule handlers.
   *=========================================================*/

  /**
   * Called by ANTLR when a method implementation
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitMethodImpl(
      final PeopleCodeParser.MethodImplContext ctx) {
    this.emitStmt(ctx);

    final Scope localScope = new Scope(Scope.Lvl.METHOD_LOCAL);
    final List<FormalParam> formalParams =
        ((AppClassPeopleCodeProg) this.eCtx.prog).methodTable.
            get(ctx.GENERIC_ID().getText()).formalParams;

    /*
     * First, declare each formal parameter as a var in the local
     * scope, using the associated type constraint.
     */
    for (FormalParam fp : formalParams) {
      localScope.declareVar(fp.id, fp.typeConstraint);
    }

    /*
     * Then, assign each arg passed to the method to each of the
     * newly declared formal parameter variables (type checking
     * will be performed by invoked method).
     */
    final List<PTType> args = Environment.getArgsFromCallStack();
    for (int i = 0; i < formalParams.size() && i < args.size(); i++) {
      localScope.assignVar(formalParams.get(i).id, args.get(i));
    }

    this.eCtx.pushScope(localScope);
    visit(ctx.stmtList());
    this.eCtx.popScope();

    this.emitStmt("end-method");
    return null;
  }

  /**
   * Called by ANTLR when an app class getter (accessor) method
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitGetImpl(
      final PeopleCodeParser.GetImplContext ctx) {
    this.emitStmt(ctx);

    final Scope localScope = new Scope(Scope.Lvl.METHOD_LOCAL);
    this.eCtx.pushScope(localScope);
    visit(ctx.stmtList());
    this.eCtx.popScope();

    return null;
  }

  /**
   * Called by ANTLR when an identifier
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitId(
      final PeopleCodeParser.IdContext ctx) {

    if (ctx.SYS_VAR_ID() != null) {
      /*
       * Catch use of %This, which points to the app class obj
       * loaded in the current execution context.
       */
      if (ctx.SYS_VAR_ID().getText().equals("%This")) {
        this.setNodeData(ctx,
            ((AppClassObjExecContext) this.eCtx).appClassObj);
      } else {
        this.setNodeData(ctx,
            Environment.getSystemVar(ctx.SYS_VAR_ID().getText()));
      }

    } else if (ctx.VAR_ID() != null) {
      final PTType a = this.eCtx.resolveIdentifier(ctx.VAR_ID().getText());
      //log.debug("Resolved {} to {}.", ctx.VAR_ID().getText(), a);
      this.setNodeData(ctx, a);

    } else if (ctx.GENERIC_ID() != null) {
      //log.debug("Resolving GENERIC_ID: {}", ctx.GENERIC_ID().getText());

      /*
       * IMPORTANT NOTE: I believe it is possible to override system functions.
       * The checks on GENERIC_ID below should be run in order of lowest scope
       * to highest scope for this reason.
       */
      if (ComponentBuffer.getSearchRecord().getRecDefn()
            .RECNAME.equals(ctx.GENERIC_ID().getText())) {
        /*
         * Detect references to search record buffer.
         */
        this.setNodeData(ctx, ComponentBuffer.getSearchRecord());

      } else if (ComponentBuffer.ptGetLevel0().getRow(1)
          .hasRecord(ctx.GENERIC_ID().getText())) {
        /*
         * Detect references to a record in level 0 of the
         * component buffer (i.e., "DERIVED_REGRM1"
         * in "DERIVED_REGFRM1.GROUP_BOX...").
         */
        this.setNodeData(ctx, ComponentBuffer.ptGetLevel0()
            .getRow(1).getRecord(ctx.GENERIC_ID().getText()));

      } else if (this.eCtx.prog.funcImplNodes.containsKey(
          ctx.GENERIC_ID().getText())) {

        log.debug("Resolved GENERIC_ID: {} to an internal function "
            + "within this program.",
            ctx.GENERIC_ID().getText());

        /*
         * Detect references to internal functions (aren't imported
         * via Declare).
         */

        // Create an execution context pointing to the currently
        // running program and the name of the function to execute.
        FunctionExecContext fec = new FunctionExecContext(
            this.eCtx.prog, ctx.GENERIC_ID().getText());

        // CRITICAL: Override the start node to be the function, rather
        // than the program; according to PT rules, functions local to
        // a program share the same program-local variables and thus
        // should not be placed on the scope stack again.
        fec.startNode = fec.funcNodeToRun;

        // ALSO CRITICAL: For the same reason as above (for changing the
        // start node), the PROGRAM_LOCAL scope of the current context
        // must be placed on the scope stack of the execution context
        // that is about to run, so that changes to those variables
        // by the callee can be seen by the caller once the callee returns.
        Scope progLocalScope = this.eCtx.scopeStack.getLast();
        if (progLocalScope.getLevel() != Scope.Lvl.PROGRAM_LOCAL) {
          throw new OPSVMachRuntimeException("Expected first scope on "
              + "current program's scope stack to be PROGRAM_LOCAL; is "
              + "actually: " + progLocalScope.getLevel());
        }
        fec.pushScope(progLocalScope);

        this.setNodeCallable(ctx, new Callable(fec));

      } else if (this.eCtx.prog.recordProgFnCalls.containsKey(
          ctx.GENERIC_ID().getText())) {

        log.debug("Resolved GENERIC_ID: {} to an external function "
            + "referenced by this program.",
            ctx.GENERIC_ID().getText());

        /*
         * Detect references to external functions imported via Declare.
         */

        // Ensure external program has been init'ed and parsed.
        final PeopleCodeProg extProg = DefnCache.getProgram(this.eCtx.prog
            .recordProgFnCalls.get(ctx.GENERIC_ID().getText()));
        extProg.loadDefnsAndPrograms();

        // Create an execution context pointing to the external program
        // and the name of the function to execute.
        this.setNodeCallable(ctx, new Callable(new FunctionExecContext(
            extProg, ctx.GENERIC_ID().getText())));

      } else if (PSDefn.DEFN_LITERAL_RESERVED_WORDS_TABLE.containsKey(
        ctx.GENERIC_ID().getText().toUpperCase())) {

        //log.debug("Resolved GENERIC_ID: {} to DEFN_LITERAL",
        //    ctx.GENERIC_ID().getText());

        /*
         * Detect defn literal reserved words (i.e.,
         * "Menu" in "Menu.SA_LEARNER_SERVICES").
         */
        this.setNodeData(ctx, Environment.DEFN_LITERAL);

      } else if (Environment.getSystemFuncPtr(
          ctx.GENERIC_ID().getText()) != null) {
        /*
         * Detect system function references.
         */
        this.setNodeCallable(ctx, Environment.getSystemFuncPtr(
            ctx.GENERIC_ID().getText()));

      } else if (DefnCache.hasRecord(ctx.GENERIC_ID().getText())) {
        /*
         * Detect references to record field literals, which look like
         * references to component buffer records but are not actually
         * in the component buffer.
         * (i.e., record field literals passed to Sort() on Rowsets).
         */
        this.setNodeData(ctx, new PTRecordLiteral(
          DefnCache.getRecord(ctx.GENERIC_ID().getText())));

      }
    }
    return null;
  }

  /**
   * Called by ANTLR when a literal
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitLiteral(
      final PeopleCodeParser.LiteralContext ctx) {

    if (ctx.IntegerLiteral() != null) {

      final PTType ptr = Environment.getFromLiteralPool(
          new Integer(ctx.IntegerLiteral().getText()));
      this.setNodeData(ctx, ptr);

    } else if (ctx.BoolLiteral() != null) {

      final String b = ctx.BoolLiteral().getText();
      if (b.equals("True") || b.equals("true")) {
        this.setNodeData(ctx, Environment.TRUE);
      } else {
        this.setNodeData(ctx, Environment.FALSE);
      }

    } else if (ctx.DecimalLiteral() != null) {
      throw new OPSVMachRuntimeException("Encountered a decimal literal; "
        + "need to create a BigDecimal memory pool and type.");

    } else if (ctx.StringLiteral() != null) {
      /*
       * Strip surrounding double quotes from literal. Note that empty
       * strings ("") are a possibility here.
       */
      final String str = ctx.StringLiteral().getText();
      this.setNodeData(ctx, Environment.getFromLiteralPool(
          str.substring(1, str.length() - 1)));
    } else {
      throw new OPSVMachRuntimeException("Unable to resolve literal to "
          + "a terminal node.");
    }

    return null;
  }

  /**
   * Called by ANTLR when a variable declaration statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitVarDeclaration(
      final PeopleCodeParser.VarDeclarationContext ctx) {

    visit(ctx.varType());

    final String scope = ctx.varScope.getText();
    final PTTypeConstraint varTc = this.getNodeTypeConstraint(ctx.varType());
    boolean didInitializeAnIdentifier = false;

    final List<PeopleCodeParser.VarDeclaratorContext> varsToDeclare =
        ctx.varDeclarator();
    for (PeopleCodeParser.VarDeclaratorContext idCtx : varsToDeclare) {

      // First, declare the var.
      log.debug("Declaring identifier ({}) with scope {} and type constraint {}.",
          idCtx.VAR_ID().getText(), scope, varTc);
      this.declareIdentifier(scope, idCtx.VAR_ID().getText(), varTc);

      if (idCtx.expr() != null) {
        // Initial value expr exists, so assign it to declared var.
        visit(idCtx.expr());
        final PTType initialValue = this.getNodeData(idCtx.expr());
        this.assignIdentifier(scope, idCtx.VAR_ID().getText(), initialValue);
        didInitializeAnIdentifier = true;
      } else if (varTc.isUnderlyingClassPrimitive()) {
        /*
         * If no initial value expr exists, *AND* the type constraint
         * represents a primitive, a new primitive must be allocated
         * immediately.
         */
        this.assignIdentifier(scope, idCtx.VAR_ID().getText(),
            varTc.alloc());
      }
    }

    if (this.eCtx.prog instanceof AppClassPeopleCodeProg) {
      if (!this.hasVarDeclBeenEmitted
          || varTc.isUnderlyingClassEqualTo(PTRowset.class)
          || varTc.isUnderlyingClassEqualTo(PTAppClassObj.class)
          || varTc.isUnderlyingClassEqualTo(PTRecord.class)
          || didInitializeAnIdentifier) {
        this.emitStmt(ctx);
        this.hasVarDeclBeenEmitted = true;
      }
    } else if((this.eCtx instanceof FunctionExecContext) &&
        this.eCtx.scopeStack.getFirst().getLevel() == Scope.Lvl.FUNCTION_LOCAL) {
      this.emitStmt(ctx);
    }
    return null;
  }

  /**
   * Called by ANTLR when the variable type portion of
   * a variable declaration statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitVarType(
      final PeopleCodeParser.VarTypeContext ctx) {

    if (ctx.appClassPath() != null) {
/*      visit(ctx.appClassPath());
      this.setNodeData(ctx, this.getNodeData(ctx.appClassPath()));*/
      throw new OPSVMachRuntimeException("TYPE SYSTEM: Create PTTypeConstraint "
          + "for app class paths.");
    } else {
      PTTypeConstraint nestedTypeConstraint = null;
      if (ctx.varType() != null
            && !ctx.GENERIC_ID().getText().equals("array")) {
        throw new OPSVMachRuntimeException("Encountered non-array var "
            + "type preceding 'of' clause: " + ctx.getText());
      }
      visit(ctx.varType());
      nestedTypeConstraint = this.getNodeTypeConstraint(ctx.varType());

      PTTypeConstraint typeConstraint;
      switch (ctx.GENERIC_ID().getText()) {
        case "array":
/*          if (nestedType instanceof PTArray) {
            type = PTArray.getSentinel(
              ((PTArray) nestedType).dimensions + 1, nestedType);
          } else {
            type = PTArray.getSentinel(1, nestedType);
          }
          break;*/
          throw new OPSVMachRuntimeException("TYPE SYSTEM: Subclass "
              + "PTTypeConstraint for use with arrays.");
        case "string":
          typeConstraint = new PTTypeConstraint<PTString>(PTString.class);
          break;
        case "date":
          typeConstraint = new PTTypeConstraint<PTDate>(PTDate.class);
          break;
        case "integer":
          typeConstraint = new PTTypeConstraint<PTInteger>(PTInteger.class);
          break;
        case "Record":
          typeConstraint = new PTTypeConstraint<PTRecord>(PTRecord.class);
          break;
        case "Rowset":
          typeConstraint = new PTTypeConstraint<PTRowset>(PTRowset.class);
          break;
        case "number":
          typeConstraint = new PTTypeConstraint<PTNumber>(PTNumber.class);
          break;
        case "boolean":
          typeConstraint = new PTTypeConstraint<PTBoolean>(PTBoolean.class);
          break;
        case "Field":
          typeConstraint = new PTTypeConstraint<PTField>(PTField.class);
          break;
        default:
          throw new OPSVMachRuntimeException("Unexpected data type: "
              + ctx.GENERIC_ID().getText());
      }
      this.setNodeTypeConstraint(ctx, typeConstraint);
    }
    return null;
  }

  /**
   * Called by ANTLR when an Evaluate statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitEvaluateStmt(
      final PeopleCodeParser.EvaluateStmtContext ctx) {
    this.emitStmt(ctx);

    visit(ctx.expr());

    log.debug("Interpreting Evaluate stmt; conditional value is {}",
      this.getNodeData(ctx.expr()));

    final EvaluateConstruct evalConstruct = new EvaluateConstruct(
        this.getNodeData(ctx.expr()));
    this.evalConstructStack.push(evalConstruct);

    final List<PeopleCodeParser.WhenBranchContext> branches = ctx.whenBranch();
    for (int i = 0; i < branches.size(); i++) {
      PeopleCodeParser.WhenBranchContext branchCtx = branches.get(i);
      try {
        visit(branchCtx);
      } catch (OPSBreakSignalException opsbse) {

        // If this is the last When branch, the Break is self-evident
        // and should not be emitted.
        if(i < (branches.size() - 1)) {
          this.emitStmt("Break");
        }

        evalConstruct.breakSeen = true;
        break;
      }
    }

    if (!evalConstruct.breakSeen && ctx.whenOtherBranch() != null) {
      visit(ctx.whenOtherBranch());
    }

    this.evalConstructStack.pop();

    return null;
  }

  /**
   * Called by ANTLR when a When branch of an Evaluate statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitWhenBranch(
      final PeopleCodeParser.WhenBranchContext ctx) {

    if (ctx.op != null) {
      throw new OPSVMachRuntimeException("Encountered relational op in when "
          + "branch, not yet supported.");
    }

    final EvaluateConstruct evalConstruct = this.evalConstructStack.peek();
    final PTType p1 = evalConstruct.baseExpr;
    visit(ctx.expr());
    final PTType p2 = this.getNodeData(ctx.expr());

    // Always emit the first When branch of an Evaluate statement
    if (!evalConstruct.hasBranchBeenEmitted) {
      this.emitStmt(ctx);
      evalConstruct.hasBranchBeenEmitted = true;

    // If this isn't the first When branch, emit it only if a true
    // When branch has not yet been seen *AND* if the last When branch
    // seen did not have an empty statement list.
    } else if(!evalConstruct.trueBranchExprSeen &&
        !evalConstruct.wasLastWhenBranchStmtListEmpty) {
      this.emitStmt(ctx);
    }

    evalConstruct.wasLastWhenBranchStmtListEmpty =
        (ctx.stmtList().getChildCount() == 0);

    /*
     * If p1 equals p2, we have reached a When branch that evaluates
     * to true; if they aren't equal, we must still check if we saw a true
     * branch expr earlier b/c control may be falling through When branches
     * (occurs until a Break is seen or the Evaluate statement ends).
     */
    PTBoolean eq = ((PTPrimitiveType) p1).isEqual((PTPrimitiveType) p2);
    if (eq == Environment.TRUE || evalConstruct.trueBranchExprSeen) {
      evalConstruct.trueBranchExprSeen = true;
      visit(ctx.stmtList());
    }

    return null;
  }

  /**
   * Called by ANTLR when a When-Other branch of an Evaluate statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitWhenOtherBranch(
      final PeopleCodeParser.WhenOtherBranchContext ctx) {

    final EvaluateConstruct evalConstruct = this.evalConstructStack.peek();
    this.emitStmt(ctx);

    /*
     * Execution cannot fall through to When-Other; if no break was seen
     * in a true branch body, the When-Other body should not run. Only run
     * if no true branches were seen.
     */
    if (!evalConstruct.trueBranchExprSeen) {
      visit(ctx.stmtList());

      // Only emit End-Evaluate if no true branch has yet been seen.
      this.emitStmt("End-Evaluate");
    }

    return null;
  }

  /**
   * Called by ANTLR when an app class path
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitAppClassPath(
      final PeopleCodeParser.AppClassPathContext ctx) {
    final AppClassPeopleCodeProg progDefn =
        (AppClassPeopleCodeProg) DefnCache.getProgram(("AppClassPC."
            + ctx.getText() + ".OnExecute").replaceAll(":", "."));
    throw new OPSVMachRuntimeException("TODO: Finish passing app class type constraint.");
 //   this.setNodeTypeConstraint(ctx,
//        new PTAppClassObjTypeConstraint<PTAppClassObj>(PTAppClassObj.class, progDefn));
//    return null;
  }

  /**
   * Called by ANTLR when a create invocation
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitCreateInvocation(
      final PeopleCodeParser.CreateInvocationContext ctx) {

    PTAppClassObj objType;
    if (ctx.appClassPath() != null) {
      visit(ctx.appClassPath());
      objType = (PTAppClassObj) this.getNodeData(ctx.appClassPath());
    } else {
      throw new OPSVMachRuntimeException("Encountered create invocation "
          + "without app class prefix; need to support this by resolving "
          + "path to class.");
    }

    /*
     * Instantation of an app class object requires that the instance
     * and method information for the class be known ahead of time.
     * This info is in the class declaration body at the start of the
     * program. If the class declaration has not yet been processed, do so now.
     */
    if (!objType.progDefn.hasClassDefnBeenLoaded) {
      objType.progDefn.loadDefnsAndPrograms();
      final ExecContext classDeclCtx = new AppClassDeclExecContext(objType);
      this.supervisor.runImmediately(classDeclCtx);
    }

    final PTAppClassObj newObj = objType.alloc();
    this.setNodeData(ctx, newObj);

    /*
     * Check for constructor; call it if it exists.
     * TODO(mquinn): If issues arise with this, I probably need to check
     * the method declaration to ensure that the constructor (if it exists)
     * is only called when the appropriate number of arguments are supplied.
     */
    if (newObj.progDefn.hasConstructor()) {
      /*
       * Load arguments to constructor onto call stack if
       * args have been provided.
       */
      Environment.pushToCallStack(PTCallFrameBoundary.getSingleton());
      if (ctx.exprList() != null) {
        for (PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
          visit(argCtx);
          Environment.pushToCallStack(this.getNodeData(argCtx));
        }
      }

      final String constructorName = newObj.progDefn.appClassName;
      final ExecContext constructorCtx =
          new AppClassObjMethodExecContext(newObj, constructorName,
              newObj.progDefn.getMethodImplStartNode(constructorName), null);
      this.supervisor.runImmediately(constructorCtx);

      // Constructors don't return anything
      if(!(Environment.popFromCallStack() instanceof PTCallFrameBoundary)) {
        throw new OPSVMachRuntimeException("After invoking create statement, "
            + "expected call frame boundary, but found data instead.");
      }

      this.repeatLastEmission();
    }

    return null;
  }

  /**
   * Called by ANTLR when a Function declaration
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitFuncImpl(
      final PeopleCodeParser.FuncImplContext ctx) {

    if(this.eCtx instanceof FunctionExecContext) {
        /**
         * If this function is the function named in the execution
         * context, continue interpretation; otherwise, signal to
         * to the supervisor that it needs to switch to the correct
         * ParseTree node.
         */
        if (((FunctionExecContext) this.eCtx).funcName.equals(
            ctx.funcSignature().GENERIC_ID().getText())) {

          this.emitStmt(ctx.funcSignature());
          visit(ctx.funcSignature());

          final Scope localScope = new Scope(Scope.Lvl.FUNCTION_LOCAL);
          final List<FormalParam> formalParams =
              this.eCtx.prog.funcTable
                .get(ctx.funcSignature().GENERIC_ID().getText()).formalParams;

          /*
           * First, declare each formal parameter as a var in the local
           * scope, using the associated type constraint.
           */
          for (FormalParam fp : formalParams) {
            localScope.declareVar(fp.id, fp.typeConstraint);
          }

          /*
           * Then, assign each arg passed to the method to each of the
           * newly declared formal parameter variables (type checking
           * will be performed by invoked method).
           */
          final List<PTType> args = Environment.getArgsFromCallStack();
          for (int i = 0; i < formalParams.size() && i < args.size(); i++) {
            localScope.assignVar(formalParams.get(i).id, args.get(i));
          }

          this.eCtx.pushScope(localScope);
          visit(ctx.stmtList());
          this.eCtx.popScope();

          this.emitStmt("End-Function");

        } else {
          throw new OPSFuncImplSignalException(ctx.
              funcSignature().GENERIC_ID().getText());
        }
    } else {
      throw new OPSVMachRuntimeException("Attempt to execute function body "
          + "outside of FunctionExecContext is not yet supported.");
    }

    return null;
  }

  private void declareIdentifier(final String scope,
      final String id, final PTTypeConstraint tc) {
    switch(scope) {
      case "Local":
        this.eCtx.declareLocalVar(id, tc);
        break;
      case "Component":
        Environment.componentScope.declareVar(id, tc);
        break;
      case "Global":
        Environment.globalScope.declareVar(id, tc);
        break;
      default:
        throw new OPSVMachRuntimeException("Encountered unexpected variable "
            + " scope: " + scope);
    }
  }

  private void assignIdentifier(final String scope,
      final String id, final PTType t) {
    switch(scope) {
      case "Local":
        this.eCtx.assignLocalVar(id, t);
        break;
      case "Component":
        Environment.componentScope.assignVar(id, t);
        break;
      case "Global":
        Environment.globalScope.assignVar(id, t);
        break;
      default:
        throw new OPSVMachRuntimeException("Encountered unexpected variable "
            + " scope: " + scope);
    }
  }

  private final class EvaluateConstruct {
    private PTType baseExpr;
    private boolean hasBranchBeenEmitted, trueBranchExprSeen, breakSeen,
      wasLastWhenBranchStmtListEmpty;
    private EvaluateConstruct(final PTType p) {
      this.baseExpr = p;
    }
  }
}

