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
		T__17=1, T__16=2, T__15=3, T__14=4, T__13=5, T__12=6, T__11=7, T__10=8, 
		T__9=9, T__8=10, T__7=11, T__6=12, T__5=13, T__4=14, T__3=15, T__2=16, 
		T__1=17, T__0=18, DecimalLiteral=19, IntegerLiteral=20, BooleanLiteral=21, 
		VAR_ID=22, OBJECT_ID=23, FUNC_ID=24, SYSTEM_VAR=25, COMMENT=26, WS=27;
	public static final String[] tokenNames = {
		"<INVALID>", "'Component'", "'If'", "'Global'", "'End-Evaluate'", "')'", 
		"'.'", "','", "'Record'", "'('", "'='", "';'", "'End-If'", "'Evaluate'", 
		"'Local'", "'string'", "'When'", "'MenuName'", "'Then'", "DecimalLiteral", 
		"IntegerLiteral", "BooleanLiteral", "VAR_ID", "OBJECT_ID", "FUNC_ID", 
		"SYSTEM_VAR", "COMMENT", "WS"
	};
	public static final int
		RULE_program = 0, RULE_classicProg = 1, RULE_stmtList = 2, RULE_stmt = 3, 
		RULE_varDecl = 4, RULE_varScopeModifier = 5, RULE_varTypeModifier = 6, 
		RULE_ifConstruct = 7, RULE_evaluateConstruct = 8, RULE_whenClause = 9, 
		RULE_expr = 10, RULE_exprList = 11, RULE_fnCall = 12, RULE_defnKeyword = 13, 
		RULE_literal = 14;
	public static final String[] ruleNames = {
		"program", "classicProg", "stmtList", "stmt", "varDecl", "varScopeModifier", 
		"varTypeModifier", "ifConstruct", "evaluateConstruct", "whenClause", "expr", 
		"exprList", "fnCall", "defnKeyword", "literal"
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
			setState(30); classicProg();
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
			setState(32); stmtList();
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
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 3) | (1L << 9) | (1L << 13) | (1L << 14) | (1L << 17) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << BooleanLiteral) | (1L << OBJECT_ID) | (1L << FUNC_ID) | (1L << SYSTEM_VAR))) != 0)) {
				{
				{
				setState(34); stmt();
				setState(35); match(11);
				}
				}
				setState(41);
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
	public static class StmtFnCallContext extends StmtContext {
		public FnCallContext fnCall() {
			return getRuleContext(FnCallContext.class,0);
		}
		public StmtFnCallContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitStmtFnCall(this);
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

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_stmt);
		try {
			setState(50);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				_localctx = new StmtVarDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(42); varDecl();
				}
				break;

			case 2:
				_localctx = new StmtIfContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(43); ifConstruct();
				}
				break;

			case 3:
				_localctx = new StmtEvaluateContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(44); evaluateConstruct();
				}
				break;

			case 4:
				_localctx = new StmtFnCallContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(45); fnCall();
				}
				break;

			case 5:
				_localctx = new StmtAssignContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(46); expr(0);
				setState(47); match(10);
				setState(48); expr(0);
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

	public static class VarDeclContext extends ParserRuleContext {
		public TerminalNode VAR_ID() { return getToken(PeopleCodeParser.VAR_ID, 0); }
		public VarTypeModifierContext varTypeModifier() {
			return getRuleContext(VarTypeModifierContext.class,0);
		}
		public VarScopeModifierContext varScopeModifier() {
			return getRuleContext(VarScopeModifierContext.class,0);
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
		enterRule(_localctx, 8, RULE_varDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52); varScopeModifier();
			setState(53); varTypeModifier();
			setState(54); match(VAR_ID);
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
		enterRule(_localctx, 10, RULE_varScopeModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 3) | (1L << 14))) != 0)) ) {
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
		enterRule(_localctx, 12, RULE_varTypeModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			_la = _input.LA(1);
			if ( !(_la==8 || _la==15) ) {
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

	public static class IfConstructContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtListContext stmtList() {
			return getRuleContext(StmtListContext.class,0);
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
		enterRule(_localctx, 14, RULE_ifConstruct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60); match(2);
			setState(61); expr(0);
			setState(62); match(18);
			setState(63); stmtList();
			setState(64); match(12);
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
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public WhenClauseContext whenClause(int i) {
			return getRuleContext(WhenClauseContext.class,i);
		}
		public List<WhenClauseContext> whenClause() {
			return getRuleContexts(WhenClauseContext.class);
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
		enterRule(_localctx, 16, RULE_evaluateConstruct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66); match(13);
			setState(67); expr(0);
			setState(69); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(68); whenClause();
				}
				}
				setState(71); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==16 );
			setState(73); match(4);
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

	public static class WhenClauseContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtListContext stmtList() {
			return getRuleContext(StmtListContext.class,0);
		}
		public WhenClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whenClause; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitWhenClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhenClauseContext whenClause() throws RecognitionException {
		WhenClauseContext _localctx = new WhenClauseContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_whenClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75); match(16);
			setState(76); expr(0);
			setState(77); stmtList();
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
	public static class ExprFnCallContext extends ExprContext {
		public FnCallContext fnCall() {
			return getRuleContext(FnCallContext.class,0);
		}
		public ExprFnCallContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprFnCall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprCompBufferRefContext extends ExprContext {
		public List<TerminalNode> OBJECT_ID() { return getTokens(PeopleCodeParser.OBJECT_ID); }
		public TerminalNode OBJECT_ID(int i) {
			return getToken(PeopleCodeParser.OBJECT_ID, i);
		}
		public ExprCompBufferRefContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprCompBufferRef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprComparisonContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprComparisonContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprComparison(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprSystemVarContext extends ExprContext {
		public TerminalNode SYSTEM_VAR() { return getToken(PeopleCodeParser.SYSTEM_VAR, 0); }
		public ExprSystemVarContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprSystemVar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprObjDefnRefContext extends ExprContext {
		public TerminalNode OBJECT_ID() { return getToken(PeopleCodeParser.OBJECT_ID, 0); }
		public DefnKeywordContext defnKeyword() {
			return getRuleContext(DefnKeywordContext.class,0);
		}
		public ExprObjDefnRefContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprObjDefnRef(this);
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
		int _startState = 20;
		enterRecursionRule(_localctx, RULE_expr);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			switch (_input.LA(1)) {
			case 9:
				{
				_localctx = new ExprParenthesizedContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(80); match(9);
				setState(81); expr(0);
				setState(82); match(5);
				}
				break;
			case 1:
			case 17:
				{
				_localctx = new ExprObjDefnRefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(84); defnKeyword();
				setState(85); match(6);
				setState(86); match(OBJECT_ID);
				}
				break;
			case DecimalLiteral:
			case IntegerLiteral:
			case BooleanLiteral:
				{
				_localctx = new ExprLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(88); literal();
				}
				break;
			case OBJECT_ID:
				{
				_localctx = new ExprCompBufferRefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(89); match(OBJECT_ID);
				setState(94);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
				while ( _alt!=2 && _alt!=-1 ) {
					if ( _alt==1 ) {
						{
						{
						setState(90); match(6);
						setState(91); match(OBJECT_ID);
						}
						} 
					}
					setState(96);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
				}
				}
				break;
			case SYSTEM_VAR:
				{
				_localctx = new ExprSystemVarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(97); match(SYSTEM_VAR);
				}
				break;
			case FUNC_ID:
				{
				_localctx = new ExprFnCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(98); fnCall();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(106);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprComparisonContext(new ExprContext(_parentctx, _parentState, _p));
					pushNewRecursionContext(_localctx, _startState, RULE_expr);
					setState(101);
					if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
					setState(102); match(10);
					setState(103); expr(2);
					}
					} 
				}
				setState(108);
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
		enterRule(_localctx, 22, RULE_exprList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109); expr(0);
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==7) {
				{
				{
				setState(110); match(7);
				setState(111); expr(0);
				}
				}
				setState(116);
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

	public static class FnCallContext extends ParserRuleContext {
		public ExprListContext exprList() {
			return getRuleContext(ExprListContext.class,0);
		}
		public TerminalNode FUNC_ID() { return getToken(PeopleCodeParser.FUNC_ID, 0); }
		public FnCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fnCall; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitFnCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FnCallContext fnCall() throws RecognitionException {
		FnCallContext _localctx = new FnCallContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_fnCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117); match(FUNC_ID);
			setState(118); match(9);
			setState(120);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 9) | (1L << 17) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << BooleanLiteral) | (1L << OBJECT_ID) | (1L << FUNC_ID) | (1L << SYSTEM_VAR))) != 0)) {
				{
				setState(119); exprList();
				}
			}

			setState(122); match(5);
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
		enterRule(_localctx, 26, RULE_defnKeyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			_la = _input.LA(1);
			if ( !(_la==1 || _la==17) ) {
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
		enterRule(_localctx, 28, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
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
		case 10: return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 1 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\35\u0083\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\3\3\3\3"+
		"\4\3\4\3\4\7\4(\n\4\f\4\16\4+\13\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5"+
		"\65\n\5\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3"+
		"\n\3\n\6\nH\n\n\r\n\16\nI\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\7\f_\n\f\f\f\16\fb\13\f\3\f\3\f\5"+
		"\ff\n\f\3\f\3\f\3\f\7\fk\n\f\f\f\16\fn\13\f\3\r\3\r\3\r\7\rs\n\r\f\r\16"+
		"\rv\13\r\3\16\3\16\3\16\5\16{\n\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20"+
		"\2\21\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36\2\6\5\2\3\3\5\5\20\20\4\2"+
		"\n\n\21\21\4\2\3\3\23\23\3\2\25\27\u0082\2 \3\2\2\2\4\"\3\2\2\2\6)\3\2"+
		"\2\2\b\64\3\2\2\2\n\66\3\2\2\2\f:\3\2\2\2\16<\3\2\2\2\20>\3\2\2\2\22D"+
		"\3\2\2\2\24M\3\2\2\2\26e\3\2\2\2\30o\3\2\2\2\32w\3\2\2\2\34~\3\2\2\2\36"+
		"\u0080\3\2\2\2 !\5\4\3\2!\3\3\2\2\2\"#\5\6\4\2#\5\3\2\2\2$%\5\b\5\2%&"+
		"\7\r\2\2&(\3\2\2\2\'$\3\2\2\2(+\3\2\2\2)\'\3\2\2\2)*\3\2\2\2*\7\3\2\2"+
		"\2+)\3\2\2\2,\65\5\n\6\2-\65\5\20\t\2.\65\5\22\n\2/\65\5\32\16\2\60\61"+
		"\5\26\f\2\61\62\7\f\2\2\62\63\5\26\f\2\63\65\3\2\2\2\64,\3\2\2\2\64-\3"+
		"\2\2\2\64.\3\2\2\2\64/\3\2\2\2\64\60\3\2\2\2\65\t\3\2\2\2\66\67\5\f\7"+
		"\2\678\5\16\b\289\7\30\2\29\13\3\2\2\2:;\t\2\2\2;\r\3\2\2\2<=\t\3\2\2"+
		"=\17\3\2\2\2>?\7\4\2\2?@\5\26\f\2@A\7\24\2\2AB\5\6\4\2BC\7\16\2\2C\21"+
		"\3\2\2\2DE\7\17\2\2EG\5\26\f\2FH\5\24\13\2GF\3\2\2\2HI\3\2\2\2IG\3\2\2"+
		"\2IJ\3\2\2\2JK\3\2\2\2KL\7\6\2\2L\23\3\2\2\2MN\7\22\2\2NO\5\26\f\2OP\5"+
		"\6\4\2P\25\3\2\2\2QR\b\f\1\2RS\7\13\2\2ST\5\26\f\2TU\7\7\2\2Uf\3\2\2\2"+
		"VW\5\34\17\2WX\7\b\2\2XY\7\31\2\2Yf\3\2\2\2Zf\5\36\20\2[`\7\31\2\2\\]"+
		"\7\b\2\2]_\7\31\2\2^\\\3\2\2\2_b\3\2\2\2`^\3\2\2\2`a\3\2\2\2af\3\2\2\2"+
		"b`\3\2\2\2cf\7\33\2\2df\5\32\16\2eQ\3\2\2\2eV\3\2\2\2eZ\3\2\2\2e[\3\2"+
		"\2\2ec\3\2\2\2ed\3\2\2\2fl\3\2\2\2gh\6\f\2\3hi\7\f\2\2ik\5\26\f\2jg\3"+
		"\2\2\2kn\3\2\2\2lj\3\2\2\2lm\3\2\2\2m\27\3\2\2\2nl\3\2\2\2ot\5\26\f\2"+
		"pq\7\t\2\2qs\5\26\f\2rp\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3\2\2\2u\31\3\2"+
		"\2\2vt\3\2\2\2wx\7\32\2\2xz\7\13\2\2y{\5\30\r\2zy\3\2\2\2z{\3\2\2\2{|"+
		"\3\2\2\2|}\7\7\2\2}\33\3\2\2\2~\177\t\4\2\2\177\35\3\2\2\2\u0080\u0081"+
		"\t\5\2\2\u0081\37\3\2\2\2\n)\64I`eltz";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}