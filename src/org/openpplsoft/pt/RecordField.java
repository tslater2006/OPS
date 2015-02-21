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

public class RecordField {

  private static Logger log = LogManager.getLogger(RecordField.class.getName());

  private static Map<Integer, FieldType> fieldTypeMap;

  private final String RECNAME, FIELDNAME;
  private final int USEEDIT, FIELDNUM, LENGTH;
  private final String DEFRECNAME, DEFFIELDNAME, LABEL_ID;
  private final FieldType fieldType;
  private final Map<String, FieldLabel> labels;

  private FieldLabel defaultLabel;

  private final int KEY_FLAG = 1;
  private final int ALTERNATE_SEARCH_KEY_FLAG = 16;
  private final int DESCENDING_KEY_FLAG = 64;
  private final int LISTBOX_ITEM_FLAG = 32;
  private final int REQUIRED_FLAG = 256;
  private final int SEARCH_KEY_FLAG = 2048;

  static {
    fieldTypeMap = new HashMap<>();
  }

  public RecordField(final String recname,
      final String fieldname,
      final int fieldtype,
      final int useedit,
      final int fieldnum,
      final int length,
      final String defrecname,
      final String deffieldname,
      final String labelId) {
    this.RECNAME = recname;
    this.FIELDNAME = fieldname;
    this.USEEDIT = useedit;
    this.FIELDNUM = fieldnum;
    this.LENGTH = length;
    this.DEFRECNAME = defrecname;
    this.DEFFIELDNAME = deffieldname;
    this.LABEL_ID = labelId;

    this.fieldType = FieldType.fieldTypeCodeToEnum(fieldtype);
    this.labels = new HashMap<String, FieldLabel>();
  }

  public String getRecName() {
    return this.RECNAME;
  }

  public String getFldName() {
    return this.FIELDNAME;
  }

  public String getDefaultRecName() {
    return this.DEFRECNAME;
  }

  public String getDefaultFldName() {
    return this.DEFFIELDNAME;
  }

  public int getFldNum() {
    return this.FIELDNUM;
  }

  public int getUseEdit() {
    return this.USEEDIT;
  }

  public FieldType getFieldType() {
    return this.fieldType;
  }

  public void addLabel(final FieldLabel label) {
    this.labels.put(label.getLabelId(), label);
    if (label.isDefault()) {
      this.defaultLabel = label;
    }
  }

  public PTTypeConstraint getTypeConstraintForUnderlyingValue() {
    switch(this.fieldType) {
      case CHARACTER:
        if(this.LENGTH == 1) {
          return PTChar.getTc();
        } else {
          return PTString.getTc();
        }
      case LONG_CHARACTER:
        return PTString.getTc();
      case NUMBER:
        return PTNumber.getTc();
      // TODO: eventually distinguish b/w NUMBER and SIGNED_NUMBER.
      case SIGNED_NUMBER:
        return PTNumber.getTc();
      case DATE:
        return PTDate.getTc();
      case TIME:
        return PTTime.getTc();
      case DATETIME:
        return PTDateTime.getTc();
      case IMAGE_ATTACHMENT:
        return PTString.getTc();
      case IMAGE_REFERENCE:
        return PTString.getTc();
        default:
      throw new OPSVMachRuntimeException("Unable to determine " +
        "appropriate type constraint for underlying record field " +
        "value given a fieldType of: " + this.fieldType + "; " +
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

  public enum FieldType {
    CHARACTER(0),
    LONG_CHARACTER(1),
    NUMBER(2),
    SIGNED_NUMBER(3),
    DATE(4),
    TIME(5),
    DATETIME(6),
    IMAGE_ATTACHMENT(8),
    IMAGE_REFERENCE(9);

    private final int FIELDTYPE;

    private FieldType(final int fieldType) {
      this.FIELDTYPE = fieldType;
      fieldTypeMap.put(fieldType, this);
    }

    private static FieldType fieldTypeCodeToEnum(final int code) {
      if (fieldTypeMap.containsKey(code)) {
        return fieldTypeMap.get(code);
      }
      throw new OPSVMachRuntimeException("No FieldType found for code: " + code);
    }
  }
}

