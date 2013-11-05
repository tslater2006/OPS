package com.enterrupt.pt_objects;

/**
 * Used when loading defintions and programs referenced
 * by a program; the type of program being loaded at the root
 * call dictates how recursion should proceed.
 */
public enum LFlag {
	COMPONENT,
	RECORD
}
