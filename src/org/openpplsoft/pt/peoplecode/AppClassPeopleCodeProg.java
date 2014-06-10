/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt.peoplecode;

import java.util.*;
import java.lang.StringBuilder;
import org.openpplsoft.runtime.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.types.*;
import org.apache.logging.log4j.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class AppClassPeopleCodeProg extends PeopleCodeProg {

  private static Logger log = LogManager.getLogger(AppClassPeopleCodeProg.class.getName());

  public String appClassName;
  private ParseTree classDeclNode;
  public String[] pathParts;
  public AppPackage rootPackage;
  public Map<String, Instance> instanceTable;
  public Map<String, Property> propertyTable;
  public Map<String, Method> methodTable;
  private Map<String, ParseTree> methodImplStartNodes;
  private Map<String, ParseTree> propGetterImplStartNodes;
  public boolean hasClassDefnBeenLoaded = false;

  public class Instance {
    public String id;
    public PTType type;
    public Instance(String i, PTType t) {
      this.id = i;
      this.type = t;
    }
  }

  public class Property {
    public String id;
    public PTType type;
    public boolean hasGetter;
    public Property(String i, PTType t, boolean g) {
      this.id = i;
      this.type = t;
      this.hasGetter = g;
    }
  }

  public class Method {
    public AccessLevel aLevel;
    public String name;
    public List<FormalParam> formalParams;
    public PTType returnType;
    public Method(AccessLevel a, String n, List<FormalParam> l, PTType r) {
      this.aLevel = a;
      this.name = n;
      this.formalParams = l;
      this.returnType = r;
    }
  }

  public AppClassPeopleCodeProg(String[] path) {
    super();
    this.pathParts = path;
    this.event = "OnExecute";
    this.initBindVals();
    this.rootPackage = DefnCache.getAppPackage(this.bindVals[1]);
    this.instanceTable = new HashMap<String, Instance>();
    this.propertyTable = new HashMap<String, Property>();
    this.methodTable = new HashMap<String, Method>();
    this.methodImplStartNodes = new HashMap<String, ParseTree>();
    this.propGetterImplStartNodes = new HashMap<String, ParseTree>();
  }

  protected void initBindVals() {

    /*
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

  public boolean hasConstructor() {
    return this.hasMethod(this.appClassName);
  }

  public boolean hasMethod(String methodName) {
    if(!this.methodImplStartNodes.containsKey(methodName)) {
      this.lexAndParse();
    }
    return this.methodImplStartNodes.containsKey(methodName);
  }

  public void saveMethodImplStartNode(String methodName, ParseTree node) {
    this.methodImplStartNodes.put(methodName, node);
  }

  public ParseTree getMethodImplStartNode(String methodName) {
    if(!this.methodImplStartNodes.containsKey(methodName)) {
      this.lexAndParse();
    }
    return this.methodImplStartNodes.get(methodName);
  }

  public void savePropGetterImplStartNode(String propertyName, ParseTree node) {
    this.propGetterImplStartNodes.put(propertyName, node);
  }

  public ParseTree getPropGetterImplStartNode(String propertyName) {
    if(!this.propGetterImplStartNodes.containsKey(propertyName)) {
      this.lexAndParse();
    }
    return this.propGetterImplStartNodes.get(propertyName);
  }

  public boolean hasPropertyGetter(String propertyName) {
    if(!this.propGetterImplStartNodes.containsKey(propertyName)) {
      this.lexAndParse();
    }
    return this.propGetterImplStartNodes.containsKey(propertyName);
  }

  public void addInstanceIdentifier(String id, PTType type) {
    log.debug("Adding instance id to table: {}, {}", id, type);
    this.instanceTable.put(id, new Instance(id, type));
  }

  public void addPropertyIdentifier(String id, PTType type,
      boolean hasGetter) {
    log.debug("Adding property id to table: {}, {}, getter?{}",
        id, type, hasGetter);
    this.propertyTable.put(id, new Property(id, type, hasGetter));
  }

  public void addMethod(AccessLevel aLvl, String name,
      List<FormalParam> fp, PTType rType) {
    log.debug("Adding method to table: {}, {}, {}, {}",
        aLvl.name(), name, fp, rType);
    this.methodTable.put(name, new Method(aLvl, name, fp, rType));
  }

  public void setClassDeclNode(ParseTree node) {
    this.classDeclNode = node;
  }

  public ParseTree getClassDeclNode() {
    if(this.classDeclNode == null) {
      this.lexAndParse();
    }
    return this.classDeclNode;
  }
}
