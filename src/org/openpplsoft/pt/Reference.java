/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.util.HashMap;

public class Reference {

  public String RECNAME;
  public String REFNAME;

  private String processedRef;
  public boolean isRecordFieldRef = false;

  public Reference(String recname, String refname) {

    this.processedRef = "";

    /*
     * If the RECNAME is a reserved word, replace it with the appropriate
     * camelcase equivalent and set the appropriate flag.
     */
    boolean containsKeyword = false;
    if(PSDefn.DEFN_LITERAL_RESERVED_WORDS_TABLE.get(recname) != null) {
      // remember: this assigns the value (which is camelcase) rather
      // than the key (which is upper).
      recname = PSDefn.DEFN_LITERAL_RESERVED_WORDS_TABLE.get(recname);
      containsKeyword = true;
    }

    this.RECNAME = recname;
    this.REFNAME = refname;

    if(this.RECNAME != null && this.RECNAME.length() > 0) {
      this.processedRef = this.RECNAME + "." + this.REFNAME;
      if(!containsKeyword) {
        this.isRecordFieldRef = true;
      }
    } else {
      this.processedRef = this.REFNAME;
    }
  }

  public String getValue() {
    return this.processedRef;
  }
}
