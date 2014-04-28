package com.enterrupt.buffers;

import java.util.*;
import java.lang.StringBuilder;
import com.enterrupt.pt.*;
import com.enterrupt.pt.pages.*;
import com.enterrupt.types.*;
import org.apache.logging.log4j.*;
import com.enterrupt.runtime.*;

public class ComponentBuffer {

	public static int currScrollLevel;
	public static ScrollBuffer currSB;
	public static ScrollBuffer compBuffer;
	public static PTRecord searchRecord;

	private static Logger log = LogManager.getLogger(ComponentBuffer.class.getName());

	static {
		compBuffer = new ScrollBuffer(0, null, null);
		currSB = compBuffer;
	}

	public static void addPageField(PgToken tok, int level, String primaryRecName) {

		// Ensure that we're pointing at the correct scroll buffer.
		pointAtScroll(level, primaryRecName);
		currSB.addPageField(tok);
	}

	public static void pointAtScroll(int targetScrollLevel, String targetPrimaryRecName) {

		// Remember that there's only one scroll level at 0.
		if(currSB.scrollLevel == targetScrollLevel &&
			(currSB.scrollLevel == 0 || currSB.primaryRecName.equals(targetPrimaryRecName))) {
			return;
		}

		while(currScrollLevel < targetScrollLevel) {
			currSB = currSB.getChildScroll(targetPrimaryRecName);
			currScrollLevel = currSB.scrollLevel;
		}

		while(currScrollLevel > targetScrollLevel) {
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
	}

	/**
	 * This is the entry point / wrapper for reading
	 * buffers out of the component buffer in a recursive, depth-first
	 * manner.
	 */
	public static IStreamableBuffer next() {
		return compBuffer.next();
	}

	/**
	 * Propagates cursor resets to all
	 * buffers.
	 */
	public static void resetCursors() {
		compBuffer.resetCursors();
	}

	public static void printStructure() {
    	int indent = 0;
        IStreamableBuffer buf;

        ComponentBuffer.resetCursors();
        while((buf = ComponentBuffer.next()) != null) {

            if(buf instanceof ScrollBuffer) {

                ScrollBuffer sbuf = (ScrollBuffer) buf;
                indent = sbuf.scrollLevel * 3;

                StringBuilder b = new StringBuilder();
                for(int i=0; i<indent; i++){b.append(" ");}
                b.append("Scroll - Level ").append(sbuf.scrollLevel)
                    .append("\tPrimary Record: ").append(sbuf.primaryRecName);
                for(int i=0; i<indent; i++){b.append(" ");}
                log.info(b.toString());
                log.info("=======================================================");

            } else if(buf instanceof RecordBuffer) {

                RecordBuffer rbuf = (RecordBuffer) buf;

                StringBuilder b = new StringBuilder();
                for(int i=0; i<indent; i++){b.append(" ");}
                b.append(" + ").append(rbuf.recName);
                log.info(b.toString());

            } else {
                RecordFieldBuffer fbuf = (RecordFieldBuffer) buf;

                StringBuilder b = new StringBuilder();
                for(int i=0; i<indent; i++){b.append(" ");}
                b.append("   - ").append(fbuf.fldName);
                log.info(b.toString());
            }
        }
	}

	/**
     * MQUINN 04-27-2014
     * Filling the component buffer involves multiple passes, total
     * involved is dynamic I believe. I'm calling this just the first
     * pass for now. The goal is to delegate as much of the filling part
     * to the buffers themselves, as that will be the same across all passes.
	 */
	public static void firstPassFill() {
        IStreamableBuffer buf;

        ComponentBuffer.resetCursors();
        while((buf = ComponentBuffer.next()) != null) {

            if(buf instanceof RecordBuffer) {
                RecordBuffer rbuf = (RecordBuffer) buf;

				/**
				 * For the first pass, only fill table and view
				 * (i.e., not derived) buffers that have at least
				 * one key (altho fill will be canceled if not all
				 * key values are known).
				 */
				Record recDefn = DefnCache.getRecord(rbuf.recName);
				if(recDefn.isTable() || recDefn.isView()
					&& recDefn.hasAnyKeys()) {
					rbuf.firstPassFill();
				}
            }
        }
	}
}
