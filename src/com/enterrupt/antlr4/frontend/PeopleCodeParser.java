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
		T__24=1, T__23=2, T__22=3, T__21=4, T__20=5, T__19=6, T__18=7, T__17=8, 
		T__16=9, T__15=10, T__14=11, T__13=12, T__12=13, T__11=14, T__10=15, T__9=16, 
		T__8=17, T__7=18, T__6=19, T__5=20, T__4=21, T__3=22, T__2=23, T__1=24, 
		T__0=25, GENERIC_ID=26, DecimalLiteral=27, IntegerLiteral=28, StringLiteral=29, 
		VAR_ID=30, SYS_VAR_ID=31, COMMENT=32, WS=33;
	public static final String[] tokenNames = {
		"<INVALID>", "'True'", "','", "'('", "'false'", "'When-Other'", "'Evaluate'", 
		"'When'", "'true'", "'Component'", "'False'", "'Global'", "'If'", "'End-Evaluate'", 
		"')'", "'.'", "'Record'", "'='", "';'", "'End-If'", "'Exit'", "'Break'", 
		"'Local'", "'string'", "'MenuName'", "'Then'", "GENERIC_ID", "DecimalLiteral", 
		"IntegerLiteral", "StringLiteral", "VAR_ID", "SYS_VAR_ID", "COMMENT", 
		"WS"
	};
	public static final int
		RULE_program = 0, RULE_classicProg = 1, RULE_stmtList = 2, RULE_stmt = 3, 
		RULE_varDecl = 4, RULE_varScopeModifier = 5, RULE_varTypeModifier = 6, 
		RULE_ifConstruct = 7, RULE_evaluateConstruct = 8, RULE_whenClause = 9, 
		RULE_whenOtherClause = 10, RULE_expr = 11, RULE_exprList = 12, RULE_defnKeyword = 13, 
		RULE_literal = 14, RULE_booleanLiteral = 15, RULE_definitionLiteral = 16, 
		RULE_id = 17;
	public static final String[] ruleNames = {
		"program", "classicProg", "stmtList", "stmt", "varDecl", "varScopeModifier", 
		"varTypeModifier", "ifConstruct", "evaluateConstruct", "whenClause", "whenOtherClause", 
		"expr", "exprList", "defnKeyword", "literal", "booleanLiteral", "definitionLiteral", 
		"id"
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
			setState(36); classicProg();
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
			setState(38); stmtList();
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
			setState(45);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 3) | (1L << 4) | (1L << 6) | (1L << 8) | (1L << 9) | (1L << 10) | (1L << 11) | (1L << 12) | (1L << 16) | (1L << 20) | (1L << 21) | (1L << 22) | (1L << 24) | (1L << GENERIC_ID) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << StringLiteral) | (1L << VAR_ID) | (1L << SYS_VAR_ID))) != 0)) {
				{
				{
				setState(40); stmt();
				setState(41); match(18);
				}
				}
				setState(47);
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
	public static class StmtFnCallContext extends StmtContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ExprListContext exprList() {
			return getRuleContext(ExprListContext.class,0);
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
			setState(64);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new StmtVarDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(48); varDecl();
				}
				break;

			case 2:
				_localctx = new StmtIfContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(49); ifConstruct();
				}
				break;

			case 3:
				_localctx = new StmtEvaluateContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(50); evaluateConstruct();
				}
				break;

			case 4:
				_localctx = new StmtExitContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(51); match(20);
				}
				break;

			case 5:
				_localctx = new StmtBreakContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(52); match(21);
				}
				break;

			case 6:
				_localctx = new StmtFnCallContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(53); id();
				setState(54); match(3);
				setState(56);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 3) | (1L << 4) | (1L << 8) | (1L << 9) | (1L << 10) | (1L << 16) | (1L << 24) | (1L << GENERIC_ID) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << StringLiteral) | (1L << VAR_ID) | (1L << SYS_VAR_ID))) != 0)) {
					{
					setState(55); exprList();
					}
				}

				setState(58); match(14);
				}
				break;

			case 7:
				_localctx = new StmtAssignContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(60); expr(0);
				setState(61); match(17);
				setState(62); expr(0);
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
			setState(66); varScopeModifier();
			setState(67); varTypeModifier();
			setState(68); match(VAR_ID);
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
			setState(70);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 9) | (1L << 11) | (1L << 22))) != 0)) ) {
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
			setState(72);
			_la = _input.LA(1);
			if ( !(_la==16 || _la==23) ) {
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
			setState(74); match(12);
			setState(75); expr(0);
			setState(76); match(25);
			setState(77); stmtList();
			setState(78); match(19);
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
		public WhenOtherClauseContext whenOtherClause() {
			return getRuleContext(WhenOtherClauseContext.class,0);
		}
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
			setState(80); match(6);
			setState(81); expr(0);
			setState(83); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(82); whenClause();
				}
				}
				setState(85); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==7 );
			setState(88);
			_la = _input.LA(1);
			if (_la==5) {
				{
				setState(87); whenOtherClause();
				}
			}

			setState(90); match(13);
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
			setState(92); match(7);
			setState(93); expr(0);
			setState(94); stmtList();
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

	public static class WhenOtherClauseContext extends ParserRuleContext {
		public StmtListContext stmtList() {
			return getRuleContext(StmtListContext.class,0);
		}
		public WhenOtherClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whenOtherClause; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitWhenOtherClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhenOtherClauseContext whenOtherClause() throws RecognitionException {
		WhenOtherClauseContext _localctx = new WhenOtherClauseContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_whenOtherClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96); match(5);
			setState(97); stmtList();
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
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ExprListContext exprList() {
			return getRuleContext(ExprListContext.class,0);
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
		int _startState = 22;
		enterRecursionRule(_localctx, RULE_expr);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				_localctx = new ExprParenthesizedContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(100); match(3);
				setState(101); expr(0);
				setState(102); match(14);
				}
				break;

			case 2:
				{
				_localctx = new ExprLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(104); literal();
				}
				break;

			case 3:
				{
				_localctx = new ExprIdContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(105); id();
				}
				break;

			case 4:
				{
				_localctx = new ExprFnCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(106); id();
				setState(107); match(3);
				setState(109);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 3) | (1L << 4) | (1L << 8) | (1L << 9) | (1L << 10) | (1L << 16) | (1L << 24) | (1L << GENERIC_ID) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << StringLiteral) | (1L << VAR_ID) | (1L << SYS_VAR_ID))) != 0)) {
					{
					setState(108); exprList();
					}
				}

				setState(111); match(14);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(120);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprComparisonContext(new ExprContext(_parentctx, _parentState, _p));
					pushNewRecursionContext(_localctx, _startState, RULE_expr);
					setState(115);
					if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
					setState(116); match(17);
					setState(117); expr(1);
					}
					} 
				}
				setState(122);
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
		enterRule(_localctx, 24, RULE_exprList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123); expr(0);
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==2) {
				{
				{
				setState(124); match(2);
				setState(125); expr(0);
				}
				}
				setState(130);
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
			setState(131);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 9) | (1L << 16) | (1L << 24))) != 0)) ) {
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
		enterRule(_localctx, 28, RULE_literal);
		try {
			setState(138);
			switch (_input.LA(1)) {
			case DecimalLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(133); match(DecimalLiteral);
				}
				break;
			case IntegerLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(134); match(IntegerLiteral);
				}
				break;
			case 9:
			case 16:
			case 24:
				enterOuterAlt(_localctx, 3);
				{
				setState(135); definitionLiteral();
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 4);
				{
				setState(136); match(StringLiteral);
				}
				break;
			case 1:
			case 4:
			case 8:
			case 10:
				enterOuterAlt(_localctx, 5);
				{
				setState(137); booleanLiteral();
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
		enterRule(_localctx, 30, RULE_booleanLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 4) | (1L << 8) | (1L << 10))) != 0)) ) {
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
		enterRule(_localctx, 32, RULE_definitionLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142); defnKeyword();
			setState(143); match(15);
			setState(144); match(GENERIC_ID);
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
		public List<TerminalNode> GENERIC_ID() { return getTokens(PeopleCodeParser.GENERIC_ID); }
		public TerminalNode GENERIC_ID(int i) {
			return getToken(PeopleCodeParser.GENERIC_ID, i);
		}
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
		enterRule(_localctx, 34, RULE_id);
		try {
			int _alt;
			setState(163);
			switch (_input.LA(1)) {
			case SYS_VAR_ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(146); match(SYS_VAR_ID);
				}
				break;
			case VAR_ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(147); match(VAR_ID);
				setState(152);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
				while ( _alt!=2 && _alt!=-1 ) {
					if ( _alt==1 ) {
						{
						{
						setState(148); match(15);
						setState(149); match(GENERIC_ID);
						}
						} 
					}
					setState(154);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
				}
				}
				break;
			case GENERIC_ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(155); match(GENERIC_ID);
				setState(160);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				while ( _alt!=2 && _alt!=-1 ) {
					if ( _alt==1 ) {
						{
						{
						setState(156); match(15);
						setState(157); match(GENERIC_ID);
						}
						} 
					}
					setState(162);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				}
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 11: return expr_sempred((ExprContext)_localctx, predIndex);
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3#\u00a8\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\3\2\3\2\3\3\3\3\3\4\3\4\3\4\7\4.\n\4\f\4\16\4\61\13\4\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5;\n\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5C\n\5\3"+
		"\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\6\n"+
		"V\n\n\r\n\16\nW\3\n\5\n[\n\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\rp\n\r\3\r\3\r\5\rt\n\r\3\r\3"+
		"\r\3\r\7\ry\n\r\f\r\16\r|\13\r\3\16\3\16\3\16\7\16\u0081\n\16\f\16\16"+
		"\16\u0084\13\16\3\17\3\17\3\20\3\20\3\20\3\20\3\20\5\20\u008d\n\20\3\21"+
		"\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\7\23\u0099\n\23\f\23\16"+
		"\23\u009c\13\23\3\23\3\23\3\23\7\23\u00a1\n\23\f\23\16\23\u00a4\13\23"+
		"\5\23\u00a6\n\23\3\23\2\24\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$"+
		"\2\6\5\2\13\13\r\r\30\30\4\2\22\22\31\31\5\2\13\13\22\22\32\32\6\2\3\3"+
		"\6\6\n\n\f\f\u00ad\2&\3\2\2\2\4(\3\2\2\2\6/\3\2\2\2\bB\3\2\2\2\nD\3\2"+
		"\2\2\fH\3\2\2\2\16J\3\2\2\2\20L\3\2\2\2\22R\3\2\2\2\24^\3\2\2\2\26b\3"+
		"\2\2\2\30s\3\2\2\2\32}\3\2\2\2\34\u0085\3\2\2\2\36\u008c\3\2\2\2 \u008e"+
		"\3\2\2\2\"\u0090\3\2\2\2$\u00a5\3\2\2\2&\'\5\4\3\2\'\3\3\2\2\2()\5\6\4"+
		"\2)\5\3\2\2\2*+\5\b\5\2+,\7\24\2\2,.\3\2\2\2-*\3\2\2\2.\61\3\2\2\2/-\3"+
		"\2\2\2/\60\3\2\2\2\60\7\3\2\2\2\61/\3\2\2\2\62C\5\n\6\2\63C\5\20\t\2\64"+
		"C\5\22\n\2\65C\7\26\2\2\66C\7\27\2\2\678\5$\23\28:\7\5\2\29;\5\32\16\2"+
		":9\3\2\2\2:;\3\2\2\2;<\3\2\2\2<=\7\20\2\2=C\3\2\2\2>?\5\30\r\2?@\7\23"+
		"\2\2@A\5\30\r\2AC\3\2\2\2B\62\3\2\2\2B\63\3\2\2\2B\64\3\2\2\2B\65\3\2"+
		"\2\2B\66\3\2\2\2B\67\3\2\2\2B>\3\2\2\2C\t\3\2\2\2DE\5\f\7\2EF\5\16\b\2"+
		"FG\7 \2\2G\13\3\2\2\2HI\t\2\2\2I\r\3\2\2\2JK\t\3\2\2K\17\3\2\2\2LM\7\16"+
		"\2\2MN\5\30\r\2NO\7\33\2\2OP\5\6\4\2PQ\7\25\2\2Q\21\3\2\2\2RS\7\b\2\2"+
		"SU\5\30\r\2TV\5\24\13\2UT\3\2\2\2VW\3\2\2\2WU\3\2\2\2WX\3\2\2\2XZ\3\2"+
		"\2\2Y[\5\26\f\2ZY\3\2\2\2Z[\3\2\2\2[\\\3\2\2\2\\]\7\17\2\2]\23\3\2\2\2"+
		"^_\7\t\2\2_`\5\30\r\2`a\5\6\4\2a\25\3\2\2\2bc\7\7\2\2cd\5\6\4\2d\27\3"+
		"\2\2\2ef\b\r\1\2fg\7\5\2\2gh\5\30\r\2hi\7\20\2\2it\3\2\2\2jt\5\36\20\2"+
		"kt\5$\23\2lm\5$\23\2mo\7\5\2\2np\5\32\16\2on\3\2\2\2op\3\2\2\2pq\3\2\2"+
		"\2qr\7\20\2\2rt\3\2\2\2se\3\2\2\2sj\3\2\2\2sk\3\2\2\2sl\3\2\2\2tz\3\2"+
		"\2\2uv\6\r\2\3vw\7\23\2\2wy\5\30\r\2xu\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3"+
		"\2\2\2{\31\3\2\2\2|z\3\2\2\2}\u0082\5\30\r\2~\177\7\4\2\2\177\u0081\5"+
		"\30\r\2\u0080~\3\2\2\2\u0081\u0084\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083"+
		"\3\2\2\2\u0083\33\3\2\2\2\u0084\u0082\3\2\2\2\u0085\u0086\t\4\2\2\u0086"+
		"\35\3\2\2\2\u0087\u008d\7\35\2\2\u0088\u008d\7\36\2\2\u0089\u008d\5\""+
		"\22\2\u008a\u008d\7\37\2\2\u008b\u008d\5 \21\2\u008c\u0087\3\2\2\2\u008c"+
		"\u0088\3\2\2\2\u008c\u0089\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008b\3\2"+
		"\2\2\u008d\37\3\2\2\2\u008e\u008f\t\5\2\2\u008f!\3\2\2\2\u0090\u0091\5"+
		"\34\17\2\u0091\u0092\7\21\2\2\u0092\u0093\7\34\2\2\u0093#\3\2\2\2\u0094"+
		"\u00a6\7!\2\2\u0095\u009a\7 \2\2\u0096\u0097\7\21\2\2\u0097\u0099\7\34"+
		"\2\2\u0098\u0096\3\2\2\2\u0099\u009c\3\2\2\2\u009a\u0098\3\2\2\2\u009a"+
		"\u009b\3\2\2\2\u009b\u00a6\3\2\2\2\u009c\u009a\3\2\2\2\u009d\u00a2\7\34"+
		"\2\2\u009e\u009f\7\21\2\2\u009f\u00a1\7\34\2\2\u00a0\u009e\3\2\2\2\u00a1"+
		"\u00a4\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a6\3\2"+
		"\2\2\u00a4\u00a2\3\2\2\2\u00a5\u0094\3\2\2\2\u00a5\u0095\3\2\2\2\u00a5"+
		"\u009d\3\2\2\2\u00a6%\3\2\2\2\17/:BWZosz\u0082\u008c\u009a\u00a2\u00a5";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}