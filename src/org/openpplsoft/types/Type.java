/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

/**
 * Each PeopleTools type implementation object must
 * have one of these core enumerated base types.
 */
public enum Type {
  STRING, BOOLEAN, INTEGER, DEFN_LITERAL, RECORD, FIELD,
  APP_CLASS_OBJ, ARRAY, DATE, ROWSET, NUMBER, ROW, REC_LITERAL,
  FLD_LITERAL, DATETIME, TIME
}
