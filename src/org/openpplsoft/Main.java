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

    try {
      Runtime.getRuntime().addShutdownHook(new ENTShutdownHook());
      TraceFileVerifier.init(profileToRun);
      Environment.psEnvironmentName = (String) ctx.getBean("psEnvironmentName");

      Environment.setSystemVar("%Component", new PTString(profileToRun.getComponentName()));
      Environment.setSystemVar("%Menu", new PTString("SA_LEARNER_SERVICES"));
      Environment.setSystemVar("%OperatorId", new PTString("KADAMS"));
      Environment.setSystemVar("%EmployeeId", new PTString("AA0001"));
      Environment.setSystemVar("%Action_UpdateDisplay", new PTString("U"));
      Environment.setSystemVar("%Action_Add", new PTString("A"));

      /*
       * The following system vars can theoretically vary among environments.
       * However, at the moment, I am running this on identically configured
       * vanilla PS instances. Therefore, I am not going to externalize these
       * values for the time being.
       */
      Environment.setSystemVar("%Portal", new PTString("EMPLOYEE"));
      Environment.setSystemVar("%Node", new PTString("HRMS"));

      // Since we are verifying against a tracefile, we have to override
      // the default current date with the date the tracefile was generated on.
      Environment.setSystemVar("%Date", profileToRun.getTraceFileDate());

      final Component c = DefnCache.getComponent(
          (String) Environment.getSystemVar("%Component").read(), "GBL");
      final Menu m = DefnCache.getMenu(
          (String) Environment.getSystemVar("%Menu").read());

      ComponentBuffer.init(c);
      ComponentBuffer.fireEvent(PCEvent.SEARCH_INIT);
//      c.loadAndRunRecordPConSearchRecord();
  //    c.loadAndRunComponentPConSearchRecord();
      c.fillSearchRecord();

      c.loadPages();

      c.assembleComponentStructure();
      ComponentBuffer.printStructure();
      ComponentStructureVerifier.verify(profileToRun);
      ComponentBuffer.generateFromStructure();

/*
      ComponentBuffer.firstPassFill();
      c.runPreBuild();

      // TEMPORARY
      ComponentBuffer.printContents();*/
//      c.runDefaultProcessing();

      TraceFileVerifier.logVerificationSummary(false);

    } catch (final OPSVMachRuntimeException opsvmre) {
      log.fatal(opsvmre.getMessage(), opsvmre);
      TraceFileVerifier.logVerificationSummary(true);
      System.exit(1);
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
