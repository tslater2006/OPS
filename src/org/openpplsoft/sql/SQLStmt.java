/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.sql;

import java.sql.*;
import java.util.*;
import java.util.regex.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.trace.IEmission;
import org.apache.logging.log4j.*;

public class SQLStmt implements IEmission {
  public String sql;
  public Map<Integer, String> bindVals;

  private static Logger log = LogManager.getLogger(SQLStmt.class.getName());

  /**
   * @sql : bind tokens should be non-numeric (i.e., "?")
   *        to ensure equals() works correctly.
   */
  public SQLStmt(String sql) {
    this.sql = sql;
    this.bindVals = new HashMap<Integer, String>();
  }

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof SQLStmt))
      return false;

    SQLStmt otherStmt = (SQLStmt)obj;
    if(!this.sql.equals(otherStmt.sql) ||
      this.bindVals.size() != otherStmt.bindVals.size()) {
      return false;
    }

    //log.debug("SQL stmt is: {}", this.sql);

    // Ensure bind value indices and values match those in the other statement.
    for(Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {

      //log.debug("Checking key {} with value {}.", cursor.getKey(), cursor.getValue());

      if(!cursor.getValue().equals(
          otherStmt.bindVals.get(cursor.getKey()))) {
        //log.debug("Other stmt for key {} has value {}.", cursor.getKey(), otherStmt.bindVals.get(cursor.getKey()));
        //throw new OPSVMachRuntimeException("Bindings differ.");
        return false;
      }
    }
    return true;
  }

  public String toString() {
    String str = sql + "\n";
    for(Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {
      str = str + ":" + cursor.getKey() + " = " + cursor.getValue() + "\n";
    }
    return str;
  }
}

