/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import org.openpplsoft.trace.PCInstruction;
import org.openpplsoft.antlr4.frontend.*;

import java.util.List;
import java.util.LinkedList;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InterpreterEmissionsFilter {

  private static Logger log = LogManager.getLogger(
      InterpreterEmissionsFilter.class.getName());

  private InterpretSupervisor parentSupervisor;
  private LinkedList<PCInstruction> inspectedInstrEmissions;
  private LinkedList<PCInstruction> pendingInstrEmissions;

  public InterpreterEmissionsFilter(final InterpretSupervisor s) {
    this.inspectedInstrEmissions = new LinkedList<PCInstruction>();
    this.pendingInstrEmissions = new LinkedList<PCInstruction>();
    this.parentSupervisor = s;
  }

  public CommonTokenStream getTokens() {
    return this.parentSupervisor.getCurrentlyExecutingTokenStream();
  }

  public void emit(final Token tok) {
    final StringBuilder b = new StringBuilder(tok.getText());
    b.append(this.getSemicolonsAfterTokenIdx(tok.getTokenIndex()+1));

    final PCInstruction instr = new PCInstruction(b.toString());
    instr.sourceToken = tok;
    this.pendingInstrEmissions.addLast(instr);
    this.flushPendingInstrEmissions();
  }

  public void emit(final ParserRuleContext ctx) {
    final StringBuffer line = new StringBuffer();
    final Interval interval = ctx.getSourceInterval();

    int i = interval.a;
    boolean newlineSeen = false;
    while (i <= interval.b) {
      final Token t = this.getTokens().get(i);
      if (t.getChannel() == PeopleCodeLexer.REFERENCES_CHANNEL) {
        i++;
        continue;
      }
      if (t.getText().contains("\n")) {
        newlineSeen = true;
        break;
      }
      line.append(t.getText());
      i++;
    }

    // Only look for semicolons if a newline wasn't seen.
    if (!newlineSeen) {
      line.append(this.getSemicolonsAfterTokenIdx(i));
    }

    final PCInstruction instr = new PCInstruction(line.toString());
    instr.sourceContext = ctx;
    this.pendingInstrEmissions.addLast(instr);
    this.flushPendingInstrEmissions();
  }

  private void flushPendingInstrEmissions() {

    // Start with the first pending instruction
    // emission and work to end of list; we cannot use an Iterator
    // here because removal of the current element is not possible when
    // lookahead is required
    while (this.pendingInstrEmissions.size() > 0) {
      final PCInstruction instr = this.pendingInstrEmissions.getFirst();
      final String i = instr.getInstruction();

      // If no instructions have been inspected yet,
      // emit this one without additional checking.
      if (this.inspectedInstrEmissions.size() == 0) {
        this.pendingInstrEmissions.removeFirst();
        TraceFileVerifier.submitEnforcedEmission(instr);
        this.inspectedInstrEmissions.addLast(instr);
        continue;
      }

      // Guaranteed to be non-null given immediately preceding check.
      final PCInstruction prev = this.inspectedInstrEmissions.getLast();

      /*====================================================*/
      /* BEGIN checks involving previous emission           */
      /*====================================================*/

      /*
       * At this point, the pending instruction emission is cleared
       * for submission to the trace file verifier.
       */
      this.pendingInstrEmissions.removeFirst();
      TraceFileVerifier.submitEnforcedEmission(instr);
      this.inspectedInstrEmissions.addLast(instr);
    }
  }

  private String getSemicolonsAfterTokenIdx(final int tokIdx) {
    int i = tokIdx;
    final StringBuilder b = new StringBuilder();
    //log.debug("Looking at {}", this.getTokens().get(i).getText());
    while (this.getTokens().get(i).getText().equals(";")) {
      b.append(";");
      i++;
    }
    return b.toString();
  }
}
