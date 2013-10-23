package com.enterrupt;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.StringBuilder;
import com.enterrupt.pt_objects.*;

public class ComponentBuffer {

	//private static HashMap<String, RecordBuffer> level0RecBufferTable;
	//private static ArrayList<RecordBuffer> level0OrderedRecBuffers;

	//private static HashMap<Integer, ScrollBuffer> scrollTable;

	public static int currScrollLevel;
	public static ScrollBuffer currSB;

	public static void init() {
		//scrollTable = new HashMap<Integer, ScrollBuffer>();
		//scrollTable.put(1, new ScrollBuffer(1));
		//scrollTable.put(2, new ScrollBuffer(2));
		//scrollTable.put(3, new ScrollBuffer(3));
	}

	/*public static void placeholder (PageField pf, int level, String primaryRecName) {

		ScrollBuffer sb = scrollTable.get(level);
		if(sb == null) {
			sb = new ScrollBuffer(level);
			scrollTable.put(level, sb);
		}

		// Ensure we're pointing at the correct scroll buffer.
		pointAtScroll(pf.contextScrollLevel, pf.contextPrimaryRecName);

		// The primary RECNAME for scroll level 0 isn't none until the very first page field is inserted.
		if(compBuffer.primaryRecName == null) {
			compBuffer = new ScrollBuffer(currSB.scrollLevel, pf.RECNAME, null);
			currSB = compBuffer;
		}
		//System.out.println("Primary rec: " + currSB.primaryRecName);
		currSB.addPageField(pf);
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

		// The scroll level may not have changed, but the target scroll buffer may have.
		if(currScrollLevel > 0 &&
			!currSB.primaryRecName.equals(targetPrimaryRecName)) {
			currSB = currSB.parent.getChildScroll(targetPrimaryRecName);
			currScrollLevel = currSB.scrollLevel;
		}

		//System.out.println("Scroll level set to " + currSB.scrollLevel + " with primary rec name " + 
		//	currSB.primaryRecName);
	}*/

	public static void print() {
		System.out.println("\n\nPRINTING COMPONENT BUFFER");
		System.out.println("=========================");
		//System.out.println(compBuffer.toString(0));
	}
}

