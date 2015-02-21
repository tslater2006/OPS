/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import org.openpplsoft.types.PTDate;
import org.openpplsoft.types.PTDateTime;

/*
 * Represents a component to load with the OPS
 * runtime, along with tracefile info.
 */
public class ComponentRuntimeProfile {

  private final String componentName, mode, tracefileName, oprid;
  private final PTDate tracefileDate;
  private final PTDateTime tracefileDateTime;

  public ComponentRuntimeProfile(final String c, final String m,
      final String oprid, final String tfName, final String tfDate,
      final String tfDateTime) {
    this.componentName = c;
    this.mode = m;
    this.oprid = oprid;
    this.tracefileName = tfName;
    this.tracefileDate = new PTDate(tfDate);
    this.tracefileDateTime = new PTDateTime(tfDateTime);
  }

  public String getComponentName() {
    return this.componentName;
  }

  public String getMode() {
    return this.mode;
  }

  public String getOprid() {
    return this.oprid;
  }

  public String getTraceFileName() {
    return this.tracefileName;
  }

  public PTDate getTraceFileDate() {
    return this.tracefileDate;
  }

  public PTDateTime getTraceFileDateTime() {
    return this.tracefileDateTime;
  }
}
