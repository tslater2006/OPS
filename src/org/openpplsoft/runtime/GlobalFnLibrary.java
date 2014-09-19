/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.*;
import java.util.*;
import java.util.regex.*;

import org.openpplsoft.sql.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.buffers.*;
import org.openpplsoft.types.*;
import org.apache.logging.log4j.*;

public class GlobalFnLibrary {

  private static Logger log = LogManager.getLogger(GlobalFnLibrary.class.getName());

  /*
   * This is a shared function used by logical PeopleCode functions (tests for
   * blank values).
   * IMPORTANT: Use this: http://it.toolbox.com/blogs/spread-knowledge/understanding-blanknull-field-values-for-using-with-all-and-none-peoplecode-functions-40672
   * as a reference for null/blank rules with PeopleSoft data types.
   */
  private static boolean doesContainValue(PTType p) {
    if(p instanceof PTField) {
      return doesContainValue(((PTField)p).getValue());
    } else if(p instanceof PTString) {
      return ((PTString) p).read().trim().length() > 0;
    } else if(p instanceof PTRecord) {
      /*
       * IMPORTANT: I am not sure if this is accurate. PT documentation
       * does not explicitly mention when a record is considered to logically
       * be blank/empty.
       */
      return (((PTRecord) p).getRecDefn() != null);
    } else {
      throw new OPSVMachRuntimeException("Unexpected data type passed " +
        "to doesContainValue(ptdt).");
    }
  }

  /*==================================*/
  /* Global system functions          */
  /*==================================*/

  /*
   * Return true if none of the specified fields contain a value, return false
   * if one or more contain a value.
   */
  public static void PT_None() {
    for(PTType arg : Environment.getDereferencedArgsFromCallStack()) {
      if(doesContainValue(arg)) {
        Environment.pushToCallStack(new PTBoolean(false));
        return;
      }
    }
    Environment.pushToCallStack(new PTBoolean(true));
  }

  /*
   * Return true if all of the specified fields contain a value, return false
   * if one or more do not.
   */
  public static void PT_All() {
    for(PTType arg : Environment.getDereferencedArgsFromCallStack()) {
      if(!doesContainValue(arg)) {
        Environment.pushToCallStack(new PTBoolean(false));
        return;
      }
    }
    Environment.pushToCallStack(new PTBoolean(true));
  }

  /**
   * TODO(mquinn): Implement this function.
   */
  public static void PT_Hide() {
    Environment.getDereferencedArgsFromCallStack();
  }

  /**
   * TODO(mquinn): Implement this function.
   */
  public static void PT_SetSearchDialogBehavior() {
    Environment.getDereferencedArgsFromCallStack();
  }

  /**
   * TODO(mquinn): Implement this function.
   */
  public static void PT_AllowEmplIdChg() {
    Environment.getDereferencedArgsFromCallStack();
  }

