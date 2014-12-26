/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.types.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.pt.*;

public class RecordField {

  public String RECNAME;
  public String FIELDNAME;
  public int USEEDIT;
  public int FIELDNUM;
  public int LENGTH;
  public String DEFRECNAME;
  public String DEFFIELDNAME;
  public String LABEL_ID;
  private int typeFlag;
  private FieldLabel defaultLabel;
  private Map<String, FieldLabel> labels;

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
    this.labels = new HashMap<String, FieldLabel>();
  }

  public void addLabel(final FieldLabel label) {
    this.labels.put(label.getLabelId(), label);
    if (label.isDefault()) {
      this.defaultLabel = label;
    }
  }

  public PTTypeConstraint getTypeConstraintForUnderlyingValue() {
    switch(this.typeFlag) {
      case 0:  // char
        if(this.LENGTH == 1) {
          return PTChar.getTc();
        } else {
          return PTString.getTc();
        }
      case 1: // long char
        return PTString.getTc();
      case 2:
        return PTNumber.getTc();
      case 3: // TODO: 2 is unsigned, 3 is signed: should I distinguish?
        return PTNumber.getTc();
      case 4:
        return PTDate.getTc();
      case 5:
        return PTTime.getTc();
      case 6:
        return PTDateTime.getTc();
      case 8: // TODO: 8 is image/attachment; doesn't need to be stored.
        return PTString.getTc();
      case 9: // TODO: 9 is image reference; doesn't need to be stored.
        return PTString.getTc();
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

  public PeopleCodeProg getProgramForEvent(final PCEvent event) {
    final Record recDefn = DefnCache.getRecord(this.RECNAME);
    recDefn.discoverRecordPC();

    final List<RecordPeopleCodeProg> recFldProgs =
        recDefn.getRecordProgsForField(this.FIELDNAME);
    if (recFldProgs != null) {
      for (final RecordPeopleCodeProg prog : recFldProgs) {
        if (prog.getEvent().equals(event.getName())) {
          return prog;
        }
      }
    }
    return null;
  }

  public boolean hasDefaultConstantValue() {
    log.debug("hasDefaultConstantValue ({}.{})? DEFRECNAME={}, DEFFIELDNAME={}",
        this.RECNAME, this.FIELDNAME, this.DEFRECNAME, this.DEFFIELDNAME);
    return this.DEFRECNAME.trim().length() == 0
        && this.DEFFIELDNAME.trim().length() > 0;
  }

  public boolean hasDefaultNonConstantValue() {
    log.debug("hasDefaultNonConstantValue ({}.{})? DEFRECNAME={}, DEFFIELDNAME={}",
        this.RECNAME, this.FIELDNAME, this.DEFRECNAME, this.DEFFIELDNAME);
    return this.DEFRECNAME.trim().length() > 0
        && this.DEFFIELDNAME.trim().length() > 0;
  }

  public FieldLabel getLabelById(final String labelId) {
    return this.labels.get(labelId);
  }
}

