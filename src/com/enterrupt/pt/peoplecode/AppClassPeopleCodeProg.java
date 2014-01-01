package com.enterrupt.pt.peoplecode;

import java.util.*;
import java.lang.StringBuilder;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.*;
import com.enterrupt.types.*;
import org.apache.logging.log4j.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class AppClassPeopleCodeProg extends PeopleCodeProg {

	private static Logger log = LogManager.getLogger(AppClassPeopleCodeProg.class.getName());

	public String appClassName;
	public ParseTree classDeclNode;
	public String[] pathParts;
	public AppPackage rootPackage;
	public Map<String, ParseTree> methodEntryPoints;
	public Map<String, List<String>> methodFormalParams;
	public Map<String, Instance> instanceTable;
	public boolean hasClassDefnBeenLoaded = false;

	public class Instance {
		public AccessLevel aLevel;
		public String id;
		public PTType type;
		public Instance(AccessLevel a, String i, PTType t) {
			this.aLevel = a;
			this.id = i;
			this.type = t;
		}
	}

	public AppClassPeopleCodeProg(String[] path) {
		super();
		this.pathParts = path;
		this.event = "OnExecute";
		this.initBindVals();
		this.rootPackage = DefnCache.getAppPackage(this.bindVals[1]);
		this.methodEntryPoints = new HashMap<String, ParseTree>();
		this.methodFormalParams = new HashMap<String, List<String>>();
		this.instanceTable = new HashMap<String, Instance>();
	}

	protected void initBindVals() {

        /**
         * Due to the variable length nature of app class paths,
         * we need to determine which bind values we'll be querying the database
         * with based on the length provided and knowledge of OBJECTID constants.
         */
        int pathPartIdx = 0;
        int nextObjectId = 105; // 105 through 106 == subpackage
        this.bindVals = new String[14];
        for(int i = 0; i < this.bindVals.length; i+=2) {
            if(i == 0) {
                this.bindVals[0] = "104"; // 104 == root App Package
                this.bindVals[1] = pathParts[pathPartIdx++];
            } else if(pathPartIdx == (pathParts.length - 1)) {
                this.bindVals[i] = "107"; // 107 == final App Class in hierarchy
                this.bindVals[i+1] = pathParts[pathPartIdx++];
            } else if(pathPartIdx < pathParts.length) {
                this.bindVals[i] = "" + nextObjectId++;
                this.bindVals[i+1] = pathParts[pathPartIdx++];
            } else {

                // The event OBJECTID/OBJECTVAL pair must follow the final app class.
                if(this.bindVals[i-2].equals("107")) {
                    this.bindVals[i] = "12";      // 12 == Event
                    this.bindVals[i+1] = this.event;
                } else {
                    this.bindVals[i] = "0";
                    this.bindVals[i+1] = PSDefn.NULL;
                }
            }
        }
	}

	public String getDescriptor() {

		StringBuilder sb = new StringBuilder();
		sb.append("AppClassPC.");
		for(int i = 0; i < this.pathParts.length; i++) {
			sb.append(this.pathParts[i]).append(".");
		}
		sb.append(this.event);
		return sb.toString();
	}

    public void saveMethodEntryPoint(String methodName, ParseTree entryPoint) {
        this.methodEntryPoints.put(methodName, entryPoint);
	}

	public void addInstanceIdentifier(AccessLevel aLvl, String id, PTType type) {
		log.debug("Adding instance id to table: {}, {}, {}", aLvl.name(), id, type);
		this.instanceTable.put(id, new Instance(aLvl, id, type));
	}

	public void addFormalParamForMethod(String methodName, String paramId) {
		List<String> paramList = this.methodFormalParams.get(methodName);
		if(paramList == null) {
			paramList = new ArrayList<String>();
			this.methodFormalParams.put(methodName, paramList);
		}
		paramList.add(paramId);
	}
}
