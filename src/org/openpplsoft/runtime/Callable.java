/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Callable {

  private static Logger log = LogManager.getLogger(Callable.class.getName());

  private final ExecContext eCtx;
  private final Method ptMethod;
  private final Object obj;

  public Callable()  {
    this.eCtx = null;
    this.ptMethod = null;
    this.obj = null;
  }

  public Callable(final ExecContext e) {
    this.eCtx = e;
    this.ptMethod = null;
    this.obj = null;
  }

  /*
   * For method calls on PT objects (i.e., PTRowset.Flush())
   * or system func calls (i.e., GlobalFnLibrary's Lower()).
   */
  public Callable(final Method m, final Object o) {
    this.eCtx = null;
    this.ptMethod = m;
    this.obj = o;
  }

  public ExecContext getExecContext() {
    return this.eCtx;
  }

  public boolean hasMethod() {
    return this.ptMethod != null;
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
