package com.enterrupt.pt_objects;

public class PageField {

    public String RECNAME;
    public String FIELDNAME;
    public byte FIELDUSE;
	public String SUBPNLNAME;
	public int FIELDTYPE;
	public Record recordDefn;
	public int OCCURSLEVEL;

    public int contextScrollLevel; // calculated using OCCURSLEVEL
	public String contextPrimaryRecName;

    public PageField(String recname, String fieldname) {
        this.RECNAME = recname;
        this.FIELDNAME = fieldname;
    }
}
