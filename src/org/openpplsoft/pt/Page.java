/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openpplsoft.pt.pages.PFlag;
import org.openpplsoft.pt.pages.PgToken;
import org.openpplsoft.pt.peoplecode.PagePeopleCodeProg;
import org.openpplsoft.pt.peoplecode.PeopleCodeProg;
import org.openpplsoft.runtime.DefnCache;
import org.openpplsoft.runtime.OPSVMachRuntimeException;
import org.openpplsoft.sql.OPSResultSet;
import org.openpplsoft.sql.OPSStmt;
import org.openpplsoft.sql.StmtLibrary;

/**
 * Represents a PeopleTools page definition.
 */
public class Page {

  private static Logger log = LogManager.getLogger(Page.class.getName());

  private final String ptPNLNAME;

  private List<PgToken> subpages;
  private List<PgToken> secpages;
  private Map<Integer, PgToken> tokenTable;
  private List<PgToken> tokens;
  private PeopleCodeProg pageActivateProg;
  private Set<Integer> referencedMsgSets;
  private boolean hasInitialized, hasDiscoveredPagePC;

  /**
   * Creates a representation of the page definition for
   * the provided page name.
   * @param pnlname name of the page defn
   */
  public Page(final String pnlname) {
    this.ptPNLNAME = pnlname;
  }

  /**
   * @return the name of the page (PNLNAME field on PSPNLFIELD table)
   */
  public String getPNLNAME() {
    return this.ptPNLNAME;
  }

  /**
   * @return the page tokens attached to this page
   */
  public List<PgToken> getTokens() {
    return this.tokens;
  }

  /**
   * Pulls data from the database about this page definition.
   * This functionality is separate from the constructor body because
   * certain queries used here are enforced emissions; running the queries
   * at instantiation would cause queries to be out of order.
   */
  public void init() {

    if (this.hasInitialized) { return; }
    this.hasInitialized = true;

    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSPNLDEFN",
        new String[]{this.ptPNLNAME});
    OPSResultSet rs = ostmt.executeQuery();

    // Do nothing with record for now.
    rs.next();
    rs.close();
    ostmt.close();

    this.subpages = new ArrayList<PgToken>();
    this.secpages = new ArrayList<PgToken>();
    this.tokenTable = new HashMap<Integer, PgToken>();
    this.tokens = new ArrayList<PgToken>();
    this.referencedMsgSets = new LinkedHashSet<Integer>();

    ostmt = StmtLibrary.getStaticSQLStmt("query.PSPNLFIELD",
        new String[]{this.ptPNLNAME});
    rs = ostmt.executeQuery();

