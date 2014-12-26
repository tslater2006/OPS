/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.util.HashMap;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

public class BytecodeReference {

  private final String RECNAME, REFNAME;

  private boolean isRecordFieldRef = false;
  private String refStr;

  public BytecodeReference(String recname, String refname) {

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

  public boolean isRecordFieldRef() {
    return this.isRecordFieldRef;
  }

  public String getValue() {
    return this.refStr;
  }
}
