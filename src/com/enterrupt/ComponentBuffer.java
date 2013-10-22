package com.enterrupt;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.StringBuilder;
import com.enterrupt.pt_objects.*;

public class ComponentBuffer {

	private static ScrollBuffer compBuffer;

	public static int currScrollLevel;
	public static ScrollBuffer currSB;

	public static void init() {
		compBuffer = new ScrollBuffer(0, null, null);
		currSB = compBuffer;
	}

	public static void addPageField(PageField pf) {

		// Ensure we're pointing at the correct scroll buffer.
		pointAtScroll(pf.scrollLevel, pf.scrollLevelPrimaryRecName);

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

		//System.out.println("Scroll level set to " + currSB.scrollLevel + " with primary rec name " + 
		//	currSB.primaryRecName);
	}

	public static void print() {
		System.out.println("\n\nPRINTING COMPONENT BUFFER");
		System.out.println("=========================");
		System.out.println(compBuffer.toString(0));
	}
}

