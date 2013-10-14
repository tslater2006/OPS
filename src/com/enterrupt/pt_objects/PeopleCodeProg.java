package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Blob;
import java.lang.StringBuilder;

public class PeopleCodeProg {

    public String recname;
    public String fldname;
    public String event;
	private StringBuilder progTextBuilder;
    public byte[] progBytes;

	public int byteCursorPos = 0;

    public PeopleCodeProg(String recname, String fldname, String event) {
        this.recname = recname;
        this.fldname = fldname;
        this.event = event;
    }

    public void setProgText(Blob b) throws Exception {

        /**
         * TODO: Here we risk losing progtext bytes since the length (originally a long)
         * is cast to int. Build in a check for this, use binarystream when this is the case.
         */
        int num_bytes = (int) b.length();
        this.progBytes = b.getBytes(1, num_bytes); // first byte is at idx 1
    }

	public void setByteCursorPos(int pos) {
		this.byteCursorPos = pos;
	}

	public byte getCurrentByte() {
		return this.progBytes[this.byteCursorPos];
	}

	public byte readNextByte() {
		System.out.printf("[READ] %d: 0x%02X\n", this.byteCursorPos, this.progBytes[this.byteCursorPos]);
		return this.progBytes[this.byteCursorPos++];
	}

	public byte readAhead() {
		if(this.byteCursorPos >= this.progBytes.length - 1) {
			return -1;
		}
		return this.progBytes[this.byteCursorPos];
	}

	public void appendProgText(char c) {
		if(this.progTextBuilder == null) {
			this.progTextBuilder = new StringBuilder();
		}
		progTextBuilder.append(c);
	}

	public void appendProgText(String s) {
		if(this.progTextBuilder == null) {
			this.progTextBuilder = new StringBuilder();
		}
		progTextBuilder.append(s);
	}

	public String getProgText() {
		return this.progTextBuilder.toString();
	}
}

