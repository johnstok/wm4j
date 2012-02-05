/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;


/**
 * Represents a URI path.
 *
 * @author Keith Webster Johnston.
 */
public class Path {

    private final ArrayList<String> _segments = new ArrayList<String>();


    /**
     * Constructor.
     *
     * @param rawPath The encoded, non-normalised path, as a string.
     * @param encodingCharset The character set to use for decoding.
     */
    public Path(final String rawPath, final Charset encodingCharset) {
        if (null==rawPath) { return; }

        final String[] segments = rawPath.split("/");
        for (final String s : segments) {
            final String segment = decode(s, encodingCharset);
            if (segment.length()>0 && !".".equals(segment)) {
                if ("..".equals(segment) && _segments.size()>0) {
                    final int lastIndex = _segments.size()-1;
                    if (!"..".equals(_segments.get(lastIndex))) {
                        _segments.remove(_segments.size()-1);
                    } else {
                        _segments.add(segment);
                    }
                } else {
                    _segments.add(segment);
                }
            }
        }
    }


    /**
     * Get the number of segments in the path.
     *
     * @return The segment count, as an integer.
     */
    public int getSize() { return _segments.size(); }


    /**
     * Get the segment at the specified index.
     *
     * Segments are indexed from 0; 0 being the left-most segment in the URI
     * string.
     *
     * @param index The index of the required segment.
     *
     * @return The segment value, as a string.
     */
    public String getSegment(final int index) {
        return _segments.get(index);
    }


    private String decode(final String segment, final Charset encodingCharset) {
        try {
            return URLDecoder.decode(segment, encodingCharset.name());
        } catch (final UnsupportedEncodingException e) {
            throw new ClientHttpException(Status.BAD_REQUEST, e);
        }
    }
}
