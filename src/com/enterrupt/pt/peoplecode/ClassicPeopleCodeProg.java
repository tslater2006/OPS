package com.enterrupt.pt.peoplecode;

import com.enterrupt.parser.*;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.*;

public abstract class ClassicPeopleCodeProg extends PeopleCodeProg {

	public abstract String getDescriptor();

	protected void subclassTokenHandler(Token t, PeopleCodeTokenStream stream, int recursionLevel, LFlag lflag) {

    	if(t.flags.contains(TFlag.DECLARE)) {

        	t = stream.readNextToken();
            if(!t.flags.contains(TFlag.FUNCTION)) {
            	throw new EntVMachRuntimeException("Expected FUNCTION.");
            }

            // Name of the function.
            t = stream.readNextToken();

            t = stream.readNextToken();
            if(!t.flags.contains(TFlag.PEOPLECODE)) {
            	throw new EntVMachRuntimeException("Expected PEOPLECODE.");
            }

            t = stream.readNextToken();
            Reference refObj = t.refObj;

            t = stream.readNextToken();
            String event = t.pureStrVal;

            // Load the record definition if it hasn't already been cached.
            DefnCache.getRecord(refObj.RECNAME);

            PeopleCodeProg prog = new RecordPeopleCodeProg(refObj.RECNAME, refObj.REFNAME, event);
            prog = DefnCache.getProgram(prog);

            this.referencedProgs.add(prog);

            // Load the program's initial metadata if it hasn't already been cached.
			prog.init();
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

            	PeopleCodeProg prog = new AppClassPeopleCodeProg(path);
				prog = DefnCache.getProgram(prog);

 	            referencedProgs.add(prog);

    	        // Load the program's initial metadata.
				prog.init();
       		}
		}

		/**
		 * References to records should always be loaded when the root program is a Record PC prog.
		 * If the root program is a Component PC prog, references to records should only be loaded
		 * if this object represents either the root Component PC program itself or one of the
		 * programs referenced by it (recursion levels 0 and 1, respectively).
		 */
		if((lflag == LFlag.RECORD || (lflag == LFlag.COMPONENT && recursionLevel < 2)) &&
			t.flags.contains(TFlag.REFERENCE) && t.refObj.isRecordFieldRef) {

	        // Load the record defn if it hasn't already been cached; only want record.field references here.
        	DefnCache.getRecord(t.refObj.RECNAME);
        }
	}
}
