package com.enterrupt.buffers;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.Collections;
import com.enterrupt.pt_objects.*;

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
