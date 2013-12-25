package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.scope.*;

public class PTAppClassObject implements PTDataType {

	public AppClassPeopleCodeProg progDefn;
	public AppClassObjRefEnvi persistentRefEnvi;

	public PTAppClassObject(AppClassPeopleCodeProg prog) {
		this.progDefn = prog;
		this.persistentRefEnvi = new AppClassObjRefEnvi();

		for(Map.Entry<String, Void> cursor : prog.instanceIdTable.entrySet()) {
			this.persistentRefEnvi.declareVar(cursor.getKey(), new MemPointer());
		}
	}

	public MemPointer access(String s) {
		if(!progDefn.methodEntryPoints.containsKey(s)) {
			throw new EntDataTypeException("Call to access(s) on " +
				"PTAppClassObject found no methods for s="+s);
		}

		/**
		 * TODO: Eliminate unnecessary allocation by having the underlying
		 * program definition create these pointers during initialization.
		 */
        return new MemPointer(new PTCallable(new AppClassObjExecContext(this, s)));
	}

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTAppClassObject))
            return false;

        PTAppClassObject other = (PTAppClassObject)obj;
		throw new EntDataTypeException("equals() for PTAppClassObject not yet implemented.");
    }

	public String toString() {
		return "PTAppClassObject:" + progDefn.getDescriptor();
	}
}

