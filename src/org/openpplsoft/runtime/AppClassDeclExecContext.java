/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import org.openpplsoft.types.PTAppClassObj;

/**
 * Execution of an app class requires evaluation starting
 * at the class' declaration node; this differs from
 * non-app class PeopleCode programs.
 */
public class AppClassDeclExecContext extends ExecContext {

  /**
   * @param obj The app class around which to form an execution context.
   */
  public AppClassDeclExecContext(final PTAppClassObj obj) {
    super(obj.progDefn);
    this.startNode = obj.progDefn.getClassDeclNode();
  }
}
