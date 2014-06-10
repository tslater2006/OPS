/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.runtime;

import java.util.*;
import java.lang.reflect.Method;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;

public class Environment {

  public static PTBoolean TRUE;
  public static PTBoolean FALSE;
  public static PTDefnLiteral DEFN_LITERAL;

  public static Scope globalScope;
  public static Scope componentScope;

  private static Map<String, PTPrimitiveType> systemVarTable;
  private static Map<String, Callable> systemFuncTable;

  private static Map<Integer, PTInteger> integerLiteralPool;
  private static Map<String, PTString> stringLiteralPool;
  private static Map<Double, PTNumber> numberLiteralPool;

  private static Stack<PTType> callStack;

  private static String[] supportedGlobalVars = {"%EmployeeId",
    "%OperatorId", "%Menu", "%Component"};

  private static Logger log = LogManager.getLogger(Environment.class.getName());

  static {

    // Load static boolean literals.
    TRUE = (PTBoolean)PTBoolean.getSentinel().alloc().setReadOnly();
    TRUE.systemWrite(true);

    FALSE = (PTBoolean)PTBoolean.getSentinel().alloc().setReadOnly();
    FALSE.systemWrite(false);

    DEFN_LITERAL = (PTDefnLiteral)PTDefnLiteral.getSentinel().alloc().setReadOnly();

    // Setup global and component scopes.
    globalScope = new Scope(Scope.Lvl.GLOBAL);
    componentScope = new Scope(Scope.Lvl.COMPONENT);

    // Create memory pools for supported data types.
    integerLiteralPool = new HashMap<Integer, PTInteger>();
    stringLiteralPool = new HashMap<String, PTString>();
    numberLiteralPool = new HashMap<Double, PTNumber>();

    // Allocate space for system vars, mark each as read-only.
    systemVarTable = new HashMap<String, PTPrimitiveType>();
    for(String varName : supportedGlobalVars) {
      systemVarTable.put(varName, (PTString)PTString.getSentinel()
                        .alloc().setReadOnly());
    }

    // Set up system variable aliases. TODO: When I have a few of these, create these dynamically.
    systemVarTable.put("%UserId", systemVarTable.get("%OperatorId"));

    // Initialize the call stack.
    callStack = new Stack<PTType>();

    // Cache references to global PT functions to avoid repeated reflection lookups at runtime.
    Method[] methods = GlobalFnLibrary.class.getMethods();
    systemFuncTable = new HashMap<String, Callable>();
    for(Method m : methods) {
      if(m.getName().indexOf("PT_") == 0) {
        systemFuncTable.put(m.getName().substring(3), new Callable(m,
          GlobalFnLibrary.class));
      }
    }
  }

  public static void pushToCallStack(PTType p) {
    log.debug("Push\tCallStack\t" + (p == null ? "null" : p));
    callStack.push(p);
  }

  public static PTType popFromCallStack() {
    PTType p = callStack.pop();
    log.debug("Pop\tCallStack\t\t" + (p == null ? "null" : p));
    return p;
  }

  public static PTType peekAtCallStack() {
    return callStack.peek();
  }

  public static int getCallStackSize() {
    return callStack.size();
  }

  public static void setSystemVar(String var, String value) {
    // Assuming var is mapped to a PTString for now.
    ((PTString)systemVarTable.get(var)).systemWrite(
      Environment.getFromLiteralPool(value).read());
  }

  public static PTPrimitiveType getSystemVar(String var) {

    PTPrimitiveType a = null;
    switch(var) {
      case "%Date":
        a = PTDate.getSentinel().alloc();
        break;
      default:
        a = systemVarTable.get(var);
    }

    if(a == null) {
      throw new EntVMachRuntimeException("Attempted to access a system var "
       + "that is undefined: " + var);
    }
    return a;
  }

  public static Callable getSystemFuncPtr(String func) {
    return systemFuncTable.get(func);
  }

  public static PTInteger getFromLiteralPool(Integer val) {
    PTInteger p = integerLiteralPool.get(val);
    if(p == null) {
      p = (PTInteger)PTInteger.getSentinel().alloc().setReadOnly();
      p.systemWrite(val);
      integerLiteralPool.put(val, p);
    }
    return p;
  }

  public static PTNumber getFromLiteralPool(Double val) {
    PTNumber p = numberLiteralPool.get(val);
    if(p == null) {
      p = (PTNumber)PTNumber.getSentinel().alloc().setReadOnly();
      p.systemWrite(val);
      numberLiteralPool.put(val, p);
    }
    return p;
  }

  public static PTString getFromLiteralPool(String val) {
    PTString p = stringLiteralPool.get(val);
    if(p == null) {
      p = (PTString)PTString.getSentinel().alloc().setReadOnly();
      p.systemWrite(val);
      stringLiteralPool.put(val, p);
    }
    return p;
  }

  public static List<PTType> getArgsFromCallStack() {

    List<PTType> args = new ArrayList<PTType>();
    PTType p;
    while((p = Environment.peekAtCallStack()) != null) {
      args.add(Environment.popFromCallStack());
    }

    // The last argument appears at the top of the stack,
    // so we need to reverse the argument list here before returning it.
    Collections.reverse(args);
    return args;
  }
}
