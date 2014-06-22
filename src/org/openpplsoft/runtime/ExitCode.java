/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

public enum ExitCode {

  SUCCESS(0),
  GENERIC_FAILURE(1),
  ENT_VIRTUAL_MACH_RUNTIME_EXCEPTION(2),
  REFLECT_FAIL_RTE_STATIC_INIT(3),
  FAILED_TO_CREATE_PSTMT_FROM_CONN(4),
  UNABLE_TO_ACQUIRE_DB_CONN(5),
  GENERIC_SQL_EXCEPTION(6),
  FAILED_READ_FROM_BLOB_STREAM(7),
  REFLECT_FAIL_SYS_FN_INVOCATION(8),
  COMP_STRUCTURE_FILE_NOT_FOUND(9),
  FAILED_READ_FROM_COMP_STRUCT_FILE(10),
  TRACE_FILE_NOT_FOUND(11),
  FAILED_READ_FROM_TRACE_FILE(12),
  IGNORE_STMTS_FILE_NOT_FOUND(13),
  FAILED_READ_FROM_IGNORE_STMTS_FILE(14),
  FAILED_READ_FROM_STATIC_SQL_DEFN_FILE(15),
  STATIC_SQL_DEFN_FILE_NOT_FOUND(16);


  private int code;

  private ExitCode(int code) {
    this.code = code;
  }

  public int getCode() {
    return this.code;
  }
}
