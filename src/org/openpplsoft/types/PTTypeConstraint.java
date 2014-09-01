/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

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

  public boolean isUnderlyingClassObject() {
    return PTObjectType.class.isAssignableFrom(this.underlyingClass);
  }

  public boolean isUnderlyingClassEqualTo(final Class clazz) {
    return this.underlyingClass == clazz;
  }

  public Class<T> getUnderlyingClass() {
    return this.underlyingClass;
  }

  public T alloc() {
    try {
      Constructor<T> cons = this.underlyingClass
          .getConstructor(PTTypeConstraint.class);
      return cons.newInstance(this);
    } catch (final NoSuchMethodException nsme) {
      throw new EntDataTypeException("Unable to dynamically instantiate "
          + "underlying PT class for :" + this.underlyingClass, nsme);
    } catch (final InvocationTargetException ite) {
      throw new EntDataTypeException("Unable to dynamically instantiate "
          + "underlying PT class for :" + this.underlyingClass, ite);
    } catch (final InstantiationException ie) {
      throw new EntDataTypeException("Unable to dynamically instantiate "
          + "underlying PT class for :" + this.underlyingClass, ie);
    } catch (final IllegalAccessException iae) {
      throw new EntDataTypeException("Unable to dynamically instantiate "
          + "underlying PT class for :" + this.underlyingClass, iae);
    }
  }

  public boolean typeCheck(PTType a) {
    log.debug("Constraint typecheck: underlying class is {}, a is {}",
      this.underlyingClass, a.getClass());
    return (this.underlyingClass == a.getClass());
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PTTypeConstraint<?>)) {
      return false;
    }

    final PTTypeConstraint<?> other = (PTTypeConstraint<?>) obj;
    return (this.underlyingClass == other.getUnderlyingClass());
  }

  @Override
  public int hashCode() {
    final int HBC_INITIAL = 3019, HBC_MULTIPLIER = 71;

    return new HashCodeBuilder(HBC_INITIAL, HBC_MULTIPLIER)
      .append(this.underlyingClass).toHashCode();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("TC:");
    b.append(this.underlyingClass.getSimpleName());
    return b.toString();
  }
}