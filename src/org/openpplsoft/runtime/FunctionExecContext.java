/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.types.PTTypeConstraint;

import org.antlr.v4.runtime.tree.*;

/**
 * Execution context details for calls to referenced
 * external functions via Declare statement.
 */
public class FunctionExecContext extends ExecContext {

  public String funcName;
  public ParseTree funcNodeToRun;
  public boolean hasTargetFuncBeenExecuted;

  public FunctionExecContext(final PeopleCodeProg prog, final String fnName) {
    super(prog);

    // REMEMBER: We deliberately used the function name in the
    // FuncImpl object rather than the one passed as argument here,
    // because PS doesn't distinguish b/w upper and lower case function
    // names, but the tracefile uses the one associated with the actual
    // function implementation, *not* the one used by the caller.
    PeopleCodeProg.FuncImpl fImpl = prog.getFunctionImpl(fnName);
    this.funcName = fImpl.funcName;
    this.funcNodeToRun = fImpl.parseTreeNode;
  }

  @Override
  public String getMethodOrFuncName() {
    return this.funcName;
  }

  public void markTargetFuncAsExecuted() {
    this.hasTargetFuncBeenExecuted = true;
  }

  public boolean hasTargetFuncBeenExecuted() {
    return this.hasTargetFuncBeenExecuted;
  }
}
