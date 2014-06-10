/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

import org.openpplsoft.runtime.*;

/**
 * This error strategy bails out upon encountering the
 * first syntax error; no recovery is attempted. Because the
 * PeopleCode being parsed originates from the database, rather
 * than a parsing request by the programmer, any syntax errors
 * are indicative of a problem with the PeopleCode grammar file
 * defined and used by the OPS runtime.
 */
public class OPSErrorStrategy extends DefaultErrorStrategy {

  /**
   * Default no-args constructor.
   */
  public OPSErrorStrategy() {}

  @Override
  public void recover(final Parser recognizer,
      final RecognitionException e) {
    throw new OPSVMachRuntimeException(e.getMessage(), e);
  }

  // Prevents recovery from inline errors.
  @Override
  public Token recoverInline(final Parser recognizer) {
    final InputMismatchException ime = new InputMismatchException(recognizer);
    throw new OPSVMachRuntimeException(ime.getMessage(), ime);
  }

  // Prevents recovery from errors in subrules.
  @Override
  public void sync(final Parser recognizer) { }
}
