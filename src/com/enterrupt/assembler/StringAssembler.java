package com.enterrupt.assembler;

import java.lang.StringBuilder;
import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

public abstract class StringAssembler extends ElementAssembler {

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

