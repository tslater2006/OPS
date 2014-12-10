/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;

/**
 * Represents a PeopleTools search record buffer. Note that
 * search record buffers are vastly different than normal record
 * buffers, in that all fields on the record defn are always included
 * in the buffer, and that there is no need to stream a search record buffer.
 * As a result, this class intentionally does not extend from RecordBuffer
 * or implement IStreamableBuffer.
 */
public class SearchRecordBuffer {

  private static Logger log =
      LogManager.getLogger(SearchRecordBuffer.class.getName());

  private Record searchRecDefn;

  public SearchRecordBuffer(final Record searchRecDefn) {
    this.searchRecDefn = searchRecDefn;
  }

  public Record getRecDefn() {
    return this.searchRecDefn;
  }
}


