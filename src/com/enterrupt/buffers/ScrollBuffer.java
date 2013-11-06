package com.enterrupt.buffers;

import java.util.*;
import java.lang.StringBuilder;
import com.enterrupt.pt.*;
import com.enterrupt.pt.pages.*;

public class ScrollBuffer implements IStreamableBuffer {

    public int scrollLevel;
    public String primaryRecName;
    public ScrollBuffer parent;

    public HashMap<String, RecordBuffer> recBufferTable;
    public ArrayList<RecordBuffer> orderedRecBuffers;

    public HashMap<String, ScrollBuffer> scrollBufferTable;
    public ArrayList<ScrollBuffer> orderedScrollBuffers;

	// Used for reading.
	private boolean hasEmittedSelf = false;
	private int recBufferCursor = 0;
	private int scrollBufferCursor = 0;

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

	public IStreamableBuffer next() {

		if(!this.hasEmittedSelf) {
			this.hasEmittedSelf = true;
			return this;
		}

		if(this.recBufferCursor < this.orderedRecBuffers.size()) {
			RecordBuffer rbuf = this.orderedRecBuffers.get(this.recBufferCursor);
			IStreamableBuffer toRet = rbuf.next();
			if(toRet != null) {
				return toRet;
			} else {
				this.recBufferCursor++;
				return this.next();
			}
		}

		if(this.scrollBufferCursor < this.orderedScrollBuffers.size()) {
			ScrollBuffer sbuf = this.orderedScrollBuffers.get(this.scrollBufferCursor);
			IStreamableBuffer toRet = sbuf.next();
			if(toRet != null) {
				return toRet;
			} else {
				this.scrollBufferCursor++;
				return this.next();
			}
		}

		return null;
	}

	public void resetCursors() {

		this.hasEmittedSelf = false;
		this.recBufferCursor = 0;
		this.scrollBufferCursor = 0;

		for(RecordBuffer rbuf : this.orderedRecBuffers) {
			rbuf.resetCursors();
		}

		for(ScrollBuffer sbuf : this.orderedScrollBuffers) {
			sbuf.resetCursors();
		}
	}
}
