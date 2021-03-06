/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.antlr4.InterpreterVisitor;
import org.openpplsoft.antlr4.OPSFuncImplSignalException;
import org.openpplsoft.antlr4.OPSReturnException;
import org.openpplsoft.trace.PCBegin;
import org.openpplsoft.trace.PCEnd;
import org.openpplsoft.trace.PCStart;
import org.openpplsoft.types.*;

public class InterpretSupervisor {

  private LinkedList<ExecContext> execContextStack = new LinkedList<ExecContext>();
  private ICBufferEntity cBufferContextEntity;

  private static Logger log = LogManager.getLogger(InterpretSupervisor.class.getName());

  public InterpretSupervisor(final ExecContext e) {
    this.execContextStack.push(e);
  }

  public InterpretSupervisor(final ExecContext e,
      final ICBufferEntity contextEntity) {
    this.execContextStack.push(e);
    this.cBufferContextEntity = contextEntity;
  }

  public PTBufferRecord resolveContextualCBufferRecordReference(final String recName) {
    if (this.cBufferContextEntity != null) {
      return this.cBufferContextEntity
          .resolveContextualCBufferRecordReference(recName);
    }
    return null;
  }

  public PTReference<PTBufferField> resolveContextualCBufferRecordFieldReference(
      final String recName, final String fieldName) {
    if (this.cBufferContextEntity != null) {
      return this.cBufferContextEntity
          .resolveContextualCBufferRecordFieldReference(recName, fieldName);
    }
    return null;
  }

  public PTBufferRowset resolveContextualCBufferScrollReference(
      final PTScrollLiteral scrollName) {
    if (this.cBufferContextEntity != null) {
      return this.cBufferContextEntity
          .resolveContextualCBufferScrollReference(scrollName);
    }
    return null;
  }

  public PTBufferRecord getNearestCBufferRecordInContext() {
    if (this.cBufferContextEntity instanceof PTRecord) {
      return (PTBufferRecord) this.cBufferContextEntity;
    } else if (this.cBufferContextEntity instanceof PTField) {
      return ((PTBufferField) this.cBufferContextEntity).getParentRecord();
    } else {
      throw new OPSVMachRuntimeException("Can't find nearest record in component "
          + "buffer context; expected context entity to be Record or Field but "
          + "is actually: " + this.cBufferContextEntity);
    }
  }

  public void run() {

    try {
      this.runTopOfStack();
    } catch (final OPSVMachRuntimeException opsvmre) {
      this.fatallyLogContextStack();
      throw opsvmre;
    }

    if (TraceFileVerifier.isVerifierPaused()) {
      throw new OPSVMachRuntimeException("Expected TraceFileVerifier to be synced up, "
          + "but it is in the paused state.");
    }

    if(this.execContextStack.size() != 0) {
      throw new OPSVMachRuntimeException("Expected exec context stack to be empty.");
    }

    if(Environment.getCallStackSize() != 0) {
      throw new OPSVMachRuntimeException("Expected call stack to be empty.");
    }
  }

