package com.enterrupt.parsers;

import com.enterrupt.pt_objects.PeopleCodeProg;
import com.enterrupt.tokens.*;
import java.lang.StringBuilder;
import com.enterrupt.PCParser;

public abstract class StringParser extends ElementParser {

    public String getString() {
        byte b;
        PeopleCodeProg prog = PCParser.prog;
        StringBuilder builder = new StringBuilder();

        while((b = prog.readNextByte()) != 0) {
            prog.byteCursorPos++;       //skip 0
            if(b == (byte) 10) {
                builder.append('\n');
            } else {
                builder.append((char) b);
            }
        }
        prog.byteCursorPos++;
        return builder.toString();
    }
}

