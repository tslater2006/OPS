package com.enterrupt.buffers;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.Collections;
import com.enterrupt.BuildAssistant;
import com.enterrupt.pt_objects.*;

public class RecordBuffer {

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
            f.checkFieldBufferRules(); // Ensure this is done after adding to the table, could cause infinte loop ot$
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

