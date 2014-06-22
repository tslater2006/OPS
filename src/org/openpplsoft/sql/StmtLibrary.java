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

      // Don't forget to save last statement defn.
      defn.sql = b.toString().replaceAll("\\n", "");
      log.debug("Saving defn; sql is: {}", defn.sql);
      staticSqlDefns.put(defn.uniqueLabel, defn);

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

  public static OPSStmt getSearchRecordFillQuery() {

    PTRecord searchRec = ComponentBuffer.getSearchRecord();
    Record recDefn = searchRec.recDefn;
    List<RecordField> rfList = recDefn.getExpandedFieldList();
    List<String> bindVals = new ArrayList<String>();

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

    return new OPSStmt(queryStr, bindVals.toArray(
        new String[bindVals.size()]), OPSStmt.EmissionType.ENFORCED);
  }

  public static OPSStmt prepareFillStmt(Record recDefn, String whereStr, String[] bindVals) {

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

    return new OPSStmt(query.toString(), bindVals,
        OPSStmt.EmissionType.ENFORCED);
  }

  public static OPSStmt prepareSelectByKeyEffDtStmt(
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

    return new OPSStmt(query.toString(), bindVals.toArray(
        new String[bindVals.size()]), OPSStmt.EmissionType.ENFORCED);
  }

  public static OPSStmt prepareSelectByKey(
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

    return new OPSStmt(query.toString(), bindVals.toArray(
        new String[bindVals.size()]), OPSStmt.EmissionType.ENFORCED);
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

  public static OPSStmt prepareFirstPassFillQuery(
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

    return new OPSStmt(query.toString(), bindVals.toArray(
        new String[bindVals.size()]), OPSStmt.EmissionType.ENFORCED);
  }

  public static void disconnect() {
    try {
      conn.close();
    } catch(java.sql.SQLException sqle) {
      log.warn("Unable to close connection to database.", sqle);
    }
  }
}
