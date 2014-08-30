/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTTypeConstraint<T extends PTType> {

  private static Logger log = LogManager.getLogger(PTTypeConstraint.class.getName());

  protected Class<T> underlyingClass;

  public PTTypeConstraint(final Class<T> clazz) {
    this.underlyingClass = clazz;
  }

  public boolean isUnderlyingClassPrimitive() {
    return PTPrimitiveType.class.isAssignableFrom(this.underlyingClass);
  }

  public boolean isUnderlyingClassEqualTo(final Class clazz) {
    return this.underlyingClass == clazz;
  }

  public T alloc() {
    try {
//      return Class.getDeclaredConstructor(this.underlyingClass).newInstance();
      return this.underlyingClass.newInstance();
    } catch (final InstantiationException ie) {
      throw new EntDataTypeException("Unable to dynamically instantiate "
          + "underlying class for PTTypeConstraint.", ie);
    } catch (final IllegalAccessException iae) {
      throw new EntDataTypeException("Unable to dynamically instantiate "
          + "underlying class for PTTypeConstraint.", iae);
    }
  }

  public boolean typeCheck(PTType a) {
    log.debug("Constraint typecheck: underlying class is {}, a is {}",
      this.underlyingClass, a.getClass());
    return (this.underlyingClass == a.getClass());
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("TypeConstraint:");
    b.append(this.underlyingClass.getName());
    return b.toString();
  }
}
