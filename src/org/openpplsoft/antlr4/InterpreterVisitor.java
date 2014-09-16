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
import java.util.Iterator;

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
  private AccessLevel blockAccessLvl;
  private PeopleCodeParser.StmtBreakContext lastSeenBreakContext;
  private InterpreterEmissionsFilter eFilter;
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
    this.eFilter = new InterpreterEmissionsFilter(this.tokens);
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
    this.eFilter.emit(ctx);

    // Get value of conditional expression.
    visit(ctx.expr());
    final boolean exprResult =
        ((PTBoolean) this.getNodeData(ctx.expr())).read();

    // If expression evaluates to true, visit the conditional body;
    // otherwise, visit the Else body if it exists.
    if (exprResult) {
      visit(ctx.stmtList(0));
      if (ctx.stmtList(1) != null) {
        this.eFilter.emit(ctx.elsetok);
      } else {
        this.eFilter.emit(ctx.endif);
      }
    } else {
      if (ctx.stmtList(1) != null) {
        visit(ctx.stmtList(1));
        this.eFilter.emit(ctx.endif);
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

    PTNumberType counter = null;
    try {
      counter = this.getOrDerefNumber(
          this.eCtx.resolveIdentifier(ctx.VAR_ID().getText()));
    } catch(final ExecContext.OPSVMachIdentifierResolutionException ire) {
      throw new OPSVMachRuntimeException("Unable to resolve var id in For "
          + "stmt.", ire);
    }

    visit(ctx.expr(0));
    final PTNumberType initialExpr =
        (PTNumberType) this.getNodeData(ctx.expr(0));

    // Initialize incrementing expression.
    counter.copyValueFrom(initialExpr);

    visit(ctx.expr(1));
    final PTNumberType toExpr =
        this.getOrDerefNumber(this.getNodeData(ctx.expr(1)));

    this.eFilter.emit(ctx);
    while (counter.isLessThanOrEqual(toExpr).read()) {

      try {
        visit(ctx.stmtList());
      } catch (OPSBreakSignalException opsbse) {
        this.eFilter.emit(this.lastSeenBreakContext);
        break;
      }

      // Increment and set new value of incrementing expression.
      counter.copyValueFrom(counter.add(new PTInteger(1)));

      this.eFilter.emit(ctx.endfor);
      this.eFilter.emit(ctx);
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
    this.lastSeenBreakContext = ctx;
    throw new OPSBreakSignalException();
  }

  /**
   * Called by ANTLR when an assignment statement
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  @SuppressWarnings("unchecked")
  public Void visitStmtAssign(
      final PeopleCodeParser.StmtAssignContext ctx) {
    this.eFilter.emit(ctx);
    visit(ctx.expr(1));
    final PTType src = this.getNodeData(ctx.expr(1));
    visit(ctx.expr(0));
    final PTType dst = this.getNodeData(ctx.expr(0));

    if (!(dst instanceof PTReference)) {
      throw new OPSVMachRuntimeException("Illegal assignment; not a valid l-value: "
          + dst);
    }

    final PTReference lRef = (PTReference) dst;
    PTType rawSrc = src;

    if (src instanceof PTReference) {
      rawSrc = ((PTReference) src).deref();
    }

    if (rawSrc instanceof PTPrimitiveType) {
      if (lRef.deref() instanceof PTPrimitiveType) {
        ((PTPrimitiveType) lRef.deref()).copyValueFrom((PTPrimitiveType) rawSrc);
      } else if (lRef.deref() instanceof PTField) {
        ((PTField) lRef.deref()).getValue().copyValueFrom((PTPrimitiveType) rawSrc);
      } else {
        throw new OPSVMachRuntimeException("Assignment failed; rawSrc is primitive "
            + "but lRef dereferences to neither a primitive nor a PTField.");
      }
    } else if(rawSrc instanceof PTObjectType) {
      try {
        lRef.pointTo(rawSrc);
      } catch (final OPSImmutableRefAttemptedChangeException opsirace) {
        if (rawSrc instanceof PTField && lRef.deref() instanceof PTPrimitiveType) {
          // If lRef refers to a primitive and rawSrc is a Field, re-attempt the
          // assignment, this time copying the value from
          // the Field's underlying value to the referred primitive.
          ((PTPrimitiveType) lRef.deref()).copyValueFrom(((PTField) rawSrc).getValue());
        } else {
          throw new OPSVMachRuntimeException("Assignment failed, even after "
              + "checking if rawSrc is a PTField that needs to be unwrapped "
              + "to its enclosed value.", opsirace);
        }
      } catch (final OPSTypeCheckException opstce) {
        throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
      }
    } else {
      throw new OPSVMachRuntimeException("Assignment failed; unexpected "
          + "rawSrc: " + src + "; lRef is " + lRef);
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
    this.eFilter.emit(ctx);
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
    this.eFilter.emit(ctx);
    if (ctx.expr() != null) {
      visit(ctx.expr());

      PTType retVal = this.getNodeData(ctx.expr());
      if (retVal instanceof PTReference) {
        // References cannot be returned; dereference first.
        retVal = ((PTReference) retVal).deref();
      }

      if (this.eCtx instanceof AppClassObjExecContext) {
        try {
          ((AppClassObjExecContext) this.eCtx)
              .expectedReturnTypeConstraint.typeCheck(retVal);
          Environment.pushToCallStack(retVal);
        } catch (final OPSTypeCheckException opstce) {
          throw new OPSVMachRuntimeException("Value returned in app class "
              + "obj execution context does not match the expected type.", opstce);
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
     * If the node represents a rowset, or a reference to one,
     * this expression is a row indexing operation
    *  (i.e., "&rs(1)"). Call getRow() to run
     * the indexing operation.
     */
    if (t instanceof PTRowset) {
      ((PTRowset) t).PT_GetRow();
    } else if (t instanceof PTReference
        && ((PTReference) t).deref() instanceof PTRowset) {
      ((PTRowset) ((PTReference) t).deref()).PT_GetRow();
    } else if(call == null) {
      throw new OPSVMachRuntimeException("No Callable exists for fn expression: "
          + ctx.expr().getText());
    } else if (call.ptMethod != null) {
      call.invokePtMethod();
    } else {
      this.supervisor.runImmediately(call.eCtx);
      if(!(call.eCtx instanceof FunctionExecContext)) {
        this.eFilter.repeatLastEmission();
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
    final PTNumberType lhs =
        (PTNumberType) this.getNodeData(ctx.expr(0));
    visit(ctx.expr(1));
    final PTNumberType rhs =
        (PTNumberType) this.getNodeData(ctx.expr(1));

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
    final PTNumberType lhs =
        (PTNumberType) this.getNodeData(ctx.expr(0));
    visit(ctx.expr(1));
    final PTNumberType rhs =
        (PTNumberType) this.getNodeData(ctx.expr(1));

    if (ctx.a != null) {
      this.setNodeData(ctx, lhs.add(rhs));
    } else if (ctx.s != null) {
      this.setNodeData(ctx, lhs.sub(rhs));
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

    final PTObjectType obj = this.getOrDerefObject(this.getNodeData(ctx.expr()));
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
      this.eFilter.repeatLastEmission();
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
    PTType t = this.getNodeData(ctx.expr());

    for (PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
      visit(argCtx);
      PTType indexExpr = this.getNodeData(argCtx);
//      log.debug("About to index into {} with index expr {}.",
//        t, indexExpr);

      if (!(t instanceof PTArray)) {
        throw new OPSVMachRuntimeException("Object to index into is "
            + "not an array; illegal object: " + t);
      }

      /*
       * Because multiple expressions are supported in an array
       * indexing expression (i.e., &arr[1, 5]), we need to overwrite
       * the current array beind indexed into b/c there may be another
       * expression that will index into the array resulting here:
       */
      t = ((PTArray) t).getElement(indexExpr);
    }

    this.setNodeData(ctx, t);
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
    final PTPrimitiveType lhs = this.getOrDerefPrimitive(
        this.getNodeData(ctx.expr(0)));

    visit(ctx.expr(1));
    final PTPrimitiveType rhs = this.getOrDerefPrimitive(
        this.getNodeData(ctx.expr(1)));

    PTBoolean result;
    if (ctx.l != null) {
      result = lhs.isLessThan(rhs);
      log.debug("isLessThan: {}? {}", ctx.getText(), result);
    } else if (ctx.g != null) {
      result = lhs.isGreaterThan(rhs);
      log.debug("isGreaterThan: {}? {}", ctx.getText(), result);
    } else if (ctx.ge != null) {
      result = lhs.isGreaterThanOrEqual(rhs);
      log.debug("isGreaterThanOrEqual: {}? {}", ctx.getText(), result);
    } else if (ctx.le != null) {
      result = lhs.isLessThanOrEqual(rhs);
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
    final PTPrimitiveType lhs = this.getOrDerefPrimitive(
        this.getNodeData(ctx.expr(0)));

    visit(ctx.expr(1));
    final PTPrimitiveType rhs = this.getOrDerefPrimitive(
        this.getNodeData(ctx.expr(1)));

    PTBoolean result;
    if (ctx.e != null) {
      result = lhs.isEqual(rhs);
      log.debug("isEqual: {}? {}", ctx.getText(), result);

    } else if (ctx.i != null) {
      result = lhs.isEqual(rhs);
      if (result.read()) {
        result = new PTBoolean(false);
      } else {
        result = new PTBoolean(true);
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
        this.setNodeData(ctx, new PTBoolean(true));
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
        this.setNodeData(ctx, new PTBoolean(true));
      } else {
        this.setNodeData(ctx, new PTBoolean(false));
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
    this.eFilter.emit(ctx);

    final Scope localScope = new Scope(Scope.Lvl.METHOD_LOCAL);
    final List<FormalParam> formalParams =
        ((AppClassPeopleCodeProg) this.eCtx.prog).methodTable.
            get(ctx.GENERIC_ID().getText()).formalParams;

    // This logic is externalized from this method b/c visitMethodImpl
    // shares the same logic.
    this.bindArgsToFormalParams(localScope, formalParams);

    this.eCtx.pushScope(localScope);
    visit(ctx.stmtList());
    this.eCtx.popScope();

    this.eFilter.emit(ctx.endmethod);
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
    this.eFilter.emit(ctx);

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

      PTType a = null;
      final String varId = ctx.VAR_ID().getText();
      try {
        a = this.eCtx.resolveIdentifier(varId);
      } catch(final ExecContext.OPSVMachIdentifierResolutionException ire1) {

        /*
         * If the var identifier cannot be resolved, it must be auto-declared,
         * which is one of the saddest language features of PeopleCode.
         * Auto-declared vars always have Local scope and are of type Any.
         */
        this.declareIdentifier("Local", varId, new PTAnyTypeConstraint());
        log.debug("Auto-declared identifier {} (Local scope, of type Any).", varId);

        try {
          a = this.eCtx.resolveIdentifier(varId);
        } catch(final ExecContext.OPSVMachIdentifierResolutionException ire2) {
          throw new OPSVMachRuntimeException("Failed to resolve identifier "
              + varId + " after auto-declaring it.", ire2);
        }
      }
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

      } else if (this.eCtx.prog.hasFunctionImplNamed(
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
        this.setNodeData(ctx, PTDefnLiteral.getSingleton());

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

      final PTType ptr = new PTInteger(new Integer(ctx.IntegerLiteral().getText()));
      this.setNodeData(ctx, ptr);

    } else if (ctx.BoolLiteral() != null) {

      final String b = ctx.BoolLiteral().getText();
      if (b.equals("True") || b.equals("true")) {
        this.setNodeData(ctx, new PTBoolean(true));
      } else {
        this.setNodeData(ctx, new PTBoolean(false));
      }

    } else if (ctx.DecimalLiteral() != null) {
      throw new OPSVMachRuntimeException("Encountered a decimal literal; "
        + "get a PTNumber from the literal pool; IT IS ABSOLUTELY IMPORTANT"
        + " THAT YOU DO NOT CONVERT THE DECIMAL LITERAL TEXT FROM STRING TO DOUBLE "
        + " WHEN CREATING THE BIGDECIMAL TO PASS AS ARG TO getFromLiteralPool;"
        + " you risk creating a BigDecimal with an inexact value. Just pass the text"
        + " of the parsed decimal literal token to the BigDecimal constructor.");

    } else if (ctx.StringLiteral() != null) {
      /*
       * Strip surrounding double quotes from literal. Note that empty
       * strings ("") are a possibility here.
       */
      final String str = ctx.StringLiteral().getText();
      this.setNodeData(ctx, new PTString(str.substring(1, str.length() - 1)));
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

      if (idCtx.expr() != null) {
        // If initial value expr exists, declare *and* initialize the var.
        visit(idCtx.expr());
        final PTType initialValue = this.getNodeData(idCtx.expr());
        this.declareAndInitIdentifier(scope,
            idCtx.VAR_ID().getText(), varTc, initialValue);
        didInitializeAnIdentifier = true;
      } else {
        // Otherwise, just declare the identifier.
        this.declareIdentifier(scope, idCtx.VAR_ID().getText(), varTc);
      }
    }

    if (this.eCtx.prog instanceof AppClassPeopleCodeProg) {
      if (!this.hasVarDeclBeenEmitted
          || varTc.isUnderlyingClassEqualTo(PTRowset.class)
          || varTc.isUnderlyingClassEqualTo(PTAppClassObj.class)
          || varTc.isUnderlyingClassEqualTo(PTRecord.class)
          || (varTc.isUnderlyingClassEqualTo(PTNumber.class)
              && varsToDeclare.size() == 1
              && !didInitializeAnIdentifier)
          || didInitializeAnIdentifier) {
        this.eFilter.emit(ctx);
        this.hasVarDeclBeenEmitted = true;
      }
    } else if((this.eCtx instanceof FunctionExecContext) &&
        this.eCtx.scopeStack.getFirst().getLevel() == Scope.Lvl.FUNCTION_LOCAL) {
      this.eFilter.emit(ctx);
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
      visit(ctx.appClassPath());
      this.setNodeTypeConstraint(ctx,
          this.getNodeTypeConstraint(ctx.appClassPath()));
    } else {
      PTTypeConstraint nestedTc = null;
      if (ctx.varType() != null) {
        if(!ctx.GENERIC_ID().getText().equals("array")) {
          throw new OPSVMachRuntimeException("Encountered non-array var "
              + "type preceding 'of' clause: " + ctx.getText());
        }
        visit(ctx.varType());
        nestedTc = this.getNodeTypeConstraint(ctx.varType());
      }

      PTTypeConstraint typeConstraint;
      switch (ctx.GENERIC_ID().getText()) {
        case "array":
          if (nestedTc instanceof PTArrayTypeConstraint) {
            typeConstraint = new PTArrayTypeConstraint(
                ((PTArrayTypeConstraint) nestedTc).getReqdDimension() + 1,
                    nestedTc);
          } else {
            typeConstraint = new PTArrayTypeConstraint(1, nestedTc);
          }
          break;
        case "string":
          typeConstraint = PTString.getTc();
          break;
        case "date":
          typeConstraint = PTDate.getTc();
          break;
        case "integer":
          typeConstraint = PTInteger.getTc();
          break;
        case "Record":
          typeConstraint = new PTRecordTypeConstraint();
          break;
        case "Rowset":
          typeConstraint = new PTRowsetTypeConstraint();
          break;
        case "number":
          typeConstraint = PTNumber.getTc();
          break;
        case "boolean":
          typeConstraint = PTBoolean.getTc();
          break;
        case "Field":
          typeConstraint = new PTFieldTypeConstraint();
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
    this.eFilter.emit(ctx);

    visit(ctx.expr());
    final PTPrimitiveType baseExpr = this.getOrDerefPrimitive(
        this.getNodeData(ctx.expr()));
    log.debug("Interpreting Evaluate stmt; conditional base value is {}", baseExpr);

    final EvaluateConstruct evalConstruct = new EvaluateConstruct(baseExpr);
    this.evalConstructStack.push(evalConstruct);

    // Store reference to End-Evaluate token for emission by other methods.
    evalConstruct.endEvaluateToken = ctx.endevaluate;

    final List<PeopleCodeParser.WhenBranchContext> branches = ctx.whenBranch();
    for (int i = 0; i < branches.size(); i++) {
      PeopleCodeParser.WhenBranchContext branchCtx = branches.get(i);
      try {
        visit(branchCtx);
      } catch (OPSBreakSignalException opsbse) {

        // If this is the last When branch, the Break is self-evident
        // and should not be emitted.
        if(i < (branches.size() - 1)) {
          this.eFilter.emit(this.lastSeenBreakContext);
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
      this.eFilter.emit(ctx);
      evalConstruct.hasBranchBeenEmitted = true;

    // If this isn't the first When branch, emit it only if a true
    // When branch has not yet been seen *AND* if the last When branch
    // seen did not have an empty statement list.
    } else if(!evalConstruct.trueBranchExprSeen &&
        !evalConstruct.wasLastWhenBranchStmtListEmpty) {
      this.eFilter.emit(ctx);
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
    if (eq.read() || evalConstruct.trueBranchExprSeen) {
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
    this.eFilter.emit(ctx);

    /*
     * Execution cannot fall through to When-Other; if no break was seen
     * in a true branch body, the When-Other body should not run. Only run
     * if no true branches were seen.
     */
    if (!evalConstruct.trueBranchExprSeen) {
      visit(ctx.stmtList());

      // Only emit End-Evaluate if no true branch has yet been seen.
      this.eFilter.emit(evalConstruct.endEvaluateToken);
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
    this.setNodeTypeConstraint(ctx, new PTAppClassObjTypeConstraint(progDefn));
    return null;
  }

  /**
   * Called by ANTLR when a create invocation
   * is being visited in the parse tree.
   * @param ctx the associated ParseTree node
   * @return null
   */
  public Void visitCreateInvocation(
      final PeopleCodeParser.CreateInvocationContext ctx) {

    PTAppClassObjTypeConstraint objTc;
    if (ctx.appClassPath() != null) {
      visit(ctx.appClassPath());
      objTc = (PTAppClassObjTypeConstraint)
          this.getNodeTypeConstraint(ctx.appClassPath());
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
    if (!objTc.getReqdProgDefn().hasClassDefnBeenLoaded) {
      objTc.getReqdProgDefn().loadDefnsAndPrograms();
      final ExecContext classDeclCtx = new AppClassDeclExecContext(objTc);
      this.supervisor.runImmediately(classDeclCtx);
    }

    final PTAppClassObj newObj = objTc.alloc();
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

      this.eFilter.repeatLastEmission();
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

          this.eFilter.emit(ctx.funcSignature());
          visit(ctx.funcSignature());

          final Scope localScope = new Scope(Scope.Lvl.FUNCTION_LOCAL);
          final List<FormalParam> formalParams =
              this.eCtx.prog.getFunction(
                ctx.funcSignature().GENERIC_ID().getText()).formalParams;

          // This logic is externalized from this method b/c visitMethodImpl
          // shares the same logic.
          this.bindArgsToFormalParams(localScope, formalParams);

          this.eCtx.pushScope(localScope);
          visit(ctx.stmtList());
          this.eCtx.popScope();

          this.eFilter.emit(ctx.endfunction);

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

  private void declareAndInitIdentifier(final String scope,
      final String id, final PTTypeConstraint tc, final PTType initialVal) {
    try {
      switch(scope) {
        case "Local":
          this.eCtx.declareAndInitLocalVar(id, tc, initialVal);
          break;
        case "Component":
          Environment.componentScope.declareAndInitVar(id, tc, initialVal);
          break;
        case "Global":
          Environment.globalScope.declareAndInitVar(id, tc, initialVal);
          break;
        default:
          throw new OPSVMachRuntimeException("Encountered unexpected variable "
              + " scope: " + scope);
      }
    } catch (final OPSTypeCheckException opstce) {
      throw new OPSVMachRuntimeException("Failed to declare and initialize "
          + "identifier; type check exception occurred.", opstce);
    }
  }

  private void bindArgsToFormalParams(final Scope localScope,
      final List<FormalParam> formalParams) {

    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != formalParams.size()) {
      throw new OPSVMachRuntimeException("Unable to bind args to formal "
          + "params due to size mismatch.");
    }

    // Declare and initialize each identifier with the matching arg value.
    for (int i = 0; i < formalParams.size() && i < args.size(); i++) {
      PTType arg = args.get(i);
      FormalParam fp = formalParams.get(i);

      if (this.eCtx instanceof AppClassObjExecContext) {
        /**
         * App class obj methods only allow references to be passed
         * when using the "out" reserved word in the formal parameter
         * definition. This is not supported at this time, so I am
         * unwrapping all references for now.
         * TODO(mquinn): Keep this in mind.
         */
        if (arg instanceof PTReference) {
          arg = ((PTReference) arg).deref();
        }
      } else {
        throw new OPSVMachRuntimeException("Unexpected execution context "
            + "in which args are being bound to formal parameters.");
      }

      try {
        localScope.declareAndInitVar(fp.id, fp.typeConstraint, arg);
      } catch (final OPSTypeCheckException opstce1) {
        /**
         * It's possible that the argument is a field object that
         * needs to have its "default method" (getValue()) called prior
         * to binding it to the formal param identifier.
         */
        if (arg instanceof PTField) {
          try {
            localScope.declareAndInitVar(fp.id, fp.typeConstraint,
                ((PTField) arg).getValue());
          } catch (final OPSTypeCheckException opstce2) {
            throw new OPSVMachRuntimeException("Failed to bind arg to param, "
                + "even after unwrapping the arg of type Field, due to type "
                + "check exception.", opstce2);
          }
        } else {
            throw new OPSVMachRuntimeException(opstce1.getMessage(), opstce1);
        }
      }
    }
  }

  private PTPrimitiveType getOrDerefPrimitive(final PTType rawExpr) {
    if (rawExpr instanceof PTPrimitiveType) {
      return (PTPrimitiveType) rawExpr;
    } else if (rawExpr instanceof PTReference
        && ((PTReference) rawExpr).deref() instanceof PTPrimitiveType) {
      return (PTPrimitiveType) ((PTReference) rawExpr).deref();
    } else {
      throw new OPSVMachRuntimeException("Expected either a primitive or a reference "
          + "to one (getOrDerefPrimitive).");
    }
  }

  private PTObjectType getOrDerefObject(final PTType rawExpr) {
    if (rawExpr instanceof PTObjectType) {
      return (PTObjectType) rawExpr;
    } else if (rawExpr instanceof PTReference
        && ((PTReference) rawExpr).deref() instanceof PTObjectType) {
      return (PTObjectType) ((PTReference) rawExpr).deref();
    } else {
      throw new OPSVMachRuntimeException("Expected either an object or a reference "
          + "to one (getOrDerefObject).");
    }
  }

  private PTNumberType getOrDerefNumber(final PTType rawExpr) {
    if (rawExpr instanceof PTNumberType) {
      return (PTNumberType) rawExpr;
    } else if (rawExpr instanceof PTReference
        && ((PTReference) rawExpr).deref() instanceof PTNumberType) {
      return (PTNumberType) ((PTReference) rawExpr).deref();
    } else {
      throw new OPSVMachRuntimeException("Expected either a number or a reference "
          + "to one (getOrDerefNumber).");
    }
  }

  private final class EvaluateConstruct {
    private PTType baseExpr;
    private Token endEvaluateToken;
    private boolean hasBranchBeenEmitted, trueBranchExprSeen, breakSeen,
      wasLastWhenBranchStmtListEmpty;
    private EvaluateConstruct(final PTType p) {
      this.baseExpr = p;
    }
  }
}

