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
		T__42=1, T__41=2, T__40=3, T__39=4, T__38=5, T__37=6, T__36=7, T__35=8, 
		T__34=9, T__33=10, T__32=11, T__31=12, T__30=13, T__29=14, T__28=15, T__27=16, 
		T__26=17, T__25=18, T__24=19, T__23=20, T__22=21, T__21=22, T__20=23, 
		T__19=24, T__18=25, T__17=26, T__16=27, T__15=28, T__14=29, T__13=30, 
		T__12=31, T__11=32, T__10=33, T__9=34, T__8=35, T__7=36, T__6=37, T__5=38, 
		T__4=39, T__3=40, T__2=41, T__1=42, T__0=43, GENERIC_ID=44, DecimalLiteral=45, 
		IntegerLiteral=46, StringLiteral=47, VAR_ID=48, SYS_VAR_ID=49, REM=50, 
		COMMENT=51, WS=52;
	public static final String[] tokenNames = {
		"<INVALID>", "'Field'", "'True'", "'End-For'", "','", "'('", "'To'", "'false'", 
		"'PeopleCode'", "'Declare'", "'When-Other'", "'Evaluate'", "'Else'", "'Panel'", 
		"'When'", "'Rowset'", "'boolean'", "'For'", "'true'", "'Component'", "'And'", 
		"'Global'", "'If'", "'False'", "'End-Evaluate'", "')'", "'.'", "'Record'", 
		"'FieldFormula'", "'@'", "'<>'", "'='", "';'", "'End-If'", "'number'", 
		"'Function'", "'Exit'", "'Local'", "'Break'", "'integer'", "'string'", 
		"'MenuName'", "'|'", "'Then'", "GENERIC_ID", "DecimalLiteral", "IntegerLiteral", 
		"StringLiteral", "VAR_ID", "SYS_VAR_ID", "REM", "COMMENT", "WS"
	};
	public static final int
		RULE_program = 0, RULE_classicProg = 1, RULE_stmtList = 2, RULE_stmt = 3, 
		RULE_expr = 4, RULE_exprList = 5, RULE_varDecl = 6, RULE_varScopeModifier = 7, 
		RULE_varTypeModifier = 8, RULE_funcImport = 9, RULE_defnPath = 10, RULE_event = 11, 
		RULE_ifConstruct = 12, RULE_forConstruct = 13, RULE_evaluateConstruct = 14, 
		RULE_defnKeyword = 15, RULE_relop = 16, RULE_literal = 17, RULE_booleanLiteral = 18, 
		RULE_definitionLiteral = 19, RULE_id = 20;
	public static final String[] ruleNames = {
		"program", "classicProg", "stmtList", "stmt", "expr", "exprList", "varDecl", 
		"varScopeModifier", "varTypeModifier", "funcImport", "defnPath", "event", 
		"ifConstruct", "forConstruct", "evaluateConstruct", "defnKeyword", "relop", 
		"literal", "booleanLiteral", "definitionLiteral", "id"
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
			setState(42); classicProg();
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
			setState(44); stmtList();
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
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 5) | (1L << 7) | (1L << 9) | (1L << 11) | (1L << 13) | (1L << 17) | (1L << 18) | (1L << 19) | (1L << 21) | (1L << 22) | (1L << 23) | (1L << 27) | (1L << 29) | (1L << 36) | (1L << 37) | (1L << 38) | (1L << 41) | (1L << GENERIC_ID) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << StringLiteral) | (1L << VAR_ID) | (1L << SYS_VAR_ID))) != 0)) {
				{
				{
				setState(46); stmt();
				setState(47); match(32);
				}
				}
				setState(53);
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
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
	 
		public StmtContext() { }
		public void copyFrom(StmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StmtEvaluateContext extends StmtContext {
		public EvaluateConstructContext evaluateConstruct() {
			return getRuleContext(EvaluateConstructContext.class,0);
		}
		public StmtEvaluateContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtEvaluate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtBreakContext extends StmtContext {
		public StmtBreakContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtBreak(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtAssignContext extends StmtContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public StmtAssignContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtAssign(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtVarDeclContext extends StmtContext {
		public VarDeclContext varDecl() {
			return getRuleContext(VarDeclContext.class,0);
		}
		public StmtVarDeclContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtVarDecl(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtFuncImportContext extends StmtContext {
		public FuncImportContext funcImport() {
			return getRuleContext(FuncImportContext.class,0);
		}
		public StmtFuncImportContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtFuncImport(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtFnCallContext extends StmtContext {
		public ExprListContext exprList() {
			return getRuleContext(ExprListContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtFnCallContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtFnCall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtForContext extends StmtContext {
		public ForConstructContext forConstruct() {
			return getRuleContext(ForConstructContext.class,0);
		}
		public StmtForContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtFor(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtIfContext extends StmtContext {
		public IfConstructContext ifConstruct() {
			return getRuleContext(IfConstructContext.class,0);
		}
		public StmtIfContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtIf(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtExitContext extends StmtContext {
		public StmtExitContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtExit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_stmt);
		int _la;
		try {
			setState(72);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new StmtFuncImportContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(54); funcImport();
				}
				break;

			case 2:
				_localctx = new StmtVarDeclContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(55); varDecl();
				}
				break;

			case 3:
				_localctx = new StmtIfContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(56); ifConstruct();
				}
				break;

			case 4:
				_localctx = new StmtForContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(57); forConstruct();
				}
				break;

			case 5:
				_localctx = new StmtEvaluateContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(58); evaluateConstruct();
				}
				break;

			case 6:
				_localctx = new StmtExitContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(59); match(36);
				}
				break;

			case 7:
				_localctx = new StmtBreakContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(60); match(38);
				}
				break;

			case 8:
				_localctx = new StmtAssignContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(61); expr(0);
				setState(62); match(31);
				setState(63); expr(0);
				}
				break;

			case 9:
				_localctx = new StmtFnCallContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(65); expr(0);
				setState(66); match(5);
				setState(68);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 5) | (1L << 7) | (1L << 13) | (1L << 18) | (1L << 19) | (1L << 23) | (1L << 27) | (1L << 29) | (1L << 41) | (1L << GENERIC_ID) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << StringLiteral) | (1L << VAR_ID) | (1L << SYS_VAR_ID))) != 0)) {
					{
					setState(67); exprList();
					}
				}

				setState(70); match(25);
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
	public static class ExprParenthesizedContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprParenthesizedContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprParenthesized(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprConcatenateContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprConcatenateContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprConcatenate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprFnCallContext extends ExprContext {
		public ExprListContext exprList() {
			return getRuleContext(ExprListContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprFnCallContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprFnCall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprIdContext extends ExprContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ExprIdContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprId(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprBooleanContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprBooleanContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprBoolean(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprDynamicReferenceContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprDynamicReferenceContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprDynamicReference(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprStaticReferenceContext extends ExprContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprStaticReferenceContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprStaticReference(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprEqualityContext extends ExprContext {
		public RelopContext relop() {
			return getRuleContext(RelopContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprEqualityContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprEquality(this);
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

	public final ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState, _p);
		ExprContext _prevctx = _localctx;
		int _startState = 8;
		enterRecursionRule(_localctx, RULE_expr);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			switch (_input.LA(1)) {
			case 5:
				{
				_localctx = new ExprParenthesizedContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(75); match(5);
				setState(76); expr(0);
				setState(77); match(25);
				}
				break;
			case 29:
				{
				_localctx = new ExprDynamicReferenceContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(79); match(29);
				setState(80); match(5);
				setState(81); expr(0);
				setState(82); match(25);
				}
				break;
			case 1:
			case 2:
			case 7:
			case 13:
			case 18:
			case 19:
			case 23:
			case 27:
			case 41:
			case DecimalLiteral:
			case IntegerLiteral:
			case StringLiteral:
				{
				_localctx = new ExprLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(84); literal();
				}
				break;
			case GENERIC_ID:
			case VAR_ID:
			case SYS_VAR_ID:
				{
				_localctx = new ExprIdContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(85); id();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(109);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(107);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						_localctx = new ExprBooleanContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(88);
						if (!(3 >= _localctx._p)) throw new FailedPredicateException(this, "3 >= $_p");
						{
						setState(89); match(20);
						}
						setState(90); expr(4);
						}
						break;

					case 2:
						{
						_localctx = new ExprConcatenateContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(91);
						if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
						{
						setState(92); match(42);
						}
						setState(93); expr(2);
						}
						break;

					case 3:
						{
						_localctx = new ExprStaticReferenceContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(94);
						if (!(5 >= _localctx._p)) throw new FailedPredicateException(this, "5 >= $_p");
						setState(95); match(26);
						setState(96); id();
						}
						break;

					case 4:
						{
						_localctx = new ExprFnCallContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(97);
						if (!(4 >= _localctx._p)) throw new FailedPredicateException(this, "4 >= $_p");
						setState(98); match(5);
						setState(100);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 5) | (1L << 7) | (1L << 13) | (1L << 18) | (1L << 19) | (1L << 23) | (1L << 27) | (1L << 29) | (1L << 41) | (1L << GENERIC_ID) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << StringLiteral) | (1L << VAR_ID) | (1L << SYS_VAR_ID))) != 0)) {
							{
							setState(99); exprList();
							}
						}

						setState(102); match(25);
						}
						break;

					case 5:
						{
						_localctx = new ExprEqualityContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(103);
						if (!(2 >= _localctx._p)) throw new FailedPredicateException(this, "2 >= $_p");
						setState(104); relop();
						setState(105); expr(0);
						}
						break;
					}
					} 
				}
				setState(111);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
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
		enterRule(_localctx, 10, RULE_exprList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112); expr(0);
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(113); match(4);
				setState(114); expr(0);
				}
				}
				setState(119);
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

	public static class VarDeclContext extends ParserRuleContext {
		public List<TerminalNode> VAR_ID() { return getTokens(PeopleCodeParser.VAR_ID); }
		public VarTypeModifierContext varTypeModifier() {
			return getRuleContext(VarTypeModifierContext.class,0);
		}
		public VarScopeModifierContext varScopeModifier() {
			return getRuleContext(VarScopeModifierContext.class,0);
		}
		public TerminalNode VAR_ID(int i) {
			return getToken(PeopleCodeParser.VAR_ID, i);
		}
		public VarDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitVarDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDeclContext varDecl() throws RecognitionException {
		VarDeclContext _localctx = new VarDeclContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_varDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120); varScopeModifier();
			setState(121); varTypeModifier();
			setState(122); match(VAR_ID);
			setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(123); match(4);
				setState(124); match(VAR_ID);
				}
				}
				setState(129);
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

	public static class VarScopeModifierContext extends ParserRuleContext {
		public VarScopeModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varScopeModifier; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitVarScopeModifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarScopeModifierContext varScopeModifier() throws RecognitionException {
		VarScopeModifierContext _localctx = new VarScopeModifierContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_varScopeModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 19) | (1L << 21) | (1L << 37))) != 0)) ) {
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

	public static class VarTypeModifierContext extends ParserRuleContext {
		public VarTypeModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varTypeModifier; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitVarTypeModifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarTypeModifierContext varTypeModifier() throws RecognitionException {
		VarTypeModifierContext _localctx = new VarTypeModifierContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_varTypeModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 15) | (1L << 16) | (1L << 27) | (1L << 34) | (1L << 39) | (1L << 40))) != 0)) ) {
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

	public static class FuncImportContext extends ParserRuleContext {
		public DefnPathContext defnPath() {
			return getRuleContext(DefnPathContext.class,0);
		}
		public TerminalNode GENERIC_ID() { return getToken(PeopleCodeParser.GENERIC_ID, 0); }
		public EventContext event() {
			return getRuleContext(EventContext.class,0);
		}
		public FuncImportContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcImport; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitFuncImport(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncImportContext funcImport() throws RecognitionException {
		FuncImportContext _localctx = new FuncImportContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_funcImport);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134); match(9);
			setState(135); match(35);
			setState(136); match(GENERIC_ID);
			setState(137); match(8);
			setState(138); defnPath();
			setState(139); event();
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

	public static class DefnPathContext extends ParserRuleContext {
		public List<TerminalNode> GENERIC_ID() { return getTokens(PeopleCodeParser.GENERIC_ID); }
		public TerminalNode GENERIC_ID(int i) {
			return getToken(PeopleCodeParser.GENERIC_ID, i);
		}
		public DefnPathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defnPath; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitDefnPath(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefnPathContext defnPath() throws RecognitionException {
		DefnPathContext _localctx = new DefnPathContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_defnPath);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141); match(GENERIC_ID);
			setState(146);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==26) {
				{
				{
				setState(142); match(26);
				setState(143); match(GENERIC_ID);
				}
				}
				setState(148);
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

	public static class EventContext extends ParserRuleContext {
		public EventContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_event; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitEvent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventContext event() throws RecognitionException {
		EventContext _localctx = new EventContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_event);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149); match(28);
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

	public static class IfConstructContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtListContext stmtList(int i) {
			return getRuleContext(StmtListContext.class,i);
		}
		public List<StmtListContext> stmtList() {
			return getRuleContexts(StmtListContext.class);
		}
		public IfConstructContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifConstruct; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitIfConstruct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfConstructContext ifConstruct() throws RecognitionException {
		IfConstructContext _localctx = new IfConstructContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_ifConstruct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151); match(22);
			setState(152); expr(0);
			setState(153); match(43);
			setState(154); stmtList();
			setState(157);
			_la = _input.LA(1);
			if (_la==12) {
				{
				setState(155); match(12);
				setState(156); stmtList();
				}
			}

			setState(159); match(33);
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

	public static class ForConstructContext extends ParserRuleContext {
		public TerminalNode VAR_ID() { return getToken(PeopleCodeParser.VAR_ID, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public StmtListContext stmtList() {
			return getRuleContext(StmtListContext.class,0);
		}
		public ForConstructContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forConstruct; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitForConstruct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForConstructContext forConstruct() throws RecognitionException {
		ForConstructContext _localctx = new ForConstructContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_forConstruct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(161); match(17);
			setState(162); match(VAR_ID);
			setState(163); match(31);
			setState(164); expr(0);
			setState(165); match(6);
			setState(166); expr(0);
			setState(167); stmtList();
			setState(168); match(3);
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

	public static class EvaluateConstructContext extends ParserRuleContext {
		public List<RelopContext> relop() {
			return getRuleContexts(RelopContext.class);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public StmtListContext stmtList(int i) {
			return getRuleContext(StmtListContext.class,i);
		}
		public List<StmtListContext> stmtList() {
			return getRuleContexts(StmtListContext.class);
		}
		public RelopContext relop(int i) {
			return getRuleContext(RelopContext.class,i);
		}
		public EvaluateConstructContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_evaluateConstruct; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitEvaluateConstruct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EvaluateConstructContext evaluateConstruct() throws RecognitionException {
		EvaluateConstructContext _localctx = new EvaluateConstructContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_evaluateConstruct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170); match(11);
			setState(171); expr(0);
			setState(179); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(172); match(14);
				setState(174);
				_la = _input.LA(1);
				if (_la==30 || _la==31) {
					{
					setState(173); relop();
					}
				}

				setState(176); expr(0);
				setState(177); stmtList();
				}
				}
				setState(181); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==14 );
			setState(185);
			_la = _input.LA(1);
			if (_la==10) {
				{
				setState(183); match(10);
				setState(184); stmtList();
				}
			}

			setState(187); match(24);
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

	public static class DefnKeywordContext extends ParserRuleContext {
		public DefnKeywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defnKeyword; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitDefnKeyword(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefnKeywordContext defnKeyword() throws RecognitionException {
		DefnKeywordContext _localctx = new DefnKeywordContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_defnKeyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 13) | (1L << 19) | (1L << 27) | (1L << 41))) != 0)) ) {
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

	public static class RelopContext extends ParserRuleContext {
		public RelopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relop; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitRelop(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelopContext relop() throws RecognitionException {
		RelopContext _localctx = new RelopContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_relop);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			_la = _input.LA(1);
			if ( !(_la==30 || _la==31) ) {
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

	public static class LiteralContext extends ParserRuleContext {
		public DefinitionLiteralContext definitionLiteral() {
			return getRuleContext(DefinitionLiteralContext.class,0);
		}
		public TerminalNode IntegerLiteral() { return getToken(PeopleCodeParser.IntegerLiteral, 0); }
		public TerminalNode StringLiteral() { return getToken(PeopleCodeParser.StringLiteral, 0); }
		public TerminalNode DecimalLiteral() { return getToken(PeopleCodeParser.DecimalLiteral, 0); }
		public BooleanLiteralContext booleanLiteral() {
			return getRuleContext(BooleanLiteralContext.class,0);
		}
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
		enterRule(_localctx, 34, RULE_literal);
		try {
			setState(198);
			switch (_input.LA(1)) {
			case DecimalLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(193); match(DecimalLiteral);
				}
				break;
			case IntegerLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(194); match(IntegerLiteral);
				}
				break;
			case 1:
			case 13:
			case 19:
			case 27:
			case 41:
				enterOuterAlt(_localctx, 3);
				{
				setState(195); definitionLiteral();
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 4);
				{
				setState(196); match(StringLiteral);
				}
				break;
			case 2:
			case 7:
			case 18:
			case 23:
				enterOuterAlt(_localctx, 5);
				{
				setState(197); booleanLiteral();
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

	public static class BooleanLiteralContext extends ParserRuleContext {
		public BooleanLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitBooleanLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleanLiteralContext booleanLiteral() throws RecognitionException {
		BooleanLiteralContext _localctx = new BooleanLiteralContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_booleanLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 2) | (1L << 7) | (1L << 18) | (1L << 23))) != 0)) ) {
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

	public static class DefinitionLiteralContext extends ParserRuleContext {
		public TerminalNode GENERIC_ID() { return getToken(PeopleCodeParser.GENERIC_ID, 0); }
		public DefnKeywordContext defnKeyword() {
			return getRuleContext(DefnKeywordContext.class,0);
		}
		public DefinitionLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_definitionLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitDefinitionLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefinitionLiteralContext definitionLiteral() throws RecognitionException {
		DefinitionLiteralContext _localctx = new DefinitionLiteralContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_definitionLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202); defnKeyword();
			setState(203); match(26);
			setState(204); match(GENERIC_ID);
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

	public static class IdContext extends ParserRuleContext {
		public TerminalNode SYS_VAR_ID() { return getToken(PeopleCodeParser.SYS_VAR_ID, 0); }
		public TerminalNode VAR_ID() { return getToken(PeopleCodeParser.VAR_ID, 0); }
		public TerminalNode GENERIC_ID() { return getToken(PeopleCodeParser.GENERIC_ID, 0); }
		public IdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdContext id() throws RecognitionException {
		IdContext _localctx = new IdContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GENERIC_ID) | (1L << VAR_ID) | (1L << SYS_VAR_ID))) != 0)) ) {
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
		case 4: return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 3 >= _localctx._p;

		case 1: return 1 >= _localctx._p;

		case 2: return 5 >= _localctx._p;

		case 3: return 4 >= _localctx._p;

		case 4: return 2 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\66\u00d3\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2\3\2\3\3\3\3\3\4\3\4\3\4\7"+
		"\4\64\n\4\f\4\16\4\67\13\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\5\5G\n\5\3\5\3\5\5\5K\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\5\6Y\n\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\5\6g\n\6\3\6\3\6\3\6\3\6\3\6\7\6n\n\6\f\6\16\6q\13\6\3\7\3\7\3\7"+
		"\7\7v\n\7\f\7\16\7y\13\7\3\b\3\b\3\b\3\b\3\b\7\b\u0080\n\b\f\b\16\b\u0083"+
		"\13\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\7"+
		"\f\u0093\n\f\f\f\16\f\u0096\13\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\5\16\u00a0\n\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\5\20\u00b1\n\20\3\20\3\20\3\20\6\20\u00b6\n\20\r"+
		"\20\16\20\u00b7\3\20\3\20\5\20\u00bc\n\20\3\20\3\20\3\21\3\21\3\22\3\22"+
		"\3\23\3\23\3\23\3\23\3\23\5\23\u00c9\n\23\3\24\3\24\3\25\3\25\3\25\3\25"+
		"\3\26\3\26\3\26\2\27\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*\2\b"+
		"\5\2\25\25\27\27\'\'\7\2\3\3\21\22\35\35$$)*\7\2\3\3\17\17\25\25\35\35"+
		"++\3\2 !\6\2\4\4\t\t\24\24\31\31\4\2..\62\63\u00db\2,\3\2\2\2\4.\3\2\2"+
		"\2\6\65\3\2\2\2\bJ\3\2\2\2\nX\3\2\2\2\fr\3\2\2\2\16z\3\2\2\2\20\u0084"+
		"\3\2\2\2\22\u0086\3\2\2\2\24\u0088\3\2\2\2\26\u008f\3\2\2\2\30\u0097\3"+
		"\2\2\2\32\u0099\3\2\2\2\34\u00a3\3\2\2\2\36\u00ac\3\2\2\2 \u00bf\3\2\2"+
		"\2\"\u00c1\3\2\2\2$\u00c8\3\2\2\2&\u00ca\3\2\2\2(\u00cc\3\2\2\2*\u00d0"+
		"\3\2\2\2,-\5\4\3\2-\3\3\2\2\2./\5\6\4\2/\5\3\2\2\2\60\61\5\b\5\2\61\62"+
		"\7\"\2\2\62\64\3\2\2\2\63\60\3\2\2\2\64\67\3\2\2\2\65\63\3\2\2\2\65\66"+
		"\3\2\2\2\66\7\3\2\2\2\67\65\3\2\2\28K\5\24\13\29K\5\16\b\2:K\5\32\16\2"+
		";K\5\34\17\2<K\5\36\20\2=K\7&\2\2>K\7(\2\2?@\5\n\6\2@A\7!\2\2AB\5\n\6"+
		"\2BK\3\2\2\2CD\5\n\6\2DF\7\7\2\2EG\5\f\7\2FE\3\2\2\2FG\3\2\2\2GH\3\2\2"+
		"\2HI\7\33\2\2IK\3\2\2\2J8\3\2\2\2J9\3\2\2\2J:\3\2\2\2J;\3\2\2\2J<\3\2"+
		"\2\2J=\3\2\2\2J>\3\2\2\2J?\3\2\2\2JC\3\2\2\2K\t\3\2\2\2LM\b\6\1\2MN\7"+
		"\7\2\2NO\5\n\6\2OP\7\33\2\2PY\3\2\2\2QR\7\37\2\2RS\7\7\2\2ST\5\n\6\2T"+
		"U\7\33\2\2UY\3\2\2\2VY\5$\23\2WY\5*\26\2XL\3\2\2\2XQ\3\2\2\2XV\3\2\2\2"+
		"XW\3\2\2\2Yo\3\2\2\2Z[\6\6\2\3[\\\7\26\2\2\\n\5\n\6\2]^\6\6\3\3^_\7,\2"+
		"\2_n\5\n\6\2`a\6\6\4\3ab\7\34\2\2bn\5*\26\2cd\6\6\5\3df\7\7\2\2eg\5\f"+
		"\7\2fe\3\2\2\2fg\3\2\2\2gh\3\2\2\2hn\7\33\2\2ij\6\6\6\3jk\5\"\22\2kl\5"+
		"\n\6\2ln\3\2\2\2mZ\3\2\2\2m]\3\2\2\2m`\3\2\2\2mc\3\2\2\2mi\3\2\2\2nq\3"+
		"\2\2\2om\3\2\2\2op\3\2\2\2p\13\3\2\2\2qo\3\2\2\2rw\5\n\6\2st\7\6\2\2t"+
		"v\5\n\6\2us\3\2\2\2vy\3\2\2\2wu\3\2\2\2wx\3\2\2\2x\r\3\2\2\2yw\3\2\2\2"+
		"z{\5\20\t\2{|\5\22\n\2|\u0081\7\62\2\2}~\7\6\2\2~\u0080\7\62\2\2\177}"+
		"\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082"+
		"\17\3\2\2\2\u0083\u0081\3\2\2\2\u0084\u0085\t\2\2\2\u0085\21\3\2\2\2\u0086"+
		"\u0087\t\3\2\2\u0087\23\3\2\2\2\u0088\u0089\7\13\2\2\u0089\u008a\7%\2"+
		"\2\u008a\u008b\7.\2\2\u008b\u008c\7\n\2\2\u008c\u008d\5\26\f\2\u008d\u008e"+
		"\5\30\r\2\u008e\25\3\2\2\2\u008f\u0094\7.\2\2\u0090\u0091\7\34\2\2\u0091"+
		"\u0093\7.\2\2\u0092\u0090\3\2\2\2\u0093\u0096\3\2\2\2\u0094\u0092\3\2"+
		"\2\2\u0094\u0095\3\2\2\2\u0095\27\3\2\2\2\u0096\u0094\3\2\2\2\u0097\u0098"+
		"\7\36\2\2\u0098\31\3\2\2\2\u0099\u009a\7\30\2\2\u009a\u009b\5\n\6\2\u009b"+
		"\u009c\7-\2\2\u009c\u009f\5\6\4\2\u009d\u009e\7\16\2\2\u009e\u00a0\5\6"+
		"\4\2\u009f\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1"+
		"\u00a2\7#\2\2\u00a2\33\3\2\2\2\u00a3\u00a4\7\23\2\2\u00a4\u00a5\7\62\2"+
		"\2\u00a5\u00a6\7!\2\2\u00a6\u00a7\5\n\6\2\u00a7\u00a8\7\b\2\2\u00a8\u00a9"+
		"\5\n\6\2\u00a9\u00aa\5\6\4\2\u00aa\u00ab\7\5\2\2\u00ab\35\3\2\2\2\u00ac"+
		"\u00ad\7\r\2\2\u00ad\u00b5\5\n\6\2\u00ae\u00b0\7\20\2\2\u00af\u00b1\5"+
		"\"\22\2\u00b0\u00af\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2"+
		"\u00b3\5\n\6\2\u00b3\u00b4\5\6\4\2\u00b4\u00b6\3\2\2\2\u00b5\u00ae\3\2"+
		"\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8"+
		"\u00bb\3\2\2\2\u00b9\u00ba\7\f\2\2\u00ba\u00bc\5\6\4\2\u00bb\u00b9\3\2"+
		"\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00be\7\32\2\2\u00be"+
		"\37\3\2\2\2\u00bf\u00c0\t\4\2\2\u00c0!\3\2\2\2\u00c1\u00c2\t\5\2\2\u00c2"+
		"#\3\2\2\2\u00c3\u00c9\7/\2\2\u00c4\u00c9\7\60\2\2\u00c5\u00c9\5(\25\2"+
		"\u00c6\u00c9\7\61\2\2\u00c7\u00c9\5&\24\2\u00c8\u00c3\3\2\2\2\u00c8\u00c4"+
		"\3\2\2\2\u00c8\u00c5\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c7\3\2\2\2\u00c9"+
		"%\3\2\2\2\u00ca\u00cb\t\6\2\2\u00cb\'\3\2\2\2\u00cc\u00cd\5 \21\2\u00cd"+
		"\u00ce\7\34\2\2\u00ce\u00cf\7.\2\2\u00cf)\3\2\2\2\u00d0\u00d1\t\7\2\2"+
		"\u00d1+\3\2\2\2\21\65FJXfmow\u0081\u0094\u009f\u00b0\u00b7\u00bb\u00c8";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}