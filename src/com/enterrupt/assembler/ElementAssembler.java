package com.enterrupt.assembler;

import java.lang.StringBuilder;
import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

public abstract class ElementAssembler {

    public int format;
    public abstract void assemble(PeopleCodeByteStream stream);
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

