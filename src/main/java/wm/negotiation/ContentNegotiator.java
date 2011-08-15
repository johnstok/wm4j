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
package wm.negotiation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import wm.ContentEncoding;
import wm.Specification;
import wm.Specifications;
import wm.WeightedValue;


/**
 * Negotiator for entity encodings.
 *
 * @author Keith Webster Johnston.
 */
@Specifications({
    @Specification(name="rfc-2616", section="3.5"),
    @Specification(name="rfc-2616", section="14.3")
})
public class ContentNegotiator {

    private static final WeightedValue ANY =
        new WeightedValue(ContentEncoding.ANY, 0.001f);
    private static final WeightedValue IDENTITY =
        new WeightedValue(ContentEncoding.IDENTITY, 0.001f);

    private final Set<WeightedValue> _supportedEncodings;


    /**
     * Constructor.
     *
     * @param values The supported encodings
     */
    public ContentNegotiator(final WeightedValue... values) {
        final List<WeightedValue> supported = Arrays.asList(values);
        Collections.sort(supported);
        _supportedEncodings = new LinkedHashSet<WeightedValue>(supported);
        _supportedEncodings.add(IDENTITY);
        _supportedEncodings.remove(ANY);
    }


    /**
     * Constructor.
     *
     * @param values The supported encodings
     */
    public ContentNegotiator(final Set<String> values) {
        final List<WeightedValue> supported = new ArrayList<WeightedValue>();
        for (final String value : values) {
            supported.add(new WeightedValue(value, 1.0f));
        }
        Collections.sort(supported);
        _supportedEncodings = new LinkedHashSet<WeightedValue>(supported);
        _supportedEncodings.add(IDENTITY);
        _supportedEncodings.remove(ANY);
    }


    /**
     * Select an encoding from the specified list.
     *
     * @param clientEncodings The allowed encodings.
     *
     * @return The encoding selected using the HTTP 1.1 algorithm.
     */
    public String selectEncoding(final List<WeightedValue> clientEncodings) {
        if (null == clientEncodings) { return ContentEncoding.IDENTITY; }

        final List<WeightedValue> disallowedEncodings =
            new ArrayList<WeightedValue>();
        final List<WeightedValue> allowedEncodings =
            new ArrayList<WeightedValue>();

        for (final WeightedValue clientEncoding : clientEncodings) {
            if (clientEncoding.getWeight()<=0) {
                disallowedEncodings.add(clientEncoding);
            } else {
                allowedEncodings.add(clientEncoding);
            }
        }

        Collections.sort(allowedEncodings); //14.3#3

        for (final WeightedValue clientEncoding : allowedEncodings) {
            if (_supportedEncodings.contains(clientEncoding)) {
                return clientEncoding.getValue();
            }
            if (ContentEncoding.ANY.equals(clientEncoding.getValue())) {
                for (final WeightedValue supported : _supportedEncodings) {
                    if (!disallowedEncodings.contains(supported)) {
                        return supported.getValue();
                    }
                }
            }
        }
        if (disallowedEncodings.contains(ANY)
            && !allowedEncodings.contains(IDENTITY)) { return null; }
        if (disallowedEncodings.contains(IDENTITY)) { return null; }
        return ContentEncoding.IDENTITY;
    }


    /**
     * Select an encoding from the specified list.
     *
     * @param clientEncodings The allowed encodings.
     *
     * @return The encoding selected using the HTTP 1.1 algorithm.
     */
    public String selectEncoding(final WeightedValue... clientEncodings) {
        return selectEncoding(Arrays.asList(clientEncodings));
    }
}
