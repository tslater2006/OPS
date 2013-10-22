package com.enterrupt.pt_objects;

public class PageField {

    public String RECNAME;
    public String FIELDNAME;
    public byte FIELDUSE;
	public String SUBPNLNAME;

    public int scrollLevel; // calculated using OCCURSLEVEL

	public boolean beginsScroll = false; 	// when true, tells component buffer to shift to sub scroll.

    public PageField(String recname, String fieldname) {
        this.RECNAME = recname;
        this.FIELDNAME = fieldname;
    }
}
