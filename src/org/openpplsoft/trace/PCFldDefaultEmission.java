/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.openpplsoft.runtime.Environment;
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
  public boolean fromConstantFlag, fromRecordFlag;
  public String metaValue = null;

  public PCFldDefaultEmission(final String recName, final String fldName) {
    this.ptRECNAME = recName;
    this.ptFLDNAME = fldName;
  }

  public PCFldDefaultEmission(final String recName, final String fldName,
      final String value) {
    this.ptRECNAME = recName;
    this.ptFLDNAME = fldName;
    this.defaultedValue = value;
  }

  public void setDefaultedValue(final String v) {
    this.defaultedValue = v;
  }

  public void setFromConstantFlag() {
    this.fromConstantFlag = true;
  }

  public void setFromRecordFlag() {
    this.fromRecordFlag = true;
  }

  /**
   * A meta value is something like "%date", from which
   * the actual field constant value is derived.
   */
  public void setMetaValue(final String mv) {
    this.metaValue = mv;
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
        && this.fromRecordFlag == other.fromRecordFlag
        && this.fromConstantFlag == other.fromConstantFlag) {

      if ("%date".equals(this.metaValue)) {
        // If this emission has a "%date" meta-value, it was interpreted by the
        // interpreter; instead of comparing the other emission against this one,
        // which will surely fail since the tracefile was generated on a different
        // date than the one the interpreter considers to be today, compare the other
        // emission against %Date, which has its value overriden with the trace file
        // date for the component profile being run.
        return other.defaultedValue.startsWith(
            Environment.getSystemVar("%Date").readAsString());
      } else if ("%date".equals(other.metaValue)) {
        // If the other emission has a "%date" meta-value, it was interpreted by the
        // interpreter; instead of comparing this emission against the other one,
        // which will surely fail since the tracefile was generated on a different
        // date than the one the interpreter considers to be today, compare this
        // emission against %Date, which has its value overriden with the trace file
        // date for the component profile being run.
        return this.defaultedValue.startsWith(
            Environment.getSystemVar("%Date").readAsString());
      } else {
        return this.defaultedValue.equals(other.defaultedValue);
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 1061, HCB_MULTIPLIER = 41;

    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.ptRECNAME)
        .append(this.ptFLDNAME).append(this.defaultedValue)
        .append(this.fromConstantFlag).toHashCode();
  }

  @Override
  public String toString() {
    return "-: " + this.ptRECNAME + "." + this.ptFLDNAME
        + (this.fromConstantFlag ? " constant " : " ")
        + "default "
        + (this.fromRecordFlag ? "from record " : "")
        + this.defaultedValue;
  }
}
