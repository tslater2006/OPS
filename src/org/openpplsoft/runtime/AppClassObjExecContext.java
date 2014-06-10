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

  public String methodOrGetterName;
  public PTAppClassObj appClassObj;
  public PTType expectedReturnType;

  public AppClassObjExecContext(PTAppClassObj obj, String m, ParseTree s, PTType r) {
    super(obj.progDefn);
    this.appClassObj = obj;
    this.startNode = s;
    this.methodOrGetterName = m;
    this.expectedReturnType = r;
    this.pushScope(obj.propertyScope);
    this.pushScope(obj.instanceScope);
  }
}
