/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.pt;

import org.apache.logging.log4j.*;
import com.enterrupt.runtime.*;
import com.enterrupt.types.*;

public class RecordField {

  public String RECNAME;
  public String FIELDNAME;
  public int USEEDIT;
  public int FIELDNUM;
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

  public PTType getSentinelForUnderlyingValue() {
    switch(this.typeFlag) {
      case 0:
        return PTString.getSentinel();
      case 1: // TODO: 0 is char, 1 is long char: should I distinguish?
        return PTString.getSentinel();
      case 2:
        return PTNumber.getSentinel();
      case 3: // TODO: 2 is unsigned, 3 is signed: should I distinguish?
        return PTNumber.getSentinel();
      case 4:
        return PTDate.getSentinel();
      case 5:
        return PTTime.getSentinel();
      case 6:
        return PTDateTime.getSentinel();
      case 8: // TODO: 8 is image/attachment; doesn't need to be stored.
        return PTString.getSentinel();
      case 9: // TODO: 9 is image reference; doesn't need to be stored.
        return PTString.getSentinel();
        default:
      throw new EntVMachRuntimeException("Unable to determine " +
        "appropriate sentinel for underlying record field " +
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

