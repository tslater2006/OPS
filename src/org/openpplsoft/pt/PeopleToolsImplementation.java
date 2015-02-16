/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotates all methods that implement PeopleTools functionality
 * for use by PeopleCode programs being executed by the runtime;
 * this anntotation exists solely for documentation purposes
 * at the time of this writing. Ideally, down the road,
 * I'd like to have the compiler ensure various properties
 * of all annotated methods (i.e., public void signature,
 * never called directly by the interpreter/runtime code, etc.).
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface PeopleToolsImplementation {}
