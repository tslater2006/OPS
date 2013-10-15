package com.enterrupt.interpreter;

public class Token {

    public static final int IF = 0x1;
    public static final int SYSTEM = 0x2;
    public static final int FUNCTION = 0x4;
    public static final int VARIABLE = 0x8;
    public static final int COMMENT = 0x10;
    public static final int REFERENCE = 0x20;
    public static final int L_PAREN = 0x30;
    public static final int R_PAREN = 0x40;
    public static final int EQUAL = 0x50;
    public static final int SEMICOLON = 0x60;
    public static final int END_IF = 0x70;
    public static final int THEN = 0x80;
    public static final int TRUE = 0x90;
    public static final int SYSTEM_VAR = SYSTEM | VARIABLE;
    public static final int SYSTEM_FN = SYSTEM | FUNCTION;

	public int type;

	public Token(int type) {
		this.type = type;
	}
}
