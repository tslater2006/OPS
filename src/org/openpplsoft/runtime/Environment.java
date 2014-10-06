/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.math.BigDecimal;
import java.util.*;
import java.lang.reflect.Method;

import org.openpplsoft.types.*;
import org.openpplsoft.runtime.*;
import org.apache.logging.log4j.*;

public class Environment {

  public static Scope globalScope;
  public static Scope componentScope;

  // i.e., XENCSDEV, ENTCSDEV (appears in PS URLs)
  public static String psEnvironmentName;

  private static Map<String, PTPrimitiveType> systemVarTable;
  private static Map<String, Callable> systemFuncTable;

  private static Stack<PTType> callStack;

  private static Logger log = LogManager.getLogger(Environment.class.getName());

  static {

    systemVarTable = new HashMap<String, PTPrimitiveType>();

    // Setup global and component scopes.
    globalScope = new Scope(Scope.Lvl.GLOBAL);
    componentScope = new Scope(Scope.Lvl.COMPONENT);

    // Initialize the call stack.
    callStack = new Stack<PTType>();

    // Cache references to global PT functions to
    // avoid repeated reflection lookups at runtime.
    Method[] methods = GlobalFnLibrary.class.getMethods();
    systemFuncTable = new HashMap<String, Callable>();
    for(Method m : methods) {
      if(m.getName().indexOf("PT_") == 0) {
        systemFuncTable.put(m.getName().substring(3), new Callable(m,
          GlobalFnLibrary.class));
      }
    }
  }

  /**
   * Pushes the provided PT data value to the call stack. If the
   * value is of primitive type, a copy of it will be placed on the call stack,
   * since PT only supports pass-by-reference of objects; primitives are passed
   * by value.
   */
  public static void pushToCallStack(final PTType p) {
    if (p instanceof PTPrimitiveType) {
      PTType copiedPrimitive = p.getOriginatingTypeConstraint().alloc();
      ((PTPrimitiveType) copiedPrimitive).copyValueFrom((PTPrimitiveType) p);
      log.debug("Push\tCallStack\t"
          + (copiedPrimitive == null ? "null" : copiedPrimitive));
      callStack.push(copiedPrimitive);
    } else {
      log.debug("Push\tCallStack\t" + (p == null ? "null" : p));
      callStack.push(p);
    }
  }

  public static PTType popFromCallStack() {
    PTType p = callStack.pop();
    log.debug("Pop\tCallStack\t" + (p == null ? "null" : p));
    return p;
  }

  public static PTType peekAtCallStack() {
    return callStack.peek();
  }

  public static int getCallStackSize() {
    return callStack.size();
  }

  public static void setSystemVar(final String var, final PTPrimitiveType value) {
    value.setReadOnly();
    systemVarTable.put(var, value);
  }

  public static PTPrimitiveType getSystemVar(final String var) {

    PTPrimitiveType a = null;
    switch(var) {
      case "%UserId":
        a = systemVarTable.get("%OperatorId");
        break;
      case "%PanelGroup":
        a = systemVarTable.get("%Component");
        break;
      default:
        a = systemVarTable.get(var);
    }

    if(a == null) {
      throw new OPSVMachRuntimeException("Attempted to access a system var "
       + "that is undefined: " + var);
    }

    /*
     * There may be instances where a system variable is passed
     * as an expression to a function that may then write to the
     * identifier the arg has been bound to. Those cases require that we always
     * pass a copy of the system variable, rather than the system variable itself,
     * as the sysvar is read-only and such a write will trigger an RTE.
     */
    final PTPrimitiveType copy =
        (PTPrimitiveType) a.getOriginatingTypeConstraint().alloc();
    copy.copyValueFrom(a);
    return copy;
  }

  public static Callable getSystemFuncPtr(String func) {
    return systemFuncTable.get(func);
  }

  public static List<PTType> getDereferencedArgsFromCallStack() {
    List<PTType> args = new ArrayList<PTType>();
    PTType p;
    while(!((p = Environment.peekAtCallStack()) instanceof PTCallFrameBoundary)) {
      PTType arg = Environment.popFromCallStack();
      if (arg instanceof PTReference) {
        arg = ((PTReference) arg).deref();
      }
      args.add(arg);
    }

    // The last argument appears at the top of the stack,
    // so we need to reverse the argument list here before returning it.
    Collections.reverse(args);
    return args;
  }

  public static List<PTType> getArgsFromCallStack() {

    List<PTType> args = new ArrayList<PTType>();
    PTType p;
    while(!((p = Environment.peekAtCallStack()) instanceof PTCallFrameBoundary)) {
      args.add(Environment.popFromCallStack());
    }

    // The last argument appears at the top of the stack,
    // so we need to reverse the argument list here before returning it.
    Collections.reverse(args);
    return args;
  }
}
