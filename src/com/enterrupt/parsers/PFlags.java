package com.enterrupt.parsers;

public class PFlags {

	public static final int PUNCTUATION = 0x0;
	public static final int SPACE_BEFORE = 0x1;
	public static final int SPACE_AFTER = 0x2;
	public static final int NEWLINE_BEFORE = 0x4;
	public static final int NEWLINE_AFTER = 0x8;
	public static final int INCREASE_INDENT = 0x10;
	public static final int DECREASE_INDENT = 0x20;
	public static final int RESET_INDENT_BEFORE = 0x80;
	public static final int RESET_INDENT_AFTER = 0x100;
	public static final int NO_SPACE_BEFORE = 0x200;
	public static final int NO_SPACE_AFTER = 0x400;
	public static final int INCREASE_INDENT_ONCE = 0x800;
	public static final int AND_INDICATOR = 0x1000;
	public static final int NEWLINE_ONCE = 0x2000;
	public static final int IN_DECLARE = 0x4000;
	public static final int SEMICOLON = 0x8000;
	public static final int SPACE_BEFORE2 = 0x10000;
	public static final int COMMENT_ON_SAME_LINE = 0x20000;
	public static final int SPACE_BEFORE_AND_AFTER = SPACE_BEFORE | SPACE_AFTER;
	public static final int SPACE_BEFORE_AND_AFTER2 = SPACE_BEFORE2 | SPACE_BEFORE | SPACE_AFTER;
	public static final int IF_STYLE = NEWLINE_BEFORE | SPACE_BEFORE | SPACE_AFTER;
	public static final int NEWLINE_BEFORE_AND_AFTER = NEWLINE_BEFORE | NEWLINE_AFTER;
	public static final int THEN_STYLE = SPACE_BEFORE | SPACE_BEFORE2 | NEWLINE_AFTER |
										 SPACE_AFTER | INCREASE_INDENT;
	public static final int ENDIF_STYLE = NEWLINE_BEFORE | SPACE_BEFORE | DECREASE_INDENT | NEWLINE_AFTER;
}
