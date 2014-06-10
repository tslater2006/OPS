/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.sql.*;
import java.util.*;
import java.io.*;
import org.openpplsoft.sql.StmtLibrary;
import org.openpplsoft.buffers.*;
import org.openpplsoft.types.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.pt.pages.*;
import org.apache.logging.log4j.*;

public class Component {

  public String PNLGRPNAME;
  public String MARKET;
  public String ADDSRCHRECNAME; // search record used when in add mode
  public String SEARCHRECNAME; // name of non-add search record for this component
  public int ACTIONS;     // 4-bit mask of allowed component modes
  public int PRIMARYACTION; // 0 - 2: New, Search, Keyword Search
  public int DFLTACTION;    // 0 - 3: The mode used when PRIMARYACTION is Search

  private String searchRecordToUse; // based on mode component is running in.

  public ArrayList<Page> pages;
  public ArrayList<ComponentPeopleCodeProg> orderedComponentProgs;

  private static Logger log = LogManager.getLogger(Component.class.getName());

  private boolean hasListOfComponentPCBeenRetrieved = false;

  private class ScrollMarker {
    public String primaryRecName;
    public int scrollLevel;
    public PFlag src;

    public ScrollMarker() {}

    public ScrollMarker(int s, String p, PFlag a) {
      this.scrollLevel = s;
      this.primaryRecName = p;
      this.src = a;
    }
  }

