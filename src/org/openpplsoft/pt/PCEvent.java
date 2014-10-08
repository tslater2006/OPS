/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

public enum PCEvent {
  SEARCH_INIT("SearchInit"),
  PRE_BUILD("PreBuild"),
  FIELD_DEFAULT("FieldDefault"),
  FIELD_FORMULA("FieldFormula"),
  ROW_INIT("RowInit");

  private String name;

  private PCEvent(final String n) {
    this.name = n;
  }

  public String getName() {
    return this.name;
  }
}
