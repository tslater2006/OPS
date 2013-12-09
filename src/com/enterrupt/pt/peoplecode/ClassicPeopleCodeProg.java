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
