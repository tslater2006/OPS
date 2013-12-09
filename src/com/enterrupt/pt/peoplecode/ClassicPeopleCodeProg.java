package com.enterrupt.pt.peoplecode;

import java.util.*;
import com.enterrupt.parser.*;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.*;
import org.apache.logging.log4j.*;
import com.enterrupt.buffers.*;

public abstract class ClassicPeopleCodeProg extends PeopleCodeProg {

	private Logger log = LogManager.getLogger(ClassicPeopleCodeProg.class.getName());

	public abstract String getDescriptor();

	protected void subclassTokenHandler(Token t, PeopleCodeTokenStream stream, int recursionLevel, LFlag lflag,
		Stack<PeopleCodeProg> traceStack) {

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

				log.debug("Loading defns/progs for {} with parent {}",
					prog.getDescriptor(), this.getDescriptor());
				prog.recurseLoadDefnsAndPrograms(recursionLevel+1, lflag, traceStack);
       		} else {
				//TODO: throw new EntVMachRuntimeException("Unknown token syntax following GLOBAL/COMPONENT/LOCAL.");
			}
		}

		if(t.flags.contains(TFlag.CREATE)) {

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

				//log.debug(java.util.Arrays.toString(path));
				log.debug("(create) Loading defns/progs for {} with parent {}",
					prog.getDescriptor(), this.getDescriptor());
				prog.recurseLoadDefnsAndPrograms(recursionLevel+1, lflag, traceStack);
       		} else {
				//TODO: throw new EntVMachRuntimeException("Unknown token syntax following CREATE.");
			}
		}

		/**
		 * References to records should always be loaded when the root program is a Record PC prog.
		 * If the root program is a Component PC prog, references to records should only be loaded
		 * if this object represents either the root Component PC program itself or one of the
		 * programs referenced by it (recursion levels 0 and 1, respectively). Remember that
		 */
		if(((lflag == LFlag.RECORD && recursionLevel < 4) || (lflag == LFlag.COMPONENT && recursionLevel < 2)) &&
			t.flags.contains(TFlag.REFERENCE) && t.refObj.isRecordFieldRef) {

			log.debug("recursion level: {}", recursionLevel);
			log.debug("Loading recdefn {} on parent {}", t.refObj.RECNAME,
				this.getDescriptor());

	        // Load the record defn if it hasn't already been cached; only want record.field references here.
        	DefnCache.getRecord(t.refObj.RECNAME);
        }
	}
}
