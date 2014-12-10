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
 * Represents a PeopleTools search record buffer. This class exists
 * mainly to clarify those parts of the code that expect a record buffer
 * explicitly representing a search record, rather than a plain record.
 */
public class SearchRecordBuffer extends RecordBuffer {

  private static Logger log =
      LogManager.getLogger(SearchRecordBuffer.class.getName());

  public SearchRecordBuffer(final Record searchRecDefn) {
    super(searchRecDefn);

    /**
     * By definition, search record buffers always contain all
     * fields that are part of the underlying record defn.
     */
    this.expandEntireRecordIntoBuffer();
  }
}


