/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

/**
 * Root parent class for all PeopleTools type implementations.
 */
public abstract class PTType {

  private final PTTypeConstraint originatingTypeConstraint;

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
