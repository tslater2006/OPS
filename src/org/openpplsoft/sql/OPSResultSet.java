/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.sql;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.runtime.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.types.*;

/**
 * Wrapper for all ResultSets opened by
 * the OPS runtime.
 */
public class OPSResultSet implements AutoCloseable {

  private static Logger log =
      LogManager.getLogger(OPSResultSet.class.getName());

  private final ResultSet rs;

  public OPSResultSet(final ResultSet rs) {
    this.rs = rs;
  }

  public Blob getBlob(final String colName) {
    try {
      return rs.getBlob(colName);
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public String getClobAsString(final int colIdx) {
    try {
      final String clobAsStr = this.clobToString(rs.getClob(colIdx));
      log.debug("[OPSResultSet] clob as string: {}", clobAsStr);
      return clobAsStr;
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public String getClobAsString(final String colName) {
    try {
      final String clobAsStr = this.clobToString(rs.getClob(colName));
      log.debug("[OPSResultSet] clob as string: {}", clobAsStr);
      return clobAsStr;
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  private String clobToString(final Clob clob) {

    try (final BufferedReader clobReader = new BufferedReader(
            clob.getCharacterStream())) {
      if (clob.length() > (long) Integer.MAX_VALUE) {
        throw new OPSVMachRuntimeException("Clob is longer than maximum possible "
            + "length of String; unable to store entire value in String unless "
            + "extra logic is added here.");
      }

      final StringBuilder sb = new StringBuilder();
      String line;
      while(null != (line = clobReader.readLine())) {
          sb.append(line);
      }

      clob.free();
      return sb.toString();
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    } catch (final IOException ioe) {
      throw new OPSVMachRuntimeException(ioe.getMessage(), ioe);
    }
  }

  public String getBlobAsString(final String colName) {
    try {

      final Blob blob = rs.getBlob(colName);

      if (blob.length() > (long) Integer.MAX_VALUE) {
        throw new OPSVMachRuntimeException("Length of Blob "
            + "is greater than Integer.MAX_VALUE; extra logic is required here "
            + "in order to ensure we are retrieving the entire value.");
      }

      final StringBuilder sb = new StringBuilder();
      final byte[] arr = blob.getBytes(1, (int) blob.length());
      for (final byte b : arr) {
        // Skip null bytes, there are a ton of these in PS BLOB fields.
        if (b > 0) {
          sb.append(Character.toString((char) b));
        }
      }

      blob.free();
      log.debug("[OPSResultSet] blob as string: {}", sb.toString());
      return sb.toString();
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public Timestamp getTimestamp(final String colName) {
    try {
      final Timestamp ts = rs.getTimestamp(colName);
      log.debug("[OPSResultSet] timestamp: {}", ts);
      return ts;
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public int getInt(final String colName) {
    try {
      final int integer = rs.getInt(colName);
      log.debug("[OPSResultSet] int: {}", integer);
      return integer;
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public int getInt(final int colIdx) {
    try {
      final int integer = rs.getInt(colIdx);
      log.debug("[OPSResultSet] int: {}", integer);
      return integer;
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public String getString(final String colName) {
    try {
      final String str = rs.getString(colName);
      log.debug("[OPSResultSet] str: {}", str);
      return str;
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public String getString(final int colIdx) {
    try {
      final String str = rs.getString(colIdx);
      log.debug("[OPSResultSet] str: {}", str);
      return str;
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public BigDecimal getBigDecimal(final String colName) {
    try {
      final BigDecimal bd = rs.getBigDecimal(colName);
      log.debug("[OPSResultSet] big decimal: {}", bd);
      return bd;
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public BigDecimal getBigDecimal(final int colIdx) {
    try {
      final BigDecimal bd = rs.getBigDecimal(colIdx);
      log.debug("[OPSResultSet] big decimal: {}", bd);
      return bd;
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public boolean next() {
    try {
      return rs.next();
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public boolean isFirst() {
    try {
      return rs.isFirst();
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public int getColumnCount() {
    try {
      return this.rs.getMetaData().getColumnCount();
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public String getColumnName(final int idx) {
    try {
      return this.rs.getMetaData().getColumnName(idx);
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public String getColumnTypeName(final int idx) {
    try {
      return this.rs.getMetaData().getColumnTypeName(idx);
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public void readIntoRecord(final PTRecord<?,?> recObj) {
    for (int i = 1; i <= this.getColumnCount(); i++) {
      final String colName = this.getColumnName(i);
      final PTReference<? extends PTField> fldRef = recObj.getFieldRef(colName);
      final PTPrimitiveType dbVal = this.coerceDbValToFldVal(i, fldRef.deref());
      Environment.assign(fldRef, dbVal);
    }
  }

  /**
   * There are times when the result set may contain fields that do not
   * exist on the record you want to read into (i.e., using Select method on Rowset
   * class with a record that differs from the primary record defn of the Rowset).
   * If you use the normal "readIntoRecord", an exception will be thrown
   * when such fields are the subject of an attempted write. If you use this method,
   * those fields will simply be skipped.
   */
  public void readIntoRecordDefinedFieldsOnly(final PTRecord<?,?> recObj) {
    final Record recDefn = recObj.getRecDefn();
    for (int i = 1; i <= this.getColumnCount(); i++) {
      final String colName = this.getColumnName(i);
      if (recDefn.hasField(colName)) {
        final PTReference<? extends PTField> fldRef = recObj.getFieldRef(colName);
        final PTPrimitiveType dbVal = this.coerceDbValToFldVal(i, fldRef.deref());
        Environment.assign(fldRef, dbVal);
      }
    }
  }

  public void readNamedColumnIntoField(final PTField fldObj, final String colName) {

    for (int i = 1; i <= this.getColumnCount(); i++) {
      if (this.getColumnName(i).equals(colName)) {
        final PTPrimitiveType dbVal = this.coerceDbValToFldVal(i, fldObj);

        // We cannot use Environment.assign() b/c that method requires that
        // the destination be a reference. Since we know fields contain only
        // primitives, and that db values are always primitives, we do the
        // assignment manually here.
        fldObj.getValue().copyValueFrom(dbVal);
      }
    }
  }

  /**
   * This method must be used whenever a Field's value is
   * being read in from the database. This method takes into
   * account the FieldType of the Field being written to; this
   * is the PeopleTools-specific field type that is database agnostic
   * and has various conventions surrounding it (i.e., regarding blanks, nulls,
   * etc.). See the book "PeopleSoft for the Oracle DBA", pages 136-137 for
   * more. Note that this method is evolving and is not final.
   */
  private PTPrimitiveType coerceDbValToFldVal(final int colIdx,
      final PTField fld) {
    final RecordField fldDefn = fld.getRecordFieldDefn();
    switch (fldDefn.getFieldType()) {
      case CHARACTER:
        /**
         * PeopleSoft stores non-required blank CHARACTER fields
         * as ' ' in the database. These must be translated to
         * their empty string runtime equivalent.
         */
        final String dbStr = this.getString(colIdx);
        if (!fldDefn.isRequired() && dbStr.equals(" ")) {
          return new PTString("");
        }
      default:
        return this.getTypeCompatibleValue(colIdx,
            fld.getValue().getOriginatingTypeConstraint());
    }
  }

  /**
   * Retrieves a value from the given column name that is type
   * compatible with the provided type constraint.
   * IMPORTANT NOTE: This method calls the variants of the get____()
   * methods on ResultSet that accept integer column indices as arguments, rather
   * than String column names. This should be done whenever possible, because
   * sometimes result set column names can differ from those that are expected
   * on PT defintions (i.e., due to aliases in queries).
   */
  public PTPrimitiveType getTypeCompatibleValue(final int colIdx,
      PTTypeConstraint tc) {

    final String colTypeName = this.getColumnTypeName(colIdx);

    log.debug("Getting type compatible value from column named {}; tc = {}",
        this.getColumnTypeName(colIdx), tc);

    if (tc instanceof PTAnyTypeConstraint) {
      if (colTypeName.equals("CHAR")) {
        return new PTChar(this.getString(colIdx));
      } else if (colTypeName.equals("VARCHAR2")) {
        return new PTString(this.getString(colIdx));
      }
    } else if (tc.isUnderlyingClassEqualTo(PTChar.class)) {
      if (colTypeName.equals("CHAR") || colTypeName.equals("VARCHAR2")) {
        return new PTChar(this.getString(colIdx));
      }
    } else if (tc.isUnderlyingClassEqualTo(PTString.class)) {
      if (colTypeName.equals("CLOB")) {
        return new PTString(this.getClobAsString(colIdx));
      } else if (colTypeName.equals("CHAR") || colTypeName.equals("VARCHAR2")) {
        return new PTString(this.getString(colIdx));
      }
    } else if (tc.isUnderlyingClassEqualTo(PTNumber.class)) {
      if(colTypeName.equals("NUMBER")) {
        return new PTNumber(this.getBigDecimal(colIdx));
      }
    } else if (tc.isUnderlyingClassEqualTo(PTInteger.class)) {
      if(colTypeName.equals("NUMBER")) {
        return new PTInteger(this.getInt(colIdx));
      }
    } else if (tc.isUnderlyingClassEqualTo(PTDate.class)) {
      if (colTypeName.equals("VARCHAR2")) {
        return new PTDate(this.getString(colIdx));
      }
    } else if (tc.isUnderlyingClassEqualTo(PTDateTime.class)) {
      if(colTypeName.equals("VARCHAR2") || colTypeName.equals("TIMESTAMP")) {
        return new PTDateTime(this.getString(colIdx));
      }
    }

    throw new OPSVMachRuntimeException("Unable to get type compatible value from "
        + "OPSResultSet; no valid combination exists for the database column type ("
        + colTypeName + ") of the provided field and the provided type constraint: "
        + tc);
  }

  /**
   * Closes the underlying ResultSet.
   * @throws SQLException if closing of underlying ResultSet
   *    results in an error.
   */
  @Override
  public void close() {
    try {
      rs.close();
    } catch (final SQLException sqle) {
      log.warn("Failed to close ResultSet.", sqle);
    }
  }
}

