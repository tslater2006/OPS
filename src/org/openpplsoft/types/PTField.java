/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import org.openpplsoft.pt.*;
import java.util.*;
import org.openpplsoft.runtime.*;

public final class PTField extends PTObjectType {

  private static Type staticTypeFlag = Type.FIELD;
  private static Map<String, Method> ptMethodTable;

  private PTPrimitiveType value;
  public RecordField recFieldDefn;
  public PTBoolean visibleProperty;

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

  private PTField() {
    super(staticTypeFlag);
  }

  private PTField(RecordField rfd) {
    super(staticTypeFlag);
    this.recFieldDefn = rfd;
    this.value = ((PTPrimitiveType)recFieldDefn
            .getSentinelForUnderlyingValue()).alloc();
    this.visibleProperty = (PTBoolean)PTBoolean.getSentinel().alloc();
  }

  public void setDefault() {
    value.setDefault();
  }

  public PTPrimitiveType getValue() {
    return this.value;
  }

  public PTType dotProperty(String s) {
    if(s.equals("Value")) {
      return this.value;
    } else if(s.equals("Visible")) {
      return this.visibleProperty;
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

  /**
   * Implements the .SetDefault PeopleCode method for field objects.
   */
  public void PT_SetDefault() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected no args.");
    }
    this.setDefault();
  }

  /*
   * Calls to make a field read-only should make the
   * field's value read-only as well.
   */
  @Override
  public PTType setReadOnly() {
    super.setReadOnly();
    if(this.value != null) {
      this.value.setReadOnly();
    }
    return this;
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTField &&
        this.getType() == a.getType());
  }

  public static PTField getSentinel() {
    // If the sentinel has already been cached, return it immediately.
    String cacheKey = getCacheKey();
    if(PTType.isSentinelCached(cacheKey)) {
      return (PTField)PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    PTField sentinelObj = new PTField();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  /*
   * Allocated fields must have an associated record field defn in order
   * to determine the type of the value enclosed within them. However, this
   * defn is not part of the type itself; a Field variable can be assigned
   * any Field object, regardless of its underlying record field defn.
   */
  public PTField alloc(RecordField recFieldDefn) {
    PTField newObj = new PTField(recFieldDefn);
    PTType.clone(this, newObj);
    return newObj;
  }

  private static String getCacheKey() {
    StringBuilder b = new StringBuilder(staticTypeFlag.name());
    return b.toString();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(":").append(recFieldDefn.FIELDNAME);
    b.append(",value=").append(value.toString());
    return b.toString();
  }
}
