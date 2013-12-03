// Generated from /home/mquinn/evm/grammars/PeopleCode.g4 by ANTLR 4.1
package com.enterrupt.antlr4.generated;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PeopleCodeParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__9=1, T__8=2, T__7=3, T__6=4, T__5=5, T__4=6, T__3=7, T__2=8, T__1=9, 
		T__0=10, BOOLEAN=11, NUMBER=12, SYSTEM_VAR=13, CBUFFER_REF=14, OBJECT_REF=15, 
		FN_NAME=16, COMMENT=17, WS=18;
	public static final String[] tokenNames = {
		"<INVALID>", "'If'", "')'", "'.'", "','", "'('", "'='", "'MenuName'", 
		"';'", "'End-If'", "'Then'", "BOOLEAN", "NUMBER", "SYSTEM_VAR", "CBUFFER_REF", 
		"OBJECT_REF", "FN_NAME", "COMMENT", "WS"
	};
	public static final int
		RULE_program = 0, RULE_classicProg = 1, RULE_stmt = 2, RULE_expr = 3, 
		RULE_fn_call = 4, RULE_defn_ref = 5;
	public static final String[] ruleNames = {
		"program", "classicProg", "stmt", "expr", "fn_call", "defn_ref"
	};

	@Override
	public String getGrammarFileName() { return "PeopleCode.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public PeopleCodeParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public ClassicProgContext classicProg() {
			return getRuleContext(ClassicProgContext.class,0);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).exitProgram(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12); classicProg();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassicProgContext extends ParserRuleContext {
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public TerminalNode SYSTEM_VAR() { return getToken(PeopleCodeParser.SYSTEM_VAR, 0); }
		public TerminalNode CBUFFER_REF() { return getToken(PeopleCodeParser.CBUFFER_REF, 0); }
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public ClassicProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classicProg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).enterClassicProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).exitClassicProg(this);
		}
	}

	public final ClassicProgContext classicProg() throws RecognitionException {
		ClassicProgContext _localctx = new ClassicProgContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_classicProg);
		int _la;
		try {
			setState(24);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(17);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << CBUFFER_REF) | (1L << FN_NAME))) != 0)) {
					{
					{
					setState(14); stmt();
					}
					}
					setState(19);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(20); match(CBUFFER_REF);
				setState(21); match(6);
				setState(22); match(SYSTEM_VAR);
				setState(23); match(8);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtContext extends ParserRuleContext {
		public TerminalNode CBUFFER_REF() { return getToken(PeopleCodeParser.CBUFFER_REF, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Fn_callContext fn_call() {
			return getRuleContext(Fn_callContext.class,0);
		}
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).enterStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).exitStmt(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			switch (_input.LA(1)) {
			case 1:
				{
				setState(26); match(1);
				setState(27); expr(0);
				setState(28); match(10);
				setState(32);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << CBUFFER_REF) | (1L << FN_NAME))) != 0)) {
					{
					{
					setState(29); stmt();
					}
					}
					setState(34);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(35); match(9);
				}
				break;
			case FN_NAME:
				{
				setState(37); fn_call();
				}
				break;
			case CBUFFER_REF:
				{
				setState(38); match(CBUFFER_REF);
				setState(39); match(6);
				setState(40); expr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(43); match(8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public int _p;
		public Defn_refContext defn_ref() {
			return getRuleContext(Defn_refContext.class,0);
		}
		public TerminalNode BOOLEAN() { return getToken(PeopleCodeParser.BOOLEAN, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public TerminalNode SYSTEM_VAR() { return getToken(PeopleCodeParser.SYSTEM_VAR, 0); }
		public TerminalNode CBUFFER_REF() { return getToken(PeopleCodeParser.CBUFFER_REF, 0); }
		public Fn_callContext fn_call() {
			return getRuleContext(Fn_callContext.class,0);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode NUMBER() { return getToken(PeopleCodeParser.NUMBER, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public ExprContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).exitExpr(this);
		}
	}

	public final ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState, _p);
		ExprContext _prevctx = _localctx;
		int _startState = 6;
		enterRecursionRule(_localctx, RULE_expr);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			switch (_input.LA(1)) {
			case FN_NAME:
				{
				setState(46); fn_call();
				}
				break;
			case CBUFFER_REF:
				{
				setState(47); match(CBUFFER_REF);
				}
				break;
			case 7:
				{
				setState(48); defn_ref();
				}
				break;
			case SYSTEM_VAR:
				{
				setState(49); match(SYSTEM_VAR);
				}
				break;
			case NUMBER:
				{
				setState(50); match(NUMBER);
				}
				break;
			case BOOLEAN:
				{
				setState(51); match(BOOLEAN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(59);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprContext(_parentctx, _parentState, _p);
					pushNewRecursionContext(_localctx, _startState, RULE_expr);
					setState(54);
					if (!(6 >= _localctx._p)) throw new FailedPredicateException(this, "6 >= $_p");
					setState(55); match(6);
					setState(56); expr(7);
					}
					} 
				}
				setState(61);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Fn_callContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode FN_NAME() { return getToken(PeopleCodeParser.FN_NAME, 0); }
		public Fn_callContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fn_call; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).enterFn_call(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).exitFn_call(this);
		}
	}

	public final Fn_callContext fn_call() throws RecognitionException {
		Fn_callContext _localctx = new Fn_callContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_fn_call);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62); match(FN_NAME);
			setState(63); match(5);
			setState(64); expr(0);
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(65); match(4);
				setState(66); expr(0);
				}
				}
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(72); match(2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Defn_refContext extends ParserRuleContext {
		public TerminalNode OBJECT_REF() { return getToken(PeopleCodeParser.OBJECT_REF, 0); }
		public Defn_refContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defn_ref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).enterDefn_ref(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PeopleCodeListener ) ((PeopleCodeListener)listener).exitDefn_ref(this);
		}
	}

	public final Defn_refContext defn_ref() throws RecognitionException {
		Defn_refContext _localctx = new Defn_refContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_defn_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74); match(7);
			setState(75); match(3);
			setState(76); match(OBJECT_REF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 3: return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 6 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\24Q\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\3\7\3\22\n\3\f\3\16\3\25"+
		"\13\3\3\3\3\3\3\3\3\3\5\3\33\n\3\3\4\3\4\3\4\3\4\7\4!\n\4\f\4\16\4$\13"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4,\n\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\5\5\67\n\5\3\5\3\5\3\5\7\5<\n\5\f\5\16\5?\13\5\3\6\3\6\3\6\3\6\3\6"+
		"\7\6F\n\6\f\6\16\6I\13\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\2\b\2\4\6\b\n\f\2"+
		"\2V\2\16\3\2\2\2\4\32\3\2\2\2\6+\3\2\2\2\b\66\3\2\2\2\n@\3\2\2\2\fL\3"+
		"\2\2\2\16\17\5\4\3\2\17\3\3\2\2\2\20\22\5\6\4\2\21\20\3\2\2\2\22\25\3"+
		"\2\2\2\23\21\3\2\2\2\23\24\3\2\2\2\24\33\3\2\2\2\25\23\3\2\2\2\26\27\7"+
		"\20\2\2\27\30\7\b\2\2\30\31\7\17\2\2\31\33\7\n\2\2\32\23\3\2\2\2\32\26"+
		"\3\2\2\2\33\5\3\2\2\2\34\35\7\3\2\2\35\36\5\b\5\2\36\"\7\f\2\2\37!\5\6"+
		"\4\2 \37\3\2\2\2!$\3\2\2\2\" \3\2\2\2\"#\3\2\2\2#%\3\2\2\2$\"\3\2\2\2"+
		"%&\7\13\2\2&,\3\2\2\2\',\5\n\6\2()\7\20\2\2)*\7\b\2\2*,\5\b\5\2+\34\3"+
		"\2\2\2+\'\3\2\2\2+(\3\2\2\2,-\3\2\2\2-.\7\n\2\2.\7\3\2\2\2/\60\b\5\1\2"+
		"\60\67\5\n\6\2\61\67\7\20\2\2\62\67\5\f\7\2\63\67\7\17\2\2\64\67\7\16"+
		"\2\2\65\67\7\r\2\2\66/\3\2\2\2\66\61\3\2\2\2\66\62\3\2\2\2\66\63\3\2\2"+
		"\2\66\64\3\2\2\2\66\65\3\2\2\2\67=\3\2\2\289\6\5\2\39:\7\b\2\2:<\5\b\5"+
		"\2;8\3\2\2\2<?\3\2\2\2=;\3\2\2\2=>\3\2\2\2>\t\3\2\2\2?=\3\2\2\2@A\7\22"+
		"\2\2AB\7\7\2\2BG\5\b\5\2CD\7\6\2\2DF\5\b\5\2EC\3\2\2\2FI\3\2\2\2GE\3\2"+
		"\2\2GH\3\2\2\2HJ\3\2\2\2IG\3\2\2\2JK\7\4\2\2K\13\3\2\2\2LM\7\t\2\2MN\7"+
		"\5\2\2NO\7\21\2\2O\r\3\2\2\2\t\23\32\"+\66=G";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}