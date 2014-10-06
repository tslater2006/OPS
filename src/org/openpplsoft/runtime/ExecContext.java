/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import org.openpplsoft.pt.peoplecode.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import org.openpplsoft.types.*;
import org.openpplsoft.runtime.*;

public abstract class ExecContext {

  public PeopleCodeProg prog;
  public ParseTree startNode;
  public LinkedList<Scope> scopeStack;

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

  public class OPSVMachIdentifierResolutionException extends Exception {
    public OPSVMachIdentifierResolutionException(final String msg) {
      super(msg);
    }
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

  public PTType resolveIdentifier(String id)
      throws OPSVMachIdentifierResolutionException {

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
    if(Environment.componentScope.isIdResolvable(id)) {
      return Environment.componentScope.resolveVar(id);
    }

    // If id is still not resolved, check the Global scope.
    if(Environment.globalScope.isIdResolvable(id)) {
      return Environment.globalScope.resolveVar(id);
    }

    throw new OPSVMachIdentifierResolutionException(
        "Unable to resolve identifier ("
        + id + ") to PTType after checking all scopes.");
  }
}

