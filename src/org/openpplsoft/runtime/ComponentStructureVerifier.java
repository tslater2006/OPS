/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.runtime;

import java.util.*;
import java.io.*;
import com.enterrupt.buffers.*;
import com.enterrupt.pt.*;
import org.apache.logging.log4j.*;

public class ComponentStructureVerifier {

  public static boolean hasBeenVerified = false;

  private static Logger log = LogManager.getLogger(ComponentStructureVerifier.class.getName());

  public static void verify() {

    if(hasBeenVerified) { return; }
    String pnlgrpname = System.getProperty("ComponentToLoad");

    int indent = 0;
    IStreamableBuffer buf;

    File structureFile = new File("test/" + pnlgrpname + ".structure");
    BufferedReader reader = null;

    try {
      reader = new BufferedReader(new FileReader(structureFile));
    } catch(java.io.FileNotFoundException fnfe) {
      log.fatal(fnfe.getMessage(), fnfe);
      System.exit(ExitCode.COMP_STRUCTURE_FILE_NOT_FOUND.getCode());
    }

    String line = null;
    String lineParts[];

    ComponentBuffer.resetCursors();
    while((buf = ComponentBuffer.next()) != null) {

      try {
        line = reader.readLine().trim();
      } catch(java.io.IOException ioe) {
        log.fatal(ioe.getMessage(), ioe);
        System.exit(ExitCode.FAILED_READ_FROM_COMP_STRUCT_FILE.getCode());
      }

      lineParts = line.split(";");

      if(buf instanceof ScrollBuffer) {

        ScrollBuffer sbuf = (ScrollBuffer) buf;
        indent = sbuf.scrollLevel * 3;

        StringBuilder b = new StringBuilder();
        for(int i=0; i<indent; i++){b.append(" ");}
        b.append("Scroll - Level ").append(sbuf.scrollLevel).append("\tPrimary Record: ")
            .append(sbuf.primaryRecName);
        for(int i=0; i<indent; i++){b.append(" ");}
        log.info(b.toString());
        log.info("=======================================================");

        if(lineParts.length != 3 || !lineParts[0].equals("SCROLL") ||
            Integer.parseInt(lineParts[1]) != sbuf.scrollLevel ||
            (!lineParts[2].replaceAll("-", "_").equals(sbuf.primaryRecName)
            && Integer.parseInt(lineParts[1]) > 0)) {
          throw new EntVMachRuntimeException("Incorrect/absent scroll token encountered " +
              "during component structure validation.");
        }
      } else if(buf instanceof RecordBuffer) {
        RecordBuffer rbuf = (RecordBuffer) buf;
        StringBuilder b = new StringBuilder();
        for(int i=0; i<indent; i++){b.append(" ");}
        b.append(" + ").append(rbuf.recName);
        log.info(b.toString());

        if(lineParts.length != 2 || !lineParts[0].equals("RECORD") ||
            !lineParts[1].replaceAll("-", "_").equals(rbuf.recName)) {
          throw new EntVMachRuntimeException("Incorrect/absent record token encountered " +
              "during component structure validation.");
        }
      } else {
        RecordFieldBuffer fbuf = (RecordFieldBuffer) buf;
        StringBuilder b = new StringBuilder();
        for(int i=0; i<indent; i++){b.append(" ");}
        b.append("   - ").append(fbuf.fldName);
        log.info(b.toString());

        if(lineParts.length != 2 || !lineParts[0].equals("FIELD") ||
            !lineParts[1].replaceAll("-", "_").equals(fbuf.fldName)) {
          throw new EntVMachRuntimeException("Incorrect/absent field token encountered " +
              "during component structure validation.");
        }
      }
    }

    try {
      if(!reader.readLine().trim().equals("END-COMPONENT-STRUCTURE")) {
        throw new EntVMachRuntimeException("Expected END-COMPONENT-STRUCTURE in .structure file.");
      }
    } catch(java.io.IOException ioe) {
      log.fatal(ioe.getMessage(), ioe);
      System.exit(ExitCode.FAILED_READ_FROM_COMP_STRUCT_FILE.getCode());
    }
    hasBeenVerified = true;
  }
}
