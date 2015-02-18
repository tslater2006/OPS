/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.buffers.ComponentBuffer;
import org.openpplsoft.buffers.IStreamableBuffer;
import org.openpplsoft.buffers.RecordBuffer;
import org.openpplsoft.buffers.RecordFieldBuffer;
import org.openpplsoft.buffers.ScrollBuffer;
import org.openpplsoft.pt.PSDefn;

public class ComponentStructureVerifier {

  private static boolean hasBeenVerified = false;

  private static Logger log = LogManager.getLogger(
      ComponentStructureVerifier.class.getName());

  public static boolean hasBeenVerified() {
    return hasBeenVerified;
  }

  public static void verify(ComponentRuntimeProfile profile) {

    if(hasBeenVerified) { return; }
    String pnlgrpname = profile.getComponentName();

    int indent = 0;
    IStreamableBuffer buf;

    File structureFile = new File("test/" + pnlgrpname + ".structure");

    try (final BufferedReader reader =
          new BufferedReader(new FileReader(structureFile))) {

      String line = null;
      String lineParts[];
      boolean skipUpcomingRecordFields = false;

      ComponentBuffer.resetCursors();
      while((buf = ComponentBuffer.next()) != null) {

        if (!skipUpcomingRecordFields) {
          line = reader.readLine().trim();
        }

        lineParts = line.split(";");

        if(buf instanceof ScrollBuffer) {

          ScrollBuffer sbuf = (ScrollBuffer) buf;
          skipUpcomingRecordFields = false;
          indent = sbuf.getScrollLevel() * 3;

          StringBuilder b = new StringBuilder();
          for(int i=0; i<indent; i++){b.append(' ');}
          b.append("Scroll - Level ").append(sbuf.getScrollLevel()).append("\tPrimary Record: ")
              .append(sbuf.getPrimaryRecName());
          for(int i=0; i<indent; i++){b.append(' ');}
          log.info(b.toString());
          log.info("=======================================================");

          if(lineParts.length != 3 || !lineParts[0].equals("SCROLL") ||
              Integer.parseInt(lineParts[1]) != sbuf.getScrollLevel() ||
              (!lineParts[2].replaceAll("-", "_").equals(sbuf.getPrimaryRecName())
              && Integer.parseInt(lineParts[1]) > 0)) {
            throw new OPSVMachRuntimeException("Incorrect/absent scroll token encountered " +
                "during component structure validation.");
          }
        } else if(buf instanceof RecordBuffer) {

          RecordBuffer rbuf = (RecordBuffer) buf;
          skipUpcomingRecordFields = false;

          if (!rbuf.doesContainStructuralFields()
              || PSDefn.isSystemRecord(rbuf.getRecDefn().getRecName())) {
            skipUpcomingRecordFields = true;
            continue;
          }

          StringBuilder b = new StringBuilder();
          for(int i=0; i<indent; i++){b.append(' ');}
          b.append(" + ").append(rbuf.getRecDefn().getRecName());
          log.info(b.toString());

          if(lineParts.length != 2 || !lineParts[0].equals("RECORD") ||
              !lineParts[1].replaceAll("-", "_").equals(rbuf.getRecDefn().getRecName())) {
            throw new OPSVMachRuntimeException("Incorrect/absent record token encountered " +
                "during component structure validation; expected: " + line + "; received: "
                + rbuf.getRecDefn().getRecName());
          }
        } else {

          if (skipUpcomingRecordFields) {
            continue;
          }

          RecordFieldBuffer fbuf = (RecordFieldBuffer) buf;
          StringBuilder b = new StringBuilder();
          for(int i=0; i<indent; i++){b.append(' ');}
          b.append("   - ").append(fbuf.getFldName());
          log.info(b.toString());

          if(lineParts.length != 2 || !lineParts[0].equals("FIELD") ||
              !lineParts[1].replaceAll("-", "_").equals(fbuf.getFldName())) {
            throw new OPSVMachRuntimeException("Incorrect/absent field token encountered " +
                "during component structure validation.");
          }
        }
      }

      if (!skipUpcomingRecordFields) {
        line = reader.readLine();
      }

      if(!line.trim().equals("END-COMPONENT-STRUCTURE")) {
        throw new OPSVMachRuntimeException("Expected END-COMPONENT-STRUCTURE in .structure file.");
      }

      hasBeenVerified = true;
    } catch(final IOException ioe) {
      throw new OPSVMachRuntimeException(ioe.getMessage(), ioe);
    }
  }
}

