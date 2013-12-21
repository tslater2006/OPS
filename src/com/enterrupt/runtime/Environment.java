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
	private static HashMap<String, StringPtr> compBufferTable;
	private static HashMap<String, Method> systemFuncTable;

	private static HashMap<Integer, MemoryPtr> integerMemPool;
	private static HashMap<String, MemoryPtr> stringMemPool;

	private static String[] supportedGlobalVars = {"%EmployeeId", "%OperatorId", "%Menu",
					"%Component"};

	private static Logger log = LogManager.getLogger(Environment.class.getName());

	static {

		try {
			// Load static boolean literals.
			TRUE = new BooleanPtr(new Boolean(true), MFlag.READ_ONLY);
			FALSE = new BooleanPtr(new Boolean(false), MFlag.READ_ONLY);

			// Create memory pools for supported data types.
			integerMemPool = new HashMap<Integer, MemoryPtr>();
			stringMemPool = new HashMap<String, MemoryPtr>();

			// Allocate space for system vars, mark each as read-only.
			systemVarTable = new HashMap<String, MemoryPtr>();
			for(String varName : supportedGlobalVars) {
				systemVarTable.put(varName, new StringPtr(MFlag.READ_ONLY));
			}

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

        } catch(java.lang.NoSuchMethodException nsme) {
            log.fatal(nsme.getMessage(), nsme);
            System.exit(ExitCode.REFLECT_FAIL_RTE_STATIC_INIT.getCode());
        }
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

	public static MemoryPtr getFromMemoryPool(Integer val) {
		MemoryPtr p = integerMemPool.get(val);
		if(p != null) {
			return p;
		}
		p = new IntegerPtr(val, MFlag.READ_ONLY);
		integerMemPool.put(val, p);
		return p;
	}

	public static MemoryPtr getFromMemoryPool(String val) {
		MemoryPtr p = stringMemPool.get(val);
		if(p != null) {
			return p;
		}
		p = new StringPtr(val, MFlag.READ_ONLY);
		stringMemPool.put(val, p);
		return p;
	}
}
