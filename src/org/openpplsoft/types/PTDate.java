/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.*;

import java.sql.ResultSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

public final class PTDate extends PTPrimitiveType<Date> {

  private static PTTypeConstraint<PTDate> dateTc;
  private static final String PS_DATE_FORMAT = "yyyy-MM-dd";

  private static Logger log = LogManager.getLogger(PTDate.class.getName());

  static {
    dateTc = new PTTypeConstraint<PTDate>(PTDate.class);
  }

  public PTDate(final Date initialValue) {
    super(dateTc);

    // Do not check for null before assigning; PTDate values use null for blank.
    this.value = initialValue;
  }

  public PTDate(final String initialValue) {
    super(dateTc);

    if (initialValue == null) {
      // If the value provided is null, set the field value to null.
      this.value = null;
    } else {
      try {
        // A non-null string must match the PeopleSoft date format.
        final DateFormat df = new SimpleDateFormat(PS_DATE_FORMAT);
        this.value = df.parse(initialValue);
      } catch (final ParseException pe) {
        throw new OPSVMachRuntimeException(pe.getMessage(), pe);
      }
    }
  }

  public PTDate(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public static PTTypeConstraint<PTDate> getTc() {
    return dateTc;
  }

  @Override
  public String readAsString() {
    final DateFormat df = new SimpleDateFormat(PS_DATE_FORMAT);
    return df.format(this.value);
  }

  public void copyValueFrom(PTPrimitiveType src) {
    if (!(src instanceof PTDate)) {
      throw new OPSDataTypeException("Expected src to be PTDate.");
    }
    this.write(((PTDate) src).read());
  }

  public void setBlank() {
    // In App Designer debugging, PT shows "<no value>" for newly created,
    // uninitialized date variables, so using a Java null to simulate.
    this.value = null;
  }

  public boolean isBlank() {
    return (this.value == null);
  }

  public void writeSYSDATE() {
    final OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.SYSDATE", new String[]{});
    ResultSet rs = null;
    try {
      rs = ostmt.executeQuery();
      rs.next();
      this.write(new Date(rs.getTimestamp("sysdate").getTime()));
    } catch (final java.sql.SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    } finally {
      try {
        if (rs != null) { rs.close(); }
        if (ostmt != null) { ostmt.close(); }
      } catch (final java.sql.SQLException sqle) {
        log.warn("Unable to close ostmt and/or rs in finally block.");
      }
    }
  }

  public PTBoolean isEqual(PTPrimitiveType op) {
    throw new OPSDataTypeException("isEqual not implemented for " +
      "Date.");
  }

  public PTBoolean isGreaterThan(PTPrimitiveType op) {
    throw new OPSDataTypeException("isGreaterThan not implemented for " +
      "Date.");
  }

  public PTBoolean isGreaterThanOrEqual(PTPrimitiveType op) {
    if(!(op instanceof PTDate)) {
      throw new OPSDataTypeException("Expected op to be PTDate.");
    }
    if(this.value.compareTo(((PTDate)op).read()) >= 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public PTBoolean isLessThan(PTPrimitiveType op) {
    throw new OPSDataTypeException("isLessThan is not supported for " +
      "Date.");
  }

  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    if(!(op instanceof PTDate)) {
      throw new OPSDataTypeException("Expected op to be PTDate.");
    }
    if(this.value.compareTo(((PTDate)op).read()) <= 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PTDate))
      return false;
    PTDate other = (PTDate)obj;
    if(this.read().equals(other.read())) {
      return true;
    }
    return false;
  }
}
