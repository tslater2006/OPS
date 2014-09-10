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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.types.*;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Central location for retrieval of static SQL statements,
 * in addition to creation of dynamic SQL statements.
 */
public final class StmtLibrary {

  private static Connection conn;
  private static Logger log =
      LogManager.getLogger(StmtLibrary.class.getName());

  private static Map<String, StaticSqlDefn> staticSqlDefns;
  private static Pattern bindIdxPattern;
  private static Pattern dateInPattern;
  private static Pattern effDtCheckPattern;

  static {
    final ClassPathXmlApplicationContext ctx =
        new ClassPathXmlApplicationContext(System.getProperty("contextFile"));
    final DataSource ds = (DataSource) ctx.getBean("dataSource");

    try {
      conn = ds.getConnection();
    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.UNABLE_TO_ACQUIRE_DB_CONN.getCode());
    }

    /*
     * Load static SQL defns into memory from file.
     */
    final Resource sqlDefnRsrc =
        (ClassPathResource) ctx.getBean("staticSqlDefnsResource");
    staticSqlDefns = new HashMap<String, StaticSqlDefn>();

    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(sqlDefnRsrc.getFile()));
      StringBuilder b = new StringBuilder();
      StaticSqlDefn defn = null;
      String line = "";
      while ((line = br.readLine()) != null) {
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
          final String isEnforced = line.split(":")[1].trim();
          if (isEnforced.equals("true")) {
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
        if (br != null) { br.close(); }
      } catch (final java.io.IOException ioe) {
        log.warn("Unable to close br in finally block.");
      }
    }

    // compile meta-SQL detection regex patterns.
    bindIdxPattern = Pattern.compile(":\\d+");
    dateInPattern = Pattern.compile("%(DATEIN|DateIn)\\((.+?)\\)");
    effDtCheckPattern = Pattern.compile(
      "%EffDtCheck\\(([A-Za-z_]+)\\s+([A-Za-z0-9_]+),"
        + "\\s+([A-Za-z]+),\\s+(%[A-Za-z]+\\(\\?\\))\\)");
  }

  private StmtLibrary() {}

  /**
   * Retrieves the underlying JDBC connection.
   * @return the underlying JDBC connection
   */
  public static Connection getConnection() {
    return conn;
  }

  /**
   * Generates an OPSStmt for the static SQL stmt with the provided
   * label, along with the provided bind values.
   * @param uniqueLabel the label for the desired statement in the static
   *   SQL defn file
   * @param bindVals the values to bind to the statement parameters
   * @return the OPSStmt to be executed
   */
  public static OPSStmt getStaticSQLStmt(final String uniqueLabel,
      final String[] bindVals) {
    return new OPSStmt(staticSqlDefns.get(uniqueLabel).sql,
        bindVals, staticSqlDefns.get(uniqueLabel).emissionType);
  }

  /**
   * Generates an OPSStmt that will fill the search record on
   * the ComponentBuffer.
   * @return the OPSStmt to be executed
   */
  public static OPSStmt getSearchRecordFillQuery() {

    final PTRecord searchRec = ComponentBuffer.getSearchRecord();
    final Record recDefn = searchRec.getRecDefn();
    final List<RecordField> rfList = recDefn.getExpandedFieldList();
    final List<String> bindVals = new ArrayList<String>();

    /* NOTE: "SELECT" is prepended below due to PS oddities. */
    final StringBuilder query = new StringBuilder();
    boolean distinctKeywordUsed = false;

    int i = 0;
    for (RecordField rf : rfList) {
      log.debug("USEEDIT for {}: {}", rf.FIELDNAME, rf.USEEDIT);
      if (rf.isSearchKey()) {
        if (i > 0) { query.append(", "); }
        if (rf.isListBoxItem() && rf.FIELDNAME.equals("EMPLID")) {
          query.append("DISTINCT ");
          distinctKeywordUsed = true;
        }
        query.append(rf.FIELDNAME);
        i++;
      }
    }

    query.append(" FROM PS_").append(recDefn.RECNAME).append(" WHERE ");

    i = 0;
    for (RecordField rf : rfList) {
      if (rf.isKey() || rf.isSearchKey()) {
        if (i > 0) { query.append(" AND "); }

        String val = (String) searchRec.getField(rf.FIELDNAME)
            .getValue().read();

        /*
         * If this is the OPRID field and it has a null value
         * in the search record, default it to the system var value.
         * TODO(mquinn): If multiple fields require this kind of defaulting,
         * abstract this into the underlying RecordField object.
         */
        if (val == null && rf.FIELDNAME.equals("OPRID")) {
          val = (String) Environment.getSystemVar("%OperatorId").read();

          // Write the value used into the OPRID field on the search record.
          ((PTString) searchRec.getField(rf.FIELDNAME).getValue()).systemWrite(val);
        }

        query.append(rf.FIELDNAME);
        if (rf.isListBoxItem() && rf.FIELDNAME.equals("EMPLID")) {
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
    for (RecordField rf : rfList) {
      if (rf.isSearchKey()) {
        if (i > 0) { query.append(", "); }
        query.append(rf.FIELDNAME);
        if (rf.isDescendingKey()) {
          query.append(" DESC");
        }
        i++;
      }
    }

    String queryStr;
    if (distinctKeywordUsed) {
      queryStr = "SELECT " + query.toString();
    } else {
      queryStr = "SELECT  " + query.toString();
    }

    return new OPSStmt(queryStr, bindVals.toArray(
        new String[bindVals.size()]), OPSStmt.EmissionType.ENFORCED);
  }

  public static OPSStmt prepareSqlFromSQLDefn(final SQL sqlDefn,
    final String[] bindVals) {

    // Replace numeric bind sockets (":1") with "?".
    final Matcher bindIdxMatcher = bindIdxPattern.matcher(sqlDefn.getSQLText());
    final String newSql = bindIdxMatcher.replaceAll("?");

    return new OPSStmt(newSql, bindVals, OPSStmt.EmissionType.ENFORCED);
  }


  /**
   * Generates an OPSStmt that will fill the given record.
   * @param recDefn the record to fill
   * @param whereStr the WHERE clause that will be part of the stmt
   * @param bindVals the bind values to bind to the stmt params
   * @return the OPSStmt to be executed
   */
  public static OPSStmt prepareFillStmt(final Record recDefn,
      final String whereStr, final String[] bindVals) {

    final String rootAlias = "FILL";
    final StringBuilder query = new StringBuilder(
        generateSelectClause(recDefn, rootAlias));

    // Replace numeric bind sockets (":1") with "?".
    final Matcher bindIdxMatcher = bindIdxPattern.matcher(whereStr);
    String newWhereStr = bindIdxMatcher.replaceAll("?");

    // Expand any %EffDtCheck meta-SQL
    final Matcher effDtCheckMatcher = effDtCheckPattern.matcher(newWhereStr);
    while (effDtCheckMatcher.find()) {

      final String effDtCheckRecord = effDtCheckMatcher.group(1);
      final String effDtSubqueryAlias = effDtCheckMatcher.group(2);
      final String effDtRootAlias = effDtCheckMatcher.group(3);
      final String effDtBound = effDtCheckMatcher.group(4);

      if(!rootAlias.equals(effDtRootAlias)) {
        throw new OPSVMachRuntimeException("While preparing fill query, "
          + "found %EffDtCheck that has a root alias (" + effDtRootAlias
          + ") different than expected (" + rootAlias + ").");
      }

      StringBuilder effDtSubqueryBuilder = new StringBuilder(
        "SELECT MAX(EFFDT) FROM ").append(effDtCheckRecord)
        .append(" ").append(effDtSubqueryAlias).append(" WHERE");

      final Record effDtRecord = DefnCache.getRecord(effDtCheckRecord);
      for(Map.Entry<String, RecordField> cursor
        : effDtRecord.fieldTable.entrySet()) {
        final RecordField rf = cursor.getValue();
        if(rf.isKey() && !rf.FIELDNAME.equals("EFFDT")) {
          effDtSubqueryBuilder.append(" ").append(effDtSubqueryAlias)
            .append(".").append(rf.FIELDNAME).append("=")
            .append(effDtRootAlias).append(".").append(rf.FIELDNAME)
            .append(" AND");
        }
      }

      effDtSubqueryBuilder.append(" ").append(effDtSubqueryAlias)
        .append(".EFFDT<=").append(effDtBound);

      newWhereStr = effDtCheckMatcher.replaceAll(effDtRootAlias
        + ".EFFDT=(" + effDtSubqueryBuilder.toString() + ")");
    }

    // Replace occurrences of %DATEIN/DateIn with TO_DATE(*,'YYYY-MM-DD')
    final Matcher dateInMatcher = dateInPattern.matcher(newWhereStr);
    while (dateInMatcher.find()) {
      newWhereStr = dateInMatcher.replaceAll("TO_DATE("
          + dateInMatcher.group(2) + ",'YYYY-MM-DD')");
    }

    query.append("  ").append(newWhereStr);
    //log.debug("Fill query string: {}", query.toString());

    return new OPSStmt(query.toString(), bindVals,
        OPSStmt.EmissionType.ENFORCED);
  }

  /**
   * Generates an OPSStmt that will select from the record by key
   * and consider the EFFDT as well.
   * @param recDefn the record defn to be selected from
   * @param recObj the record obj to be selected from
   * @param effDt the effective date to be considered
   * @return the OPSStmt to be executed
   */
  public static OPSStmt prepareSelectByKeyEffDtStmt(
      final Record recDefn, final PTRecord recObj, final PTDate effDt) {

    final String tableAlias = "A";
    final StringBuilder query = new StringBuilder(
        generateSelectClause(recDefn, tableAlias));

    query.append(" WHERE ");

    final List<RecordField> rfList = recDefn.getExpandedFieldList();
    final List<String> bindVals = new ArrayList<String>();

    boolean isFirstKey = true;
    for (RecordField rf : rfList) {
      if (rf.isKey()) {
        if (!isFirstKey) { query.append(" AND "); }
        isFirstKey = false;

        query.append(tableAlias).append(".")
            .append(rf.FIELDNAME).append("=");

        if (!rf.FIELDNAME.equals("EFFDT")) {
          query.append("?");
          bindVals.add((String) recObj.getFields().get(rf.FIELDNAME)
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
          for (RecordField subRf : rfList) {
            if (subRf.isKey()) {
              if (!isFirstKeyOnSub) { query.append(" AND "); }
              if (!subRf.FIELDNAME.equals("EFFDT")) {
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

  /**
   * Generates an OPSStmt that will select from the given record
   * using the record's key(s).
   * @param recDefn the record defn to select from
   * @param recObj the record obj to select from
   * @return the OPSStmt to be executed
   */
  public static OPSStmt prepareSelectByKey(
      final Record recDefn, final PTRecord recObj) {

    final String tableAlias = "";
    final StringBuilder query = new StringBuilder(
        generateSelectClause(recDefn, tableAlias));

    final List<RecordField> rfList = recDefn.getExpandedFieldList();
    final List<String> bindVals = new ArrayList<String>();

    boolean isFirstKey = true;
    for (RecordField rf : rfList) {
      if (rf.isKey()) {

        if (isFirstKey) {
          query.append("WHERE ");
          isFirstKey = false;
        } else {
          query.append(" AND ");
        }

        query.append(rf.FIELDNAME).append("=?");
        bindVals.add((String) recObj.getFields().get(rf.FIELDNAME)
          .getValue().read());
      }
    }

    return new OPSStmt(query.toString(), bindVals.toArray(
        new String[bindVals.size()]), OPSStmt.EmissionType.ENFORCED);
  }

  /**
   * Generates the SELECT clause given a record defn.
   * @param recDefn the record definition to select from
   * @param tableAlias the alias for the record (if none, use empty str)
   * @return the SELECT clause in string form
   */
  private static String generateSelectClause(final Record recDefn,
      final String tableAlias) {

    String dottedAlias = tableAlias;
    if (dottedAlias.length() > 0) {
      dottedAlias = dottedAlias.concat(".");
    }

    final StringBuilder selectClause = new StringBuilder("SELECT ");
    final List<RecordField> rfList = recDefn.getExpandedFieldList();

    for (int i = 0; i < rfList.size(); i++) {
      if (i > 0) { selectClause.append(","); }
      final String fieldname = rfList.get(i).FIELDNAME;

      /**
       * For certain field types, wrap field names in
       * appropriate function calls.
       */
      if (rfList.get(i).getTypeConstraintForUnderlyingValue()
          .isUnderlyingClassEqualTo(PTDate.class)) {
        selectClause.append("TO_CHAR(").append(dottedAlias)
            .append(fieldname).append(",'YYYY-MM-DD')");

      } else if (rfList.get(i).getTypeConstraintForUnderlyingValue()
          .isUnderlyingClassEqualTo(PTDateTime.class)) {
        selectClause.append("TO_CHAR(CAST((").append(dottedAlias)
            .append(fieldname)
            .append(") AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF')");

      } else {
        selectClause.append(dottedAlias).append(fieldname);
      }
    }
    selectClause.append(" FROM ").append(recDefn
        .getFullDatabaseRecordName()).append(" ").append(tableAlias);

    return selectClause.toString();
  }

  /**
   * Generates an OPSStmt that will fill data into the provided
   * RecordBuffer during the first pass fill phase.
   * @param rbuf the RecordBuffer to fill
   * @return the OPSStmt to be executed
   */
  public static OPSStmt prepareFirstPassFillQuery(
    final RecordBuffer rbuf) {

    /*
     * Iterate over the fields in the expanded record field list
     * and select each of those in the statment. For each field that is
     * a key, add it to the WHERE clause and get its value from the
     * scroll buffer chain.
     */
    final Record recDefn = DefnCache.getRecord(rbuf.getRecName());
    final List<RecordField> rfList = recDefn.getExpandedFieldList();

    // If a non-required key exists, the SELECT clause will be limited
    // to select only key fields.
    boolean limitSelectClauseToKeys = false;
    if (recDefn.hasANonRequiredKeyField()) {
      limitSelectClauseToKeys = true;
    }

    boolean hasANonKeyField = recDefn.hasANonKeyField();

    /*
     * Ensure all required keys have an associated value in the scroll
     * buffer hierarchy. If any key does not, do not continue.
     */
    for (RecordField rf : rfList) {
      if (rf.isKey()
          && rbuf.getParentScrollBuffer()
            .getKeyValueFromHierarchy(rf.FIELDNAME) == null) {

        if(rf.isRequired()) {
          log.debug("Aborting first pass fill for Record.{}; "
              + "value does not exist for search key: {}", rbuf.getRecName(),
              rf.FIELDNAME);
          return null;
        } else {
          // If a non-required key field does not have a matching value,
          // we need to issue a query for all of the fields on the record.
          limitSelectClauseToKeys = false;
        }
      }
    }

    /*
     * Begin building fill query.
     */
    final StringBuilder query = new StringBuilder("SELECT ");
    final List<String> bindVals = new ArrayList<String>();

    for (int i = 0; i < rfList.size(); i++) {
      final RecordField rf = rfList.get(i);

      // If record has at least one non-required key, SELECT clause
      // must be limited to keys only.
      if (limitSelectClauseToKeys && !rf.isKey()
          && !rf.isSearchKey() && !rf.isAlternateSearchKey()) {
        continue;
      }

      // Excluding OPRID for recs w/ non key fields present;
      // this may be incorrect long-term.
      if (rf.FIELDNAME.equals("OPRID") && limitSelectClauseToKeys) {
        continue;
      }

      if (i > 0) { query.append(", "); }
      final String fieldname = rf.FIELDNAME;
      final PTTypeConstraint valTc = rf
          .getTypeConstraintForUnderlyingValue();

      // Apparently key fields that are dates or timestamps
      // appear twice in SELECT clauses...
      if ( (valTc.isUnderlyingClassEqualTo(PTDate.class)
            || valTc.isUnderlyingClassEqualTo(PTDateTime.class))
          && rf.isKey()) {
        query.append(fieldname).append(", ");
      }

      if (valTc.isUnderlyingClassEqualTo(PTDate.class)) {
        query.append("TO_CHAR(").append(fieldname)
          .append(",'YYYY-MM-DD')");
      } else if (valTc.isUnderlyingClassEqualTo(PTDateTime.class)) {
        query.append("TO_CHAR(CAST((").append(fieldname)
          .append(") AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF')");
      } else {
        query.append(fieldname);
      }
    }

    query.append(" FROM PS_").append(rbuf.getRecName());

    int i = 0;
    for (RecordField rf : rfList) {
      if (rf.isKey()
          && rbuf.getParentScrollBuffer()
            .getKeyValueFromHierarchy(rf.FIELDNAME) != null) {
        if (i == 0) { query.append(" WHERE "); }
        if (i > 0) { query.append(" AND "); }
        final String val = (String) rbuf.getParentScrollBuffer()
            .getKeyValueFromHierarchy(rf.FIELDNAME).read();
        query.append(rf.FIELDNAME).append("=?");
        bindVals.add(val);
        i++;
      }
    }

    i = 0;
    for (RecordField rf : rfList) {
      if (rf.isKey()) {

        // Excluding OPRID for recs w/ non key fields present;
        // this may be incorrect long-term.
        if (rf.FIELDNAME.equals("OPRID") && limitSelectClauseToKeys) {
          continue;
        }

        if (i == 0) { query.append(" ORDER BY "); }
        if (i > 0) { query.append(", "); }
        query.append(rf.FIELDNAME);
        if (rf.isDescendingKey()) {
          query.append(" DESC");
        }
        i++;
      }
    }

    return new OPSStmt(query.toString(), bindVals.toArray(
        new String[bindVals.size()]), OPSStmt.EmissionType.ENFORCED);
  }

  /**
   * Disconnects the underlying JDBC connection.
   */
  public static void disconnect() {
    try {
      conn.close();
    } catch (final java.sql.SQLException sqle) {
      log.warn("Unable to close connection to database.", sqle);
    }
  }

  private static final class StaticSqlDefn {
    private String uniqueLabel, sql;
    private OPSStmt.EmissionType emissionType;
    private StaticSqlDefn() {}
  }
}
