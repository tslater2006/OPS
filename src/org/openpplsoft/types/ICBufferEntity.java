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
  PTRecord resolveContextualCBufferRecordReference(final String recName);
  PTReference<PTField> resolveContextualCBufferRecordFieldReference(
      final String recName, final String fldName);
  PTRowset resolveContextualCBufferScrollReference(final PTScrollLiteral scrollName);
  void generateKeylist(
      final String fieldName, final Keylist keylist);
  void runFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary);
  int determineScrollLevel();
}
