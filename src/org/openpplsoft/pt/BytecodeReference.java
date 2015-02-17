/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

public class BytecodeReference {

  private final int NAMENUM;
  private final String RECNAME, REFNAME;

  private boolean isUsedInProgram, isRecordFieldRef, isRootReference;
  private String refStr;

  public BytecodeReference(final int namenum, final String recname,
        final String refname) {
    this.NAMENUM = namenum;
    if (recname == null || recname.trim().length() == 0) {
      this.RECNAME = "";
      this.REFNAME = refname;
      this.refStr = refname;
      this.isRecordFieldRef = false;
    } else if (refname == null || refname.trim().length() == 0) {
      this.RECNAME = recname;
      this.REFNAME = "";
      this.refStr = recname;
      this.isRecordFieldRef = false;
    } else {

      /*
       * If the provided RECNAME is a reserved word, replace it with the appropriate
       * camelcase equivalent and set the appropriate flag.
       */
      if(PSDefn.DEFN_LITERAL_RESERVED_WORDS_TABLE.containsKey(recname)) {
        // remember: this assigns the value (which is camelcase) rather
        // than the key (which is upper).
        this.RECNAME = PSDefn.DEFN_LITERAL_RESERVED_WORDS_TABLE.get(recname);
        this.REFNAME = refname;
        this.refStr = this.RECNAME + "." + this.REFNAME;
        this.isRecordFieldRef = false;
      } else {
        // If the provided RECNAME is not a reserved word, this is a reference
        // to a record field.
        this.RECNAME = recname;
        this.REFNAME = refname;
        this.refStr = this.RECNAME + "." + this.REFNAME;
        this.isRecordFieldRef = true;
      }
    }
  }

  public void markAsUsedInProgram() {
    this.isUsedInProgram = true;
  }

  public boolean isUsedInProgram() {
    return this.isUsedInProgram;
  }

  public void markAsRootReference() {
    this.isRootReference = true;
  }

  public boolean isRootReference() {
    return this.isRootReference;
  }

  public int getNameNum() {
    return this.NAMENUM;
  }

  public boolean isRecordFieldRef() {
    return this.isRecordFieldRef;
  }

  public RecFldName getAsRecFldName() {
    if (!this.isRecordFieldRef) {
      throw new OPSVMachRuntimeException("Illegal call to get bytecode reference "
          + "as RecFldName; this bytecode reference is not a record field reference.");
    }
    return new RecFldName(this.RECNAME, this.REFNAME);
  }

  public String getAsString() {
    return this.refStr;
  }

  @Override
  public String toString() {
    return "[" + this.NAMENUM + "]"
        + this.refStr + " (isRecordFieldRef? " + this.isRecordFieldRef + ")"
        + ", (isUsed? " + this.isUsedInProgram + ") (isRootRef? "
        + this.isRootReference + ")";
  }
}
