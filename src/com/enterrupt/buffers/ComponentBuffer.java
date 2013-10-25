package com.enterrupt.buffers;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.Collections;
import com.enterrupt.BuildAssistant;
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

