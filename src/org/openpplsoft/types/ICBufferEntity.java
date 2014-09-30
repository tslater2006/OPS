/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.pt.PCEvent;

public interface ICBufferEntity {
  void fireEvent(final PCEvent event);
  PTType resolveContextualCBufferReference(final String identifier);
  PTPrimitiveType findValueForKeyInCBufferContext(
      final String fieldName) throws OPSCBufferKeyLookupException;
}
