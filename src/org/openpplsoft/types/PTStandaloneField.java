/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.RecordField;

public final class PTStandaloneField extends PTField<PTStandaloneRecord> {

  private static Logger log = LogManager.getLogger(
      PTStandaloneField.class.getName());

  public PTStandaloneField(final PTFieldTypeConstraint origTc,
      final PTStandaloneRecord pRecord, final RecordField rfd) {
    super(origTc, rfd);
    this.parentRecord = pRecord;
  }

  public PTImmutableReference dotProperty(String s) {
    if(s.toLowerCase().equals("value")) {
      return this.valueRef;
    } else if(s.toLowerCase().equals("visible")) {
      return this.visiblePropertyRef;
    } else if(s.toLowerCase().equals("name")) {
      return this.fldNamePropertyRef;
    } else if(s.toLowerCase().equals("displayonly")) {
      return this.displayOnlyPropertyRef;
    }
    return null;
  }

  @Override
  public String toString() {
    return "[STANDALONE]" + super.toString();
  }
}
