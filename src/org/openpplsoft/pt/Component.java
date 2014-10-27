/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.pages.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.trace.*;
import org.openpplsoft.types.*;

/**
 * Represents a PeopleTools component definition.
 */
public class Component {

  private static Logger log = LogManager.getLogger(Component.class.getName());

  private String ptPNLGRPNAME;
  private String ptMARKET;

  // search record used when in add mode
  private String ptADDSRCHRECNAME;
  // name of non-add search record for this component
  private String ptSEARCHRECNAME;
  // 4-bit mask of allowed component modes
  private int ptACTIONS;
  // 0 - 2: New, Search, Keyword Search
  private int ptPRIMARYACTION;
  // 0 - 3: The mode used when ptPRIMARYACTION is Search
  private int ptDFLTACTION;
  // based on mode component is running in.
  private String searchRecordToUse;

  private List<Page> pages;
  private List<ComponentPeopleCodeProg> orderedComponentProgs;
  private boolean hasListOfComponentPCBeenRetrieved;

  /**
   * Creates a representation of the component with the given name
   * and market.
   * @param pnlgrpname the name of the component
   * @param market the market to which the component belongs
   */
  public Component(final String pnlgrpname, final String market) {

    this.ptPNLGRPNAME = pnlgrpname;
    this.ptMARKET = market;

    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSPNLGRPDEFN",
        new String[]{this.ptPNLGRPNAME, this.ptMARKET});
    OPSResultSet rs = ostmt.executeQuery();

    rs.next();
    this.ptADDSRCHRECNAME = rs.getString("ADDSRCHRECNAME");
    this.ptSEARCHRECNAME = rs.getString("SEARCHRECNAME");
    this.ptACTIONS = rs.getInt("ACTIONS");
    this.ptPRIMARYACTION = rs.getInt("PRIMARYACTION");
    this.ptDFLTACTION = rs.getInt("DFLTACTION");

    rs.close();
    ostmt.close();

    /*
     * Select the search record to use based on the mode
     * the component should open in.
     */
    if (this.ptPRIMARYACTION == PSDefn.PRIMARYACTION_NEW) {
      this.searchRecordToUse = this.ptADDSRCHRECNAME;
    } else if (this.ptPRIMARYACTION == PSDefn.PRIMARYACTION_SEARCH) {
      this.searchRecordToUse = this.ptSEARCHRECNAME;
    } else {
      throw new OPSVMachRuntimeException("Unable to select search record "
          + "due to unknown Primary Action value.");
    }

    this.pages = new ArrayList<Page>();
    ostmt = StmtLibrary.getStaticSQLStmt("query.PSPNLGROUP",
        new String[]{this.ptPNLGRPNAME, this.ptMARKET});
    rs = ostmt.executeQuery();

    while (rs.next()) {
      // All pages at the root of the component start at scroll level 0.
      final Page p = new Page(rs.getString("PNLNAME"));
      log.debug("Component contains Page.{}", p.getPNLNAME());
      this.pages.add(p);
    }

