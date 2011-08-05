/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;


/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public class ETag {

    private final String _value;


    /**
     * Constructor.
     *
     * @param value
     */
    public ETag(final String value) { _value = value; }


    /**
     * Accessor.
     *
     * @return Returns the value.
     */
    public String getValue() { return _value; }
}
