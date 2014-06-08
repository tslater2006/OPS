/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.runtime;

import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;
import com.enterrupt.types.*;
import org.antlr.v4.runtime.tree.*;

public class AppClassObjGetterExecContext extends AppClassObjExecContext {

  public AppClassObjGetterExecContext(PTAppClassObj obj, String m,
      ParseTree s, PTType r) {
    super(obj, m, s, r);
  }
}
