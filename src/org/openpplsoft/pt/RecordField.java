/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import org.apache.logging.log4j.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.types.*;

public class RecordField {

  public String RECNAME;
  public String FIELDNAME;
  public int USEEDIT;
  public int FIELDNUM;
  public int LENGTH;
  private int typeFlag;

  private final int KEY_FLAG = 1;
  private final int ALTERNATE_SEARCH_KEY_FLAG = 16;
  private final int DESCENDING_KEY_FLAG = 64;
  private final int LISTBOX_ITEM_FLAG = 32;
  private final int REQUIRED_FLAG = 256;
  private final int SEARCH_KEY_FLAG = 2048;

  private static Logger log = LogManager.getLogger(RecordField.class.getName());

  public RecordField(String r, String f, int t) {
    this.RECNAME = r;
    this.FIELDNAME = f;
    this.typeFlag = t;
  }

  public PTTypeConstraint getTypeConstraintForUnderlyingValue() {
    switch(this.typeFlag) {
      case 0:  // char
        if(this.LENGTH == 1) {
          return new PTTypeConstraint<PTChar>(PTChar.class);
        } else {
          return new PTTypeConstraint<PTString>(PTString.class);
        }
      case 1: // long char
        return new PTTypeConstraint<PTString>(PTString.class);
      case 2:
        return new PTTypeConstraint<PTNumber>(PTNumber.class);
      case 3: // TODO: 2 is unsigned, 3 is signed: should I distinguish?
        return new PTTypeConstraint<PTNumber>(PTNumber.class);
      case 4:
        return new PTTypeConstraint<PTDate>(PTDate.class);
      case 5:
        return new PTTypeConstraint<PTTime>(PTTime.class);
      case 6:
        return new PTTypeConstraint<PTDateTime>(PTDateTime.class);
      case 8: // TODO: 8 is image/attachment; doesn't need to be stored.
        return new PTTypeConstraint<PTString>(PTString.class);
      case 9: // TODO: 9 is image reference; doesn't need to be stored.
        return new PTTypeConstraint<PTString>(PTString.class);
        default:
      throw new OPSVMachRuntimeException("Unable to determine " +
        "appropriate type constraint for underlying record field " +
        "value given a typeFlag of: " + this.typeFlag + "; " +
        "RECNAME=" + RECNAME + ", FIELDNAME=" + FIELDNAME);
    }
  }

  public boolean isKey() {
    return ((this.USEEDIT & this.KEY_FLAG) > 0);
  }

  public boolean isDescendingKey() {
    return ((this.USEEDIT & this.DESCENDING_KEY_FLAG) > 0);
  }

  public boolean isSearchKey() {
    return ((this.USEEDIT & this.SEARCH_KEY_FLAG) > 0);
  }

  public boolean isAlternateSearchKey() {
    return ((this.USEEDIT & this.ALTERNATE_SEARCH_KEY_FLAG) > 0);
  }

  public boolean isListBoxItem() {
    return ((this.USEEDIT & this.LISTBOX_ITEM_FLAG) > 0);
  }

  public boolean isRequired() {
    return ((this.USEEDIT & this.REQUIRED_FLAG) > 0);
  }
}

