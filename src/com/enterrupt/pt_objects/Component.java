package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Stack;
import java.sql.ResultSet;
import java.util.HashMap;
import java.sql.Blob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.parser.Parser;
import com.enterrupt.interpreter.Interpreter;
import com.enterrupt.buffers.*;

public class Component {

    public ArrayList<Page> pages;
    private String PNLGRPNAME;
    private String MARKET;
    private String SEARCHRECNAME; // name of search record for this component

	public ArrayList<ComponentPeopleCodeProg> orderedComponentProgs;
	private boolean hasListOfComponentPCBeenRetrieved = false;

	private class ScrollMarker {
		public String primaryRecName;
		public int scrollLevel;
		public AFlag src;

		public ScrollMarker() {}

		public ScrollMarker(int s, String p, AFlag a) {
			this.scrollLevel = s;
			this.primaryRecName = p;
			this.src = a;
		}
	}

    public Component(String pnlgrpname, String market) {
        this.PNLGRPNAME = pnlgrpname;
        this.MARKET = market;
        pages = new ArrayList<Page>();
    }

    public void loadInitialMetadata() throws Exception {

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSPNLGRPDEFN(this.PNLGRPNAME, this.MARKET);
        rs = pstmt.executeQuery();
        rs.next();
        this.SEARCHRECNAME = rs.getString("SEARCHRECNAME");
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSPNLGROUP(this.PNLGRPNAME, this.MARKET);
        rs = pstmt.executeQuery();
        while(rs.next()) {
			// all pages at the root of the component start at scroll level 0.
			Page p = new Page(rs.getString("PNLNAME"));
            this.pages.add(p);
        }
        rs.close();
        pstmt.close();
    }

	public void loadSearchRecord() throws Exception {

		// Loads the search record and puts it in cache.
		BuildAssistant.getRecordDefn(this.SEARCHRECNAME);
    }

    public void getListOfComponentPC() throws Exception {

		if(!this.hasListOfComponentPCBeenRetrieved) {

	        PreparedStatement pstmt;
	  		ResultSet rs;

			this.orderedComponentProgs = new ArrayList<ComponentPeopleCodeProg>();

	        pstmt = StmtLibrary.getPSPCMPROG_CompPCList(PSDefn.COMPONENT, this.PNLGRPNAME,
                                                        PSDefn.MARKET, this.MARKET);
   	        rs = pstmt.executeQuery();

     	    while(rs.next()) {

				String objectid3 = rs.getString("OBJECTID3").trim();
				String objectval3 = rs.getString("OBJECTVALUE3").trim();
				String objectid4 = rs.getString("OBJECTID4").trim();
				String objectval4 = rs.getString("OBJECTVALUE4").trim();
				String objectid5 = rs.getString("OBJECTID5").trim();
				String objectval5 = rs.getString("OBJECTVALUE5").trim();

				PeopleCodeProg prog = null;

				// Example: SSS_STUDENT_CENTER.GBL.PreBuild
				if(objectid3.equals(PSDefn.EVENT)) {
					prog = new ComponentPeopleCodeProg(this.PNLGRPNAME, this.MARKET,
						objectval3);

				// Example: SSS_STUDENT_CENTER.LS_SS_PERS_SRCH.SearchInit
				} else if(objectid3.equals(PSDefn.RECORD) && objectid4.equals(PSDefn.EVENT)) {
					prog = new ComponentPeopleCodeProg(this.PNLGRPNAME, this.MARKET,
						objectval3, objectval4);

				// Example: SSS_STUDENT_CENTER.LS_DERIVED_SSS_SCL.SS_CLS_SCHED_LINK.FieldChange
				} else if(objectid3.equals(PSDefn.RECORD) && objectid4.equals(PSDefn.FIELD) &&
							objectid5.equals(PSDefn.EVENT)) {
					prog = new ComponentPeopleCodeProg(this.PNLGRPNAME, this.MARKET,
						objectval3, objectval4, objectval5);

				} else {
					System.out.println("[ERROR] Unexpected type of Component PC encountered.");
					System.exit(1);
				}

				prog = BuildAssistant.getProgramOrCacheIfMissing(prog);
				this.orderedComponentProgs.add((ComponentPeopleCodeProg)prog);
     	   	}
      	   	rs.close();
      	   	pstmt.close();

			this.hasListOfComponentPCBeenRetrieved = true;
		}
    }

