/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.antlr4.frontend.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.runtime.*;

/**
 * Responsible for listening to events during the lexing/parsing
 * of a supplied PeopleCode program and taking the appropriate
 * actions, which mainly consist of filling out various PT data
 * structure representations and loading other programs recursively.
 */
public class ProgLoadListener extends PeopleCodeBaseListener {

  private static Logger log =
      LogManager.getLogger(ProgLoadListener.class.getName());

  private final PeopleCodeProg srcProg;
  private final BufferedTokenStream tokens;

  private ParseTreeProperty<PeopleCodeProg> varTypeProgs =
      new ParseTreeProperty<PeopleCodeProg>();

  private boolean inExtFuncImport;
  private boolean inFuncImpl;
  private List<BytecodeReference> funcImplBytecodeRefs;
  private Set<String> funcImplInternalFuncRefs;
  private Set<String> funcImplExternalFuncRefs;

  /**
   * Attaches this listener to the specific program
   * that will be parsed.
   * @param p the program that will be parsed
   */
  public ProgLoadListener(final PeopleCodeProg p) {
    this.srcProg = p;
    this.tokens = p.getTokenStream();
  }

  private void setVarTypeProg(final ParseTree node,
      final PeopleCodeProg prog) {
    this.varTypeProgs.put(node, prog);
  }

  private PeopleCodeProg getVarTypeProg(final ParseTree node) {
    return this.varTypeProgs.get(node);
  }

  @Override
  public void exitProgram(final PeopleCodeParser.ProgramContext ctx) {
    /*
     * This program may not have any executable statements (i.e., it may
     * have a comment but that's it). When firing PC events,
     * programs that don't have at least one stmt in the stmtList of
     * the root program node in the parse tree will not be submitted to
     * the interpret supervisor.
     */
    this.srcProg.setHasAtLeastOneStatement(ctx.stmtList().stmt(0) != null);
  }

  /**
   * Save the class declaration start node for use in selectively parsing
   * identifiers and methods during app class object instantiation.
   * @param ctx the context node for the class declaration stmt
   */
  @Override
  public void enterClassDeclaration(
      final PeopleCodeParser.ClassDeclarationContext ctx) {
    ((AppClassPeopleCodeProg) this.srcProg).setClassDeclNode(ctx);
  }

  /**
   * When an app package/class is imported, load the root package's defn
   * and save the package path for use during potential class resolution later.
   * @param ctx the context node for the app class import stmt
   */
  @Override
  public void exitAppClassImport(
      final PeopleCodeParser.AppClassImportContext ctx) {
    if (ctx.appPkgPath() != null) {
      final String rootPkgName = ctx.appPkgPath().GENERIC_ID(0).getText();
      DefnCache.getAppPackage(rootPkgName);
      this.srcProg.addImportedAppPackagePath(
          new AppPackagePath(ctx.appPkgPath().getText()));
    } else {
      final String rootPkgName = ctx.appClassPath().GENERIC_ID(0).getText();
      DefnCache.getAppPackage(rootPkgName);
      final String appClassName = ctx.appClassPath().GENERIC_ID(
        ctx.appClassPath().GENERIC_ID().size() - 1).getText();
      this.srcProg.addPathToImportedClass(
          appClassName, new AppPackagePath(ctx.appClassPath().getText()));
    }
  }

  /**
   * If an instance statment in an App Class program references
   * another app class object, immediately load the program containing
   * that app class.
   * @param ctx the context node for the instance stmt
   */
  @Override
  public void exitInstance(final PeopleCodeParser.InstanceContext ctx) {
    if (this.getVarTypeProg(ctx.varType()) != null) {
      log.debug(">>> Instance: {}", ctx.getText());
      this.handlePropOrInstanceAppClassRef(this.getVarTypeProg(ctx.varType()));
    }
  }

  /**
   * If a property statment in an App Class program references
   * another app class object, immediately load the program
   * containing that app class.
   * @param ctx the context node for the property stmt
   */
  @Override
  public void exitProperty(final PeopleCodeParser.PropertyContext ctx) {
    if (this.getVarTypeProg(ctx.varType()) != null) {
      log.debug(">>> Property: {}", ctx.getText());
      this.handlePropOrInstanceAppClassRef(this.getVarTypeProg(ctx.varType()));
    }
  }

  /**
   * If a variable declaration is seen in a non-app class
   * program, note the reference and initialize the program.
   * @param ctx the context node for the declaration stmt
   */
  @Override
  public void exitVarDeclaration(
      final PeopleCodeParser.VarDeclarationContext ctx) {
    if (this.getVarTypeProg(ctx.varType()) != null) {
      PeopleCodeProg prog = this.getVarTypeProg(ctx.varType());
      prog = DefnCache.getProgram(prog);
      this.srcProg.addReferencedProg(prog);
    }
  }

