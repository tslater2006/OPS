/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import org.antlr.v4.runtime.*;
import org.openpplsoft.antlr4.frontend.*;
import org.openpplsoft.runtime.*;

public class NoErrorTolerancePeopleCodeLexer extends PeopleCodeLexer {

  public NoErrorTolerancePeopleCodeLexer(CharStream input) {
    super(input);
  }

  public void recover(LexerNoViableAltException e) {
    throw new EntVMachRuntimeException(e.getMessage(), e);
  }
}
