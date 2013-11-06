package com.enterrupt.parser;

import com.enterrupt.pt_objects.PeopleCodeByteStream;
import java.lang.StringBuilder;

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