  /**
   * When a variable type in a variable declaration in a non-app
   * class program references an object in an app class, we need to
   * parse out the app class reference and load that program's
   * OnExecute PeopleCode segment.
   * @param ctx the context node for the variable type segment
   *    of the variable declaration stmt
   */
  @Override
  public void exitVarType(final PeopleCodeParser.VarTypeContext ctx) {

    if (ctx.appClassPath() != null) {
      List<String> appClassParts = new ArrayList<String>();
      for (TerminalNode id : ctx.appClassPath().GENERIC_ID()) {
        appClassParts.add(id.getText());
      }
      PeopleCodeProg prog = new AppClassPeopleCodeProg(appClassParts.toArray(
          new String[appClassParts.size()]));
      this.setVarTypeProg(ctx, prog);
      //log.debug("(0) Path found: {} in {}", appClassParts, ctx.getText());
    } else if (ctx.GENERIC_ID() != null) {

      /*
       * The GENERIC_ID could be a primitive or complex var type; if it is,
       * don't consider it to be an app class (technically "date", "Record",
       * etc. are allowable app class names, keep this in mind if issues arise).
       */
      if (!PSDefn.VAR_TYPES_TABLE.contains(ctx.GENERIC_ID().getText())) {
        PeopleCodeProg prog = this.srcProg.resolveAppClassToProg(
            ctx.GENERIC_ID().getText());
        this.setVarTypeProg(ctx, prog);
        //log.debug("(1) Class name resolved: {} in {}",
        //  appClassParts, ctx.getText());
      }
    } else if (ctx.varType() != null) {
      // if the nested variable type has a program attached to it,
      // bubble it up before exiting.
      PeopleCodeProg prog = this.getVarTypeProg(ctx.varType());
      if (prog != null) {
        this.setVarTypeProg(ctx, prog);
      }
    }
  }

  @Override
  public void enterExtFuncImport(
      final PeopleCodeParser.ExtFuncImportContext ctx) {
    this.inExtFuncImport = true;
  }

  /**
   * Detect references to externally-defined functions. We make
   * note of the referenced program at this time; later, once
   * we've counted any and all function calls to this and other
   * functions defined in the referenced program, that program will
   * itself be loaded, provided that the number of references is > 0.
   * @param ctx the context node for the function import stmt
   */
  @Override
  public void exitExtFuncImport(
      final PeopleCodeParser.ExtFuncImportContext ctx) {

    if (this.srcProg instanceof AppClassPeopleCodeProg) {
      return;
    }

    final String fnName = ctx.GENERIC_ID().getText();
    final String recname = ctx.recDefnPath().GENERIC_ID(0).getText();
    final String fldname = ctx.recDefnPath().GENERIC_ID(1).getText();

    PeopleCodeProg prog = new RecordPeopleCodeProg(recname, fldname,
        ctx.event().getText());
    prog = DefnCache.getProgram(prog);

    this.srcProg.addReferencedProg(prog);
    this.srcProg.addRecordProgFnImport(fnName, (RecordPeopleCodeProg) prog);

    // Load the prog's initial metadata if it hasn't already been cached.
    prog.init();

    this.inExtFuncImport = false;
  }

  /**
   * Save function entry points in the parse tree for lookup during
   * interpretation later.
   * @param ctx the context node for the function declaration stmt
   */
  @Override
  public void enterFuncImpl(
      final PeopleCodeParser.FuncImplContext ctx) {

    log.debug("Saving parse tree node for Function *{}* in "
        + "program {}", ctx.funcSignature().GENERIC_ID().getText(),
        this.srcProg.getDescriptor());

    this.srcProg.registerFunctionImpl(
        ctx.funcSignature().GENERIC_ID().getText(), ctx);

    this.inFuncImpl = true;
    this.funcImplBytecodeRefs = new ArrayList<BytecodeReference>();
    this.funcImplInternalFuncRefs = new HashSet<String>();
    this.funcImplExternalFuncRefs = new HashSet<String>();
  }

  @Override
  public void exitFuncImpl(
      final PeopleCodeParser.FuncImplContext ctx) {

    log.debug("Saving func impl bytecode refs for Function *{}* in "
        + "program {}; refs are: ", ctx.funcSignature().GENERIC_ID().getText(),
        this.srcProg, this.funcImplBytecodeRefs);
    final PeopleCodeProg.FuncImpl fnImpl = this.srcProg
        .getFunctionImpl(ctx.funcSignature().GENERIC_ID().getText());
    fnImpl.setBytecodeReferences(this.funcImplBytecodeRefs);
    fnImpl.setInternalFuncReferences(this.funcImplInternalFuncRefs);
    fnImpl.setExternalFuncReferences(this.funcImplExternalFuncRefs);

    this.inFuncImpl = false;
    this.funcImplBytecodeRefs = null;
    this.funcImplInternalFuncRefs = null;
    this.funcImplExternalFuncRefs = null;
  }

