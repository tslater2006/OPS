/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.util.*;
import org.openpplsoft.pt.*;
import org.apache.logging.log4j.*;
import java.lang.reflect.*;
import org.openpplsoft.types.*;

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
    } catch(final java.lang.IllegalAccessException iae) {
      throw new OPSVMachRuntimeException(iae.getMessage(), iae);
    } catch(final java.lang.reflect.InvocationTargetException ite) {
      throw new OPSVMachRuntimeException(ite.getMessage(), ite);
    }
  }
}
