/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt.peoplecode;

import org.openpplsoft.runtime.*;

public class AppPackagePath {

  private final String[] parts;

  public AppPackagePath(final String pathStr) {
    String[] pathParts = pathStr.split(":");
    if(pathParts.length < 2) {
      throw new OPSVMachRuntimeException("Expecting at least two parts " +
         "in app package / class path: " + pathStr);
    }

    /*
     * Remove the last part of the path. If it's a package path,
     * the last part will be a wildcard ('*'). Otherwise, it's an app
     * class path, in which case the last part will be the name of
     * an app class. We only want package (and subpackage) names here.
     * NOTE: Exceptions to this can occur; apparently in PS it is possible
     * to "import $PkgName:$SpkgName:$ClassName:*", which is ridiculous. Code
     * in the package path traversal method in AppClassPeopleCodeProg has been
     * modified to handle this situation, *but code using this class should not
     * assume that the path parts array always points to a subpackage.*
     */
    this.parts = new String[pathParts.length - 1];
    for(int i=0; i < this.parts.length; i++) {
      this.parts[i] = pathParts[i];
    }
  }

  public String getRootPkgName() {
    return this.parts[0];
  }

  public String[] getParts() {
    return this.parts;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(this.parts[0]);
    for(int i = 1; i < this.parts.length; i++) {
      builder.append(':').append(parts[i]);
    }
    return builder.toString();
  }
}
