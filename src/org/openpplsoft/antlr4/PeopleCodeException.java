/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

/**
 * This exception is thrown by the interpreter when a PeopleCode
 * exception is thrown.
 */
public class PeopleCodeException extends RuntimeException {

  /**
   * Creates an instance of the exception.
   * @param instructionText the text of the return stmt in the
   *    PeopleCode program.
   */
  public PeopleCodeException() {
    super();
  }
}
