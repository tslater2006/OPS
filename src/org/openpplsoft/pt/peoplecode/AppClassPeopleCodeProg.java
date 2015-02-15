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

  private static Logger log = LogManager.getLogger(
      AppClassPeopleCodeProg.class.getName());

  private final String[] pathParts;
  private final AppPackage rootPackage;
  private final Map<String, Instance> instanceTable;
  private final Map<String, Property> propertyTable;
  private final Map<String, Method> methodTable;
  private final Map<String, ParseTree> methodImplStartNodes;
  private final Map<String, ParseTree> propGetterImplStartNodes;
  private final Map<String, ParseTree> propSetterImplStartNodes;

  private String appClassName;
  private ParseTree classDeclNode;
  private boolean hasClassDefnBeenLoaded = false;

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
    this.propSetterImplStartNodes = new HashMap<String, ParseTree>();
  }

  public String getAppClassName() {
    return this.appClassName;
  }

  public void setAppClassName(final String appClassName) {
    this.appClassName = appClassName;
  }

  public Map<String, Property> getPropertyTable() {
    return this.propertyTable;
  }

  public Map<String, Method> getMethodTable() {
    return this.methodTable;
  }

  public Map<String, Instance> getInstanceTable() {
    return this.instanceTable;
  }

  public boolean hasClassDefnBeenLoaded() {
    return this.hasClassDefnBeenLoaded;
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

  public boolean hasPropertyGetterImpl(String propertyName) {
    if(!this.propGetterImplStartNodes.containsKey(propertyName)) {
      this.lexAndParse();
    }
    return this.propGetterImplStartNodes.containsKey(propertyName);
  }

  public boolean hasPropertySetterImpl(final String propertyName) {
    if(!this.propSetterImplStartNodes.containsKey(propertyName)) {
      this.lexAndParse();
    }
    return this.propSetterImplStartNodes.containsKey(propertyName);
  }

  public void savePropSetterImplStartNode(String propertyName, ParseTree node) {
    this.propSetterImplStartNodes.put(propertyName, node);
  }

  public ParseTree getPropSetterImplStartNode(String propertyName) {
    if(!this.propSetterImplStartNodes.containsKey(propertyName)) {
      this.lexAndParse();
    }
    return this.propSetterImplStartNodes.get(propertyName);
  }

  public boolean hasIdentifier(final String identifierName) {
    return this.instanceTable.containsKey(identifierName);
  }

  public void addInstanceIdentifier(String id, PTTypeConstraint tc) {
    log.debug("Adding instance id to table: {}, {}", id, tc);
    this.instanceTable.put(id, new Instance(id, tc));
  }

  public void addPropertyIdentifier(String id, PTTypeConstraint typeConstraint,
      boolean hasGetter, boolean hasSetter) {
    log.debug("Adding property id to table: {}, {}, getter?{}, setter?{}",
        id, typeConstraint, hasGetter, hasSetter);
    this.propertyTable.put(id, new Property(id, typeConstraint, hasGetter, hasSetter));
  }

  public void addMethod(AccessLevel aLvl, String name,
      List<FormalParam> fp, PTTypeConstraint rTypeConstraint) {
    log.debug("Adding method to table: {}, {}, {}, {}",
        aLvl.name(), name, fp, rTypeConstraint);
    this.methodTable.put(name, new Method(aLvl, name, fp, rTypeConstraint));
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

  public String getFullyQualifiedName() {
    final StringBuilder b = new StringBuilder();
    for (int i = 0; i < this.pathParts.length; i++) {
      if (i > 0) {
        b.append(":");
      }
      b.append(this.pathParts[i]);
    }
    return b.toString();
  }

  public class Instance {
    public String id;
    public PTTypeConstraint typeConstraint;
    public Instance(String i, PTTypeConstraint tc) {
      this.id = i;
      this.typeConstraint = tc;
    }
  }

  public class Property {
    public String id;
    public PTTypeConstraint typeConstraint;
    public boolean hasGetter, hasSetter;
    public Property(String i, PTTypeConstraint t, boolean g, boolean s) {
      this.id = i;
      this.typeConstraint = t;
      this.hasGetter = g;
      this.hasSetter = s;
    }
  }

  public class Method {
    public AccessLevel aLevel;
    public String name;
    public List<FormalParam> formalParams;
    public PTTypeConstraint returnTypeConstraint;
    public Method(AccessLevel a, String n, List<FormalParam> l, PTTypeConstraint rTc) {
      this.aLevel = a;
      this.name = n;
      this.formalParams = l;
      this.returnTypeConstraint = rTc;
    }
  }
}
