package com.enterrupt.pt.peoplecode;

import java.sql.*;
import java.util.*;
import java.io.InputStream;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.assembler.*;
import com.enterrupt.pt.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;

public abstract class PeopleCodeProg {

	protected String[] bindVals;
	public String event;
	public String programText;
    public byte[] progBytes;

	public ArrayList<PeopleCodeProg> referencedProgs;
	public HashMap<String, RecordPeopleCodeProg> recordProgFnCalls;
	public HashMap<String, Boolean> importedRootAppPackages;
	public TreeMap<Integer, Reference> progRefsTable;
	public HashMap<RecordPeopleCodeProg, Boolean> confirmedRecordProgCalls;
	public HashMap<String, List<AppPackagePath>> importedAppClasses;
	public List<AppPackagePath> importedAppPackagePaths;

	private static Logger log = LogManager.getLogger(PeopleCodeProg.class.getName());

	private boolean hasInitialized = false;
	private boolean haveLoadedDefnsAndPrograms = false;

    protected PeopleCodeProg() {}

	protected abstract void initBindVals();

	public abstract String getDescriptor();

	public void init() {

		if(this.hasInitialized) {
			return;
		}

		this.hasInitialized = true;

    	PreparedStatement pstmt = null;
        ResultSet rs = null;

		try {

	        // Get program text.
    	    pstmt = StmtLibrary.getPSPCMPROG_GetPROGTXT(this.bindVals[0], this.bindVals[1],
        	                                            this.bindVals[2], this.bindVals[3],
            	                                        this.bindVals[4], this.bindVals[5],
                	                                    this.bindVals[6], this.bindVals[7],
                    	                                this.bindVals[8], this.bindVals[9],
                        	                            this.bindVals[10], this.bindVals[11],
                            	                        this.bindVals[12], this.bindVals[13]);
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
    	        throw new EntVMachRuntimeException("Number of bytes in " + this.getDescriptor() + " ("
        	        + this.progBytes.length + ") not equal to PROGLEN (" + PROGLEN + ").");
        	}

			this.progRefsTable = new TreeMap<Integer, Reference>();

	    	// Get program references.
  		    pstmt = StmtLibrary.getPSPCMPROG_GetRefs(this.bindVals[0], this.bindVals[1],
                                                     this.bindVals[2], this.bindVals[3],
                                                     this.bindVals[4], this.bindVals[5],
                                                     this.bindVals[6], this.bindVals[7],
                                                     this.bindVals[8], this.bindVals[9],
            	                                     this.bindVals[10], this.bindVals[11],
                                                     this.bindVals[12], this.bindVals[13]);
        	rs = pstmt.executeQuery();
	        while(rs.next()) {
    	        this.progRefsTable.put(rs.getInt("NAMENUM"),
        	        new Reference(rs.getString("RECNAME").trim(), rs.getString("REFNAME").trim()));
        	}

        } catch(java.sql.SQLException sqle) {
            log.fatal(sqle.getMessage(), sqle);
            System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
        } finally {
            try {
                if(rs != null) { rs.close(); }
                if(pstmt != null) { pstmt.close(); }
            } catch(java.sql.SQLException sqle) {}
        }

		/**
		 * Assemble the text of the program from its constituent bytecode.
		 */
		PeopleCodeByteStream byteStream = new PeopleCodeByteStream(this);
		Assembler a = new Assembler(byteStream);
		a.assemble();
		this.programText = byteStream.getAssembledText();
	}

    public void appendProgBytes(Blob blob) {

		int b;
		InputStream stream = null;
		ArrayList<Integer> bytes = new ArrayList<Integer>();

		try {
			stream = blob.getBinaryStream();

			while((b = stream.read()) != -1) {
				bytes.add(b);
			}
		} catch(java.sql.SQLException sqle) {
			log.fatal(sqle.getMessage(), sqle);
			System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
		} catch(java.io.IOException ioe) {
			log.fatal(ioe.getMessage(), ioe);
			System.exit(ExitCode.FAILED_READ_FROM_BLOB_STREAM.getCode());
		} finally {
			try {
				if(stream != null) { stream.close(); }
			} catch(java.io.IOException ioe) {}
		}

		int startIdx;
		byte[] allBytes;

		if(this.progBytes == null) {
			allBytes = new byte[bytes.size()];
			startIdx = 0;
		} else {
			allBytes = new byte[this.progBytes.length + bytes.size()];
			startIdx = this.progBytes.length;

			// Copy previous bytes to new array.
			for(int i = 0; i < startIdx; i++) {
				allBytes[i] = this.progBytes[i];
			}
		}

		Iterator<Integer> iter = bytes.iterator();
		for(int i = startIdx; i < allBytes.length; i++) {
			allBytes[i] = iter.next().byteValue();
		}

		this.progBytes = allBytes;
    }

	public void loadDefnsAndPrograms() {
		ProgLoadSupervisor supervisor = new ProgLoadSupervisor(this);
		supervisor.start();
	}

	public boolean haveDefnsAndProgsBeenLoaded() {

		if(this.haveLoadedDefnsAndPrograms) {
			return true;
		} else {
			this.referencedProgs = new ArrayList<PeopleCodeProg>();
			this.recordProgFnCalls = new HashMap<String, RecordPeopleCodeProg>();
        	this.importedRootAppPackages = new HashMap<String, Boolean>();
			this.confirmedRecordProgCalls = new HashMap<RecordPeopleCodeProg, Boolean>();
			this.importedAppClasses = new HashMap<String, List<AppPackagePath>>();
			this.importedAppPackagePaths = new ArrayList<AppPackagePath>();

			this.haveLoadedDefnsAndPrograms = true;
			return false;
		}
	}

	public Reference getReference(int refNbr) {
		return this.progRefsTable.get(refNbr);
	}

	public String toString() {
		return this.getDescriptor();
	}
}

