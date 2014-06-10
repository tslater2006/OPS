/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import org.antlr.v4.runtime.*;
import org.openpplsoft.runtime.*;

/*
 * This error strategy bails out upon encountering the
 * first syntax error; no recovery is attempted. Because the
 * PeopleCode being parsed originates from the database, rather
 * than a parsing request by the programmer, any syntax errors
 * are indicative of a problem with the PeopleCode grammar within
 * Enterrupt.
 */
public class EntErrorStrategy extends DefaultErrorStrategy {

  @Override
  public void recover(Parser recognizer, RecognitionException e) {
    throw new EntVMachRuntimeException(e.getMessage(), e);
  }

  // Prevents recovery from inline errors.
  @Override
  public Token recoverInline(Parser recognizer) throws RecognitionException {
    InputMismatchException ime = new InputMismatchException(recognizer);
    throw new EntVMachRuntimeException(ime.getMessage(), ime);
  }

  // Prevents recovery from errors in subrules.
  @Override
  public void sync(Parser recognizer) { }
}
