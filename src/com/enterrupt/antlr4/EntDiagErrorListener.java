package com.enterrupt.antlr4;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Nullable;
import java.util.*;
import java.lang.StringBuilder;
import com.enterrupt.runtime.*;

/**
 * This listener does not tolerate ANTLR errors of any kind,
 * including ambiguous grammar alternatives. Every exception received
 * here is wrapped as an Enterrupt exception and bubbled up.
 */
public class EntDiagErrorListener extends DiagnosticErrorListener {

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
							Object offendingSymbol,
							int line, int charPositionInLine,
							String msg,
							RecognitionException ex) {

		// If ANTLR reports that it is switching from SLL(*) to ALL(*) parsing,
		// that is not an error; no need to throw an exception here.
		if(msg.startsWith("reportAttemptingFullContext")) {
			return;
		}

		// If ANTLR reports that a full-context prediction returned a unique result,
		// that is not an error; no need to throw an exception here.
		if(msg.startsWith("reportContextSensitivity")) {
			return;
		}

		/**
		 * The PeopleCode grammar is ambiguous with regard to assignment and
		 * comparison statements - both use '=' as the operator. Exceptions
		 * related to this ambiguity can be ignored; I've ordered the rules such
		 * that the correct action will be taken when ANTLR chooses the first alternative.
		 */
		if(msg.startsWith("reportAmbiguity d=4 (stmt): ambigAlts={15, 16}")
			|| msg.startsWith("reportAmbiguity d=9 (expr): ambigAlts={1, 2}")
			|| msg.startsWith("reportAmbiguity d=18 (funcDeclaration): ambigAlts={1, 2}")) {
			return;
		}

		List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
		Collections.reverse(stack);
		StringBuilder builder = new StringBuilder();
		builder.append("line ").append(line);
		builder.append(":").append(charPositionInLine);
		builder.append(" at ").append(offendingSymbol);
		builder.append(": ").append(msg);
		builder.append(". Rule stack: ").append(stack);
		throw new EntVMachRuntimeException(builder.toString(), ex);
	}
}
