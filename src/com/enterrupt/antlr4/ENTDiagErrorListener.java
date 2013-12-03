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
public class ENTDiagErrorListener extends DiagnosticErrorListener {

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
							Object offendingSymbol,
							int line, int charPositionInLine,
							String msg,
							RecognitionException ex) {

		List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
		Collections.reverse(stack);
		StringBuilder builder = new StringBuilder();
		builder.append("line ").append(line);
		builder.append(":").append(charPositionInLine);
		builder.append(" at ").append(offendingSymbol);
		builder.append(": ").append( msg);
		builder.append(". Rule stack: ").append(stack);
		throw new EntVMachRuntimeException(builder.toString(), ex);
	}
}
