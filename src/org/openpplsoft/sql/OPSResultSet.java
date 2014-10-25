/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.sql;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.runtime.*;

/**
 * Wrapper for all ResultSets opened by
 * the OPS runtime.
 */
public class OPSResultSet {

  private static Logger log =
      LogManager.getLogger(OPSResultSet.class.getName());

  private ResultSet rs;

  public OPSResultSet(final ResultSet rs) {
    this.rs = rs;
  }

  public Blob getBlob(final String colName) {
    try {
      return rs.getBlob(colName);
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public String getClobAsString(final String colName) {
    try {

      final Clob clob = rs.getClob("SQLTEXT");

      if (clob.length() > (long) Integer.MAX_VALUE) {
        throw new OPSVMachRuntimeException("Clob is longer than maximum possible "
            + "length of String; unable to store entire value in String unless "
            + "extra logic is added here.");
      }

      final StringBuilder sb = new StringBuilder();
      final BufferedReader clobReader = new BufferedReader(
          clob.getCharacterStream());

      String line;
      while(null != (line = clobReader.readLine())) {
          sb.append(line);
      }
      clobReader.close();

      clob.free();
      return sb.toString();
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    } catch (final IOException ioe) {
      throw new OPSVMachRuntimeException(ioe.getMessage(), ioe);
    }
  }

  public String getBlobAsString(final String colName) {
    try {

      final Blob blob = rs.getBlob(colName);

      if (blob.length() > (long) Integer.MAX_VALUE) {
        throw new OPSVMachRuntimeException("Length of Blob "
            + "is greater than Integer.MAX_VALUE; extra logic is required here "
            + "in order to ensure we are retrieving the entire value.");
      }

      final StringBuilder sb = new StringBuilder();
      final byte[] arr = blob.getBytes(1, (int) blob.length());
      for (final byte b : arr) {
        // Skip null bytes, there are a ton of these in PS BLOB fields.
        if (b > 0) {
          sb.append(Character.toString((char) b));
        }
      }

      blob.free();
      return sb.toString();
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public Timestamp getTimestamp(final String colName) {
    try {
      return rs.getTimestamp(colName);
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public int getInt(final String colName) {
    try {
      return rs.getInt(colName);
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public String getString(final String colName) {
    try {
      return rs.getString(colName);
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public boolean next() {
    try {
      return rs.next();
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public boolean isFirst() {
    try {
      return rs.isFirst();
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public int getColumnCount() {
    try {
      return this.rs.getMetaData().getColumnCount();
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public String getColumnName(final int idx) {
    try {
      return this.rs.getMetaData().getColumnName(idx);
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  public String getColumnTypeName(final int idx) {
    try {
      return this.rs.getMetaData().getColumnTypeName(idx);
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  /**
   * Closes the underlying ResultSet.
   * @throws SQLException if closing of underlying ResultSet
   *    results in an error.
   */
  public void close() {
    try {
      rs.close();
    } catch (final SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }
}

