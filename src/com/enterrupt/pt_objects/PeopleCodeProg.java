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
		 		/*System.out.print(t.flags);

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
		this.verifyEntireProgramText();
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
		}

		Iterator<Integer> iter = bytes.iterator();
		for(int i = startIdx; i < allBytes.length; i++) {
			allBytes[i] = iter.next().byteValue();
		}

		this.progBytes = allBytes;
    }

	public void loadReferencedDefinitionsAndPrograms() throws Exception {

		Token t;
		PeopleCodeTokenStream stream = new PeopleCodeTokenStream(this);
		ArrayList<PeopleCodeProg> referencedProgs = new ArrayList<PeopleCodeProg>();

		while(!(t = stream.readNextToken()).flags.contains(TFlag.END_OF_PROGRAM)) {

			if(t.flags.contains(TFlag.DECLARE)) {

				t = stream.readNextToken();
				if(!t.flags.contains(TFlag.FUNCTION)) {
					System.out.println("[ERROR] Expected FUNCTION after DECLARES.");
					System.exit(1);
				}

				// Name of the function.
				t = stream.readNextToken();

				t = stream.readNextToken();
				if(!t.flags.contains(TFlag.PEOPLECODE)) {
					System.out.println("[ERROR] Expected PEOPLECODE after function name.");
					System.exit(1);
				}

				t = stream.readNextToken();
				Reference refObj = t.refObj;

				t = stream.readNextToken();
				String event = t.pureStrVal;

				// Load the record definition if it hasn't already been cached.
				BuildAssistant.getRecordDefn(refObj.RECNAME);

				PeopleCodeProg prog = new RecordPeopleCodeProg(refObj.RECNAME, refObj.REFNAME, event);
				prog = BuildAssistant.getProgramOrCacheIfMissing(prog);

				referencedProgs.add(prog);

				// Load the program's initial metadata if it hasn't already been cached.
				BuildAssistant.loadInitialMetadataForProg(prog.getDescriptor());
			}

			if(t.flags.contains(TFlag.IMPORT)) {

				ArrayList<String> pathParts = new ArrayList<String>();

				// Path to app class is variable length.
				do {
					t = stream.readNextToken();
					pathParts.add(t.pureStrVal);
					t = stream.readNextToken();
				} while(t.flags.contains(TFlag.COLON));

				PeopleCodeProg prog = new AppPackagePeopleCodeProg(pathParts.toArray(new String[0]));
				prog = BuildAssistant.getProgramOrCacheIfMissing(prog);

				referencedProgs.add(prog);

				// Load the program's initial metadata.
				BuildAssistant.loadInitialMetadataForProg(prog.getDescriptor());
			}

			// Load the record defn if it hasn't already been cached; only want record.field references here.
			if(t.flags.contains(TFlag.REFERENCE) && t.refObj.isRecordFieldRef) {
				BuildAssistant.getRecordDefn(t.refObj.RECNAME);
			}
		}

		/**
		 * All programs referenced by this program must have their referenced
		 * definitions and programs loaded now.
		 */
		for(PeopleCodeProg prog : referencedProgs) {

			/**
			 * TODO: Keep this in the back of your mind. It appears that Record PC programs
			 * have their definitions and external function references loaded, but App Package PC
			 * programs do not. If this doesn't get loaded during initialization, I'll need to understand
			 * where App Package PC dependencies get loaded when it comes to time to run the interpreter.
			 */
			if(!(prog instanceof AppPackagePeopleCodeProg)) {
				BuildAssistant.loadReferencedProgsAndDefnsForProg(prog.getDescriptor());
			}
		}
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
}

