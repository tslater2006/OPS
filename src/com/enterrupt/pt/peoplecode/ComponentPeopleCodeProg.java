package com.enterrupt.pt_objects;

import java.lang.StringBuilder;

public class ComponentPeopleCodeProg extends ClassicPeopleCodeProg {

	public String PNLGRPNAME;
	public String MARKET;
	public String RECNAME;
	public String FLDNAME;

	public ComponentPeopleCodeProg(String pnlgrpname, String market, String event) {
		super();
		this.PNLGRPNAME = pnlgrpname;
		this.MARKET = market;
		this.event = event;
		this.initBindVals();
	}

	public ComponentPeopleCodeProg(String pnlgrpname, String market, String recname, String event) {
		super();
		this.PNLGRPNAME = pnlgrpname;
		this.MARKET = market;
		this.RECNAME = recname;
		this.event = event;
		this.initBindVals();
	}

	public ComponentPeopleCodeProg(String pnlgrpname, String market, String recname, String fldname, String event) {
		super();
		this.PNLGRPNAME = pnlgrpname;
		this.MARKET = market;
		this.RECNAME = recname;
		this.FLDNAME = fldname;
		this.event = event;
		this.initBindVals();
	}

	protected void initBindVals() {

		this.bindVals = new String[14];

		this.bindVals[0] = PSDefn.COMPONENT;
		this.bindVals[1] = this.PNLGRPNAME;
		this.bindVals[2] = PSDefn.MARKET;
		this.bindVals[3] = this.MARKET;
		for(int i = 4; i < this.bindVals.length; i+=2) {
			this.bindVals[i] = "0";
			this.bindVals[i+1] = PSDefn.NULL;
		}

		int idx = 4;
		if(this.RECNAME != null) {
			this.bindVals[idx++] = PSDefn.RECORD;
			this.bindVals[idx++] = this.RECNAME;

			if(this.FLDNAME != null) {
				this.bindVals[idx++] = PSDefn.FIELD;
				this.bindVals[idx++] = this.FLDNAME;
			}
		}

		this.bindVals[idx++] = PSDefn.EVENT;
		this.bindVals[idx++] = this.event;
	}

	public String getDescriptor() {

		StringBuilder builder = new StringBuilder();
		builder.append("ComponentPC.").append(this.PNLGRPNAME).append(".").append(this.MARKET).append(".");

		if(this.RECNAME != null) {
			builder.append(this.RECNAME).append(".");

			if(this.FLDNAME != null) {
				builder.append(this.FLDNAME).append(".");
			}
		}

		builder.append(this.event);
		return builder.toString();
	}
}
