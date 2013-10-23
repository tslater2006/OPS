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

	public static void addPageField(PgToken tok, int level, String primaryRecName) throws Exception {

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

    public void addPageField(PgToken tok) throws Exception {

        RecordBuffer r = this.recBufferTable.get(tok.RECNAME);
        if(r == null) {
            r = new RecordBuffer(tok.RECNAME);
        	this.recBufferTable.put(r.recName, r);
            orderedRecBuffers.add(r);
        }

        r.addPageField(tok.RECNAME, tok.FIELDNAME);
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
    public HashMap<String, RecordFieldBuffer> fieldBufferTable;
	public ArrayList<RecordFieldBuffer> unorderedFieldBuffers;

    public RecordBuffer(String r) {
        this.recName = r;
        this.fieldBufferTable = new HashMap<String, RecordFieldBuffer>();
		this.unorderedFieldBuffers = new ArrayList<RecordFieldBuffer>();
    }

    public void addPageField(String RECNAME, String FIELDNAME) throws Exception {

        RecordFieldBuffer f = this.fieldBufferTable.get(FIELDNAME);
        if(f == null) {
            f = new RecordFieldBuffer(RECNAME, FIELDNAME, this);
	        this.fieldBufferTable.put(f.fldName, f);
			this.unorderedFieldBuffers.add(f);
			f.processPageField(); // Ensure this is done after adding to the table, could cause infinte loop otherwise.
        }
    }

    public String toString(int indent) {

        StringBuilder b = new StringBuilder();

        for(int i=0; i<indent; i++) {b.append(" ");}
        b.append("- " + this.recName + "\n");

        //Collections.sort(this.orderedRecordFieldBuffers);
        for(RecordFieldBuffer f : this.unorderedFieldBuffers) {
            for(int i=0; i<(indent + 2); i++) {b.append(" ");}
            b.append("+ " + f.toString() + "\n");
        }
        return b.toString();
    }
}

class RecordFieldBuffer implements Comparable<RecordFieldBuffer> {

    public String fldName;
	public Record recDefn;
	public RecordField fldDefn;
	public RecordBuffer parentRecordBuffer;

    public RecordFieldBuffer(String r, String f, RecordBuffer parent) throws Exception {
        this.fldName = f;
		this.recDefn = BuildAssistant.getRecordDefn(r);
		this.fldDefn = this.recDefn.fieldTable.get(this.fldName);
		this.parentRecordBuffer = parent;

		/**
		 * TODO: Traversing a record's subrecords here may be necessary
		 * in the future; skipping for now as it is not needed to form
		 * the component structure.
		 */
		if(this.fldDefn == null) {
			System.out.println("[ERROR] Field is not on the record. Likely on a subrecord. Subrecord traversal in RecordFieldBuffer not supported at this time.");
			System.exit(1);
		}
    }

	public void processPageField() throws Exception {
		if(this.fldDefn.isKey()) {
			for(Map.Entry<String, RecordField> cursor : this.recDefn.fieldTable.entrySet()) {
				if(!cursor.getKey().equals(this.fldName)) {
					this.parentRecordBuffer.addPageField(this.recDefn.RECNAME, cursor.getValue().FIELDNAME);
				}
			}
		}
	}

    public String toString() {
        return fldName;
    }

    public int compareTo(RecordFieldBuffer fb) {
        return this.fldName.compareTo(fb.fldName);
    }
}

