/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt.pages;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;

public class PgToken {

  private EnumSet<PFlag> flags;
  private String RECNAME, FIELDNAME, SUBPNLNAME;
  private int OCCURSLEVEL, FIELDNUM, ASSOCFIELDNUM;
  private int FIELDUSE;

  // If this token represents a related display field,
  // dispControlFieldTok refers to the associated display
  // control field.
  private PgToken dispControlFieldTok;

  // If this token represents a display control field,
  // this list contains references to all of the related
  // display field tokens that use this field for display control.
  private List<PgToken> relDispFieldToks =
      new ArrayList<PgToken>();

  private String primaryRecName;     // used for SCROLL_START tokens.

  private final byte DISPLAY_ONLY_FLAG = (byte) 1;
  private final byte INVISIBLE_FLAG = (byte) 2;
  private final byte DISP_CNTRL_FLAG = (byte) 8;
  private final byte REL_DISP_FLAG = (byte) 16;

  public PgToken() {
    this.flags = EnumSet.noneOf(PFlag.class);
  }

  public PgToken(
      final String recName,
      final String fldName,
      final String subPnlName,
      final int occursLevel,
      final int fieldUse,
      final int assocFieldNum,
      final int fieldNum) {
    this.RECNAME = recName;
    this.FIELDNAME = fldName;
    this.SUBPNLNAME = subPnlName;
    this.OCCURSLEVEL = occursLevel;
    this.FIELDUSE = fieldUse;
    this.ASSOCFIELDNUM = assocFieldNum;
    this.FIELDNUM = fieldNum;

    this.flags = EnumSet.noneOf(PFlag.class);
  }

  public PgToken(PFlag flag) {
    this.flags = EnumSet.of(flag);
  }

  public PgToken(EnumSet<PFlag> flagSet) {
    this.flags = EnumSet.copyOf(flagSet);
  }

  public String getRecName() {
    return this.RECNAME;
  }

  public String getFldName() {
    return this.FIELDNAME;
  }

  public int getOccursLevel() {
    return this.OCCURSLEVEL;
  }

  public int getFieldNum() {
    return this.FIELDNUM;
  }

  public int getAssocFieldNum() {
    return this.ASSOCFIELDNUM;
  }

  public String getSubPnlName() {
    return this.SUBPNLNAME;
  }

  public PgToken getDispControlFieldTok() {
    return this.dispControlFieldTok;
  }

  /**
   * If this is a display control token and *all* of
   * the reldisp tokens referencing it are on the PSXLATITEM
   * record, this token is considered an Xlat display control field
   * token.
   */
  public boolean isXlatDisplayControl() {

    long xlatCount = this.relDispFieldToks.stream()
        .filter(tok -> tok.getRecName().equals("PSXLATITEM"))
        .count();

    return this.isDisplayControl()
        && this.relDispFieldToks.size() > 0
        && xlatCount == this.relDispFieldToks.size();
  }

  public void setDispControlFieldTok(final PgToken dispCtrlFieldTok) {
    this.dispControlFieldTok = dispCtrlFieldTok;
  }

  public List<PgToken> getRelDispFieldToks() {
    return this.relDispFieldToks;
  }

  public void addRelDispFieldTok(final PgToken relDispTok) {
    this.relDispFieldToks.add(relDispTok);
  }

  public String getPrimaryRecName() {
    return this.primaryRecName;
  }

  public void setPrimaryRecName(final String recName) {
    this.primaryRecName = recName;
  }

  public void setSubPnlName(final String subPnlName) {
    this.SUBPNLNAME = subPnlName;
  }

  public void addFlag(final PFlag flag) {
    this.flags.add(flag);
  }

  public boolean hasFlag(final PFlag flag) {
    return this.flags.contains(flag);
  }

  public boolean isDisplayOnly() {
    return ((this.FIELDUSE & this.DISPLAY_ONLY_FLAG) > 0);
  }

  public boolean isInvisible() {
    return ((this.FIELDUSE & this.INVISIBLE_FLAG) > 0);
  }

  public boolean isDisplayControl() {
    return ((this.FIELDUSE & this.DISP_CNTRL_FLAG) > 0);
  }

  public boolean isRelatedDisplay() {
    return ((this.FIELDUSE & this.REL_DISP_FLAG) > 0);
  }

  public String getDispControlRecFieldName() {
    return this.dispControlFieldTok.RECNAME
        + "." + this.dispControlFieldTok.FIELDNAME;
  }

  public boolean doesBelongInComponentBuffer() {

    // If RECNAME or FIELDNAME is empty, don't add.
    if(this.RECNAME.length() == 0 || this.FIELDNAME.length() == 0) {
      return false;
    }

    /*
     * TODO: Keep an eye on these two checks involving subrecords.
     * It may be necessary to resolve subrecord fields to their parent/gp/etc.,
     * rather than simply waiting for records to be expanded by the component buffer.
     */
    // Subrecords should not be added.
    Record recDefn = DefnCache.getRecord(this.RECNAME);
    if(recDefn.isSubrecord()) {
      return false;
    }

    return true;
  }

  public boolean hasSubPnlName() {
    return this.SUBPNLNAME != null && this.SUBPNLNAME.length() > 0;
  }

  public String toString() {

    StringBuilder b = new StringBuilder();

    if (this.flags.contains(PFlag.PAGE)) {
      b.append(this.SUBPNLNAME);

      if(this.RECNAME != null && this.RECNAME.length() > 0) {
        b.append(", ").append("RECNAME=").append(this.RECNAME);
      }

      if(this.FIELDNAME != null && this.FIELDNAME.length() > 0) {
        b.append(", ").append("FIELDNAME=").append(this.FIELDNAME);
      }
    } else if (!this.flags.contains(PFlag.END_OF_PAGE)) {
      b.append(this.RECNAME).append(".").append(this.FIELDNAME);

      if(this.hasSubPnlName()) {
        b.append(", SUBPNLNAME=").append(this.SUBPNLNAME);
      }
    }

    if(this.primaryRecName != null && this.primaryRecName.length() > 0) {
      b.append(", ").append("primaryRecName=").append(this.primaryRecName);
    }

    b.append(this.flags);
    b.append(", dspctrl?").append(this.isDisplayControl());
    b.append(", reldisp?").append(this.isRelatedDisplay());
    b.append(", invis?").append(this.isInvisible());
    b.append(", FIELDUSE=").append(this.FIELDUSE);
    return b.toString();
  }
}
