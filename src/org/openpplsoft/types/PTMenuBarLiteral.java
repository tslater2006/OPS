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
 * Represents a PeopleTools menu bar literal.
 */
public final class PTMenuBarLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.MENUBAR_LITERAL;

  private String ptBARNAME;

  /**
   * Creates a new menu bar literal object that isn't
   * attached to a menu bar name; can only be called by
   * an internal method.
   */
  private PTMenuBarLiteral() {
    super(staticTypeFlag);
  }

  /**
   * Creates a new menu bar literal that is attached to a
   * specific menu bar name; can only be called by an internal
   * method.
   * @param m the menu bar name (BARNAME) to attach
   */
  private PTMenuBarLiteral(final String barname) {
    super(staticTypeFlag);
    this.ptBARNAME = barname;
  }

  /**
   * Returns the name of the menu bar represented by this literal.
   * @return the name of the menu bar
   */
  public String getMenuBarName() {
    return this.ptBARNAME;
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
    return (a instanceof PTMenuBarLiteral
        && this.getType() == a.getType());
  }

  /**
   * Retrieves a sentinel menu bar literal object from the cache,
   * or creates it if it doesn't exist.
   * @return the sentinel object
   */
  public static PTMenuBarLiteral getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    final String cacheKey = getCacheKey();
    if (PTType.isSentinelCached(cacheKey)) {
      return (PTMenuBarLiteral) PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    final PTMenuBarLiteral sentinelObj = new PTMenuBarLiteral();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  /**
   * Allocates a new menu bar literal object and attaches it
   * to the menu bar name named in the provided argument.
   * @param bStr the menu bar name, prefixed with "BarName."
   * @return the newly allocated menu bar literal object
   */
  public PTMenuBarLiteral alloc(final String bStr) {

    if(!bStr.startsWith("BarName.")) {
      throw new OPSVMachRuntimeException("Expected bStr to start "
          + "with 'BarName.' while alloc'ing PTMenuBarLiteral; bStr = "
          + bStr);
    }
    final PTMenuBarLiteral newObj = new PTMenuBarLiteral(
        bStr.replaceFirst("BarName.", ""));
    PTType.clone(this, newObj);
    return newObj;
  }

  /**
   * Retrieves the cache key for this menu bar literal.
   * @return the menu bar literal's cache key
   */
  private static String getCacheKey() {
    return new StringBuilder(staticTypeFlag.name()).toString();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptBARNAME=").append(this.ptBARNAME);
    return b.toString();
  }
}
