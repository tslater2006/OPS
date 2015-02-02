/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a PeopleTools record field.
 */
public class RecFldName implements Comparable<RecFldName> {

  private static Logger log =
      LogManager.getLogger(RecFldName.class.getName());

  private final String recName, fldName;

  public RecFldName(final String recName, final String fldName) {
    validate(recName, fldName);
    this.recName = recName;
    this.fldName = fldName;
  }

  public RecFldName(final String recFldStr) {
      final String[] recFldParts = recFldStr.split("\\.");
    if (recFldParts.length != 2) {
      throw new OPSVMachRuntimeException("recFldStr provided to RecFldName "
          + "constructor is not valid (expected \"RECNAME.FLDNAME\"): "
          + recFldStr);
    }

    validate(recFldParts[0], recFldParts[1]);
    this.recName = recFldParts[0];
    this.fldName = recFldParts[1];
  }

  private void validate(final String recName, final String fldName) {
    if (recName == null || recName.trim().length() == 0) {
      throw new OPSVMachRuntimeException("Illegal recName provided during "
          + "creation of RecFldName object: " + recName);
    }

    if (fldName == null || fldName.trim().length() == 0) {
      throw new OPSVMachRuntimeException("Illegal fldName provided during "
          + "creation of RecFldName object: " + fldName);
    }
  }

  @Override
  public int compareTo(final RecFldName other) {
    final int recNameCompare = this.recName.compareTo(other.recName);
    if (recNameCompare != 0) {
      return recNameCompare;
    }

    return this.fldName.compareTo(other.fldName);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof RecFldName)) {
      return false;
    }

    final RecFldName other = (RecFldName) obj;
    return this.recName.equals(other.recName)
        && this.fldName.equals(other.fldName);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 4259, HCB_MULTIPLIER = 619;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.recName).append(this.fldName);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    return this.recName + "." + this.fldName;
  }
}
