package com.enterrupt.pt_objects;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.lang.StringBuilder;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.parser.Token;
import com.enterrupt.parser.TFlag;

public class AppPackagePeopleCodeProg extends PeopleCodeProg {

	public String[] bindValues;
	public String[] pathParts;
	public AppPackage rootPackage;

	public AppPackagePeopleCodeProg(String[] path) throws Exception {
		super();
		this.pathParts = path;
		this.event = "OnExecute";

		/**
	     * Due to the variable length nature of app class paths,
		 * we need to determine which bind values we'll be querying the database
		 * with based on the length provided and knowledge of OBJECTID constants.
		 */
		int pathPartIdx = 0;
		int nextObjectId = 105; // 105 through 107 == App Class
		this.bindValues = new String[14];
		for(int i = 0; i < this.bindValues.length; i+=2) {
			if(i == 0) {
				this.bindValues[0] = "104"; // 104 == App Package
				this.bindValues[1] = path[pathPartIdx++];
			} else if(pathPartIdx == (path.length - 1)) {
				this.bindValues[i] = "107"; // 107 == final App Class in hierarchy
				this.bindValues[i+1] = path[pathPartIdx++];
			} else if(pathPartIdx < path.length) {
				this.bindValues[i] = "" + nextObjectId++;
				this.bindValues[i+1] = path[pathPartIdx++];
			} else {

				// The event OBJECTID/OBJECTVAL pair must follow the final app class.
				if(this.bindValues[i-2].equals("107")) {
					this.bindValues[i] = "12";		// 12 == Event
					this.bindValues[i+1] = this.event;
				} else {
					this.bindValues[i] = "0";
					this.bindValues[i+1] = PSDefn.NULL;
				}
			}
		}

		this.rootPackage = BuildAssistant.getAppPackageDefn(this.bindValues[1]);

		//System.out.println(java.util.Arrays.toString(this.bindValues));
		//System.exit(1);
	}

	public String getDescriptor() {
		StringBuilder sb = new StringBuilder();
		sb.append("AppPackagePC:");
		for(int i = 0; i < this.pathParts.length; i++) {
			sb.append(this.pathParts[i]).append(".");
		}
		sb.append(this.event);
		return sb.toString();
	}

	protected void progSpecific_loadInitialMetadata() throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rs;

        // Get program text.
        pstmt = StmtLibrary.getPSPCMPROG_GetPROGTXT(this.bindValues[0], this.bindValues[1],
													this.bindValues[2], this.bindValues[3],
													this.bindValues[4], this.bindValues[5],
													this.bindValues[6], this.bindValues[7],
													this.bindValues[8], this.bindValues[9],
													this.bindValues[10], this.bindValues[11],
													this.bindValues[12], this.bindValues[13]);
        rs = pstmt.executeQuery();

        /**
         * Append the program bytecode; there could be multiple records
         * for this program if the length exceeds 28,000 bytes. Note that
         * the above query must be ordered by PROSEQ, otherwise these records
         * will need to be pre-sorted before appending the BLOBs together.
         */
        int PROGLEN = -1;
        while(rs.next()) {
            PROGLEN = rs.getInt("PROGLEN");     // PROGLEN is the same for all records returned here.
            this.appendProgBytes(rs.getBlob("PROGTXT"));
        }
        rs.close();
        pstmt.close();

        if(this.progBytes.length != PROGLEN) {
            System.out.println("[ERROR] Number of bytes in " + this.getDescriptor() + " ("
				+ this.progBytes.length + ") not equal to PROGLEN (" + PROGLEN + ").");
            System.exit(1);
        }

    	// Get program references.
        pstmt = StmtLibrary.getPSPCMPROG_GetRefs(this.bindValues[0], this.bindValues[1],
													this.bindValues[2], this.bindValues[3],
													this.bindValues[4], this.bindValues[5],
													this.bindValues[6], this.bindValues[7],
													this.bindValues[8], this.bindValues[9],
													this.bindValues[10], this.bindValues[11],
													this.bindValues[12], this.bindValues[13]);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            this.progRefsTable.put(rs.getInt("NAMENUM"),
                new Reference(rs.getString("RECNAME").trim(), rs.getString("REFNAME").trim()));
        }
        rs.close();
        pstmt.close();
    }

	public Clob getProgTextClob() throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rs;

        pstmt = StmtLibrary.getPSPCMTXT(this.bindValues[0], this.bindValues[1],
													this.bindValues[2], this.bindValues[3],
													this.bindValues[4], this.bindValues[5],
													this.bindValues[6], this.bindValues[7],
													this.bindValues[8], this.bindValues[9],
													this.bindValues[10], this.bindValues[11],
													this.bindValues[12], this.bindValues[13]);
        rs = pstmt.executeQuery();
        if(rs.next()) {
            return rs.getClob("PCTXT");
		}
		return null;
	}

	protected void typeSpecific_handleReferencedToken(Token t, PeopleCodeTokenStream stream,
		int recursionLevel, String mode) throws Exception {

		/**
		 * Detect application classes referenced as instance and property values in an object
		 * definition.
		 */
		if(t.flags.contains(TFlag.INSTANCE) || t.flags.contains(TFlag.PROPERTY)) {

			t = stream.readNextToken();
			Token l = stream.peekNextToken();

			if(t.flags.contains(TFlag.PURE_STRING)
				&& l.flags.contains(TFlag.COLON)
				&& importedAppPackages.get(t.pureStrVal) != null) {

				String[] path = this.getAppClassPathFromStream(t, stream);

				PeopleCodeProg prog = new AppPackagePeopleCodeProg(path);
				prog = BuildAssistant.getProgramOrCacheIfMissing(prog);

				referencedProgs.add(prog);

				// Load the program's initial metadata.
				BuildAssistant.loadInitialMetadataForProg(prog.getDescriptor());

				/**
				 * I added this call in order to align ENT with the Component PC loading section of the
				 * the trace file; I've learned that when PT encounters a referenced object in an app
				 * class, it recursively loads that app class's references immediately. Thus I added this call.
			     * It's possible that this call may need to be locked down to Component PC
				 * loading mode if issues with Record PC loading surface later on. At this time, running
				 * this unconditionally is not interfering with the previous sections of the trace file.
				 * TODO: keep this in mind.
				 */
				BuildAssistant.loadReferencedProgsAndDefnsForProg(prog.getDescriptor(), recursionLevel+1, mode);
			}
		}
	}
}
