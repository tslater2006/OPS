package com.enterrupt.pt_objects;

import java.sql.Blob;
import java.sql.Clob;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.io.InputStream;
import java.lang.StringBuilder;
import java.security.MessageDigest;
import org.apache.commons.codec.binary.Base64;
import com.enterrupt.BuildAssistant;
import com.enterrupt.parser.Parser;
import com.enterrupt.parser.Token;
import com.enterrupt.parser.TFlag;

public abstract class PeopleCodeProg {

    public String event;
	public TreeMap<Integer, Reference> progRefsTable;

	/** TODO: Byte cursor should be moved to a PeopleCodeByteStream class. */
    public byte[] progBytes;
	public int byteCursorPos = 0;

	public Token[] progTokens;

	private StringBuilder progTextBuilder;
	private boolean hasInitialMetadataBeenLoaded;
	private boolean haveLoadedReferencedDefnsAndProgs;
	protected ArrayList<PeopleCodeProg> referencedProgs;
	protected HashMap<String, Boolean> importedAppPackages;

    protected PeopleCodeProg() {
		this.progBytes = new byte[0];
		this.progRefsTable = new TreeMap<Integer, Reference>();
		this.hasInitialMetadataBeenLoaded = false;
		this.haveLoadedReferencedDefnsAndProgs = false;
    }

	public void markLoaded() {
		this.hasInitialMetadataBeenLoaded = true;
	}

	public boolean isLoaded() {
		return this.hasInitialMetadataBeenLoaded;
	}

	public void markReferencesLoaded() {
		this.haveLoadedReferencedDefnsAndProgs = true;
	}

	public boolean areReferencesLoaded() {
		return this.haveLoadedReferencedDefnsAndProgs;
	}

	protected abstract void progSpecific_loadInitialMetadata() throws Exception;
	protected abstract void typeSpecific_handleReferencedToken(Token t, PeopleCodeTokenStream stream) throws Exception;
	public abstract Clob getProgTextClob() throws Exception;
	public abstract String getDescriptor();

	public void loadInitialMetadata() throws Exception {

		this.progSpecific_loadInitialMetadata();

		/**
		 * Parse the entire program into tokens. We'll find out what programs are
		 * referenced by this one in the process. The final list of tokens will used
		 * by the interpreter at runtime.
		 */
		ArrayList<Token> tokenList = new ArrayList<Token>();
		Parser p = new Parser(this);
		Token t;

		do {

			t = p.parseNextToken();

			// Discard comments.
			if(t.flags.contains(TFlag.COMMENT)) {
				continue;
			}

	       /**
	        * I've been having an issue with the parser emitting an IDENTIFIER token immediately
    	    * after a NUMBER token in LS_SS_PERS_SRCH.EMPLID.SearchInit, despite the fact that the identifier
      		* name parsed out is empty. If the identifier is empty here, I'm going to parse the next token.
      	  	* If it's another empty IDENTIFIER, I'll exit. Otherwise I'll let the interpreter continue.
        	* TODO: Once I have more PeopleCode running through here, try to collect information about how
       		* often this occurs, after which tokens, etc. I may be able to modify the NumberParser / other parser
        	* objects accordingly.
        	*/
		/*	if(t.flags.equals(EnumSet.of(TFlag.DISCARD, TFlag.IDENTIFIER))) {
				t = p.parseNextToken();
				if(t.flags.equals(EnumSet.of(TFlag.DISCARD, TFlag.IDENTIFIER))) {
					System.out.println("[ERROR] Discarded two empty IDENTIFIER tokens in a row.");
					System.exit(1);
				}
			}*/

			if(!t.flags.contains(TFlag.DISCARD)) {
				tokenList.add(t);
		 	/*	System.out.print(t.flags);

				if(t.flags.contains(TFlag.REFERENCE)) {
					System.out.print("\t\t\t" + t.refObj.getValue());
				}
				if(t.flags.contains(TFlag.PURE_STRING)) {
					System.out.print("\t\t\t" + t.pureStrVal);
				}
				if(t.flags.contains(TFlag.EMBEDDED_STRING)) {
					System.out.print("\t\t\t" + t.embeddedStrVal);
				}
				if(t.flags.contains(TFlag.NUMBER)) {
					System.out.print("\t\t\t" + t.numericVal);
				}

				System.out.println();*/
			}
		} while(!t.flags.contains(TFlag.END_OF_PROGRAM));

		this.progTokens = tokenList.toArray(new Token[0]);
		//System.out.println(this.getProgText());

		/**
		 * TODO: Re-enable this at some point.
		 */
		//this.verifyEntireProgramText();
	}

	public void setByteCursorPos(int pos) {
		this.byteCursorPos = pos;
	}

	public byte getCurrentByte() {
		return this.progBytes[this.byteCursorPos];
	}

