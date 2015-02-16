/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;
import java.util.*;
import java.sql.*;

import org.openpplsoft.runtime.*;
import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.pt.pages.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.trace.*;
import org.openpplsoft.pt.peoplecode.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PTStandaloneField extends PTField<PTStandaloneRecord> {

  private static Logger log = LogManager.getLogger(
      PTStandaloneField.class.getName());

  private static Map<String, Method> ptMethodTable;

  static {
    final String PT_METHOD_PREFIX = "PT_";

    ptMethodTable = new HashMap<String, Method>();
    final Class[] classes = new Class[]{PTField.class, PTStandaloneField.class};

    for (final Class cls : classes) {
      final Method[] methods = cls.getMethods();
      for (Method m : methods) {
        if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
          ptMethodTable.put(m.getName().substring(
              PT_METHOD_PREFIX.length()), m);
        }
      }
    }
  }

  public PTStandaloneField(final PTFieldTypeConstraint origTc,
      final PTStandaloneRecord pRecord, final RecordField rfd) {
    super(origTc, rfd);
    this.parentRecord = pRecord;
  }

  public PTImmutableReference dotProperty(String s) {
    if(s.toLowerCase().equals("value")) {
      return this.valueRef;
    } else if(s.toLowerCase().equals("visible")) {
      return this.visiblePropertyRef;
    } else if(s.toLowerCase().equals("name")) {
      return this.fldNamePropertyRef;
    } else if(s.toLowerCase().equals("displayonly")) {
      return this.displayOnlyPropertyRef;
    }
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
    return "[STANDALONE]" + super.toString();
  }
}
