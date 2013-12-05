package com.enterrupt.pt;

import java.util.HashMap;

public class Reference {

	public String RECNAME;
	public String REFNAME;

	private String processedRef;
	public boolean isRecordFieldRef = false;

    public static final String[] refReservedWords;
	public static HashMap<String, String> refReservedWordsTable;

	static {

		/**
		 * TODO: REMOVE THIS. The ANTLR grammar should be
		 * the authoritative source (see definitionLiteral rule).
		 */
		refReservedWords = new String[] {"Component", "Panel", "RecName", "Scroll",
                         "MenuName", "BarName", "ItemName", "CompIntfc",
                         "Image", "Interlink", "StyleSheet", "FileLayout",
                         "Page", "PanelGroup", "Message", "BusProcess",
                         "BusEvent", "BusActivity", "Field", "Record",
                         "Operation", "SQL", "HTML"};

		refReservedWordsTable = new HashMap<String, String>();
		for(String s : refReservedWords) {
			refReservedWordsTable.put(s.toUpperCase(), s);
		}
	}

	public Reference(String recname, String refname) {

		this.processedRef = "";

		/**
	     * If the RECNAME is a reserved word, replace it with the appropriate
		 * camelcase equivalent and set the appropriate flag.
		 */
		boolean containsKeyword = false;
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