	public byte readNextByte() {
		//System.out.printf("[READ] %d: 0x%02X\n", this.byteCursorPos, this.progBytes[this.byteCursorPos]);
		return this.progBytes[this.byteCursorPos++];
	}

	public byte readAhead() {
		if(this.byteCursorPos >= this.progBytes.length - 1) {
			return -1;
		}
		return this.progBytes[this.byteCursorPos];
	}

	public void appendProgText(char c) {
		if(this.progTextBuilder == null) {
			this.progTextBuilder = new StringBuilder();
		}
		progTextBuilder.append(c);
	}

	public void appendProgText(String s) {
		if(this.progTextBuilder == null) {
			this.progTextBuilder = new StringBuilder();
		}
		progTextBuilder.append(s);
	}

	public String getProgText() {
		return this.progTextBuilder.toString();
	}

	public void resetProgText() {
		this.progTextBuilder = new StringBuilder();
	}

	public Reference getProgReference(int refNbr) {
		return this.progRefsTable.get(refNbr);
	}

    public void appendProgBytes(Blob blob) throws Exception {

		int b;
		InputStream stream = blob.getBinaryStream();
		ArrayList<Integer> bytes = new ArrayList<Integer>();

		while((b = stream.read()) != -1) {
			bytes.add(b);
		}

		int startIdx;
		byte[] allBytes;

		if(this.progBytes.length == 0) {
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

	/**
     * REMEMBER: SQL emitted here should not show up in the emittedStmts
     * list, otherwise trace verification will fail.
	 * Note: PSPCMTXT contains only 1/10 of the records in PSPCMPROG. Do not fail
     * here if no match is found in PSPCMTXT.
     */
    public void verifyEntireProgramText() throws Exception {

		Clob progTextClob = this.getProgTextClob();

		if(progTextClob != null) {

			/**
			 * TODO: We risk losing text characters here, need to use
		     * a character stream or other method in the long term.
			 */
			int progTextLen = (int) progTextClob.length();
			String officialProgText = progTextClob.getSubString(1, progTextLen); // first char is at pos 1
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");

        	crypt.reset();
        	crypt.update(this.getProgText().trim().getBytes());
        	byte[] entDigest = crypt.digest();
    	    String entBase64 = Base64.encodeBase64String(entDigest);

			crypt.reset();
			crypt.update(officialProgText.trim().getBytes());
			byte[] psDigest = crypt.digest();
			String psBase64 = Base64.encodeBase64String(psDigest);

			if(!entBase64.equals(psBase64)) {
				System.out.println("[ERROR] PeopleCode program digests do not match.");
				System.exit(1);
			} else {
				//System.out.println("[OK] PeopleCode program digests match.");
			}
		} else {
			//System.out.println("[NOTICE]: PCPCMTXT does not contain PC for requested program.");
		}
    }

	public void loadReferencedDefnsAndPrograms() throws Exception {

 		Token t;
		this.referencedProgs = new ArrayList<PeopleCodeProg>();
        this.importedAppPackages = new HashMap<String, Boolean>();
        PeopleCodeTokenStream stream = new PeopleCodeTokenStream(this);

        while(!(t = stream.readNextToken()).flags.contains(TFlag.END_OF_PROGRAM)) {

            //System.out.println(t.flags);

			/**
			 * TODO: Determine whether this belongs here or in ClassicPeopleCodeProg.
			 * For now I'm assuming that imported packages are checked in both classic
			 * and app package programs.
			 */
            if(t.flags.contains(TFlag.IMPORT)) {

                ArrayList<String> pathParts = new ArrayList<String>();

                // Path is variable length.
                do {
                    t = stream.readNextToken();
                    pathParts.add(t.pureStrVal);
                    t = stream.readNextToken();
                } while(t.flags.contains(TFlag.COLON));

                // Load the app package (get list of all programs within) if not already cached.
                BuildAssistant.getAppPackageDefn(pathParts.get(0));

                importedAppPackages.put(pathParts.get(0), true);
            }

			this.typeSpecific_handleReferencedToken(t, stream);
        }

        /**
         * All programs referenced by this program must have their referenced
         * definitions and programs loaded now.
         */
        for(PeopleCodeProg prog : referencedProgs) {
            BuildAssistant.loadInitialMetadataForProg(prog.getDescriptor());
   	        BuildAssistant.loadReferencedProgsAndDefnsForProg(prog.getDescriptor());
        }
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
        stream.readNextToken();     // skip over COLON

        // Path is variable length.
        do {
        	t = stream.readNextToken();
            pathParts.add(t.pureStrVal);
            t = stream.readNextToken();
        } while(t.flags.contains(TFlag.COLON));

		return pathParts.toArray(new String[0]);
	}
}

