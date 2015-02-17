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

public class ProgramExecContext extends ExecContext {

  public ProgramExecContext(final PeopleCodeProg p,
      final int cBufferLevel, final int cBufferRow) {
    super(p);
    this.setExecutionScrollLevel(cBufferLevel);
    this.setExecutionRowIdx(cBufferRow);
  }

  /**
   * Program execution contexts do not involve
   * a function or method name, so return blank as
   * is shown in PS trace files.
   */
  @Override
  public String getMethodOrFuncName() {
    return "";
  }
}
