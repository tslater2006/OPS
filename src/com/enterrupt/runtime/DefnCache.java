/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.runtime;

import java.util.HashMap;
import com.enterrupt.pt.*;
import com.enterrupt.pt.peoplecode.*;
import org.apache.logging.log4j.*;

public class DefnCache {

  private static HashMap<String, Record> records;
  private static HashMap<String, PeopleCodeProg> programs;
  private static HashMap<String, Page> pages;
  private static HashMap<String, AppPackage> appPackages;

  private static Logger log = LogManager.getLogger(DefnCache.class.getName());

  static {
    records = new HashMap<String, Record>();
    programs = new HashMap<String, PeopleCodeProg>();
    pages = new HashMap<String, Page>();
    appPackages = new HashMap<String, AppPackage>();
  }

  public static boolean hasRecord(String RECNAME) {
    return records.containsKey(RECNAME);
  }

  public static Record getRecord(String RECNAME) {

    // Ignore requests for system records like PSXlATITEM.
    if(RECNAME == null || RECNAME.length() == 0
      || PSDefn.isSystemRecord(RECNAME)) {
      return null;
    }

    Record r = records.get(RECNAME);
    if(r == null) {
      log.debug("Caching record defn for {}", RECNAME);
      r = new Record(RECNAME);
      records.put(RECNAME, r);
    }
    r.init();
    return r;
  }

  public static Page getPage(String PNLNAME) {

    if(PNLNAME.length() == 0) {
      return null;
    }

    Page p = pages.get(PNLNAME);
    if(p == null) {
      log.debug("Caching page defn for {}", PNLNAME);
      p = new Page(PNLNAME);
      pages.put(p.PNLNAME, p);
    }
    p.init();
    p.discoverPagePC();
    return p;
  }

  public static AppPackage getAppPackage(String packageName) {

    AppPackage ap = appPackages.get(packageName);
    if(ap == null) {
      log.debug("Caching app package defn for {}", packageName);
      ap = new AppPackage(packageName);
      appPackages.put(ap.rootPkgName, ap);
    }
    ap.discoverAppClassPC();
    return ap;
  }

  public static PeopleCodeProg getProgram(PeopleCodeProg prog) {
    PeopleCodeProg p = programs.get(prog.getDescriptor());
    if(p == null) {
      log.debug("Caching program defn for {}", prog.getDescriptor());
      p = prog;
      programs.put(p.getDescriptor(), p);
    }
    p.init();
    return p;
  }

  public static PeopleCodeProg getProgram(String progDescriptor) {
    return programs.get(progDescriptor);
  }
}
