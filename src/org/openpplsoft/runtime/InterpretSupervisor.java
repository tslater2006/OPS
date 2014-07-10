/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.util.*;
import java.io.*;
import java.nio.charset.Charset;
import org.openpplsoft.runtime.*;
import org.openpplsoft.trace.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.types.*;
import org.apache.logging.log4j.*;
import org.openpplsoft.antlr4.*;

public class InterpretSupervisor {

  private LinkedList<ExecContext> execContextStack;

  private static Logger log = LogManager.getLogger(InterpretSupervisor.class.getName());

  public InterpretSupervisor(ExecContext e) {
    this.execContextStack = new LinkedList<ExecContext>();
    this.execContextStack.push(e);
  }

  public void run() {

    this.runTopOfStack();

    if(this.execContextStack.size() != 0) {
      throw new OPSVMachRuntimeException("Expected exec context stack to be empty.");
    }

    if(Environment.getCallStackSize() != 0) {
      throw new OPSVMachRuntimeException("Expected call stack to be empty.");
    }
  }

  private void runTopOfStack() {

    ExecContext context = execContextStack.peek();

    boolean doEmitProgramMarkers = true;

    /*
     * If another context is on the stack, it means that context was
     * interrupted by the call to run this one. If the programs being run in
     * both contexts are the same, PCStart/PCBegin/PCEnd markers should not
     * be emitted.
     */
    if(execContextStack.size() > 1) {
      // get the context that was interrupted by the call to run this context.
      ExecContext prevContext = execContextStack.get(1);
      if(prevContext.prog.getDescriptor().equals(context.prog.getDescriptor())) {
        doEmitProgramMarkers = false;
      }
    }

    context.prog.loadDefnsAndPrograms();

    /*
     * Requests to load app class declaration bodies do not
     * involve execution of PeopleCode instructions, and thus should
     * not trigger any trace file emissions.
     */
    if(context instanceof AppClassDeclExecContext) {
      InterpreterVisitor interpreter = new InterpreterVisitor(context, this);
      interpreter.visit(context.startNode);
      execContextStack.pop();
      return;
    }

    String methodOrFuncName = "";
    if(context instanceof ProgramExecContext &&
      ((ProgramExecContext)context).funcName != null) {
      methodOrFuncName = ((ProgramExecContext)context).funcName;
    }
    if(context instanceof AppClassObjExecContext &&
      ((AppClassObjExecContext)context).methodOrGetterName != null) {
      methodOrFuncName = ((AppClassObjExecContext)context).methodOrGetterName;
    }
    if(context instanceof FunctionExecContext) {
      methodOrFuncName = ((FunctionExecContext) context).funcName;
    }

    String descriptor = context.prog.getDescriptor();
    descriptor = descriptor.substring(descriptor.indexOf(".") + 1);

    if(doEmitProgramMarkers) {
      TraceFileVerifier.submitEnforcedEmission(new PCStart(
        (execContextStack.size() == 1 ? "start" : "start-ext"),
        String.format("%02d", execContextStack.size() - 1),
        methodOrFuncName, descriptor));
      TraceFileVerifier.submitEnforcedEmission(new PCBegin(descriptor, "0", "0"));
    }

    InterpreterVisitor interpreter = new InterpreterVisitor(context, this);
    boolean normalExit = true;
    try {
      interpreter.visit(context.startNode);
    } catch (OPSReturnException opsre) {
      normalExit = false;
    } catch (OPSFuncImplSignalException opsise) {
      log.debug("Caught OPSFuncImplSignalException; switching to target function now.");
      interpreter.visit(((FunctionExecContext) context).extFuncImplNode);
    }

    if(normalExit) {
      if(context instanceof ProgramExecContext &&
        context.scopeStack.size() > 0) {
        throw new OPSVMachRuntimeException("Expected all scope ptrs in the " +
          "ProgramExecContext to be popped.");
      }

      if(context instanceof AppClassObjExecContext &&
        context.scopeStack.size() > 2) {
        throw new OPSVMachRuntimeException("Expected all scope ptrs except " +
          "for the underlying object's prop and instance scopes to be popped.");
      }
    }

    if(doEmitProgramMarkers) {
      TraceFileVerifier.submitEnforcedEmission(new PCEnd(
        (execContextStack.size() == 1 ? "end" : "end-ext"),
        String.format("%02d", execContextStack.size() - 1),
        methodOrFuncName, descriptor));
    }

    execContextStack.pop();
  }

  public void runImmediately(ExecContext eCtx) {
    execContextStack.push(eCtx);
    this.runTopOfStack();
  }

  public ExecContext getThisExecContext() {
    return this.execContextStack.peek();
  }
}
