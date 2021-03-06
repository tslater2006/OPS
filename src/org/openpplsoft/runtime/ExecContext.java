/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.util.LinkedList;

import org.antlr.v4.runtime.tree.ParseTree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.peoplecode.PeopleCodeProg;
import org.openpplsoft.types.OPSTypeCheckException;
import org.openpplsoft.types.PTAnyTypeConstraint;
import org.openpplsoft.types.PTType;
import org.openpplsoft.types.PTTypeConstraint;

public abstract class ExecContext {

  private static final Logger log = LogManager.getLogger(
      ExecContext.class.getName());

  private final PeopleCodeProg prog;
  private final LinkedList<Scope> scopeStack;

  protected ParseTree startNode;

  /*
   * The level (scroll) the program to be run resides on, along with
   * the row of the of that level (scroll) the program is being run in.
   * App class exec contexts do not have these values set at instantiation b/c
   * they are not wedded to the component processor; the interpreter supervisor
   * will set the values manually (using the values of the context immediately
   * preceding it on the stack) before loading those contexts onto the stack.
   */
  private int execCBufferScrollLevel = -1, execCBufferRowIdx = -1;

  public ExecContext(final PeopleCodeProg p) {
    this.prog = p;
    this.startNode = p.getParseTree();
    this.scopeStack = new LinkedList<Scope>();
  }

  public abstract String getMethodOrFuncName();

  public PeopleCodeProg getProg() {
    return this.prog;
  }

  public ParseTree getStartNode() {
    return this.startNode;
  }

  public LinkedList<Scope> getScopeStack() {
    return this.scopeStack;
  }

  public void setExecutionScrollLevel(final int lvl) {
    this.execCBufferScrollLevel = lvl;
  }

  public int getExecutionScrollLevel() {
    return this.execCBufferScrollLevel;
  }

  public void setExecutionRowIdx(final int idx) {
    this.execCBufferRowIdx = idx;
  }

  public int getExecutionRowIdx() {
    return this.execCBufferRowIdx;
  }

  public void pushScope(Scope s) {
    // Place the scope at the front of the linked list.
    this.scopeStack.push(s);
  }

  public void popScope() {
    // Remove the scope from the front of the linked list.
    this.scopeStack.pop();
  }

  public void declareLocalVar(final String id, final PTTypeConstraint tc) {
    Scope topMostScope = this.scopeStack.peekFirst();
    topMostScope.declareVar(id, tc);
  }

  public void declareAndInitLocalVar(final String id, final PTTypeConstraint tc,
      final PTType initialVal) {
    Scope topMostScope = this.scopeStack.peekFirst();
    try {
      topMostScope.declareAndInitVar(id, tc, initialVal);
    } catch (final OPSTypeCheckException opstce) {
      throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
    }
  }

  public PTType resolveIdentifier(final String id) {

    /*
     * Search through the stack of scopes;
     * most recently pushed scopes get first priority,
     * so search from front of list (stack) to back.
     */
    for(Scope scope : this.scopeStack) {
      if(scope.isIdResolvable(id)) {
        return scope.resolveVar(id);
      }
    }

    // If id is not in any local scopes, check the Component scope.
    if(Environment.getComponentScope().isIdResolvable(id)) {
      return Environment.getComponentScope().resolveVar(id);
    }

    // If id is still not resolved, check the Global scope.
    if(Environment.getGlobalScope().isIdResolvable(id)) {
      return Environment.getGlobalScope().resolveVar(id);
    }

    /*
     * If the var identifier cannot be resolved, it must be auto-declared,
     * which is one of the saddest language features of PeopleCode.
     * Auto-declared vars always have Local scope and are of type Any.
     */
    this.declareLocalVar(id, new PTAnyTypeConstraint());
    log.info("Auto-declared identifier {} (Local scope, of type Any).", id);
    return this.resolveIdentifier(id);
  }
}

