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

  private PTTypeConstraint originatingTypeConstraint;
  private boolean isReadOnly;

  protected PTType(final PTTypeConstraint origTypeConstraint) {
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
   * Clones a source type to a destination type object. The
   * source and destination must have the same enumerated type
   * flag.
   * @param src the source type object to clone from
   * @param dest the destination type object to clone to
   */
  protected static void clone(final PTType src,
      final PTType dest) {
      throw new OPSDataTypeException("clone() called on PTType; need to "
          + "move and implement this elsewhere.");
/*    if (src.type != dest.type) {
      throw new OPSDataTypeException("Attempted to clone PTType objects "
          + "with different type enum flags (" + src.type + " to "
          + dest.type + ")");
    }
    dest.setType(src.getType());
    dest.setFlags(src.getFlags());

    if (src instanceof PTArray) {
      ((PTArray) dest).dimensions = ((PTArray) src).dimensions;
      ((PTArray) dest).baseTypeConstraint = ((PTArray) src).baseTypeConstraint;
    }*/
  }

  protected void checkIsWriteable() {
    if (this.isReadOnly) {
      throw new OPSDataTypeException("Attempted illegal write to a "
          + "readonly PTType object.");
    }
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("PTType:origTc=");
    b.append(this.originatingTypeConstraint);
    return b.toString();
  }
}
