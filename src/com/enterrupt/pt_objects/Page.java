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
	public ArrayList<Object> pageObjs;
	public static final byte REL_DISP_FLAG = (byte) 16;

    public Page(String pnlname) {
        this.PNLNAME = pnlname;
        this.subpages = new ArrayList<Page>();
        this.secpages = new ArrayList<Page>();
		this.pageObjs = new ArrayList<Object>();
    }

    public void loadInitialMetadata() throws Exception {

		if(BuildAssistant.pageDefnCache.get(this.PNLNAME) != null) {
			return;
		}

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

			String r = rs.getString("RECNAME").trim();
			String f = rs.getString("FIELDNAME").trim();

            switch(rs.getInt("FIELDTYPE")) {
                case 11:
                    Page p1 = new Page(rs.getString("SUBPNLNAME"));
					this.subpages.add(p1);
					this.pageObjs.add(p1);
                    break;
                case 18:
                    Page p2 = new Page(rs.getString("SUBPNLNAME"));
                    this.secpages.add(p2);
					this.pageObjs.add(p2);
				case 19:
					//System.out.println("Found grid; occurs level: " + rs.getInt("OCCURSLEVEL"));
					break;
        		case 27:
					//System.out.println("Found scroll; occurs level: " + rs.getInt("OCCURSLEVEL"));
					break;
		      	default:
					if(r.length() > 0 && f.length() > 0) {
						PageField pf = new PageField(r, f);
						pf.FIELDUSE = (byte) rs.getInt("FIELDUSE");
						pf.OCCURSLEVEL = rs.getInt("OCCURSLEVEL");
						pageObjs.add(pf);
					}
            }

			/**
			 * Issue request for record's definition and fields,
			 * regardless of field type.
		 	 */
			if(r.length() > 0) {
				Record rec = new Record(r);
				rec.loadInitialMetadata();
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

	public void generateStructure() {

		for(Object pageobj : this.pageObjs) {

			if(pageobj instanceof PageField) {
				PageField pf = (PageField) pageobj;
				if((pf.FIELDUSE & REL_DISP_FLAG) > 0) {
					//System.out.println("Related display field: " + fldname + "on page " + this.PNLNAME);
				} else {
					ComponentBuffer.addField(pf);
				}
				continue;
			}

			// TODO: Should this just involve subpages first, or both subpages and secpages?
			Page p = (Page) pageobj;
			p.generateStructure();
		}
	}
}

