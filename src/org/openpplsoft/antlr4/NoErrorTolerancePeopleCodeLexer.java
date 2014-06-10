/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;

import org.openpplsoft.antlr4.frontend.*;
import org.openpplsoft.runtime.*;

/**
 * This lexer does not recover from lexer errors; it fails
 * irrecoverably instead.
 */
public class NoErrorTolerancePeopleCodeLexer extends PeopleCodeLexer {

  /**
   * @param input the CharStream to lex
   */
  public NoErrorTolerancePeopleCodeLexer(final CharStream input) {
    super(input);
  }

  @Override
  public void recover(final LexerNoViableAltException e) {
    throw new OPSVMachRuntimeException(e.getMessage(), e);
  }
}
