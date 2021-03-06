/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.buffers.ComponentBuffer;
import org.openpplsoft.pt.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.types.*;

public class GlobalFnLibrary {

  private static Logger log = LogManager.getLogger(
      GlobalFnLibrary.class.getName());
  public InterpretSupervisor interpretSupervisor;

  public GlobalFnLibrary(final InterpretSupervisor supervisor) {
    this.interpretSupervisor = supervisor;
  }

  public static boolean hasFuncNamed(final String id) {
    try {
      final Method method = GlobalFnLibrary.class
          .getMethod(id, new Class[0]);
    } catch (final NoSuchMethodException nsme) {
      return false;
    }
    return true;
  }

  public Callable getFuncCallable(final String id) {
    try {
      final Method method = this.getClass().getMethod(id, new Class[0]);
      return new Callable(method, this);
    } catch (final NoSuchMethodException nsme) {
      throw new OPSVMachRuntimeException(
          "No global function exists for identifier: " + id);
    }
  }

  /*
   * This is a shared function used by logical PeopleCode functions (tests for
   * blank values).
   * IMPORTANT: Use this: http://it.toolbox.com/blogs/spread-knowledge/understanding-blanknull-field-values-for-using-with-all-and-none-peoplecode-functions-40672
   * as a reference for null/blank rules with PeopleSoft data types.
   */
  private boolean doesContainValue(final PTType val) {

    final PTType p = Environment.getOrDeref(val);

    if (p instanceof PTNull) {
      return false;
    } else if (p instanceof PTField) {
      return doesContainValue(((PTField) p).getValue());
    } else if(p instanceof PTString) {
      // IMPORTANT: PS uses " " for blanks, but "" is also indicative of
      // an absent value, so must check this before checking PrimitiveType values
      // in general (next conditional).
      return ((PTString) p).read().trim().length() > 0;
    } else if(p instanceof PTPrimitiveType) {
      return !((PTPrimitiveType) p).isBlank();
    } else if(p instanceof PTRecord) {
      /*
       * IMPORTANT: I am not sure if this is accurate. PT documentation
       * does not explicitly mention when a record is considered to logically
       * be blank/empty.
       */
      return (((PTRecord) p).getRecDefn() != null);
    } else if (p instanceof PTRecordFieldSpecifier) {
      return doesContainValue(
          ((PTRecordFieldSpecifier) p).resolveInCBufferContext().deref());
    } else {
      throw new OPSVMachRuntimeException("Unexpected data type passed " +
        "to doesContainValue(ptdt): " + p);
    }
  }

  /*==================================*/
  /* Global system functions          */
  /*==================================*/

  /*
   * Return true if none of the specified fields contain a value, return false
   * if one or more contain a value.
   */
  @PeopleToolsImplementation
  public void None() {
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
  @PeopleToolsImplementation
  public void All() {
    for(PTType arg : Environment.getDereferencedArgsFromCallStack()) {
      if(!doesContainValue(arg)) {
        Environment.pushToCallStack(new PTBoolean(false));
        return;
      }
    }
    Environment.pushToCallStack(new PTBoolean(true));
  }

  @PeopleToolsImplementation
  public void Hide() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 1 || !(args.get(0) instanceof PTRecordFieldSpecifier)) {
      throw new OPSVMachRuntimeException("Expected single PTRecordFieldSpecifier "
          + " arg to Hide.");
    }

