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

  public EnumSet<PFlag> flags;
  public String RECNAME;
  public String FIELDNAME;
  public String SUBPNLNAME;
  public int OCCURSLEVEL, FIELDNUM, ASSOCFIELDNUM;
  public byte FIELDUSE;

  // If this token represents a related display field,
  // dispControlFieldTok refers to the associated display
  // control field.
  public PgToken dispControlFieldTok;

  // If this token represents a display control field,
  // this list contains references to all of the related
  // display field tokens that use this field for display control.
  public List<PgToken> relDispFieldToks =
      new ArrayList<PgToken>();

  private final byte INVISIBLE_FLAG = (byte) 2;
  private final byte DISP_CNTRL_FLAG = (byte) 8;
  private final byte REL_DISP_FLAG = (byte) 16;

  public String primaryRecName;     // used for SCROLL_START tokens.

  public PgToken() {
    this.flags = EnumSet.noneOf(PFlag.class);
  }

  public PgToken(PFlag flag) {
    this.flags = EnumSet.of(flag);
  }

  public PgToken(EnumSet<PFlag> flagSet) {
    this.flags = EnumSet.copyOf(flagSet);
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

  public boolean doesBelongInComponentStructure() {

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

    /*
     * Even if RECNAME does not point to a subrecord, this field
     * may point to a subrecord on the record pointed to by RECNAME,
     * in which case it will not be present in the immediate record.
     * If that's the case, this field should not be added.
     */
    if(recDefn.fieldTable.get(this.FIELDNAME) == null) {
      return false;
    }

    return true;
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

      if(this.SUBPNLNAME != null && this.SUBPNLNAME.length() > 0) {
        b.append("SUBPNLNAME=").append(this.SUBPNLNAME);
      }
    }

    if(this.primaryRecName != null && this.primaryRecName.length() > 0) {
      b.append(", ").append("primaryRecName=").append(this.primaryRecName);
    }

    b.append(this.flags);
    b.append(", reldisp?").append(this.isRelatedDisplay());
    b.append(", invis?").append(this.isInvisible());

    return b.toString();
  }
}
