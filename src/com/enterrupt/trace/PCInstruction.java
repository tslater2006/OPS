package com.enterrupt.trace;

public class PCInstruction implements IEmission {

	public String instruction;

	public PCInstruction(String i) {
		this.instruction = i;
	}

	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof PCInstruction))
			return false;

		PCInstruction other = (PCInstruction)obj;
		if(this.instruction.equals(other.instruction)) {
			return true;
		}
		return false;
	}

	public String toString() {
		return "-: " + this.instruction;
	}
}
