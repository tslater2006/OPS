package com.enterrupt.types;

import com.enterrupt.runtime.*;
import java.lang.reflect.*;
import org.apache.logging.log4j.*;

public class PTSysFunc implements PTDataType {

	public Method systemFunc;

	private static Logger log = LogManager.getLogger(PTSysFunc.class.getName());

	public PTSysFunc(Method m) {
		this.systemFunc = m;
	}

	public void invoke() {
		try {
        	this.systemFunc.invoke(GlobalFnLibrary.class);
        } catch(java.lang.IllegalAccessException iae) {
			log.fatal(iae.getMessage(), iae);
            System.exit(ExitCode.REFLECT_FAIL_SYS_FN_INVOCATION.getCode());
        } catch(java.lang.reflect.InvocationTargetException ite) {
            log.fatal(ite.getMessage(), ite);
            System.exit(ExitCode.REFLECT_FAIL_SYS_FN_INVOCATION.getCode());
        }
	}

	public MemPointer access(String s) {
		throw new EntDataTypeException("Illegal call to access(s) on " +
			"PTSysFunc; s=" + s);
	}

	public boolean equals(Object obj) {
		throw new EntDataTypeException("Illegal call to equals(obj) on " +
			"PTSysFunc.");
	}

	public String toString() {
		return "#PTSysFunc";
	}
}
