/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 * Modified by   $Author$
 * Modified on   $Date$
 *
 * Changes: see version control.
 *-----------------------------------------------------------------------------
 */
package wm;

import java.util.ArrayList;
import java.util.List;


/**
 * Supported HTTP headers.
 *
 * @author Keith Webster Johnston.
 */
public final class Header {

    /** ALLOW : String. */
    public static final String ALLOW =
        "Allow";                                                   //$NON-NLS-1$

    /** LOCATION : String. */
    public static final String LOCATION =
        "Location";                                                //$NON-NLS-1$

    /** ACCEPT_ENCODING : String. */
    public static final String ACCEPT_ENCODING =
        "Accept-Encoding";                                         //$NON-NLS-1$

    /** SERVER : String. */
    public static final String SERVER =
        "Server";                                                  //$NON-NLS-1$

    /** CONTENT_TYPE : String. */
    public static final String CONTENT_TYPE =
        "Content-Type";                                            //$NON-NLS-1$

    /** DATE : String. */
    public static final String DATE =
        "Date";                                                    //$NON-NLS-1$

    /** LAST_MODIFIED : String. */
    public static final String LAST_MODIFIED =
        "Last-Modified";                                           //$NON-NLS-1$

    /** CONTENT_ENCODING : String. */
    public static final String CONTENT_ENCODING =
        "Content-Encoding";                                        //$NON-NLS-1$

    /** CONTENT_ENCODING : String. */
    public static final String E_TAG =
        "ETag";                                                    //$NON-NLS-1$

    /** RANGE : String. */
    public static final String RANGE =
        "Range";                                                   //$NON-NLS-1$

    /** CONTENT_RANGE : String. */
    public static final String CONTENT_RANGE =
        "Content-Range";                                           //$NON-NLS-1$

    /** ACCEPT_RANGES : String. */
    public static final String ACCEPT_RANGES =
        "Accept-Ranges";                                           //$NON-NLS-1$

    /** IF_MODIFIED_SINCE : String */
    public static final String IF_MODIFIED_SINCE =
        "If-Modified-Since";                                       //$NON-NLS-1$

    /** IF_MATCH : String */
    public static final String IF_MATCH =
        "If-Match";                                                //$NON-NLS-1$

    /** IF_NONE_MATCH */
    public static final String IF_NONE_MATCH =
        "If-None-Match";                                           //$NON-NLS-1$

    /** COOKIE : String */
    public static final String COOKIE =
        "Cookie";                                                  //$NON-NLS-1$

    /** ACCEPT : String. */
    public static final String ACCEPT =
        "Accept";                                                  //$NON-NLS-1$

    /** ACCEPT_LANGUAGE : String. */
    public static final String ACCEPT_LANGUAGE =
        "Accept-Language";                                         //$NON-NLS-1$

    /** ACCEPT_CHARSET : String. */
    public static final String ACCEPT_CHARSET =
        "Accept-Charset";                                          //$NON-NLS-1$

    /** CONTENT_LENGTH : String. */
    public static final String CONTENT_LENGTH =
        "Content-Length";                                          //$NON-NLS-1$

    /** IF_UNMODIFIED_SINCE : String. */
    public static final String IF_UNMODIFIED_SINCE =
        "If-Unmodified-Since";                                     //$NON-NLS-1$


    /**
     * Parse an 'Accept-Charset' header into a list of weighted values.
     *
     * <pre>
     *       Accept-Charset = "Accept-Charset" ":"
     *        1#( ( charset | "*" )[ ";" "q" "=" qvalue ] )
     * </pre>
     *
     * Each charset MAY be given an associated quality value which represents
     * the user's preference for that charset. The default value is q=1.
     *
     * @param value
     *
     * @return
     */
    public static List<WeightedValue> parseAcceptCharset(final String value) {
        /*
         * TODO Handle:
         *  - duplicate cRange (incl case variations).
         *  - malformed cRange
         *  - malformed field
         */
        final List<WeightedValue> wValues = new ArrayList<WeightedValue>();

        if (null==value || 1>value.trim().length()) { return wValues; }

        final String[] cRanges = value.split(",");
        for (final String cRange : cRanges) {
            if (null==cRange || 1>cRange.trim().length()) { continue; }
            wValues.add(Value.parse(cRange).asWeightedValue("q",1f));
        }

        return wValues;
    }


    /**
     * TODO: Add a description for this method.
     *
     * @param get_req_header
     * @return
     */
    public static List<WeightedValue> parseAcceptEncoding(final String get_req_header) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    private Header() { super(); }
}
