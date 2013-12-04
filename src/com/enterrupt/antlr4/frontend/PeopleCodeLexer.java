// Generated from /home/mquinn/evm/grammars/PeopleCode.g4 by ANTLR 4.1
package com.enterrupt.antlr4.frontend;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PeopleCodeLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__8=1, T__7=2, T__6=3, T__5=4, T__4=5, T__3=6, T__2=7, T__1=8, T__0=9, 
		ReservedDefnWord=10, DecimalLiteral=11, IntegerLiteral=12, BooleanLiteral=13, 
		OBJECT_ID=14, FUNC_ID=15, SYSTEM_VAR=16, COMMENT=17, WS=18;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'If'", "')'", "'.'", "','", "'('", "'='", "';'", "'End-If'", "'Then'", 
		"'MenuName'", "DecimalLiteral", "IntegerLiteral", "BooleanLiteral", "OBJECT_ID", 
		"FUNC_ID", "SYSTEM_VAR", "COMMENT", "WS"
	};
	public static final String[] ruleNames = {
		"T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", 
		"ReservedDefnWord", "DecimalLiteral", "IntegerLiteral", "BooleanLiteral", 
		"OBJECT_ID", "FUNC_ID", "SYSTEM_VAR", "COMMENT", "WS"
	};


	public PeopleCodeLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PeopleCode.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 16: COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 17: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1: skip();  break;
		}
	}
	private void COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\24\u0097\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3"+
		"\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\6\fO\n\f\r\f\16\fP\3\r\3\r"+
		"\3\r\7\rV\n\r\f\r\16\rY\13\r\5\r[\n\r\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16o\n\16"+
		"\3\17\3\17\7\17s\n\17\f\17\16\17v\13\17\3\20\6\20y\n\20\r\20\16\20z\3"+
		"\21\3\21\6\21\177\n\21\r\21\16\21\u0080\3\22\3\22\3\22\3\22\7\22\u0087"+
		"\n\22\f\22\16\22\u008a\13\22\3\22\3\22\3\22\3\22\3\22\3\23\6\23\u0092"+
		"\n\23\r\23\16\23\u0093\3\23\3\23\3\u0088\24\3\3\1\5\4\1\7\5\1\t\6\1\13"+
		"\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1"+
		"\37\21\1!\22\1#\23\2%\24\3\3\2\7\3\2\62;\3\2C\\\4\2C\\aa\4\2C\\c|\5\2"+
		"\13\f\17\17\"\"\u00a1\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2"+
		"\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\3\'\3\2\2\2\5*\3\2\2\2\7,\3\2\2"+
		"\2\t.\3\2\2\2\13\60\3\2\2\2\r\62\3\2\2\2\17\64\3\2\2\2\21\66\3\2\2\2\23"+
		"=\3\2\2\2\25B\3\2\2\2\27K\3\2\2\2\31Z\3\2\2\2\33n\3\2\2\2\35p\3\2\2\2"+
		"\37x\3\2\2\2!|\3\2\2\2#\u0082\3\2\2\2%\u0091\3\2\2\2\'(\7K\2\2()\7h\2"+
		"\2)\4\3\2\2\2*+\7+\2\2+\6\3\2\2\2,-\7\60\2\2-\b\3\2\2\2./\7.\2\2/\n\3"+
		"\2\2\2\60\61\7*\2\2\61\f\3\2\2\2\62\63\7?\2\2\63\16\3\2\2\2\64\65\7=\2"+
		"\2\65\20\3\2\2\2\66\67\7G\2\2\678\7p\2\289\7f\2\29:\7/\2\2:;\7K\2\2;<"+
		"\7h\2\2<\22\3\2\2\2=>\7V\2\2>?\7j\2\2?@\7g\2\2@A\7p\2\2A\24\3\2\2\2BC"+
		"\7O\2\2CD\7g\2\2DE\7p\2\2EF\7w\2\2FG\7P\2\2GH\7c\2\2HI\7o\2\2IJ\7g\2\2"+
		"J\26\3\2\2\2KL\5\31\r\2LN\7\60\2\2MO\t\2\2\2NM\3\2\2\2OP\3\2\2\2PN\3\2"+
		"\2\2PQ\3\2\2\2Q\30\3\2\2\2R[\7\62\2\2SW\4\63;\2TV\4\62;\2UT\3\2\2\2VY"+
		"\3\2\2\2WU\3\2\2\2WX\3\2\2\2X[\3\2\2\2YW\3\2\2\2ZR\3\2\2\2ZS\3\2\2\2["+
		"\32\3\2\2\2\\]\7V\2\2]^\7t\2\2^_\7w\2\2_o\7g\2\2`a\7v\2\2ab\7t\2\2bc\7"+
		"w\2\2co\7g\2\2de\7H\2\2ef\7c\2\2fg\7n\2\2gh\7u\2\2ho\7g\2\2ij\7h\2\2j"+
		"k\7c\2\2kl\7n\2\2lm\7u\2\2mo\7g\2\2n\\\3\2\2\2n`\3\2\2\2nd\3\2\2\2ni\3"+
		"\2\2\2o\34\3\2\2\2pt\t\3\2\2qs\t\4\2\2rq\3\2\2\2sv\3\2\2\2tr\3\2\2\2t"+
		"u\3\2\2\2u\36\3\2\2\2vt\3\2\2\2wy\t\5\2\2xw\3\2\2\2yz\3\2\2\2zx\3\2\2"+
		"\2z{\3\2\2\2{ \3\2\2\2|~\7\'\2\2}\177\t\5\2\2~}\3\2\2\2\177\u0080\3\2"+
		"\2\2\u0080~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\"\3\2\2\2\u0082\u0083\7"+
		"\61\2\2\u0083\u0084\7,\2\2\u0084\u0088\3\2\2\2\u0085\u0087\13\2\2\2\u0086"+
		"\u0085\3\2\2\2\u0087\u008a\3\2\2\2\u0088\u0089\3\2\2\2\u0088\u0086\3\2"+
		"\2\2\u0089\u008b\3\2\2\2\u008a\u0088\3\2\2\2\u008b\u008c\7,\2\2\u008c"+
		"\u008d\7\61\2\2\u008d\u008e\3\2\2\2\u008e\u008f\b\22\2\2\u008f$\3\2\2"+
		"\2\u0090\u0092\t\6\2\2\u0091\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0091"+
		"\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0096\b\23\3\2"+
		"\u0096&\3\2\2\2\f\2PWZntz\u0080\u0088\u0093";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}