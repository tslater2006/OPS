/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

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

  private PTRecord record;

  /**
   * Create a new row object that isn't attached to a record
   * definition; can only be called by internal methods.
   */
  protected PTRow() {
    super(staticTypeFlag);
  }

  /**
   * Create a new row object that is attached to a record
   * definition; can only be called by internal methods.
   * @param rec the record defn to attach
   */
  protected PTRow(final PTRecord rec) {
    super(staticTypeFlag);
    this.record = rec;
  }

  /**
   * Retrieve the record defn attached to this row object.
   * @return the record defn attached to this row object
   */
  public PTRecord getRecord() {
    return this.record;
  }

  @Override
  public PTType dotProperty(final String s) {
    if (this.record.recDefn.RECNAME.equals(s)) {
      return this.record;
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

    /*
     * Calls to make a row read-only should make its
     * record read-only as well.
     */
    if (this.record != null) {
      this.record.setReadOnly();
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
   * Allocate a new row object with an attached record defn.
   * @param rec the record defn to attach
   * @return the newly allocated row object
   */
  public PTRow alloc(final PTRecord rec) {
    final PTRow newObj = new PTRow(rec);
    PTType.clone(this, newObj);
    return newObj;
  }

  private static String getCacheKey() {
    final StringBuilder b = new StringBuilder(staticTypeFlag.name());
    return b.toString();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",record=").append(this.record);
    return b.toString();
  }
}
