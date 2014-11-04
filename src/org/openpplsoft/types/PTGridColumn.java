/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

/**
 * Represents a PeopleTools GridColumn object.
 */
public final class PTGridColumn extends PTObjectType {

  private static Logger log = LogManager.getLogger(PTGridColumn.class.getName());

  private static Map<String, Method> ptMethodTable;

  static {
    final String PT_METHOD_PREFIX = "PT_";

    // cache pointers to PeopleTools Record methods.
    final Method[] methods = PTGridColumn.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  public PTGridColumn(final PTGridColumnTypeConstraint origTc) {
    super(origTc);
  }

  @Override
  public PTType dotProperty(final String s) {
    return null;
  }

  @Override
  public Callable dotMethod(final String s) {
    if (ptMethodTable.containsKey(s)) {
      return new Callable(ptMethodTable.get(s), this);
    }
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    return b.toString();
  }
}
