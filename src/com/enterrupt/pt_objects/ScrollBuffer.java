package com.enterrupt.pt_objects;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

public class ScrollBuffer {

	public int scrollLevel;
    public String primaryRecName;
    public ScrollBuffer parent;

    public HashMap<String, RecordBuffer> recBufferTable;
    public ArrayList<RecordBuffer> orderedRecBuffers;

    public HashMap<String, ScrollBuffer> scrollBufferTable;
    public ArrayList<ScrollBuffer> orderedScrollBuffers;

    public ScrollBuffer(int level, String primaryRecName, ScrollBuffer parent) {
        this.scrollLevel = level;
        this.primaryRecName = primaryRecName;
        this.parent = parent;
        this.recBufferTable = new HashMap<String, RecordBuffer>();
        this.orderedRecBuffers = new ArrayList<RecordBuffer>();
        this.scrollBufferTable = new HashMap<String, ScrollBuffer>();
        this.orderedScrollBuffers = new ArrayList<ScrollBuffer>();
    }

    public void addPageField(PageField pf) {

        RecordBuffer r = this.recBufferTable.get(pf.RECNAME);
        if(r == null) {
            r = new RecordBuffer(pf.RECNAME);
            orderedRecBuffers.add(r);
        }

        r.addField(pf.FIELDNAME);
        this.recBufferTable.put(pf.RECNAME, r);
    }

    public ScrollBuffer getChildScroll(String targetPrimaryRecName) {

        ScrollBuffer sb = this.scrollBufferTable.get(targetPrimaryRecName);
        if(sb == null) {
            sb = new ScrollBuffer(this.scrollLevel + 1, targetPrimaryRecName, this);
			this.scrollBufferTable.put(targetPrimaryRecName, sb);
			this.orderedScrollBuffers.add(sb);
        }
        return sb;
    }

    public String toString(int indent) {

        StringBuilder b = new StringBuilder();

		for(int i=0; i<indent; i++) {b.append(" ");}
		b.append("Scroll - Level " + this.scrollLevel + "\tPrimary Record: " + this.primaryRecName + "\n");

		for(int i=0; i<indent; i++) {b.append(" ");}
		b.append("=========================================================\n");

		for(RecordBuffer r : this.orderedRecBuffers) {
            b.append(r.toString(indent + 2));
        }
		for(ScrollBuffer sb : this.orderedScrollBuffers) {
			b.append(sb.toString(indent + 2));
		}
        //b.append("Level " + this.scrollLevel + " records: " + this.orderedRecBuffers.size() + "\n");
		//b.append("Level " + (this.scrollLevel + 1) + " scrolls: " + this.orderedScrollBuffers.size() + "\n"); 
        return b.toString();
    }
}

class RecordBuffer {

    public String recName;
    public HashMap<String, FieldBuffer> fieldBufferTable;
	public ArrayList<FieldBuffer> orderedFieldBuffers;

    public RecordBuffer(String r) {
        this.recName = r;
        this.fieldBufferTable = new HashMap<String, FieldBuffer>();
		this.orderedFieldBuffers = new ArrayList<FieldBuffer>();
    }

    public void addField(String fldname) {
        FieldBuffer f = this.fieldBufferTable.get(fldname);
        if(f == null) {
            f = new FieldBuffer(fldname);
			orderedFieldBuffers.add(f);
        }
        this.fieldBufferTable.put(fldname, f);
    }

	public String toString(int indent) {

		StringBuilder b = new StringBuilder();

		for(int i=0; i<indent; i++) {b.append(" ");}
		b.append("- " + this.recName + "\n");

		//Collections.sort(this.orderedFieldBuffers);
		for(FieldBuffer f : this.orderedFieldBuffers) {
			for(int i=0; i<(indent + 2); i++) {b.append(" ");}
			b.append("+ " + f.toString() + "\n");
		}
		return b.toString();
	}
}

class FieldBuffer implements Comparable<FieldBuffer> {

    public String fldName;

    public FieldBuffer(String f) {
        this.fldName = f;
    }

	public String toString() {
		return fldName;
	}

	public int compareTo(FieldBuffer fb) {
		return this.fldName.compareTo(fb.fldName);
	}
}
