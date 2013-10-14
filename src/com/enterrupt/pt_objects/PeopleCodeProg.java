package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Blob;

public class PeopleCodeProg {

    public String recname;
    public String fldname;
    public String event;
    public byte[] progtxtbytes;

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
        this.progtxtbytes = b.getBytes(1, num_bytes); // first byte is at idx 1
    }
}

