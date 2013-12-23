package com.enterrupt.trace;

public class PCStart implements IEmission {

	public String nest;
	public String progDescriptor;

	public PCStart(String nest, String progDescriptor) {
		this.nest = nest;
		this.progDescriptor = progDescriptor;
	}

	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof PCStart))
			return false;

		PCStart other = (PCStart)obj;
		if(this.nest.equals(other.nest) &&
			this.progDescriptor.equals(other.progDescriptor)) {
			return true;
		}
		return false;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(">>> start     Nest=").append(this.nest);
		builder.append("  ").append(this.progDescriptor);
		return builder.toString();
	}
}
