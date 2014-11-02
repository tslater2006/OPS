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
  public Object obj;

  public Callable() {}

  public Callable(ExecContext e) {
    this.eCtx = e;
  }

  /*
   * For method calls on PT objects (i.e., PTRowset.Flush())
   * or system func calls (i.e., GlobalFnLibrary's Lower()).
   */
  public Callable(Method m, Object o) {
    this.ptMethod = m;
    this.obj = o;
  }

  public void invokePtMethod() {
    try {
      this.ptMethod.invoke(this.obj);
    } catch (final java.lang.IllegalAccessException iae) {
      throw new OPSVMachRuntimeException(iae.getMessage(), iae);
    } catch (final java.lang.reflect.InvocationTargetException ite) {
      /*
       * If the cause of the ITE is due to an OPS RTE, throw the underlying
       * cause; otherwise, certain OPS RTEs that are checked by the interpreter
       * (i.e., OPSIllegalNonCBufferFieldAccessAttempt) will see the ITE rather
       * than the underlying OPS RTE, and therefore will not catch it.
       */
      if (ite.getCause() instanceof OPSVMachRuntimeException) {
        throw (OPSVMachRuntimeException) ite.getCause();
      } else {
        throw new OPSVMachRuntimeException(ite.getMessage(), ite);
      }
    }
  }
}
