/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

/**
 * All PT buffer definitions that have a need to be streamable
 * in the OPS runtime must implement this interface.
 */
public interface IStreamableBuffer {
  /**
   * Retrieves the next buffer in the read sequence.
   * @return the next buffer in the read sequence, or null
   *    if all child buffers have been read.
   */
  IStreamableBuffer next();

  /**
   * Resets the read cursors for this and all child buffers;
   * call will be propagated to all children.
   */
  void resetCursors();
}
