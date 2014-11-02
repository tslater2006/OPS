/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import org.openpplsoft.types.PTDate;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;

/*
 * Represents a component to load with the OPS
 * runtime, along with tracefile info.
 */
public class ComponentRuntimeProfile {

  private String componentName, mode, tracefileName, oprid;
  private PTDate tracefileDate;

  public ComponentRuntimeProfile(final String c, final String m,
      final String oprid, final String tfName, final String tfDate) {
    this.componentName = c;
    this.mode = m;
    this.oprid = oprid;
    this.tracefileName = tfName;
    this.tracefileDate = new PTDate(tfDate);
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
}
