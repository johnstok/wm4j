/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;


/**
 * Documents the specification that a Java class implements.
 *
 * @author Keith Webster Johnston.
 */
public @interface Specification {
    String name();
    String section() default "";
}
