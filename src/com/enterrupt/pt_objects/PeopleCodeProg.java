package com.enterrupt.pt_objects;

import java.sql.Blob;
import java.sql.Clob;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.EnumSet;
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

    public byte[] progBytes;
	public int byteCursorPos = 0;

	public Token[] progTokens;
	public int tokenCursorPos = 0;

	private StringBuilder progTextBuilder;
	protected boolean hasInitialMetadataBeenLoaded;

    protected PeopleCodeProg() {
		this.progRefsTable = new TreeMap<Integer, Reference>();
		this.hasInitialMetadataBeenLoaded = false;
    }

	protected abstract void progSpecific_loadInitialMetadata() throws Exception;
	public abstract Clob getProgTextClob() throws Exception;

	public void loadInitialMetadata() throws Exception {

		if(this.hasInitialMetadataBeenLoaded) {
			return;
		}

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
			if(t.flags.equals(EnumSet.of(TFlag.DISCARD, TFlag.IDENTIFIER))) {
				t = p.parseNextToken();
				if(t.flags.equals(EnumSet.of(TFlag.DISCARD, TFlag.IDENTIFIER))) {
					System.out.println("[ERROR] Discarded two empty IDENTIFIER tokens in a row.");
					System.exit(1);
				}
			}

			tokenList.add(t);
			System.out.println(t.flags);

		} while(!t.flags.contains(TFlag.END_OF_PROGRAM));

		this.progTokens = tokenList.toArray(new Token[0]);
		System.out.println(this.getProgText());
		this.verifyEntireProgramText();

		this.hasInitialMetadataBeenLoaded = true;
	}

	public void setByteCursorPos(int pos) {
		this.byteCursorPos = pos;
	}

	public Token readNextToken() {
		return this.progTokens[this.tokenCursorPos++];
	}

	public Token peekNextToken() {
		return this.progTokens[this.tokenCursorPos];
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

    public void setProgText(Blob b) throws Exception {

        /**
         * TODO: Here we risk losing progtext bytes since the length (originally a long)
         * is cast to int. Build in a check for this, use binarystream when this is the case.
         */
        int num_bytes = (int) b.length();
        this.progBytes = b.getBytes(1, num_bytes); // first byte is at idx 1
    }

	public void loadReferencedDefinitionsAndPrograms() throws Exception {

		/**
		 * We need to determine which functions, if any, are imported
		 * by this program; these references are not denoted in PSPCMNAME, so
		 * they must be collected via the parser.
		 */
/*		HashMap<Reference, PeopleCodeProg> importedFuncTable
			= Parser.scanForListOfDeclaredAndImportedFunctions(this);

		for(Map.Entry<Integer, Reference> cursor : this.progRefsTable.entrySet()) {
			Reference ref = cursor.getValue();
*/
			/**
			 * Currently assuming that no keyword indicates it's a Record.Field reference;
			 * this assumption may or may not be valid in the future. TODO: Verify this.
			 */
/*			if(ref.isRecordFieldRef) {
				BuildAssistant.getRecordDefn(ref.RECNAME);
			}
*/
			/**
			 * If this reference corresponds to an imported/declared external PeopleCode function,
			 * that program's initial metadata must be loaded now.
			 */
/*			if(importedFuncTable.get(ref) != null) {
				importedFuncTable.get(ref).loadInitialMetadata();
			}
		}
*/
		/**
		 * All programs referenced by this program must have their referenced
		 * definitions and programs loaded now.
		 */
/*		for(Map.Entry<Reference, PeopleCodeProg> cursor : importedFuncTable.entrySet()) {
			PeopleCodeProg prog = cursor.getValue();
			prog.loadReferencedDefinitionsAndPrograms();
		}*/
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

