/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.runtime;

import java.util.*;
import com.enterrupt.pt.*;
import org.apache.logging.log4j.*;
import java.lang.reflect.*;
import com.enterrupt.types.*;

public class Callable {

  private static Logger log = LogManager.getLogger(Callable.class.getName());

  public ExecContext eCtx;
  public Method ptMethod;
  public Class ptMethodClass;
  public PTObjectType obj;

  public Callable(ExecContext e) {
    this.eCtx = e;
  }

  /*
   * For system functions (i.e., CreateRecord).
   */
  public Callable(Method m, Class c) {
    this.ptMethod = m;
    this.ptMethodClass = c;
  }

  /*
   * For method calls on PT objects (i.e., PTRowset.Flush()).
   */
  public Callable(Method m, PTObjectType o) {
    this.ptMethod = m;
    this.obj = o;
  }

  public void invokePtMethod() {
    try {
      if(this.obj != null) {
        this.ptMethod.invoke(this.obj);
      } else {
        this.ptMethod.invoke(this.ptMethodClass);
      }
    } catch(java.lang.IllegalAccessException iae) {
      log.fatal(iae.getMessage(), iae);
      System.exit(ExitCode.REFLECT_FAIL_SYS_FN_INVOCATION.getCode());
    } catch(java.lang.reflect.InvocationTargetException ite) {
      log.fatal(ite.getMessage(), ite);
      System.exit(ExitCode.REFLECT_FAIL_SYS_FN_INVOCATION.getCode());
    }
  }
}