    rs.close();
    ostmt.close();
  }

  /**
   * Retrieves the list of PeopleCode programs attached to this
   * component, and caches the corresponding definition for each.
   */
  public void getListOfComponentPC() {

    if (this.hasListOfComponentPCBeenRetrieved) { return; }
    this.hasListOfComponentPCBeenRetrieved = true;

    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSPCMPROG_CompPCList",
        new String[]{PSDefn.COMPONENT, this.ptPNLGRPNAME, PSDefn.MARKET,
        this.ptMARKET});
    final OPSResultSet rs = ostmt.executeQuery();

    this.orderedComponentProgs = new ArrayList<ComponentPeopleCodeProg>();

    while (rs.next()) {
      final String objectid3 = rs.getString("OBJECTID3").trim();
      final String objectval3 = rs.getString("OBJECTVALUE3").trim();
      final String objectid4 = rs.getString("OBJECTID4").trim();
      final String objectval4 = rs.getString("OBJECTVALUE4").trim();
      final String objectid5 = rs.getString("OBJECTID5").trim();
      final String objectval5 = rs.getString("OBJECTVALUE5").trim();

      PeopleCodeProg prog = null;

      // Example: SSS_STUDENT_CENTER.GBL.PreBuild
      if (objectid3.equals(PSDefn.EVENT)) {
        prog = new ComponentPeopleCodeProg(this.ptPNLGRPNAME, this.ptMARKET,
          objectval3);

      // Example: SSS_STUDENT_CENTER.LS_SS_PERS_SRCH.SearchInit
      } else if (objectid3.equals(PSDefn.RECORD)
            && objectid4.equals(PSDefn.EVENT)) {
        prog = new ComponentPeopleCodeProg(this.ptPNLGRPNAME, this.ptMARKET,
          objectval3, objectval4);

      // Example: SSS_STUDENT_CENTER.LS_DERIVED_SSS_SCL
      //            .SS_CLS_SCHED_LINK.FieldChange
      } else if (objectid3.equals(PSDefn.RECORD)
            && objectid4.equals(PSDefn.FIELD)
            && objectid5.equals(PSDefn.EVENT)) {
        prog = new ComponentPeopleCodeProg(this.ptPNLGRPNAME, this.ptMARKET,
          objectval3, objectval4, objectval5);

      } else {
        throw new OPSVMachRuntimeException("Unexpected type of "
            + "Component PC encountered.");
      }

      prog = DefnCache.getProgram(prog);
      this.orderedComponentProgs.add((ComponentPeopleCodeProg) prog);
    }

    rs.close();
    ostmt.close();
  }

  public ComponentPeopleCodeProg getProgramForRecordFieldEvent(
      final PCEvent event, final RecordField recFldDefn) {
    for (ComponentPeopleCodeProg prog : this.orderedComponentProgs) {
      if (prog.RECNAME != null && prog.RECNAME.equals(recFldDefn.RECNAME)
          && prog.FLDNAME != null && prog.FLDNAME.equals(recFldDefn.FIELDNAME)
          && prog.event.equals(event.getName())) {
        return prog;
      }
    }
    return null;
  }

  public ComponentPeopleCodeProg getProgramForRecordEvent(
      final PCEvent event, final Record recDefn) {
    for (ComponentPeopleCodeProg prog : this.orderedComponentProgs) {
      if (prog.RECNAME != null && prog.RECNAME.equals(recDefn.RECNAME)
          && prog.FLDNAME == null
          && prog.event.equals(event.getName())) {
        return prog;
      }
    }
    return null;
  }

  public ComponentPeopleCodeProg getProgramForEvent(final PCEvent event) {
    for (ComponentPeopleCodeProg prog : this.orderedComponentProgs) {
      if (prog.RECNAME == null
          && prog.FLDNAME == null
          && prog.event.equals(event.getName())) {
        return prog;
      }
    }
    return null;
  }

  public String getSearchRecordName() {
    return this.searchRecordToUse;
  }

  /**
   * @return Whether or not at least one FieldFormula program was run.
   */
  public boolean runFieldFormula() {
    throw new OPSVMachRuntimeException("TODO: Reimplement runFieldFormula, this time "
        + "iterating through rows of rowsets in component buffer.");
  }

  public List<Page> getPages() {
    return this.pages;
  }

  /**
   * For the flowchart, see:
   * http://docs.oracle.com/cd/E18083_01/pt851pbr0/eng/psbooks/tpcd/chapter.htm?File=tpcd/htm/tpcd07.htm%23g037ee99c9453fb39_ef90c_10c791ddc07__4acchttp://docs.oracle.com/cd/E18083_01/pt851pbr0/eng/psbooks/tpcd/chapter.htm?File=tpcd/htm/tpcd07.htm%23g037ee99c9453fb39_ef90c_10c791ddc07__4acc
   */
  public void runDefaultProcessing() {
    // First, run field-level default processing; continue to do so
    // until a pass indicates that either 1) there are no blank fields
    // remaining or 2) no field was changed during the pass.
    boolean doContinue;
    do {
      doContinue = this.runFieldLevelDefaultProcessing();
    } while (doContinue);

    // Next, run the FieldFormula program for all fields in all rows
    // of the component.
    boolean wereAnyFieldFormulaProgsRun = this.runFieldFormula();

    // If any field formula programs were run, continue to run default
    // processing as before.
    if (wereAnyFieldFormulaProgsRun) {
      do {
        doContinue = this.runFieldLevelDefaultProcessing();
      } while (doContinue);
    }
  }

  /**
   * Iterates through each field in the component buffer and runs default
   * processing on it; this could mean giving it a predefined constant value,
   * looking up the default value from another table, or running a FieldDefault
   * program to set the value programmatically.
   */
  private boolean runFieldLevelDefaultProcessing() {
    throw new OPSVMachRuntimeException("TODO: Re-implement runFieldLevelDefaultProcessing, "
        + "iterating over rows in rowsets this time rather than scroll buffers (see git history "
        + "for logic to use here).");
  }
}
