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
 * Represents a PeopleTools menu item literal.
 */
public final class PTMenuItemLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.MENUITEM_LITERAL;

  private String ptITEMNAME;

  /**
   * Creates a new menu item literal object that isn't
   * attached to a menu item name; can only be called by
   * an internal method.
   */
  private PTMenuItemLiteral() {
    super(staticTypeFlag);
  }

  /**
   * Creates a new menu item literal that is attached to a
   * specific menu item name; can only be called by an internal
   * method.
   * @param itemname the menu item name (ITEMNAME) to attach
   */
  private PTMenuItemLiteral(final String itemname) {
    super(staticTypeFlag);
    this.ptITEMNAME = itemname;
  }

  /**
   * Returns the name of the menu item represented by this literal.
   * @return the name of the menu item
   */
  public String getMenuItemName() {
    return this.ptITEMNAME;
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
    return (a instanceof PTMenuItemLiteral
        && this.getType() == a.getType());
  }

  /**
   * Retrieves a sentinel menu item literal object from the cache,
   * or creates it if it doesn't exist.
   * @return the sentinel object
   */
  public static PTMenuItemLiteral getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    final String cacheKey = getCacheKey();
    if (PTType.isSentinelCached(cacheKey)) {
      return (PTMenuItemLiteral) PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    final PTMenuItemLiteral sentinelObj = new PTMenuItemLiteral();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  /**
   * Allocates a new menu item literal object and attaches it
   * to the menu item name named in the provided argument.
   * @param iStr the menu item name, prefixed with "ItemName."
   * @return the newly allocated menu item literal object
   */
  public PTMenuItemLiteral alloc(final String iStr) {

    if(!iStr.startsWith("ItemName.")) {
      throw new OPSVMachRuntimeException("Expected iStr to start "
          + "with 'ItemName.' while alloc'ing PTMenuItemLiteral; iStr = "
          + iStr);
    }
    final PTMenuItemLiteral newObj = new PTMenuItemLiteral(
        iStr.replaceFirst("ItemName.", ""));
    PTType.clone(this, newObj);
    return newObj;
  }

  /**
   * Retrieves the cache key for this menu item literal.
   * @return the menu item literal's cache key
   */
  private static String getCacheKey() {
    return new StringBuilder(staticTypeFlag.name()).toString();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptITEMNAME=").append(this.ptITEMNAME);
    return b.toString();
  }
}
