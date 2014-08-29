/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTTypeConstraint<T extends PTType> extends PTType {

  private static Logger log = LogManager.getLogger(PTTypeConstraint.class.getName());

  private Class<T> underlyingClass;

  public PTTypeConstraint(final Class<T> clazz) {
    super(Type.TYPE_CONSTRAINT);
    this.underlyingClass = clazz;
  }

  public boolean isUnderlyingClassPrimitive() {
    return PTPrimitiveType.class.isAssignableFrom(this.underlyingClass);
  }

  public T alloc() {
    try {
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
}