    /*
     * TODO(mquinn): Throw exception if no records were read
     * (need to use counter, no method on rs available).
     */
    int nextFieldNum = 1;
    while (rs.next()) {

      final PgToken pf = new PgToken(
          rs.getString("RECNAME").trim(),
          rs.getString("FIELDNAME").trim(),
          rs.getString("SUBPNLNAME").trim(),
          rs.getInt("OCCURSLEVEL"),
          rs.getInt("FIELDUSE"),
          rs.getInt("ASSOCFIELDNUM"),
        /*
         * Even though PSPNLFIELD has a FIELDNUM field, it is
         * not selected in the query (although it is in the ORDER
         * BY clause). I do not want to make PSPNLFIELD unenforced,
         * so for now I am using an incremented var.
         */
          nextFieldNum++);

    /*  if (pf.getRecName().equals("DERIVED_ADDRESS")
          && pf.getFldName().equals("ADDRESS4")
          && this.ptPNLNAME.equals("EO_ADDRESS_SRCH")
          && rs.getInt("FIELDUSE") == 0) {
        throw new OPSVMachRuntimeException("FIELDUSE is 0, here: " + this.ptPNLNAME);
      }*/

      switch (rs.getInt("FIELDTYPE")) {
        case PSDefn.PageFieldType.STATIC_TEXT:
        case PSDefn.PageFieldType.FRAME:
        case PSDefn.PageFieldType.STATIC_IMAGE:
        case PSDefn.PageFieldType.HORIZONTAL_RULE:
          break;
        case PSDefn.PageFieldType.GROUPBOX:
          pf.addFlag(PFlag.GROUPBOX);
          this.tokenTable.put(pf.getFieldNum(), pf);
          this.tokens.add(pf);
          break;
        case PSDefn.PageFieldType.SUBPAGE:
          pf.addFlag(PFlag.PAGE);
          pf.addFlag(PFlag.SUBPAGE);
          this.subpages.add(pf);
          this.tokenTable.put(pf.getFieldNum(), pf);
          this.tokens.add(pf);
          break;
        case PSDefn.PageFieldType.PUSHBTN_LINK_PEOPLECODE:
        case PSDefn.PageFieldType.PUSHBTN_LINK_SCROLL_ACTION:
        case PSDefn.PageFieldType.PUSHBTN_LINK_TOOLBAR_ACTION:
        case PSDefn.PageFieldType.PUSHBTN_LINK_EXTERNAL:
        case PSDefn.PageFieldType.PUSHBTN_LINK_INTERNAL:
        case PSDefn.PageFieldType.PUSHBTN_LINK_PROCESS:
        case PSDefn.PageFieldType.PUSHBTN_LINK_SECPAGE:
        case PSDefn.PageFieldType.PUSHBTN_LINK_PROMPT_ACTION:
        case PSDefn.PageFieldType.PUSHBTN_LINK_PAGE_ANCHOR:
        case PSDefn.PageFieldType.PUSHBTN_LINK_INST_MSG_ACTION:
          pf.addFlag(PFlag.PUSHBTN_LINK);
          this.tokenTable.put(pf.getFieldNum(), pf);
          this.tokens.add(pf);
          break;
        case PSDefn.PageFieldType.SECPAGE:
          pf.addFlag(PFlag.PAGE);
          pf.addFlag(PFlag.SECPAGE);
          this.secpages.add(pf);
          this.tokenTable.put(pf.getFieldNum(), pf);
          this.tokens.add(pf);
          break;
        case PSDefn.PageFieldType.SCROLL_BAR:
        case PSDefn.PageFieldType.GRID:
        case PSDefn.PageFieldType.SCROLL_AREA:
          pf.addFlag(PFlag.SCROLL_START);
          this.tokenTable.put(pf.getFieldNum(), pf);
          this.tokens.add(pf);
          break;
        default:
          pf.addFlag(PFlag.GENERIC);
          this.tokenTable.put(pf.getFieldNum(), pf);
          this.tokens.add(pf);
          if (pf.getRecName().length() == 0 || pf.getFldName().length() == 0) {
            throw new OPSVMachRuntimeException("A generic field with "
                + "either a blank RECNAME "
                + "or FIELDNAME was encountered.");
          }
      }
    }

    rs.close();
    ostmt.close();

