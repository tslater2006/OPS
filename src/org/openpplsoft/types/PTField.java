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

public abstract class PTField<R extends PTRecord> extends PTObjectType {

  private static Logger log = LogManager.getLogger(PTField.class.getName());

  private static Map<String, Method> ptMethodTable;

  protected R parentRecord;
  protected RecordField recFieldDefn;
  protected RecordFieldBuffer recFieldBuffer;
  protected PTImmutableReference<PTPrimitiveType> valueRef;
  protected PTImmutableReference<PTBoolean> visiblePropertyRef,
      displayOnlyPropertyRef;
  protected PTImmutableReference<PTString> fldNamePropertyRef;
  protected boolean isGrayedOut;

  static {
    final String PT_METHOD_PREFIX = "PT_";

    // cache pointers to PeopleTools Field methods.
    final Method[] methods = PTField.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  public PTField(final PTFieldTypeConstraint origTc) {
    super(origTc);
  }

  public R getParentRecord() {
    return this.parentRecord;
  }

  public PTPrimitiveType getValue() {
    return this.valueRef.deref();
  }

  public RecordField getRecordFieldDefn() {
    return this.recFieldDefn;
  }

  public void setBlank() {
    valueRef.deref().setBlank();
  }
}