  private void runTopOfStack() {

    ExecContext context = execContextStack.peek();

    boolean isThisProgSameAsInterruptedProg = false;

    /*
     * If another context is on the stack, it means that context was
     * interrupted by the call to run this one. If the programs being run in
     * both contexts are the same, PCStart/PCBegin/PCEnd markers should not
     * be emitted.
     */
    if(execContextStack.size() > 1) {
      // get the context that was interrupted by the call to run this context.
      ExecContext prevContext = execContextStack.get(1);
      if(prevContext.getProg().getDescriptor()
          .equals(context.getProg().getDescriptor())) {
        isThisProgSameAsInterruptedProg = true;
      }
    }

    context.getProg().loadDefnsAndPrograms();

    /*
     * Requests to load app class declaration bodies do not
     * involve execution of PeopleCode instructions, and thus should
     * not trigger any trace file emissions.
     */
    if(context instanceof AppClassDeclExecContext) {
      InterpreterVisitor interpreter = new InterpreterVisitor(context, this);
      interpreter.visit(context.getStartNode());
      execContextStack.pop();
      return;
    }

    final String methodOrFuncName = context.getMethodOrFuncName();
    String descriptor = context.getProg().getDescriptor();
    descriptor = descriptor.substring(descriptor.indexOf(".") + 1);

    if(!isThisProgSameAsInterruptedProg) {
      TraceFileVerifier.submitEnforcedEmission(new PCStart(
          (execContextStack.size() == 1 ? "start" : "start-ext"),
              String.format("%02d", execContextStack.size() - 1),
                  methodOrFuncName, descriptor));

      // Internally (in OPS) the search record has a scroll level of -1;
      // for trace file purposes though, search records are on level 0.
      int execScrollLevel = Math.max(0, context.getExecutionScrollLevel());

      TraceFileVerifier.submitEnforcedEmission(new PCBegin(descriptor,
          execScrollLevel, context.getExecutionRowIdx()));
    }

    InterpreterVisitor interpreter = new InterpreterVisitor(context, this);
    boolean normalExit = true;
    try {
      interpreter.visit(context.getStartNode());
    } catch (final OPSReturnException opsre) {
      normalExit = false;
    } catch (final OPSFuncImplSignalException opsise) {
      /*
       * Only execute the target func if it hasn't already. If the first
       * function in a program is the target func, it will already have
       * been executed w/o having thrown an exception, so must check
       * to prevent executing it again.
       */
      if (!((FunctionExecContext) context).hasTargetFuncBeenExecuted()) {
        log.debug("Caught OPSFuncImplSignalException and target func has "
            + "yet been executed; switching to target function now.");
        interpreter.visit(((FunctionExecContext) context).getFuncNodeToRun());
      }
    }

    if(normalExit) {
      if(context instanceof ProgramExecContext &&
        context.getScopeStack().size() > 0) {
        throw new OPSVMachRuntimeException("Expected all scope ptrs in the " +
          "ProgramExecContext to be popped.");
      }

      if(context instanceof AppClassObjExecContext &&
        context.getScopeStack().size() > 2) {
        throw new OPSVMachRuntimeException("Expected all scope ptrs except " +
          "for the underlying object's prop and instance scopes to be popped.");
      }
    }

    if(!isThisProgSameAsInterruptedProg) {
      TraceFileVerifier.submitEnforcedEmission(new PCEnd(
        (execContextStack.size() == 1 ? "end" : "end-ext"),
        String.format("%02d", execContextStack.size() - 1),
        methodOrFuncName, descriptor));
    }

    execContextStack.pop();
  }

  public void runImmediately(ExecContext eCtx) {
    // Full program exec contexts require the component scroll
    // level and row idx of the context in which they're executing
    // at instantiation. For all other contexts, use the context
    // scroll and row from the previously running program.
    if (!(eCtx instanceof ProgramExecContext)) {
      eCtx.setExecutionScrollLevel(
          execContextStack.peek().getExecutionScrollLevel());
      eCtx.setExecutionRowIdx(
          execContextStack.peek().getExecutionRowIdx());
    }
    execContextStack.push(eCtx);
    this.runTopOfStack();
  }

  public ExecContext getThisExecContext() {
    return this.execContextStack.peek();
  }

  private void fatallyLogContextStack() {
    String indent = "";
    for (int i = this.execContextStack.size() - 1; i >= 0; i--) {
      ExecContext e = this.execContextStack.get(i);
      if (e.getMethodOrFuncName().length() > 0) {
        log.fatal("!!! PC Call Stack !!! {} |-> {} [Method/Func: {}]",
            indent, e.getProg(), e.getMethodOrFuncName());
      } else {
        log.fatal("!!! PC Call Stack !!! {} |-> {}", indent, e.getProg());
      }
      indent = indent.concat("    ");
    }
  }
}

