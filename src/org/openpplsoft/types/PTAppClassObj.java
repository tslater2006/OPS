/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.pt.*;
import java.util.*;
import org.openpplsoft.types.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.runtime.*;

public final class PTAppClassObj extends PTObjectType {

  private static Type staticTypeFlag = Type.APP_CLASS_OBJ;
  public AppClassPeopleCodeProg progDefn;
  public Scope propertyScope;
  public Scope instanceScope;

  public PTAppClassObj(AppClassPeopleCodeProg prog) {
    super(staticTypeFlag);
    this.progDefn = prog;
    this.propertyScope = new Scope(Scope.Lvl.APP_CLASS_OBJ_PROPERTY);
    this.instanceScope = new Scope(Scope.Lvl.APP_CLASS_OBJ_INSTANCE);

    // Load instance identifiers into instance scope.
    for(Map.Entry<String, AppClassPeopleCodeProg.Instance> cursor :
        this.progDefn.instanceTable.entrySet()) {
      AppClassPeopleCodeProg.Instance instance = cursor.getValue();

      this.instanceScope.declareVar(instance.id, instance.typeConstraint);

      /*
       * Primitive instance variables must have space allocated for them
       * immediately.
       */
      if(instance.typeConstraint.isUnderlyingClassPrimitive()) {
        this.instanceScope.assignVar(instance.id,
            instance.typeConstraint.alloc());
      }
    }

    // Load property identifiers into property scope.
    for(Map.Entry<String, AppClassPeopleCodeProg.Property> cursor :
        this.progDefn.propertyTable.entrySet()) {
      AppClassPeopleCodeProg.Property property = cursor.getValue();

      this.propertyScope.declareVar(property.id, property.typeConstraint);

      /*
       * Primitive properties *that lack getters* must have space allocated for them
       * immediately.
       */
      if(!property.hasGetter &&
            property.typeConstraint.isUnderlyingClassPrimitive()) {
        this.propertyScope.declareVar(property.id,
          property.typeConstraint);
      }
    }
  }

  public PTType dotProperty(String s) {

    /*
     * Properties *without* a getter are to be considered simple data entries;
     * return the value associated with them in this object's property scope.
     * Note that properties are always public, so no need to check access level.
     */
    if(this.progDefn.hasPropertyGetter(s)) {
      if(!this.progDefn.propertyTable.get(s).hasGetter) {
        return this.propertyScope.resolveVar(s);
      }
    }

    return null;
  }

  public Callable dotMethod(String s) {

    /*
     * Properties with a getter are to be considered methods on the app class
     * object; return a callable accordingly. Note that properties are always
     * public, so no need to check access level.
     */
    if(this.progDefn.hasPropertyGetter(s)) {
      if(this.progDefn.propertyTable.get(s).hasGetter) {
        return new Callable(new AppClassObjGetterExecContext(this, s,
          this.progDefn.getPropGetterImplStartNode(s),
          this.progDefn.propertyTable.get(s).typeConstraint));
      }
    }

   /*
    * Note: Private methods
    * are private to the *CLASS*, not the instances of the class themselves.
    */
    if(this.progDefn.hasMethod(s)) {
      if(this.progDefn.methodTable.get(s).aLevel == AccessLevel.PUBLIC) {
        return new Callable(new AppClassObjMethodExecContext(this, s,
          this.progDefn.getMethodImplStartNode(s),
          this.progDefn.methodTable.get(s).returnTypeConstraint));
      } else {
        throw new OPSVMachRuntimeException("Encountered request for non-public "+
          "method; need to determine if the calling entity is an obj "+
          "with the same app class type as this one, since private methods "+
          "are private to all instances of the app class, not individual objects.");
      }
    }
    return null;
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTAppClassObj &&
      this.getType() == a.getType() &&
      this.progDefn == ((PTAppClassObj)a).progDefn);
  }

  public static PTAppClassObj getSentinel(AppClassPeopleCodeProg prog) {

    // If the sentinel has already been cached, return it immediately.
    String cacheKey = getCacheKey(prog);
    if(PTType.isSentinelCached(cacheKey)) {
      return (PTAppClassObj)PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    PTAppClassObj sentinelObj = new PTAppClassObj(prog);
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  public PTAppClassObj alloc() {
    PTAppClassObj newObj = new PTAppClassObj(this.progDefn);
    PTType.clone(this, newObj);
    return newObj;
  }

  private static String getCacheKey(AppClassPeopleCodeProg prog) {
    StringBuilder b = new StringBuilder(staticTypeFlag.name());
    b.append(":").append(prog.getDescriptor());
    return b.toString();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(",prog=").append(this.progDefn.getDescriptor());
    return b.toString();
  }
}

