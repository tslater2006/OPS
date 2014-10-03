/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.pt.PCEvent;
import org.openpplsoft.runtime.FieldDefaultProcSummary;
import org.openpplsoft.runtime.FireEventSummary;

public interface ICBufferEntity {
  void fireEvent(final PCEvent event, final FireEventSummary fireEventSummary);
  PTType resolveContextualCBufferReference(final String identifier);
  PTPrimitiveType findValueForKeyInCBufferContext(
      final String fieldName) throws OPSCBufferKeyLookupException;
  void runFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary);
}
