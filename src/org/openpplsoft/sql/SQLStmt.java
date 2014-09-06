/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.sql;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.runtime.*;
import org.openpplsoft.trace.IEmission;

/**
 * Represents a SQL statement, emitted either by
 * PeopleSoft or the OPS runtime.
 */
public class SQLStmt implements IEmission {

  private static Logger log = LogManager.getLogger(SQLStmt.class.getName());

  protected String sql;
  protected Map<Integer, String> bindVals;

  /**
   * @param s the SQL statement to represent; note that
   *    bind tokens should be non-numeric (i.e., "?")
   *    to ensure equals() works correctly.
   */
  public SQLStmt(final String s) {
    this.sql = s;
    this.bindVals = new HashMap<Integer, String>();
  }

  /**
   * @return bind values for this SQL statement
   */
  public Map<Integer, String> getBindVals() {
    return this.bindVals;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof SQLStmt)) {
      return false;
    }

    final SQLStmt otherStmt = (SQLStmt) obj;
    if (!this.sql.equals(otherStmt.sql)
        || this.bindVals.size() != otherStmt.bindVals.size()) {
      return false;
    }

    //log.debug("SQL stmt is: {}", this.sql);

    // Ensure bind value indices and values match
    // those in the other statement.
    for (Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {

      //log.debug("Checking key {} with value {}.", cursor.getKey(),
      //    cursor.getValue());

      if (!cursor.getValue().equals(
          otherStmt.bindVals.get(cursor.getKey()))) {
        //log.debug("Other stmt for key {} has value {}.",
        //    cursor.getKey(), otherStmt.bindVals.get(cursor.getKey()));
        //throw new OPSVMachRuntimeException("Bindings differ.");
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    String str = this.sql + "\n";
    for (Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {
      str = str + ":" + cursor.getKey() + " = " + cursor.getValue() + "\n";
    }
    return str;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 5, HCB_MULTIPLIER = 29;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.sql);

    for (Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {
      hbc.append(cursor.getValue());
    }
    return hbc.toHashCode();
  }
}

