package com.enterrupt.runtime;

import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;

public class Environment {

	public static BooleanPtr TRUE;
	public static BooleanPtr FALSE;

	private static HashMap<String, MemoryPtr> systemVarTable;
	private static HashMap<String, Method> systemFuncTable;
	private static HashMap<String, StringPtr> compBufferTable;

	private static HashMap<Integer, MemoryPtr> integerLiteralPool;
	private static HashMap<String, MemoryPtr> stringLiteralPool;

    private static Stack<MemoryPtr> callStack;

	private static String[] supportedGlobalVars = {"%EmployeeId", "%OperatorId", "%Menu",
					"%Component"};

	private static Logger log = LogManager.getLogger(Environment.class.getName());

	static {

		try {
			// Load static boolean literals.
			TRUE = new BooleanPtr(new Boolean(true), MFlag.READ_ONLY);
			FALSE = new BooleanPtr(new Boolean(false), MFlag.READ_ONLY);

			// Create memory pools for supported data types.
			integerLiteralPool = new HashMap<Integer, MemoryPtr>();
			stringLiteralPool = new HashMap<String, MemoryPtr>();

			// Allocate space for system vars, mark each as read-only.
			systemVarTable = new HashMap<String, MemoryPtr>();
			for(String varName : supportedGlobalVars) {
				systemVarTable.put(varName, new StringPtr(MFlag.READ_ONLY));
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
			compBufferTable = new HashMap<String, StringPtr>();

			// Initialize the call stack.
			callStack = new Stack<MemoryPtr>();

        } catch(java.lang.NoSuchMethodException nsme) {
            log.fatal(nsme.getMessage(), nsme);
            System.exit(ExitCode.REFLECT_FAIL_RTE_STATIC_INIT.getCode());
        }
	}

    public static void pushToCallStack(MemoryPtr p) {
        log.debug("Push\tCallStack\t" + (p == null ? "null" : p.flags.toString()));
        callStack.push(p);
    }

    public static MemoryPtr popFromCallStack() {
        MemoryPtr p = callStack.pop();
        log.debug("Pop\tCallStack\t\t" + (p == null ? "null" : p.flags.toString()));
        return p;
    }

    public static MemoryPtr peekAtCallStack() {
        return callStack.peek();
    }

	public static int getCallStackSize() {
		return callStack.size();
	}

	public static void setCompBufferEntry(String recordField, StringPtr ptr) {
		compBufferTable.put(recordField, ptr);
	}

	public static StringPtr getCompBufferEntry(String recordField) {
		StringPtr ptr = compBufferTable.get(recordField);
		if(ptr == null) {
			ptr = new StringPtr();
			setCompBufferEntry(recordField, ptr);
		}
		return ptr;
	}

	public static void setSystemVar(String var, String value) {
		((StringPtr)systemVarTable.get(var)).systemWrite(value);
	}

	public static MemoryPtr getSystemVar(String var) {
		return systemVarTable.get(var);
	}

	public static Method getSystemFunc(String func) {
		return systemFuncTable.get(func);
	}

	public static MemoryPtr getFromLiteralPool(Integer val) {
		MemoryPtr p = integerLiteralPool.get(val);
		if(p != null) {
			return p;
		}
		p = new IntegerPtr(val, MFlag.READ_ONLY);
		integerLiteralPool.put(val, p);
		return p;
	}

	public static MemoryPtr getFromLiteralPool(String val) {
		MemoryPtr p = stringLiteralPool.get(val);
		if(p != null) {
			return p;
		}
		p = new StringPtr(val, MFlag.READ_ONLY);
		stringLiteralPool.put(val, p);
		return p;
	}
}
