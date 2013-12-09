package com.enterrupt.pt.peoplecode;

import java.sql.*;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.*;
import org.apache.logging.log4j.*;
import java.util.*;

public class AppPackage {

	public String rootPkgName;
	public PackageTreeNode rootPkgNode;

	private static Logger log = LogManager.getLogger(AppPackage.class.getName());

	private boolean hasDiscoveredAppClassPC = false;

	private class PackageTreeNode {
		public String pkgName;
		public Map<String, PackageTreeNode> subPkgs;
		public Map<String, Void> classNames;
		public PackageTreeNode(String name) {
			this.pkgName = name;
			this.subPkgs = new HashMap<String, PackageTreeNode>();
			this.classNames = new HashMap<String, Void>();
		}

		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Package: ").append(this.pkgName).append("\n");
			builder.append(" - Classes: ");
			for(Map.Entry<String, Void> entry : this.classNames.entrySet()) {
				builder.append(entry.getKey()).append(", ");
			}
			builder.append("\nSubpackages:\n");
			for(Map.Entry<String, PackageTreeNode> entry : this.subPkgs.entrySet()) {
				String subTreeStr = entry.getValue().toString();
				String[] lines = subTreeStr.split("\n");
				for(String line : lines) {
					builder.append("     ").append(line).append("\n");
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

        PreparedStatement pstmt = null;
        ResultSet rs = null;

		try {
	        pstmt = StmtLibrary.getPSPCMPROG_RecordPCList(PSDefn.APP_PACKAGE, this.rootPkgName);
   		    rs = pstmt.executeQuery();

	        /**
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

			//log.debug("\n\n\nPackage tree: \n{}\n\n\n", this.rootPkgNode);

        } catch(java.sql.SQLException sqle) {
            log.fatal(sqle.getMessage(), sqle);
            System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
        } finally {
            try {
                if(rs != null) { rs.close(); }
                if(pstmt != null) { pstmt.close(); }
            } catch(java.sql.SQLException sqle) {}
        }
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
				currNode.classNames.put(val, null);
				break;
			}

			// If we get to this point, this entry has an unexpected format.
			throw new EntVMachRuntimeException("Unexpected OBJECTID in app class path " +
				"from database: " + id);
		}
	}

  	public Map<String, Void> getClassesInPath(AppPackagePath pkgPath) {

		PackageTreeNode currNode = this.rootPkgNode;

		if(!pkgPath.parts[0].equals(this.rootPkgNode.pkgName)) {
			throw new EntVMachRuntimeException("The app package path provided (" +
				pkgPath + ") does not match the root package name (" + this.rootPkgName + ").");
		}

		for(int i = 1; i < pkgPath.parts.length; i++) {

			currNode = currNode.subPkgs.get(pkgPath.parts[i]);

			if(currNode == null) {
				throw new EntVMachRuntimeException("The app package path provided (" +
					pkgPath + ") does not resolve in the context of this package (" + this.rootPkgName + ").");
			}
		}

		//log.debug("Classes in path: {}", currNode.classNames);
		return currNode.classNames;
    }
}
