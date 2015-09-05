/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import org.openpplsoft.runtime.*;

/**
 * This listener does not tolerate ANTLR errors of any kind,
 * including ambiguous grammar alternatives (unless explicitly exempted
 * below). Every exception received
 * here is wrapped as an OPS runtime exception and bubbled up.
 */
public class OPSDiagErrorListener extends DiagnosticErrorListener {

  /**
   * Default, no-args constructor.
   */
  public OPSDiagErrorListener() {}

  @Override
  public void syntaxError(final Recognizer<?, ?> recognizer,
      final Object offendingSymbol, final int line,
      final int charPositionInLine, final String msg,
      final RecognitionException ex) {

    // If ANTLR reports that it is switching from SLL(*) to ALL(*)
    // parsing, that is not an error; no need to throw an exception here.
    if (msg.startsWith("reportAttemptingFullContext")) {
      return;
    }

    // If ANTLR reports that a full-context prediction returned a
    // unique result, that is not an error; no need to throw an
    // exception here.
    if (msg.startsWith("reportContextSensitivity")) {
      return;
    }

    /*
     * The PeopleCode grammar is ambiguous with regard to assignment and
     * comparison statements - both use '=' as the operator. Exceptions
     * related to this ambiguity can be ignored; I've ordered the rules such
     * that the correct action will be taken when ANTLR chooses the
     * first alternative.
     */
    if (msg.startsWith("reportAmbiguity d=4 (stmt): ambigAlts={20, 21}")
          || msg.startsWith("reportAmbiguity d=13 (expr): ambigAlts={1, 2}")
          || msg.startsWith("reportAmbiguity d=33 (funcSignature): "
              + "ambigAlts={1, 2}")
          || msg.startsWith("reportAmbiguity d=25 (classBlock): "
              + "ambigAlts={1, 2}")) {
      return;
    }

    final List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
    Collections.reverse(stack);
    final StringBuilder builder = new StringBuilder();
    builder.append("line ").append(line);
    builder.append(':').append(charPositionInLine);
    builder.append(" at ").append(offendingSymbol);
    builder.append(": ").append(msg);
    builder.append(". Rule stack: ").append(stack);
    throw new OPSVMachRuntimeException(builder.toString(), ex);
  }
}
