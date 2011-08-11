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
package wm.negotiation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import wm.MediaType;
import wm.Value;
import wm.WeightedValue;


/**
 * Negotiates the preferred media type for a resource.
 *
 * Implements RFC-2616, Section 14.1.
 *
 * @author Keith Webster Johnston.
 */
public class MediaTypeNegotiator {

    private final Set<MediaType> _availableMediaTypes;


    /**
     * Constructor.
     *
     * @param keySet
     */
    public MediaTypeNegotiator(final Set<MediaType> availableMediaTypes) {
        _availableMediaTypes = availableMediaTypes; // TODO: Make defensive copy?
    }


    /**
     * Parse an 'Accept' header into a list of weighted values.
     *
     * <pre>
     * Accept         = "Accept" ":"
     *                  #( media-range [ accept-params ] )
     * media-range    = ( "&#42;/*"
     *                  | ( type "/" "*" )
     *                  | ( type "/" subtype )
     *                  ) *( ";" parameter )
     * accept-params  = ";" "q" "=" qvalue *( accept-extension )
     * accept-extension = ";" token [ "=" ( token | quoted-string ) ]
     * </pre>
     *
     * The asterisk "*" character is used to group media types into ranges, with
     * "&#42;/*" indicating all media types and "type/*" indicating all subtypes
     * of that type. The media-range MAY include media type parameters that are
     * applicable to that range.
     *
     * Each media-range MAY be followed by one or more accept-params, beginning
     * with the "q" parameter for indicating a relative quality factor. The
     * first "q" parameter (if any) separates the media-range parameter(s) from
     * the accept-params. Quality factors allow the user or user agent to
     * indicate the relative degree of preference for that media-range, using
     * the qvalue scale from 0 to 1 (section 3.9). The default value is q=1.
     *
     * Use of the "q" parameter name to separate media type parameters from
     * Accept extension parameters is due to historical practice. Although this
     * prevents any media type parameter named "q" from being used with a media
     * range, such an event is believed to be unlikely given the lack of any "q"
     * parameters in the IANA media type registry and the rare usage of any
     * media type parameters in Accept. Future media types are discouraged from
     * registering any parameter named "q".
     *
     * @param value The value to parse.
     *
     * @return The corresponding list of weighted values.
     */
    public static List<WeightedValue> parse(final String value) {
        /*
         * TODO Handle:
         *  - duplicate mtRange (incl case variations).
         *  - malformed mtRange
         *  - malformed value
         */
        final List<WeightedValue> wValues = new ArrayList<WeightedValue>();

        if (null==value || 1>value.trim().length()) { return wValues; }

        final String[] mtRanges = value.split(",");
        for (final String mtRange : mtRanges) {
            if (null==mtRange || 1>mtRange.trim().length()) { continue; }
            wValues.add(Value.parse(mtRange).asWeightedValue("q",1f));
        }

        return wValues;
    }


    /**
     * Negotiate a media type.
     *
     * If no Accept header field is present, then it is assumed that the client
     * accepts all media types. If an Accept header field is present, and if the
     * server cannot send a response which is acceptable according to the
     * combined Accept field value, then the server SHOULD send a 406 (not
     * acceptable) response.
     *
     * Media ranges can be overridden by more specific media ranges or specific
     * media types. If more than one media range applies to a given type, the
     * most specific reference has precedence. For example,
     *
     * <pre>Accept: text/*, text/html, text/html;level=1, &#42;/*</pre>
     * have the following precedence:
     * <pre>
     * 1) text/html;level=1
     * 2) text/html
     * 3) text/*
     * 4) &#42;/*
     * </pre>
     *
     * The media type quality factor associated with a given type is determined
     * by finding the media range with the highest precedence which matches that
     * type. For example,
     *
     * <pre>
     * Accept: text/*;q=0.3, text/html;q=0.7, text/html;level=1,
     *         text/html;level=2;q=0.4, &#42;/*;q=0.5
     * </pre>
     * would cause the following values to be associated:
     * <pre>
     * text/html;level=1         = 1
     * text/html                 = 0.7
     * text/plain                = 0.3
     *
     * image/jpeg                = 0.5
     * text/html;level=2         = 0.4
     * text/html;level=3         = 0.7
     * </pre>
     * Note: A user agent might be provided with a default set of quality
     * values for certain media ranges. However, unless the user agent is
     * a closed system which cannot interact with other rendering agents,
     * this default set ought to be configurable by the user.
     *
     * @param mediaRanges The range of accepted media types.
     *
     * @return The selected media type.
     */
    public MediaType select(final List<WeightedValue> mediaRanges) {
        return null;
    }
}
