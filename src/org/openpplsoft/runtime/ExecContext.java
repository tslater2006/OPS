/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.runtime;

import com.enterrupt.pt.peoplecode.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;

public abstract class ExecContext {

  public PeopleCodeProg prog;
  public ParseTree startNode;
  public LinkedList<Scope> scopeStack;

  public ExecContext(PeopleCodeProg p) {
    this.prog = p;
    this.startNode = p.getParseTree();
    this.scopeStack = new LinkedList<Scope>();
  }

  public void pushScope(Scope s) {
    // Place the scope at the front of the linked list.
    this.scopeStack.push(s);
  }

  public void popScope() {
    // Remove the scope from the front of the linked list.
    this.scopeStack.pop();
  }

  public void declareLocalVar(String id, PTType p) {
    Scope topMostScope = this.scopeStack.peekFirst();
    topMostScope.declareVar(id, p);
  }

  public PTType resolveIdentifier(String id) {

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

    throw new EntVMachRuntimeException("Unable to resolve identifier (" +
        id + ") to PTType after checking all scopes.");
  }

  public void assignToIdentifier(String id, PTType p) {
    /*
     * Search through the stack of scopes;
     * most recently pushed scopes get first priority,
     * so search from front of list (stack) to back.
     */
    for(Scope scope : this.scopeStack) {
      if(scope.isIdResolvable(id)) {
        scope.assignVar(id, p);
        return;
      }
    }

    // If id is not in any local scopes, check the Component scope.
    if(Environment.componentScope.isIdResolvable(id)) {
      Environment.componentScope.assignVar(id, p);
      return;
    }

    // If id is still not resolved, check the Global scope.
    if(Environment.globalScope.isIdResolvable(id)) {
      Environment.globalScope.assignVar(id, p);
      return;
    }

    throw new EntVMachRuntimeException("Unable to resolve identifier (" +
        id + ") in any scope before assigning to identifier.");
  }
}

