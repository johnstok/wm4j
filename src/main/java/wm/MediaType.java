/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
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
     * Text whether the specified media type matches this one.
     *
     * @param mediaType The media type to test.
     *
     * @return True if the provided media type matches; false otherwise.
     */
    public boolean matches(final String mediaType) {
        return matches(new MediaType(mediaType));
    }


    /**
     * Text whether the specified media type matches this one.
     *
     * @param mediaType The media type to test.
     *
     * @return True if the provided media type matches; false otherwise.
     */
    private boolean matches(final MediaType mediaType) {
        if (_type.equalsIgnoreCase(mediaType._type) && _subtype.equalsIgnoreCase(mediaType._subtype)) {
            return true;
        } else if (_type.equalsIgnoreCase(mediaType._type) && "*".equals(mediaType._subtype)) {
            return true;
        } else if ("*".equals(mediaType._type) && "*".equals(mediaType._subtype)) {
            return true;
        }
        return false;
    }


    public static final MediaType ANY = new MediaType("*", "*");
    public static final MediaType HTML = new MediaType("text", "html");
}
