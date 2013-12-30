package com.enterrupt.types;

public class PTArray extends PTObjectType {

	public int dimensions;
	public PTType baseType;

	protected PTArray(int d, PTType b) {
		super(Type.ARRAY);
		this.dimensions = d;
		this.baseType = b;
	}

	public PTType dot(String s) {
		throw new EntDataTypeException("Need to support dot() on arrays.");
	}

	public void assgmtDelegate(PTPrimitiveType src) {
		throw new EntDataTypeException("assgmtDelegate for arrays " +
			"is not yet supported.");
	}

	public boolean typeCheck(PTType a) {
		return (a instanceof PTArray  &&
			this.getType() == a.getType() &&
			this.dimensions == ((PTArray)a).dimensions &&
			this.baseType == ((PTArray)a).baseType);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(super.toString());
		b.append(",dim=").append(this.dimensions);
		b.append(",baseType=").append(this.baseType.toString());
		return b.toString();
	}
}

