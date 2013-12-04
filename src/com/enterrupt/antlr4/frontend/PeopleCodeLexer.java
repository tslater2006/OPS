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
		T__13=1, T__12=2, T__11=3, T__10=4, T__9=5, T__8=6, T__7=7, T__6=8, T__5=9, 
		T__4=10, T__3=11, T__2=12, T__1=13, T__0=14, IntegerLiteral=15, IDENTIFIER=16, 
		COMMENT=17, WS=18;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'True'", "'If'", "'False'", "')'", "'.'", "','", "'('", "'='", "'false'", 
		"';'", "'End-If'", "'MenuName'", "'true'", "'Then'", "IntegerLiteral", 
		"IDENTIFIER", "COMMENT", "WS"
	};
	public static final String[] ruleNames = {
		"T__13", "T__12", "T__11", "T__10", "T__9", "T__8", "T__7", "T__6", "T__5", 
		"T__4", "T__3", "T__2", "T__1", "T__0", "IntegerLiteral", "IDENTIFIER", 
		"COMMENT", "WS"
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\24\u0088\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13"+
		"\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\7\20"+
		"e\n\20\f\20\16\20h\13\20\5\20j\n\20\3\21\5\21m\n\21\3\21\6\21p\n\21\r"+
		"\21\16\21q\3\22\3\22\3\22\3\22\7\22x\n\22\f\22\16\22{\13\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\23\6\23\u0083\n\23\r\23\16\23\u0084\3\23\3\23\3y\24"+
		"\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27"+
		"\r\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\1#\23\2%\24\3\3\2\4\5\2C\\aa"+
		"c|\5\2\13\f\17\17\"\"\u008d\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3"+
		"\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2"+
		"\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37"+
		"\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\3\'\3\2\2\2\5,\3\2\2\2\7/\3"+
		"\2\2\2\t\65\3\2\2\2\13\67\3\2\2\2\r9\3\2\2\2\17;\3\2\2\2\21=\3\2\2\2\23"+
		"?\3\2\2\2\25E\3\2\2\2\27G\3\2\2\2\31N\3\2\2\2\33W\3\2\2\2\35\\\3\2\2\2"+
		"\37i\3\2\2\2!l\3\2\2\2#s\3\2\2\2%\u0082\3\2\2\2\'(\7V\2\2()\7t\2\2)*\7"+
		"w\2\2*+\7g\2\2+\4\3\2\2\2,-\7K\2\2-.\7h\2\2.\6\3\2\2\2/\60\7H\2\2\60\61"+
		"\7c\2\2\61\62\7n\2\2\62\63\7u\2\2\63\64\7g\2\2\64\b\3\2\2\2\65\66\7+\2"+
		"\2\66\n\3\2\2\2\678\7\60\2\28\f\3\2\2\29:\7.\2\2:\16\3\2\2\2;<\7*\2\2"+
		"<\20\3\2\2\2=>\7?\2\2>\22\3\2\2\2?@\7h\2\2@A\7c\2\2AB\7n\2\2BC\7u\2\2"+
		"CD\7g\2\2D\24\3\2\2\2EF\7=\2\2F\26\3\2\2\2GH\7G\2\2HI\7p\2\2IJ\7f\2\2"+
		"JK\7/\2\2KL\7K\2\2LM\7h\2\2M\30\3\2\2\2NO\7O\2\2OP\7g\2\2PQ\7p\2\2QR\7"+
		"w\2\2RS\7P\2\2ST\7c\2\2TU\7o\2\2UV\7g\2\2V\32\3\2\2\2WX\7v\2\2XY\7t\2"+
		"\2YZ\7w\2\2Z[\7g\2\2[\34\3\2\2\2\\]\7V\2\2]^\7j\2\2^_\7g\2\2_`\7p\2\2"+
		"`\36\3\2\2\2aj\7\62\2\2bf\4\63;\2ce\4\62;\2dc\3\2\2\2eh\3\2\2\2fd\3\2"+
		"\2\2fg\3\2\2\2gj\3\2\2\2hf\3\2\2\2ia\3\2\2\2ib\3\2\2\2j \3\2\2\2km\7\'"+
		"\2\2lk\3\2\2\2lm\3\2\2\2mo\3\2\2\2np\t\2\2\2on\3\2\2\2pq\3\2\2\2qo\3\2"+
		"\2\2qr\3\2\2\2r\"\3\2\2\2st\7\61\2\2tu\7,\2\2uy\3\2\2\2vx\13\2\2\2wv\3"+
		"\2\2\2x{\3\2\2\2yz\3\2\2\2yw\3\2\2\2z|\3\2\2\2{y\3\2\2\2|}\7,\2\2}~\7"+
		"\61\2\2~\177\3\2\2\2\177\u0080\b\22\2\2\u0080$\3\2\2\2\u0081\u0083\t\3"+
		"\2\2\u0082\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0082\3\2\2\2\u0084"+
		"\u0085\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0087\b\23\3\2\u0087&\3\2\2\2"+
		"\t\2filqy\u0084";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}