package com.enterrupt.pt_objects;

public class PageField {

    public String RECNAME;
    public String FIELDNAME;
    public byte FIELDUSE;
    public int OCCURSLEVEL;

    public PageField(String recname, String fieldname) {
        this.RECNAME = recname;
        this.FIELDNAME = fieldname;
    }
}
