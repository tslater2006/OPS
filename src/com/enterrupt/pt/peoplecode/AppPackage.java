package com.enterrupt.pt.peoplecode;

import java.sql.*;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.*;
import org.apache.logging.log4j.*;
import java.util.*;

public class AppPackage {

	public String rootPkgName;
	private Map<String, SubPackage> subPkgs;
	public Map<String, Void> classesInRootPkg;

	private static Logger log = LogManager.getLogger(AppPackage.class.getName());

	private boolean hasDiscoveredAppClassPC = false;

	private class SubPackage {
		public String subPkgName;
		public Map<String, SubPackage> subPkgs;
		public Map<String, Void> classesInPkg;
		public SubPackage(String name) {
			this.subPkgName = name;
			this.subPkgs = new HashMap<String, SubPackage>();
			this.classesInPkg = new HashMap<String, Void>();
		}
	}

	public AppPackage(String pName) {
		this.rootPkgName = pName;
		this.subPkgs = new HashMap<String, SubPackage>();
		this.classesInRootPkg = new HashMap<String, Void>();
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

  	public Map<String, Void> getClassesInPath(AppPackagePath pkgPath) {

		if(!pkgPath.parts[0].equals(this.rootPkgName)) {
			throw new EntVMachRuntimeException("The app package path provided belongs to " +
				"a root package other than this one.");
		}

		// If path just contains root package name, return root-level classes.
		if(pkgPath.parts.length == 1) {
			return this.classesInRootPkg;
		}

		// Otherwise, descend the subpackage tree according to the path.
		SubPackage subPkg = null;
		for(String part : pkgPath.parts) {
			if(subPkg == null) {
				subPkg = this.subPkgs.get(part);
			} else {
				subPkg = subPkg.subPkgs.get(part);
			}

			if(subPkg == null) {
				throw new EntVMachRuntimeException("The path provided cannot be resolved " +
					"in the context of this app package.");
			}
		}

		return subPkg.classesInPkg;
    }

	private void addToSubpkgTree(String[][] pkgEntryParts) {

		int expectedSubPkgId = 105;
		SubPackage parentSubPkg = null;

		for(int i = 0; i < pkgEntryParts.length; i++) {
			int id = Integer.parseInt(pkgEntryParts[i][0]);
			String val = pkgEntryParts[i][1];

			// If we've reached a subpackage (in the proper order),
			// save it at the root level or in the last traversed subpackage.
			if(id == expectedSubPkgId && id < 107) {

				SubPackage subPkg;
				if(parentSubPkg == null) {
					subPkg = this.subPkgs.get(val);
					if(subPkg == null) {
						subPkg = new SubPackage(val);
						this.subPkgs.put(val, subPkg);
					}
				} else {
					subPkg = parentSubPkg.subPkgs.get(val);
					if(subPkg == null) {
						subPkg = new SubPackage(val);
						parentSubPkg.subPkgs.put(val, subPkg);
					}
				}
				parentSubPkg = subPkg;
				expectedSubPkgId++;
				continue;
			}

			// We've reached the app class; is either root or in a sub package.
			if(id == 107) {
				if(parentSubPkg == null) {
					this.classesInRootPkg.put(val, null);
				} else {
					parentSubPkg.classesInPkg.put(val, null);
				}
				break;
			}

			// Once we reach the event field, we can stop.
			if(id == Integer.parseInt(PSDefn.EVENT)) {
				break;
			}

			// If we get to this point, this entry has an unexpected format.
			throw new EntVMachRuntimeException("Unexpected OBJECTID in app class path " +
				"from database: " + id);
		}
	}
}
