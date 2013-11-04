package com.enterrupt.pt_objects;

import java.util.HashMap;

public class Reference {

    public static final String[] refReservedWords = {"Component", "Panel", "RecName", "Scroll",
                                                   "MenuName", "BarName", "ItemName", "CompIntfc",
                                                   "Image", "Interlink", "StyleSheet", "FileLayout",
                                                   "Page", "PanelGroup", "Message", "BusProcess",
                                                   "BusEvent", "BusActivity", "Field", "Record",
                                                   "Operation", "SQL", "HTML"};
	public static HashMap<String, String> refReservedWordsTable;
	private String processedRef;
	public String RECNAME;
	public String REFNAME;
	public boolean isRecordFieldRef;

	public Reference(String recname, String refname) {

		if(refReservedWordsTable == null) {
			refReservedWordsTable = new HashMap<String, String>();
			for(String s : refReservedWords) {
				refReservedWordsTable.put(s.toUpperCase(), s);
			}
		}

		this.processedRef = "";
		this.isRecordFieldRef = false;

		boolean containsKeyword = false;

		/**
	     * If the RECNAME is a reserved word, replace it with the appropriate
		 * camelcase equivalent and set the appropriate flag.
		 */
		if(refReservedWordsTable.get(recname) != null) {
			// remember: this assigns the value (which is camelcase) rather than the key (which is upper).
			recname = refReservedWordsTable.get(recname);
			containsKeyword = true;
		}

		this.RECNAME = recname;
		this.REFNAME = refname;

		if(this.RECNAME != null && this.RECNAME.length() > 0) {
			this.processedRef = this.RECNAME + "." + this.REFNAME;
			if(!containsKeyword) {
				this.isRecordFieldRef = true;
			}
		} else {
			this.processedRef = this.REFNAME;
		}
	}

	public String getValue() {
		return this.processedRef;
	}
}
