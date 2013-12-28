package com.enterrupt.types;

import java.util.EnumSet;
import com.enterrupt.pt.peoplecode.*;

public class StdPointer extends Pointer {

	public StdPointer(MFlag f) {
		super(f);
	}

	public StdPointer() {}

	public StdPointer(PTDataType trgt) {
		this.target = trgt;
	}

	public StdPointer(PTDataType trgt, EnumSet<MFlag> fSet) {
		super(fSet);
		this.target = trgt;
	}

	public void assign(PTDataType newTarget) {
		/**
		 * Assignment to read-only pointers is
		 * not allowed.
		 */
		if(this.flags.contains(MFlag.READ_ONLY)) {
			throw new EntDataTypeException("Encountered attempt to assign " +
				"a value to a read-only memory location.");
		}

		if(this.typeCheck(newTarget)) {
			this.target = newTarget;
		} else {
			throw new EntDataTypeException("Type check failed; encountered " +
				"attempt to assign data type of class " + newTarget.getClass().
				getName() + " to pointer with flags: " + this.flags);
		}
	}

	/**
	 * This method should enforce type coherence but
	 * disregard the READ_ONLY flag if present.
	 */
	public void systemAssign(PTDataType newTarget) {
		if(this.typeCheck(newTarget)) {
			this.target = newTarget;
		} else {
			throw new EntDataTypeException("Type check failed; encountered " +
				"attempt to (system) assign data type of class " + newTarget.getClass().
				getName() + " to pointer with flags: " + this.flags);
		}
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("StdP:");
		if(target == null) {
			builder.append("@NULL");
		} else {
			builder.append(this.target.toString());
		}
		builder.append(this.flags.toString());
		return builder.toString();
	}

	public boolean typeCheck(PTDataType o) {
		return (o instanceof PTString && this.flags.contains(MFlag.STRING))
			|| (o instanceof PTFreeRecord && this.flags.contains(MFlag.RECORD));
	}
}
