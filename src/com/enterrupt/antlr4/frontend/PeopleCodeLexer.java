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
		T__63=1, T__62=2, T__61=3, T__60=4, T__59=5, T__58=6, T__57=7, T__56=8, 
		T__55=9, T__54=10, T__53=11, T__52=12, T__51=13, T__50=14, T__49=15, T__48=16, 
		T__47=17, T__46=18, T__45=19, T__44=20, T__43=21, T__42=22, T__41=23, 
		T__40=24, T__39=25, T__38=26, T__37=27, T__36=28, T__35=29, T__34=30, 
		T__33=31, T__32=32, T__31=33, T__30=34, T__29=35, T__28=36, T__27=37, 
		T__26=38, T__25=39, T__24=40, T__23=41, T__22=42, T__21=43, T__20=44, 
		T__19=45, T__18=46, T__17=47, T__16=48, T__15=49, T__14=50, T__13=51, 
		T__12=52, T__11=53, T__10=54, T__9=55, T__8=56, T__7=57, T__6=58, T__5=59, 
		T__4=60, T__3=61, T__2=62, T__1=63, T__0=64, VAR_ID=65, SYS_VAR_ID=66, 
		GENERIC_ID=67, DecimalLiteral=68, IntegerLiteral=69, StringLiteral=70, 
		REM=71, COMMENT=72, WS=73;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'Field'", "'BusProcess'", "'To'", "'false'", "'Page'", "'RecName'", "'HTML'", 
		"'Else'", "'Rowset'", "'For'", "'boolean'", "'CompIntfc'", "'Global'", 
		"'False'", "'End-Evaluate'", "'Image'", "')'", "'Record'", "'@'", "'PanelGroup'", 
		"'='", "'End-If'", "'BarName'", "'Function'", "'Exit'", "'Break'", "'Local'", 
		"'BusEvent'", "'|'", "'Then'", "'True'", "'End-For'", "','", "'('", "':'", 
		"'Interlink'", "'StyleSheet'", "'Operation'", "'PeopleCode'", "'Declare'", 
		"'ItemName'", "'When-Other'", "'Evaluate'", "'Panel'", "'When'", "'BusActivity'", 
		"'true'", "'Component'", "'import'", "'If'", "'And'", "'.'", "'FieldFormula'", 
		"'Scroll'", "'create'", "'<>'", "'SQL'", "';'", "'number'", "'Message'", 
		"'integer'", "'string'", "'MenuName'", "'FileLayout'", "VAR_ID", "SYS_VAR_ID", 
		"GENERIC_ID", "DecimalLiteral", "IntegerLiteral", "StringLiteral", "REM", 
		"COMMENT", "WS"
	};
	public static final String[] ruleNames = {
		"T__63", "T__62", "T__61", "T__60", "T__59", "T__58", "T__57", "T__56", 
		"T__55", "T__54", "T__53", "T__52", "T__51", "T__50", "T__49", "T__48", 
		"T__47", "T__46", "T__45", "T__44", "T__43", "T__42", "T__41", "T__40", 
		"T__39", "T__38", "T__37", "T__36", "T__35", "T__34", "T__33", "T__32", 
		"T__31", "T__30", "T__29", "T__28", "T__27", "T__26", "T__25", "T__24", 
		"T__23", "T__22", "T__21", "T__20", "T__19", "T__18", "T__17", "T__16", 
		"T__15", "T__14", "T__13", "T__12", "T__11", "T__10", "T__9", "T__8", 
		"T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", "VAR_ID", 
		"SYS_VAR_ID", "GENERIC_ID", "DecimalLiteral", "IntegerLiteral", "StringLiteral", 
		"REM", "COMMENT", "WS"
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
		case 70: REM_action((RuleContext)_localctx, actionIndex); break;

		case 71: COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 72: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2: skip();  break;
		}
	}
	private void REM_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}
	private void COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2K\u0290\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22"+
		"\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35"+
		"\3\35\3\35\3\35\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3!\3"+
		"!\3!\3!\3!\3!\3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%"+
		"\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'"+
		"\3\'\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3)\3)\3*\3*\3"+
		"*\3*\3*\3*\3*\3*\3*\3+\3+\3+\3+\3+\3+\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3"+
		",\3,\3,\3,\3-\3-\3-\3-\3-\3-\3.\3.\3.\3.\3.\3/\3/\3/\3/\3/\3/\3/\3/\3"+
		"/\3/\3/\3/\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\61\3\61\3\61\3\61"+
		"\3\61\3\61\3\61\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\63\3\63\3\63\3\64"+
		"\3\64\3\64\3\64\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66"+
		"\3\66\3\66\3\66\3\66\3\67\3\67\3\67\3\67\3\67\3\67\3\67\38\38\38\38\3"+
		"8\38\38\39\39\39\3:\3:\3:\3:\3;\3;\3<\3<\3<\3<\3<\3<\3<\3=\3=\3=\3=\3"+
		"=\3=\3=\3=\3>\3>\3>\3>\3>\3>\3>\3>\3?\3?\3?\3?\3?\3?\3?\3@\3@\3@\3@\3"+
		"@\3@\3@\3@\3@\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3B\3B\6B\u0245\nB\rB\16"+
		"B\u0246\3C\3C\6C\u024b\nC\rC\16C\u024c\3D\3D\7D\u0251\nD\fD\16D\u0254"+
		"\13D\3E\3E\3E\6E\u0259\nE\rE\16E\u025a\3F\3F\3F\7F\u0260\nF\fF\16F\u0263"+
		"\13F\5F\u0265\nF\3G\3G\7G\u0269\nG\fG\16G\u026c\13G\3G\3G\3H\3H\3H\3H"+
		"\3H\7H\u0275\nH\fH\16H\u0278\13H\3H\3H\3I\3I\3I\3I\7I\u0280\nI\fI\16I"+
		"\u0283\13I\3I\3I\3I\3I\3I\3J\6J\u028b\nJ\rJ\16J\u028c\3J\3J\3\u0281K\3"+
		"\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r"+
		"\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27"+
		"\1-\30\1/\31\1\61\32\1\63\33\1\65\34\1\67\35\19\36\1;\37\1= \1?!\1A\""+
		"\1C#\1E$\1G%\1I&\1K\'\1M(\1O)\1Q*\1S+\1U,\1W-\1Y.\1[/\1]\60\1_\61\1a\62"+
		"\1c\63\1e\64\1g\65\1i\66\1k\67\1m8\1o9\1q:\1s;\1u<\1w=\1y>\1{?\1}@\1\177"+
		"A\1\u0081B\1\u0083C\1\u0085D\1\u0087E\1\u0089F\1\u008bG\1\u008dH\1\u008f"+
		"I\2\u0091J\3\u0093K\4\3\2\t\5\2C\\aac|\4\2C\\c|\6\2\62;C\\aac|\3\2\62"+
		";\3\2$$\4\2\f\f\17\17\5\2\13\f\17\17\"\"\u0299\2\3\3\2\2\2\2\5\3\2\2\2"+
		"\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3"+
		"\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2"+
		"\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2"+
		"\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2"+
		"\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2"+
		"\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2"+
		"\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y"+
		"\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2"+
		"\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2"+
		"\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177"+
		"\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2"+
		"\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091"+
		"\3\2\2\2\2\u0093\3\2\2\2\3\u0095\3\2\2\2\5\u009b\3\2\2\2\7\u00a6\3\2\2"+
		"\2\t\u00a9\3\2\2\2\13\u00af\3\2\2\2\r\u00b4\3\2\2\2\17\u00bc\3\2\2\2\21"+
		"\u00c1\3\2\2\2\23\u00c6\3\2\2\2\25\u00cd\3\2\2\2\27\u00d1\3\2\2\2\31\u00d9"+
		"\3\2\2\2\33\u00e3\3\2\2\2\35\u00ea\3\2\2\2\37\u00f0\3\2\2\2!\u00fd\3\2"+
		"\2\2#\u0103\3\2\2\2%\u0105\3\2\2\2\'\u010c\3\2\2\2)\u010e\3\2\2\2+\u0119"+
		"\3\2\2\2-\u011b\3\2\2\2/\u0122\3\2\2\2\61\u012a\3\2\2\2\63\u0133\3\2\2"+
		"\2\65\u0138\3\2\2\2\67\u013e\3\2\2\29\u0144\3\2\2\2;\u014d\3\2\2\2=\u014f"+
		"\3\2\2\2?\u0154\3\2\2\2A\u0159\3\2\2\2C\u0161\3\2\2\2E\u0163\3\2\2\2G"+
		"\u0165\3\2\2\2I\u0167\3\2\2\2K\u0171\3\2\2\2M\u017c\3\2\2\2O\u0186\3\2"+
		"\2\2Q\u0191\3\2\2\2S\u0199\3\2\2\2U\u01a2\3\2\2\2W\u01ad\3\2\2\2Y\u01b6"+
		"\3\2\2\2[\u01bc\3\2\2\2]\u01c1\3\2\2\2_\u01cd\3\2\2\2a\u01d2\3\2\2\2c"+
		"\u01dc\3\2\2\2e\u01e3\3\2\2\2g\u01e6\3\2\2\2i\u01ea\3\2\2\2k\u01ec\3\2"+
		"\2\2m\u01f9\3\2\2\2o\u0200\3\2\2\2q\u0207\3\2\2\2s\u020a\3\2\2\2u\u020e"+
		"\3\2\2\2w\u0210\3\2\2\2y\u0217\3\2\2\2{\u021f\3\2\2\2}\u0227\3\2\2\2\177"+
		"\u022e\3\2\2\2\u0081\u0237\3\2\2\2\u0083\u0242\3\2\2\2\u0085\u0248\3\2"+
		"\2\2\u0087\u024e\3\2\2\2\u0089\u0255\3\2\2\2\u008b\u0264\3\2\2\2\u008d"+
		"\u0266\3\2\2\2\u008f\u026f\3\2\2\2\u0091\u027b\3\2\2\2\u0093\u028a\3\2"+
		"\2\2\u0095\u0096\7H\2\2\u0096\u0097\7k\2\2\u0097\u0098\7g\2\2\u0098\u0099"+
		"\7n\2\2\u0099\u009a\7f\2\2\u009a\4\3\2\2\2\u009b\u009c\7D\2\2\u009c\u009d"+
		"\7w\2\2\u009d\u009e\7u\2\2\u009e\u009f\7R\2\2\u009f\u00a0\7t\2\2\u00a0"+
		"\u00a1\7q\2\2\u00a1\u00a2\7e\2\2\u00a2\u00a3\7g\2\2\u00a3\u00a4\7u\2\2"+
		"\u00a4\u00a5\7u\2\2\u00a5\6\3\2\2\2\u00a6\u00a7\7V\2\2\u00a7\u00a8\7q"+
		"\2\2\u00a8\b\3\2\2\2\u00a9\u00aa\7h\2\2\u00aa\u00ab\7c\2\2\u00ab\u00ac"+
		"\7n\2\2\u00ac\u00ad\7u\2\2\u00ad\u00ae\7g\2\2\u00ae\n\3\2\2\2\u00af\u00b0"+
		"\7R\2\2\u00b0\u00b1\7c\2\2\u00b1\u00b2\7i\2\2\u00b2\u00b3\7g\2\2\u00b3"+
		"\f\3\2\2\2\u00b4\u00b5\7T\2\2\u00b5\u00b6\7g\2\2\u00b6\u00b7\7e\2\2\u00b7"+
		"\u00b8\7P\2\2\u00b8\u00b9\7c\2\2\u00b9\u00ba\7o\2\2\u00ba\u00bb\7g\2\2"+
		"\u00bb\16\3\2\2\2\u00bc\u00bd\7J\2\2\u00bd\u00be\7V\2\2\u00be\u00bf\7"+
		"O\2\2\u00bf\u00c0\7N\2\2\u00c0\20\3\2\2\2\u00c1\u00c2\7G\2\2\u00c2\u00c3"+
		"\7n\2\2\u00c3\u00c4\7u\2\2\u00c4\u00c5\7g\2\2\u00c5\22\3\2\2\2\u00c6\u00c7"+
		"\7T\2\2\u00c7\u00c8\7q\2\2\u00c8\u00c9\7y\2\2\u00c9\u00ca\7u\2\2\u00ca"+
		"\u00cb\7g\2\2\u00cb\u00cc\7v\2\2\u00cc\24\3\2\2\2\u00cd\u00ce\7H\2\2\u00ce"+
		"\u00cf\7q\2\2\u00cf\u00d0\7t\2\2\u00d0\26\3\2\2\2\u00d1\u00d2\7d\2\2\u00d2"+
		"\u00d3\7q\2\2\u00d3\u00d4\7q\2\2\u00d4\u00d5\7n\2\2\u00d5\u00d6\7g\2\2"+
		"\u00d6\u00d7\7c\2\2\u00d7\u00d8\7p\2\2\u00d8\30\3\2\2\2\u00d9\u00da\7"+
		"E\2\2\u00da\u00db\7q\2\2\u00db\u00dc\7o\2\2\u00dc\u00dd\7r\2\2\u00dd\u00de"+
		"\7K\2\2\u00de\u00df\7p\2\2\u00df\u00e0\7v\2\2\u00e0\u00e1\7h\2\2\u00e1"+
		"\u00e2\7e\2\2\u00e2\32\3\2\2\2\u00e3\u00e4\7I\2\2\u00e4\u00e5\7n\2\2\u00e5"+
		"\u00e6\7q\2\2\u00e6\u00e7\7d\2\2\u00e7\u00e8\7c\2\2\u00e8\u00e9\7n\2\2"+
		"\u00e9\34\3\2\2\2\u00ea\u00eb\7H\2\2\u00eb\u00ec\7c\2\2\u00ec\u00ed\7"+
		"n\2\2\u00ed\u00ee\7u\2\2\u00ee\u00ef\7g\2\2\u00ef\36\3\2\2\2\u00f0\u00f1"+
		"\7G\2\2\u00f1\u00f2\7p\2\2\u00f2\u00f3\7f\2\2\u00f3\u00f4\7/\2\2\u00f4"+
		"\u00f5\7G\2\2\u00f5\u00f6\7x\2\2\u00f6\u00f7\7c\2\2\u00f7\u00f8\7n\2\2"+
		"\u00f8\u00f9\7w\2\2\u00f9\u00fa\7c\2\2\u00fa\u00fb\7v\2\2\u00fb\u00fc"+
		"\7g\2\2\u00fc \3\2\2\2\u00fd\u00fe\7K\2\2\u00fe\u00ff\7o\2\2\u00ff\u0100"+
		"\7c\2\2\u0100\u0101\7i\2\2\u0101\u0102\7g\2\2\u0102\"\3\2\2\2\u0103\u0104"+
		"\7+\2\2\u0104$\3\2\2\2\u0105\u0106\7T\2\2\u0106\u0107\7g\2\2\u0107\u0108"+
		"\7e\2\2\u0108\u0109\7q\2\2\u0109\u010a\7t\2\2\u010a\u010b\7f\2\2\u010b"+
		"&\3\2\2\2\u010c\u010d\7B\2\2\u010d(\3\2\2\2\u010e\u010f\7R\2\2\u010f\u0110"+
		"\7c\2\2\u0110\u0111\7p\2\2\u0111\u0112\7g\2\2\u0112\u0113\7n\2\2\u0113"+
		"\u0114\7I\2\2\u0114\u0115\7t\2\2\u0115\u0116\7q\2\2\u0116\u0117\7w\2\2"+
		"\u0117\u0118\7r\2\2\u0118*\3\2\2\2\u0119\u011a\7?\2\2\u011a,\3\2\2\2\u011b"+
		"\u011c\7G\2\2\u011c\u011d\7p\2\2\u011d\u011e\7f\2\2\u011e\u011f\7/\2\2"+
		"\u011f\u0120\7K\2\2\u0120\u0121\7h\2\2\u0121.\3\2\2\2\u0122\u0123\7D\2"+
		"\2\u0123\u0124\7c\2\2\u0124\u0125\7t\2\2\u0125\u0126\7P\2\2\u0126\u0127"+
		"\7c\2\2\u0127\u0128\7o\2\2\u0128\u0129\7g\2\2\u0129\60\3\2\2\2\u012a\u012b"+
		"\7H\2\2\u012b\u012c\7w\2\2\u012c\u012d\7p\2\2\u012d\u012e\7e\2\2\u012e"+
		"\u012f\7v\2\2\u012f\u0130\7k\2\2\u0130\u0131\7q\2\2\u0131\u0132\7p\2\2"+
		"\u0132\62\3\2\2\2\u0133\u0134\7G\2\2\u0134\u0135\7z\2\2\u0135\u0136\7"+
		"k\2\2\u0136\u0137\7v\2\2\u0137\64\3\2\2\2\u0138\u0139\7D\2\2\u0139\u013a"+
		"\7t\2\2\u013a\u013b\7g\2\2\u013b\u013c\7c\2\2\u013c\u013d\7m\2\2\u013d"+
		"\66\3\2\2\2\u013e\u013f\7N\2\2\u013f\u0140\7q\2\2\u0140\u0141\7e\2\2\u0141"+
		"\u0142\7c\2\2\u0142\u0143\7n\2\2\u01438\3\2\2\2\u0144\u0145\7D\2\2\u0145"+
		"\u0146\7w\2\2\u0146\u0147\7u\2\2\u0147\u0148\7G\2\2\u0148\u0149\7x\2\2"+
		"\u0149\u014a\7g\2\2\u014a\u014b\7p\2\2\u014b\u014c\7v\2\2\u014c:\3\2\2"+
		"\2\u014d\u014e\7~\2\2\u014e<\3\2\2\2\u014f\u0150\7V\2\2\u0150\u0151\7"+
		"j\2\2\u0151\u0152\7g\2\2\u0152\u0153\7p\2\2\u0153>\3\2\2\2\u0154\u0155"+
		"\7V\2\2\u0155\u0156\7t\2\2\u0156\u0157\7w\2\2\u0157\u0158\7g\2\2\u0158"+
		"@\3\2\2\2\u0159\u015a\7G\2\2\u015a\u015b\7p\2\2\u015b\u015c\7f\2\2\u015c"+
		"\u015d\7/\2\2\u015d\u015e\7H\2\2\u015e\u015f\7q\2\2\u015f\u0160\7t\2\2"+
		"\u0160B\3\2\2\2\u0161\u0162\7.\2\2\u0162D\3\2\2\2\u0163\u0164\7*\2\2\u0164"+
		"F\3\2\2\2\u0165\u0166\7<\2\2\u0166H\3\2\2\2\u0167\u0168\7K\2\2\u0168\u0169"+
		"\7p\2\2\u0169\u016a\7v\2\2\u016a\u016b\7g\2\2\u016b\u016c\7t\2\2\u016c"+
		"\u016d\7n\2\2\u016d\u016e\7k\2\2\u016e\u016f\7p\2\2\u016f\u0170\7m\2\2"+
		"\u0170J\3\2\2\2\u0171\u0172\7U\2\2\u0172\u0173\7v\2\2\u0173\u0174\7{\2"+
		"\2\u0174\u0175\7n\2\2\u0175\u0176\7g\2\2\u0176\u0177\7U\2\2\u0177\u0178"+
		"\7j\2\2\u0178\u0179\7g\2\2\u0179\u017a\7g\2\2\u017a\u017b\7v\2\2\u017b"+
		"L\3\2\2\2\u017c\u017d\7Q\2\2\u017d\u017e\7r\2\2\u017e\u017f\7g\2\2\u017f"+
		"\u0180\7t\2\2\u0180\u0181\7c\2\2\u0181\u0182\7v\2\2\u0182\u0183\7k\2\2"+
		"\u0183\u0184\7q\2\2\u0184\u0185\7p\2\2\u0185N\3\2\2\2\u0186\u0187\7R\2"+
		"\2\u0187\u0188\7g\2\2\u0188\u0189\7q\2\2\u0189\u018a\7r\2\2\u018a\u018b"+
		"\7n\2\2\u018b\u018c\7g\2\2\u018c\u018d\7E\2\2\u018d\u018e\7q\2\2\u018e"+
		"\u018f\7f\2\2\u018f\u0190\7g\2\2\u0190P\3\2\2\2\u0191\u0192\7F\2\2\u0192"+
		"\u0193\7g\2\2\u0193\u0194\7e\2\2\u0194\u0195\7n\2\2\u0195\u0196\7c\2\2"+
		"\u0196\u0197\7t\2\2\u0197\u0198\7g\2\2\u0198R\3\2\2\2\u0199\u019a\7K\2"+
		"\2\u019a\u019b\7v\2\2\u019b\u019c\7g\2\2\u019c\u019d\7o\2\2\u019d\u019e"+
		"\7P\2\2\u019e\u019f\7c\2\2\u019f\u01a0\7o\2\2\u01a0\u01a1\7g\2\2\u01a1"+
		"T\3\2\2\2\u01a2\u01a3\7Y\2\2\u01a3\u01a4\7j\2\2\u01a4\u01a5\7g\2\2\u01a5"+
		"\u01a6\7p\2\2\u01a6\u01a7\7/\2\2\u01a7\u01a8\7Q\2\2\u01a8\u01a9\7v\2\2"+
		"\u01a9\u01aa\7j\2\2\u01aa\u01ab\7g\2\2\u01ab\u01ac\7t\2\2\u01acV\3\2\2"+
		"\2\u01ad\u01ae\7G\2\2\u01ae\u01af\7x\2\2\u01af\u01b0\7c\2\2\u01b0\u01b1"+
		"\7n\2\2\u01b1\u01b2\7w\2\2\u01b2\u01b3\7c\2\2\u01b3\u01b4\7v\2\2\u01b4"+
		"\u01b5\7g\2\2\u01b5X\3\2\2\2\u01b6\u01b7\7R\2\2\u01b7\u01b8\7c\2\2\u01b8"+
		"\u01b9\7p\2\2\u01b9\u01ba\7g\2\2\u01ba\u01bb\7n\2\2\u01bbZ\3\2\2\2\u01bc"+
		"\u01bd\7Y\2\2\u01bd\u01be\7j\2\2\u01be\u01bf\7g\2\2\u01bf\u01c0\7p\2\2"+
		"\u01c0\\\3\2\2\2\u01c1\u01c2\7D\2\2\u01c2\u01c3\7w\2\2\u01c3\u01c4\7u"+
		"\2\2\u01c4\u01c5\7C\2\2\u01c5\u01c6\7e\2\2\u01c6\u01c7\7v\2\2\u01c7\u01c8"+
		"\7k\2\2\u01c8\u01c9\7x\2\2\u01c9\u01ca\7k\2\2\u01ca\u01cb\7v\2\2\u01cb"+
		"\u01cc\7{\2\2\u01cc^\3\2\2\2\u01cd\u01ce\7v\2\2\u01ce\u01cf\7t\2\2\u01cf"+
		"\u01d0\7w\2\2\u01d0\u01d1\7g\2\2\u01d1`\3\2\2\2\u01d2\u01d3\7E\2\2\u01d3"+
		"\u01d4\7q\2\2\u01d4\u01d5\7o\2\2\u01d5\u01d6\7r\2\2\u01d6\u01d7\7q\2\2"+
		"\u01d7\u01d8\7p\2\2\u01d8\u01d9\7g\2\2\u01d9\u01da\7p\2\2\u01da\u01db"+
		"\7v\2\2\u01dbb\3\2\2\2\u01dc\u01dd\7k\2\2\u01dd\u01de\7o\2\2\u01de\u01df"+
		"\7r\2\2\u01df\u01e0\7q\2\2\u01e0\u01e1\7t\2\2\u01e1\u01e2\7v\2\2\u01e2"+
		"d\3\2\2\2\u01e3\u01e4\7K\2\2\u01e4\u01e5\7h\2\2\u01e5f\3\2\2\2\u01e6\u01e7"+
		"\7C\2\2\u01e7\u01e8\7p\2\2\u01e8\u01e9\7f\2\2\u01e9h\3\2\2\2\u01ea\u01eb"+
		"\7\60\2\2\u01ebj\3\2\2\2\u01ec\u01ed\7H\2\2\u01ed\u01ee\7k\2\2\u01ee\u01ef"+
		"\7g\2\2\u01ef\u01f0\7n\2\2\u01f0\u01f1\7f\2\2\u01f1\u01f2\7H\2\2\u01f2"+
		"\u01f3\7q\2\2\u01f3\u01f4\7t\2\2\u01f4\u01f5\7o\2\2\u01f5\u01f6\7w\2\2"+
		"\u01f6\u01f7\7n\2\2\u01f7\u01f8\7c\2\2\u01f8l\3\2\2\2\u01f9\u01fa\7U\2"+
		"\2\u01fa\u01fb\7e\2\2\u01fb\u01fc\7t\2\2\u01fc\u01fd\7q\2\2\u01fd\u01fe"+
		"\7n\2\2\u01fe\u01ff\7n\2\2\u01ffn\3\2\2\2\u0200\u0201\7e\2\2\u0201\u0202"+
		"\7t\2\2\u0202\u0203\7g\2\2\u0203\u0204\7c\2\2\u0204\u0205\7v\2\2\u0205"+
		"\u0206\7g\2\2\u0206p\3\2\2\2\u0207\u0208\7>\2\2\u0208\u0209\7@\2\2\u0209"+
		"r\3\2\2\2\u020a\u020b\7U\2\2\u020b\u020c\7S\2\2\u020c\u020d\7N\2\2\u020d"+
		"t\3\2\2\2\u020e\u020f\7=\2\2\u020fv\3\2\2\2\u0210\u0211\7p\2\2\u0211\u0212"+
		"\7w\2\2\u0212\u0213\7o\2\2\u0213\u0214\7d\2\2\u0214\u0215\7g\2\2\u0215"+
		"\u0216\7t\2\2\u0216x\3\2\2\2\u0217\u0218\7O\2\2\u0218\u0219\7g\2\2\u0219"+
		"\u021a\7u\2\2\u021a\u021b\7u\2\2\u021b\u021c\7c\2\2\u021c\u021d\7i\2\2"+
		"\u021d\u021e\7g\2\2\u021ez\3\2\2\2\u021f\u0220\7k\2\2\u0220\u0221\7p\2"+
		"\2\u0221\u0222\7v\2\2\u0222\u0223\7g\2\2\u0223\u0224\7i\2\2\u0224\u0225"+
		"\7g\2\2\u0225\u0226\7t\2\2\u0226|\3\2\2\2\u0227\u0228\7u\2\2\u0228\u0229"+
		"\7v\2\2\u0229\u022a\7t\2\2\u022a\u022b\7k\2\2\u022b\u022c\7p\2\2\u022c"+
		"\u022d\7i\2\2\u022d~\3\2\2\2\u022e\u022f\7O\2\2\u022f\u0230\7g\2\2\u0230"+
		"\u0231\7p\2\2\u0231\u0232\7w\2\2\u0232\u0233\7P\2\2\u0233\u0234\7c\2\2"+
		"\u0234\u0235\7o\2\2\u0235\u0236\7g\2\2\u0236\u0080\3\2\2\2\u0237\u0238"+
		"\7H\2\2\u0238\u0239\7k\2\2\u0239\u023a\7n\2\2\u023a\u023b\7g\2\2\u023b"+
		"\u023c\7N\2\2\u023c\u023d\7c\2\2\u023d\u023e\7{\2\2\u023e\u023f\7q\2\2"+
		"\u023f\u0240\7w\2\2\u0240\u0241\7v\2\2\u0241\u0082\3\2\2\2\u0242\u0244"+
		"\7(\2\2\u0243\u0245\t\2\2\2\u0244\u0243\3\2\2\2\u0245\u0246\3\2\2\2\u0246"+
		"\u0244\3\2\2\2\u0246\u0247\3\2\2\2\u0247\u0084\3\2\2\2\u0248\u024a\7\'"+
		"\2\2\u0249\u024b\t\2\2\2\u024a\u0249\3\2\2\2\u024b\u024c\3\2\2\2\u024c"+
		"\u024a\3\2\2\2\u024c\u024d\3\2\2\2\u024d\u0086\3\2\2\2\u024e\u0252\t\3"+
		"\2\2\u024f\u0251\t\4\2\2\u0250\u024f\3\2\2\2\u0251\u0254\3\2\2\2\u0252"+
		"\u0250\3\2\2\2\u0252\u0253\3\2\2\2\u0253\u0088\3\2\2\2\u0254\u0252\3\2"+
		"\2\2\u0255\u0256\5\u008bF\2\u0256\u0258\7\60\2\2\u0257\u0259\t\5\2\2\u0258"+
		"\u0257\3\2\2\2\u0259\u025a\3\2\2\2\u025a\u0258\3\2\2\2\u025a\u025b\3\2"+
		"\2\2\u025b\u008a\3\2\2\2\u025c\u0265\7\62\2\2\u025d\u0261\4\63;\2\u025e"+
		"\u0260\4\62;\2\u025f\u025e\3\2\2\2\u0260\u0263\3\2\2\2\u0261\u025f\3\2"+
		"\2\2\u0261\u0262\3\2\2\2\u0262\u0265\3\2\2\2\u0263\u0261\3\2\2\2\u0264"+
		"\u025c\3\2\2\2\u0264\u025d\3\2\2\2\u0265\u008c\3\2\2\2\u0266\u026a\7$"+
		"\2\2\u0267\u0269\n\6\2\2\u0268\u0267\3\2\2\2\u0269\u026c\3\2\2\2\u026a"+
		"\u0268\3\2\2\2\u026a\u026b\3\2\2\2\u026b\u026d\3\2\2\2\u026c\u026a\3\2"+
		"\2\2\u026d\u026e\7$\2\2\u026e\u008e\3\2\2\2\u026f\u0270\7t\2\2\u0270\u0271"+
		"\7g\2\2\u0271\u0272\7o\2\2\u0272\u0276\3\2\2\2\u0273\u0275\n\7\2\2\u0274"+
		"\u0273\3\2\2\2\u0275\u0278\3\2\2\2\u0276\u0274\3\2\2\2\u0276\u0277\3\2"+
		"\2\2\u0277\u0279\3\2\2\2\u0278\u0276\3\2\2\2\u0279\u027a\bH\2\2\u027a"+
		"\u0090\3\2\2\2\u027b\u027c\7\61\2\2\u027c\u027d\7,\2\2\u027d\u0281\3\2"+
		"\2\2\u027e\u0280\13\2\2\2\u027f\u027e\3\2\2\2\u0280\u0283\3\2\2\2\u0281"+
		"\u0282\3\2\2\2\u0281\u027f\3\2\2\2\u0282\u0284\3\2\2\2\u0283\u0281\3\2"+
		"\2\2\u0284\u0285\7,\2\2\u0285\u0286\7\61\2\2\u0286\u0287\3\2\2\2\u0287"+
		"\u0288\bI\3\2\u0288\u0092\3\2\2\2\u0289\u028b\t\b\2\2\u028a\u0289\3\2"+
		"\2\2\u028b\u028c\3\2\2\2\u028c\u028a\3\2\2\2\u028c\u028d\3\2\2\2\u028d"+
		"\u028e\3\2\2\2\u028e\u028f\bJ\4\2\u028f\u0094\3\2\2\2\r\2\u0246\u024c"+
		"\u0252\u025a\u0261\u0264\u026a\u0276\u0281\u028c";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}