    ((PTRecordFieldSpecifier) args.get(0)).resolveInCBufferContext().deref().hide();
  }

  @PeopleToolsImplementation
  public void UnHide() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 1 || !(args.get(0) instanceof PTRecordFieldSpecifier)) {
      throw new OPSVMachRuntimeException("Expected single PTRecordFieldSpecifier "
          + "arg to UnHide.");
    }

    ((PTRecordFieldSpecifier) args.get(0)).resolveInCBufferContext().deref().unhide();
  }

  /**
   * TODO(mquinn): Implement this function.
   */
  @PeopleToolsImplementation
  public void SetSearchDialogBehavior() {
    Environment.getDereferencedArgsFromCallStack();
  }

  /**
   * TODO(mquinn): Implement this function.
   */
  @PeopleToolsImplementation
  public void AllowEmplIdChg() {
    Environment.getDereferencedArgsFromCallStack();
  }

  @PeopleToolsImplementation
  public void Rept() {

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

  @PeopleToolsImplementation
  public void Len() {

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
  @PeopleToolsImplementation
  public void IsModalComponent() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected zero arguments.");
    }
    Environment.pushToCallStack(new PTBoolean(false));
  }

  @PeopleToolsImplementation
  public void CreateRecord() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 1 || (!(args.get(0) instanceof PTRecordLiteral))) {
      throw new OPSVMachRuntimeException("Expected single RecordLiteral arg.");
    }

    final Record recDefn = DefnCache.getRecord(
        ((PTRecordLiteral) args.get(0)).read());

    final PTStandaloneRecord rec = new PTRecordTypeConstraint().allocStandaloneRecord(null, recDefn);

    rec.setDefault();
    Environment.pushToCallStack(rec);
  }

  @PeopleToolsImplementation
  public void CreateRowset() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 1 || (!(args.get(0) instanceof PTRecordLiteral))) {
      throw new OPSVMachRuntimeException("Expected single RecordLiteral arg.");
    }

    final Record recDefn = DefnCache.getRecord(
        ((PTRecordLiteral) args.get(0)).read());

   final PTStandaloneRowset newRowset =
       new PTRowsetTypeConstraint().allocStandaloneRowset(null, recDefn);

    Environment.pushToCallStack(newRowset);
  }

  @PeopleToolsImplementation
  public void CreateArray() {

    /*
     * I am simply calling CreateArrayRept for now,
     * because I saw input for CreateArray(" ", 0) despite
     * the fact that the documentation says all arguments should
     * be the same type. In the future, there will likely be
     * instances where something other than CreateArrayRept
     * should be done.
     */
    CreateArrayRept();
  }

  /**
   * Makes the Rowset object representing the level 0
   * component buffer available to caller.
   */
  @PeopleToolsImplementation
  public void GetLevel0() {
    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected zero arguments.");
    }

    Environment.pushToCallStack(ComponentBuffer.getLevelZeroRowset());
  }

  @PeopleToolsImplementation
  public void CreateArrayRept() {

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
      newArray = new PTArrayTypeConstraint(arr.getDimensions() + 1,
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

  @PeopleToolsImplementation
  public void IsMenuItemAuthorized() {

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
      ((PTMenuLiteral) args.get(0)).read(),
      ((PTMenuBarLiteral) args.get(1)).read(),
      ((PTMenuItemLiteral) args.get(2)).read(),
      ((PTPageLiteral) args.get(3)).read(),
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
    try (final OPSStmt ostmt = StmtLibrary.getStaticSQLStmt(
            "query.PSAUTHITEM_PSOPRCLS_IsMenuItemAuthorized", bindVals);
         final OPSResultSet rs = ostmt.executeQuery()) {
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
    }

    // If no record permitted access for the given actionMode,
    // access to the menu item is not authorized.
    log.debug("IsMenuItemAuthorized: no permissible records found,"
      + " returning False.");
    Environment.pushToCallStack(new PTBoolean(false));
  }

  @PeopleToolsImplementation
  public void MsgGetText() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() < 3) {
      throw new OPSVMachRuntimeException("Expected 3 or more args to "
          + "MsgGetText (this fn supports var args).");
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

    // If message not found in message, return default message provided.
    if (msgSet == null) {
      log.debug("MsgGetText found no msg set with setnbr={}; "
          + "returning default message provided: {}", msgSetNbr,
          ((PTString) args.get(2)).read());
      Environment.pushToCallStack(args.get(2));
      return;
    }

    final int msgNbr;
    if (args.get(1) instanceof PTNumber) {
      msgNbr = ((PTNumber) args.get(1)).readAsInteger();
    } else {
      msgNbr = ((PTInteger) args.get(1)).read();
    }

    // If message not found in message, return default message provided.
    if (!msgSet.hasMsgNbr(msgNbr)) {
      log.debug("MsgGetText found no msg with setnbr={} and msgnbr={}; "
          + "returning default message provided: {}", msgSetNbr, msgNbr,
          ((PTString) args.get(2)).read());
      Environment.pushToCallStack(args.get(2));
      return;
    }

    // Since at least 3 args are guaranteed to be provided to this
    // method, we can create a sublist that will be empty if no bind vals
    // were provided, or if some have, the sublist will contain only those vals.
    final List<PTType> bindVals = args.subList(3, args.size());
    final String[] msgBindVals = new String[bindVals.size()];
    for (int i = 0; i < bindVals.size(); i++) {
      msgBindVals[i] = Environment.getOrDerefPrimitive(bindVals.get(i)).readAsString();
    }

    final String msg = msgSet.getMessage(msgNbr, msgBindVals);
    Environment.pushToCallStack(new PTString(msg));
  }

  @PeopleToolsImplementation
  public void GenerateComponentContentRelURL() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 8) {
      throw new OPSVMachRuntimeException("Expected exactly 8 args to "
          + "GenerateComponentContentRelURL; "
          + "altho var args to this fn are legal (see PT "
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
    url.append(Environment.getName());

    // append portal name (i.e., EMPLOYEE)
    url.append('/').append(((PTString) args.get(0)).read());

    // append node name (i.e., HRMS)
    url.append('/').append(((PTString) args.get(1)).read());

    // append "c" (for component)
    url.append("/c/");

    // append <menu>.<component>.<market>
    url.append(((PTMenuLiteral) args.get(2)).read())
       .append('.').append(((PTComponentLiteral) args.get(4)).read())
       .append('.').append(((PTString) args.get(3)).read());

    // append "?Page=<page>"
    url.append("?Page=").append(((PTPageLiteral) args.get(5)).read());

    // append "&Action=<action>"
    url.append("&Action=").append(((PTString) args.get(6)).read());

    final PTRecord<?, ?> keylistRec = ((PTRecord<?, ?>) args.get(7));

    /*
     * The keylist must be in alphabetical order by key.
     */
    for(final PTReference<? extends PTField> fieldRef :
        keylistRec.getFieldRefsInAlphabeticOrderByFieldName()) {
      // NOTE: trim() is important here; blank values are a single space
      // in PS and should be reduced to the empty string.
      final PTField fld = fieldRef.deref();
      url.append('&').append(fld.getRecordFieldDefn().getFldName()).append('=')
          .append(fld.getValue().readAsString().trim());
    }

    log.debug("GenerateComponentContentURL: From args, generated url: {}",
      url.toString());

    Environment.pushToCallStack(new PTString(url.toString()));
  }

  @PeopleToolsImplementation
  public void Truncate() {

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

  @PeopleToolsImplementation
  public void String() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 1
        || !(args.get(0) instanceof PTInteger)) {
      throw new OPSVMachRuntimeException("Expected exactly 1 arg "
          + "of type PTInteger to String.");
    }

    Environment.pushToCallStack(
        new PTString(((PTInteger) args.get(0)).readAsString()));
  }

  @PeopleToolsImplementation
  public void GetHTMLText() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() == 0
        || !(args.get(0) instanceof PTHTMLLiteral)) {
      throw new OPSVMachRuntimeException("Expected at least 1 arg, "
          + "the first being of type PTHTMLLiteral to GetHTMLText.");
    }

    final HTML html = DefnCache.getHTML(((PTHTMLLiteral) args.get(0)).read());

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

  @PeopleToolsImplementation
  public void Proper() {

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

  @PeopleToolsImplementation
  public void Lower() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 1
        || !(args.get(0) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected exactly 1 arg, "
          + "of type PTString to Lower.");
    }

    Environment.pushToCallStack(
        new PTString(((PTString) args.get(0)).read().toLowerCase()));
  }

  @PeopleToolsImplementation
  public void GetSQL() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() == 0
        || !(args.get(0) instanceof PTSQLLiteral)) {
      throw new OPSVMachRuntimeException("Expected at least 1 arg, "
          + "the first being of type PTSQLLiteral to GetSQL.");
    }

    final SQL sql = DefnCache.getSQL(((PTSQLLiteral) args.get(0)).read());
    final PTPrimitiveType[] bindVals = new PTPrimitiveType[args.size() - 1];

    for (int i = 1; i < args.size(); i++) {
      PTType bVal = args.get(i);
      if (bVal instanceof PTPrimitiveType) {
        bindVals[i - 1] = (PTPrimitiveType) bVal;
      } else if (bVal instanceof PTField) {
        bindVals[i - 1] = ((PTField) bVal).getValue();
      } else if (bVal instanceof PTRecordFieldSpecifier) {
        bindVals[i - 1] = ((PTRecordFieldSpecifier) bVal)
            .resolveInCBufferContext().deref().getValue();
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

  @PeopleToolsImplementation
  public void SQLExec() {

    // Do not get all args in their dereferenced form; outvars will likely
    // be references and we need to preserve that.
    List<PTType> args = Environment.getArgsFromCallStack();

    if(args.size() == 0
        || !(args.get(0) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected at least 1 arg, "
          + "the first being of type PTString containing SQL text to "
          + "execute; (NOTE: it is possible for a SQL object to be passed "
          + "as the first arg instead of a string).");
    }

    String sqlCmd = ((PTString) args.get(0)).read();

    // If this statement begins with "Select", lowercase the keyword and add
    // a space (no clue why, but this is what PS does).
    if (sqlCmd.startsWith("Select")) {
      sqlCmd = sqlCmd.replaceFirst("Select", "SELECT ");
    }

    // Note: this regex uses positve lookbehind and lookahead.
    Pattern bindIdxPattern = Pattern.compile("(?<=\\s*)(:(\\d+))(?=\\s?)");
    Matcher bindIdxMatcher = bindIdxPattern.matcher(sqlCmd);
    List<String> bindVals = new ArrayList<String>();

    int maxArgIdx = 0;

    while (bindIdxMatcher.find()) {

      final int argIdx = Integer.parseInt(bindIdxMatcher.group(2));

      if (argIdx >= args.size()) {
        throw new OPSVMachRuntimeException("Expected another bind expr "
            + "but reached the end of the provided argument list.");
      }

      // Bind exprs start with argument at index 1; bind indices also start
      // at index 1, so we can use the value of argIdx directly here.
      final PTPrimitiveType bindExpr = Environment.getOrDerefPrimitive(
          args.get(argIdx));
      bindVals.add(bindExpr.readAsString());

      if (argIdx > maxArgIdx) {
        maxArgIdx = argIdx;
      }
    }

    // Replace all numbered bind indices with question marks.
    bindIdxMatcher.reset();
    sqlCmd = bindIdxMatcher.replaceAll("?");

    try (final OPSStmt ostmt = new OPSStmt(sqlCmd,
            bindVals.toArray(new String[bindVals.size()]),
                OPSStmt.EmissionType.ENFORCED);
         final OPSResultSet rs = ostmt.executeQuery()) {

      // The index of the first argument to be used as an out var is
      // 1 greater than the index of the last argument used as a bind expr.
      int nextOutVarIdx = maxArgIdx + 1;

      /*
       * SQLExec only selects the first row of a result set; it discards
       * any rows after that. Additionally, the result set may legitimately
       * be empty.
       */
      if (rs.next()) {
        log.debug("Record returned by SQLExec; writing selected fields to out vars...");

        for (int colIdx = 1; colIdx <= rs.getColumnCount(); colIdx++) {

          if (nextOutVarIdx >= args.size()) {
            throw new OPSVMachRuntimeException("Expected an out var "
                + "but reached the end of the provided argument list.");
          }

          final PTType outVar = args.get(nextOutVarIdx++);
          final PTPrimitiveType dbVal = rs.getTypeCompatibleValue(colIdx,
              outVar.getOriginatingTypeConstraint());

          Environment.assign(outVar, dbVal);
        }
      } else {
        /*
         * If no row was returned, SQLExec blanks/NULLs any outvars passed
         * as arguments. The exception to this involves *NON-WORK* Component
         * Processor fields, these are *not* blanked/nullified.
         */
        for (int i = nextOutVarIdx; i < args.size(); i++) {
          final PTType outVar = args.get(i);
          final PTType outVarDerefed = Environment.getOrDeref(outVar);
          log.debug("In SQLExec, about to blank/nullify field due to absence "
              + "of record returned by SQLExec: {}", outVar);
          if (outVarDerefed instanceof PTPrimitiveType) {
            ((PTPrimitiveType) outVarDerefed).setBlank();
          } else {
            if (outVarDerefed instanceof PTField) {
              throw new OPSVMachRuntimeException("TODO: Need to blank a field; "
                  + "YOU MUST FIRST DETERMINE "
                  + "if it's a non-work component processor field, these do not get "
                  + "blanked; see PT documentation for SQLExec: "
                  + "http://docs.oracle.com/cd/E13292_01/pt849pbr0/eng/psbooks/tpcl/chapter.htm?File=tpcl/htm/tpcl02.htm");
            } else {
              // Do not pass derefed outvar as l-value, assgmt will fail.
              Environment.assign(outVar, new PTNull(
                  outVar.getOriginatingTypeConstraint()));
            }
          }
        }
      }
    }

    // SQLExec returns True if execution completed successfully.
    Environment.pushToCallStack(new PTBoolean(true));
  }

  @PeopleToolsImplementation
  public void GetField() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 1
        || !(args.get(0) instanceof PTRecordFieldSpecifier)) {
      throw new OPSVMachRuntimeException("Expected exactly 1 arg "
          + "of type PTRecordFieldSpecifier to GetField.");
    }

    Environment.pushToCallStack(
        ((PTRecordFieldSpecifier) args.get(0)).resolveInCBufferContext().deref());
  }

  @PeopleToolsImplementation
  public void GetRecord() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if (args.size() == 0) {
      Environment.pushToCallStack(this.interpretSupervisor
          .getNearestCBufferRecordInContext());
    } else if(args.size() == 1
        && args.get(0) instanceof PTRecordLiteral) {

      final PTType resEntity = this.interpretSupervisor
          .resolveContextualCBufferRecordReference(
              ((PTRecordLiteral) args.get(0)).read());
      if (resEntity == null || !(resEntity instanceof PTRecord)) {
        throw new OPSVMachRuntimeException("Failure in GetRecord: expected provided identifier ("
            + args.get(0) + ") to resolve to record but instead resolved to: " + resEntity);
      }

      Environment.pushToCallStack(resEntity);
    } else {
      throw new OPSVMachRuntimeException("Expected exactly 0 args or 1 arg "
          + "of type PTRecord to GetRecord.");
    }
  }

  @PeopleToolsImplementation
  public void Gray() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 1
        || !(args.get(0) instanceof PTRecordFieldSpecifier)) {
      throw new OPSVMachRuntimeException("Expected exactly 1 arg "
          + "of type PTRecordFieldSpecifier to Gray.");
    }

    ((PTRecordFieldSpecifier) args.get(0)).resolveInCBufferContext()
        .deref().grayOut();
  }

  /**
   * IMPORTANT NOTE: As I've implemented it here, this function generates
   * an absolute rather than relative URL. There are very likely issues
   * with the final URL reproduced, due to lack of info at this time.
   * TODO(mquinn): Keep this in mind in the event of issues.
   */
  @PeopleToolsImplementation
  public void GenerateScriptRelativeURL() {

    List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 6) {
      throw new OPSVMachRuntimeException("Expected exactly 6 args to "
          + "GenerateScriptRelativeURL; altho var args to this fn are legal (see PT "
          + "reference for defn) they are not supported at this time.");
    }

    if(!(args.get(0) instanceof PTString)
        || !(args.get(1) instanceof PTString)
        || !(args.get(2) instanceof PTRecordLiteral)
        || !(args.get(3) instanceof PTFieldLiteral)
        || !(args.get(4) instanceof PTString)
        || !(args.get(5) instanceof PTString)) {
      throw new OPSVMachRuntimeException("The arguments provided to "
          + "GenerateScriptRelativeURL do not match the expected types.");
    }

    final StringBuilder url = new StringBuilder("/psp/");

    // append PS environment name (i.e., XENCSDEV, ENTCSDEV, etc.)
    url.append(Environment.getName());

    // append portal name (i.e., EMPLOYEE)
    url.append('/').append(((PTString) args.get(0)).read());

    // append node name (i.e., HRMS)
    url.append('/').append(((PTString) args.get(1)).read());

    // append "s" (for script)
    url.append("/s/");

    // append <recname>.<fldname>
    url.append(((PTRecordLiteral) args.get(2)).read())
       .append('.').append(((PTFieldLiteral) args.get(3)).read());

    // append ".<event>"
    url.append('.').append(((PTString) args.get(4)).read());

    // append ".<funcname>"
    url.append('.').append(((PTString) args.get(5)).read());

    log.debug("GenerateScriptRelativeURL: From args, generated url: {}",
      url.toString());

    Environment.pushToCallStack(new PTString(url.toString()));
  }

  @PeopleToolsImplementation
  public void GetGrid() {

    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if (args.size() != 2
        || !(args.get(0) instanceof PTPageLiteral)
        || !(args.get(1) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected a PageLiteral and "
          + "String as args to GetGrid.");
    }

    final PTGrid grid = (new PTGridTypeConstraint()).alloc(
        ((PTPageLiteral) args.get(0)), ((PTString) args.get(1)));
    Environment.pushToCallStack(grid);
  }

  /**
   * IMPORTANT: According to the PT documentation, passing a scroll
   * name to GetRowset returns the *child* rowset of the rowset in
   * current context with a primary record name == to the scroll name.
   * It does not reach up the rowset hierarchy indefinitely, unlike
   * GetRecord().
   */
  @PeopleToolsImplementation
  public void GetRowset() {

    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if (args.size() != 1
        || !(args.get(0) instanceof PTScrollLiteral)) {
      throw new OPSVMachRuntimeException("Expected a single ScrollLiteral "
          + "arg to GetRowset.");
    }

    final PTScrollLiteral scrollName = ((PTScrollLiteral) args.get(0));
    final PTRowset resRowset = this.interpretSupervisor
        .resolveContextualCBufferScrollReference(scrollName);
    if (resRowset == null) {
      throw new OPSVMachRuntimeException("Failure in GetRowset; no rowset "
          + "found for scroll name: " + scrollName);
    }

    Environment.pushToCallStack(resRowset);
  }

  @PeopleToolsImplementation
  public void Weekday() {

    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 1
        || !(args.get(0) instanceof PTDate)) {
      throw new OPSVMachRuntimeException("Expected exactly 1 arg "
          + "of type PTDate to Weekday.");
    }

    final Calendar dtCal = Calendar.getInstance();
    dtCal.setTime(((PTDate) args.get(0)).read());

    // PS maps Sunday to 1 and Saturday to 7.
    final int psDayOfWeek = dtCal.get(Calendar.DAY_OF_WEEK);
    Environment.pushToCallStack(new PTInteger(psDayOfWeek));
  }

  /**
   * Note; negative delta values are allowed here (both in
   * technical sense and in the PT defn/implementation.
   */
  @PeopleToolsImplementation
  public void AddToDate() {

    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if(args.size() != 4
        || !(args.get(0) instanceof PTDate)
        || !(args.get(1) instanceof PTInteger)
        || !(args.get(2) instanceof PTInteger)
        || !(args.get(3) instanceof PTInteger)) {
      throw new OPSVMachRuntimeException("Expected exactly 4 args "
          + "(1 PTDate, the rest PTInteger) to AddToDate.");
    }

    final Calendar dtCal = Calendar.getInstance();
    dtCal.setTime(((PTDate) args.get(0)).read());

    final int yearDelta = ((PTInteger) args.get(1)).read();
    final int monthDelta = ((PTInteger) args.get(2)).read();
    final int dayDelta = ((PTInteger) args.get(3)).read();

    // Order matters here; see PT documentation; when adding months
    // to the date, "if the date provided
    // is the last day of the month, and the next month is shorter, the
    // returned result is the last day of the next month."
    dtCal.add(Calendar.DATE, dayDelta);
    dtCal.add(Calendar.MONTH, monthDelta);
    dtCal.add(Calendar.YEAR, yearDelta);

    Environment.pushToCallStack(new PTDate(dtCal.getTime()));
  }

  // Not implementing for now.
  @PeopleToolsImplementation
  public void SetCursorPos() {
    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();
  }

  @PeopleToolsImplementation
  public void RTrim() {

    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if (args.size() != 1
        || !(args.get(0) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected a single String "
          + "arg to RTrim.");
    }

    String trimmedStr = ((PTString) args.get(0)).read();
    while (trimmedStr.length() > 0
        && trimmedStr.charAt(trimmedStr.length() - 1) == ' ') {
      trimmedStr = trimmedStr.substring(0, trimmedStr.length() - 1);
    }

    Environment.pushToCallStack(new PTString(trimmedStr));
  }

  @PeopleToolsImplementation
  public void Char() {

    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if (args.size() != 1
        || !(args.get(0) instanceof PTInteger)) {
      throw new OPSVMachRuntimeException("Expected a single Integer "
          + "arg to Char.");
    }

    final int n = ((PTInteger) args.get(0)).read();
    Environment.pushToCallStack(new PTString(Character.toString((char) n)));
  }

  @PeopleToolsImplementation
  public void GetUserOption() {

    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if (args.size() != 2
        || !(args.get(0) instanceof PTString)
        || !(args.get(1) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected 2 String "
          + "args to GetUserOption.");
    }

    final String[] bindVals = {
      ((PTString) args.get(0)).read(),
      ((PTString) args.get(1)).read()
    };

    try (final OPSStmt ostmt = StmtLibrary.getStaticSQLStmt(
            "query.GetUserOption", bindVals);
         final OPSResultSet rs = ostmt.executeQuery()) {

      if (rs.next()) {
        Environment.pushToCallStack(
            new PTString(rs.getString("USER_OPTION_VALUE")));
      } else {
        throw new OPSVMachRuntimeException("Expected a single record in GetUserOption "
            + "result set, none returned.");
      }

      if (rs.next()) {
        throw new OPSVMachRuntimeException("Expected a single record in GetUserOption "
            + "result set, multiple returned.");
      }
    }
  }
}
