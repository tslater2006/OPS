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
	public static final String[] tokenNames = {
		"<INVALID>", "'Field'", "'BusProcess'", "'To'", "'false'", "'Page'", "'RecName'", 
		"'HTML'", "'Else'", "'Rowset'", "'For'", "'boolean'", "'CompIntfc'", "'Global'", 
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
	public static final int
		RULE_program = 0, RULE_stmtList = 1, RULE_stmt = 2, RULE_expr = 3, RULE_exprList = 4, 
		RULE_varDecl = 5, RULE_varScopeModifier = 6, RULE_varTypeModifier = 7, 
		RULE_appClassImport = 8, RULE_appClassPath = 9, RULE_funcImport = 10, 
		RULE_recDefnPath = 11, RULE_event = 12, RULE_ifConstruct = 13, RULE_forConstruct = 14, 
		RULE_evaluateConstruct = 15, RULE_defnType = 16, RULE_relop = 17, RULE_literal = 18, 
		RULE_boolLiteral = 19, RULE_defnLiteral = 20, RULE_id = 21;
	public static final String[] ruleNames = {
		"program", "stmtList", "stmt", "expr", "exprList", "varDecl", "varScopeModifier", 
		"varTypeModifier", "appClassImport", "appClassPath", "funcImport", "recDefnPath", 
		"event", "ifConstruct", "forConstruct", "evaluateConstruct", "defnType", 
		"relop", "literal", "boolLiteral", "defnLiteral", "id"
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
		public StmtListContext stmtList() {
			return getRuleContext(StmtListContext.class,0);
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
		enterRule(_localctx, 2, RULE_stmtList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 4) | (1L << 5) | (1L << 6) | (1L << 7) | (1L << 10) | (1L << 12) | (1L << 13) | (1L << 14) | (1L << 16) | (1L << 18) | (1L << 19) | (1L << 20) | (1L << 23) | (1L << 25) | (1L << 26) | (1L << 27) | (1L << 28) | (1L << 31) | (1L << 34) | (1L << 36) | (1L << 37) | (1L << 38) | (1L << 40) | (1L << 41) | (1L << 43) | (1L << 44) | (1L << 46) | (1L << 47) | (1L << 48) | (1L << 49) | (1L << 50) | (1L << 54) | (1L << 55) | (1L << 57) | (1L << 60) | (1L << 63))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (64 - 64)) | (1L << (VAR_ID - 64)) | (1L << (SYS_VAR_ID - 64)) | (1L << (GENERIC_ID - 64)) | (1L << (DecimalLiteral - 64)) | (1L << (IntegerLiteral - 64)) | (1L << (StringLiteral - 64)))) != 0)) {
				{
				{
				setState(46); stmt();
				setState(47); match(58);
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
	public static class StmtAppClassImportContext extends StmtContext {
		public AppClassImportContext appClassImport() {
			return getRuleContext(AppClassImportContext.class,0);
		}
		public StmtAppClassImportContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtAppClassImport(this);
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
		enterRule(_localctx, 4, RULE_stmt);
		int _la;
		try {
			setState(73);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new StmtAppClassImportContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(54); appClassImport();
				}
				break;

			case 2:
				_localctx = new StmtFuncImportContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(55); funcImport();
				}
				break;

			case 3:
				_localctx = new StmtVarDeclContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(56); varDecl();
				}
				break;

			case 4:
				_localctx = new StmtIfContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(57); ifConstruct();
				}
				break;

			case 5:
				_localctx = new StmtForContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(58); forConstruct();
				}
				break;

			case 6:
				_localctx = new StmtEvaluateContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(59); evaluateConstruct();
				}
				break;

			case 7:
				_localctx = new StmtExitContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(60); match(25);
				}
				break;

			case 8:
				_localctx = new StmtBreakContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(61); match(26);
				}
				break;

			case 9:
				_localctx = new StmtAssignContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(62); expr(0);
				setState(63); match(21);
				setState(64); expr(0);
				}
				break;

			case 10:
				_localctx = new StmtFnCallContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(66); expr(0);
				setState(67); match(34);
				setState(69);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 4) | (1L << 5) | (1L << 6) | (1L << 7) | (1L << 12) | (1L << 14) | (1L << 16) | (1L << 18) | (1L << 19) | (1L << 20) | (1L << 23) | (1L << 28) | (1L << 31) | (1L << 34) | (1L << 36) | (1L << 37) | (1L << 38) | (1L << 41) | (1L << 44) | (1L << 46) | (1L << 47) | (1L << 48) | (1L << 54) | (1L << 55) | (1L << 57) | (1L << 60) | (1L << 63))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (64 - 64)) | (1L << (VAR_ID - 64)) | (1L << (SYS_VAR_ID - 64)) | (1L << (GENERIC_ID - 64)) | (1L << (DecimalLiteral - 64)) | (1L << (IntegerLiteral - 64)) | (1L << (StringLiteral - 64)))) != 0)) {
					{
					setState(68); exprList();
					}
				}

				setState(71); match(17);
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
	public static class ExprCreateContext extends ExprContext {
		public ExprListContext exprList() {
			return getRuleContext(ExprListContext.class,0);
		}
		public AppClassPathContext appClassPath() {
			return getRuleContext(AppClassPathContext.class,0);
		}
		public ExprCreateContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprCreate(this);
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
		int _startState = 6;
		enterRecursionRule(_localctx, RULE_expr);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			switch (_input.LA(1)) {
			case 34:
				{
				_localctx = new ExprParenthesizedContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(76); match(34);
				setState(77); expr(0);
				setState(78); match(17);
				}
				break;
			case 19:
				{
				_localctx = new ExprDynamicReferenceContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(80); match(19);
				setState(81); match(34);
				setState(82); expr(0);
				setState(83); match(17);
				}
				break;
			case 55:
				{
				_localctx = new ExprCreateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(85); match(55);
				setState(86); appClassPath();
				setState(87); match(34);
				setState(89);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 4) | (1L << 5) | (1L << 6) | (1L << 7) | (1L << 12) | (1L << 14) | (1L << 16) | (1L << 18) | (1L << 19) | (1L << 20) | (1L << 23) | (1L << 28) | (1L << 31) | (1L << 34) | (1L << 36) | (1L << 37) | (1L << 38) | (1L << 41) | (1L << 44) | (1L << 46) | (1L << 47) | (1L << 48) | (1L << 54) | (1L << 55) | (1L << 57) | (1L << 60) | (1L << 63))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (64 - 64)) | (1L << (VAR_ID - 64)) | (1L << (SYS_VAR_ID - 64)) | (1L << (GENERIC_ID - 64)) | (1L << (DecimalLiteral - 64)) | (1L << (IntegerLiteral - 64)) | (1L << (StringLiteral - 64)))) != 0)) {
					{
					setState(88); exprList();
					}
				}

				setState(91); match(17);
				}
				break;
			case 1:
			case 2:
			case 4:
			case 5:
			case 6:
			case 7:
			case 12:
			case 14:
			case 16:
			case 18:
			case 20:
			case 23:
			case 28:
			case 31:
			case 36:
			case 37:
			case 38:
			case 41:
			case 44:
			case 46:
			case 47:
			case 48:
			case 54:
			case 57:
			case 60:
			case 63:
			case 64:
			case DecimalLiteral:
			case IntegerLiteral:
			case StringLiteral:
				{
				_localctx = new ExprLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(93); literal();
				}
				break;
			case VAR_ID:
			case SYS_VAR_ID:
			case GENERIC_ID:
				{
				_localctx = new ExprIdContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(94); id();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(118);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(116);
					switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
					case 1:
						{
						_localctx = new ExprBooleanContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(97);
						if (!(3 >= _localctx._p)) throw new FailedPredicateException(this, "3 >= $_p");
						{
						setState(98); match(51);
						}
						setState(99); expr(4);
						}
						break;

					case 2:
						{
						_localctx = new ExprConcatenateContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(100);
						if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
						{
						setState(101); match(29);
						}
						setState(102); expr(2);
						}
						break;

					case 3:
						{
						_localctx = new ExprStaticReferenceContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(103);
						if (!(5 >= _localctx._p)) throw new FailedPredicateException(this, "5 >= $_p");
						setState(104); match(52);
						setState(105); id();
						}
						break;

					case 4:
						{
						_localctx = new ExprFnCallContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(106);
						if (!(4 >= _localctx._p)) throw new FailedPredicateException(this, "4 >= $_p");
						setState(107); match(34);
						setState(109);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 4) | (1L << 5) | (1L << 6) | (1L << 7) | (1L << 12) | (1L << 14) | (1L << 16) | (1L << 18) | (1L << 19) | (1L << 20) | (1L << 23) | (1L << 28) | (1L << 31) | (1L << 34) | (1L << 36) | (1L << 37) | (1L << 38) | (1L << 41) | (1L << 44) | (1L << 46) | (1L << 47) | (1L << 48) | (1L << 54) | (1L << 55) | (1L << 57) | (1L << 60) | (1L << 63))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (64 - 64)) | (1L << (VAR_ID - 64)) | (1L << (SYS_VAR_ID - 64)) | (1L << (GENERIC_ID - 64)) | (1L << (DecimalLiteral - 64)) | (1L << (IntegerLiteral - 64)) | (1L << (StringLiteral - 64)))) != 0)) {
							{
							setState(108); exprList();
							}
						}

						setState(111); match(17);
						}
						break;

					case 5:
						{
						_localctx = new ExprEqualityContext(new ExprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(112);
						if (!(2 >= _localctx._p)) throw new FailedPredicateException(this, "2 >= $_p");
						setState(113); relop();
						setState(114); expr(0);
						}
						break;
					}
					} 
				}
				setState(120);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
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
		enterRule(_localctx, 8, RULE_exprList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(121); expr(0);
			setState(126);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==33) {
				{
				{
				setState(122); match(33);
				setState(123); expr(0);
				}
				}
				setState(128);
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
		enterRule(_localctx, 10, RULE_varDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129); varScopeModifier();
			setState(130); varTypeModifier();
			setState(131); match(VAR_ID);
			setState(136);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==33) {
				{
				{
				setState(132); match(33);
				setState(133); match(VAR_ID);
				}
				}
				setState(138);
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
		enterRule(_localctx, 12, RULE_varScopeModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 13) | (1L << 27) | (1L << 48))) != 0)) ) {
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
		public AppClassPathContext appClassPath() {
			return getRuleContext(AppClassPathContext.class,0);
		}
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
		enterRule(_localctx, 14, RULE_varTypeModifier);
		try {
			setState(149);
			switch (_input.LA(1)) {
			case 18:
				enterOuterAlt(_localctx, 1);
				{
				setState(141); match(18);
				}
				break;
			case 62:
				enterOuterAlt(_localctx, 2);
				{
				setState(142); match(62);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 3);
				{
				setState(143); match(11);
				}
				break;
			case 1:
				enterOuterAlt(_localctx, 4);
				{
				setState(144); match(1);
				}
				break;
			case 61:
				enterOuterAlt(_localctx, 5);
				{
				setState(145); match(61);
				}
				break;
			case 59:
				enterOuterAlt(_localctx, 6);
				{
				setState(146); match(59);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 7);
				{
				setState(147); match(9);
				}
				break;
			case GENERIC_ID:
				enterOuterAlt(_localctx, 8);
				{
				setState(148); appClassPath();
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

	public static class AppClassImportContext extends ParserRuleContext {
		public AppClassPathContext appClassPath() {
			return getRuleContext(AppClassPathContext.class,0);
		}
		public AppClassImportContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_appClassImport; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitAppClassImport(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AppClassImportContext appClassImport() throws RecognitionException {
		AppClassImportContext _localctx = new AppClassImportContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_appClassImport);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151); match(49);
			setState(152); appClassPath();
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

	public static class AppClassPathContext extends ParserRuleContext {
		public List<TerminalNode> GENERIC_ID() { return getTokens(PeopleCodeParser.GENERIC_ID); }
		public TerminalNode GENERIC_ID(int i) {
			return getToken(PeopleCodeParser.GENERIC_ID, i);
		}
		public AppClassPathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_appClassPath; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitAppClassPath(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AppClassPathContext appClassPath() throws RecognitionException {
		AppClassPathContext _localctx = new AppClassPathContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_appClassPath);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154); match(GENERIC_ID);
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==35) {
				{
				{
				setState(155); match(35);
				setState(156); match(GENERIC_ID);
				}
				}
				setState(161);
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

	public static class FuncImportContext extends ParserRuleContext {
		public TerminalNode GENERIC_ID() { return getToken(PeopleCodeParser.GENERIC_ID, 0); }
		public RecDefnPathContext recDefnPath() {
			return getRuleContext(RecDefnPathContext.class,0);
		}
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
		enterRule(_localctx, 20, RULE_funcImport);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162); match(40);
			setState(163); match(24);
			setState(164); match(GENERIC_ID);
			setState(165); match(39);
			setState(166); recDefnPath();
			setState(167); event();
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

	public static class RecDefnPathContext extends ParserRuleContext {
		public List<TerminalNode> GENERIC_ID() { return getTokens(PeopleCodeParser.GENERIC_ID); }
		public TerminalNode GENERIC_ID(int i) {
			return getToken(PeopleCodeParser.GENERIC_ID, i);
		}
		public RecDefnPathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recDefnPath; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitRecDefnPath(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RecDefnPathContext recDefnPath() throws RecognitionException {
		RecDefnPathContext _localctx = new RecDefnPathContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_recDefnPath);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169); match(GENERIC_ID);
			setState(174);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==52) {
				{
				{
				setState(170); match(52);
				setState(171); match(GENERIC_ID);
				}
				}
				setState(176);
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
		enterRule(_localctx, 24, RULE_event);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(177); match(53);
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
		enterRule(_localctx, 26, RULE_ifConstruct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(179); match(50);
			setState(180); expr(0);
			setState(181); match(30);
			setState(182); stmtList();
			setState(185);
			_la = _input.LA(1);
			if (_la==8) {
				{
				setState(183); match(8);
				setState(184); stmtList();
				}
			}

			setState(187); match(22);
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
		enterRule(_localctx, 28, RULE_forConstruct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189); match(10);
			setState(190); match(VAR_ID);
			setState(191); match(21);
			setState(192); expr(0);
			setState(193); match(3);
			setState(194); expr(0);
			setState(195); stmtList();
			setState(196); match(32);
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
		enterRule(_localctx, 30, RULE_evaluateConstruct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198); match(43);
			setState(199); expr(0);
			setState(207); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(200); match(45);
				setState(202);
				_la = _input.LA(1);
				if (_la==21 || _la==56) {
					{
					setState(201); relop();
					}
				}

				setState(204); expr(0);
				setState(205); stmtList();
				}
				}
				setState(209); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==45 );
			setState(213);
			_la = _input.LA(1);
			if (_la==42) {
				{
				setState(211); match(42);
				setState(212); stmtList();
				}
			}

			setState(215); match(15);
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

	public static class DefnTypeContext extends ParserRuleContext {
		public DefnTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defnType; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitDefnType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefnTypeContext defnType() throws RecognitionException {
		DefnTypeContext _localctx = new DefnTypeContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_defnType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			_la = _input.LA(1);
			if ( !(((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (1 - 1)) | (1L << (2 - 1)) | (1L << (5 - 1)) | (1L << (6 - 1)) | (1L << (7 - 1)) | (1L << (12 - 1)) | (1L << (16 - 1)) | (1L << (18 - 1)) | (1L << (20 - 1)) | (1L << (23 - 1)) | (1L << (28 - 1)) | (1L << (36 - 1)) | (1L << (37 - 1)) | (1L << (38 - 1)) | (1L << (41 - 1)) | (1L << (44 - 1)) | (1L << (46 - 1)) | (1L << (48 - 1)) | (1L << (54 - 1)) | (1L << (57 - 1)) | (1L << (60 - 1)) | (1L << (63 - 1)) | (1L << (64 - 1)))) != 0)) ) {
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
		enterRule(_localctx, 34, RULE_relop);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			_la = _input.LA(1);
			if ( !(_la==21 || _la==56) ) {
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
		public BoolLiteralContext boolLiteral() {
			return getRuleContext(BoolLiteralContext.class,0);
		}
		public DefnLiteralContext defnLiteral() {
			return getRuleContext(DefnLiteralContext.class,0);
		}
		public TerminalNode IntegerLiteral() { return getToken(PeopleCodeParser.IntegerLiteral, 0); }
		public TerminalNode StringLiteral() { return getToken(PeopleCodeParser.StringLiteral, 0); }
		public TerminalNode DecimalLiteral() { return getToken(PeopleCodeParser.DecimalLiteral, 0); }
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
		enterRule(_localctx, 36, RULE_literal);
		try {
			setState(226);
			switch (_input.LA(1)) {
			case DecimalLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(221); match(DecimalLiteral);
				}
				break;
			case IntegerLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(222); match(IntegerLiteral);
				}
				break;
			case 1:
			case 2:
			case 5:
			case 6:
			case 7:
			case 12:
			case 16:
			case 18:
			case 20:
			case 23:
			case 28:
			case 36:
			case 37:
			case 38:
			case 41:
			case 44:
			case 46:
			case 48:
			case 54:
			case 57:
			case 60:
			case 63:
			case 64:
				enterOuterAlt(_localctx, 3);
				{
				setState(223); defnLiteral();
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 4);
				{
				setState(224); match(StringLiteral);
				}
				break;
			case 4:
			case 14:
			case 31:
			case 47:
				enterOuterAlt(_localctx, 5);
				{
				setState(225); boolLiteral();
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

	public static class BoolLiteralContext extends ParserRuleContext {
		public BoolLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitBoolLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolLiteralContext boolLiteral() throws RecognitionException {
		BoolLiteralContext _localctx = new BoolLiteralContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_boolLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 4) | (1L << 14) | (1L << 31) | (1L << 47))) != 0)) ) {
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

	public static class DefnLiteralContext extends ParserRuleContext {
		public DefnTypeContext defnType() {
			return getRuleContext(DefnTypeContext.class,0);
		}
		public TerminalNode GENERIC_ID() { return getToken(PeopleCodeParser.GENERIC_ID, 0); }
		public DefnLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defnLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitDefnLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefnLiteralContext defnLiteral() throws RecognitionException {
		DefnLiteralContext _localctx = new DefnLiteralContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_defnLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230); defnType();
			setState(231); match(52);
			setState(232); match(GENERIC_ID);
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
		enterRule(_localctx, 42, RULE_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			_la = _input.LA(1);
			if ( !(((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (VAR_ID - 65)) | (1L << (SYS_VAR_ID - 65)) | (1L << (GENERIC_ID - 65)))) != 0)) ) {
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
		case 3: return expr_sempred((ExprContext)_localctx, predIndex);
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3K\u00ef\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\3\3\3\3\3\3"+
		"\7\3\64\n\3\f\3\16\3\67\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\5\4H\n\4\3\4\3\4\5\4L\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\\\n\5\3\5\3\5\3\5\3\5\5\5b\n\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5p\n\5\3\5\3\5\3\5\3\5"+
		"\3\5\7\5w\n\5\f\5\16\5z\13\5\3\6\3\6\3\6\7\6\177\n\6\f\6\16\6\u0082\13"+
		"\6\3\7\3\7\3\7\3\7\3\7\7\7\u0089\n\7\f\7\16\7\u008c\13\7\3\b\3\b\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u0098\n\t\3\n\3\n\3\n\3\13\3\13\3\13\7"+
		"\13\u00a0\n\13\f\13\16\13\u00a3\13\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r"+
		"\3\r\3\r\7\r\u00af\n\r\f\r\16\r\u00b2\13\r\3\16\3\16\3\17\3\17\3\17\3"+
		"\17\3\17\3\17\5\17\u00bc\n\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\21\5\21\u00cd\n\21\3\21\3\21\3\21\6\21"+
		"\u00d2\n\21\r\21\16\21\u00d3\3\21\3\21\5\21\u00d8\n\21\3\21\3\21\3\22"+
		"\3\22\3\23\3\23\3\24\3\24\3\24\3\24\3\24\5\24\u00e5\n\24\3\25\3\25\3\26"+
		"\3\26\3\26\3\26\3\27\3\27\3\27\2\30\2\4\6\b\n\f\16\20\22\24\26\30\32\34"+
		"\36 \"$&(*,\2\7\5\2\17\17\35\35\62\62\23\2\3\4\7\t\16\16\22\22\24\24\26"+
		"\26\31\31\36\36&(++..\60\60\62\6288;;>>AB\4\2\27\27::\6\2\6\6\20\20!!"+
		"\61\61\3\2CE\u0101\2.\3\2\2\2\4\65\3\2\2\2\6K\3\2\2\2\ba\3\2\2\2\n{\3"+
		"\2\2\2\f\u0083\3\2\2\2\16\u008d\3\2\2\2\20\u0097\3\2\2\2\22\u0099\3\2"+
		"\2\2\24\u009c\3\2\2\2\26\u00a4\3\2\2\2\30\u00ab\3\2\2\2\32\u00b3\3\2\2"+
		"\2\34\u00b5\3\2\2\2\36\u00bf\3\2\2\2 \u00c8\3\2\2\2\"\u00db\3\2\2\2$\u00dd"+
		"\3\2\2\2&\u00e4\3\2\2\2(\u00e6\3\2\2\2*\u00e8\3\2\2\2,\u00ec\3\2\2\2."+
		"/\5\4\3\2/\3\3\2\2\2\60\61\5\6\4\2\61\62\7<\2\2\62\64\3\2\2\2\63\60\3"+
		"\2\2\2\64\67\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2\2\66\5\3\2\2\2\67\65\3"+
		"\2\2\28L\5\22\n\29L\5\26\f\2:L\5\f\7\2;L\5\34\17\2<L\5\36\20\2=L\5 \21"+
		"\2>L\7\33\2\2?L\7\34\2\2@A\5\b\5\2AB\7\27\2\2BC\5\b\5\2CL\3\2\2\2DE\5"+
		"\b\5\2EG\7$\2\2FH\5\n\6\2GF\3\2\2\2GH\3\2\2\2HI\3\2\2\2IJ\7\23\2\2JL\3"+
		"\2\2\2K8\3\2\2\2K9\3\2\2\2K:\3\2\2\2K;\3\2\2\2K<\3\2\2\2K=\3\2\2\2K>\3"+
		"\2\2\2K?\3\2\2\2K@\3\2\2\2KD\3\2\2\2L\7\3\2\2\2MN\b\5\1\2NO\7$\2\2OP\5"+
		"\b\5\2PQ\7\23\2\2Qb\3\2\2\2RS\7\25\2\2ST\7$\2\2TU\5\b\5\2UV\7\23\2\2V"+
		"b\3\2\2\2WX\79\2\2XY\5\24\13\2Y[\7$\2\2Z\\\5\n\6\2[Z\3\2\2\2[\\\3\2\2"+
		"\2\\]\3\2\2\2]^\7\23\2\2^b\3\2\2\2_b\5&\24\2`b\5,\27\2aM\3\2\2\2aR\3\2"+
		"\2\2aW\3\2\2\2a_\3\2\2\2a`\3\2\2\2bx\3\2\2\2cd\6\5\2\3de\7\65\2\2ew\5"+
		"\b\5\2fg\6\5\3\3gh\7\37\2\2hw\5\b\5\2ij\6\5\4\3jk\7\66\2\2kw\5,\27\2l"+
		"m\6\5\5\3mo\7$\2\2np\5\n\6\2on\3\2\2\2op\3\2\2\2pq\3\2\2\2qw\7\23\2\2"+
		"rs\6\5\6\3st\5$\23\2tu\5\b\5\2uw\3\2\2\2vc\3\2\2\2vf\3\2\2\2vi\3\2\2\2"+
		"vl\3\2\2\2vr\3\2\2\2wz\3\2\2\2xv\3\2\2\2xy\3\2\2\2y\t\3\2\2\2zx\3\2\2"+
		"\2{\u0080\5\b\5\2|}\7#\2\2}\177\5\b\5\2~|\3\2\2\2\177\u0082\3\2\2\2\u0080"+
		"~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\13\3\2\2\2\u0082\u0080\3\2\2\2\u0083"+
		"\u0084\5\16\b\2\u0084\u0085\5\20\t\2\u0085\u008a\7C\2\2\u0086\u0087\7"+
		"#\2\2\u0087\u0089\7C\2\2\u0088\u0086\3\2\2\2\u0089\u008c\3\2\2\2\u008a"+
		"\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\r\3\2\2\2\u008c\u008a\3\2\2\2"+
		"\u008d\u008e\t\2\2\2\u008e\17\3\2\2\2\u008f\u0098\7\24\2\2\u0090\u0098"+
		"\7@\2\2\u0091\u0098\7\r\2\2\u0092\u0098\7\3\2\2\u0093\u0098\7?\2\2\u0094"+
		"\u0098\7=\2\2\u0095\u0098\7\13\2\2\u0096\u0098\5\24\13\2\u0097\u008f\3"+
		"\2\2\2\u0097\u0090\3\2\2\2\u0097\u0091\3\2\2\2\u0097\u0092\3\2\2\2\u0097"+
		"\u0093\3\2\2\2\u0097\u0094\3\2\2\2\u0097\u0095\3\2\2\2\u0097\u0096\3\2"+
		"\2\2\u0098\21\3\2\2\2\u0099\u009a\7\63\2\2\u009a\u009b\5\24\13\2\u009b"+
		"\23\3\2\2\2\u009c\u00a1\7E\2\2\u009d\u009e\7%\2\2\u009e\u00a0\7E\2\2\u009f"+
		"\u009d\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2"+
		"\2\2\u00a2\25\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4\u00a5\7*\2\2\u00a5\u00a6"+
		"\7\32\2\2\u00a6\u00a7\7E\2\2\u00a7\u00a8\7)\2\2\u00a8\u00a9\5\30\r\2\u00a9"+
		"\u00aa\5\32\16\2\u00aa\27\3\2\2\2\u00ab\u00b0\7E\2\2\u00ac\u00ad\7\66"+
		"\2\2\u00ad\u00af\7E\2\2\u00ae\u00ac\3\2\2\2\u00af\u00b2\3\2\2\2\u00b0"+
		"\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\31\3\2\2\2\u00b2\u00b0\3\2\2"+
		"\2\u00b3\u00b4\7\67\2\2\u00b4\33\3\2\2\2\u00b5\u00b6\7\64\2\2\u00b6\u00b7"+
		"\5\b\5\2\u00b7\u00b8\7 \2\2\u00b8\u00bb\5\4\3\2\u00b9\u00ba\7\n\2\2\u00ba"+
		"\u00bc\5\4\3\2\u00bb\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bd\3\2"+
		"\2\2\u00bd\u00be\7\30\2\2\u00be\35\3\2\2\2\u00bf\u00c0\7\f\2\2\u00c0\u00c1"+
		"\7C\2\2\u00c1\u00c2\7\27\2\2\u00c2\u00c3\5\b\5\2\u00c3\u00c4\7\5\2\2\u00c4"+
		"\u00c5\5\b\5\2\u00c5\u00c6\5\4\3\2\u00c6\u00c7\7\"\2\2\u00c7\37\3\2\2"+
		"\2\u00c8\u00c9\7-\2\2\u00c9\u00d1\5\b\5\2\u00ca\u00cc\7/\2\2\u00cb\u00cd"+
		"\5$\23\2\u00cc\u00cb\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce"+
		"\u00cf\5\b\5\2\u00cf\u00d0\5\4\3\2\u00d0\u00d2\3\2\2\2\u00d1\u00ca\3\2"+
		"\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4"+
		"\u00d7\3\2\2\2\u00d5\u00d6\7,\2\2\u00d6\u00d8\5\4\3\2\u00d7\u00d5\3\2"+
		"\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00da\7\21\2\2\u00da"+
		"!\3\2\2\2\u00db\u00dc\t\3\2\2\u00dc#\3\2\2\2\u00dd\u00de\t\4\2\2\u00de"+
		"%\3\2\2\2\u00df\u00e5\7F\2\2\u00e0\u00e5\7G\2\2\u00e1\u00e5\5*\26\2\u00e2"+
		"\u00e5\7H\2\2\u00e3\u00e5\5(\25\2\u00e4\u00df\3\2\2\2\u00e4\u00e0\3\2"+
		"\2\2\u00e4\u00e1\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e3\3\2\2\2\u00e5"+
		"\'\3\2\2\2\u00e6\u00e7\t\5\2\2\u00e7)\3\2\2\2\u00e8\u00e9\5\"\22\2\u00e9"+
		"\u00ea\7\66\2\2\u00ea\u00eb\7E\2\2\u00eb+\3\2\2\2\u00ec\u00ed\t\6\2\2"+
		"\u00ed-\3\2\2\2\24\65GK[aovx\u0080\u008a\u0097\u00a1\u00b0\u00bb\u00cc"+
		"\u00d3\u00d7\u00e4";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}