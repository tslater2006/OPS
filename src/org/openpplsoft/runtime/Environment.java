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

import org.openpplsoft.pt.*;
import org.openpplsoft.types.*;
import org.openpplsoft.runtime.*;
import org.apache.logging.log4j.*;

public class Environment {

  public static Scope globalScope;
  public static Scope componentScope;

  // i.e., XENCSDEV, ENTCSDEV (appears in PS URLs)
  public static String psEnvironmentName;
  private static User user;

  private static Map<String, PTPrimitiveType> systemVarTable;

  private static Stack<PTType> callStack;

  private static Logger log = LogManager.getLogger(Environment.class.getName());

  static {

    systemVarTable = new HashMap<String, PTPrimitiveType>();

    // Setup global and component scopes.
    globalScope = new Scope(Scope.Lvl.GLOBAL);
    componentScope = new Scope(Scope.Lvl.COMPONENT);

    // Initialize the call stack.
    callStack = new Stack<PTType>();
  }

  public static void init(final String psEnviName, final String oprid) {
    psEnvironmentName = psEnviName;
    user = DefnCache.getUser(oprid);
    Environment.setSystemVar("%OperatorId", new PTString(user.getOprid()));
    Environment.setSystemVar("%EmployeeId", new PTString(user.getEmplid()));
    Environment.setSystemVar("%OperatorClass", new PTString(user.getOprClass()));
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
      case "%AsOfDate":
        a = systemVarTable.get("%Date");
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

  public static PTType getOrDeref(final PTType rawExpr) {
    if (rawExpr instanceof PTReference) {
      return ((PTReference) rawExpr).deref();
    }
    return rawExpr;
  }

  public static PTPrimitiveType getOrDerefPrimitive(final PTType rawExpr) {
    if (rawExpr instanceof PTPrimitiveType) {
      return (PTPrimitiveType) rawExpr;
    } else if (rawExpr instanceof PTReference) {
        return getOrDerefPrimitive(((PTReference) rawExpr).deref());
    } else if (rawExpr instanceof PTField) {
      return ((PTField) rawExpr).getValue();
    } else if (rawExpr instanceof PTRecordFieldSpecifier) {
      return ((PTRecordFieldSpecifier) rawExpr).resolveInCBufferContext()
          .deref().getValue();
    } else {
      throw new OPSVMachRuntimeException("Expected either a primitive, a reference "
          + "to one, or a Field (getOrDerefPrimitive): " + rawExpr);
    }
  }

  public static PTObjectType getOrDerefObject(final PTType rawExpr) {
    if (rawExpr instanceof PTObjectType) {
      return (PTObjectType) rawExpr;
    } else if (rawExpr instanceof PTReference
        && ((PTReference) rawExpr).deref() instanceof PTObjectType) {
      return (PTObjectType) ((PTReference) rawExpr).deref();
    } else {
      throw new OPSVMachRuntimeException("Expected either an object or a reference "
          + "to one (getOrDerefObject).");
    }
  }

  public static PTNumberType getOrDerefNumber(final PTType rawExpr) {
    if (rawExpr instanceof PTNumberType) {
      return (PTNumberType) rawExpr;
    } else if (rawExpr instanceof PTReference) {
      final PTReference ref = ((PTReference) rawExpr);
      final PTType derefedVal = ref.deref();
      if (derefedVal instanceof PTNumberType) {
        return (PTNumberType) derefedVal;
      } else if (ref instanceof PTAnyTypeReference) {
        ((PTAnyTypeReference) ref).castTo(PTNumber.getTc());
        return (PTNumberType) ((PTReference) ref).deref();
      }
    }

    throw new OPSVMachRuntimeException("Expected either a number or a reference "
        + "to one (getOrDerefNumber).");
  }

  public static PTBoolean getOrDerefBoolean(final PTType rawExpr) {
    if (rawExpr instanceof PTBoolean) {
      return (PTBoolean) rawExpr;
    } else if (rawExpr instanceof PTReference
        && ((PTReference) rawExpr).deref() instanceof PTBoolean) {
      return (PTBoolean) ((PTReference) rawExpr).deref();
    } else {
      throw new OPSVMachRuntimeException("Expected either a boolean or a reference "
          + "to one (getOrDerefBoolean); instead encountered: " + rawExpr);
    }
  }

  @SuppressWarnings("unchecked")
  public static void assign(final PTType dst, final PTType src) {

    PTType resolvedDst = dst;

    // A RecordFieldSpecifier provided as an l-value is a reference to
    // a record field buffer in the component buffers and must be resolved.
    if (dst instanceof PTRecordFieldSpecifier) {
      resolvedDst = ((PTRecordFieldSpecifier) dst).resolveInCBufferContext();
    }

    if (!(resolvedDst instanceof PTReference)) {
      throw new OPSVMachRuntimeException("Illegal assignment; not a valid l-value: "
          + resolvedDst);
    }

    final PTReference lRef = (PTReference) resolvedDst;
    PTType rawSrc = src;

    // A RecordFieldSpecifier provided as an r-value is a reference to a
    // record field buffer in the component buffers and must be resolved.
    if (rawSrc instanceof PTRecordFieldSpecifier) {
      rawSrc = ((PTRecordFieldSpecifier) rawSrc).resolveInCBufferContext();
    }

    if (rawSrc instanceof PTReference) {
      rawSrc = ((PTReference) rawSrc).deref();
    }

    log.debug("Assigning from {} to {}", rawSrc, resolvedDst);

    if (rawSrc instanceof PTPrimitiveType) {
      if (lRef.deref() instanceof PTPrimitiveType) {
        ((PTPrimitiveType) lRef.deref()).copyValueFrom((PTPrimitiveType) rawSrc);
      } else if (lRef.deref() instanceof PTField) {
        ((PTField) lRef.deref()).getValue().copyValueFrom((PTPrimitiveType) rawSrc);

      // NOTE: You must look at the type constraint of the *reference* in the
      // conditional directly below this comment, not the
      // value it points to, b/c the value could be null and as of this writing,
      // PTNull is a singleton and thus per-null originating type constraints
      // are not associated with it.
      } else if (lRef.getOriginatingTypeConstraint()
          instanceof PTAnyTypeConstraint) {
        /*
         * Variables of type Any can point to objects or primitives. If an object
         * (or Null) is asigned to the identifier, a new primitive must be alloc'ed
         * containing a copy of the value in source operand.
         */
        PTPrimitiveType primCopy = (PTPrimitiveType)
            ((PTPrimitiveType) rawSrc).getOriginatingTypeConstraint().alloc();
        primCopy.copyValueFrom((PTPrimitiveType) rawSrc);
        try {
          lRef.pointTo(primCopy);
        } catch (final OPSImmutableRefAttemptedChangeException opsirace) {
          throw new OPSVMachRuntimeException(opsirace.getMessage(), opsirace);
        } catch (final OPSTypeCheckException opstce) {
          throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
        }
      } else {
        throw new OPSVMachRuntimeException("Assignment failed; rawSrc is primitive "
            + "but lRef dereferences to neither a primitive nor a PTField: " + lRef.deref());
      }
    } else if(rawSrc instanceof PTObjectType) {
      try {
        lRef.pointTo(rawSrc);
      } catch (final OPSImmutableRefAttemptedChangeException opsirace) {
        if (rawSrc instanceof PTField && lRef.deref() instanceof PTPrimitiveType) {
          // If lRef refers to a primitive and rawSrc is a Field, re-attempt the
          // assignment, this time copying the value from
          // the Field's underlying value to the referred primitive.
          ((PTPrimitiveType) lRef.deref()).copyValueFrom(((PTField) rawSrc).getValue());
        } else {
          throw new OPSVMachRuntimeException("Assignment failed, even after "
              + "checking if rawSrc is a PTField that needs to be unwrapped "
              + "to its enclosed value.", opsirace);
        }
      } catch (final OPSTypeCheckException opstce) {
        throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
      }
    } else {
      throw new OPSVMachRuntimeException("Assignment failed; unexpected "
          + "rawSrc: " + src + "; lRef is " + lRef);
    }
  }
}
