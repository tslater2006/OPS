package com.enterrupt.parsers;

import com.enterrupt.pt_objects.PeopleCodeProg;
import java.lang.StringBuilder;
import com.enterrupt.tokens.*;
import com.enterrupt.Parser;

public abstract class ElementParser {

    public int format;
    public abstract void parse() throws Exception;
    public abstract Token interpret() throws Exception;
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

