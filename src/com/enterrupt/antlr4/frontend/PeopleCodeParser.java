// Generated from /home/mquinn/evm/grammars/PeopleCode.g4 by ANTLR 4.1
package com.enterrupt.antlr4.frontend;
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
		T__8=1, T__7=2, T__6=3, T__5=4, T__4=5, T__3=6, T__2=7, T__1=8, T__0=9, 
		ReservedDefnWord=10, DecimalLiteral=11, IntegerLiteral=12, BooleanLiteral=13, 
		OBJECT_ID=14, FUNC_ID=15, SYSTEM_VAR=16, COMMENT=17, WS=18;
	public static final String[] tokenNames = {
		"<INVALID>", "'If'", "')'", "'.'", "','", "'('", "'='", "';'", "'End-If'", 
		"'Then'", "'MenuName'", "DecimalLiteral", "IntegerLiteral", "BooleanLiteral", 
		"OBJECT_ID", "FUNC_ID", "SYSTEM_VAR", "COMMENT", "WS"
	};
	public static final int
		RULE_program = 0, RULE_classicProg = 1, RULE_stmtList = 2, RULE_stmt = 3, 
		RULE_ifStmt = 4, RULE_stmtExpr = 5, RULE_exprList = 6, RULE_expr = 7, 
		RULE_literal = 8;
	public static final String[] ruleNames = {
		"program", "classicProg", "stmtList", "stmt", "ifStmt", "stmtExpr", "exprList", 
		"expr", "literal"
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18); classicProg();
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
		public StmtListContext stmtList() {
			return getRuleContext(StmtListContext.class,0);
		}
		public ClassicProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classicProg; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitClassicProg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassicProgContext classicProg() throws RecognitionException {
		ClassicProgContext _localctx = new ClassicProgContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_classicProg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20); stmtList();
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

	public static class StmtListContext extends ParserRuleContext {
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtListContext stmtList() throws RecognitionException {
		StmtListContext _localctx = new StmtListContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_stmtList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 5) | (1L << ReservedDefnWord) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << BooleanLiteral) | (1L << OBJECT_ID) | (1L << FUNC_ID) | (1L << SYSTEM_VAR))) != 0)) {
				{
				{
				setState(22); stmt();
				}
				}
				setState(27);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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
		public IfStmtContext ifStmt() {
			return getRuleContext(IfStmtContext.class,0);
		}
		public StmtExprContext stmtExpr() {
			return getRuleContext(StmtExprContext.class,0);
		}
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_stmt);
		int _la;
		try {
			setState(33);
			switch (_input.LA(1)) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(28); ifStmt();
				setState(30);
				_la = _input.LA(1);
				if (_la==7) {
					{
					setState(29); match(7);
					}
				}

				}
				break;
			case 5:
			case ReservedDefnWord:
			case DecimalLiteral:
			case IntegerLiteral:
			case BooleanLiteral:
			case OBJECT_ID:
			case FUNC_ID:
			case SYSTEM_VAR:
				enterOuterAlt(_localctx, 2);
				{
				setState(32); stmtExpr();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class IfStmtContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtListContext stmtList() {
			return getRuleContext(StmtListContext.class,0);
		}
		public IfStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitIfStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfStmtContext ifStmt() throws RecognitionException {
		IfStmtContext _localctx = new IfStmtContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_ifStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(35); match(1);
			setState(36); expr(0);
			setState(37); match(9);
			setState(38); stmtList();
			setState(39); match(8);
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

	public static class StmtExprContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtExprContext stmtExpr() throws RecognitionException {
		StmtExprContext _localctx = new StmtExprContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_stmtExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41); expr(0);
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

	public static class ExprListContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprListContext exprList() throws RecognitionException {
		ExprListContext _localctx = new ExprListContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_exprList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43); expr(0);
			setState(48);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(44); match(4);
				setState(45); expr(0);
				}
				}
				setState(50);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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
		public ExprContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public ExprContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
			this._p = ctx._p;
		}
	}
	public static class ObjDefnRefContext extends ExprContext {
		public TerminalNode OBJECT_ID() { return getToken(PeopleCodeParser.OBJECT_ID, 0); }
		public TerminalNode ReservedDefnWord() { return getToken(PeopleCodeParser.ReservedDefnWord, 0); }
		public ObjDefnRefContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitObjDefnRef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SystemVarContext extends ExprContext {
		public TerminalNode SYSTEM_VAR() { return getToken(PeopleCodeParser.SYSTEM_VAR, 0); }
		public SystemVarContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitSystemVar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenthesizedExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParenthesizedExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitParenthesizedExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignStmtContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public AssignStmtContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitAssignStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CBufferRefContext extends ExprContext {
		public List<TerminalNode> OBJECT_ID() { return getTokens(PeopleCodeParser.OBJECT_ID); }
		public TerminalNode OBJECT_ID(int i) {
			return getToken(PeopleCodeParser.OBJECT_ID, i);
		}
		public CBufferRefContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitCBufferRef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprLiteralContext extends ExprContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public ExprLiteralContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FnCallContext extends ExprContext {
		public ExprListContext exprList() {
			return getRuleContext(ExprListContext.class,0);
		}
		public TerminalNode FUNC_ID() { return getToken(PeopleCodeParser.FUNC_ID, 0); }
		public FnCallContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitFnCall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ComparisonContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ComparisonContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitComparison(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState, _p);
		ExprContext _prevctx = _localctx;
		int _startState = 14;
		enterRecursionRule(_localctx, RULE_expr);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			switch (_input.LA(1)) {
			case 5:
				{
				_localctx = new ParenthesizedExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(52); match(5);
				setState(53); expr(0);
				setState(54); match(2);
				}
				break;
			case ReservedDefnWord:
				{
				_localctx = new ObjDefnRefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(56); match(ReservedDefnWord);
				setState(57); match(3);
				setState(58); match(OBJECT_ID);
				}
				break;
			case DecimalLiteral:
			case IntegerLiteral:
			case BooleanLiteral:
				{
				_localctx = new ExprLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(59); literal();
				}
				break;
			case OBJECT_ID:
				{
				_localctx = new CBufferRefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(60); match(OBJECT_ID);
				setState(65);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				while ( _alt!=2 && _alt!=-1 ) {
					if ( _alt==1 ) {
						{
						{
						setState(61); match(3);
						setState(62); match(OBJECT_ID);
						}
						} 
					}
					setState(67);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				}
				}
				break;
			case SYSTEM_VAR:
				{
				_localctx = new SystemVarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(68); match(SYSTEM_VAR);
				}
				break;
			case FUNC_ID:
				{
				_localctx = new FnCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(69); match(FUNC_ID);
				setState(70); match(5);
				setState(72);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 5) | (1L << ReservedDefnWord) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << BooleanLiteral) | (1L << OBJECT_ID) | (1L << FUNC_ID) | (1L << SYSTEM_VAR))) != 0)) {
					{
					setState(71); exprList();
					}
				}

				setState(74); match(2);
				setState(76);
				switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
				case 1:
					{
					setState(75); match(7);
					}
					break;
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(90);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(88);
					switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
					case 1:
						{
						_localctx = new ComparisonContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(80);
						if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
						setState(81); match(6);
						setState(82); expr(2);
						}
						break;

					case 2:
						{
						_localctx = new AssignStmtContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(83);
						if (!(2 >= _localctx._p)) throw new FailedPredicateException(this, "2 >= $_p");
						setState(84); match(6);
						setState(85); expr(0);
						setState(86); match(7);
						}
						break;
					}
					} 
				}
				setState(92);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
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

	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode IntegerLiteral() { return getToken(PeopleCodeParser.IntegerLiteral, 0); }
		public TerminalNode DecimalLiteral() { return getToken(PeopleCodeParser.DecimalLiteral, 0); }
		public TerminalNode BooleanLiteral() { return getToken(PeopleCodeParser.BooleanLiteral, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << BooleanLiteral))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
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
		case 7: return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 1 >= _localctx._p;

		case 1: return 2 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\24b\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\3\3"+
		"\3\3\3\4\7\4\32\n\4\f\4\16\4\35\13\4\3\5\3\5\5\5!\n\5\3\5\5\5$\n\5\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\7\b\61\n\b\f\b\16\b\64\13\b\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\7\tB\n\t\f\t\16\tE\13\t"+
		"\3\t\3\t\3\t\3\t\5\tK\n\t\3\t\3\t\5\tO\n\t\5\tQ\n\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\7\t[\n\t\f\t\16\t^\13\t\3\n\3\n\3\n\2\13\2\4\6\b\n\f\16"+
		"\20\22\2\3\3\2\r\17f\2\24\3\2\2\2\4\26\3\2\2\2\6\33\3\2\2\2\b#\3\2\2\2"+
		"\n%\3\2\2\2\f+\3\2\2\2\16-\3\2\2\2\20P\3\2\2\2\22_\3\2\2\2\24\25\5\4\3"+
		"\2\25\3\3\2\2\2\26\27\5\6\4\2\27\5\3\2\2\2\30\32\5\b\5\2\31\30\3\2\2\2"+
		"\32\35\3\2\2\2\33\31\3\2\2\2\33\34\3\2\2\2\34\7\3\2\2\2\35\33\3\2\2\2"+
		"\36 \5\n\6\2\37!\7\t\2\2 \37\3\2\2\2 !\3\2\2\2!$\3\2\2\2\"$\5\f\7\2#\36"+
		"\3\2\2\2#\"\3\2\2\2$\t\3\2\2\2%&\7\3\2\2&\'\5\20\t\2\'(\7\13\2\2()\5\6"+
		"\4\2)*\7\n\2\2*\13\3\2\2\2+,\5\20\t\2,\r\3\2\2\2-\62\5\20\t\2./\7\6\2"+
		"\2/\61\5\20\t\2\60.\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2"+
		"\63\17\3\2\2\2\64\62\3\2\2\2\65\66\b\t\1\2\66\67\7\7\2\2\678\5\20\t\2"+
		"89\7\4\2\29Q\3\2\2\2:;\7\f\2\2;<\7\5\2\2<Q\7\20\2\2=Q\5\22\n\2>C\7\20"+
		"\2\2?@\7\5\2\2@B\7\20\2\2A?\3\2\2\2BE\3\2\2\2CA\3\2\2\2CD\3\2\2\2DQ\3"+
		"\2\2\2EC\3\2\2\2FQ\7\22\2\2GH\7\21\2\2HJ\7\7\2\2IK\5\16\b\2JI\3\2\2\2"+
		"JK\3\2\2\2KL\3\2\2\2LN\7\4\2\2MO\7\t\2\2NM\3\2\2\2NO\3\2\2\2OQ\3\2\2\2"+
		"P\65\3\2\2\2P:\3\2\2\2P=\3\2\2\2P>\3\2\2\2PF\3\2\2\2PG\3\2\2\2Q\\\3\2"+
		"\2\2RS\6\t\2\3ST\7\b\2\2T[\5\20\t\2UV\6\t\3\3VW\7\b\2\2WX\5\20\t\2XY\7"+
		"\t\2\2Y[\3\2\2\2ZR\3\2\2\2ZU\3\2\2\2[^\3\2\2\2\\Z\3\2\2\2\\]\3\2\2\2]"+
		"\21\3\2\2\2^\\\3\2\2\2_`\t\2\2\2`\23\3\2\2\2\f\33 #\62CJNPZ\\";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}