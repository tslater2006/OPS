/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.*;
import java.sql.ResultSet;

import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class PTDateTime extends PTPrimitiveType<DateTime> {

  private static final Logger log = LogManager.getLogger(PTDateTime.class.getName());

  private static PTTypeConstraint<PTDateTime> dateTimeTc;
  private static final String PS_DATE_TIME_FMT1 = "yyyy-MM-dd-HH.mm.ss.SSSSSS";
  private static final String PS_DATE_TIME_FMT2 = "yyyy-MM-dd HH:mm:ss";

  static {
    dateTimeTc = new PTTypeConstraint<PTDateTime>(PTDateTime.class);
  }

  public PTDateTime(final String initialValue) {
    super(dateTimeTc);

    if (initialValue == null) {
      throw new OPSVMachRuntimeException("Failed to initialize new PTDateTime; "
         + "initial value is null.");
    }

    try {
      final DateTimeFormatter dtf = DateTimeFormat.forPattern(PS_DATE_TIME_FMT1);
      this.value = dtf.parseDateTime(initialValue);
    } catch (final java.lang.IllegalArgumentException iae) {
      final DateTimeFormatter dtf = DateTimeFormat.forPattern(PS_DATE_TIME_FMT2);
      this.value = dtf.parseDateTime(initialValue);
    }
  }

  public PTDateTime(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public static PTTypeConstraint<PTDateTime> getTc() {
    return dateTimeTc;
  }

  public void copyValueFrom(PTPrimitiveType src) {
    if (!(src instanceof PTDateTime)) {
      throw new OPSDataTypeException("Expected src to be PTDateTime.");
    }
    this.write(((PTDateTime) src).read());
  }

  @Override
  public String readAsString() {
    final DateTimeFormatter dtf = DateTimeFormat.forPattern(PS_DATE_TIME_FMT2);
    return dtf.print(this.value);
  }

  public void setBlank() {
    // In App Designer debugging, PT shows "<no value>" for newly created,
    // uninitialized date variables, so using a Java null to simulate.
    this.value = null;
  }

  @Override
  public boolean isBlank() {
    return (this.value == null);
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
    if(!(op instanceof PTDateTime)) {
      throw new OPSDataTypeException("Expected op to be PTDateTime.");
    }
    if(this.value.compareTo(((PTDateTime)op).read()) >= 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public PTBoolean isLessThan(PTPrimitiveType op) {
    throw new OPSDataTypeException("isLessThan is not supported for " +
      "Date.");
  }

  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    if(!(op instanceof PTDateTime)) {
      throw new OPSDataTypeException("Expected op to be PTDateTime.");
    }
    if(this.value.compareTo(((PTDateTime)op).read()) <= 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public void writeSYSDATE() {
    final OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.SYSDATE", new String[]{});
    ResultSet rs = null;
    try {
      rs = ostmt.executeQuery();
      rs.next();
      this.write(new DateTime(rs.getTimestamp("sysdate")));
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

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PTDateTime))
      return false;

    PTDateTime other = (PTDateTime)obj;
    if(this.read().equals(other.read())) {
      return true;
    }
    return false;
  }
}
