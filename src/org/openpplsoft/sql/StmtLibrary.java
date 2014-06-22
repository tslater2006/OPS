/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.sql;

import java.io.BufferedReader;
import java.io.FileReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.types.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.buffers.*;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class StmtLibrary {

  private static Connection conn;
  private static Logger log =
      LogManager.getLogger(StmtLibrary.class.getName());

  private static Map<String, StaticSqlDefn> staticSqlDefns;
  private static Pattern bindIdxPattern;
  private static Pattern dateInPattern;

  private static class StaticSqlDefn {
    private String uniqueLabel, sql;
    private OPSStmt.EmissionType emissionType;
  }

  static {
    ClassPathXmlApplicationContext ctx =
        new ClassPathXmlApplicationContext(System.getProperty("contextFile"));
    DataSource ds = (DataSource) ctx.getBean("dataSource");

    try {
      conn = ds.getConnection();
    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.UNABLE_TO_ACQUIRE_DB_CONN.getCode());
    }

    /*
     * Load static SQL defns into memory from file.
     */
    Resource sqlDefnRsrc = (ClassPathResource) ctx.getBean("staticSqlDefnsResource");
    staticSqlDefns = new HashMap<String, StaticSqlDefn>();

    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(sqlDefnRsrc.getFile()));
      StringBuilder b = new StringBuilder();
      StaticSqlDefn defn = null;
      String line = "";
      while((line = br.readLine()) != null) {
        if (line.startsWith("=!=")) {
          continue;
        } else if (line.startsWith("###[label]:")) {
          if (defn != null) {
            // All newlines must be removed, otherwise tracefile verificatin
            // will fail.
            defn.sql = b.toString().replaceAll("\\n", "");
            log.debug("Saving defn; sql is: {}", defn.sql);
            staticSqlDefns.put(defn.uniqueLabel, defn);
          }
          defn = new StaticSqlDefn();
          b = new StringBuilder();
          defn.uniqueLabel = line.split(":")[1].trim();
          log.debug("Label: {}", defn.uniqueLabel);
        } else if (line.startsWith("#[enforced]:")) {
          String isEnforced = line.split(":")[1].trim();
          if(isEnforced.equals("true")) {
            defn.emissionType = OPSStmt.EmissionType.ENFORCED;
          } else {
            defn.emissionType = OPSStmt.EmissionType.UNENFORCED;
          }
          log.debug("Enforced? {}", defn.emissionType);
        } else {
          b.append(line);
        }
      }

      // Save last statement defn.
      defn.sql = b.toString().replaceAll("\\n", "");
      log.debug("Saving defn; sql is: {}", defn.sql);
      staticSqlDefns.put(defn.uniqueLabel, defn);

      br.close();
    } catch (final java.io.IOException ioe) {
      log.fatal(ioe.getMessage(), ioe);
      System.exit(ExitCode.FAILED_READ_FROM_STATIC_SQL_DEFN_FILE.getCode());
    } finally {
      try {
        if(br != null) { br.close(); }
      } catch (final java.io.IOException ioe) {
        log.warn("Unable to close br in finally block.");
      }
    }

    // compile meta-SQL detection regex patterns.
    bindIdxPattern = Pattern.compile(":\\d+");
    dateInPattern = Pattern.compile("%DATEIN\\((.+?)\\)");
  }

  public static Connection getConnection() {
    return conn;
  }

  public static OPSStmt getStaticSQLStmt(final String uniqueLabel,
      final String[] bindVals) {
    return new OPSStmt(staticSqlDefns.get(uniqueLabel).sql,
        bindVals, staticSqlDefns.get(uniqueLabel).emissionType);
  }

  public static PreparedStatement getPSPCMPROG_CompPCList(String b1, String b2, String b3, String b4) {
    OPSStmt stmt = new OPSStmt("SELECT OBJECTID1,OBJECTVALUE1, OBJECTID2,OBJECTVALUE2, OBJECTID3,OBJECTVALUE3, OBJECTID4,OBJECTVALUE4, OBJECTID5,OBJECTVALUE5, OBJECTID6,OBJECTVALUE6, OBJECTID7,OBJECTVALUE7  FROM PSPCMPROG WHERE OBJECTID1 = ? AND OBJECTVALUE1 = ? AND  OBJECTID2 = ? AND OBJECTVALUE2 = ? ORDER BY OBJECTID1,OBJECTVALUE1, OBJECTID2,OBJECTVALUE2, OBJECTID3,OBJECTVALUE3, OBJECTID4,OBJECTVALUE4, OBJECTID5,OBJECTVALUE5, OBJECTID6,OBJECTVALUE6, OBJECTID7,OBJECTVALUE7");
    stmt.bindVals.put(1, b1);
    stmt.bindVals.put(2, b2);
    stmt.bindVals.put(3, b3);
    stmt.bindVals.put(4, b4);
    return stmt.generateUnenforcedPreparedStmt(conn);
  }

  public static PreparedStatement getPSPCMPROG_RecordPCList(String b1, String b2) {
    OPSStmt stmt = new OPSStmt("SELECT OBJECTID1,OBJECTVALUE1, OBJECTID2,OBJECTVALUE2, OBJECTID3,OBJECTVALUE3, OBJECTID4,OBJECTVALUE4, OBJECTID5,OBJECTVALUE5, OBJECTID6,OBJECTVALUE6, OBJECTID7,OBJECTVALUE7  FROM PSPCMPROG WHERE OBJECTID1 = ? AND OBJECTVALUE1 = ? ORDER BY OBJECTID1,OBJECTVALUE1, OBJECTID2,OBJECTVALUE2, OBJECTID3,OBJECTVALUE3, OBJECTID4,OBJECTVALUE4, OBJECTID5,OBJECTVALUE5, OBJECTID6,OBJECTVALUE6, OBJECTID7,OBJECTVALUE7");
    stmt.bindVals.put(1, b1);
    stmt.bindVals.put(2, b2);
    return stmt.generateUnenforcedPreparedStmt(conn);
  }

  public static PreparedStatement getPSPCMPROG_GetPROGTXT(String b1, String b2, String b3, String b4, String b5, String b6, String b7, String b8, String b9, String b10, String b11, String b12, String b13, String b14) {
    OPSStmt stmt = new OPSStmt("SELECT VERSION, PROGRUNLOC, NAMECOUNT, PROGLEN, PROGTXT, LICENSE_CODE, TO_CHAR(CAST((LASTUPDDTTM) AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF'), LASTUPDOPRID, PROGFLAGS, PROGEXTENDS, PROGSEQ FROM PSPCMPROG WHERE  OBJECTID1 = ? AND OBJECTVALUE1 = ? AND  OBJECTID2 = ? AND OBJECTVALUE2 = ? AND  OBJECTID3 = ? AND OBJECTVALUE3 = ? AND  OBJECTID4 = ? AND OBJECTVALUE4 = ? AND  OBJECTID5 = ? AND OBJECTVALUE5 = ? AND  OBJECTID6 = ? AND OBJECTVALUE6 = ? AND  OBJECTID7 = ? AND OBJECTVALUE7 = ? ORDER BY PROGSEQ");
    stmt.bindVals.put(1, b1);
    stmt.bindVals.put(2, b2);
    stmt.bindVals.put(3, b3);
    stmt.bindVals.put(4, b4);
    stmt.bindVals.put(5, b5);
    stmt.bindVals.put(6, b6);
    stmt.bindVals.put(7, b7);
    stmt.bindVals.put(8, b8);
    stmt.bindVals.put(9, b9);
    stmt.bindVals.put(10, b10);
    stmt.bindVals.put(11, b11);
    stmt.bindVals.put(12, b12);
    stmt.bindVals.put(13, b13);
    stmt.bindVals.put(14, b14);
    return stmt.generateUnenforcedPreparedStmt(conn);
  }

  public static PreparedStatement getPSPCMPROG_GetRefs(String b1, String b2, String b3, String b4, String b5, String b6, String b7, String b8, String b9, String b10, String b11, String b12, String b13, String b14) {
    OPSStmt stmt = new OPSStmt("SELECT RECNAME, REFNAME, PACKAGEROOT, QUALIFYPATH, NAMENUM FROM PSPCMNAME WHERE  OBJECTID1 = ? AND OBJECTVALUE1 = ? AND  OBJECTID2 = ? AND OBJECTVALUE2 = ? AND  OBJECTID3 = ? AND OBJECTVALUE3 = ? AND  OBJECTID4 = ? AND OBJECTVALUE4 = ? AND  OBJECTID5 = ? AND OBJECTVALUE5 = ? AND  OBJECTID6 = ? AND OBJECTVALUE6 = ? AND  OBJECTID7 = ? AND OBJECTVALUE7 = ? ORDER BY NAMENUM");
    stmt.bindVals.put(1, b1);
    stmt.bindVals.put(2, b2);
    stmt.bindVals.put(3, b3);
    stmt.bindVals.put(4, b4);
    stmt.bindVals.put(5, b5);
    stmt.bindVals.put(6, b6);
    stmt.bindVals.put(7, b7);
    stmt.bindVals.put(8, b8);
    stmt.bindVals.put(9, b9);
    stmt.bindVals.put(10, b10);
    stmt.bindVals.put(11, b11);
    stmt.bindVals.put(12, b12);
    stmt.bindVals.put(13, b13);
    stmt.bindVals.put(14, b14);
    return stmt.generateUnenforcedPreparedStmt(conn);
  }

  public static PreparedStatement getPSPNLDEFN(String b1) {
    OPSStmt stmt = new OPSStmt("SELECT VERSION, PNLTYPE, GRIDHORZ, GRIDVERT, FIELDCOUNT, MAXPNLFLDID, HELPCONTEXTNUM, PANELLEFT, PANELTOP, PANELRIGHT, PANELBOTTOM, PNLSTYLE, STYLESHEETNAME, PNLUSE, DEFERPROC, DESCR, POPUPMENU, LICENSE_CODE, TO_CHAR(CAST((LASTUPDDTTM) AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF'), LASTUPDOPRID, OBJECTOWNERID, DESCRLONG FROM PSPNLDEFN WHERE PNLNAME = ?");
    stmt.bindVals.put(1, b1);
    return stmt.generateEnforcedPreparedStmt(conn);
  }

  public static PreparedStatement getPSPNLFIELD(String b1) {
    OPSStmt stmt = new OPSStmt("SELECT PNLFLDID, FIELDTYPE, EDITSIZE, FIELDLEFT, FIELDTOP, FIELDRIGHT, FIELDBOTTOM, EDITLBLLEFT, EDITLBLTOP, EDITLBLRIGHT, EDITLBLBOTTOM, DSPLFORMAT, DSPLFILL, LBLTYPE, LBLLOC, LBLPADSIZE, LABEL_ID, LBLTEXT, FIELDUSE, FIELDUSETMP, DEFERPROC, OCCURSLEVEL, OCCURSCOUNT1, OCCURSCOUNT2, OCCURSCOUNT3, OCCURSOFFSET1, OCCURSOFFSET2, OCCURSOFFSET3, PNLFIELDNAME, RECNAME, FIELDNAME, SUBPNLNAME, ONVALUE, OFFVALUE, ASSOCFIELDNUM, FIELDSTYLE, LABELSTYLE, FIELDSIZETYPE, LABELSIZETYPE, PRCSNAME, PRCSTYPE, FORMATFAMILY, DISPFMTNAME, PROMPTFIELD, POPUPMENU, TREECTRLID, TREECTRLTYPE, MULTIRECTREE, NODECOUNT, GRDCOLUMNCOUNT, GRDSHOWCOLHDG, GRDSHOWROWHDG, GRDODDROWSTYLE, GRDEVENROWSTYLE, GRDACTIVETABSTYLE, GRDINACTIVETABSTYL, GRDNAVBARSTYLE, GRDLABELSTYLE, GRDLBLMSGSET, GRDLBLMSGNUM, GRDLBLALIGN, GRDACTTYPE, TABENABLE, PBDISPLAYTYPE, OPENNEWWINDOW, URLDYNAMIC,  URL_ID, GOTOPORTALNAME, GOTONODENAME, GOTOMENUNAME, GOTOPNLGRPNAME,  GOTOMKTNAME, GOTOPNLNAME, GOTOPNLACTION, SRCHBYPNLDATA, SCROLLACTION, TOOLACTION, CONTNAME, CONTNAMEOVER, CONTNAMEDISABLE, PTLBLIMGCOLLAPSE, PTLBLIMGEXPAND, SELINDICATORTYPE, PTADJHIDDENFIELDS, PTCOLLAPSEDATAAREA, PTDFLTVIEWEXPANDED, PTHIDEFIELDS, SHOWCOLHIDEROWS, PTLEBEXPANDFIELD, SHOWTABCNTLBTN, SECUREINVISIBLE, ENABLEASANCHOR, URLENCODEDBYAPP, USEDEFAULTLABEL, GRDALLOWCOLSORT FROM PSPNLFIELD WHERE PNLNAME = ? ORDER BY FIELDNUM");
    stmt.bindVals.put(1, b1);
    return stmt.generateEnforcedPreparedStmt(conn);
  }

  public static PreparedStatement getSearchRecordFillQuery() {

    PTRecord searchRec = ComponentBuffer.getSearchRecord();
    Record recDefn = searchRec.recDefn;
    List<RecordField> rfList = recDefn.getExpandedFieldList();
    ArrayList<String> bindVals = new ArrayList<String>();

    /* NOTE: "SELECT" is prepended below due to PS oddities. */
    StringBuilder query = new StringBuilder();
    boolean distinctKeywordUsed = false;

    int i = 0;
    for(RecordField rf : rfList) {
      log.debug("USEEDIT for {}: {}", rf.FIELDNAME, rf.USEEDIT);
      if(rf.isSearchKey()) {
        if(i > 0) { query.append(", "); }
        if(rf.isListBoxItem() && rf.FIELDNAME.equals("EMPLID")) {
          query.append("DISTINCT ");
          distinctKeywordUsed = true;
        }
        query.append(rf.FIELDNAME);
        i++;
      }
    }

    query.append(" FROM PS_").append(recDefn.RECNAME).append(" WHERE ");

    i = 0;
    for(RecordField rf : rfList) {
      if(rf.isKey() || rf.isSearchKey()) {
        if(i > 0) { query.append(" AND "); }

        String val = (String)searchRec.getField(rf.FIELDNAME)
            .getValue().read();

        /*
         * If this is the OPRID field and it has a null value
         * in the search record, default it to the system var value.
         * TODO: If multiple fields require this kind of defaulting,
         * abstract this into the underlying RecordField object.
         */
        if(val == null && rf.FIELDNAME.equals("OPRID")) {
          val = (String)Environment.getSystemVar("%OperatorId").read();
        }

        query.append(rf.FIELDNAME);
        if(rf.isListBoxItem() && rf.FIELDNAME.equals("EMPLID")) {
          query.append(" LIKE '").append(val).append("%'");
        } else {
          query.append("=?");
          bindVals.add(val);
        }

        i++;
      }
    }

    query.append(" ORDER BY ");

    i = 0;
    for(RecordField rf : rfList) {
      if(rf.isSearchKey()) {
        if(i > 0) { query.append(", "); }
        query.append(rf.FIELDNAME);
        if(rf.isDescendingKey()) {
          query.append(" DESC");
        }
        i++;
      }
    }

    String queryStr;
    if(distinctKeywordUsed) {
      queryStr = "SELECT " + query.toString();
    } else {
      queryStr = "SELECT  " + query.toString();
    }

    OPSStmt stmt = new OPSStmt(queryStr);
    for(i = 0; i < bindVals.size(); i++) {
      stmt.bindVals.put(i+1, bindVals.get(i));
    }

    return stmt.generateEnforcedPreparedStmt(conn);
  }

  public static PreparedStatement prepareFillStmt(Record recDefn, String whereStr, String[] bindVals) {

    StringBuilder query = new StringBuilder(
      generateSelectClause(recDefn, "FILL"));

      // Replace numeric bind sockets (":1") with "?".
      Matcher bindIdxMatcher = bindIdxPattern.matcher(whereStr);
      whereStr = bindIdxMatcher.replaceAll("?");

      // Replace occurrences of %DATEIN(*) with TO_DATE(*,'YYYY-MM-DD')
      Matcher dateInMatcher = dateInPattern.matcher(whereStr);
      while(dateInMatcher.find()) {
        //log.debug("Found DATEIN: " + dateInMatcher.group(0));
        whereStr = dateInMatcher.replaceAll("TO_DATE("+
            dateInMatcher.group(1)+",'YYYY-MM-DD')");
        }

        query.append("  ").append(whereStr);
        //log.debug("Fill query string: {}", query.toString());

    /*
     * Prepare the statement.
     */
    OPSStmt stmt = new OPSStmt(query.toString());
    for(int i = 0; i < bindVals.length; i++) {
      stmt.bindVals.put(i+1, bindVals[i]);
    }

    return stmt.generateEnforcedPreparedStmt(conn);
  }

  public static PreparedStatement prepareSelectByKeyEffDtStmt(
      Record recDefn, PTRecord recObj, PTDate effDt) {

    String tableAlias = "A";
    StringBuilder query = new StringBuilder(
        generateSelectClause(recDefn, tableAlias));

    query.append(" WHERE ");

    List<RecordField> rfList = recDefn.getExpandedFieldList();
    List<String> bindVals = new ArrayList<String>();

    boolean isFirstKey = true;
    for(RecordField rf : rfList) {
      if(rf.isKey()) {
        if(!isFirstKey) { query.append(" AND "); }
        isFirstKey = false;

        query.append(tableAlias).append(".")
            .append(rf.FIELDNAME).append("=");

        if(!rf.FIELDNAME.equals("EFFDT")) {
          query.append("?");
          bindVals.add((String)recObj.fields.get(rf.FIELDNAME)
              .getValue().read());
        } else {
          /*
           * Insert subquery for EFFDT field.
           */
          query.append("(")
             .append("SELECT MAX(EFFDT) FROM PS_")
             .append(recDefn.RECNAME)
             .append(" B WHERE ");

          boolean isFirstKeyOnSub = true;
          for(RecordField subRf : rfList) {
            if(subRf.isKey()) {
              if(!isFirstKeyOnSub) { query.append(" AND "); }
              if(!subRf.FIELDNAME.equals("EFFDT")) {
                query.append("B.").append(subRf.FIELDNAME)
                   .append("=").append(tableAlias)
                   .append(".").append(subRf.FIELDNAME);
              } else {
                query.append("B.EFFDT<=TO_DATE(")
                   .append("?,'YYYY-MM-DD')");
                bindVals.add(effDt.read());
              }
              isFirstKeyOnSub = false;
            }
          }
          query.append(")");
        }
      }
    }

    OPSStmt stmt = new OPSStmt(query.toString());
    for(int i = 0; i < bindVals.size(); i++) {
      stmt.bindVals.put(i+1, bindVals.get(i));
    }

    return stmt.generateEnforcedPreparedStmt(conn);
  }

  public static PreparedStatement prepareSelectByKey(
    Record recDefn, PTRecord recObj) {

    String tableAlias = "";
    StringBuilder query = new StringBuilder(
        generateSelectClause(recDefn, tableAlias));
    query.append("WHERE ");

    List<RecordField> rfList = recDefn.getExpandedFieldList();
    List<String> bindVals = new ArrayList<String>();

    boolean isFirstKey = true;
    for(RecordField rf : rfList) {
      if(rf.isKey()) {
        if(!isFirstKey) { query.append(" AND "); }
        isFirstKey = false;

        query.append(rf.FIELDNAME).append("=?");
        bindVals.add((String)recObj.fields.get(rf.FIELDNAME)
          .getValue().read());
      }
    }

    OPSStmt stmt = new OPSStmt(query.toString());
    for(int i = 0; i < bindVals.size(); i++) {
      stmt.bindVals.put(i+1, bindVals.get(i));
    }

    return stmt.generateEnforcedPreparedStmt(conn);
  }

  private static String generateSelectClause(Record recDefn,
      String tableAlias) {

    String dottedAlias = tableAlias;
    if(dottedAlias.length() > 0) {
      dottedAlias = dottedAlias.concat(".");
    }

    StringBuilder selectClause = new StringBuilder("SELECT ");
    List<RecordField> rfList = recDefn.getExpandedFieldList();

    for(int i = 0; i < rfList.size(); i++) {
      if(i > 0) { selectClause.append(","); }
      String fieldname = rfList.get(i).FIELDNAME;

      // Selected date fields must be wrapped with TO_CHAR directive.
      if(rfList.get(i).getSentinelForUnderlyingValue()
          instanceof PTDate) {
        selectClause.append("TO_CHAR(").append(dottedAlias)
            .append(fieldname).append(",'YYYY-MM-DD')");
      } else {
        selectClause.append(dottedAlias).append(fieldname);
      }
    }
    selectClause.append(" FROM PS_").append(recDefn.RECNAME)
        .append(" ").append(tableAlias);

    return selectClause.toString();
  }

  public static PreparedStatement prepareFirstPassFillQuery(
    RecordBuffer rbuf) {

    /*
     * Iterate over the fields in the expanded record field list
     * and select each of those in the statment. For each field that is
     * a key, add it to the WHERE clause and get its value from the
     * scroll buffer chain.
     */
    Record recDefn = DefnCache.getRecord(rbuf.getRecName());
    List<RecordField> rfList = recDefn.getExpandedFieldList();

    /*
     * Ensure all keys have an associated value in the scroll
     * buffer hierarchy. If any key does not, do not continue.
     */
    for(RecordField rf : rfList) {
      if(rf.isSearchKey()
          && rbuf.getParentScrollBuffer()
            .getKeyValueFromHierarchy(rf.FIELDNAME) == null) {
        log.debug("Aborting first pass fill for Record.{}; " +
          "value does not exist for search key: {}", rbuf.getRecName(), rf.FIELDNAME);
        return null;
      }
    }

    /*
     * Begin building fill query.
     */
    StringBuilder query = new StringBuilder("SELECT ");
    ArrayList<String> bindVals = new ArrayList<String>();

    for(int i = 0; i < rfList.size(); i++) {
      if(i > 0) { query.append(", "); }
      String fieldname = rfList.get(i).FIELDNAME;
      PTType val = rfList.get(i)
          .getSentinelForUnderlyingValue();

      if(val instanceof PTDate) {
        query.append("TO_CHAR(").append(fieldname)
          .append(",'YYYY-MM-DD')");
      } else if(val instanceof PTDateTime) {
        query.append("TO_CHAR(CAST((").append(fieldname)
          .append(") AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF')");
      } else {
        query.append(fieldname);
      }
    }

    query.append(" FROM PS_").append(rbuf.getRecName());

    int i = 0;
    for(RecordField rf : rfList) {
      if(rf.isSearchKey()) {
        if(i == 0) { query.append(" WHERE "); }
        if(i > 0) { query.append(" AND "); }
        String val = (String)rbuf.getParentScrollBuffer().getKeyValueFromHierarchy(
            rf.FIELDNAME).read();
        query.append(rf.FIELDNAME).append("=?");
        bindVals.add(val);
        i++;
      }
    }

    i = 0;
    for(RecordField rf : rfList) {
      if(rf.isSearchKey()) {
        if(i == 0) { query.append(" ORDER BY "); }
        if(i > 0) { query.append(", "); }
        query.append(rf.FIELDNAME);
        if(rf.isDescendingKey()) {
          query.append(" DESC");
        }
        i++;
      }
    }

    OPSStmt stmt = new OPSStmt(query.toString());
    for(i = 0; i < bindVals.size(); i++) {
      stmt.bindVals.put(i+1, bindVals.get(i));
    }

    return stmt.generateEnforcedPreparedStmt(conn);
  }

  public static void disconnect() {
    try {
      conn.close();
    } catch(java.sql.SQLException sqle) {
      log.warn("Unable to close connection to database.", sqle);
    }
  }
}
