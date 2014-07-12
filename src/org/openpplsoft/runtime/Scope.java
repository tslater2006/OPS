/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.util.HashMap;
import java.util.Map;

import org.openpplsoft.types.*;

/**
 * Represents a set of variable identifiers for a specific
 * scope level within a PeopleCode program.
 */
public class Scope {

  /**
   * Enumerates the various scope levels available
   * in a PeopleCode program.
   */
  public enum Lvl {
    GLOBAL, COMPONENT,
    PROGRAM_LOCAL, FUNCTION_LOCAL, METHOD_LOCAL,
    APP_CLASS_OBJ_INSTANCE, APP_CLASS_OBJ_PROPERTY
  }

  private Lvl level;
  private Map<String, PTType> symbolTable;

  /**
   * Creates a new scope for the specified scope level.
   * @param l the scoping level represented by this object
   */
  public Scope(final Scope.Lvl l) {
    this.level = l;
    this.symbolTable = new HashMap<String, PTType>();
  }

  /**
   * Declares a variable in this scope attached to the provided identifier.
   * If the variable has already been declared, an RTE will be thrown; the
   * only exception to this is for Component-scoped variables, which are declared
   * in every program in which they are referenced.
   * @param id the identifier to declare
   * @param type the type to declare
   */
  public void declareVar(final String id, final PTType type) {
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
    this.symbolTable.put(id, type);
  }

  /**
   * Assigns a value to the variable attached to the provided identifier.
   * If the variable has not been declared, an RTE will be thrown.
   * @param id the identifier to assign to
   * @param newRef the reference to assign
   */
  public void assignVar(final String id, final PTType newRef) {
    final PTType currRef = this.symbolTable.get(id);

    if (currRef.typeCheck(newRef)) {
      this.symbolTable.put(id, newRef);
    } else {
      throw new OPSVMachRuntimeException("Identifier assignment failed type "
          + "check; currRef (" + currRef.toString() + ") and newRef ("
          + newRef.toString() + ") are not type compatible.");
    }
  }

  /**
   * Retrieves the current reference assigned to the provided identifier.
   * @param id the identifier to resolve
   * @return the current reference if declared, otherwise null
   */
  public PTType resolveVar(final String id) {
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
