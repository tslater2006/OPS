package com.enterrupt;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.StringBuilder;
import com.enterrupt.pt_objects.PageField;

public class ComponentBuffer {

	public static ScrollBuffer level0;

	public static void init() {
		level0 = new ScrollBuffer(0);
	}

	public static void addField(PageField pf) {

		if(pf.OCCURSLEVEL == 0) {
			level0.addField(pf.RECNAME, pf.FIELDNAME);
		}
	}

	public static void print() {
		System.out.println("\n\nPRINTING COMPONENT BUFFER");
		System.out.println("=========================");
		System.out.println(level0);
	}
}

class ScrollBuffer {

	public String primaryRecName;
	public int scrollLevel;

	public HashMap<String, RecordBuffer> records;
	public ArrayList<RecordBuffer> orderedRecords;

	public HashMap<String, ScrollBuffer> scrolls;

	public ScrollBuffer(int level) {
		this.scrollLevel = level;
		this.scrolls = new HashMap<String, ScrollBuffer>();
		this.records = new HashMap<String, RecordBuffer>();
		this.orderedRecords = new ArrayList<RecordBuffer>();
	}

	public void addField(String recname, String fldname) {
		RecordBuffer r = this.records.get(recname);
		if(r == null) {
			r = new RecordBuffer(recname);
			orderedRecords.add(r);
		}

		r.addField(fldname);
		this.records.put(recname, r);
	}

	public String toString() {

		StringBuilder b = new StringBuilder();
		for(RecordBuffer r : orderedRecords) {
			b.append(r.recName + "\n");
		}
		b.append("Total records: " + orderedRecords.size() + "\n");
		return b.toString();
	}
}

class RecordBuffer {

	public String recName;
	public HashMap<String, FieldBuffer> fields;

	public RecordBuffer(String r) {
		this.recName = r;
		this.fields = new HashMap<String, FieldBuffer>();
	}

	public void addField(String fldname) {
		FieldBuffer f = this.fields.get(fldname);
		if(f == null) {
			f = new FieldBuffer(fldname);
		}
		this.fields.put(fldname, f);
	}
}

class FieldBuffer {

	public String fldName;

	public FieldBuffer(String f) {
		this.fldName = f;
	}
}
