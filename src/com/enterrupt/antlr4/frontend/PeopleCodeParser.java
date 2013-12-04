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
		T__25=1, T__24=2, T__23=3, T__22=4, T__21=5, T__20=6, T__19=7, T__18=8, 
		T__17=9, T__16=10, T__15=11, T__14=12, T__13=13, T__12=14, T__11=15, T__10=16, 
		T__9=17, T__8=18, T__7=19, T__6=20, T__5=21, T__4=22, T__3=23, T__2=24, 
		T__1=25, T__0=26, DecimalLiteral=27, IntegerLiteral=28, VAR_ID=29, BUFFER_ID=30, 
		FUNC_ID=31, SYS_VAR_ID=32, COMMENT=33, WS=34;
	public static final String[] tokenNames = {
		"<INVALID>", "'True'", "','", "'('", "'false'", "'When-Other'", "'Evaluate'", 
		"'Visible'", "'When'", "'true'", "'Component'", "'Global'", "'If'", "'False'", 
		"'End-Evaluate'", "'.'", "')'", "'Record'", "'='", "';'", "'End-If'", 
		"'Exit'", "'Break'", "'Local'", "'string'", "'MenuName'", "'Then'", "DecimalLiteral", 
		"IntegerLiteral", "VAR_ID", "BUFFER_ID", "FUNC_ID", "SYS_VAR_ID", "COMMENT", 
		"WS"
	};
	public static final int
		RULE_program = 0, RULE_classicProg = 1, RULE_stmtList = 2, RULE_stmt = 3, 
		RULE_varDecl = 4, RULE_varScopeModifier = 5, RULE_varTypeModifier = 6, 
		RULE_ifConstruct = 7, RULE_evaluateConstruct = 8, RULE_whenClause = 9, 
		RULE_whenOtherClause = 10, RULE_expr = 11, RULE_exprList = 12, RULE_fnCall = 13, 
		RULE_defnRef = 14, RULE_defnKeyword = 15, RULE_bufferRef = 16, RULE_bufferFldProperty = 17, 
		RULE_literal = 18, RULE_booleanLiteral = 19;
	public static final String[] ruleNames = {
		"program", "classicProg", "stmtList", "stmt", "varDecl", "varScopeModifier", 
		"varTypeModifier", "ifConstruct", "evaluateConstruct", "whenClause", "whenOtherClause", 
		"expr", "exprList", "fnCall", "defnRef", "defnKeyword", "bufferRef", "bufferFldProperty", 
		"literal", "booleanLiteral"
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
			setState(40); classicProg();
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
			setState(42); stmtList();
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
			setState(49);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 3) | (1L << 4) | (1L << 6) | (1L << 9) | (1L << 10) | (1L << 11) | (1L << 12) | (1L << 13) | (1L << 17) | (1L << 21) | (1L << 22) | (1L << 23) | (1L << 25) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << VAR_ID) | (1L << BUFFER_ID) | (1L << FUNC_ID) | (1L << SYS_VAR_ID))) != 0)) {
				{
				{
				setState(44); stmt();
				setState(45); match(19);
				}
				}
				setState(51);
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
		try {
			setState(62);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				_localctx = new StmtVarDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(52); varDecl();
				}
				break;

			case 2:
				_localctx = new StmtIfContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(53); ifConstruct();
				}
				break;

			case 3:
				_localctx = new StmtEvaluateContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(54); evaluateConstruct();
				}
				break;

			case 4:
				_localctx = new StmtExitContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(55); match(21);
				}
				break;

			case 5:
				_localctx = new StmtBreakContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(56); match(22);
				}
				break;

			case 6:
				_localctx = new StmtFnCallContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(57); fnCall();
				}
				break;

			case 7:
				_localctx = new StmtAssignContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(58); expr(0);
				setState(59); match(18);
				setState(60); expr(0);
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
			setState(64); varScopeModifier();
			setState(65); varTypeModifier();
			setState(66); match(VAR_ID);
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
			setState(68);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 10) | (1L << 11) | (1L << 23))) != 0)) ) {
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
			setState(70);
			_la = _input.LA(1);
			if ( !(_la==17 || _la==24) ) {
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
			setState(72); match(12);
			setState(73); expr(0);
			setState(74); match(26);
			setState(75); stmtList();
			setState(76); match(20);
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
			setState(78); match(6);
			setState(79); expr(0);
			setState(81); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(80); whenClause();
				}
				}
				setState(83); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==8 );
			setState(86);
			_la = _input.LA(1);
			if (_la==5) {
				{
				setState(85); whenOtherClause();
				}
			}

			setState(88); match(14);
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
			setState(90); match(8);
			setState(91); expr(0);
			setState(92); stmtList();
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
			setState(94); match(5);
			setState(95); stmtList();
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
	public static class ExprDataBufferContext extends ExprContext {
		public BufferRefContext bufferRef() {
			return getRuleContext(BufferRefContext.class,0);
		}
		public ExprDataBufferContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprDataBuffer(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprDefnRefContext extends ExprContext {
		public DefnRefContext defnRef() {
			return getRuleContext(DefnRefContext.class,0);
		}
		public ExprDefnRefContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprDefnRef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprVarContext extends ExprContext {
		public TerminalNode VAR_ID() { return getToken(PeopleCodeParser.VAR_ID, 0); }
		public ExprVarContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprVar(this);
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
		public TerminalNode SYS_VAR_ID() { return getToken(PeopleCodeParser.SYS_VAR_ID, 0); }
		public ExprSystemVarContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitExprSystemVar(this);
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
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				_localctx = new ExprParenthesizedContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(98); match(3);
				setState(99); expr(0);
				setState(100); match(16);
				}
				break;

			case 2:
				{
				_localctx = new ExprDefnRefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(102); defnRef();
				}
				break;

			case 3:
				{
				_localctx = new ExprLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(103); literal();
				}
				break;

			case 4:
				{
				_localctx = new ExprDataBufferContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(104); bufferRef();
				}
				break;

			case 5:
				{
				_localctx = new ExprSystemVarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(105); match(SYS_VAR_ID);
				}
				break;

			case 6:
				{
				_localctx = new ExprVarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(106); match(VAR_ID);
				}
				break;

			case 7:
				{
				_localctx = new ExprFnCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(107); fnCall();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(115);
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
					setState(110);
					if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
					setState(111); match(18);
					setState(112); expr(2);
					}
					} 
				}
				setState(117);
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
		enterRule(_localctx, 24, RULE_exprList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118); expr(0);
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==2) {
				{
				{
				setState(119); match(2);
				setState(120); expr(0);
				}
				}
				setState(125);
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
		enterRule(_localctx, 26, RULE_fnCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126); match(FUNC_ID);
			setState(127); match(3);
			setState(129);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 3) | (1L << 4) | (1L << 9) | (1L << 10) | (1L << 13) | (1L << 17) | (1L << 25) | (1L << DecimalLiteral) | (1L << IntegerLiteral) | (1L << VAR_ID) | (1L << BUFFER_ID) | (1L << FUNC_ID) | (1L << SYS_VAR_ID))) != 0)) {
				{
				setState(128); exprList();
				}
			}

			setState(131); match(16);
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

	public static class DefnRefContext extends ParserRuleContext {
		public DefnKeywordContext defnKeyword() {
			return getRuleContext(DefnKeywordContext.class,0);
		}
		public TerminalNode BUFFER_ID() { return getToken(PeopleCodeParser.BUFFER_ID, 0); }
		public DefnRefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defnRef; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitDefnRef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefnRefContext defnRef() throws RecognitionException {
		DefnRefContext _localctx = new DefnRefContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_defnRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133); defnKeyword();
			setState(134); match(15);
			setState(135); match(BUFFER_ID);
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
			setState(137);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 10) | (1L << 17) | (1L << 25))) != 0)) ) {
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

	public static class BufferRefContext extends ParserRuleContext {
		public TerminalNode VAR_ID() { return getToken(PeopleCodeParser.VAR_ID, 0); }
		public BufferFldPropertyContext bufferFldProperty() {
			return getRuleContext(BufferFldPropertyContext.class,0);
		}
		public TerminalNode BUFFER_ID(int i) {
			return getToken(PeopleCodeParser.BUFFER_ID, i);
		}
		public List<TerminalNode> BUFFER_ID() { return getTokens(PeopleCodeParser.BUFFER_ID); }
		public BufferRefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bufferRef; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitBufferRef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BufferRefContext bufferRef() throws RecognitionException {
		BufferRefContext _localctx = new BufferRefContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_bufferRef);
		int _la;
		try {
			int _alt;
			setState(151);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(139); match(BUFFER_ID);
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(140);
				_la = _input.LA(1);
				if ( !(_la==VAR_ID || _la==BUFFER_ID) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(143); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(141); match(15);
						setState(142); match(BUFFER_ID);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(145); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
				} while ( _alt!=2 && _alt!=-1 );
				setState(149);
				switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
				case 1:
					{
					setState(147); match(15);
					setState(148); bufferFldProperty();
					}
					break;
				}
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

	public static class BufferFldPropertyContext extends ParserRuleContext {
		public BufferFldPropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bufferFldProperty; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PeopleCodeVisitor ) return ((PeopleCodeVisitor<? extends T>)visitor).visitBufferFldProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BufferFldPropertyContext bufferFldProperty() throws RecognitionException {
		BufferFldPropertyContext _localctx = new BufferFldPropertyContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_bufferFldProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153); match(7);
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
		enterRule(_localctx, 36, RULE_literal);
		try {
			setState(158);
			switch (_input.LA(1)) {
			case DecimalLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(155); match(DecimalLiteral);
				}
				break;
			case IntegerLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(156); match(IntegerLiteral);
				}
				break;
			case 1:
			case 4:
			case 9:
			case 13:
				enterOuterAlt(_localctx, 3);
				{
				setState(157); booleanLiteral();
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
		enterRule(_localctx, 38, RULE_booleanLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 4) | (1L << 9) | (1L << 13))) != 0)) ) {
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3$\u00a5\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\3\2\3\2\3\3\3\3\3\4\3\4\3\4\7\4\62\n\4"+
		"\f\4\16\4\65\13\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5A\n\5\3\6"+
		"\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\6\nT"+
		"\n\n\r\n\16\nU\3\n\5\nY\n\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\ro\n\r\3\r\3\r\3\r\7\rt\n"+
		"\r\f\r\16\rw\13\r\3\16\3\16\3\16\7\16|\n\16\f\16\16\16\177\13\16\3\17"+
		"\3\17\3\17\5\17\u0084\n\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\22"+
		"\3\22\3\22\3\22\6\22\u0092\n\22\r\22\16\22\u0093\3\22\3\22\5\22\u0098"+
		"\n\22\5\22\u009a\n\22\3\23\3\23\3\24\3\24\3\24\5\24\u00a1\n\24\3\25\3"+
		"\25\3\25\2\26\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(\2\7\4\2\f\r"+
		"\31\31\4\2\23\23\32\32\5\2\f\f\23\23\33\33\3\2\37 \6\2\3\3\6\6\13\13\17"+
		"\17\u00a7\2*\3\2\2\2\4,\3\2\2\2\6\63\3\2\2\2\b@\3\2\2\2\nB\3\2\2\2\fF"+
		"\3\2\2\2\16H\3\2\2\2\20J\3\2\2\2\22P\3\2\2\2\24\\\3\2\2\2\26`\3\2\2\2"+
		"\30n\3\2\2\2\32x\3\2\2\2\34\u0080\3\2\2\2\36\u0087\3\2\2\2 \u008b\3\2"+
		"\2\2\"\u0099\3\2\2\2$\u009b\3\2\2\2&\u00a0\3\2\2\2(\u00a2\3\2\2\2*+\5"+
		"\4\3\2+\3\3\2\2\2,-\5\6\4\2-\5\3\2\2\2./\5\b\5\2/\60\7\25\2\2\60\62\3"+
		"\2\2\2\61.\3\2\2\2\62\65\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64\7\3\2"+
		"\2\2\65\63\3\2\2\2\66A\5\n\6\2\67A\5\20\t\28A\5\22\n\29A\7\27\2\2:A\7"+
		"\30\2\2;A\5\34\17\2<=\5\30\r\2=>\7\24\2\2>?\5\30\r\2?A\3\2\2\2@\66\3\2"+
		"\2\2@\67\3\2\2\2@8\3\2\2\2@9\3\2\2\2@:\3\2\2\2@;\3\2\2\2@<\3\2\2\2A\t"+
		"\3\2\2\2BC\5\f\7\2CD\5\16\b\2DE\7\37\2\2E\13\3\2\2\2FG\t\2\2\2G\r\3\2"+
		"\2\2HI\t\3\2\2I\17\3\2\2\2JK\7\16\2\2KL\5\30\r\2LM\7\34\2\2MN\5\6\4\2"+
		"NO\7\26\2\2O\21\3\2\2\2PQ\7\b\2\2QS\5\30\r\2RT\5\24\13\2SR\3\2\2\2TU\3"+
		"\2\2\2US\3\2\2\2UV\3\2\2\2VX\3\2\2\2WY\5\26\f\2XW\3\2\2\2XY\3\2\2\2YZ"+
		"\3\2\2\2Z[\7\20\2\2[\23\3\2\2\2\\]\7\n\2\2]^\5\30\r\2^_\5\6\4\2_\25\3"+
		"\2\2\2`a\7\7\2\2ab\5\6\4\2b\27\3\2\2\2cd\b\r\1\2de\7\5\2\2ef\5\30\r\2"+
		"fg\7\22\2\2go\3\2\2\2ho\5\36\20\2io\5&\24\2jo\5\"\22\2ko\7\"\2\2lo\7\37"+
		"\2\2mo\5\34\17\2nc\3\2\2\2nh\3\2\2\2ni\3\2\2\2nj\3\2\2\2nk\3\2\2\2nl\3"+
		"\2\2\2nm\3\2\2\2ou\3\2\2\2pq\6\r\2\3qr\7\24\2\2rt\5\30\r\2sp\3\2\2\2t"+
		"w\3\2\2\2us\3\2\2\2uv\3\2\2\2v\31\3\2\2\2wu\3\2\2\2x}\5\30\r\2yz\7\4\2"+
		"\2z|\5\30\r\2{y\3\2\2\2|\177\3\2\2\2}{\3\2\2\2}~\3\2\2\2~\33\3\2\2\2\177"+
		"}\3\2\2\2\u0080\u0081\7!\2\2\u0081\u0083\7\5\2\2\u0082\u0084\5\32\16\2"+
		"\u0083\u0082\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0086"+
		"\7\22\2\2\u0086\35\3\2\2\2\u0087\u0088\5 \21\2\u0088\u0089\7\21\2\2\u0089"+
		"\u008a\7 \2\2\u008a\37\3\2\2\2\u008b\u008c\t\4\2\2\u008c!\3\2\2\2\u008d"+
		"\u009a\7 \2\2\u008e\u0091\t\5\2\2\u008f\u0090\7\21\2\2\u0090\u0092\7 "+
		"\2\2\u0091\u008f\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0091\3\2\2\2\u0093"+
		"\u0094\3\2\2\2\u0094\u0097\3\2\2\2\u0095\u0096\7\21\2\2\u0096\u0098\5"+
		"$\23\2\u0097\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u009a\3\2\2\2\u0099"+
		"\u008d\3\2\2\2\u0099\u008e\3\2\2\2\u009a#\3\2\2\2\u009b\u009c\7\t\2\2"+
		"\u009c%\3\2\2\2\u009d\u00a1\7\35\2\2\u009e\u00a1\7\36\2\2\u009f\u00a1"+
		"\5(\25\2\u00a0\u009d\3\2\2\2\u00a0\u009e\3\2\2\2\u00a0\u009f\3\2\2\2\u00a1"+
		"\'\3\2\2\2\u00a2\u00a3\t\6\2\2\u00a3)\3\2\2\2\16\63@UXnu}\u0083\u0093"+
		"\u0097\u0099\u00a0";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}