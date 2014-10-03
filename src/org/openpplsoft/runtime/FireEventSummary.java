/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

public class FireEventSummary {

  private int numEventProgsExecuted = 0;

  public FireEventSummary() {}

  public void incrementNumEventProgsExecuted() {
    this.numEventProgsExecuted++;
  }

  public int getNumEventProgsExecuted() {
    return this.numEventProgsExecuted;
  }
}

