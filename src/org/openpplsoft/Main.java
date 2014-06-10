/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

/**
 * Main entry class for the OPS runtime.
 */
public final class Main {

  private static Logger log = LogManager.getLogger(Main.class.getName());

  /**
   * Default no-arg constructor.
   */
  private Main() {}

  /**
   * Main entry point for the OPS runtime.
   * @param args command line args to main
   */
  public static void main(final String[] args) {

    try {
      Runtime.getRuntime().addShutdownHook(new ENTShutdownHook());

      Environment.setSystemVar("%Component",
          System.getProperty("ComponentToLoad"));
      Environment.setSystemVar("%Menu", "SA_LEARNER_SERVICES");
      Environment.setSystemVar("%OperatorId", "KADAMS");
      Environment.setSystemVar("%EmployeeId", "AA0001");

      final Component c = new Component(
          (String) Environment.getSystemVar("%Component").read(), "GBL");
      final Menu m = new Menu(
          (String) Environment.getSystemVar("%Menu").read());

      c.getListOfComponentPC();
      c.loadAndRunRecordPConSearchRecord();
      c.loadAndRunComponentPConSearchRecord();
      c.fillSearchRecord();

      c.loadPages();

      c.assembleComponentStructure();
      ComponentBuffer.printStructure();
      ComponentStructureVerifier.verify();
      //ComponentBuffer.firstPassFill();

      TraceFileVerifier.logVerificationSummary(false);

    } catch (final EntVMachRuntimeException evmre) {
      log.fatal(evmre.getMessage(), evmre);
      TraceFileVerifier.logVerificationSummary(true);
      System.exit(ExitCode.ENT_VIRTUAL_MACH_RUNTIME_EXCEPTION.getCode());
    }
  }

  private static class ENTShutdownHook extends Thread {
    public ENTShutdownHook() {}
    public void run() {
      StmtLibrary.disconnect();
      TraceFileVerifier.closeTraceFile();
    }
  }
}
