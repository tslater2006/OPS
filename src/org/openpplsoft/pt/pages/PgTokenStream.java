/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt.pages;

import java.util.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.buffers.*;
import org.apache.logging.log4j.*;

public class PgTokenStream {

  private Page p;
  private int cursor;
  private PgTokenStream pfs;
  private boolean doReadFromPfs;
  private boolean isClosed;
  private int prevOCCURSLEVEL = -1;
  private HashMap<String, Boolean> loadedPageNames;

  private static Logger log = LogManager.getLogger(PgTokenStream.class.getName());

  public PgTokenStream(String PNLNAME) {
    this.p = DefnCache.getPage(PNLNAME);
    this.loadedPageNames = new HashMap<String, Boolean>();
  }

  public PgToken next() {

    // If the end of page token has been emitted, only emit null.
    if(isClosed) {
      return null;
    }

    // If a nested page stream is active, read from that stream.
    if(doReadFromPfs) {
      return this.readFromPfs();
    }

    List<PgToken> tokens = p.getTokens();
    if(this.cursor < tokens.size()) {
      PgToken tok = tokens.get(this.cursor++);

      // Changes in scroll level must be reported to the receiver.
      if(tok.OCCURSLEVEL < this.prevOCCURSLEVEL) {
        tok.flags.add(PFlag.SCROLL_LVL_DECREMENT);
      }

      // If this is a subpage or secpage, expand it in-place to its constituent tokens.
      if(tok.flags.contains(PFlag.PAGE)) {
        if(this.loadedPageNames.get(tok.SUBPNLNAME) == null) {
          this.pfs = new PgTokenStream(tok.SUBPNLNAME);
          this.loadedPageNames.put(tok.SUBPNLNAME, true);
          this.doReadFromPfs = true;
        } else {
          this.prevOCCURSLEVEL = tok.OCCURSLEVEL; // Must save previous OCCURSLEVEL before returning.
          return this.next();
        }
      }

      // If this is a scroll bar / grid / scroll area, find its primary record name.
      if(tok.flags.contains(PFlag.SCROLL_START)) {

        int lookAheadCursor = this.cursor;
        String primaryRecNameCandidate = null;

        while(lookAheadCursor < tokens.size()) {
          PgToken lookToken = tokens.get(lookAheadCursor++);

          // Stop looking once this scroll area has ended.
          if(lookToken.OCCURSLEVEL < tok.OCCURSLEVEL) {
            break;
          }

          // Ignore groupboxes and any field without a valid RECNAME.
          if(!lookToken.flags.contains(PFlag.GROUPBOX) && lookToken.RECNAME != null
            && !lookToken.isRelatedDisplay() && lookToken.RECNAME.length() > 0) {

            /*
             * First priority is given to a RECNAME in the scroll area that *is not*
             * a child record of the current scroll buffer. However, this may not exist
             * (i.e., if there is only one field in the scroll area that has a RECNAME in the
             * buffer already). To prepare for that case, we must save the first RECNAME we come across
             * for potential use as the primary record name of this scroll area.
             */
            if(ComponentBuffer.getCurrentScrollBuffer()
                .getRecBufferTable().get(lookToken.RECNAME) == null) {
              primaryRecNameCandidate = lookToken.RECNAME;
              break;
            } else if(primaryRecNameCandidate == null) {
              // Save the candidate for consideration after while loop ends.
              primaryRecNameCandidate = lookToken.RECNAME;
            }
          }
        }

        if(primaryRecNameCandidate == null) {
          throw new OPSVMachRuntimeException("Unable to find the scroll area's primary record name.");
        } else {
          tok.primaryRecName = primaryRecNameCandidate;
        }
      }

      /*
       * If this token is not a page, but it has a SUBPNLNAME, preemptively emit it in the stream.
       * This token should be emitted immediately after the preemptively loaded page, so remind the cursor
       * by 1 to ensure that it gets picked up again later.
       */
      if(tok.SUBPNLNAME.length() > 0 && !tok.flags.contains(PFlag.PAGE)) {
        if(this.loadedPageNames.get(tok.SUBPNLNAME) == null) {
          this.pfs = new PgTokenStream(tok.SUBPNLNAME);
          this.loadedPageNames.put(tok.SUBPNLNAME, true);
          this.doReadFromPfs = true;

          this.cursor--;    // This token will be picked up again after page stream has ended.

          /*
           * Emit a new PAGE token so that the caller knows a sub page is being emitted.
           * The current token will be emitted after the sub page has finished streaming.
           * Note that prevOCCURSLEVEL is not saved before returning here; we're going to pick
           * up where we left off later so the prevOCCURSLEVEL must not be changed.
           */
          PgToken preemptivePgToken = new PgToken(PFlag.PAGE);
          preemptivePgToken.SUBPNLNAME = tok.SUBPNLNAME;
          return preemptivePgToken;
        }
      }

      this.prevOCCURSLEVEL = tok.OCCURSLEVEL;

      return tok;

    } else {
      // Before null is emitted, tell the receiver that this page has ended.
      PgToken endTok = new PgToken();
      endTok.flags.add(PFlag.END_OF_PAGE);
      this.isClosed = true;
      return endTok;
    }
  }

  public PgToken readFromPfs() {

    PgToken tok = this.pfs.next();
    if(tok == null) {
      this.doReadFromPfs = false;
      tok = this.next();
    }
    return tok;
  }
}
