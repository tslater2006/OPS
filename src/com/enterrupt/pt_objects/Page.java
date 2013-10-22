package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.Stack;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.HashMap;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.ComponentBuffer;

class ScrollLevelStart {
	public int scrollLevel;
	public String primaryRecName;
	public ScrollLevelStart(int s, String p) {
		this.scrollLevel = s;
		this.primaryRecName = p;
	}
}

public class Page {

    public String PNLNAME;

	public int scrollLevel;	  // calculated using OCCURSLEVEL
	public String scrollLevelPrimaryRecName;

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

		Stack<ScrollLevelStart> scrolls = new Stack<ScrollLevelStart>();
		scrolls.push(new ScrollLevelStart(this.scrollLevel, this.scrollLevelPrimaryRecName));
		boolean newScrollStartedFlag = false;

        while(rs.next()) {

			String r = rs.getString("RECNAME").trim();
			String f = rs.getString("FIELDNAME").trim();
			String subpnlname = rs.getString("SUBPNLNAME").trim();
			int currScrollLevel = this.scrollLevel + rs.getInt("OCCURSLEVEL");

			while(scrolls.peek().scrollLevel > currScrollLevel) {
				scrolls.pop();
			}

			if(newScrollStartedFlag) {
				scrolls.peek().primaryRecName = r;
				newScrollStartedFlag = false;
			}

            switch(rs.getInt("FIELDTYPE")) {

				case 2:
					System.out.println("Found groupox for rec " + r + " on page " + this.PNLNAME);
					break;

                case 11:
                    Page p1 = new Page(subpnlname, scrolls.peek().scrollLevel);
					p1.scrollLevelPrimaryRecName = scrolls.peek().primaryRecName;
					this.subpages.add(p1);
					this.pageObjs.add(p1);
                    break;
                case 18:
                    Page p2 = new Page(subpnlname, scrolls.peek().scrollLevel);
					p2.scrollLevelPrimaryRecName = scrolls.peek().primaryRecName;
                    this.secpages.add(p2);
					this.pageObjs.add(p2);
					break;

				// Treating scroll bar, grid, and scroll area processing the same.
				case 10:
				case 19:
        		case 27:
					//System.out.println("Found scroll bar/grid/scroll at page: " + this.PNLNAME);
					newScrollStartedFlag = true;
					scrolls.push(new ScrollLevelStart(currScrollLevel, null));
					break;

		      	default:
					if(r.length() > 0 && f.length() > 0) {
						PageField pf = new PageField(r, f);
						pf.FIELDUSE = (byte) rs.getInt("FIELDUSE");
						if(subpnlname.length() > 0) {
							pf.SUBPNLNAME = subpnlname;
						}
						pf.scrollLevel = scrolls.peek().scrollLevel;
						pf.scrollLevelPrimaryRecName = scrolls.peek().primaryRecName;
						pageObjs.add(pf);
					}
            }

			if(r.equals("FERPA_OVERRIDE")) {
				System.out.println(this.PNLNAME);
				System.out.println(scrolls.peek().scrollLevel + "\t" + scrolls.peek().primaryRecName);
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

		HashMap<Page, Boolean> preemptivelyGeneratedPageList = new HashMap<Page, Boolean>();

		for(Object pageobj : this.pageObjs) {

			if(pageobj instanceof PageField) {

				PageField pf = (PageField) pageobj;

				/**
				 * TODO: In the event of problems, mess around with the ordering here.
			 	 */
				if(pf.SUBPNLNAME != null) {

					/**
					 * TODO 10-22-2013
					 * The code below fixed the problem with Level 0 record ordering,
					 * but fails to account for potential scroll starts before seeking to
					 * the page to preemptively load. Need to re-architect this method, possibly
					 * with a queue or other data structure.
					 */
					//System.out.println("Found subpanel attacted to Record." + pf.RECNAME + " Field." + pf.FIELDNAME);
					for(int i=0; i<this.pageObjs.size(); i++) {
						if(this.pageObjs.get(i) instanceof Page
							&& ((Page) pageObjs.get(i)).PNLNAME.equals(pf.SUBPNLNAME)) {
							((Page) pageObjs.get(i)).generateStructure();
							preemptivelyGeneratedPageList.put(((Page) pageObjs.get(i)), true);
							break;
						}
					}
					ComponentBuffer.addPageField(pf);
				} else if((pf.FIELDUSE & REL_DISP_FLAG) > 0) {
					//System.out.println("Related display field: " + fldname + "on page " + this.PNLNAME);
				} else {
					ComponentBuffer.addPageField(pf);
				}
				continue;
			}

			Page p = (Page) pageobj;
			if(preemptivelyGeneratedPageList.get(p) == null) {
				//System.out.println("Generating structure from page: " + p.PNLNAME);
				p.generateStructure();
			}
		}

		// Reset the buffer pointer to where it was when we started.
		ComponentBuffer.pointAtScroll(initialScrollLevel, initialPrimaryRecName);
	}
}