  public static void PT_Rept() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 2) {
      throw new OPSVMachRuntimeException("Expected two args.");
    }

    PTString str = (PTString)args.get(0);
    PTInteger reptNum = (PTInteger)args.get(1);

    StringBuilder b = new StringBuilder();
    for(int i = 0; i < reptNum.read(); i++) {
      b.append(str.read());
    }

    Environment.pushToCallStack(new PTString(b.toString()));
    }

  public static void PT_Len() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 1 && !(args.get(0) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected single string arg.");
    }

    Environment.pushToCallStack(
        new PTInteger(((PTString) args.get(0)).read().length()));
  }

  /*
   * TODO: Return true if DoModalComponent
   * has been previously called; requires more research.
   */
  public static void PT_IsModalComponent() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected zero arguments.");
    }
    Environment.pushToCallStack(new PTBoolean(false));
  }

  public static void PT_CreateRecord() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 1 || (!(args.get(0) instanceof PTString))) {
      throw new OPSVMachRuntimeException("Expected single string arg.");
    }

    PTRecord rec = new PTRecordTypeConstraint().alloc(
        DefnCache.getRecord(((PTString)args.get(0)).read()));
    rec.setDefault();
    Environment.pushToCallStack(rec);
  }

  public static void PT_CreateRowset() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 1 || (!(args.get(0) instanceof PTString))) {
      throw new OPSVMachRuntimeException("Expected single string arg.");
    }

    Environment.pushToCallStack(new PTRowsetTypeConstraint().alloc(
        DefnCache.getRecord(((PTString)args.get(0)).read())));
  }

  public static void PT_CreateArray() {

    /*
     * I am simply calling CreateArrayRept for now,
     * because I saw input for CreateArray(" ", 0) despite
     * the fact that the documentation says all arguments should
     * be the same type. In the future, there will likely be
     * instances where something other than CreateArrayRept
     * should be done.
     */
    PT_CreateArrayRept();
  }

  /**
   * Makes the Rowset object representing the level 0
   * component buffer available to caller.
   */
  public static void PT_GetLevel0() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected zero arguments.");
    }

    Environment.pushToCallStack(ComponentBuffer.ptGetLevel0());
  }

  public static void PT_CreateArrayRept() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 2 || (!(args.get(1) instanceof PTInteger))) {
      throw new OPSVMachRuntimeException("Expected two args, with the second "
          + "being an integer.");
    }

    int initialSize = ((PTInteger)args.get(1)).read();

    /*
     * If the type argument passed in is an array, create a new array
     * reference with a dimension that exceeds the array argument's by 1.
     * Otherwise create a one-dimension array.
     */
    PTArray newArray = null;
    if(args.get(0) instanceof PTArray) {
      PTArray arr = (PTArray) args.get(0);
      newArray = new PTArrayTypeConstraint(arr.dimensions + 1,
          arr.getOriginatingTypeConstraint()).alloc();
    } else {
      newArray = new PTArrayTypeConstraint(1,
          args.get(0).getOriginatingTypeConstraint()).alloc();
    }

    /*
     * IMPORTANT NOTE: If the type passed to this CreateArrayRept call is
     * an array, each iteration of the initialization loop should insert
     * a reference to that array into the array being initialized. The documentation
     * states that this is the behavior that occurs, unless .Clone() is used.
     */
    for(int i = 0; i < initialSize; i++) {
      throw new OPSVMachRuntimeException("Must support array instantiation in "+
        "CreateArrayRept; make sure to check toString() output.");
    }

    Environment.pushToCallStack(newArray);
  }

  public static void PT_IsMenuItemAuthorized() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(!(args.get(0) instanceof PTMenuLiteral)
        || !(args.get(1) instanceof PTMenuBarLiteral)
        || !(args.get(2) instanceof PTMenuItemLiteral)
        || !(args.get(3) instanceof PTPageLiteral)
        || !(args.get(4) instanceof PTString)) {
      throw new OPSVMachRuntimeException("The arguments provided to "
          + "IsMenuItemAuthorized do not match the expected types.");
    }

    String[] bindVals = {
      ((PTMenuLiteral) args.get(0)).getMenuName(),
      ((PTMenuBarLiteral) args.get(1)).getMenuBarName(),
      ((PTMenuItemLiteral) args.get(2)).getMenuItemName(),
      ((PTPageLiteral) args.get(3)).getPageName(),
      ((PTString) Environment.getSystemVar("%OperatorId")).read()
    };

    final String actionMode = ((PTString) args.get(4)).read();

    if (! (actionMode.equals("U") || actionMode.equals("A"))) {
      throw new OPSVMachRuntimeException("Unknown action mode value "
        + "(" + actionMode + ") encountered by IsMenuItemAuthorized.");
    }

    /*
     * First, get the menu defn provided and ensure that all of the
     * referenced component defns have been loaded into the defn cache.
     * At this time, nothing is done with these component defns within this
     * implementation of IsMenuItemAuthorized. However, this is clearly being
     * done by the PT implementation, and since component defn SQL is enforced,
     * it must also be done here as well in order to pass tracefile verification.
     * TODO(mquinn): Note that this can potentially be skipped if/when running in
     * an optimized mode that does not verify against a tracefile.
     */
    final Menu menuDefn = DefnCache.getMenu(bindVals[0]);
    menuDefn.loadReferencedComponents();

    /*
     * IMPORTANT NOTE:
     * The SQL retrieved here was handwritten by me (MQUINN) and not
     * based on anything found in a tracefile, since there are no SQL
     * stmts in/around the areas where IsMenuItemAuthorized is called in
     * the tracefiles I have at this point. Keep this in mind in the event
     * of future issues / changes.
     */
    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt(
        "query.PSAUTHITEM_PSOPRCLS_IsMenuItemAuthorized",
        bindVals);
    ResultSet rs = null;

    try {
      rs = ostmt.executeQuery();

      /*
       * Iterate over each record in the resultset; we are looking for
       * the first one that allows the user to access the requested menu item
       * in the mode provided as an argument.
       * NOTE: I am checking simple equality b/w the mode arg and
       * AUTHORIZEDACTIONS for now. AUTHORIZEDACTIONS is actually a bit mask.
       * See the following for more information:
       * - http://www.erpassociates.com/peoplesoft-corner-weblog/security/secrets-of-psauthitem.html
       * - http://peoplesoftwiki.blogspot.com/2009/12/finding-barname-itemname-and-all-about.html
       * - http://peoplesoft.ittoolbox.com/groups/technical-functional/peopletools-l/how-to-interpret-authorizedactons-with-components-under-barname-3522093
       */
      while (rs.next()) {
        final String permList = rs.getString("PERMISSION_LIST_NAME");
        final int authorizedActions = rs.getInt("AUTHORIZEDACTIONS");

        log.debug("IsMenuItemAuthorized: Checking: "
            + "Permission List: {}; AUTHORIZEDACTIONS: {}",
            rs.getString("PERMISSION_LIST_NAME"),
            rs.getInt("AUTHORIZEDACTIONS"));

        /*
         * TODO(mquinn): This mapping is done adhoc right now, but once
         * more action modes are added, an enum should be use; see the links
         * listed above for exact mappings.
         */
        final int ADD_MASK = 1;
        final int UPDATE_DISPLAY_MASK = 2;
        if (   ((authorizedActions & ADD_MASK) > 0) && actionMode.equals("A")
            || ((authorizedActions & UPDATE_DISPLAY_MASK) > 0)
                   && actionMode.equals("U")) {
          log.debug("IsMenuItemAuthorized: found permissible record, returning True.");
          Environment.pushToCallStack(new PTBoolean(true));
          return;
        }
      }
    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if (rs != null) { rs.close(); }
        if (ostmt != null) { ostmt.close(); }
      } catch (final java.sql.SQLException sqle) {
        log.warn("Unable to close rs and/or ostmt in finally block.");
      }
    }

    // If no record permitted access for the given actionMode,
    // access to the menu item is not authorized.
    log.debug("IsMenuItemAuthorized: no permissible records found,"
      + " returning False.");
    Environment.pushToCallStack(new PTBoolean(false));
  }

  public static void PT_MsgGetText() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 3) {
      throw new OPSVMachRuntimeException("Expected exactly 3 args to "
          + "MsgGetText; altho var args to this fn are legal (see PT "
          + "reference for defn) they are not supported at this time.");
    }

    if(!(args.get(0) instanceof PTNumber || args.get(0) instanceof PTInteger)
        || !(args.get(1) instanceof PTNumber || args.get(0) instanceof PTInteger)
        || !(args.get(2) instanceof PTString)) {
      throw new OPSVMachRuntimeException("The arguments provided to "
          + "MsgGetText do not match the expected types.");
    }

    final int msgSetNbr;
    if (args.get(0) instanceof PTNumber) {
      msgSetNbr = ((PTNumber) args.get(0)).readAsInteger();
    } else {
      msgSetNbr = ((PTInteger) args.get(0)).read();
    }
    final MsgSet msgSet = DefnCache.getMsgSet(msgSetNbr);

    final int msgNbr;
    if (args.get(0) instanceof PTNumber) {
      msgNbr = ((PTNumber) args.get(1)).readAsInteger();
    } else {
      msgNbr = ((PTInteger) args.get(1)).read();
    }
    final String msg = msgSet.getMessage(msgNbr);

    if (msg == null) {
      log.debug("MsgGetText found no msg with setnbr={} and msgnbr={}; "
          + "returning default message provided: {}", msgSetNbr, msgNbr,
          ((PTString) args.get(2)).read());
      Environment.pushToCallStack(args.get(2));
      return;
    }

    Environment.pushToCallStack(new PTString(msg));
  }

  public static void PT_GenerateComponentContentRelURL() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 8) {
      throw new OPSVMachRuntimeException("Expected exactly 8 args to "
          + "MsgGetText; altho var args to this fn are legal (see PT "
          + "reference for defn) they are not supported at this time.");
    }

    if(!(args.get(0) instanceof PTString)
        || !(args.get(1) instanceof PTString)
        || !(args.get(2) instanceof PTMenuLiteral)
        || !(args.get(3) instanceof PTString)
        || !(args.get(4) instanceof PTComponentLiteral)
        || !(args.get(5) instanceof PTPageLiteral)
        || !(args.get(6) instanceof PTString)
        || !(args.get(7) instanceof PTRecord)) {
      throw new OPSVMachRuntimeException("The arguments provided to "
          + "GenerateComponentContentRelUrl do not match the expected types.");
    }

    // the "c" in "psc" stands for component; this will never be "psp" here
    // (since url is being generated for *component*)
    final StringBuilder url = new StringBuilder("/psc/");

    // append PS environment name (i.e., XENCSDEV, ENTCSDEV, etc.)
    url.append(Environment.psEnvironmentName);

    // append portal name (i.e., EMPLOYEE)
    url.append("/").append(((PTString) args.get(0)).read());

    // append node name (i.e., HRMS)
    url.append("/").append(((PTString) args.get(1)).read());

    // append "c" (for component)
    url.append("/c/");

    // append <menu>.<component>.<market>
    url.append(((PTMenuLiteral) args.get(2)).getMenuName())
       .append(".").append(((PTComponentLiteral) args.get(4)).getComponentName())
       .append(".").append(((PTString) args.get(3)).read());

    // append "?Page=<page>"
    url.append("?Page=").append(((PTPageLiteral) args.get(5)).getPageName());

    // append "&Action=<action>"
    url.append("&Action=").append(((PTString) args.get(6)).read());

    /*
     * The keylist must be in alphabetical order by key.
     */
    Map<String, PTImmutableReference<PTField>> fieldsUnsorted
        = ((PTRecord) args.get(7)).getFieldRefs();
    Map<String, PTImmutableReference<PTField>> fieldsKeyAscMap =
        new TreeMap<String, PTImmutableReference<PTField>>(fieldsUnsorted);
    for(Map.Entry<String, PTImmutableReference<PTField>> cursor
        : fieldsKeyAscMap.entrySet()) {
      // NOTE: trim() is important here; blank values are a single space
      // in PS and should be reduced to the empty string.
      url.append("&").append(cursor.getKey()).append("=")
          .append(cursor.getValue().deref().getValue().readAsString().trim());
    }

    log.debug("GenerateComponentContentURL: From args, generated url: {}",
      url.toString());

    Environment.pushToCallStack(new PTString(url.toString()));
  }

  public static void PT_Truncate() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 2
        || !(args.get(0) instanceof PTNumber)
        || !(args.get(1) instanceof PTInteger)) {
      throw new OPSVMachRuntimeException("Expected exactly 2 args "
          + "of type PTNumber and PTNumber/PTInteger to Truncate.");
    }

    final BigDecimal bigDec = ((PTNumber) args.get(0)).read();
    final int desiredDecimalDigits = ((PTInteger) args.get(1)).read();

    BigDecimal truncatedBigDec = bigDec.setScale(desiredDecimalDigits, RoundingMode.DOWN);

    log.debug("Truncated {} to have {} decimal digits; result is: {}",
      bigDec, desiredDecimalDigits, truncatedBigDec);

    Environment.pushToCallStack(new PTNumber(truncatedBigDec));
  }

  public static void PT_String() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 1
        || !(args.get(0) instanceof PTInteger)) {
      throw new OPSVMachRuntimeException("Expected exactly 1 arg "
          + "of type PTInteger to String.");
    }

    Environment.pushToCallStack(
        new PTString(((PTInteger) args.get(0)).readAsString()));
  }

  public static void PT_GetHTMLText() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() == 0
        || !(args.get(0) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected at least 1 arg, "
          + "the first being of type PTString to GetHTMLText.");
    }

    final HTML html = DefnCache.getHTML(((PTString) args.get(0)).read());

    // If no additional args were provided, there is no need to look for
    // bind placeholders in the html text.
    if (args.size() == 1) {
      Environment.pushToCallStack(new PTString(html.getHTMLText()));
      return;
    }

    String htmlStr = html.getHTMLText();

    // Replace bind placeholders in HTML defn with provided values.
    for(int i = 1; i < args.size(); i++) {
      if (!(args.get(i) instanceof PTString)) {
          throw new OPSVMachRuntimeException("Expected arg to GetHTMLText "
              + "passed as bind value to be of type PTString.");
      }

      Pattern bindPattern = Pattern.compile("%BIND\\(:" + i + "\\)");
      Matcher bindMatcher = bindPattern.matcher(htmlStr);

      if(!bindMatcher.find()) {
          throw new OPSVMachRuntimeException("Expected a bind placeholder "
              + "in html text (%BIND(:" + i + ") but found none; this may "
              + "not be a true problem (it may be possible that not every "
              + "value will be bound for every HTML defn) but I am making this "
              + "an exception for now to determine 1) if this is the case and 2) "
              + "if any bind placeholders are not in full uppercase (i.e., %Bind).");
      }

      htmlStr = bindMatcher.replaceAll(((PTString) args.get(i)).read());
    }

    Environment.pushToCallStack(new PTString(htmlStr));
  }

  public static void PT_Proper() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 1
        || !(args.get(0) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected exactly 1 arg, "
          + "of type PTString to Proper.");
    }

    String str = ((PTString) args.get(0)).read();

    // If the str is less than 2 chars long, return it's uppercased form.
    if (str.length() < 2) {
      Environment.pushToCallStack(new PTString(str.toUpperCase()));
    }

    /*
     * Otherwise, we know str is at least two chars long, so
     * uppercase the first character in the string and lowercase
     * the rest; regex will be used to uppercase those chars
     * that follow a whitespace character (NOTE: PT documentation says
     * "non-letter" but in testing, this is most definitely not the case,
     * only chars following whitespace are upper-cased).
     */
    str = "" + Character.toUpperCase(str.charAt(0))
          + str.substring(1).toLowerCase();

    final Pattern bindPattern = Pattern.compile("\\s+[a-zA-Z]");
    final Matcher bindMatcher = bindPattern.matcher(str);

    final StringBuffer sb = new StringBuffer();
    while (bindMatcher.find()) {
      bindMatcher.appendReplacement(sb, bindMatcher.group().toUpperCase());
    }
    bindMatcher.appendTail(sb);

    // At this point, string is ready to be returned to caller.
    Environment.pushToCallStack(new PTString(sb.toString()));
  }

  public static void PT_Lower() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 1
        || !(args.get(0) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected exactly 1 arg, "
          + "of type PTString to Lower.");
    }

    Environment.pushToCallStack(
        new PTString(((PTString) args.get(0)).read().toLowerCase()));
  }

  public static void PT_GetSQL() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() == 0
        || !(args.get(0) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected at least 1 arg, "
          + "the first being of type PTString to GetHTMLText.");
    }

    final SQL sql = DefnCache.getSQL(((PTString) args.get(0)).read());
    final PTPrimitiveType[] bindVals = new PTPrimitiveType[args.size() - 1];

    for (int i = 1; i < args.size(); i++) {
      PTType bVal = args.get(i);
      if (bVal instanceof PTPrimitiveType) {
        bindVals[i - 1] = (PTPrimitiveType) bVal;
      } else if (bVal instanceof PTField
          && ((PTField) bVal).getValue() instanceof PTPrimitiveType) {
        // If a Field object is passed as a bind value, it must be unboxed
        // and its unboxed value must be primitive.
        bindVals[i - 1] = (PTPrimitiveType) ((PTField) bVal).getValue();
      } else {
        throw new OPSVMachRuntimeException("Illegal bind value supplied to GetSQL; "
            + "expected primitive but got: " + args.get(i));
      }
    }

    PTSQL sqlObj = new PTSQLTypeConstraint().alloc(sql, bindVals);

    // Execute the underlying SQL, thereby making the associated ResultSet
    // available for operations like Fetch.
    sqlObj.executeSql();

    Environment.pushToCallStack(sqlObj);
  }

  /*==================================*/
  /* Shared OPS functions             */
  /*==================================*/

  public static void readRecordFromResultSet(Record recDefn,
      PTRecord recObj, ResultSet rs) throws SQLException {

    ResultSetMetaData rsMetadata = rs.getMetaData();
    int numCols = rsMetadata.getColumnCount();

    for(int i = 1; i <= numCols; i++) {
      final String colName = rsMetadata.getColumnName(i);
      final String colTypeName = rsMetadata.getColumnTypeName(i);
      final PTField fldObj = recObj.getFieldRef(colName).deref();
      GlobalFnLibrary.readFieldFromResultSet(fldObj,
        colName, colTypeName, rs);
    }
  }

  public static void readFieldFromResultSet(PTField fldObj,
      String colName, String colTypeName, ResultSet rs) throws SQLException {

    PTTypeConstraint origTc = fldObj.getValue().getOriginatingTypeConstraint();

    log.debug("Copying {} with type {} from resultset to Field:{} "+
        "with type constraint {}", colName, colTypeName,
        fldObj.getRecordFieldDefn().FIELDNAME, origTc);

    if(origTc.isUnderlyingClassEqualTo(PTChar.class)) {
      /*
       * TODO(mquinn): Read CLOB and chars from ResultSet rather than
       * as string.
       */
      if(colTypeName.equals("CHAR") || colTypeName.equals("VARCHAR2")) {
        ((PTChar) fldObj.getValue()).write(
          rs.getString(colName));
      } else {
        throw new OPSVMachRuntimeException("Unexpected db " +
          "type for PTChar: " + colTypeName + "; " +
          "colName=" + colName);
      }
    } else if (origTc.isUnderlyingClassEqualTo(PTString.class)) {
      if(colTypeName.equals("VARCHAR2") || colTypeName.equals("CLOB")
          || colTypeName.equals("CHAR")) {
        ((PTString) fldObj.getValue()).write(
          rs.getString(colName));
      } else {
        throw new OPSVMachRuntimeException("Unexpected db " +
          "type for PTString: " + colTypeName + "; " +
          "colName=" + colName);
      }
    } else if (origTc.isUnderlyingClassEqualTo(PTNumber.class)) {
      if(colTypeName.equals("NUMBER")) {
        ((PTNumber) fldObj.getValue()).write(rs.getBigDecimal(colName));
      } else {
        throw new OPSVMachRuntimeException("Unexpected db " +
          "type for PTNumber: " + colTypeName + "; " +
          "colName=" + colName);
      }
    } else if (origTc.isUnderlyingClassEqualTo(PTDate.class)) {
      if(colTypeName.equals("VARCHAR2")) {
        ((PTDate)fldObj.getValue()).write(
          rs.getString(colName));
      } else {
        throw new OPSVMachRuntimeException("Unexpected db " +
          "type for PTDate: " + colTypeName + "; " +
          "colName=" + colName);
      }
    } else if (origTc.isUnderlyingClassEqualTo(PTDateTime.class)) {
        /*
         * TODO(mquinn): May need to be split apart into separate
         * statements with different types.
         */
      if(colTypeName.equals("VARCHAR2") || colTypeName.equals("TIMESTAMP")) {
        ((PTDateTime)fldObj.getValue()).write(
          rs.getString(colName));
      } else {
        throw new OPSVMachRuntimeException("Unexpected db "
            + "type for PTDateTime: " + colTypeName + "; "
            + "colName=" + colName);
      }
    } else {
      throw new OPSVMachRuntimeException("Unexpected field "
          + "value type constraint encountered when filling rowset: "
          + origTc);
    }
  }

  @SuppressWarnings("unchecked")
  public static void assign(final PTType dst, final PTType src) {

    if (!(dst instanceof PTReference)) {
      throw new OPSVMachRuntimeException("Illegal assignment; not a valid l-value: "
          + dst);
    }

    final PTReference lRef = (PTReference) dst;
    PTType rawSrc = src;

    if (src instanceof PTReference) {
      rawSrc = ((PTReference) src).deref();
    }

    if (rawSrc instanceof PTPrimitiveType) {
      if (lRef.deref() instanceof PTPrimitiveType) {
        ((PTPrimitiveType) lRef.deref()).copyValueFrom((PTPrimitiveType) rawSrc);
      } else if (lRef.deref() instanceof PTField) {
        ((PTField) lRef.deref()).getValue().copyValueFrom((PTPrimitiveType) rawSrc);

      // NOTE: You must look at the type constraint of the *reference* in the
      // conditional directly below this comment, not the
      // value it points to, b/c the value could be null and as of this writing,
      // PTNull is a singleton and thus per-null originating type constraints
      // are not associated with it.
      } else if (lRef.getOriginatingTypeConstraint()
          instanceof PTAnyTypeConstraint) {
        /*
         * Variables of type Any can point to objects or primitives. If an object
         * (or Null) is asigned to the identifier, a new primitive must be alloc'ed
         * containing a copy of the value in source operand.
         */
        PTPrimitiveType primCopy = (PTPrimitiveType)
            ((PTPrimitiveType) rawSrc).getOriginatingTypeConstraint().alloc();
        primCopy.copyValueFrom((PTPrimitiveType) rawSrc);
        try {
          lRef.pointTo(primCopy);
        } catch (final OPSImmutableRefAttemptedChangeException opsirace) {
          throw new OPSVMachRuntimeException(opsirace.getMessage(), opsirace);
        } catch (final OPSTypeCheckException opstce) {
          throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
        }
      } else {
        throw new OPSVMachRuntimeException("Assignment failed; rawSrc is primitive "
            + "but lRef dereferences to neither a primitive nor a PTField: " + lRef.deref());
      }
    } else if(rawSrc instanceof PTObjectType) {
      try {
        lRef.pointTo(rawSrc);
      } catch (final OPSImmutableRefAttemptedChangeException opsirace) {
        if (rawSrc instanceof PTField && lRef.deref() instanceof PTPrimitiveType) {
          // If lRef refers to a primitive and rawSrc is a Field, re-attempt the
          // assignment, this time copying the value from
          // the Field's underlying value to the referred primitive.
          ((PTPrimitiveType) lRef.deref()).copyValueFrom(((PTField) rawSrc).getValue());
        } else {
          throw new OPSVMachRuntimeException("Assignment failed, even after "
              + "checking if rawSrc is a PTField that needs to be unwrapped "
              + "to its enclosed value.", opsirace);
        }
      } catch (final OPSTypeCheckException opstce) {
        throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
      }
    } else {
      throw new OPSVMachRuntimeException("Assignment failed; unexpected "
          + "rawSrc: " + src + "; lRef is " + lRef);
    }
  }
}