  /**
   * Detect create stmts, which reference app classes. Upon
   * encountering one, we need to load the app class OnExecute
   * program corresponding to the class instance being created.
   * @param ctx the context node for the create statement
   */
  @Override
  public void exitCreateInvocation(
      final PeopleCodeParser.CreateInvocationContext ctx) {

    if (this.srcProg instanceof AppClassPeopleCodeProg) {
      return;
    }

    PeopleCodeProg prog = null;
    if (ctx.appClassPath() != null) {
      /*
       * This invocation of 'create' includes the full path to the
       * app class object being instantiated.
       */
      final List<String> appClassParts = new ArrayList<String>();
      for (TerminalNode id : ctx.appClassPath().GENERIC_ID()) {
        appClassParts.add(id.getText());
      }
      prog = new AppClassPeopleCodeProg(
          appClassParts.toArray(new String[appClassParts.size()]));

    } else {
      /*
       * This invocation of 'create' is creating an instance of
       * an app class but we only have the app class name.
       */
      prog = this.srcProg.resolveAppClassToProg(
          ctx.GENERIC_ID().getText());
    }

    prog = DefnCache.getProgram(prog);
    this.srcProg.addReferencedProg(prog);
  }

  /**
   * Save method entry points in the parse tree for lookup during
   * interpretation later.
   * @param ctx the context node for the method impl statement
   */
  @Override
  public void enterMethodImpl(
      final PeopleCodeParser.MethodImplContext ctx) {
    ((AppClassPeopleCodeProg) this.srcProg)
        .saveMethodImplStartNode(ctx.GENERIC_ID().getText(), ctx);
  }

  /**
   * Save property getter entry points in the parse tree for lookup during
   * interpretation later.
   * @param ctx the context node for the property getter impl stmt
   */
  @Override
  public void enterGetImpl(final PeopleCodeParser.GetImplContext ctx) {
    ((AppClassPeopleCodeProg) this.srcProg)
        .savePropGetterImplStartNode(ctx.GENERIC_ID().getText(), ctx);
  }

  /**
   * Save property setter entry points in the parse tree for lookup during
   * interpretation later.
   * @param ctx the context node for the property setter impl stmt
   */
  @Override
  public void enterSetImpl(final PeopleCodeParser.SetImplContext ctx) {
    ((AppClassPeopleCodeProg) this.srcProg)
        .savePropSetterImplStartNode(ctx.GENERIC_ID().getText(), ctx);
  }

  /**
   * Detect function calls; if the function is external
   * (imported via "Declare" statement), mark it as used in program.
   * @param ctx the context node for the function or index call stmt
   */
  @Override
  public void enterExprFnOrIdxCall(
      final PeopleCodeParser.ExprFnOrIdxCallContext ctx) {

    if (this.srcProg instanceof AppClassPeopleCodeProg) {
      return;
    }

    if (ctx.expr() instanceof PeopleCodeParser.ExprIdContext) {
      final String exprId =
          ((PeopleCodeParser.ExprIdContext) ctx.expr()).id().getText();

      /*
       * If this is a function call within a function impl,
       * determine whether the called function is defined internally
       * within this program or on an internal program, then annotate
       * the current func impl accordingly.
       */
      if (this.inFuncImpl) {
        if (this.srcProg.hasFunctionImplNamed(exprId)) {
          this.funcImplInternalFuncRefs.add(exprId);
        } else if (this.srcProg.hasRecordProgFnImport(exprId)) {
          this.funcImplExternalFuncRefs.add(exprId);
        }
      }

      /*
       * If this is a function call to a function defined on an
       * external program, mark it as being used in the program.
       */
      if (!this.srcProg.hasFunctionImplNamed(exprId)
          && this.srcProg.hasRecordProgFnImport(exprId)) {
        this.srcProg.getRecordProgFnImport(exprId).markAsUsedInProgram();
      }
    }
  }

  /**
   * @param ctx the context node for the parser rule that is about
   *    to be entered
   */
  @Override
  public void enterEveryRule(final ParserRuleContext ctx) {

    final int tokPos = ctx.getStart().getTokenIndex();
    final List<Token> refChannel = this.tokens.getHiddenTokensToLeft(tokPos,
        PeopleCodeLexer.REFERENCES_CHANNEL);

    if (refChannel != null) {
      final Token refTok = refChannel.get(0);
      if (refTok != null) {
        final String text = refTok.getText();
        final int refIdx = Integer.parseInt(
            text.substring(8, text.length() - 1));

        final BytecodeReference ref =
            this.srcProg.getBytecodeReference(refIdx);

        if (!this.inExtFuncImport) {
          if (srcProg.getEvent().equals("FieldFormula") && this.inFuncImpl) {
            // Do not mark as root reference.
          } else {
            ref.markAsRootReference();
          }
        }

        // If we're currently traversing a function impl, add this
        // bytecode reference to that function impl's metadata.
        if (this.inFuncImpl) {
          this.funcImplBytecodeRefs.add(ref);
        }
      }
    }
  }

  /*======================================================================*/
  /* shared functions                                                     */
  /*======================================================================*/

  /**
   * This method should be called to immediately load programs
   * referenced in an App Class program within a property
   * or instance statement.
   * @param prog a program referenced by the program being lexed/parsed
   *    by this instance of ProgLoadListener
   */
  private void handlePropOrInstanceAppClassRef(final PeopleCodeProg prog) {
    final PeopleCodeProg p = DefnCache.getProgram(prog);
    this.srcProg.addReferencedProg(p);
  }
}
