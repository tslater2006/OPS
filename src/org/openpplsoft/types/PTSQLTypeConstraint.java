/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.pt.SQL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTSQLTypeConstraint extends PTTypeConstraint<PTSQL> {

  private static Logger log = LogManager.getLogger(
      PTSQLTypeConstraint.class.getName());

  public PTSQLTypeConstraint() {
    super(PTSQL.class);
  }

  @Override
  public PTSQL alloc() {
    throw new OPSDataTypeException("Call to alloc() PTSQL from type constraint "
        + "without providing associated SQL defn and bind values is illegal.");
  }

 /**
  * Allocates a new SQL object with an attached SQL defn and bind values.
  * Allocated SQL objects must have an associated SQL defn and array of bind values,
  * as these are prequisites for instantiating an object instance. However, the SQL defn
  * and bind values are not part of the type itself; a SQL variable can be assigned
  * any SQL object, regardless of its underlying SQL defn and bind values (this is why
  * the typeCheck and equals/hashCode methods are not overriden by this class,
  * there's no need to do so).
  * @param sqlDefn the SQL defn to attach
  * @param bindVals the array of values to bind to the query (allowed to be empty)
  * @return the newly allocated SQL object
  */
  public PTSQL alloc(final SQL sqlDefn, final PTPrimitiveType[] bindVals) {
    return new PTSQL(this, sqlDefn, bindVals);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
