/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.openpplsoft.runtime.Environment;
import org.openpplsoft.runtime.OPSVMachRuntimeException;
import org.openpplsoft.sql.OPSResultSet;
import org.openpplsoft.sql.OPSStmt;
import org.openpplsoft.sql.StmtLibrary;

public final class PTDateTime extends PTPrimitiveType<DateTime> {

  private static final Logger log = LogManager.getLogger(
      PTDateTime.class.getName());

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

  @Override
  protected DateTime primitiveToRaw(final PTPrimitiveType src) {
    if (!(src instanceof PTDateTime)) {
      throw new OPSDataTypeException("Expected src to be PTDateTime.");
    }
    return ((PTDateTime) src).read();
  }

  @Override
  public String readAsString() {
    if (this.value == null) {
      return null;
    }
    final DateTimeFormatter dtf = DateTimeFormat.forPattern(PS_DATE_TIME_FMT1);
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
    final OPSResultSet rs = ostmt.executeQuery();

    rs.next();

    // TODO: MAKE THIS CONDITIONAL BASED ON TRACEFILE VERIFICATION
    // Overriding the datetime with that on which the tracefile was generated
    // is only relevant when verifying against a tracefile in the first place.
    //this.write(new DateTime(rs.getTimestamp("sysdate")));
    this.write((DateTime) Environment.getSystemVar("%Time").read());

    rs.close();
    ostmt.close();
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
