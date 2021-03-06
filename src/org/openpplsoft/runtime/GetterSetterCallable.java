/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetterSetterCallable extends Callable {

  private static Logger log = LogManager.getLogger(GetterSetterCallable.class.getName());

  public AppClassObjMethodExecContext getterExecContext, setterExecContext;

  public GetterSetterCallable() {}

  public void setGetterExecContext(final AppClassObjMethodExecContext getterExecContext) {
    this.getterExecContext = getterExecContext;
  }

  public void setSetterExecContext(final AppClassObjMethodExecContext setterExecContext) {
    this.setterExecContext = setterExecContext;
  }

  public AppClassObjMethodExecContext getGetterExecContext() {
    return this.getterExecContext;
  }

  public AppClassObjMethodExecContext getSetterExecContext() {
    return this.setterExecContext;
  }

  public boolean hasSetterExecContext() {
    return this.setterExecContext != null;
  }

  public boolean hasGetterExecContext() {
    return this.getterExecContext != null;
  }
}
