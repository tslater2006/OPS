package com.enterrupt;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.Collections;
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
            r = new RecordBuffer(tok.RECNAME, this.scrollLevel, this.primaryRecName);
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
	public int scrollLevel;
	public boolean isPrimaryScrollRecordBuffer;
    public HashMap<String, RecordFieldBuffer> fieldBufferTable;
	public ArrayList<RecordFieldBuffer> unorderedFieldBuffers;
	private boolean hasBeenExpanded;

    public RecordBuffer(String r, int scrollLevel, String primaryRecName) {
        this.recName = r;
		this.scrollLevel = scrollLevel;

		if(primaryRecName != null && this.recName.equals(primaryRecName)) {
			this.isPrimaryScrollRecordBuffer = true;
		}

        this.fieldBufferTable = new HashMap<String, RecordFieldBuffer>();
		this.unorderedFieldBuffers = new ArrayList<RecordFieldBuffer>();
    }

    public void addPageField(String RECNAME, String FIELDNAME) throws Exception {

        RecordFieldBuffer f = this.fieldBufferTable.get(FIELDNAME);
        if(f == null) {
            f = new RecordFieldBuffer(RECNAME, FIELDNAME, this);
	        this.fieldBufferTable.put(f.fldName, f);
			this.unorderedFieldBuffers.add(f);
			f.checkFieldBufferRules(); // Ensure this is done after adding to the table, could cause infinte loop otherwise.
        }
    }

	public void expandEntireRecordIntoBuffer() throws Exception {

		if(!hasBeenExpanded) {

			fieldBufferTable.clear();
			unorderedFieldBuffers.clear();
			Record recDefn = BuildAssistant.getRecordDefn(this.recName);
			ArrayList<RecordField> expandedFieldList = recDefn.getExpandedFieldList();

			for(RecordField fld : expandedFieldList) {
				// Note: the true RECNAME is preserved in the FieldBuffer; if the field is in a subrecord,
				// the RECNAME in the FieldBuffer will be that of the subrecord itself.
				RecordFieldBuffer fldBuffer = new RecordFieldBuffer(fld.RECNAME, fld.FIELDNAME, this);
				this.fieldBufferTable.put(fldBuffer.fldName, fldBuffer);
				this.unorderedFieldBuffers.add(fldBuffer);
			}

			hasBeenExpanded = true;
		}
	}

    public String toString(int indent) {

        StringBuilder b = new StringBuilder();

        for(int i=0; i<indent; i++) {b.append(" ");}
        b.append("- " + this.recName + "\n");

		// The expansion routine returns fields in order; running sort on that would mess up the order.
		if(!hasBeenExpanded) {
       		Collections.sort(this.unorderedFieldBuffers);
		}

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

		if(this.fldDefn == null) {
			System.out.println("[ERROR] Field is not on the record. Likely on a subrecord. Subrecord traversal in RecordFieldBuffer not supported at this time.");
			System.exit(1);
		}
    }

	public void checkFieldBufferRules() throws Exception {

		/**
		 * If a level 0, non-derived record contains at least one field
	 	 * that is neither a search key nor an alternate key, all of the record's fields
		 * should be present in the component buffer.
		 */
		if(this.parentRecordBuffer.scrollLevel == 0
			&& !this.recDefn.isDerivedWorkRecord()
			&& !this.fldDefn.isSearchKey()
			&& !this.fldDefn.isAlternateSearchKey()) {

			this.parentRecordBuffer.expandEntireRecordIntoBuffer();
		}

		/**
		 * All the fields on a primary scroll record at level 1 or higher
		 * should be present in the component buffer.
		 */
		if(this.parentRecordBuffer.scrollLevel > 0
			&& this.parentRecordBuffer.isPrimaryScrollRecordBuffer) {

			this.parentRecordBuffer.expandEntireRecordIntoBuffer();
		}
	}

    public String toString() {
        return fldName;
    }

    public int compareTo(RecordFieldBuffer fb) {
        int a = this.fldDefn.FIELDNUM;
		int b = fb.fldDefn.FIELDNUM;
		return a > b ? +1 : a < b ? -1 : 0;
    }
}

