/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt.peoplecode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.*;

import org.openpplsoft.sql.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.pt.*;

public class AppPackage {

  private static Logger log = LogManager.getLogger(AppPackage.class.getName());

  private final String rootPkgName;
  private final PackageTreeNode rootPkgNode;

  private boolean hasDiscoveredAppClassPC = false;

  private class PackageTreeNode {
    public String pkgName;
    public Map<String, PackageTreeNode> subPkgs;
    public Set<String> classNames;
    public PackageTreeNode(String name) {
      this.pkgName = name;
      this.subPkgs = new HashMap<>();
      this.classNames = new HashSet<>();
    }

    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Package: ").append(this.pkgName).append('\n');
      builder.append(" - Classes: ");
      for(final String className : this.classNames) {
        builder.append(className).append(", ");
      }
      builder.append("\nSubpackages:\n");
      for(Map.Entry<String, PackageTreeNode> entry : this.subPkgs.entrySet()) {
        String subTreeStr = entry.getValue().toString();
        String[] lines = subTreeStr.split("\n");
        for(String line : lines) {
          builder.append("     ").append(line).append('\n');
        }
      }
      return builder.toString();
    }
  }

  public AppPackage(String pName) {
    this.rootPkgName = pName;
    this.rootPkgNode = new PackageTreeNode(this.rootPkgName);
  }

  public void discoverAppClassPC() {

    if(this.hasDiscoveredAppClassPC) { return; }
    this.hasDiscoveredAppClassPC = true;

    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSPCMPROG_RecordPCList",
        new String[]{PSDefn.APP_PACKAGE, this.rootPkgName});
    OPSResultSet rs = ostmt.executeQuery();

    /*
     * NOTE: BE VERY CAREFUL HERE; not only is the result set not ordered by PROGSEQ, but
     * there may be multiple entries per program if it is greater than 28,000 bytes.
     * Keep those considerations in mind when using the records returned here.
     */
    while(rs.next()) {
      // We have 6 pairs of OBJECTID/OBJECTVALUEs that we want to save;
      // skip the first pair b/c it's just the name of this (root) app pkg.
      String[][] pkgEntryParts = new String[6][2];
      for(int i = 0; i < 6; i++) {
        pkgEntryParts[i] = new String[]{rs.getString("OBJECTID"+(i+2)),
          rs.getString("OBJECTVALUE"+(i+2))};
      }
      this.addToSubpkgTree(pkgEntryParts);
    }

    rs.close();
    ostmt.close();

    //log.debug("\n\n\nPackage tree: \n{}\n\n\n", this.rootPkgNode);
  }

  private void addToSubpkgTree(String[][] pkgEntryParts) {

    int expectedSubPkgId = 105;
    PackageTreeNode currNode = this.rootPkgNode;

    for(int i = 0; i < pkgEntryParts.length; i++) {
      int id = Integer.parseInt(pkgEntryParts[i][0]);
      String val = pkgEntryParts[i][1];

      // If we've reached a subpackage (in the proper order),
      // save it at the root level or in the last traversed subpackage.
      if(id == expectedSubPkgId && id < 107) {

        PackageTreeNode node = currNode.subPkgs.get(val);
        if(node == null) {
          node = new PackageTreeNode(val);
          currNode.subPkgs.put(val, node);
        }

        currNode = node;
        expectedSubPkgId++;
        continue;
      }

      // We've reached the app class; is either root or in a sub package.
      // Once we've reached the app class field, we can stop looping.
      if(id == 107) {
        currNode.classNames.add(val);
        break;
      }

      // If we get to this point, this entry has an unexpected format.
      throw new OPSVMachRuntimeException("Unexpected OBJECTID in app class path " +
        "from database: " + id);
    }
  }

  public Set<String> getClassesInPath(AppPackagePath pkgPath) {

    PackageTreeNode currNode = this.rootPkgNode;
    final String[] pathParts = pkgPath.getParts();

    if(!pathParts[0].equals(this.rootPkgNode.pkgName)) {
      throw new OPSVMachRuntimeException("The app package path provided ("
          + pkgPath + ") does not match the root package name ("
          + this.rootPkgName + ").");
    }

    for(int i = 1; i < pathParts.length; i++) {

      PackageTreeNode newCurrNode = currNode.subPkgs.get(pathParts[i]);

      if(newCurrNode != null) {
        currNode = newCurrNode;
      } else if(currNode.classNames.contains(pathParts[i])) {
        /*
         * Apparently in PS it is possible to "import $Pkg:$Subpkg:$Class:*",
         * which makes no sense to me at all. This check will catch instances
         * where the last part of the path is actually a class name rather than
         * a subpackage name.
         */
        return currNode.classNames;
      } else {
        throw new OPSVMachRuntimeException("The app package path provided ("
            + pkgPath + ") does not resolve in the context of this package ("
            + this.rootPkgName + ").");
      }
    }

    //log.debug("Classes in path: {}", currNode.classNames);
    return currNode.classNames;
  }

  public String getRootPkgName() {
    return this.rootPkgName;
  }
}