  public Component(String pnlgrpname, String market) {

    this.PNLGRPNAME = pnlgrpname;
    this.MARKET = market;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      pstmt = StmtLibrary.getPSPNLGRPDEFN(this.PNLGRPNAME, this.MARKET);
      rs = pstmt.executeQuery();
      rs.next();
      this.ADDSRCHRECNAME = rs.getString("ADDSRCHRECNAME");
      this.SEARCHRECNAME = rs.getString("SEARCHRECNAME");
      this.ACTIONS = rs.getInt("ACTIONS");
      this.PRIMARYACTION = rs.getInt("PRIMARYACTION");
      this.DFLTACTION = rs.getInt("DFLTACTION");
      rs.close();
      pstmt.close();

      /*
       * Select the search record to use based on the mode
       * the component should open in.
       */
      if(this.PRIMARYACTION == PSDefn.PRIMARYACTION_New) {
        this.searchRecordToUse = this.ADDSRCHRECNAME;
      } else if(this.PRIMARYACTION == PSDefn.PRIMARYACTION_Search) {
        this.searchRecordToUse = this.SEARCHRECNAME;
      } else {
        throw new EntVMachRuntimeException("Unable to select search record "
            + "due to unknown Primary Action value.");
      }

      this.pages = new ArrayList<Page>();
      pstmt = StmtLibrary.getPSPNLGROUP(this.PNLGRPNAME, this.MARKET);
      rs = pstmt.executeQuery();
      while(rs.next()) {
        // All pages at the root of the component start at scroll level 0.
        Page p = new Page(rs.getString("PNLNAME"));
        log.debug("Component contains Page.{}", p.PNLNAME);
        this.pages.add(p);
      }
    } catch(java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if(rs != null) { rs.close(); }
        if(pstmt != null) { pstmt.close(); }
      } catch(java.sql.SQLException sqle) {}
    }
  }

  public void getListOfComponentPC() {

    if(this.hasListOfComponentPCBeenRetrieved) { return; }
    this.hasListOfComponentPCBeenRetrieved = true;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      this.orderedComponentProgs = new ArrayList<ComponentPeopleCodeProg>();
      pstmt = StmtLibrary.getPSPCMPROG_CompPCList(PSDefn.COMPONENT,
          this.PNLGRPNAME, PSDefn.MARKET, this.MARKET);
      rs = pstmt.executeQuery();

      while(rs.next()) {
        String objectid3 = rs.getString("OBJECTID3").trim();
        String objectval3 = rs.getString("OBJECTVALUE3").trim();
        String objectid4 = rs.getString("OBJECTID4").trim();
        String objectval4 = rs.getString("OBJECTVALUE4").trim();
        String objectid5 = rs.getString("OBJECTID5").trim();
        String objectval5 = rs.getString("OBJECTVALUE5").trim();

        PeopleCodeProg prog = null;

        // Example: SSS_STUDENT_CENTER.GBL.PreBuild
        if(objectid3.equals(PSDefn.EVENT)) {
          prog = new ComponentPeopleCodeProg(this.PNLGRPNAME, this.MARKET,
            objectval3);

        // Example: SSS_STUDENT_CENTER.LS_SS_PERS_SRCH.SearchInit
        } else if(objectid3.equals(PSDefn.RECORD)
              && objectid4.equals(PSDefn.EVENT)) {
          prog = new ComponentPeopleCodeProg(this.PNLGRPNAME, this.MARKET,
            objectval3, objectval4);

        // Example: SSS_STUDENT_CENTER.LS_DERIVED_SSS_SCL.SS_CLS_SCHED_LINK.FieldChange
        } else if(objectid3.equals(PSDefn.RECORD)
              && objectid4.equals(PSDefn.FIELD)
              && objectid5.equals(PSDefn.EVENT)) {
          prog = new ComponentPeopleCodeProg(this.PNLGRPNAME, this.MARKET,
            objectval3, objectval4, objectval5);

        } else {
          throw new EntVMachRuntimeException("Unexpected type of "
              + "Component PC encountered.");
        }

        prog = DefnCache.getProgram(prog);
        this.orderedComponentProgs.add((ComponentPeopleCodeProg)prog);
      }
    } catch(java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if(rs != null) { rs.close(); }
        if(pstmt != null) { pstmt.close(); }
      } catch(java.sql.SQLException sqle) {}
    }
  }

  public void loadAndRunRecordPConSearchRecord() {

    Record recDefn = DefnCache.getRecord(this.searchRecordToUse);
    ComponentBuffer.searchRecord = PTRecord.getSentinel().alloc(recDefn);

    /*
     * Record PC should be loaded at this time only if the search record
     * contains one or more keys.
     */
    if(!recDefn.hasAnyKeys()) {
      log.debug("No keys on search record.");
      return;
    }

    recDefn.discoverRecordPC();
    for(PeopleCodeProg prog : recDefn.orderedRecordProgs) {
      if(prog.event.equals("SearchInit")) {
        PeopleCodeProg p = DefnCache.getProgram(prog);
        ExecContext eCtx = new ProgramExecContext(p);
        InterpretSupervisor interpreter = new InterpretSupervisor(eCtx);
        interpreter.run();
      }
    }
  }

  public void loadAndRunComponentPConSearchRecord() {

    for(ComponentPeopleCodeProg prog : this.orderedComponentProgs) {
      if(prog.RECNAME != null && prog.RECNAME.equals(this.searchRecordToUse)) {
        PeopleCodeProg p = DefnCache.getProgram(prog);
        ExecContext eCtx = new ProgramExecContext(p);
        InterpretSupervisor interpreter = new InterpretSupervisor(eCtx);
        interpreter.run();
      }
    }
  }

  public void fillSearchRecord() {

    Record recDefn = DefnCache.getRecord(this.searchRecordToUse);

    /*
     * Only fill the search record at this time if the record
     * contains at least one key.
     */
    if(!recDefn.hasAnyKeys()) {
      return;
    }

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      pstmt = StmtLibrary.getSearchRecordFillQuery();
      rs = pstmt.executeQuery();

      ResultSetMetaData rsMetadata = rs.getMetaData();
      int numCols = rsMetadata.getColumnCount();

      if(rs.next()) { // search record may be empty
        PTRecord searchRecord = ComponentBuffer.searchRecord;
        for(int i = 1; i <= numCols; i++) {
          String colName = rsMetadata.getColumnName(i);
          String colTypeName = rsMetadata.getColumnTypeName(i);
          PTField fldObj = searchRecord.getField(colName);
          GlobalFnLibrary.readFieldFromResultSet(fldObj,
            colName, colTypeName, rs);
        }
        if(rs.next()) {
          throw new EntVMachRuntimeException(
            "Result set for search record fill has more than " +
            "one record.");
        }
      }
    } catch(java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if(rs != null) { rs.close(); }
        if(pstmt != null) { pstmt.close(); }
      } catch(java.sql.SQLException sqle) {}
    }
  }

  public void loadPages() {
    for(Page p : this.pages) {
      p.recursivelyLoadSubpages();
    }

    RecordPCListRequestBuffer.init();
    for(Page p : this.pages) {
      p.recursivelyLoadSecpages();
    }

    RecordPCListRequestBuffer.flushEntireTokenStream();
  }

  public void assembleComponentStructure() {

    PgToken tok;
    PgTokenStream pfs;

    final byte REL_DISP_FLAG = (byte) 16;

    for(Page p : this.pages) {
      pfs = new PgTokenStream(p.PNLNAME);

      Stack<ScrollMarker> scrollMarkers = new Stack<ScrollMarker>();
      scrollMarkers.push(new ScrollMarker(0, null, PFlag.PAGE));

      while((tok = pfs.next()) != null) {

        //log.debug(tok);

        if(tok.flags.contains(PFlag.PAGE)) {
          ScrollMarker sm = new ScrollMarker();
          sm.src = PFlag.PAGE;
          sm.primaryRecName = scrollMarkers.peek().primaryRecName;
          sm.scrollLevel = scrollMarkers.peek().scrollLevel;
          scrollMarkers.push(sm);
          continue;
        }

        if(tok.flags.contains(PFlag.END_OF_PAGE)) {
          while(scrollMarkers.peek().src == PFlag.SCROLL_START) {
            scrollMarkers.pop(); // pop interim scroll levels.
          }
          scrollMarkers.pop();    // pop the matching page.
          continue;
        }

        if(tok.flags.contains(PFlag.SCROLL_START)) {

          // This scroll may appear right after an unended scroll;
          // if so, pop the previous one.
          ScrollMarker topSm = scrollMarkers.peek();
          if(topSm.src == PFlag.SCROLL_START &&
            !tok.primaryRecName.equals(topSm.primaryRecName)) {
            scrollMarkers.pop();
          }

          ScrollMarker sm = new ScrollMarker();
          sm.src = PFlag.SCROLL_START;
          sm.primaryRecName = tok.primaryRecName;
          sm.scrollLevel = scrollMarkers.peek().scrollLevel + tok.OCCURSLEVEL;
          scrollMarkers.push(sm);
          continue;
        }

        // Remember: don't "continue" here, since SCROLL_LVL_DECREMENT
        // can be attached to regular fields.
        if(tok.flags.contains(PFlag.SCROLL_LVL_DECREMENT)) {
          scrollMarkers.pop();
        }

        if(tok.doesBelongInComponentStructure()) {
          ComponentBuffer.addPageField(tok, scrollMarkers.peek().scrollLevel,
            scrollMarkers.peek().primaryRecName);
        }
      }

      if(scrollMarkers.size() != 0) {
        throw new EntVMachRuntimeException("Scroll marker stack size exceeds 0 " +
          "at the end of the page token stream.");
      }
    }
  }
}