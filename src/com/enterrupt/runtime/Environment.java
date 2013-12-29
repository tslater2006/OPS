package com.enterrupt.runtime;

import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;
import com.enterrupt.memory.*;
import org.apache.logging.log4j.*;

public class Environment {

	public static Pointer TRUE;
	public static Pointer FALSE;
	public static Pointer DEFN_LITERAL;

	public static RefEnvi globalRefEnvi;
	public static RefEnvi componentRefEnvi;

	private static HashMap<String, Pointer> systemVarTable;
	private static HashMap<String, Pointer> systemFuncTable;

	private static HashMap<Integer, Pointer> integerLiteralPool;
	private static HashMap<String, Pointer> stringLiteralPool;

    private static Stack<Pointer> callStack;

	private static String[] supportedGlobalVars = {"%EmployeeId", "%OperatorId", "%Menu",
					"%Component"};

	private static Logger log = LogManager.getLogger(Environment.class.getName());

	static {

		// Load static boolean literals.
		TRUE = new StdPointer(new PTBoolean(new Boolean(true)),
			EnumSet.of(MFlag.READ_ONLY, MFlag.BOOLEAN));
		FALSE = new StdPointer(new PTBoolean(new Boolean(false)),
			EnumSet.of(MFlag.READ_ONLY, MFlag.BOOLEAN));

		DEFN_LITERAL = new StdPointer(new PTDefnLiteral(),
			EnumSet.of(MFlag.READ_ONLY));

		// Setup global and component referencing environments.
		globalRefEnvi = new RefEnvi(RefEnvi.Type.GLOBAL);
		componentRefEnvi = new RefEnvi(RefEnvi.Type.COMPONENT);

		// Create memory pools for supported data types.
		integerLiteralPool = new HashMap<Integer, Pointer>();
		stringLiteralPool = new HashMap<String, Pointer>();

		// Allocate space for system vars, mark each as read-only.
		systemVarTable = new HashMap<String, Pointer>();
		for(String varName : supportedGlobalVars) {
			systemVarTable.put(varName, new StdPointer(
				new PTString(), EnumSet.of(MFlag.READ_ONLY, MFlag.STRING)));
		}

		// Set up system variable aliases. TODO: When I have a few of these, create these dynamically.
		systemVarTable.put("%UserId", systemVarTable.get("%OperatorId"));

		// Initialize the call stack.
		callStack = new Stack<Pointer>();

		try {
			/// Cache references to global PT functions to avoid repeated reflection lookups at runtime.
			Method[] methods = GlobalFnLibrary.class.getMethods();
			systemFuncTable = new HashMap<String, Pointer>();
			for(Method m : methods) {
				if(m.getName().indexOf("PT_") == 0) {
					systemFuncTable.put(m.getName().substring(3),
						new StdPointer(new PTSysFunc(
							GlobalFnLibrary.class.getMethod(m.getName()))));
				}
			}
        } catch(java.lang.NoSuchMethodException nsme) {
            log.fatal(nsme.getMessage(), nsme);
            System.exit(ExitCode.REFLECT_FAIL_RTE_STATIC_INIT.getCode());
        }
	}

    public static void pushToCallStack(Pointer p) {
        log.debug("Push\tCallStack\t" + (p == null ? "null" : p));
        callStack.push(p);
    }

    public static Pointer popFromCallStack() {
        Pointer p = callStack.pop();
        log.debug("Pop\tCallStack\t\t" + (p == null ? "null" : p));
        return p;
    }

    public static Pointer peekAtCallStack() {
        return callStack.peek();
    }

	public static int getCallStackSize() {
		return callStack.size();
	}

	public static void setSystemVar(String var, String value) {
		((StdPointer)systemVarTable.get(var)).systemAssign(new PTString(value));
	}

	public static Pointer getSystemVar(String var) {
		Pointer ptr = systemVarTable.get(var);
		if(ptr == null) {
			throw new EntVMachRuntimeException("Attempted to access a system var "
			 + "that is undefined: " + var);
		}
		return ptr;
	}

	public static Pointer getSystemFuncPtr(String func) {
		return systemFuncTable.get(func);
	}

	public static Pointer getFromLiteralPool(Integer val) {
		Pointer p = integerLiteralPool.get(val);
		if(p == null) {
			p = new StdPointer(new PTInteger(val), EnumSet.of(MFlag.READ_ONLY,
						MFlag.INTEGER));
			integerLiteralPool.put(val, p);
		}
		return p;
	}

	public static Pointer getFromLiteralPool(String val) {
		Pointer p = stringLiteralPool.get(val);
		if(p == null) {
			p = new StdPointer(new PTString(val), EnumSet.of(MFlag.READ_ONLY,
						MFlag.STRING));
			stringLiteralPool.put(val, p);
		}
		return p;
	}
}
