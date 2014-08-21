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
 * Represents a PeopleTools component literal.
 */
public final class PTComponentLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.COMPONENT_LITERAL;

  private String ptPNLGRPNAME;

  /**
   * Creates a new component literal object that isn't
   * attached to a component name; can only be called by
   * an internal method.
   */
  private PTComponentLiteral() {
    super(staticTypeFlag);
  }

  /**
   * Creates a new component literal that is attached to a
   * specific component name; can only be called by an internal
   * method.
   * @param pnlgrpname the component name to attach
   */
  private PTComponentLiteral(final String pnlgrpname) {
    super(staticTypeFlag);
    this.ptPNLGRPNAME = pnlgrpname;
  }

  /**
   * Returns the name of the component represented by this literal.
   * @return the name of the component
   */
  public String getComponentName() {
    return this.ptPNLGRPNAME;
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
    return (a instanceof PTComponentLiteral
        && this.getType() == a.getType());
  }

  /**
   * Retrieves a sentinel component literal object from the cache,
   * or creates it if it doesn't exist.
   * @return the sentinel object
   */
  public static PTComponentLiteral getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    final String cacheKey = getCacheKey();
    if (PTType.isSentinelCached(cacheKey)) {
      return (PTComponentLiteral) PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    final PTComponentLiteral sentinelObj = new PTComponentLiteral();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  /**
   * Allocates a new component literal object and attaches it
   * to the component name named in the provided argument.
   * @param cStr the component name, prefixed with "Component."
   * @return the newly allocated component literal object
   */
  public PTComponentLiteral alloc(final String cStr) {

    if(!cStr.startsWith("Component.")) {
      throw new OPSVMachRuntimeException("Expected cStr to start "
          + "with 'Component.' while alloc'ing PTComponentLiteral; cStr = "
          + cStr);
    }

    final PTComponentLiteral newObj = new PTComponentLiteral(
        cStr.replaceFirst("Component.", ""));
    PTType.clone(this, newObj);
    return newObj;
  }

  /**
   * Retrieves the cache key for this component literal.
   * @return the component literal's cache key
   */
  private static String getCacheKey() {
    return new StringBuilder(staticTypeFlag.name()).toString();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptPNLGRPNAME=").append(this.ptPNLGRPNAME);
    return b.toString();
  }
}
