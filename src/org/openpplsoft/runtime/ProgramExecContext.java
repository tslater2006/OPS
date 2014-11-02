/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.runtime.*;

public class ProgramExecContext extends ExecContext {

  public String funcName = "";
  public ParseTree funcStartNode;

  public ProgramExecContext(PeopleCodeProg p,
      final int cBufferLevel, final int cBufferRow) {
    super(p);
    this.setExecutionScrollLevel(cBufferLevel);
    this.setExecutionRowIdx(cBufferRow);
  }

  @Override
  public String getMethodOrFuncName() {
    return this.funcName;
  }
}
