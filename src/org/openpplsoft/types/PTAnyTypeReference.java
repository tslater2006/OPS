/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

public final class PTAnyTypeReference extends PTReference<PTType> {

  public PTAnyTypeReference(final PTTypeConstraint origTc,
      final PTType initialVal) throws OPSTypeCheckException {
    super(origTc, initialVal);
  }

  public void castTo(final PTTypeConstraint castTc) {
    if (this.referencedValue instanceof PTNull) {
      try {
        this.pointTo(castTc.alloc());
        return;
      } catch (final OPSImmutableRefAttemptedChangeException opsirae) {
        throw new OPSDataTypeException("Cannot cast immutable ref; you "
            + "should determine why this ref is immutable b/c references of "
            + "type Any should not be immutable by their very nature.", opsirae);
      } catch (final OPSTypeCheckException opstce) {
        throw new OPSDataTypeException("Failed to cast this reference's value ("
          + this + ") to a the provided type constraint (" + castTc + ") due to a "
          + "type check exception.", opstce);
      }
    }

    throw new OPSDataTypeException("TODO: Support casts on non-null "
        + "PTAnyTypeReferences.");
  }

  @Override
  public String toString() {
    return "[PTAnyTypeReference]" + super.toString();
  }
}

