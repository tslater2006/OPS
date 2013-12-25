package com.enterrupt.types;

import java.util.EnumSet;
import com.enterrupt.pt.peoplecode.*;

public class MemPointer {

	private EnumSet<MFlag> flags;
	private PTDataType target;
	public AppClassPeopleCodeProg appClassTypeProg;

	public MemPointer() {}

	public MemPointer(PTDataType trgt) {
		this.target = trgt;
		this.flags = EnumSet.noneOf(MFlag.class);
	}

    public MemPointer(EnumSet<MFlag> fSet) {
        this.flags = fSet;
    }

	public MemPointer(PTDataType trgt, EnumSet<MFlag> fSet) {
		this.target = trgt;
		this.flags = fSet;
	}

	/**
	 * Pointers to app class objects enforce type coherence by only
	 * accepting PTAppClassObjects with a matching prog defn.
	 */
	public MemPointer(AppClassPeopleCodeProg p) {
		this.flags = EnumSet.of(MFlag.APP_CLASS_OBJ);
		this.appClassTypeProg = p;
	}

	public boolean isInitialized() {
		return target == null;
	}

	public void assign(PTDataType operand) {
		/**
		 * TODO: If target is non-null, check for type coherence;
		 * if target is null, check flags before assigning. Throw EntDataTypeException
		 * if mismatch. Also prevent if this is marked as read-only.
		 */
		throw new EntDataTypeException("Need to enforce type coherence.");
//		this.target = operand;
	}

	public void systemAssign(PTDataType operand) {
		/**
	     * TODO: Check for type coherence, but disregard READ_ONLY flag.
		 */
		this.target = operand;
	}

	public PTDataType dereference() {
		return this.target;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if(target == null) {
			builder.append("@NULL");
		} else {
			builder.append(this.target.toString());
		}
		builder.append(this.flags.toString());
		return builder.toString();
	}
}
