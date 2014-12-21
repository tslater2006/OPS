/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.pt.Keylist;
import org.openpplsoft.pt.PCEvent;
import org.openpplsoft.runtime.FieldDefaultProcSummary;
import org.openpplsoft.runtime.FireEventSummary;

public interface ICBufferEntity {
  void fireEvent(final PCEvent event, final FireEventSummary fireEventSummary);
  PTBufferRecord resolveContextualCBufferRecordReference(final String recName);
  PTReference<PTBufferField> resolveContextualCBufferRecordFieldReference(
      final String recName, final String fldName);
  PTBufferRowset resolveContextualCBufferScrollReference(final PTScrollLiteral scrollName);
  void generateKeylist(
      final String fieldName, final Keylist keylist);
  void runFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary);
  int determineScrollLevel();
  void emitScrolls(final ScrollEmissionContext ctxFlag, final int indent);
}
