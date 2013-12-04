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
		T__17=1, T__16=2, T__15=3, T__14=4, T__13=5, T__12=6, T__11=7, T__10=8, 
		T__9=9, T__8=10, T__7=11, T__6=12, T__5=13, T__4=14, T__3=15, T__2=16, 
		T__1=17, T__0=18, DecimalLiteral=19, IntegerLiteral=20, BooleanLiteral=21, 
		VAR_ID=22, OBJECT_ID=23, FUNC_ID=24, SYSTEM_VAR=25, COMMENT=26, WS=27;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'Component'", "'If'", "'Global'", "'End-Evaluate'", "')'", "'.'", "','", 
		"'Record'", "'('", "'='", "';'", "'End-If'", "'Evaluate'", "'Local'", 
		"'string'", "'When'", "'MenuName'", "'Then'", "DecimalLiteral", "IntegerLiteral", 
		"BooleanLiteral", "VAR_ID", "OBJECT_ID", "FUNC_ID", "SYSTEM_VAR", "COMMENT", 
		"WS"
	};
	public static final String[] ruleNames = {
		"T__17", "T__16", "T__15", "T__14", "T__13", "T__12", "T__11", "T__10", 
		"T__9", "T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", 
		"T__0", "DecimalLiteral", "IntegerLiteral", "BooleanLiteral", "VAR_ID", 
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
		case 25: COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 26: WS_action((RuleContext)_localctx, actionIndex); break;
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\35\u00ef\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24"+
		"\3\24\6\24\u00a1\n\24\r\24\16\24\u00a2\3\25\3\25\3\25\7\25\u00a8\n\25"+
		"\f\25\16\25\u00ab\13\25\5\25\u00ad\n\25\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u00c1"+
		"\n\26\3\27\3\27\6\27\u00c5\n\27\r\27\16\27\u00c6\3\30\3\30\7\30\u00cb"+
		"\n\30\f\30\16\30\u00ce\13\30\3\31\6\31\u00d1\n\31\r\31\16\31\u00d2\3\32"+
		"\3\32\6\32\u00d7\n\32\r\32\16\32\u00d8\3\33\3\33\3\33\3\33\7\33\u00df"+
		"\n\33\f\33\16\33\u00e2\13\33\3\33\3\33\3\33\3\33\3\33\3\34\6\34\u00ea"+
		"\n\34\r\34\16\34\u00eb\3\34\3\34\3\u00e0\35\3\3\1\5\4\1\7\5\1\t\6\1\13"+
		"\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1"+
		"\37\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1/\31\1\61\32\1\63\33"+
		"\1\65\34\2\67\35\3\3\2\b\3\2\62;\5\2C\\aac|\3\2C\\\4\2C\\aa\4\2C\\c|\5"+
		"\2\13\f\17\17\"\"\u00fa\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2"+
		"\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2"+
		"\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3"+
		"\2\2\2\39\3\2\2\2\5C\3\2\2\2\7F\3\2\2\2\tM\3\2\2\2\13Z\3\2\2\2\r\\\3\2"+
		"\2\2\17^\3\2\2\2\21`\3\2\2\2\23g\3\2\2\2\25i\3\2\2\2\27k\3\2\2\2\31m\3"+
		"\2\2\2\33t\3\2\2\2\35}\3\2\2\2\37\u0083\3\2\2\2!\u008a\3\2\2\2#\u008f"+
		"\3\2\2\2%\u0098\3\2\2\2\'\u009d\3\2\2\2)\u00ac\3\2\2\2+\u00c0\3\2\2\2"+
		"-\u00c2\3\2\2\2/\u00c8\3\2\2\2\61\u00d0\3\2\2\2\63\u00d4\3\2\2\2\65\u00da"+
		"\3\2\2\2\67\u00e9\3\2\2\29:\7E\2\2:;\7q\2\2;<\7o\2\2<=\7r\2\2=>\7q\2\2"+
		">?\7p\2\2?@\7g\2\2@A\7p\2\2AB\7v\2\2B\4\3\2\2\2CD\7K\2\2DE\7h\2\2E\6\3"+
		"\2\2\2FG\7I\2\2GH\7n\2\2HI\7q\2\2IJ\7d\2\2JK\7c\2\2KL\7n\2\2L\b\3\2\2"+
		"\2MN\7G\2\2NO\7p\2\2OP\7f\2\2PQ\7/\2\2QR\7G\2\2RS\7x\2\2ST\7c\2\2TU\7"+
		"n\2\2UV\7w\2\2VW\7c\2\2WX\7v\2\2XY\7g\2\2Y\n\3\2\2\2Z[\7+\2\2[\f\3\2\2"+
		"\2\\]\7\60\2\2]\16\3\2\2\2^_\7.\2\2_\20\3\2\2\2`a\7T\2\2ab\7g\2\2bc\7"+
		"e\2\2cd\7q\2\2de\7t\2\2ef\7f\2\2f\22\3\2\2\2gh\7*\2\2h\24\3\2\2\2ij\7"+
		"?\2\2j\26\3\2\2\2kl\7=\2\2l\30\3\2\2\2mn\7G\2\2no\7p\2\2op\7f\2\2pq\7"+
		"/\2\2qr\7K\2\2rs\7h\2\2s\32\3\2\2\2tu\7G\2\2uv\7x\2\2vw\7c\2\2wx\7n\2"+
		"\2xy\7w\2\2yz\7c\2\2z{\7v\2\2{|\7g\2\2|\34\3\2\2\2}~\7N\2\2~\177\7q\2"+
		"\2\177\u0080\7e\2\2\u0080\u0081\7c\2\2\u0081\u0082\7n\2\2\u0082\36\3\2"+
		"\2\2\u0083\u0084\7u\2\2\u0084\u0085\7v\2\2\u0085\u0086\7t\2\2\u0086\u0087"+
		"\7k\2\2\u0087\u0088\7p\2\2\u0088\u0089\7i\2\2\u0089 \3\2\2\2\u008a\u008b"+
		"\7Y\2\2\u008b\u008c\7j\2\2\u008c\u008d\7g\2\2\u008d\u008e\7p\2\2\u008e"+
		"\"\3\2\2\2\u008f\u0090\7O\2\2\u0090\u0091\7g\2\2\u0091\u0092\7p\2\2\u0092"+
		"\u0093\7w\2\2\u0093\u0094\7P\2\2\u0094\u0095\7c\2\2\u0095\u0096\7o\2\2"+
		"\u0096\u0097\7g\2\2\u0097$\3\2\2\2\u0098\u0099\7V\2\2\u0099\u009a\7j\2"+
		"\2\u009a\u009b\7g\2\2\u009b\u009c\7p\2\2\u009c&\3\2\2\2\u009d\u009e\5"+
		")\25\2\u009e\u00a0\7\60\2\2\u009f\u00a1\t\2\2\2\u00a0\u009f\3\2\2\2\u00a1"+
		"\u00a2\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3(\3\2\2\2"+
		"\u00a4\u00ad\7\62\2\2\u00a5\u00a9\4\63;\2\u00a6\u00a8\4\62;\2\u00a7\u00a6"+
		"\3\2\2\2\u00a8\u00ab\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa"+
		"\u00ad\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ac\u00a4\3\2\2\2\u00ac\u00a5\3\2"+
		"\2\2\u00ad*\3\2\2\2\u00ae\u00af\7V\2\2\u00af\u00b0\7t\2\2\u00b0\u00b1"+
		"\7w\2\2\u00b1\u00c1\7g\2\2\u00b2\u00b3\7v\2\2\u00b3\u00b4\7t\2\2\u00b4"+
		"\u00b5\7w\2\2\u00b5\u00c1\7g\2\2\u00b6\u00b7\7H\2\2\u00b7\u00b8\7c\2\2"+
		"\u00b8\u00b9\7n\2\2\u00b9\u00ba\7u\2\2\u00ba\u00c1\7g\2\2\u00bb\u00bc"+
		"\7h\2\2\u00bc\u00bd\7c\2\2\u00bd\u00be\7n\2\2\u00be\u00bf\7u\2\2\u00bf"+
		"\u00c1\7g\2\2\u00c0\u00ae\3\2\2\2\u00c0\u00b2\3\2\2\2\u00c0\u00b6\3\2"+
		"\2\2\u00c0\u00bb\3\2\2\2\u00c1,\3\2\2\2\u00c2\u00c4\7(\2\2\u00c3\u00c5"+
		"\t\3\2\2\u00c4\u00c3\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c6"+
		"\u00c7\3\2\2\2\u00c7.\3\2\2\2\u00c8\u00cc\t\4\2\2\u00c9\u00cb\t\5\2\2"+
		"\u00ca\u00c9\3\2\2\2\u00cb\u00ce\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cc\u00cd"+
		"\3\2\2\2\u00cd\60\3\2\2\2\u00ce\u00cc\3\2\2\2\u00cf\u00d1\t\6\2\2\u00d0"+
		"\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d2\u00d3\3\2"+
		"\2\2\u00d3\62\3\2\2\2\u00d4\u00d6\7\'\2\2\u00d5\u00d7\t\6\2\2\u00d6\u00d5"+
		"\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9"+
		"\64\3\2\2\2\u00da\u00db\7\61\2\2\u00db\u00dc\7,\2\2\u00dc\u00e0\3\2\2"+
		"\2\u00dd\u00df\13\2\2\2\u00de\u00dd\3\2\2\2\u00df\u00e2\3\2\2\2\u00e0"+
		"\u00e1\3\2\2\2\u00e0\u00de\3\2\2\2\u00e1\u00e3\3\2\2\2\u00e2\u00e0\3\2"+
		"\2\2\u00e3\u00e4\7,\2\2\u00e4\u00e5\7\61\2\2\u00e5\u00e6\3\2\2\2\u00e6"+
		"\u00e7\b\33\2\2\u00e7\66\3\2\2\2\u00e8\u00ea\t\7\2\2\u00e9\u00e8\3\2\2"+
		"\2\u00ea\u00eb\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ed"+
		"\3\2\2\2\u00ed\u00ee\b\34\3\2\u00ee8\3\2\2\2\r\2\u00a2\u00a9\u00ac\u00c0"+
		"\u00c6\u00cc\u00d2\u00d8\u00e0\u00eb";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}