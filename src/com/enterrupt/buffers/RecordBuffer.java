package com.enterrupt.buffers;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.Collections;
import com.enterrupt.BuildAssistant;
import com.enterrupt.pt_objects.*;

public class RecordBuffer implements IStreamableBuffer {

    public String recName;
    public int scrollLevel;
    public boolean isPrimaryScrollRecordBuffer;
    public HashMap<String, RecordFieldBuffer> fieldBufferTable;
    public ArrayList<RecordFieldBuffer> fieldBuffers;
    private boolean hasBeenExpanded;

	// Used for reading.
	private boolean hasEmittedSelf = false;
	private int fieldBufferCursor = 0;

    public RecordBuffer(String r, int scrollLevel, String primaryRecName) {
        this.recName = r;
        this.scrollLevel = scrollLevel;

        if(primaryRecName != null && this.recName.equals(primaryRecName)) {
            this.isPrimaryScrollRecordBuffer = true;
        }

        this.fieldBufferTable = new HashMap<String, RecordFieldBuffer>();
        this.fieldBuffers = new ArrayList<RecordFieldBuffer>();
    }

    public void addPageField(String RECNAME, String FIELDNAME) throws Exception {

        RecordFieldBuffer f = this.fieldBufferTable.get(FIELDNAME);
        if(f == null) {
            f = new RecordFieldBuffer(RECNAME, FIELDNAME, this);
            this.fieldBufferTable.put(f.fldName, f);
            this.fieldBuffers.add(f);
            f.checkFieldBufferRules(); // Ensure this is done after adding to the table, could cause infinte loop ot$
        }
    }

    public void expandEntireRecordIntoBuffer() throws Exception {

        if(!hasBeenExpanded) {

            fieldBufferTable.clear();
            fieldBuffers.clear();
            Record recDefn = BuildAssistant.getRecordDefn(this.recName);
            ArrayList<RecordField> expandedFieldList = recDefn.getExpandedFieldList();

            for(RecordField fld : expandedFieldList) {
                // Note: the true RECNAME is preserved in the FieldBuffer; if the field is in a subrecord,
                // the RECNAME in the FieldBuffer will be that of the subrecord itself.
                RecordFieldBuffer fldBuffer = new RecordFieldBuffer(fld.RECNAME, fld.FIELDNAME, this);
                this.fieldBufferTable.put(fldBuffer.fldName, fldBuffer);
                this.fieldBuffers.add(fldBuffer);
            }

            hasBeenExpanded = true;
        }
    }

    public IStreamableBuffer next() {

		if(!this.hasEmittedSelf) {
			this.hasEmittedSelf = true;

			/**
			 * The expansion routine returns fields in order, so sorting the
			 * expanded array would mess up the proper order, since subrecord fields
			 * would be interleaved with the parent's fields (order is determined by FIELDNUM).
			 */
			if(!this.hasBeenExpanded) {
				Collections.sort(this.fieldBuffers);
			}
			return this;
		}

		if(this.fieldBufferCursor < this.fieldBuffers.size()) {
			RecordFieldBuffer fbuf = this.fieldBuffers.get(this.fieldBufferCursor);
			IStreamableBuffer toRet = fbuf.next();
			if(toRet != null) {
				return toRet;
			} else {
				this.fieldBufferCursor++;
				return this.next();
			}
		}

		return null;
    }

    public void resetCursors() {

		this.hasEmittedSelf = false;
		this.fieldBufferCursor = 0;
    }
}

