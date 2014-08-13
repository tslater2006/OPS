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
 * Represents a PeopleTools menu literal.
 */
public final class PTMenuLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.MENU_LITERAL;

  private String ptMENUNAME;
  private Menu menuDefn;

  /**
   * Creates a new menu literal object that isn't
   * attached to a menu defn; can only be called by
   * an internal method.
   */
  private PTMenuLiteral() {
    super(staticTypeFlag);
  }

  /**
   * Creates a new menu literal that is attached to a
   * specific menu defn; can only be called by an internal
   * method.
   * @param m the menu defn to attach
   */
  private PTMenuLiteral(final Menu m) {
    super(staticTypeFlag);
    this.ptMENUNAME = m.getMenuName();
    this.menuDefn = m;
  }

  /**
   * Returns the name of the menu represented by this literal.
   * @return the name of the menu
   */
  public String getMenuName() {
    return this.ptMENUNAME;
  }

  @Override
  public PTType dotProperty(final String s) {
    throw new EntDataTypeException("dotProperty() has not been implemented.");
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
    return (a instanceof PTMenuLiteral
        && this.getType() == a.getType());
  }

  /**
   * Retrieves a sentinel menu literal object from the cache,
   * or creates it if it doesn't exist.
   * @return the sentinel object
   */
  public static PTMenuLiteral getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    final String cacheKey = getCacheKey();
    if (PTType.isSentinelCached(cacheKey)) {
      return (PTMenuLiteral) PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    final PTMenuLiteral sentinelObj = new PTMenuLiteral();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  /**
   * Allocates a new menu literal object with a menu defn
   * attached.
   * @param m the menu defn to attach
   * @return the newly allocated menu literal object
   */
  public PTMenuLiteral alloc(final Menu m) {
    final PTMenuLiteral newObj = new PTMenuLiteral(m);
    PTType.clone(this, newObj);
    return newObj;
  }

  /**
   * Allocates a new menu literal object and attaches it
   * to the menu defn named in the provided argument.
   * @param mStr the menu name, prefixed with "Menu."
   * @return the newly allocated menu literal object
   */
  public PTMenuLiteral alloc(final String mStr) {

    if(!mStr.startsWith("MenuName.")) {
      throw new OPSVMachRuntimeException("Expected mStr to start "
          + "with 'Menu.' while alloc'ing PTMenuLiteral; mStr = "
          + mStr);
    }
    Menu m = DefnCache.getMenu(mStr.replaceFirst("MenuName.", ""));
    final PTMenuLiteral newObj = new PTMenuLiteral(m);
    PTType.clone(this, newObj);
    return newObj;
  }

  /**
   * Retrieves the cache key for this menu literal.
   * @return the menu literal's cache key
   */
  private static String getCacheKey() {
    return new StringBuilder(staticTypeFlag.name()).toString();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptMENUNAME=").append(this.ptMENUNAME);
    return b.toString();
  }
}
