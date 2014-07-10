/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * Execution context details for calls to referenced
 * external functions via Declare statement.
 */
public class FunctionExecContext extends ExecContext {

  public String funcName;
  public ParseTree extFuncImplNode;

  public FunctionExecContext(PeopleCodeProg prog, String fnName) {
    super(prog);
    this.funcName = fnName;

    this.extFuncImplNode = prog.funcImplNodes.get(fnName);
  }
}
