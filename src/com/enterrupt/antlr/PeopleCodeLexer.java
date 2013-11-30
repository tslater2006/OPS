// Generated from /home/mquinn/evm/grammars/PeopleCode.g4 by ANTLR 4.1
package com.enterrupt.antlr;
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
		T__9=1, T__8=2, T__7=3, T__6=4, T__5=5, T__4=6, T__3=7, T__2=8, T__1=9, 
		T__0=10, BOOLEAN=11, NUMBER=12, SYSTEM_VAR=13, CBUFFER_REF=14, OBJECT_REF=15, 
		FN_NAME=16, COMMENT=17, WS=18;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'If'", "')'", "'.'", "','", "'('", "'='", "'MenuName'", "';'", "'End-If'", 
		"'Then'", "BOOLEAN", "NUMBER", "SYSTEM_VAR", "CBUFFER_REF", "OBJECT_REF", 
		"FN_NAME", "COMMENT", "WS"
	};
	public static final String[] ruleNames = {
		"T__9", "T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", 
		"T__0", "BOOLEAN", "NUMBER", "SYSTEM_VAR", "CBUFFER_REF", "OBJECT_REF", 
		"FN_NAME", "COMMENT", "WS"
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\24\u008d\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\f^\n\f\3\r\6\ra\n\r\r\r\16\rb\3\16\3\16"+
		"\6\16g\n\16\r\16\16\16h\3\17\3\17\3\17\3\17\3\20\6\20p\n\20\r\20\16\20"+
		"q\3\21\6\21u\n\21\r\21\16\21v\3\22\3\22\3\22\3\22\7\22}\n\22\f\22\16\22"+
		"\u0080\13\22\3\22\3\22\3\22\3\22\3\22\3\23\6\23\u0088\n\23\r\23\16\23"+
		"\u0089\3\23\3\23\3~\24\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21"+
		"\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\1#\23\2"+
		"%\24\3\3\2\6\3\2\62;\4\2C\\c|\4\2C\\aa\5\2\13\f\17\17\"\"\u0095\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3"+
		"\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2"+
		"%\3\2\2\2\3\'\3\2\2\2\5*\3\2\2\2\7,\3\2\2\2\t.\3\2\2\2\13\60\3\2\2\2\r"+
		"\62\3\2\2\2\17\64\3\2\2\2\21=\3\2\2\2\23?\3\2\2\2\25F\3\2\2\2\27]\3\2"+
		"\2\2\31`\3\2\2\2\33d\3\2\2\2\35j\3\2\2\2\37o\3\2\2\2!t\3\2\2\2#x\3\2\2"+
		"\2%\u0087\3\2\2\2\'(\7K\2\2()\7h\2\2)\4\3\2\2\2*+\7+\2\2+\6\3\2\2\2,-"+
		"\7\60\2\2-\b\3\2\2\2./\7.\2\2/\n\3\2\2\2\60\61\7*\2\2\61\f\3\2\2\2\62"+
		"\63\7?\2\2\63\16\3\2\2\2\64\65\7O\2\2\65\66\7g\2\2\66\67\7p\2\2\678\7"+
		"w\2\289\7P\2\29:\7c\2\2:;\7o\2\2;<\7g\2\2<\20\3\2\2\2=>\7=\2\2>\22\3\2"+
		"\2\2?@\7G\2\2@A\7p\2\2AB\7f\2\2BC\7/\2\2CD\7K\2\2DE\7h\2\2E\24\3\2\2\2"+
		"FG\7V\2\2GH\7j\2\2HI\7g\2\2IJ\7p\2\2J\26\3\2\2\2KL\7V\2\2LM\7t\2\2MN\7"+
		"w\2\2N^\7g\2\2OP\7v\2\2PQ\7t\2\2QR\7w\2\2R^\7g\2\2ST\7H\2\2TU\7c\2\2U"+
		"V\7n\2\2VW\7u\2\2W^\7g\2\2XY\7h\2\2YZ\7c\2\2Z[\7n\2\2[\\\7u\2\2\\^\7g"+
		"\2\2]K\3\2\2\2]O\3\2\2\2]S\3\2\2\2]X\3\2\2\2^\30\3\2\2\2_a\t\2\2\2`_\3"+
		"\2\2\2ab\3\2\2\2b`\3\2\2\2bc\3\2\2\2c\32\3\2\2\2df\7\'\2\2eg\t\3\2\2f"+
		"e\3\2\2\2gh\3\2\2\2hf\3\2\2\2hi\3\2\2\2i\34\3\2\2\2jk\5\37\20\2kl\7\60"+
		"\2\2lm\5\37\20\2m\36\3\2\2\2np\t\4\2\2on\3\2\2\2pq\3\2\2\2qo\3\2\2\2q"+
		"r\3\2\2\2r \3\2\2\2su\t\3\2\2ts\3\2\2\2uv\3\2\2\2vt\3\2\2\2vw\3\2\2\2"+
		"w\"\3\2\2\2xy\7\61\2\2yz\7,\2\2z~\3\2\2\2{}\13\2\2\2|{\3\2\2\2}\u0080"+
		"\3\2\2\2~\177\3\2\2\2~|\3\2\2\2\177\u0081\3\2\2\2\u0080~\3\2\2\2\u0081"+
		"\u0082\7,\2\2\u0082\u0083\7\61\2\2\u0083\u0084\3\2\2\2\u0084\u0085\b\22"+
		"\2\2\u0085$\3\2\2\2\u0086\u0088\t\5\2\2\u0087\u0086\3\2\2\2\u0088\u0089"+
		"\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008b\3\2\2\2\u008b"+
		"\u008c\b\23\3\2\u008c&\3\2\2\2\n\2]bhqv~\u0089";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}