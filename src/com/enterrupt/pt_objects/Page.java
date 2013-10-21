package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.ComponentBuffer;

public class Page {

    public String PNLNAME;
    public ArrayList<String> subpages;
    public ArrayList<String> secpages;

    public Page(String pnlname) {
        this.PNLNAME = pnlname;
        this.subpages = new ArrayList<String>();
        this.secpages = new ArrayList<String>();
    }

    public void loadInitialMetadata(int indent) throws Exception {

		for(int i=0; i<indent; i++){ System.out.print(" "); }
		System.out.println("Page: " + this.PNLNAME);

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSPNLDEFN(this.PNLNAME);
        rs = pstmt.executeQuery();
        rs.next(); // Do nothing with record for now.
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSPNLFIELD(this.PNLNAME);
        rs = pstmt.executeQuery();

        ArrayList<String> subpanels = new ArrayList<String>();
        while(rs.next()) {
            switch(rs.getInt("FIELDTYPE")) {
                case 11:
                    this.subpages.add(rs.getString("SUBPNLNAME"));
                    break;
                case 18:
                    this.secpages.add(rs.getString("SUBPNLNAME"));
                    break;
				case 19:
					System.out.println("Found grid; occurs level: " + rs.getInt("OCCURSLEVEL"));
					break;
        		case 27:
					System.out.println("Found scroll; occurs level: " + rs.getInt("OCCURSLEVEL"));
					System.out.println(this.PNLNAME);
					break;
		      	default:
					String r = rs.getString("RECNAME").trim();
					String f = rs.getString("FIELDNAME").trim();
					if(r.length() > 0 && f.length() > 0) {
  	            		BuildAssistant.addRecordField(rs.getString("RECNAME"), rs.getString("FIELDNAME"));
					}
            }

			/**
			 * If the RECNAME field is not blank, load it's record metadata, regardless of
			 * what type of field the record represents.
			 */
			String recname = rs.getString("RECNAME").trim();
			if(BuildAssistant.recDefnCache.get(recname) == null && recname.trim().length() > 0) {

				//for(int i=0; i<indent + 1; i++){ System.out.print(" "); }
				//System.out.println("Record: " + recname + " (" + rs.getString("FIELDTYPE") + ")");

				Record r = new Record(recname);
				r.loadInitialMetadata();
			}

			String fldname = rs.getString("FIELDNAME").trim();
			byte fieldUseMask = (byte) rs.getInt("FIELDUSE");
			byte REL_DISP_FLAG = (byte) 16;
			if(recname.length() > 0 && fldname.length() > 0) {

				if(recname.equals("EO_ADDRESS_WRK")) {
					System.out.println("EO_ADDRESS_WRK, Field : " + fldname);
				}

				if((fieldUseMask & REL_DISP_FLAG) > 0) {
					//System.out.println("Related display field: " + fldname + "on page " + this.PNLNAME);
				} else {
					ComponentBuffer.addField(recname, fldname, rs.getInt("OCCURSLEVEL"));
				}
			}
        }
        rs.close();
        pstmt.close();
    }
}

