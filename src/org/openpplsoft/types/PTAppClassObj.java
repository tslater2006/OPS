/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.Map;

import org.openpplsoft.pt.peoplecode.AccessLevel;
import org.openpplsoft.pt.peoplecode.AppClassPeopleCodeProg;
import org.openpplsoft.runtime.AppClassObjMethodExecContext;
import org.openpplsoft.runtime.Callable;
import org.openpplsoft.runtime.GetterSetterCallable;
import org.openpplsoft.runtime.OPSVMachRuntimeException;
import org.openpplsoft.runtime.Scope;

public final class PTAppClassObj extends PTObjectType {

  private final AppClassPeopleCodeProg prog;
  private final Scope propertyScope, instanceScope;

  public PTAppClassObj(final PTAppClassObjTypeConstraint origTc,
      final AppClassPeopleCodeProg prog) {
    super(origTc);
    this.prog = prog;
    this.propertyScope = new Scope(Scope.Lvl.APP_CLASS_OBJ_PROPERTY);
    this.instanceScope = new Scope(Scope.Lvl.APP_CLASS_OBJ_INSTANCE);

    // Load instance identifiers into instance scope.
    for(Map.Entry<String, AppClassPeopleCodeProg.Instance> cursor :
        this.prog.getInstanceTable().entrySet()) {
      AppClassPeopleCodeProg.Instance instance = cursor.getValue();
      this.instanceScope.declareVar(instance.id, instance.typeConstraint);
    }

    // Load property identifiers into property scope.
    for(Map.Entry<String, AppClassPeopleCodeProg.Property> cursor :
        this.prog.getPropertyTable().entrySet()) {
      AppClassPeopleCodeProg.Property property = cursor.getValue();
      this.propertyScope.declareVar(property.id, property.typeConstraint);
    }
  }

  public PTType dotProperty(final String s) {

    /*
     * Properties *without* a getter are to be considered simple data entries;
     * return the value associated with them in this object's property scope.
     * Note that properties are always public, so no need to check access level.
     */
    if (this.prog.hasPropertyGetterImpl(s)
        && !this.prog.getPropertyTable().get(s).hasGetter) {
      return this.propertyScope.resolveVar(s);
    } else if (this.prog.hasIdentifier("&" + s)) {
      // Instance identifiers are stored in the prog defn table and in the scope
      // with ampersand prefixes; & are not allowed after ".", so we always need
      // to prefix with an ampersand when checking if the provided String is an
      // instance identifier.
      return this.instanceScope.resolveVar("&" + s);
    }

    return null;
  }

  public Callable dotMethod(final String s) {

    final GetterSetterCallable gsCallable = new GetterSetterCallable();

    /*
     * Properties with a getter are to be considered methods on the app class
     * object; return a callable accordingly. Note that properties are always
     * public, so no need to check access level.
     */
    if (this.prog.hasPropertyGetterImpl(s)
        && this.prog.getPropertyTable().get(s).hasGetter) {
      gsCallable.setGetterExecContext(new AppClassObjMethodExecContext(this, s,
          this.prog.getPropGetterImplStartNode(s),
          this.prog.getPropertyTable().get(s).typeConstraint));
    }

    /*
     * Properties with a setter are to be considered methods on the app class
     * object; return a callable accordingly. Note that properties are always
     * public, so no need to check access level.
     */
    if (this.prog.hasPropertySetterImpl(s)
        && this.prog.getPropertyTable().get(s).hasSetter) {
      gsCallable.setSetterExecContext(new AppClassObjMethodExecContext(this, s,
          this.prog.getPropSetterImplStartNode(s), null));
    }

    if (gsCallable.hasSetterExecContext() || gsCallable.hasGetterExecContext()) {
      return gsCallable;
    }

   /*
    * Note: Private methods
    * are private to the *CLASS*, not the instances of the class themselves.
    */
    if(this.prog.hasMethod(s)) {
      if(this.prog.getMethodTable().get(s).aLevel == AccessLevel.PUBLIC) {
        return new Callable(new AppClassObjMethodExecContext(this, s,
          this.prog.getMethodImplStartNode(s),
          this.prog.getMethodTable().get(s).returnTypeConstraint));
      } else {
        throw new OPSVMachRuntimeException("Encountered request for non-public "+
          "method; need to determine if the calling entity is an obj "+
          "with the same app class type as this one, since private methods "+
          "are private to all instances of the app class, not individual objects.");
      }
    }
    return null;
  }

  public AppClassPeopleCodeProg getProg() {
    return this.prog;
  }

  public Scope getInstanceScope() {
    return this.instanceScope;
  }

  public Scope getPropertyScope() {
    return this.propertyScope;
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(",prog=").append(this.prog.getDescriptor());
    return b.toString();
  }
}

