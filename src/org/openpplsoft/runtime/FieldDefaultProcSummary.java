/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

public class FieldDefaultProcSummary {

  private boolean wasFieldChanged, wasBlankFieldSeen;

  public FieldDefaultProcSummary() {}

  public void fieldWasChanged() {
    this.wasFieldChanged = true;
  }

  public void blankFieldWasSeen() {
    this.wasBlankFieldSeen = true;
  }

  /**
   * Default processing at the component level continues to run
   * as long as the just completed processing run involved both a field
   * value change *and* the witness of a blank field.
   */
  public boolean doContinueDefProc() {
    return this.wasFieldChanged && this.wasBlankFieldSeen;
  }
}

