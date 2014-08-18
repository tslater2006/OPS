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

  private static Map<String, Component> components;
  private static Map<String, Record> records;
  private static Map<String, PeopleCodeProg> programs;
  private static Map<String, Page> pages;
  private static Map<String, AppPackage> appPackages;
  private static Map<String, Menu> menus;
  private static Map<Integer, MsgSet> msgSets;

  private static Logger log = LogManager.getLogger(DefnCache.class.getName());

  static {
    components = new HashMap<String, Component>();
    records = new HashMap<String, Record>();
    programs = new HashMap<String, PeopleCodeProg>();
    pages = new HashMap<String, Page>();
    appPackages = new HashMap<String, AppPackage>();
    menus = new HashMap<String, Menu>();
    msgSets = new HashMap<Integer, MsgSet>();
  }

  private DefnCache() {}

  /**
   * Retrieve a component defn from the cache. If no cache entry
   * exists for the component, create one and initialize it.
   * @param pnlgrpname the name of the component defn to retrieve/cache.
   * @param market the name of the market for the component defn
   * @return the cached record defn
   */
  public static Component getComponent(final String pnlgrpname,
      final String market) {

    if (pnlgrpname == null || pnlgrpname.length() == 0
        || market == null || market.length() == 0){
      return null;
    }

    final String key = pnlgrpname.concat(".").concat(market);
    Component c = components.get(key);
    if (c == null) {
      log.debug("Caching component defn for {}", key);
      c = new Component(pnlgrpname, market);
      components.put(key, c);
    }
    return c;
  }

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

    if (recName == null || recName.length() == 0){
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
      pages.put(p.getPNLNAME(), p);
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

  /**
   * Retrieve a menu from the cache by menu name.
   * @param menuName the MENUNAME of the menu to retrieve
   * @return the cached menu, or null if not in cache
   */
  public static Menu getMenu(final String menuName) {
    Menu m = menus.get(menuName);
    if (m == null) {
      log.debug("Caching menu defn for {}", menuName);
      m = new Menu(menuName);
      menus.put(m.getMenuName(), m);
    }
    return m;
  }

  /**
   * Retrieve a msg set from the cache by msg set number.
   * @param msgSetNum the MESSAGE_SET_NBR of the msg set to retrieve
   * @return the cached msg set, or null if not in cache
   */
  public static MsgSet getMsgSet(final int msgSetNum) {
    MsgSet m = msgSets.get(msgSetNum);
    if (m == null) {
      log.debug("Caching msg set defn for {}", msgSetNum);
      m = new MsgSet(msgSetNum);
      msgSets.put(m.getMsgSetNbr(), m);
    }
    return m;
  }
}
