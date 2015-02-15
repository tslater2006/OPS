/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.types.*;
import org.antlr.v4.runtime.tree.*;

public abstract class AppClassObjExecContext extends ExecContext {

  private final String methodOrGetterName;
  private final PTAppClassObj appClassObj;
  private final PTTypeConstraint expectedReturnTypeConstraint;

  public AppClassObjExecContext(final PTAppClassObj obj, final String m,
      final ParseTree s, final PTTypeConstraint rTc) {
    super(obj.progDefn);
    this.appClassObj = obj;
    this.startNode = s;
    this.methodOrGetterName = m;
    this.expectedReturnTypeConstraint = rTc;
    this.pushScope(obj.propertyScope);
    this.pushScope(obj.instanceScope);
  }

  public PTAppClassObj getAppClassObj() {
    return this.appClassObj;
  }

  public PTTypeConstraint getExpectedReturnTypeConstraint() {
    return this.expectedReturnTypeConstraint;
  }

  @Override
  public String getMethodOrFuncName() {
    return this.methodOrGetterName;
  }
}
