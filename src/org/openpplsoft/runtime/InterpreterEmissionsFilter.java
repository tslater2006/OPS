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

      // Don't emit an End-If after emitting a Break.
      if(i.startsWith("End-If")
          && prev.getInstruction().startsWith("Break")) {
        this.pendingInstrEmissions.removeFirst();
        this.inspectedInstrEmissions.addLast(instr);
        continue;
      }

      // Don't emit an Else after emitting an End-If.
      if(i.startsWith("Else")
          && prev.getInstruction().startsWith("End-If")) {
        this.pendingInstrEmissions.removeFirst();
        this.inspectedInstrEmissions.addLast(instr);
        continue;
      }

      /*
       * The tracefile frequently excludes end-of-control-construct
       * instructions that appear in quick succession; usually only the first
       * and last such statments are listed in the tracefile. The conditional
       * below implements that logic.
       */
      if ((i.startsWith("End-If") || i.startsWith("End-Evaluate"))
            && (prev.getInstruction().startsWith("For")
              || prev.getInstruction().startsWith("End-If")
              || prev.getInstruction().startsWith("Else")
              || prev.getInstruction().startsWith("If")
              || prev.getInstruction().startsWith("End-Function")
              || prev.getInstruction().startsWith("End-Evaluate"))) {
        this.pendingInstrEmissions.removeFirst();
        this.inspectedInstrEmissions.addLast(instr);
        continue;
      }

      // Don't emit End-Evaluate statements directly after a When
      // branch emission.
      if (i.startsWith("End-Evaluate")
          && prev.getInstruction().startsWith("When")) {
        this.pendingInstrEmissions.removeFirst();
        this.inspectedInstrEmissions.addLast(instr);
        continue;
      }

      // Don't emit this instruction emission if the previously
      // inspected emission (*regardless of whether it was actually
      // submitted to the trace file verifier or not*) lacked a
      // trailing semicolon and was an assignment stmt.
      if ((prev.sourceContext instanceof PeopleCodeParser.StmtAssignContext)
          && !prev.getInstruction().endsWith(";")) {
        this.pendingInstrEmissions.removeFirst();
        this.inspectedInstrEmissions.addLast(instr);
        continue;
      }

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
