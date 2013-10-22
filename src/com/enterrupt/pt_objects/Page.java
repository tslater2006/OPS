package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.ComponentBuffer;

public class Page {

    public String PNLNAME;
	public int scrollLevel;
    public ArrayList<Page> subpages;
    public ArrayList<Page> secpages;
	public ArrayList<Object> pageObjs;
	public static final byte REL_DISP_FLAG = (byte) 16;

    public Page(String pnlname, int scrollLevel) {
        this.PNLNAME = pnlname;
		this.scrollLevel = scrollLevel;
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
		PageField prevScrollPanelPf = null;
        while(rs.next()) {

			String r = rs.getString("RECNAME").trim();
			String f = rs.getString("FIELDNAME").trim();

			/**
			 * Result set is forward only, so if the previous element was a scroll
			 * panel, use the current RECNAME as the primary record name of the scroll area.
			 */
			if(prevScrollPanelPf != null) {
				prevScrollPanelPf.RECNAME = r;
				this.pageObjs.add(prevScrollPanelPf);
				prevScrollPanelPf = null;
			}

            switch(rs.getInt("FIELDTYPE")) {
                case 11:
                    Page p1 = new Page(rs.getString("SUBPNLNAME"), this.scrollLevel + rs.getInt("OCCURSLEVEL"));
					this.subpages.add(p1);
					this.pageObjs.add(p1);
                    break;
                case 18:
                    Page p2 = new Page(rs.getString("SUBPNLNAME"), this.scrollLevel + rs.getInt("OCCURSLEVEL"));
                    this.secpages.add(p2);
					this.pageObjs.add(p2);
					break;

				// Treating grid and scroll processing the same.
				case 19:
        		case 27:
					//System.out.println("Found grid/scroll at page: " + this.PNLNAME);
					PageField scrollPf = new PageField(null, null);
					scrollPf.beginsScroll = true;
					scrollPf.scrollLevel = this.scrollLevel + rs.getInt("OCCURSLEVEL");
					prevScrollPanelPf = scrollPf;
					break;

		      	default:
					if(r.length() > 0 && f.length() > 0) {
						PageField pf = new PageField(r, f);
						pf.FIELDUSE = (byte) rs.getInt("FIELDUSE");
						pf.scrollLevel = this.scrollLevel + rs.getInt("OCCURSLEVEL");
						//System.out.println(pf.scrollLevel);
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

		// We'll reset the buffer pointer once we finish using these initial values.
		int initialScrollLevel = ComponentBuffer.currScrollLevel;
		String initialPrimaryRecName = ComponentBuffer.currSB.primaryRecName;

		for(Object pageobj : this.pageObjs) {

			if(pageobj instanceof PageField) {

				PageField pf = (PageField) pageobj;

				if(pf.beginsScroll) {
					ComponentBuffer.pointAtScroll(pf.scrollLevel, pf.RECNAME);
				} else if((pf.FIELDUSE & REL_DISP_FLAG) > 0) {
					//System.out.println("Related display field: " + fldname + "on page " + this.PNLNAME);
				} else {
					if(pf.scrollLevel < ComponentBuffer.currScrollLevel) {
						ComponentBuffer.pointAtScroll(pf.scrollLevel, null);
					}
					ComponentBuffer.addPageField(pf);
				}
				continue;
			}

			/**
			 * TODO: If problems arise with ordering, consider structuring this similar to loading,
			 * where subpage traversal is prioritized/split out from secpages.
			 */
			Page p = (Page) pageobj;
			System.out.println("Generating structure from page: " + p.PNLNAME);
			p.generateStructure();
		}

		// Reset the buffer pointer to where it was when we started.
		ComponentBuffer.pointAtScroll(initialScrollLevel, initialPrimaryRecName);
	}
}

