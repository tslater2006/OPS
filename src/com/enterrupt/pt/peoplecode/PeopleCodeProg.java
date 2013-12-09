package com.enterrupt.pt.peoplecode;

import java.sql.*;
import java.util.*;
import java.io.InputStream;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.parser.*;
import com.enterrupt.pt.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;

public abstract class PeopleCodeProg {

    public byte[] progBytes;
	public Token[] progTokens;

	protected String[] bindVals;
	public String event;
	public String parsedText;

	public ArrayList<PeopleCodeProg> referencedProgs;
	public HashMap<String, RecordPeopleCodeProg> recordProgFnCalls;
	public HashMap<String, Boolean> importedAppPackages;
	public TreeMap<Integer, Reference> progRefsTable;
	public HashMap<RecordPeopleCodeProg, Boolean> confirmedRecordProgCalls;

	private static Logger log = LogManager.getLogger(PeopleCodeProg.class.getName());

	private boolean hasInitialized = false;
	private boolean haveLoadedDefnsAndPrograms = false;

    protected PeopleCodeProg() {}

	protected abstract void initBindVals();

	public abstract String getDescriptor();

	protected abstract void subclassTokenHandler(Token t, PeopleCodeTokenStream stream,
		int recursionLevel, LFlag lflag, Stack<PeopleCodeProg> traceStack);

	public Reference getProgReference(int refNbr) {
		return this.progRefsTable.get(refNbr);
	}

	public void init() {

		if(this.hasInitialized) {
			log.debug("Already initialized: {}", this.getDescriptor());
			return; }

		log.debug("Initializing {}...", this.getDescriptor());
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
		 * Parse the entire program into tokens.
		 */
		ArrayList<Token> tokenList = new ArrayList<Token>();
		PeopleCodeByteStream byteStream = new PeopleCodeByteStream(this);
		Parser p = new Parser(byteStream);
		Token t;

		do {
			t = p.parseNextToken();

			// Discard comments.
			if(t.flags.contains(TFlag.COMMENT)) {
				continue;
			}

			if(!t.flags.contains(TFlag.DISCARD)) {
				tokenList.add(t);
			}
		} while(!t.flags.contains(TFlag.END_OF_PROGRAM));

		this.parsedText = byteStream.getParsedText();
		this.progTokens = tokenList.toArray(new Token[0]);
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

//		this.recurseLoadDefnsAndPrograms(0, flag, new Stack<PeopleCodeProg>());

		ProgLoadSupervisor supervisor = new ProgLoadSupervisor(this);
		supervisor.start();
	}

	public boolean haveDefnsAndProgsBeenLoaded() {

		if(this.haveLoadedDefnsAndPrograms) {
			log.debug("Already loaded ref defns/progs {}", this.getDescriptor());
			return true;
		} else {
			log.debug("Loading ref defns/progs {}...", this.getDescriptor());

			this.referencedProgs = new ArrayList<PeopleCodeProg>();
			this.recordProgFnCalls = new HashMap<String, RecordPeopleCodeProg>();
        	this.importedAppPackages = new HashMap<String, Boolean>();
			this.confirmedRecordProgCalls = new HashMap<RecordPeopleCodeProg, Boolean>();

			this.haveLoadedDefnsAndPrograms = true;
			return false;
		}
	}

	protected void recurseLoadDefnsAndPrograms(int recursionLevel, LFlag lflag, Stack<PeopleCodeProg> traceStack) {
/*
        while(!(t = stream.readNextToken()).flags.contains(TFlag.END_OF_PROGRAM)) {
*/
/*			this.subclassTokenHandler(t, stream, recursionLevel, lflag, traceStack);
        }
*/
        /**
         * All programs referenced by this program must have their referenced
         * definitions and programs loaded now.
         */
  /*      for(PeopleCodeProg prog : referencedProgs) {
			PeopleCodeProg p = DefnCache.getProgram(prog);
			p.init();
*/
			/**
			 * In Record PC mode, referenced defns and progs should be recursively loaded indefinitely.
			 * In Component PC mode, all App Package PC programs must be permitted to load their references
			 * recursively; for all other program types, their references should be loaded only if they are
			 * directly referenced in the root Component PC program being loaded (which exists at recursion
			 * level 0).
			 */
/*			if((lflag == LFlag.RECORD && recursionLevel < 3)
				|| (lflag == LFlag.COMPONENT
					&& (p instanceof AppClassPeopleCodeProg || recursionLevel == 0))) {
*/
				/**
				 * If this program is never actually called,
				 * there is no reason to load its references at this time.
				 */
/*				if(p instanceof RecordPeopleCodeProg && confirmedRecordProgCalls.get(p) == null) {
					continue;
				}

	   	        p.recurseLoadDefnsAndPrograms(recursionLevel+1, lflag, traceStack);
			}
        }

		traceStack.pop();*/
	}

	/**
	* CRITICAL TODO: I've seen an instance where a LOCAL token was followed by a single
    * PURE_STRING containing just the name of the app class in the bytecode, without the packages
    * specified before it, even though in App Designer it was displayed in text with the packages
    * before it. This was causing Enterrupt to not load the app class. I can't reproduce the issue
    * at the moment because messing with the program in App Designer caused App Designer to
    * store the whole path in the bytecode. In order to resolve this in the future, I'll need
    * to keep a list of terminal app classes in a hash table, mapped to the matching preceding path.
    * In the event of duplicate class names with different paths, I've verified that the whole path
    * will appear in the variable declaration bytecode.
    */
	protected String[] getAppClassPathFromStream(Token t, PeopleCodeTokenStream stream) {

    	ArrayList<String> pathParts = new ArrayList<String>();
        pathParts.add(t.pureStrVal);        // add the package name to the path.
        stream.readNextToken();     		// skip over COLON

        // Path is variable length.
        do {
        	t = stream.readNextToken();
            pathParts.add(t.pureStrVal);
            t = stream.readNextToken();
        } while(t.flags.contains(TFlag.COLON));

		return pathParts.toArray(new String[0]);
	}
}

