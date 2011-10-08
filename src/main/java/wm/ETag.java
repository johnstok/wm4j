/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;


/**
 * An entity tag - used for comparing multiple entities from the same resource.
 *
 * @author Keith Webster Johnston.
 */
@Specification(name="rfc-2616", section="3.11")
public class ETag {

    // FIXME: Added boolean `_weak` field.

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
