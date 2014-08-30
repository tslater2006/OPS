/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.openpplsoft.pt.*;
import org.openpplsoft.pt.peoplecode.*;

/**
 * Root parent class for all PeopleTools type implementations.
 */
public abstract class PTType {

  private static Map<String, PTType> sentinelCache;

  private Type type;
  private EnumSet<TFlag> flags;
  private boolean sentinelFlag;
  private PTTypeConstraint originatingTypeConstraint;
  private boolean isReadOnly;

  static {
    sentinelCache = new HashMap<String, PTType>();
  }

  /**
   * Creates a new type object corresponding to the enumerated
   * value provided. Can only be called by concrete subclasses.
   * @param t the enumerated flag of the type to create
   */
  protected PTType(final Type t, final PTTypeConstraint origTypeConstraint) {
    this.type = t;
    this.flags = EnumSet.noneOf(TFlag.class);
    this.originatingTypeConstraint = origTypeConstraint;
  }

  /**
   * Marks this typed object as read-only.
   */
  public void setReadOnly() {
    this.isReadOnly = true;
  }

  /**
   * Returns whether this typed object is read-only.
   */
  public boolean isReadOnly() {
    return this.isReadOnly;
  }

  public PTTypeConstraint getOriginatingTypeConstraint() {
    return this.originatingTypeConstraint;
  }

  /**
   * Determines if the provided type corresponds to this
   * object's type. This is implementation specific and thus
   * must be implemented by subclasses.
   * @param a the type object to check against
   * @return whether the provided object has the same type
   *    as this one.
   */
  public abstract boolean typeCheck(PTType a);

  /**
   * Determines if a sentinel type object has been cached
   * yet.
   * @param key the key of the sentinel type object to check
   *   for in the cache
   * @return whether the sentinel type object exists in the
   *    cache
   */
  protected static boolean isSentinelCached(final String key) {
    return sentinelCache.containsKey(key);
  }

  /**
   * Returns a cached sentinel type object.
   * @param key the key of the sentinel type object to get
   *    the cache
   * @return the cached sentinel type object requested
   */
  protected static PTType getCachedSentinel(final String key) {
    return sentinelCache.get(key);
  }

  /**
   * Adds a sentinel type object to the cache.
   * @param sentinelObj the sentinel type object to cache
   * @param cacheKey the key to store the sentinel type
   *    object under in the cache
   */
  protected static void cacheSentinel(final PTType sentinelObj,
      final String cacheKey) {
    sentinelObj.setSentinel();
    sentinelCache.put(cacheKey, sentinelObj);
  }

  /**
   * Clones a source type to a destination type object. The
   * source and destination must have the same enumerated type
   * flag.
   * @param src the source type object to clone from
   * @param dest the destination type object to clone to
   */
  protected static void clone(final PTType src,
      final PTType dest) {
    if (src.type != dest.type) {
      throw new EntDataTypeException("Attempted to clone PTType objects "
          + "with different type enum flags (" + src.type + " to "
          + dest.type + ")");
    }
    dest.setType(src.getType());
    dest.setFlags(src.getFlags());

    if (src instanceof PTArray) {
      ((PTArray) dest).dimensions = ((PTArray) src).dimensions;
      ((PTArray) dest).baseTypeConstraint = ((PTArray) src).baseTypeConstraint;
    }
  }

  /**
   * Check if the instance is a sentinel type object.
   * @return whether or not the instance is a sentinel
   */
  protected boolean isSentinel() {
    return this.sentinelFlag;
  }

  /**
   * Mark the instance type object as a sentinel.
   * @return the instance itself (allows chained methods)
   */
  private PTType setSentinel() {
    this.sentinelFlag = true;
    return this;
  }

  /**
   * Get the enumerated type of this instance.
   * @return the enumerated type of this instance
   */
  public Type getType() {
    return this.type;
  }

  /**
   * Set the enumerated type of this instance.
   * @param t the enumerated type to use
   */
  private void setType(final Type t) {
    if (this.isSentinel()) {
      throw new EntDataTypeException("Encountered illegal attempt to "
          + "set the type of a sentinel PTType.");
    }
    this.type = t;
  }

  protected void checkIsWriteable() {
    if (this.isReadOnly) {
      throw new EntDataTypeException("Attempted illegal write to a "
          + "readonly PTType object.");
    }
  }

  /**
   * Get the set of type flags for this instance.
   * @return the set of enumerated type flags for this
   *   instance
   */
  protected EnumSet<TFlag> getFlags() {
    return this.flags;
  }

  /**
   * Set the type flags for this instance object.
   * @param fSet the set of flags to use for this type object
   */
  private void setFlags(final EnumSet<TFlag> fSet) {
    if (this.isSentinel()) {
      throw new EntDataTypeException("Encountered illegal attempt to "
          + "set the flags of a sentinel PTType.");
    }
    this.flags = fSet.clone();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("PTType:origTc=");
    b.append(this.originatingTypeConstraint).append(",");
    b.append(this.type);
    if (this.flags.size() > 0) {
      b.append(this.flags);
    }
    return b.toString();
  }
}
