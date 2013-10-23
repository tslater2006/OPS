package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;

public class Page {

    public String PNLNAME;
    public ArrayList<PgToken> subpages;
    public ArrayList<PgToken> secpages;
	public ArrayList<PgToken> tokens;

    public Page(String pnlname) {
        this.PNLNAME = pnlname;
        this.subpages = new ArrayList<PgToken>();
        this.secpages = new ArrayList<PgToken>();
		this.tokens = new ArrayList<PgToken>();
    }

    public void loadInitialMetadata() throws Exception {

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSPNLDEFN(this.PNLNAME);
        rs = pstmt.executeQuery();
        rs.next(); // Do nothing with record for now.
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSPNLFIELD(this.PNLNAME);
        rs = pstmt.executeQuery();

		// TODO: Throw exception if no records were read (need to use counter, no method on rs available).
        while(rs.next()) {

			PgToken pf = new PgToken();
			pf.RECNAME = rs.getString("RECNAME").trim();
			pf.FIELDNAME = rs.getString("FIELDNAME").trim();
			pf.SUBPNLNAME = rs.getString("SUBPNLNAME").trim();
			pf.OCCURSLEVEL = rs.getInt("OCCURSLEVEL");
			pf.FIELDUSE = (byte) rs.getInt("FIELDUSE");

			/**
			 * Issue request for the record definition and record fields,
			 * regardless of field type.
		 	 */
			BuildAssistant.getRecordDefn(pf.RECNAME);

            switch(rs.getInt("FIELDTYPE")) {

				case 2:
					pf.flags.add(AFlag.GROUPBOX);
					this.tokens.add(pf);
					break;
                case 11:
					pf.flags.add(AFlag.PAGE);
					this.subpages.add(pf);
					this.tokens.add(pf);
                    break;
                case 18:
					pf.flags.add(AFlag.PAGE);
                    this.secpages.add(pf);
					this.tokens.add(pf);
					break;

				case 10: // scroll bar
				case 19: // grid
        		case 27: // scroll area
					pf.flags.add(AFlag.SCROLL_START);
					this.tokens.add(pf);
					break;

		      	default:
					if(pf.RECNAME.length() > 0 && pf.FIELDNAME.length() > 0) {
						pf.flags.add(AFlag.GENERIC);
						this.tokens.add(pf);
					}
            }
        }
        rs.close();
        pstmt.close();
    }

	public void recursivelyLoadSubpages() throws Exception {
		Page loadedPage = BuildAssistant.getLoadedPage(this.PNLNAME);
		for(PgToken tok : loadedPage.subpages) {
			Page p = BuildAssistant.getLoadedPage(tok.SUBPNLNAME);
			p.recursivelyLoadSubpages();
		}
	}

	public void recursivelyLoadSecpages() throws Exception {
		Page loadedPage = BuildAssistant.getLoadedPage(this.PNLNAME);
		Page p;

		// Recursively expand/search subpages for secpages.
		for(PgToken tok : loadedPage.subpages) {
			p = BuildAssistant.getLoadedPage(tok.SUBPNLNAME);
			p.recursivelyLoadSecpages();
		}

		// Then, recursively expand/search secpages for more secpages.
		for(PgToken tok : loadedPage.secpages) {
			p = BuildAssistant.getLoadedPage(tok.SUBPNLNAME);
			p.recursivelyLoadSecpages();
		}
	}
}
