/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.types.*;

import org.springframework.context.support.ClassPathXmlApplicationContext;

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

    // The name of the environment to access is expected at args[0]
    System.setProperty("contextFile", args[0]+".xml");
    ClassPathXmlApplicationContext ctx =
        new ClassPathXmlApplicationContext(System.getProperty("contextFile"));

    // The name of the component to load is expected at args[1]
    ComponentRuntimeProfile profileToRun =
        (ComponentRuntimeProfile) ctx.getBean(args[1]);

    // Since we are verifying against a tracefile, we have to override
    // the default date value with the date the tracefile was generated on.
    PTDate.overrideDefaultDate(profileToRun.getTraceFileDate());

    try {
      TraceFileVerifier.init(profileToRun);
      Runtime.getRuntime().addShutdownHook(new ENTShutdownHook());

      Environment.setSystemVar("%Component", profileToRun.getComponentName());
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
      ComponentStructureVerifier.verify(profileToRun);
      ComponentBuffer.firstPassFill();

      TraceFileVerifier.logVerificationSummary(false);

    } catch (final OPSVMachRuntimeException evmre) {
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