    public void loadAndRunRecordPConSearchRecord() throws Exception {

		Record recDefn = BuildAssistant.getRecordDefn(this.SEARCHRECNAME);
		recDefn.getListOfRecordPCPrograms();

        for(PeopleCodeProg prog : recDefn.orderedRecordProgs) {
            if(prog.event.equals("SearchInit")) {
				BuildAssistant.loadInitialMetadataForProg(prog.getDescriptor());

				Interpreter interpreter = new Interpreter(prog);
				interpreter.run();
            }
        }
    }

	public void loadAndRunComponentPConSearchRecord() throws Exception {

		for(ComponentPeopleCodeProg prog : this.orderedComponentProgs) {

			if(prog.RECNAME != null && prog.RECNAME.equals(this.SEARCHRECNAME)) {

				BuildAssistant.loadInitialMetadataForProg(prog.getDescriptor());
				BuildAssistant.loadReferencedProgsAndDefnsForProg(prog.getDescriptor(), 0, "CompPCMode");

				Interpreter interpreter = new Interpreter(prog);
				interpreter.run();
			}
		}
	}

	/**
	 * Note: The search record fill query can't be executed as a 100% prepared stmt b/c PT
	 * hardcodes the EMPLID in the query for some reason. This statement will also need to be
	 * dynamically generated based on the fields present, etc., which I'm skipping for now in order
     * to move on and get a better understanding of the patterns of SQL present in the next part
	 * of the trace file.
	 * TODO: Address the notes presented above.
	 */
	public void fillSearchRecord() throws Exception {

		PreparedStatement pstmt;
		ResultSet rs;

		pstmt = StmtLibrary.getSearchRecordFillQuery();
		rs = pstmt.executeQuery();

		while(rs.next()) {
			// Do nothing with records for now.
		}
		rs.close();
		pstmt.close();
	}

	public void loadPages() throws Exception {
		for(Page p : this.pages) {
			p.recursivelyLoadSubpages();
		}

		RecordPCListRequestBuffer.init();
		for(Page p : this.pages) {
			p.recursivelyLoadSecpages();
		}
		RecordPCListRequestBuffer.flushEntireTokenStream();
	}

	public void assembleComponentStructure() throws Exception {

		PgToken tok;
		PgTokenStream pfs;

		final byte REL_DISP_FLAG = (byte) 16;

		for(Page p : this.pages) {
			pfs = new PgTokenStream(p.PNLNAME);

			Stack<ScrollMarker> scrollMarkers = new Stack<ScrollMarker>();
			scrollMarkers.push(new ScrollMarker(0, null, AFlag.PAGE));

			while((tok = pfs.next()) != null) {

				if(tok.flags.contains(AFlag.PAGE)) {
					//System.out.println(tok.SUBPNLNAME);
					ScrollMarker sm = new ScrollMarker();
					sm.src = AFlag.PAGE;
					sm.primaryRecName = scrollMarkers.peek().primaryRecName;
					sm.scrollLevel = scrollMarkers.peek().scrollLevel;
					scrollMarkers.push(sm);
					continue;
				}

				if(tok.flags.contains(AFlag.END_OF_PAGE)) {
					while(scrollMarkers.peek().src == AFlag.SCROLL_START) {
						scrollMarkers.pop(); // pop interim scroll levels.
					}
					scrollMarkers.pop();		// pop the matching page.
					continue;
				}

				if(tok.flags.contains(AFlag.SCROLL_START)) {

					// This scroll may appear right after an unended scroll; if so, pop the previous one.
					ScrollMarker topSm = scrollMarkers.peek();
					if(topSm.src == AFlag.SCROLL_START &&
						!tok.primaryRecName.equals(topSm.primaryRecName)) {
						scrollMarkers.pop();
					}

					ScrollMarker sm = new ScrollMarker();
					sm.src = AFlag.SCROLL_START;
					sm.primaryRecName = tok.primaryRecName;
					sm.scrollLevel = scrollMarkers.peek().scrollLevel + tok.OCCURSLEVEL;
					scrollMarkers.push(sm);
					continue;
				}

				// Remember: don't "continue" here, since SCROLL_LVL_DECREMENT can be attached to regular fields.
				if(tok.flags.contains(AFlag.SCROLL_LVL_DECREMENT)) {
					scrollMarkers.pop();
				}

				if(tok.doesBelongInComponentStructure()) {
					ComponentBuffer.addPageField(tok, scrollMarkers.peek().scrollLevel,
						scrollMarkers.peek().primaryRecName);
				}
			}

			if(scrollMarkers.size() != 0) {
				System.out.println("[ERROR] Scroll marker stack size exceeds 0 at the end of the page token stream.");
				System.exit(1);
			}
		}
	}

