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
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
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
      throw new OPSVMachRuntimeException(ioe.getMessage(), ioe);
    } finally {
      try {
        if (br != null) { br.close(); }
      } catch (final java.io.IOException ioe) {
        log.warn("Unable to close br in finally block.");
      }
    }

    // compile meta-SQL detection regex patterns.
    bindIdxPattern = Pattern.compile(":(\\d+)");
    dateInPattern = Pattern.compile("%(DATEIN|DateIn)\\((.+?)\\)");
    effDtCheckPattern = Pattern.compile(
      "%EffDtCheck\\(([A-Za-z_]+)(\\s+([A-Za-z0-9_]+))?,\\s+([A-Za-z]+),\\s+(.*)\\)");
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

    query.append(" FROM ").append(recDefn.getDbTableName()).append(" WHERE ");

    i = 0;
    for (RecordField rf : rfList) {
      if (rf.isKey() || rf.isSearchKey()) {
        if (i > 0) { query.append(" AND "); }

        final PTPrimitiveType val = searchRec
            .getFieldRef(rf.FIELDNAME).deref().getValue();
        query.append(rf.FIELDNAME);

        if (rf.isListBoxItem() && rf.FIELDNAME.equals("EMPLID")) {
          query.append(" LIKE '").append((String) val.read()).append("%'");
        } else {
          query.append("=?");

          /*
           * If this is the OPRID field, PS automatically adds a WHERE clause
           * to select only those records where OPRID equals %OperatorId if
           * both of the following are true:
           * 1) OPRID is a key on the record being queried
           * 2) OPRID is NOT a list box item (meaning it doesn't appear in the
           *    results shown to the user in the "list box" on the search page.
           * If both of those are true, add the condition now.
           */
          if (rf.FIELDNAME.equals("OPRID") && rf.isKey() && !rf.isListBoxItem()) {
            bindVals.add(Environment.getSystemVar("%OperatorId").readAsString());
          } else {
            bindVals.add((String) val.read());
          }
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

  public static OPSStmt convertForJDBCAndGetOPSStmt(
      final String query, final String[] bindVals,
          final OPSStmt.EmissionType eType) {

    final List<String> expandedBindVals = new ArrayList<String>();
    final Matcher bindIdxMatcher = bindIdxPattern.matcher(query);
    while (bindIdxMatcher.find()) {
      final int bindIdx = Integer.parseInt(bindIdxMatcher.group(1));

      // PS bind indices are 1-based, must subtract 1 here.
      expandedBindVals.add(bindVals[bindIdx - 1]);
    }

    bindIdxMatcher.reset();
    final String newSql = bindIdxMatcher.replaceAll("?");

    return new OPSStmt(newSql, expandedBindVals.toArray(
        new String[expandedBindVals.size()]), eType);
  }

  public static OPSStmt prepareSqlFromSQLDefn(final SQL sqlDefn,
    final String[] bindVals) {

    // We can't directly get an OPSStmt with this query b/c it
    // 1) has numeric bind indices (not "?") and 2)
    // converting these numeric indices to "?" may require that the list
    // of bind values be expanded (if a bind index appears multiple
    // times in the list).
    return convertForJDBCAndGetOPSStmt(sqlDefn.getSQLText(), bindVals,
        OPSStmt.EmissionType.ENFORCED);
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

    String newWhereStr = whereStr;

    // Expand any %EffDtCheck meta-SQL
    final Matcher effDtCheckMatcher = effDtCheckPattern.matcher(newWhereStr);
    while (effDtCheckMatcher.find()) {

      final String effDtCheckRecord = effDtCheckMatcher.group(1);
      // Do not use group 2 (contains whitespace preceding the optional subquery alias)
      String effDtSubqueryAlias = effDtCheckMatcher.group(3);
      final String effDtRootAlias = effDtCheckMatcher.group(4);
      String effDtBound = effDtCheckMatcher.group(5);

      if(!rootAlias.equals(effDtRootAlias)) {
        throw new OPSVMachRuntimeException("While preparing fill query, "
          + "found %EffDtCheck that has a root alias (" + effDtRootAlias
          + ") different than expected (" + rootAlias + ").");
      }

      // If subquery alias is not specified, use name of record being checked.
      if (effDtSubqueryAlias == null) {
        effDtSubqueryAlias = effDtCheckRecord;
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

      // If the effDtBound is not a meta-sql construct, it will not
      // be wrapped with TO_DATE later in this function, so wrap it now.
      if (!effDtBound.startsWith("%")) {
        effDtBound = "TO_DATE(" + effDtBound + ",'YYYY-MM-DD')";
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

    // We can't directly get an OPSStmt with this query b/c it
    // 1) has numeric bind indices (not "?") and 2)
    // converting these numeric indices to "?" may require that the list
    // of bind values be expanded (if a bind index appears multiple
    // times in the list).
    return convertForJDBCAndGetOPSStmt(query.toString(), bindVals,
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
          bindVals.add((String) recObj.getFieldRef(rf.FIELDNAME)
              .deref().getValue().read());
        } else {
          /*
           * Insert subquery for EFFDT field.
           */
          query.append("(")
             .append("SELECT MAX(EFFDT) FROM ")
             .append(recDefn.getDbTableName())
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
                bindVals.add(effDt.readAsString());
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
        bindVals.add((String) recObj.getFieldRef(rf.FIELDNAME).deref()
          .getValue().read());
      }
    }

    return new OPSStmt(query.toString(), bindVals.toArray(
        new String[bindVals.size()]), OPSStmt.EmissionType.ENFORCED);
  }

  /**
   * @param defaultRecDefn the record defn named in DEFRECNAME for the record
   * field with the default value
   * @param fieldBeingDefaulted the field being defaulted
   */
  public static OPSStmt generateNonConstantFieldDefaultQuery(
        final Record defaultRecDefn, final PTField fieldBeingDefaulted)
            throws OPSCBufferKeyLookupException {

    final String[] aliasedFields = getOptionallyAliasedFieldsToSelect(
        defaultRecDefn, "", true);
    final List<RecordField> rfList = defaultRecDefn.getExpandedFieldList();

    final StringBuilder query = new StringBuilder("SELECT ");
    final List<String> bindVals = new ArrayList<String>();

    for (int i = 0; i < aliasedFields.length; i++) {
      if (i > 0) { query.append(", "); }
      query.append(aliasedFields[i]);
    }

    query.append(" FROM ").append(defaultRecDefn.getDbTableName());

    int i = 0;
    for (RecordField rf : rfList) {
      if (rf.isKey()) {

        // Don't include EFFSEQ in non constant queries.
        if (rf.FIELDNAME.equals("EFFSEQ")) {
          continue;
        }

        log.debug("Looking up key for {}.{}", defaultRecDefn.RECNAME,
            rf.FIELDNAME);

        String keyValue = null;

        /*
         * (Row level security): :If this is the OPRID or OPRCLASS field, PS
         * automatically adds a WHERE clause
         * to select only those records where OPRID equals %OperatorId or OPRCLASS
         * equals %OperatorClass if
         * both of the following are true:
         * 1) OPRID/OPRCLASS is a key on the record being queried
         * 2) OPRID/OPRCLASS is NOT a list box item (meaning it doesn't appear in the
         *    results shown to the user in the "list box" on the search page.
         * If both of those are true, add the condition now.
         */
        if (rf.FIELDNAME.equals("OPRID") && rf.isKey() && !rf.isListBoxItem()) {
          keyValue = Environment.getSystemVar("%OperatorId").readAsString();
          log.debug("Using %OperatorId for OPRID field.");
        } else if (rf.FIELDNAME.equals("OPRCLASS") && rf.isKey() && !rf.isListBoxItem()) {
          keyValue = Environment.getSystemVar("%OperatorClass").readAsString();
          log.debug("Using %OperatorClass for OPRCLASS field.");
        } else {
          final Keylist keylist = new Keylist();
          fieldBeingDefaulted.getParentRecord()
              .generateKeylist(rf.FIELDNAME, keylist);
          log.debug("Keylist for {}.{} (def record for non-constant query): {}",
              rf.RECNAME, rf.FIELDNAME, keylist);

          if (keylist.hasNonBlankValue()) {
            keyValue = keylist.getFirstNonBlankField().getValue().readAsString();
            log.debug("Resolved field {} to {}.", rf.FIELDNAME, keyValue);
          } else {
            if (rf.FIELDNAME.equals("EFFDT")) {
              keyValue = Environment.getSystemVar("%Date").readAsString();
            } else if (!rf.isRequired()) {
              // If no value was found, but the field isn't marked as required,
              // use a blank value.
              keyValue = ((PTPrimitiveType) rf.getTypeConstraintForUnderlyingValue()
                  .alloc()).readAsString();
            } else {
              throw new OPSCBufferKeyLookupException("A non-blank value was not found for "
                  + "the required key field: " + defaultRecDefn.RECNAME + "." + rf.FIELDNAME
                  + "; cannot "
                  + "generate non-constant default query on this record at this time.");
            }
          }
        }

        if (i == 0) { query.append(" WHERE "); }
        if (i > 0) { query.append(" AND "); }
        if (rf.FIELDNAME.equals("EFFDT")) {
          query.append(rf.FIELDNAME).append("<=TO_DATE(?,'YYYY-MM-DD')");
        } else {
          query.append(rf.FIELDNAME).append("=?");
        }
        bindVals.add(keyValue);
        i++;
      }
    }

    i = 0;
    for (RecordField rf : rfList) {
      if (rf.isKey()) {
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

    final String[] aliasedFields = getOptionallyAliasedFieldsToSelect(
        recDefn, dottedAlias, false);
    for (int i = 0; i < aliasedFields.length; i++) {
      if (i > 0) { selectClause.append(","); }
      selectClause.append(aliasedFields[i]);
    }

    selectClause.append(" FROM ").append(recDefn
        .getDbTableName()).append(" ").append(tableAlias);

    return selectClause.toString();
  }

  private static String[] getOptionallyAliasedFieldsToSelect(final Record recDefn,
      final String dottedAlias, final boolean includeRawAndToCharEffdt) {

    final List<RecordField> rfList = recDefn.getExpandedFieldList();
    final List<String> aliasedFields = new ArrayList<String>();

    for (int i = 0; i < rfList.size(); i++) {
      final String fieldname = rfList.get(i).FIELDNAME;

      /**
       * For certain field types, wrap field names in
       * appropriate function calls.
       */
      if (rfList.get(i).getTypeConstraintForUnderlyingValue()
          .isUnderlyingClassEqualTo(PTDate.class)) {

        if (fieldname.equals("EFFDT") && includeRawAndToCharEffdt) {
          aliasedFields.add(dottedAlias + fieldname);
          aliasedFields.add("TO_CHAR(" + dottedAlias
              + fieldname + ",'YYYY-MM-DD')");
        } else {
          aliasedFields.add("TO_CHAR(" + dottedAlias
              + fieldname + ",'YYYY-MM-DD')");
        }

      } else if (rfList.get(i).getTypeConstraintForUnderlyingValue()
          .isUnderlyingClassEqualTo(PTDateTime.class)) {
        aliasedFields.add("TO_CHAR(CAST((" + dottedAlias + fieldname
            + ") AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF')");
      } else {
        aliasedFields.add(dottedAlias + fieldname);
      }
    }

    return aliasedFields.toArray(new String[0]);
  }

  /**
   * Generates an OPSStmt that will fill data into the provided
   * RecordBuffer during the first pass fill phase.
   * @param rbuf the RecordBuffer to fill
   * @return the OPSStmt to be executed
   */
  public static OPSStmt prepareFirstPassFillQuery(
      final PTRecord record) {

    /*
     * Iterate over the fields in the expanded record field list
     * and select each of those in the statment. For each field that is
     * a key, add it to the WHERE clause and get its value from the
     * scroll buffer chain.
     */
    final Record recDefn = record.getRecDefn();
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

      if (rf.isKey()) {

        final Keylist keylist = new Keylist();
        record.generateKeylist(rf.FIELDNAME, keylist);
        log.debug("Keylist for field {}.{}: {}", rf.RECNAME, rf.FIELDNAME, keylist);

        if (keylist.size() == 0) {
          if (rf.isRequired()) {
            throw new OPSVMachRuntimeException("Aborting first pass fill for Record. "
                + recDefn.RECNAME + "; keylist is empty for: " + rf.FIELDNAME);
          } else {
            // If a non-required key field does not have a matching value,
            // we need to issue a query for all of the fields on the record.
            limitSelectClauseToKeys = false;
          }
        } else if (keylist.size() > 1) {
          throw new OPSVMachRuntimeException("Aborting first pass fill for Record. "
              + recDefn.RECNAME + "; multiple key values found for: " + rf.FIELDNAME);
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

    query.append(" FROM ").append(recDefn.getDbTableName());

    int i = 0;
    for (RecordField rf : rfList) {
      if (rf.isKey()) {

        String val = null;

        /*
         * If this is the OPRID field, PS automatically adds a WHERE clause
         * to select only those records where OPRID equals %OperatorId if
         * both of the following are true:
         * 1) OPRID is a key on the record being queried
         * 2) OPRID is NOT a list box item (meaning it doesn't appear in the
         *    results shown to the user in the "list box" on the search page.
         * If both of those are true, add the condition now.
         */
        if (rf.FIELDNAME.equals("OPRID") && rf.isKey() && !rf.isListBoxItem()) {
          val = Environment.getSystemVar("%OperatorId").readAsString();
        } else {
          final Keylist keylist = new Keylist();
          record.generateKeylist(rf.FIELDNAME, keylist);
          log.debug("Keylist for field {}.{}: {}", rf.RECNAME, rf.FIELDNAME, keylist);

          if (keylist.size() == 0) {
            // If key value cannot be resolved, do not include it (see logic above).
            continue;
          } else if (keylist.size() == 1) {
            val = keylist.get(0).getValue().readAsString();
          } else {
            throw new OPSVMachRuntimeException("Expected one value for key " + rf.FIELDNAME
                + " on record " + rf.RECNAME + ", multiple found.");
          }
        }

        if (i == 0) { query.append(" WHERE "); }
        if (i > 0) { query.append(" AND "); }
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
