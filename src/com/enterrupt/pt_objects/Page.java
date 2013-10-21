package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.ComponentBuffer;

public class Page {

    public String PNLNAME;
    public ArrayList<Page> subpages;
    public ArrayList<Page> secpages;

    public Page(String pnlname) {
        this.PNLNAME = pnlname;
        this.subpages = new ArrayList<Page>();
        this.secpages = new ArrayList<Page>();
    }

    public void loadInitialMetadata() throws Exception {

		if(BuildAssistant.pageDefnCache.get(this.PNLNAME) != null) {
			return;
		}

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
                    this.subpages.add(new Page(rs.getString("SUBPNLNAME")));
                    break;
                case 18:
                    this.secpages.add(new Page(rs.getString("SUBPNLNAME")));
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

		BuildAssistant.cachePage(this);
    }

	public void recursivelyLoadSubpages() throws Exception {
		this.loadInitialMetadata();
		for(Page p : this.subpages) {
			p.recursivelyLoadSubpages();
		}
	}

	public void recursivelyLoadSecpages() throws Exception {
		this.loadInitialMetadata();

		// Recursively expand/search subpages for secpages.
		for(Page p : this.subpages) {
			p.recursivelyLoadSecpages();
		}

		// Then, recursively expand/search secpages for more secpages.
		for(Page p : this.secpages) {
			p.recursivelyLoadSecpages();
		}
	}
}

