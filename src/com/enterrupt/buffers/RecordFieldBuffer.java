package com.enterrupt.buffers;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.Collections;
import com.enterrupt.BuildAssistant;
import com.enterrupt.pt_objects.*;

public class RecordFieldBuffer implements Comparable<RecordFieldBuffer> {

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
            System.out.println("[ERROR] Field is not on the record. Likely on a subrecord. Subrecord traversal " +
				"in RecordFieldBuffer not supported at this time.");
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

