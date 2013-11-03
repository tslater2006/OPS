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

public abstract class ClassicPeopleCodeProg extends PeopleCodeProg {

	public abstract Clob getProgTextClob() throws Exception;
	public abstract String getDescriptor();
	protected abstract void progSpecific_loadInitialMetadata() throws Exception;

	protected void typeSpecific_handleReferencedToken(Token t, PeopleCodeTokenStream stream) throws Exception {

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

            this.referencedProgs.add(prog);

            // Load the program's initial metadata if it hasn't already been cached.
            BuildAssistant.loadInitialMetadataForProg(prog.getDescriptor());
		}

       if(t.flags.contains(TFlag.GLOBAL) || t.flags.contains(TFlag.COMPONENT) ||
       		t.flags.contains(TFlag.LOCAL)) {

            /**
             * If the first token of the variable's type is a package
             * name seen in this program, and the look ahead token is a COLON,
             * we need to parse out the app class reference and load its OnExecute program.
             */
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
       		}
		}

        // Load the record defn if it hasn't already been cached; only want record.field references here.
        if(t.flags.contains(TFlag.REFERENCE) && t.refObj.isRecordFieldRef) {

			/*if(t.refObj.RECNAME.equals("CLASS_TBL_VW1")) {
				System.out.println(this.getDescriptor());
				System.out.println("FOUND IT");
				System.exit(1);
			}*/

        	BuildAssistant.getRecordDefn(t.refObj.RECNAME);
        }
	}
}
