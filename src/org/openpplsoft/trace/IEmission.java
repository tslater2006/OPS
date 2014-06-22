/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

/**
 * All emissions, whether emitted by OPS or representing
 * an emission from the tracefile, must conform to this
 * interface.
 */
public interface IEmission {

  /**
   * In order to enforce emissions, we must have a way
   * to check equality across all of them.
   * @param obj the object to compare against the instance
   *    for equality
   * @return whether the provided obj is equal to the instance
   */
  boolean equals(Object obj);

  /**
   * Since we're overriding equals, force overriding of hashCode()
   * as well.
   * @return the hashcode for the instance
   */
  int hashCode();

  /**
   * Allows pretty-printing of emissions to standard/log
   * output.
   * @return the emission's String representation
   */
  String toString();
}