	public boolean validateComponentStructure(boolean verboseFlag) throws Exception {

		int indent = 0;
		IStreamableBuffer buf;

		File structureFile = new File("test/" + this.PNLGRPNAME + ".structure");
		BufferedReader reader = new BufferedReader(new FileReader(structureFile));
		String line;
		String lineParts[];

		ComponentBuffer.resetCursors();
		while((buf = ComponentBuffer.next()) != null) {

			line = reader.readLine().trim();
			lineParts = line.split(";");

			if(buf instanceof ScrollBuffer) {

				ScrollBuffer sbuf = (ScrollBuffer) buf;
				indent = sbuf.scrollLevel * 3;

				if(lineParts.length != 3 || !lineParts[0].equals("SCROLL") ||
					Integer.parseInt(lineParts[1]) != sbuf.scrollLevel ||
						(!lineParts[2].replaceAll("-", "_").equals(sbuf.primaryRecName)
							&& Integer.parseInt(lineParts[1]) > 0)) {
					System.out.println("[ERROR] Incorrect/absent scroll token encountered during component structure verification.");
					System.exit(1);
				}

				if(verboseFlag) {
					for(int i=0; i<indent; i++){System.out.print(" ");}
					System.out.println("Scroll - Level " + sbuf.scrollLevel +
						"\tPrimary Record: " + sbuf.primaryRecName);
					for(int i=0; i<indent; i++){System.out.print(" ");}
					System.out.println("=======================================================");
				}

			} else if(buf instanceof RecordBuffer) {
				RecordBuffer rbuf = (RecordBuffer) buf;

				if(lineParts.length != 2 || !lineParts[0].equals("RECORD") ||
					!lineParts[1].replaceAll("-", "_").equals(rbuf.recName)) {
					System.out.println("[ERROR] Incorrect/absent record token encountered during component structure verification.");
					System.out.println(line);
					System.out.println(rbuf.recName);
					System.exit(1);
				}

				if(verboseFlag) {
					for(int i=0; i<indent; i++){System.out.print(" ");}
					System.out.println(" + " + rbuf.recName);
				}

			} else {
				RecordFieldBuffer fbuf = (RecordFieldBuffer) buf;

				if(lineParts.length != 2 || !lineParts[0].equals("FIELD") ||
					!lineParts[1].replaceAll("-", "_").equals(fbuf.fldName)) {
					System.out.println("[ERROR] Incorrect/absent field token encountered during component structure verification.");
					System.exit(1);
				}

				if(verboseFlag) {
					for(int i=0; i<indent; i++){System.out.print(" ");}
					System.out.println("   - " + fbuf.fldName);
				}
			}
		}

		if(!reader.readLine().trim().equals("END-COMPONENT-STRUCTURE")) {
			System.out.println("[ERROR] Expected END-COMPONENT-STRUCTURE in .structure file.");
			System.exit(1);
		}

		return true;
	}

	public void loadAllRecordPCProgsAndReferencedDefns() throws Exception {

		IStreamableBuffer buf;

		ComponentBuffer.resetCursors();
		while((buf = ComponentBuffer.next()) != null) {

			if(buf instanceof RecordFieldBuffer) {
				RecordFieldBuffer fbuf = (RecordFieldBuffer) buf;

				ArrayList<PeopleCodeProg> fieldProgs = fbuf.recDefn
					.recordProgsByFieldTable.get(fbuf.fldName);
				if(fieldProgs != null) {

					System.out.println("Loading Component-Level Program on Record: " + fbuf.recDefn.RECNAME +
						", Field: " + fbuf.fldName);

					for(PeopleCodeProg prog : fieldProgs) {
						BuildAssistant.loadInitialMetadataForProg(prog.getDescriptor());
						BuildAssistant.loadReferencedProgsAndDefnsForProg(prog.getDescriptor(), 0, "RecPCMode");
					}
				}
			}
		}
	}

	public void loadAllComponentPCProgsAndReferencedDefns() throws Exception {

		// Load the PostBuild event for the component first.
		for(ComponentPeopleCodeProg prog : this.orderedComponentProgs) {
			if(prog.RECNAME == null && prog.FLDNAME == null && prog.event.equals("PostBuild")) {
				BuildAssistant.loadInitialMetadataForProg(prog.getDescriptor());
				BuildAssistant.loadReferencedProgsAndDefnsForProg(prog.getDescriptor(), 0, "CompPCMode");
			}
		}
	}
}
