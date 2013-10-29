package com.enterrupt.parser;

import com.enterrupt.pt_objects.PeopleCodeProg;
import java.lang.StringBuilder;

public abstract class StringParser extends ElementParser {

    public String getString(PeopleCodeProg prog) {
        byte b;
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

