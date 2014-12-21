/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

public enum ScrollEmissionContext {
  SEARCH_RESULTS("Search Results"),
  AFTER_SCROLL_SELECT("After ScrollSelect");

  private final String traceFileLabel;

  private ScrollEmissionContext(final String label) {
    this.traceFileLabel = label;
  }

  public String getTraceFileLabel() {
    return this.traceFileLabel;
  }

  public static ScrollEmissionContext fromLabel(final String label) {
    for (final ScrollEmissionContext ctx : ScrollEmissionContext.values()) {
      if (label.equals(ctx.getTraceFileLabel())) {
        return ctx;
      }
    }

    throw new OPSDataTypeException("Failed to find ScrollEmissionContext enum "
        + "value corresponding to the provided label: " + label);
  }
}
