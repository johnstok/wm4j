/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * This file is part of wm4j.
 *
 * wm4j is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * wm4j is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with wm4j.  If not, see <http://www.gnu.org/licenses/>.
 *---------------------------------------------------------------------------*/
package wm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Encapsulates a media type.
 *
 * The type, sub-type, and parameter attribute names are case-insensitive.
 *
 * @author Keith Webster Johnston.
 */
@Specifications({
    @Specification(name="rfc-2616", section="3.7"),
    @Specification(name="rfc-1590")
})
public class MediaType {

    // TODO: Parameters.

    private final String _type;
    private final String _subtype;


    /**
     * Constructor.
     *
     * @param type
     * @param subtype
     */
    public MediaType(final String type, final String subtype) {
        _type = Contract.require().matches(Syntax.TOKEN+"+", type);
        _subtype = Contract.require().matches(Syntax.TOKEN+"+", subtype);
    }


    /**
     * Constructor.
     *
     * A media type represented as a string, in the following format: <pre>
     * media-type     = type "/" subtype *( ";" parameter )
     * type           = token
     * subtype        = token</pre>
     *
     * Linear white space (LWS) MUST NOT be used between the type and subtype,
     * nor between an attribute and its value.
     *
     * @param mediaType
     * @param subtype
     */
    public MediaType(final String mediaType) {
        final Matcher m =
            Pattern.compile("("+Syntax.TOKEN+"+)/("+Syntax.TOKEN+"+)")
            .matcher(mediaType);
        if (m.matches()) {
            _type = m.group(1);
            _subtype = m.group(2);
        } else {
            throw new IllegalArgumentException();
        }
    }


    /**
     * Accessor.
     *
     * @return Returns the type.
     */
    public String getType() {
        return _type;
    }


    /**
     * Accessor.
     *
     * @return Returns the subtype.
     */
    public String getSubtype() {
        return _subtype;
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((_subtype == null) ? 0 : _subtype.hashCode());
        result = prime * result + ((_type == null) ? 0 : _type.hashCode());
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MediaType other = (MediaType) obj;
        if (_subtype == null) {
            if (other._subtype != null) {
                return false;
            }
        } else if (!_subtype.equalsIgnoreCase(other._subtype)) {
            return false;
        }
        if (_type == null) {
            if (other._type != null) {
                return false;
            }
        } else if (!_type.equalsIgnoreCase(other._type)) {
            return false;
        }
        return true;
    }


    /**
     * Test whether the specified media type matches this one.
     *
     * @param mediaType The media type to test.
     *
     * @return True if the provided media type matches; false otherwise.
     */
    public boolean matches(final String mediaType) {
        return matches(new MediaType(mediaType));
    }


    /**
     * Test whether the specified media type matches this one.
     *
     * @param mediaType The media type to test.
     *
     * @return True if the provided media type matches; false otherwise.
     */
    public boolean matches(final MediaType mediaType) {
        if (_type.equalsIgnoreCase(mediaType._type) && _subtype.equalsIgnoreCase(mediaType._subtype)) {
            return true;
        } else if (_type.equalsIgnoreCase(mediaType._type) && "*".equals(mediaType._subtype)) {
            return true;
        } else if ("*".equals(mediaType._type) && "*".equals(mediaType._subtype)) {
            return true;
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        return _type+"/"+_subtype;
    }


    public static final MediaType ANY = new MediaType("*", "*"); // TODO: Should wildcards be allowed?
    public static final MediaType HTML = new MediaType("text", "html");
    public static final MediaType XML = new MediaType("application", "xml");
    public static final MediaType JPEG = new MediaType("image", "jpeg");
    public static final MediaType JSON = new MediaType("application", "json");


    /**
     * TODO: Add a description for this method.
     *
     * @param match
     * @return
     */
    public boolean precedes(final MediaType mediaType) {
        if (null==mediaType) {
            return true;
        } else if (ANY.equals(mediaType) && !"*".equals(_type)) {
            return true;
        } else if (_type.equalsIgnoreCase(mediaType._type)
                   && "*".equals(mediaType._subtype)
                   && !"*".equals(_subtype)) {
            return true;
        }
        return false;
    }
}
