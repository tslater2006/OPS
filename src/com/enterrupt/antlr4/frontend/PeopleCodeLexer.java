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
		T__25=1, T__24=2, T__23=3, T__22=4, T__21=5, T__20=6, T__19=7, T__18=8, 
		T__17=9, T__16=10, T__15=11, T__14=12, T__13=13, T__12=14, T__11=15, T__10=16, 
		T__9=17, T__8=18, T__7=19, T__6=20, T__5=21, T__4=22, T__3=23, T__2=24, 
		T__1=25, T__0=26, DecimalLiteral=27, IntegerLiteral=28, VAR_ID=29, BUFFER_ID=30, 
		FUNC_ID=31, SYS_VAR_ID=32, COMMENT=33, WS=34;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'True'", "','", "'('", "'false'", "'When-Other'", "'Evaluate'", "'Visible'", 
		"'When'", "'true'", "'Component'", "'Global'", "'If'", "'False'", "'End-Evaluate'", 
		"'.'", "')'", "'Record'", "'='", "';'", "'End-If'", "'Exit'", "'Break'", 
		"'Local'", "'string'", "'MenuName'", "'Then'", "DecimalLiteral", "IntegerLiteral", 
		"VAR_ID", "BUFFER_ID", "FUNC_ID", "SYS_VAR_ID", "COMMENT", "WS"
	};
	public static final String[] ruleNames = {
		"T__25", "T__24", "T__23", "T__22", "T__21", "T__20", "T__19", "T__18", 
		"T__17", "T__16", "T__15", "T__14", "T__13", "T__12", "T__11", "T__10", 
		"T__9", "T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", 
		"T__0", "DecimalLiteral", "IntegerLiteral", "VAR_ID", "BUFFER_ID", "FUNC_ID", 
		"SYS_VAR_ID", "COMMENT", "WS"
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
		case 32: COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 33: WS_action((RuleContext)_localctx, actionIndex); break;
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2$\u011d\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t"+
		"\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20"+
		"\3\20\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33"+
		"\3\33\3\33\3\33\3\34\3\34\3\34\6\34\u00e3\n\34\r\34\16\34\u00e4\3\35\3"+
		"\35\3\35\7\35\u00ea\n\35\f\35\16\35\u00ed\13\35\5\35\u00ef\n\35\3\36\3"+
		"\36\6\36\u00f3\n\36\r\36\16\36\u00f4\3\37\3\37\7\37\u00f9\n\37\f\37\16"+
		"\37\u00fc\13\37\3 \6 \u00ff\n \r \16 \u0100\3!\3!\6!\u0105\n!\r!\16!\u0106"+
		"\3\"\3\"\3\"\3\"\7\"\u010d\n\"\f\"\16\"\u0110\13\"\3\"\3\"\3\"\3\"\3\""+
		"\3#\6#\u0118\n#\r#\16#\u0119\3#\3#\3\u010e$\3\3\1\5\4\1\7\5\1\t\6\1\13"+
		"\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1"+
		"\37\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1/\31\1\61\32\1\63\33"+
		"\1\65\34\1\67\35\19\36\1;\37\1= \1?!\1A\"\1C#\2E$\3\3\2\b\3\2\62;\5\2"+
		"C\\aac|\3\2C\\\5\2\62;C\\aa\4\2C\\c|\5\2\13\f\17\17\"\"\u0125\2\3\3\2"+
		"\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17"+
		"\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2"+
		"\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3"+
		"\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3"+
		"\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2"+
		"=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\3G\3\2\2\2\5L\3"+
		"\2\2\2\7N\3\2\2\2\tP\3\2\2\2\13V\3\2\2\2\ra\3\2\2\2\17j\3\2\2\2\21r\3"+
		"\2\2\2\23w\3\2\2\2\25|\3\2\2\2\27\u0086\3\2\2\2\31\u008d\3\2\2\2\33\u0090"+
		"\3\2\2\2\35\u0096\3\2\2\2\37\u00a3\3\2\2\2!\u00a5\3\2\2\2#\u00a7\3\2\2"+
		"\2%\u00ae\3\2\2\2\'\u00b0\3\2\2\2)\u00b2\3\2\2\2+\u00b9\3\2\2\2-\u00be"+
		"\3\2\2\2/\u00c4\3\2\2\2\61\u00ca\3\2\2\2\63\u00d1\3\2\2\2\65\u00da\3\2"+
		"\2\2\67\u00df\3\2\2\29\u00ee\3\2\2\2;\u00f0\3\2\2\2=\u00f6\3\2\2\2?\u00fe"+
		"\3\2\2\2A\u0102\3\2\2\2C\u0108\3\2\2\2E\u0117\3\2\2\2GH\7V\2\2HI\7t\2"+
		"\2IJ\7w\2\2JK\7g\2\2K\4\3\2\2\2LM\7.\2\2M\6\3\2\2\2NO\7*\2\2O\b\3\2\2"+
		"\2PQ\7h\2\2QR\7c\2\2RS\7n\2\2ST\7u\2\2TU\7g\2\2U\n\3\2\2\2VW\7Y\2\2WX"+
		"\7j\2\2XY\7g\2\2YZ\7p\2\2Z[\7/\2\2[\\\7Q\2\2\\]\7v\2\2]^\7j\2\2^_\7g\2"+
		"\2_`\7t\2\2`\f\3\2\2\2ab\7G\2\2bc\7x\2\2cd\7c\2\2de\7n\2\2ef\7w\2\2fg"+
		"\7c\2\2gh\7v\2\2hi\7g\2\2i\16\3\2\2\2jk\7X\2\2kl\7k\2\2lm\7u\2\2mn\7k"+
		"\2\2no\7d\2\2op\7n\2\2pq\7g\2\2q\20\3\2\2\2rs\7Y\2\2st\7j\2\2tu\7g\2\2"+
		"uv\7p\2\2v\22\3\2\2\2wx\7v\2\2xy\7t\2\2yz\7w\2\2z{\7g\2\2{\24\3\2\2\2"+
		"|}\7E\2\2}~\7q\2\2~\177\7o\2\2\177\u0080\7r\2\2\u0080\u0081\7q\2\2\u0081"+
		"\u0082\7p\2\2\u0082\u0083\7g\2\2\u0083\u0084\7p\2\2\u0084\u0085\7v\2\2"+
		"\u0085\26\3\2\2\2\u0086\u0087\7I\2\2\u0087\u0088\7n\2\2\u0088\u0089\7"+
		"q\2\2\u0089\u008a\7d\2\2\u008a\u008b\7c\2\2\u008b\u008c\7n\2\2\u008c\30"+
		"\3\2\2\2\u008d\u008e\7K\2\2\u008e\u008f\7h\2\2\u008f\32\3\2\2\2\u0090"+
		"\u0091\7H\2\2\u0091\u0092\7c\2\2\u0092\u0093\7n\2\2\u0093\u0094\7u\2\2"+
		"\u0094\u0095\7g\2\2\u0095\34\3\2\2\2\u0096\u0097\7G\2\2\u0097\u0098\7"+
		"p\2\2\u0098\u0099\7f\2\2\u0099\u009a\7/\2\2\u009a\u009b\7G\2\2\u009b\u009c"+
		"\7x\2\2\u009c\u009d\7c\2\2\u009d\u009e\7n\2\2\u009e\u009f\7w\2\2\u009f"+
		"\u00a0\7c\2\2\u00a0\u00a1\7v\2\2\u00a1\u00a2\7g\2\2\u00a2\36\3\2\2\2\u00a3"+
		"\u00a4\7\60\2\2\u00a4 \3\2\2\2\u00a5\u00a6\7+\2\2\u00a6\"\3\2\2\2\u00a7"+
		"\u00a8\7T\2\2\u00a8\u00a9\7g\2\2\u00a9\u00aa\7e\2\2\u00aa\u00ab\7q\2\2"+
		"\u00ab\u00ac\7t\2\2\u00ac\u00ad\7f\2\2\u00ad$\3\2\2\2\u00ae\u00af\7?\2"+
		"\2\u00af&\3\2\2\2\u00b0\u00b1\7=\2\2\u00b1(\3\2\2\2\u00b2\u00b3\7G\2\2"+
		"\u00b3\u00b4\7p\2\2\u00b4\u00b5\7f\2\2\u00b5\u00b6\7/\2\2\u00b6\u00b7"+
		"\7K\2\2\u00b7\u00b8\7h\2\2\u00b8*\3\2\2\2\u00b9\u00ba\7G\2\2\u00ba\u00bb"+
		"\7z\2\2\u00bb\u00bc\7k\2\2\u00bc\u00bd\7v\2\2\u00bd,\3\2\2\2\u00be\u00bf"+
		"\7D\2\2\u00bf\u00c0\7t\2\2\u00c0\u00c1\7g\2\2\u00c1\u00c2\7c\2\2\u00c2"+
		"\u00c3\7m\2\2\u00c3.\3\2\2\2\u00c4\u00c5\7N\2\2\u00c5\u00c6\7q\2\2\u00c6"+
		"\u00c7\7e\2\2\u00c7\u00c8\7c\2\2\u00c8\u00c9\7n\2\2\u00c9\60\3\2\2\2\u00ca"+
		"\u00cb\7u\2\2\u00cb\u00cc\7v\2\2\u00cc\u00cd\7t\2\2\u00cd\u00ce\7k\2\2"+
		"\u00ce\u00cf\7p\2\2\u00cf\u00d0\7i\2\2\u00d0\62\3\2\2\2\u00d1\u00d2\7"+
		"O\2\2\u00d2\u00d3\7g\2\2\u00d3\u00d4\7p\2\2\u00d4\u00d5\7w\2\2\u00d5\u00d6"+
		"\7P\2\2\u00d6\u00d7\7c\2\2\u00d7\u00d8\7o\2\2\u00d8\u00d9\7g\2\2\u00d9"+
		"\64\3\2\2\2\u00da\u00db\7V\2\2\u00db\u00dc\7j\2\2\u00dc\u00dd\7g\2\2\u00dd"+
		"\u00de\7p\2\2\u00de\66\3\2\2\2\u00df\u00e0\59\35\2\u00e0\u00e2\7\60\2"+
		"\2\u00e1\u00e3\t\2\2\2\u00e2\u00e1\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e2"+
		"\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e58\3\2\2\2\u00e6\u00ef\7\62\2\2\u00e7"+
		"\u00eb\4\63;\2\u00e8\u00ea\4\62;\2\u00e9\u00e8\3\2\2\2\u00ea\u00ed\3\2"+
		"\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed"+
		"\u00eb\3\2\2\2\u00ee\u00e6\3\2\2\2\u00ee\u00e7\3\2\2\2\u00ef:\3\2\2\2"+
		"\u00f0\u00f2\7(\2\2\u00f1\u00f3\t\3\2\2\u00f2\u00f1\3\2\2\2\u00f3\u00f4"+
		"\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5<\3\2\2\2\u00f6"+
		"\u00fa\t\4\2\2\u00f7\u00f9\t\5\2\2\u00f8\u00f7\3\2\2\2\u00f9\u00fc\3\2"+
		"\2\2\u00fa\u00f8\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb>\3\2\2\2\u00fc\u00fa"+
		"\3\2\2\2\u00fd\u00ff\t\6\2\2\u00fe\u00fd\3\2\2\2\u00ff\u0100\3\2\2\2\u0100"+
		"\u00fe\3\2\2\2\u0100\u0101\3\2\2\2\u0101@\3\2\2\2\u0102\u0104\7\'\2\2"+
		"\u0103\u0105\t\6\2\2\u0104\u0103\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0104"+
		"\3\2\2\2\u0106\u0107\3\2\2\2\u0107B\3\2\2\2\u0108\u0109\7\61\2\2\u0109"+
		"\u010a\7,\2\2\u010a\u010e\3\2\2\2\u010b\u010d\13\2\2\2\u010c\u010b\3\2"+
		"\2\2\u010d\u0110\3\2\2\2\u010e\u010f\3\2\2\2\u010e\u010c\3\2\2\2\u010f"+
		"\u0111\3\2\2\2\u0110\u010e\3\2\2\2\u0111\u0112\7,\2\2\u0112\u0113\7\61"+
		"\2\2\u0113\u0114\3\2\2\2\u0114\u0115\b\"\2\2\u0115D\3\2\2\2\u0116\u0118"+
		"\t\7\2\2\u0117\u0116\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u0117\3\2\2\2\u0119"+
		"\u011a\3\2\2\2\u011a\u011b\3\2\2\2\u011b\u011c\b#\3\2\u011cF\3\2\2\2\f"+
		"\2\u00e4\u00eb\u00ee\u00f4\u00fa\u0100\u0106\u010e\u0119";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}