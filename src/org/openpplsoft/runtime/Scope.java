/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.openpplsoft.types.*;

/**
 * Represents a set of variable identifiers for a specific
 * scope level within a PeopleCode program.
 */
public class Scope {

  private static Logger log = LogManager.getLogger(Scope.class.getName());

  /**
   * Enumerates the various scope levels available
   * in a PeopleCode program.
   */
  public enum Lvl {
    GLOBAL, COMPONENT,
    PROGRAM_LOCAL,
    FUNCTION_LOCAL, METHOD_LOCAL,
    APP_CLASS_OBJ_INSTANCE, APP_CLASS_OBJ_PROPERTY
  }

  private Lvl level;
  private Map<String, PTReference<PTType>> symbolTable;

  /**
   * Creates a new scope for the specified scope level.
   * @param l the scoping level represented by this object
   */
  public Scope(final Scope.Lvl l) {
    this.level = l;
    this.symbolTable = new HashMap<String, PTReference<PTType>>();
  }

  public Lvl getLevel() {
    return this.level;
  }

  public void declareVar(final String id, final PTTypeConstraint tc) {
    try {
      if(tc.isUnderlyingClassPrimitive()) {
        // Primitive references must be initialized (can't be null).
        this.declareAndInitVar(id, tc, tc.alloc());
      } else {
        this.declareAndInitVar(id, tc, new PTNull(tc));
      }
    } catch (final OPSTypeCheckException opstce) {
      throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
    }
  }

  /**
   * Declares a variable in this scope attached to the provided identifier.
   * If the variable has already been declared, an RTE will be thrown; the
   * only exception to this is for Component-scoped variables, which are declared
   * in every program in which they are referenced.
   * @param id the identifier to declare
   * @param type the type to declare
   */
  @SuppressWarnings("unchecked")
  public void declareAndInitVar(final String id, final PTTypeConstraint tc,
      final PTType initialValue) throws OPSTypeCheckException {

    if (tc == null) {
      throw new OPSVMachRuntimeException("Type constraint passed to "
          + "declareAndInitVar is null.");
    }

    if (initialValue == null) {
      throw new OPSVMachRuntimeException("Initial value passed to "
          + "declareAndInitVar is null, must be a PTType object.");
    }

    if (this.isIdResolvable(id)) {
      if(this.level == Lvl.COMPONENT) {
        // re-declaration of a Component-scoped var is normal, as this is
        // how Component-scoped variables are shared among programs
        return;
      } else {
        throw new OPSVMachRuntimeException("Encountered attempt to re-declare "
            + " variable (" + id + ") in scope level " + this.level);
      }
    }

    PTReference<PTType> newSymTableRef = null;

    /*
     * If the value provided is a reference, use that reference in the
     * symbol table. This allows references created by calling entities to be
     * modified by the callee and be seen upon return to the caller.
     */
    if (initialValue instanceof PTReference) {
      tc.typeCheck(((PTReference) initialValue).deref());
      newSymTableRef = (PTReference) initialValue;
    } else if (tc instanceof PTAnyTypeConstraint) {
      newSymTableRef = new PTAnyTypeReference(tc, initialValue);
    } else {
      newSymTableRef = new PTReference<PTType>(tc, initialValue);
    }

    log.debug("Declared {} with ref = {}", id, newSymTableRef);
    this.symbolTable.put(id, newSymTableRef);
  }

  /**
   * Retrieves the reference assigned to the provided identifier.
   * @param id the identifier to resolve
   * @return the reference if the identifier has been declared, otherwise null
   */
  public PTReference<PTType> resolveVar(final String id) {
    return this.symbolTable.get(id);
  }

  /**
   * Determines if the provided identifier has been declared in this scope.
   * @param id the identifier to check
   * @return true if the identifier has been declared in this scope,
   *    false otherwise.
   */
  public boolean isIdResolvable(final String id) {
    return this.symbolTable.containsKey(id);
  }
}
