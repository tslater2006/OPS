/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.trace.*;

/**
 * Represents a PeopleTools row definition; contains
 * 1 to n child records and 0 to m child rowsets.
 */
public final class PTStandaloneRow extends PTRow<PTStandaloneRowset, PTStandaloneRecord> {

  private static Map<String, Method> ptMethodTable;

  private static final Logger log = LogManager.getLogger(PTStandaloneRow.class.getName());

  static {
    final String PT_METHOD_PREFIX = "PT_";
    // cache pointers to PeopleTools Row methods.
    final Method[] methods = PTStandaloneRow.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  public PTStandaloneRow(final PTRowTypeConstraint origTc,
      final PTStandaloneRowset pRowset) {
    super(origTc);
    this.parentRowset = pRowset;

    // Create records for each record defn registered on the parent rowset.
    for(final Record recDefn : pRowset.getRegisteredRecordDefns()) {
      this.recordMap.put(recDefn.getRecName(),
          new PTRecordTypeConstraint().allocStandaloneRecord(this, recDefn));
    }
  }

  @Override
  public String toString() {
    return "[STANDALONE]" + super.toString();
  }
}
