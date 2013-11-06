package com.enterrupt.pt.peoplecode;

import java.sql.*;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.pt.*;

public class RecordPeopleCodeProg extends ClassicPeopleCodeProg {

	public String RECNAME;
	public String FLDNAME;

	public RecordPeopleCodeProg(String recname, String fldname, String event) {
		super();
		this.RECNAME = recname;
		this.FLDNAME = fldname;
		this.event = event;
		this.initBindVals();
	}

    protected void initBindVals() {

        this.bindVals = new String[14];

        this.bindVals[0] = PSDefn.RECORD;
        this.bindVals[1] = this.RECNAME;
        this.bindVals[2] = PSDefn.FIELD;
        this.bindVals[3] = this.FLDNAME;
		this.bindVals[4] = PSDefn.EVENT;
		this.bindVals[5] = this.event;
        for(int i = 6; i < this.bindVals.length; i+=2) {
            this.bindVals[i] = "0";
            this.bindVals[i+1] = PSDefn.NULL;
        }
    }

	public String getDescriptor() {
		return "RecordPC." + this.RECNAME + "." + this.FLDNAME + "." + this.event;
	}
}
