/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.List;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;

/**
 * Represents a PeopleTools record literal.
 */
public final class PTRecordLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.REC_LITERAL;

  private String ptRECNAME;
  private Record recDefn;

  /**
   * Creates a new record literal object that isn't
   * attached to a record defn; can only be called by
   * an internal method.
   */
  private PTRecordLiteral() {
    super(staticTypeFlag);
  }

  /**
   * Creates a new record literal that is attached to a
   * specific record defn; can only be called by an internal
   * method.
   * @param r the record defn to attach
   */
  private PTRecordLiteral(final Record r) {
    super(staticTypeFlag);
    this.ptRECNAME = r.RECNAME;
    this.recDefn = r;
  }

  /**
   * Returns the name of the record represented by this literal.
   * @return the name of the record
   */
  public String getRecName() {
    return this.ptRECNAME;
  }

  /**
   * Dot accesses on record field literals must
   * always return the appropriate FieldLiteral,
   * assuming the value for s is a valid field on
   * the underlying record.
   * @param s the name of the property
   * @return the FieldLiteral corresponding to {@code s}
   */
  @Override
  public PTType dotProperty(final String s) {
    final List<RecordField> rfList = this.recDefn.getExpandedFieldList();
    for (RecordField rf : rfList) {
      if (rf.FIELDNAME.equals(s)) {
        return PTFieldLiteral.getSentinel().alloc(this.ptRECNAME, s);
      }
    }

    throw new OPSVMachRuntimeException("Unable to resolve s="
        + s + " to a field on the PTRecordLiteral for record "
        + this.ptRECNAME);
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
    return (a instanceof PTRecordLiteral
        && this.getType() == a.getType());
  }

  /**
   * Retrieves a sentinel record literal object from the cache,
   * or creates it if it doesn't exist.
   * @return the sentinel object
   */
  public static PTRecordLiteral getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    final String cacheKey = getCacheKey();
    if (PTType.isSentinelCached(cacheKey)) {
      return (PTRecordLiteral) PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    final PTRecordLiteral sentinelObj = new PTRecordLiteral();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  /**
   * Allocates a new record literal object with a record defn
   * attached.
   * @param r the record defn to attach
   * @return the newly allocated record literal object
   */
  public PTRecordLiteral alloc(final Record r) {
    final PTRecordLiteral newObj = new PTRecordLiteral(r);
    PTType.clone(this, newObj);
    return newObj;
  }

  /**
   * Allocates a new record literal object and attaches it
   * to the record defn named in the provided argument.
   * @param rStr the record name, prefixed with "Record."
   * @return the newly allocated record literal object
   */
  public PTRecordLiteral alloc(final String rStr) {

    if(!rStr.startsWith("Record.")) {
      throw new OPSVMachRuntimeException("Expected rStr to start "
          + "with 'Record.' while alloc'ing PTRecordLiteral.");
    }
    Record r = DefnCache.getRecord(rStr.replaceFirst("Record.", ""));
    final PTRecordLiteral newObj = new PTRecordLiteral(r);
    PTType.clone(this, newObj);
    return newObj;
  }

  /**
   * Retrieves the cache key for this record literal.
   * @return the record literal's cache key
   */
  private static String getCacheKey() {
    return new StringBuilder(staticTypeFlag.name()).toString();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptRECNAME=").append(this.ptRECNAME);
    return b.toString();
  }
}
