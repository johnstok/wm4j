/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;


/**
 * The HTTP version.
 *
 * @author Keith Webster Johnston.
 */
public class Version {

    private final int _major;
    private final int _minor;


    /**
     * Constructor.
     *
     * @param major The major HTTP version.
     * @param minor The minor HTTP version.
     */
    public Version(final int major, final int minor) {
        super();
        _major = major;
        _minor = minor;
    }


    /**
     * Get the major HTTP version.
     *
     * @return Version number as an integer.
     */
    public int major() { // TODO: Rename to getMajor()?
        return _major;
    }


    /**
     * Get the minor HTTP version.
     *
     * @return Version number as an integer.
     */
    public int minor() { // TODO: Rename to getMinor()?
        return _minor;
    }
}
