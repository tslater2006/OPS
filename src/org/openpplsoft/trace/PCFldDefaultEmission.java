/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents an emission of a PeopleCode trace file
 * statement indicating that a record field in the component
 * buffer was set to its appropriate default value.
 */
public class PCFldDefaultEmission implements IEmission {

  private static Logger log =
      LogManager.getLogger(PCFldDefaultEmission.class.getName());

  public String ptRECNAME, ptFLDNAME, defaultedValue;
  public boolean constantFlag;

  public PCFldDefaultEmission(final String recName, final String fldName,
      final String value) {
    this.ptRECNAME = recName;
    this.ptFLDNAME = fldName;
    this.defaultedValue = value;
  }

  public void setConstantFlag() {
    this.constantFlag = true;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PCFldDefaultEmission)) {
      return false;
    }

    final PCFldDefaultEmission other = (PCFldDefaultEmission) obj;
    if (this.ptRECNAME.equals(other.ptRECNAME)
        && this.ptFLDNAME.equals(other.ptFLDNAME)
        && this.defaultedValue.equals(other.defaultedValue)
        && this.constantFlag == other.constantFlag) {
      return true;
    }

    return false;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 1061, HCB_MULTIPLIER = 41;

    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.ptRECNAME)
        .append(this.ptFLDNAME).append(this.defaultedValue)
        .append(this.constantFlag).toHashCode();
  }

  @Override
  public String toString() {
    return "-: " + this.ptRECNAME + "." + this.ptFLDNAME
        + (this.constantFlag ? " constant " : " ") + this.defaultedValue;
  }
}
