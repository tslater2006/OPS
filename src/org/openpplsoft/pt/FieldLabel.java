/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a PeopleTools field label.
 */
public class FieldLabel {

  private static Logger log = LogManager.getLogger(FieldLabel.class.getName());

  private final String labelId, longName, shortName;
  private boolean isDefault;

  public FieldLabel(final String labelId, final String longName,
      final String shortName) {
    this.labelId = labelId;
    this.longName = longName;
    this.shortName = shortName;
  }

  public String getLabelId() {
    return this.labelId;
  }

  public String getLongName() {
    return this.longName;
  }

  public boolean isDefault() {
    return this.isDefault;
  }

  public void setAsDefault() {
    this.isDefault = true;
  }
}

