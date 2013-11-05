package com.enterrupt.parser;

import com.enterrupt.pt_objects.PeopleCodeByteStream;
import java.lang.StringBuilder;

public abstract class StringParser extends ElementParser {

    public String getString(PeopleCodeByteStream stream) {
        byte b;
        StringBuilder builder = new StringBuilder();

        while((b = stream.readNextByte()) != 0) {
            stream.incrementCursor();       //skip 0
            if(b == (byte) 10) {
                builder.append('\n');
            } else {
                builder.append((char) b);
            }
        }

		stream.incrementCursor();
        return builder.toString();
    }
}

