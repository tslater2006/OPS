package com.enterrupt;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.StringBuilder;
import com.enterrupt.pt_objects.*;

public class ComponentBuffer {

	public static int currScrollLevel;
	public static ScrollBuffer currSB;
	public static ScrollBuffer compBuffer;

	public static void init() {
		compBuffer = new ScrollBuffer(0, null, null);
		currSB = compBuffer;
	}

	public static void addPageField(PgToken tok, int level, String primaryRecName) {

		// Ensure we're pointing at the correct scroll buffer.
		pointAtScroll(level, primaryRecName);

		currSB.addPageField(tok);
	}

	public static void pointAtScroll(int targetScrollLevel, String targetPrimaryRecName) {

		//System.out.println("Switching to scroll level " + targetScrollLevel +
		//	" (primary rec name: " + targetPrimaryRecName + ")");

		// Remember that there's only one scroll level at 0.
		if(currSB.scrollLevel == targetScrollLevel &&
			(currSB.scrollLevel == 0 || currSB.primaryRecName.equals(targetPrimaryRecName))) {
			return;
		}

		while(currScrollLevel < targetScrollLevel) {
			//System.out.println("Decrementing...");
			currSB = currSB.getChildScroll(targetPrimaryRecName);
			currScrollLevel = currSB.scrollLevel;
		}

		while(currScrollLevel > targetScrollLevel) {
			//System.out.println("Incrementing...");
			currSB = currSB.parent;
			currScrollLevel = currSB.scrollLevel;
		}

		// The scroll level may not have changed, but if the targeted primary rec name
		// differs from the current, we need to change buffers.
		if(currScrollLevel > 0 &&
			!currSB.primaryRecName.equals(targetPrimaryRecName)) {
			currSB = currSB.parent.getChildScroll(targetPrimaryRecName);
			currScrollLevel = currSB.scrollLevel;
		}

		//System.out.println("Scroll level set to " + currSB.scrollLevel + " with primary rec name " + 
		//	currSB.primaryRecName);
	}

	public static void print() {
		System.out.println("\n\nPRINTING COMPONENT BUFFER");
		System.out.println("=========================");
		System.out.println(compBuffer.toString(0));
	}
}

class ScrollBuffer {

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

    public void addPageField(PgToken tok) {

        RecordBuffer r = this.recBufferTable.get(tok.RECNAME);
        if(r == null) {
            r = new RecordBuffer(tok.RECNAME);
            orderedRecBuffers.add(r);
        }

        r.addField(tok.FIELDNAME);
        this.recBufferTable.put(tok.RECNAME, r);
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

