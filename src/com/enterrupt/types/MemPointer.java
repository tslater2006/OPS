package com.enterrupt.types;

import java.util.EnumSet;

public class MemPointer {

	private EnumSet<MFlag> flags;
	private PTDataType target = null;

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

	public boolean isInitialized() {
		return target == null;
	}

	public void assign(PTDataType operand) {
		/**
		 * TODO: If target is non-null, check for type coherence;
		 * if target is null, check flags before assigning. Throw EntDataTypeException
		 * if mismatch. Also prevent if this is marked as read-only.
		 */
		this.target = operand;
	}

	public void systemAssign(PTDataType operand) {
		/**
	     * Check for type coherence, but disregard READ_ONLY flag.
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
