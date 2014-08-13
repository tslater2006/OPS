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
 * Represents a PeopleTools page literal.
 */
public final class PTPageLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.PAGE_LITERAL;

  private String ptPNLNAME;

  /**
   * Creates a new page literal object that isn't
   * attached to a page name; can only be called by
   * an internal method.
   */
  private PTPageLiteral() {
    super(staticTypeFlag);
  }

  /**
   * Creates a new page literal that is attached to a
   * specific page defn; can only be called by an internal
   * method.
   * @param pnlname the page name to attach
   */
  private PTPageLiteral(final String pnlname) {
    super(staticTypeFlag);
    this.ptPNLNAME = pnlname;
  }

  /**
   * Returns the name of the page represented by this literal.
   * @return the name of the page
   */
  public String getPageName() {
    return this.ptPNLNAME;
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
    return (a instanceof PTPageLiteral
        && this.getType() == a.getType());
  }

  /**
   * Retrieves a sentinel page literal object from the cache,
   * or creates it if it doesn't exist.
   * @return the sentinel object
   */
  public static PTPageLiteral getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    final String cacheKey = getCacheKey();
    if (PTType.isSentinelCached(cacheKey)) {
      return (PTPageLiteral) PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    final PTPageLiteral sentinelObj = new PTPageLiteral();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  /**
   * Allocates a new page literal object and attaches it
   * to the page defn named in the provided argument.
   * @param pStr the page name, prefixed with "Page."
   * @return the newly allocated page literal object
   */
  public PTPageLiteral alloc(final String pStr) {

    if(!pStr.startsWith("Page.")) {
      throw new OPSVMachRuntimeException("Expected pStr to start "
          + "with 'Page.' while alloc'ing PTPageLiteral; pStr = "
          + pStr);
    }

    final PTPageLiteral newObj = new PTPageLiteral(
        pStr.replaceFirst("Page.", ""));
    PTType.clone(this, newObj);
    return newObj;
  }

  /**
   * Retrieves the cache key for this page literal.
   * @return the page literal's cache key
   */
  private static String getCacheKey() {
    return new StringBuilder(staticTypeFlag.name()).toString();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptPNLNAME=").append(this.ptPNLNAME);
    return b.toString();
  }
}
