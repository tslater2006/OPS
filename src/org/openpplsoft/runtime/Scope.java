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

  private class SymbolTableEntry {
    public String symbolId;
    public PTTypeConstraint typeConstraint;
    public PTReference ref;
    public SymbolTableEntry(final String s, final PTTypeConstraint tc) {
      this.ref = new PTReference();
      this.symbolId = s;
      this.typeConstraint = tc;
    }
  }

  private Lvl level;
  private Map<String, SymbolTableEntry> symbolTable;

  /**
   * Creates a new scope for the specified scope level.
   * @param l the scoping level represented by this object
   */
  public Scope(final Scope.Lvl l) {
    this.level = l;
    this.symbolTable = new HashMap<String, SymbolTableEntry>();
  }

  public Lvl getLevel() {
    return this.level;
  }

  /**
   * Declares a variable in this scope attached to the provided identifier.
   * If the variable has already been declared, an RTE will be thrown; the
   * only exception to this is for Component-scoped variables, which are declared
   * in every program in which they are referenced.
   * @param id the identifier to declare
   * @param type the type to declare
   */
  public void declareVar(final String id, final PTTypeConstraint typeConstraint) {
    if (typeConstraint == null) {
      throw new OPSVMachRuntimeException("Illegal call to declareVar; typeConstraint is null."
          + " It's possible that this call to declareVar is trying to declare a variable that "
          + "has no type defined in PeopleCode, in which case the caller needs to pass a type "
          + "constraint for the Any type.");
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

    SymbolTableEntry newEntry = new SymbolTableEntry(id, typeConstraint);

    // Primitive references must be initialized (can't be null).
    if(typeConstraint.isUnderlyingClassPrimitive()) {
      newEntry.ref.pointTo(typeConstraint.alloc());
    }

    log.debug("Declared {} with ref = {}", id, newEntry.ref);
    this.symbolTable.put(id, newEntry);
  }

  /**
   * Assigns a value to the variable attached to the provided identifier.
   * If the variable has not been declared, an RTE will be thrown.
   * @param id the identifier to assign to
   * @param newVal the reference to assign
   */
  public void assignVar(final String id, final PTType newVal) {
    SymbolTableEntry symEntry = this.symbolTable.get(id);

    if (newVal == null) {
      throw new OPSVMachRuntimeException("newVal is null (Java null) in "
          + "assignVar; this is illegal and indicative of an assignment "
          + "problem somewhere.");
    }

    if (symEntry.typeConstraint.typeCheck(newVal)) {
      /*
       * Regardless of whether the newVal is an object or primitive, assignment
       * to identifiers alters the reference, not the existing referenced value.
       */
      symEntry.ref.pointTo(newVal);
    } else if (newVal instanceof PTField) {
      // Unwrap the enclosed value and assign that value to the provided identifier.
      this.assignVar(id, ((PTField) newVal).getValue());
    } else {
      throw new OPSVMachRuntimeException("Identifier assignment failed type "
          + "check and/or PTField unboxing; typeConstraint ("
          + symEntry.typeConstraint + ") and newVal ("
          + newVal + ") are not type compatible.");
    }
  }

  /**
   * Retrieves the reference assigned to the provided identifier.
   * @param id the identifier to resolve
   * @return the reference if the identifier has been declared, otherwise null
   */
  public PTReference resolveVar(final String id) {
    return this.symbolTable.get(id).ref;
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
