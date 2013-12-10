package com.enterrupt.pt.peoplecode;

import com.enterrupt.runtime.*;

public class AppPackagePath {

	public String[] parts;

	public AppPackagePath(String pathStr) {
		String[] pathParts = pathStr.split(":");
		if(pathParts.length < 2) {
			throw new EntVMachRuntimeException("Expecting at least two parts " +
				"in app package / class path: " + pathStr);
		}

		/**
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

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.parts[0]);
		for(int i = 1; i < this.parts.length; i++) {
			builder.append(":").append(parts[i]);
		}
		return builder.toString();
	}
}
