package com.enterrupt.parser;

import java.lang.StringBuilder;
import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

public abstract class ElementParser {

    public int format;
    public abstract Token parse(PeopleCodeByteStream stream);
    public abstract byte getStartByte();

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public boolean writesNonBlank() {
        return true;
    }
}

