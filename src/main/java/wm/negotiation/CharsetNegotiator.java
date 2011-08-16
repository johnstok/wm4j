/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.negotiation;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import wm.Specification;
import wm.Specifications;
import wm.WeightedValue;


/**
 * Responsibility: negotiate an appropriate charset for the response body.
 *
 * @author Keith Webster Johnston.
 */
@Specifications ({
    @Specification (name="rfc-2616", section="3.4"),
    @Specification (name="rfc-2616", section="14.2")
})
public class CharsetNegotiator
    implements
        Negotiator<Charset> {

    // Default q value is 1
    // * matches all un-mentioned charsets (incl iso-8859-1)


    private final Set<Charset> _charsets;


    /**
     * Constructor.
     */
    public CharsetNegotiator() {
        this(Charset.availableCharsets().values());
    }


    /**
     * Constructor.
     *
     * @param charsets Charsets supported by the server.
     */
    public CharsetNegotiator(final Collection<Charset> charsets) {
        _charsets = new HashSet<Charset>(charsets);
    }


    /**
     * Constructor.
     *
     * @param charsets Charsets supported by the server.
     */
    public CharsetNegotiator(final Charset... charsets) {
        this(Arrays.asList(charsets));
    }


    /**
     * Select the preferred charset from those available.
     *
     * The special value "*", if present in the Accept-Charset field, matches
     * every character set (including ISO-8859-1) which is not mentioned
     * elsewhere in the Accept-Charset field. If no "*" is present in an
     * Accept-Charset field, then all character sets not explicitly mentioned
     * get a quality value of 0, except for ISO-8859-1, which gets a quality
     * value of 1 if not explicitly mentioned.
     *
     * If no Accept-Charset header is present, the default is that any character
     * set is acceptable. If an Accept-Charset header is present, and if the
     * server cannot send a response which is acceptable according to the
     * Accept-Charset header, then the server SHOULD send an error response with
     * the 406 (not acceptable) status code, though the sending of an
     * unacceptable response is also allowed.
     *
     * @param clientCharsets
     * @return
     *
     * @see Negotiator#select(List)
     */
    @Override
    public Charset select(final List<WeightedValue> clientCharsets) {

        // If no header is present any charset is acceptable.
        if (null==clientCharsets) { return null; }

        final List<WeightedValue> copy =
            new ArrayList<WeightedValue>(clientCharsets);

        final List<String> disallowed = new ArrayList<String>();
        for (final WeightedValue wv : clientCharsets) {
            if (wv.getWeight()<=0) { disallowed.add(wv.getValue().toLowerCase(Locale.US)); }
        }

        // If * isn't present it gets q=0
        if (!contains("*", copy)) {
            copy.add(new WeightedValue("*", 0));

            // ... except iso-8859-1 which gets q=1
            if (!contains("iso-8859-1", copy)) {
                copy.add(new WeightedValue("iso-8859-1", 1));
            }
        }

        Collections.sort(copy);

        for (final WeightedValue clientCharset : copy) {
            if (clientCharset.getWeight()<=0) { break; }
            final Charset charset = find(clientCharset.getValue(), disallowed);
            if (null!=charset) { return charset; }
        }

        return null;
    }


    private Charset find(final String charsetName,
                         final List<String> disallowed) {
        if ("*".equals(charsetName)) {
            for (final Charset charset : _charsets) {
                if (isAllowed(charset, disallowed)) { return charset; }
            }
            return null;
        }

        try {
            final Charset cs = Charset.forName(charsetName);
            return _charsets.contains(cs) ? cs : null;
        } catch (final IllegalCharsetNameException e) {
            return null;
        } catch (final UnsupportedCharsetException e) {
            return null;
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }


    private boolean isAllowed(final Charset charset,
                              final List<String> disallowed) {
        if (disallowed.contains(charset.name().toLowerCase(Locale.US))) { return false; }
        for (final String alias : charset.aliases()) {
            if (disallowed.contains(alias.toLowerCase(Locale.US))) { return false; }
        }
        return true;
    }


    private boolean contains(final String charset,
                             final List<WeightedValue> weightedValues) {
        for (final WeightedValue weightedValue : weightedValues) {
            if (charset.equals(weightedValue.getValue())) { return true; }
        }
        return false;
    }
}
