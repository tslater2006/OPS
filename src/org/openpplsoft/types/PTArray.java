/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.PeopleToolsImplementation;
import org.openpplsoft.runtime.Environment;
import org.openpplsoft.runtime.OPSVMachRuntimeException;

public final class PTArray extends PTObjectType {

  private static Logger log = LogManager.getLogger(PTArray.class.getName());

  private final int dimensions;
  private final PTTypeConstraint baseTypeConstraint;
  private final LinkedList<PTType> values;

  public PTArray(PTArrayTypeConstraint origTc, int d, PTTypeConstraint baseTypeTc) {
    super(origTc);

    if(d == 1 && baseTypeTc instanceof PTArrayTypeConstraint) {
      throw new OPSVMachRuntimeException("Single dimension arrays cannot " +
        "have a base type of PTArray.");
    }

    this.dimensions = d;
    this.baseTypeConstraint = baseTypeTc;
    this.values = new LinkedList<PTType>();
  }

  public PTType getElement(PTType index) {

    int idx = -1;
    if(index instanceof PTInteger) {
      idx = ((PTInteger)index).read();
    } else if(index instanceof PTNumber) {
      idx = ((PTNumber)index).readAsInteger();
    } else {
      throw new OPSVMachRuntimeException("Unexpected type for index.");
    }

    // Must subtract 1; PT array indices are 1-based.
    return this.values.get(idx - 1);
  }

  public int getDimensions() {
    return this.dimensions;
  }

  public PTTypeConstraint getBaseTypeConstraint() {
    return this.baseTypeConstraint;
  }

  public PTType dotProperty(String s) {
    if(s.toLowerCase().equals("len")) {
      return new PTInteger(values.size());
    }
    return null;
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(",dim=").append(this.dimensions);
    b.append(",baseTypeConstraint=").append(this.baseTypeConstraint);
    b.append(",values=").append(this.values);
    return b.toString();
  }

  protected void internalPush(PTType value) {

    log.debug("Pushing {} onto array {}.", value, this);

    /*
     * NOTE: This code can promote non-array values to arrays,
     * but it cannot "flatten" arrays to the appropriate base type,
     * which will likely be required down the line.
     */
    try {

      // Ensure value matches base type constraint before continuing.
      this.baseTypeConstraint.typeCheck(value);

      /*
       * Objects should be added by reference; primitives
       * should have new versions of themselves created.
       */
      if(value.getOriginatingTypeConstraint().isUnderlyingClassObject()) {
        this.values.addLast(value);
      } else if (value.getOriginatingTypeConstraint().isUnderlyingClassPrimitive()) {
        /*
         * If the value is readonly, we can simply add it to the
         * array, as we know its value will not change. Otherwise,
         * create a clone of the object before pushing.
         */
        if (value.isReadOnly()) {
          this.values.addLast(value);
        } else {
          PTPrimitiveType clone = (PTPrimitiveType) value
              .getOriginatingTypeConstraint().alloc();
          clone.copyValueFrom(((PTPrimitiveType)value));
          this.values.addLast(clone);
        }
      } else {
        throw new OPSDataTypeException("Expected object or primitive when adding to "
            + "array; found neither: " + value.getOriginatingTypeConstraint());
      }

    } catch (final OPSTypeCheckException opstce) {

      if(this.baseTypeConstraint instanceof PTArrayTypeConstraint) {
        PTArray promotedVal = null;
        if(((PTArrayTypeConstraint) this.baseTypeConstraint).getReqdDimension() == 1) {
          promotedVal = new PTArrayTypeConstraint(1,
              value.getOriginatingTypeConstraint()).alloc();
        } else {
          promotedVal = new PTArrayTypeConstraint(
              ((PTArrayTypeConstraint) this.baseTypeConstraint).getReqdDimension(),
                  this.baseTypeConstraint).alloc();
        }
        promotedVal.internalPush(value);
        this.values.addLast(promotedVal);
      } else {
        throw new OPSVMachRuntimeException("Cannot Push onto array; "+
          "types are not compatible.", opstce);
      }
    }

    log.debug("After push, array is: {}.", this);
  }

  @PeopleToolsImplementation
  public void Push() {
    List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if(args.size() != 1) {
      throw new OPSVMachRuntimeException("Expected one argument.");
    }
    internalPush(args.get(0));
  }
}

