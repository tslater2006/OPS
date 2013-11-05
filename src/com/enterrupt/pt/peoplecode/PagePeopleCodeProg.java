package com.enterrupt.pt_objects;

import java.lang.StringBuilder;

public class PagePeopleCodeProg extends ClassicPeopleCodeProg {

	public String PNLNAME;

	public PagePeopleCodeProg(String pnlname) {
		super();
		this.PNLNAME = pnlname;
		this.event = "Activate";
		this.initBindVals();
	}

	protected void initBindVals() {
		this.bindVals = new String[14];

		this.bindVals[0] = PSDefn.PAGE;
		this.bindVals[1] = this.PNLNAME;
		this.bindVals[2] = PSDefn.EVENT;
		this.bindVals[3] = this.event;
		for(int i = 4; i < this.bindVals.length; i+=2) {
			this.bindVals[i] = "0";
			this.bindVals[i+1] = PSDefn.NULL;
		}
	}

	public String getDescriptor() {

		StringBuilder builder = new StringBuilder();
		builder.append("PagePC.").append(this.PNLNAME).append(".").append(this.event);
		return builder.toString();
	}
}
