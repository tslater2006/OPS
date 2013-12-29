package com.enterrupt.memory;

import java.util.EnumSet;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.types.*;

public class AppClassObjPointer extends Pointer {

	public AppClassPeopleCodeProg progDefn;

	/**
	 * Pointers to app class objects enforce type coherence by only
	 * accepting PTAppClassObjects with a matching prog defn.
	 */
	public AppClassObjPointer(AppClassPeopleCodeProg p) {
		super(EnumSet.of(MFlag.APP_CLASS_OBJ));
		this.progDefn = p;
	}

    public void assign(PTDataType newTarget) {
        if(newTarget instanceof PTAppClassObject) {
            this.target = newTarget;
        } else {
            throw new EntDataTypeException("Illegal attempt to assign target " +
                "of type " + newTarget.getClass().getName() + " to a PTAppClassObject " +
                "pointer.");
        }
    }

	public String toString() {
		StringBuilder builder = new StringBuilder("MP:");
		if(target == null) {
			builder.append("@NULL");
		} else {
			builder.append(this.target.toString());
		}
		builder.append(this.flags.toString());
		return builder.toString();
	}
}
