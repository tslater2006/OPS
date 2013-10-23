package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.Stack;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.ComponentBuffer;

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

/*	public void generateStructure(int contextScrollLevel, String contextPrimaryRecName) throws Exception {

		**
		 * Adjust the scroll levels and primary record names
		 * based on the current context.
		 *
		this.adjustPageObjsWithinThisContext(contextScrollLevel, contextPrimaryRecName);

		//System.out.println("Page objects for page; " + this.PNLNAME + "; size: " + this.pageObjs.size());

		// We'll reset the buffer pointer once we finish using these initial values.
		int initialScrollLevel = ComponentBuffer.currScrollLevel;
		String initialPrimaryRecName = ComponentBuffer.currSB.primaryRecName;

		//System.out.println("Generating structure for page: " + this.PNLNAME);

		HashMap<Page, Boolean> preemptivelyGeneratedPageList = new HashMap<Page, Boolean>();

		//TODO: Throw exception if there are no page objects.
		for(Object pageobj : this.pageObjs) {

			if(pageobj instanceof ScrollLevelStart) {
				continue;
			} else if(pageobj instanceof PageField) {

				PageField pf = (PageField) pageobj;

				**
				 * TODO: In the event of problems, mess around with the ordering here.
			 	 *
				if(pf.SUBPNLNAME != null) {

					**
					 * TODO 10-22-2013
					 * The code below fixed the problem with Level 0 record ordering,
					 * but fails to account for potential scroll starts before seeking to
					 * the page to preemptively load. Need to re-architect this method, possibly
					 * with a queue or other data structure.
					 *
					//System.out.println("Found subpanel attacted to Record." + pf.RECNAME + " Field." + pf.FIELDNAME);
					for(int i=0; i<this.pageObjs.size(); i++) {
						if(this.pageObjs.get(i) instanceof Page
							&& ((Page) pageObjs.get(i)).PNLNAME.equals(pf.SUBPNLNAME)) {
							Page p1 = BuildAssistant.getLoadedPage(((Page) pageObjs.get(i)));
							p1.generateStructure(p1.contextScrollLevel, p1.contextPrimaryRecName);
							preemptivelyGeneratedPageList.put(((Page) pageObjs.get(i)), true);
							break;
						}
					}
					ComponentBuffer.addPageField(pf);
				} else if((pf.FIELDUSE & REL_DISP_FLAG) > 0) {
					//System.out.println("Related display field: " + fldname + "on page " + this.PNLNAME);
				} else if(pf.recordDefn != null && pf.recordDefn.RECTYPE == 3) {
					// Subrecord fields are not part of the component structure.
					//System.out.println("Detected field on subrecord.");
				} else {
					ComponentBuffer.addPageField(pf);
				}
				continue;
			}

			Page p = (Page) pageobj;
			if(preemptivelyGeneratedPageList.get(p) == null) {
				Page p2 = BuildAssistant.getLoadedPage(p);
				if(this.PNLNAME.equals("SCC_SUM_WORK")) {
					System.out.println(p2.PNLNAME + ", " + p2.contextScrollLevel);
				}
				p2.generateStructure(p2.contextScrollLevel, p2.contextPrimaryRecName);
			}
		}

		// Reset the buffer pointer to where it was when we started.
		ComponentBuffer.pointAtScroll(initialScrollLevel, initialPrimaryRecName);
	}

	public void adjustPageObjsWithinThisContext(int contextScrollLevel, String contextPrimaryRecName) {

		Stack<ScrollLevelStart> scrolls = new Stack<ScrollLevelStart>();
		ScrollLevelStart initialSls = new ScrollLevelStart(contextScrollLevel);
		initialSls.contextPrimaryRecName = contextPrimaryRecName;
		scrolls.push(initialSls);

		for(int i=0; i < this.pageObjs.size(); i++) {
			Object obj = this.pageObjs.get(i);

			if(obj instanceof ScrollLevelStart) {
				ScrollLevelStart sls = ((ScrollLevelStart) obj);

				String foundPrimaryRecName = null;
				for(int a = (i+1); a < this.pageObjs.size(); a++) {
					Object o = this.pageObjs.get(a);
					if(o instanceof PageField) {
						PageField pf = ((PageField) o);

						// Exclude groupboxes and empty RECNAMEs.
						if(pf.FIELDTYPE != 2 && pf.RECNAME.length() > 0) {
							foundPrimaryRecName = pf.RECNAME;
							break;
						}
					}
				}

				if(foundPrimaryRecName == null) {
					System.out.println("[ERROR] Failed to find primary record name for scroll area.");
					System.exit(1);
				}

				sls.contextPrimaryRecName = foundPrimaryRecName;
				sls.contextScrollLevel = contextScrollLevel + sls.OCCURSLEVEL;
				scrolls.push(sls);

			} else {
				int expectedScrollLevel = -1;

				if(obj instanceof Page) {
					expectedScrollLevel = contextScrollLevel + ((Page) obj).OCCURSLEVEL;
				} else {
					expectedScrollLevel = contextScrollLevel + ((PageField) obj).OCCURSLEVEL;
				}

				while(scrolls.peek().contextScrollLevel > expectedScrollLevel) {
					scrolls.pop();
				}

				if(obj instanceof Page) {
					Page modPage = ((Page) obj);
					modPage.contextScrollLevel = scrolls.peek().contextScrollLevel;
					modPage.contextPrimaryRecName = scrolls.peek().contextPrimaryRecName;
					this.pageObjs.set(i, modPage);
				} else {
					PageField modPf = ((PageField) obj);
					modPf.contextScrollLevel = scrolls.peek().contextScrollLevel;
					modPf.contextPrimaryRecName = scrolls.peek().contextPrimaryRecName;
					this.pageObjs.set(i, modPf);
				}
			}
		}
	}*/
}

