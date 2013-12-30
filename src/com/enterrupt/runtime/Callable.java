package com.enterrupt.runtime;

import java.util.*;
import com.enterrupt.pt.*;
import org.apache.logging.log4j.*;
import java.lang.reflect.*;

public class Callable {

	private static Logger log = LogManager.getLogger(Callable.class.getName());

	public ExecContext eCtx;
	public Method sysFuncPtr;

	public Callable(ExecContext e) {
		this.eCtx = e;
	}

	public Callable(Method m) {
		this.sysFuncPtr = m;
	}

    public void invokeSysFunc() {
        try {
            this.sysFuncPtr.invoke(GlobalFnLibrary.class);
        } catch(java.lang.IllegalAccessException iae) {
            log.fatal(iae.getMessage(), iae);
            System.exit(ExitCode.REFLECT_FAIL_SYS_FN_INVOCATION.getCode());
        } catch(java.lang.reflect.InvocationTargetException ite) {
            log.fatal(ite.getMessage(), ite);
            System.exit(ExitCode.REFLECT_FAIL_SYS_FN_INVOCATION.getCode());
        }
    }
}
