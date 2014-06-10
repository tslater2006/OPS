/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.pt.peoplecode.*;

/**
 * Central repository for cached PeopleTools
 * definitions (records, programs, pages, etc.).
 */
public final class DefnCache {

  private static Map<String, Record> records;
  private static Map<String, PeopleCodeProg> programs;
  private static Map<String, Page> pages;
  private static Map<String, AppPackage> appPackages;

  private static Logger log = LogManager.getLogger(DefnCache.class.getName());

  static {
    records = new HashMap<String, Record>();
    programs = new HashMap<String, PeopleCodeProg>();
    pages = new HashMap<String, Page>();
    appPackages = new HashMap<String, AppPackage>();
  }

  private DefnCache() {}

  /**
   * Check if a record definition has been cached.
   * @param recName the name of the record defn to check
   * @return whether the record defn has been cached
   */
  public static boolean hasRecord(final String recName) {
    return records.containsKey(recName);
  }

  /**
   * Retrieve a record defn from the cache. If no cache entry
   * exists for the record, create one and initialize it.
   * @param recName the name of the record defn to retrieve/cache.
   * @return the cached record defn
   */
  public static Record getRecord(final String recName) {

    // Ignore requests for system records like PSXlATITEM.
    if (recName == null || recName.length() == 0
        || PSDefn.isSystemRecord(recName)) {
      return null;
    }

    Record r = records.get(recName);
    if (r == null) {
      log.debug("Caching record defn for {}", recName);
      r = new Record(recName);
      records.put(recName, r);
    }
    r.init();
    return r;
  }

  /**
   * Retrieve a page defn from the cache. If no cache entry
   * exists for the page, create one and initialize it.
   * @param pnlName the name of the page defn to retrieve/cache.
   * @return the cached page defn
   */
  public static Page getPage(final String pnlName) {

    if (pnlName.length() == 0) {
      return null;
    }

    Page p = pages.get(pnlName);
    if (p == null) {
      log.debug("Caching page defn for {}", pnlName);
      p = new Page(pnlName);
      pages.put(p.PNLNAME, p);
    }
    p.init();
    p.discoverPagePC();
    return p;
  }

  /**
   * Retrieve an app package defn from the cache. If no cache entry
   * exists for the app packge, create one and initialize it.
   * @param packageName the name of the app package defn to retrieve/cache.
   * @return the cached app package defn
   */
  public static AppPackage getAppPackage(final String packageName) {

    AppPackage ap = appPackages.get(packageName);
    if (ap == null) {
      log.debug("Caching app package defn for {}", packageName);
      ap = new AppPackage(packageName);
      appPackages.put(ap.rootPkgName, ap);
    }
    ap.discoverAppClassPC();
    return ap;
  }

  /**
   * Retrieve a program from the cache. If no cache entry
   * exists for the program, use parameter {@code prog} as the
   * corresponding cache entry.
   * @param prog the program reference to cache if prog is not in cache
   * @return the cached program
   */
  public static PeopleCodeProg getProgram(final PeopleCodeProg prog) {
    PeopleCodeProg p = programs.get(prog.getDescriptor());
    if (p == null) {
      log.debug("Caching program defn for {}", prog.getDescriptor());
      p = prog;
      programs.put(p.getDescriptor(), p);
    }
    p.init();
    return p;
  }

  /**
   * Retrieve a program from the cache by program name.
   * @param progDescriptor the fully qualified program name (includes
   * parent app packages)
   * @return the cached program, or null if not in cache
   */
  public static PeopleCodeProg getProgram(final String progDescriptor) {
    return programs.get(progDescriptor);
  }
}
