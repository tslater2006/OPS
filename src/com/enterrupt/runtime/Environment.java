package com.enterrupt.runtime;

import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;

public class Environment {

	public static MemPointer TRUE;
	public static MemPointer FALSE;

	private static HashMap<String, MemPointer> systemVarTable;
	private static HashMap<String, Method> systemFuncTable;
	private static HashMap<String, MemPointer> compBufferTable;

	private static HashMap<Integer, MemPointer> integerLiteralPool;
	private static HashMap<String, MemPointer> stringLiteralPool;

    private static Stack<MemPointer> callStack;

	private static String[] supportedGlobalVars = {"%EmployeeId", "%OperatorId", "%Menu",
					"%Component"};

	private static Logger log = LogManager.getLogger(Environment.class.getName());

	static {

		try {
			// Load static boolean literals.
			TRUE = new MemPointer(new PTBoolean(new Boolean(true)),
				EnumSet.of(MFlag.READ_ONLY));
			FALSE = new MemPointer(new PTBoolean(new Boolean(false)),
				EnumSet.of(MFlag.READ_ONLY));

			// Create memory pools for supported data types.
			integerLiteralPool = new HashMap<Integer, MemPointer>();
			stringLiteralPool = new HashMap<String, MemPointer>();

			// Allocate space for system vars, mark each as read-only.
			systemVarTable = new HashMap<String, MemPointer>();
			for(String varName : supportedGlobalVars) {
				systemVarTable.put(varName, new MemPointer(
					new PTString(), EnumSet.of(MFlag.READ_ONLY)));
			}

			// Set up system variable aliases. TODO: When I have a few of these, create these dynamically.
			systemVarTable.put("%UserId", systemVarTable.get("%OperatorId"));

			/// Cache references to global PT functions to avoid repeated reflection lookups at runtime.
			Method[] methods = GlobalFnLibrary.class.getMethods();
			systemFuncTable = new HashMap<String, Method>();
			for(Method m : methods) {
				if(m.getName().indexOf("PT_") == 0) {
					systemFuncTable.put(m.getName().substring(3), GlobalFnLibrary.class.getMethod(m.getName()));
				}
			}

			// Initialize empty component buffer.
			compBufferTable = new HashMap<String, MemPointer>();

			// Initialize the call stack.
			callStack = new Stack<MemPointer>();

        } catch(java.lang.NoSuchMethodException nsme) {
            log.fatal(nsme.getMessage(), nsme);
            System.exit(ExitCode.REFLECT_FAIL_RTE_STATIC_INIT.getCode());
        }
	}

    public static void pushToCallStack(MemPointer p) {
        log.debug("Push\tCallStack\t" + (p == null ? "null" : p));
        callStack.push(p);
    }

    public static MemPointer popFromCallStack() {
        MemPointer p = callStack.pop();
        log.debug("Pop\tCallStack\t\t" + (p == null ? "null" : p));
        return p;
    }

    public static MemPointer peekAtCallStack() {
        return callStack.peek();
    }

	public static int getCallStackSize() {
		return callStack.size();
	}

	public static void setCompBufferEntry(String recordField, MemPointer ptr) {
		compBufferTable.put(recordField, ptr);
	}

	public static MemPointer getCompBufferEntry(String recordField) {
		MemPointer ptr = compBufferTable.get(recordField);
		if(ptr == null) {
			ptr = new MemPointer(new PTString());
			setCompBufferEntry(recordField, ptr);
		}
		return ptr;
	}

	public static void setSystemVar(String var, String value) {
		systemVarTable.get(var).systemAssign(new PTString(value));
	}

	public static MemPointer getSystemVar(String var) {
		return systemVarTable.get(var);
	}

	public static Method getSystemFunc(String func) {
		return systemFuncTable.get(func);
	}

	public static MemPointer getFromLiteralPool(Integer val) {
		MemPointer p = integerLiteralPool.get(val);
		if(p != null) {
			return p;
		}
		p = new MemPointer(new PTInteger(val), EnumSet.of(MFlag.READ_ONLY));
		integerLiteralPool.put(val, p);
		return p;
	}

	public static MemPointer getFromLiteralPool(String val) {
		MemPointer p = stringLiteralPool.get(val);
		if(p != null) {
			return p;
		}
		p = new MemPointer(new PTString(val), EnumSet.of(MFlag.READ_ONLY));
		stringLiteralPool.put(val, p);
		return p;
	}
}