    /*
     * Now that we have collected this page's tokens, we need
     * to connect each related display field tokens to the display
     * control field token associated with it, and vice versa.
     * This cannot be done
     * while iterating over the result set b/c some referenced tokens
     * will not be available.
     */
    for (PgToken tok : this.tokens) {
      if (tok.isRelatedDisplay()) {
        final PgToken assocTok = this.tokenTable.get(tok.getAssocFieldNum() + 1);
        if (assocTok == null || !assocTok.isDisplayControl()) {
          throw new OPSVMachRuntimeException("On page " + this.ptPNLNAME + ", "
              + "encountered rel disp page token (" + tok + ") but assoc tok "
              + "is either null or not display control: " + assocTok);
        }
        tok.setDispControlFieldTok(assocTok);
        assocTok.addRelDispFieldTok(tok);
      }
    }
  }

  /**
   * Recursively caches all subpage definitions attached to
   * this page.
   */
  public void recursivelyLoadSubpages() {
    final Page loadedPage = DefnCache.getPage(this.ptPNLNAME);

    for (PgToken tok : loadedPage.subpages) {
      final Page p = DefnCache.getPage(tok.getSubPnlName());
      p.recursivelyLoadSubpages();
    }
  }

  /**
   * Recursively caches all secpage definitions attached to this page.
   * This method is complex for a reason. PeopleTools recursively
   * iterates through subpages before recursively iterating through
   * secpages. However, during both sets of recursion, the order in
   * which records are first referenced must be preserved in a buffer;
   * when the secpage recursion begins, each occurrence of a secpage
   * must cause the buffer to be flushed in the form of SQL requests
   * for each record's Record PC listing.
   * To accomplish this, we must be able to "expand" the secpages
   * during secpage recursion, with emphasis on the fact that
   * the expansions must be done in place, surrounded by all the
   * other fields that come before/after it
   * in the recursive traversal. The RecordPCListRequestBuffer abstracts
   * the expansion process away from this routine.
   */
  public void recursivelyLoadSecpages() {

    final  Page loadedPage = DefnCache.getPage(this.ptPNLNAME);
    final List<PgToken> secpageTokens = new ArrayList<PgToken>();

    // Recursively expand/search subpages for secpages.
    for (PgToken tok : loadedPage.tokens) {

      if (tok.hasFlag(PFlag.SUBPAGE)) {

        final Page p = DefnCache.getPage(tok.getSubPnlName());
        p.recursivelyLoadSecpages();

      } else if (tok.hasFlag(PFlag.SECPAGE)) {

        secpageTokens.add(tok);

      } else if (tok.getSubPnlName().length() == 0
          && tok.getRecName() != null && tok.getFldName() != null
          && tok.getRecName().length() > 0 && tok.getFldName().length() > 0) {

        this.issuePCListRequestForRecord(tok.getRecName());
      }
    }

    // Then, recursively expand/search secpages for more secpages.
    for (PgToken marker : secpageTokens) {
      final Page p = DefnCache.getPage(marker.getSubPnlName());
      p.recursivelyLoadSecpages();
    }
  }

  private void issuePCListRequestForRecord(final String recName) {

    if (recName != null && !PSDefn.isSystemRecord(recName)) {

      final Record recDefn = DefnCache.getRecord(recName);
      log.debug("Issuing PC List Request for {}", recDefn.getRecName());
      recDefn.discoverRecordPC();

      /*
       * If this record contains subrecords, requests for their
       * Record PC listings should be issued now.
       */
      for (String subrecname : recDefn.getSubrecordNames()) {
        this.issuePCListRequestForRecord(subrecname);
      }
    }
  }

  public PeopleCodeProg getPageActivateProg() {
    return this.pageActivateProg;
  }

  /**
   * Retrieves any and all Page PeopleCode associated with this page
   * from the database.
   */
  public void discoverPagePC() {

    if (this.hasDiscoveredPagePC) { return; }
    this.hasDiscoveredPagePC = true;

    final OPSStmt ostmt = StmtLibrary.getStaticSQLStmt(
        "query.PSPCMPROG_RecordPCList",
        new String[]{PSDefn.PAGE, this.ptPNLNAME});
    final OPSResultSet rs = ostmt.executeQuery();

    while (rs.next()) {
      final PeopleCodeProg prog = new PagePeopleCodeProg(this.ptPNLNAME);
      this.pageActivateProg = DefnCache.getProgram(prog);
    }

    rs.close();
    ostmt.close();
  }

  /**
   * Caches the msg sets referenced by the fields on this page. Note
   * that this is required for tracefile verification, as the msgset
   * queries are enforced. This can be skipped for optimal performance
   * if tracefile verification is not needed.
   * TODO(mquinn): Make this a nop when running with tracefile verification.
   */
  public void cacheReferencedMsgSets() {
    for (int msgSet : this.referencedMsgSets) {
      DefnCache.getMsgSet(msgSet);
    }
  }
}
