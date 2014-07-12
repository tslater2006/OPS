/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;

/**
 * Represents a PeopleTools row definition.
 */
public final class PTRow extends PTObjectType {

 /*
  * TODO(mquinn): Keep this in mind:
  * I believe rows can contain rowsets and multiple records.
  * The exact details are still unclear to me.
  */

  private static Type staticTypeFlag = Type.ROW;

  // Maps record names to child record objects
  private Map<String, PTRecord> childRecordMap;

  // Maps scroll/rowset primary rec names to rowset objects
  private Map<String, PTRowset> childRowsetMap;

  /**
   * Create a new row object that isn't attached to a record
   * definition; can only be called by internal methods.
   */
  protected PTRow() {
    super(staticTypeFlag);
  }

  /**
   * Create a new row object that is attached to one or more record
   * definitions; can only be called by internal methods.
   * @param r the record defn to attach
   */
  protected PTRow(final Set<Record> s) {
    super(staticTypeFlag);
    this.childRecordMap = new HashMap<String, PTRecord>();
    this.childRowsetMap = new HashMap<String, PTRowset>();

    for(Record recDefn : s) {
      this.childRecordMap.put(recDefn.RECNAME,
          PTRecord.getSentinel().alloc(recDefn));
    }
  }

  /**
   * Registering a record defn that is currently
   * unregistered will cause the row to contain a
   * newly allocated record object for that defn.
   * @param r the record defn to register
   */
  public void registerRecordDefn(final Record r) {
    if(!this.childRecordMap.containsKey(r.RECNAME)) {
      this.childRecordMap.put(r.RECNAME,
          PTRecord.getSentinel().alloc(r));
    }
  }

  /**
   * Retrieve the record associated with the record name provided
   * @return the record associated with the record name provided
   */
  public PTRecord getRecord(final String recName) {
    return this.childRecordMap.get(recName);
  }

  /**
   * Determines if the given record exists in the row.
   * @return true if record exists, false otherwise
   */
  public boolean hasRecord(final String recName) {
    return this.childRecordMap.containsKey(recName);
  }

  @Override
  public PTType dotProperty(final String s) {
    if (this.childRecordMap.containsKey(s)) {
      return this.childRecordMap.get(s);
    }
    return null;
  }

  @Override
  public Callable dotMethod(final String s) {
    return null;
  }

  @Override
  public PTPrimitiveType castTo(final PTPrimitiveType t) {
    throw new EntDataTypeException("castTo() has not been implemented.");
  }

  @Override
  public boolean typeCheck(final PTType a) {
    return (a instanceof PTRow
        && this.getType() == a.getType());
  }

  @Override
  public PTType setReadOnly() {
    super.setReadOnly();

    // Calls to make a row read-only must make its child records read-only.
    for(Map.Entry<String, PTRecord> cursor: this.childRecordMap.entrySet()) {
      cursor.getValue().setReadOnly();
    }

    // Calls to make a row read-only must make its child rowsets read-only.
    for(Map.Entry<String, PTRowset> cursor: this.childRowsetMap.entrySet()) {
      cursor.getValue().setReadOnly();
    }

    return this;
  }

  /**
   * Creates a new, or retrieves a cached, sentinel row object.
   * @return a sentinel row object
   */
  public static PTRow getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    final String cacheKey = getCacheKey();
    if (PTType.isSentinelCached(cacheKey)) {
      return (PTRow) PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    final PTRow sentinelObj = new PTRow();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  /**
   * Allocate a new row object with a set of attached record defns.
   * @param s the record defns to attach
   * @return the newly allocated row object
   */
  public PTRow alloc(final Set<Record> s) {
    final PTRow newObj = new PTRow(s);
    PTType.clone(this, newObj);
    return newObj;
  }

  private static String getCacheKey() {
    final StringBuilder b = new StringBuilder(staticTypeFlag.name());
    return b.toString();
  }

  @Override
  public String toString() {
    return new StringBuilder(super.toString())
      .append(",childRecordRecDefns=").append(this.childRecordMap.keySet())
      .append(",childRowsetRecDefns=").append(this.childRowsetMap.keySet())
      .toString();
  }
}
