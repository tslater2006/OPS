package org.openpplsoft.pt;

import java.util.ArrayList;
import java.util.List;

import org.openpplsoft.types.PTField;
import org.openpplsoft.types.PTNumberType;
import org.openpplsoft.runtime.OPSVMachRuntimeException;

public class Keylist {

  private List<PTField> keylist;

  public Keylist() {
    this.keylist = new ArrayList<PTField>();
  }

  public void add(final PTField fld) {
    this.keylist.add(fld);
  }

  public int size() {
    return this.keylist.size();
  }

  public PTField get(final int idx) {
    return this.keylist.get(idx);
  }

  public boolean hasNonBlankValue() {
    for (final PTField fld: this.keylist) {
      if (!fld.getValue().isBlank()) {
        return true;
      }
    }
    return false;
  }

  public PTField getFirstNonBlankField() {
    for (final PTField fld: this.keylist) {
      if (!fld.getValue().isBlank()) {
        return fld;
      }
    }
    throw new OPSVMachRuntimeException("A non-blank field does not "
        + "exist in the keylist.");
  }

  public boolean isFirstValueNonBlank() {
    if (keylist.size() > 0) {
      return !keylist.get(0).getValue().isBlank();
    }
    throw new OPSVMachRuntimeException("Keylist is empty; expected non-empty keylist.");
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("== Keylist ");
    if (this.keylist.size() == 0) {
      b.append("EMPTY ==================\n");
    } else {
      b.append("(").append(this.keylist.size()).append(" values) ================\n");
      for (int i = 0; i < this.keylist.size(); i++) {
        final PTField fld = this.keylist.get(i);
        final RecordField rf = fld.getRecordFieldDefn();
        b.append("  ").append(i).append(") ");
        b.append("rf: ").append(rf.getRecName())
            .append(".").append(rf.getFldName()).append(", ");
        b.append("value=|").append(fld.getValue().readAsString()).append("|");
        if (rf.isKey()) {
          b.append(" (key)");
        }
        if (fld.getValue().isMarkedAsUpdated()) {
          b.append(" (updated)");
        }
        b.append("\n");
      }
    }
    return b.toString();
  }
}
