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

public class AppClassObjMethodExecContext extends AppClassObjExecContext {

  public AppClassObjMethodExecContext(PTAppClassObj obj, String m,
      ParseTree s, PTType r) {
    super(obj, m, s, r);
  }
}

