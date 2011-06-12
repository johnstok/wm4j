/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Responsibility: negotiate an appropriate charset for the response body.
 *
 * @author Keith Webster Johnston.
 */
public class CharsetNegotiator {

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


    public Charset selectCharset(final List<WeightedValue> clientCharsets) {

        // If no header is present any charset is acceptable.
        if (null==clientCharsets) { return null; }

        final List<WeightedValue> copy =
            new ArrayList<WeightedValue>(clientCharsets);

        final List<String> disallowed = new ArrayList<String>();
        for (WeightedValue wv : clientCharsets) {
            if (wv.getWeight()<=0) { disallowed.add(wv.getValue()); }
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

        for (WeightedValue clientCharset : copy) {
            if (clientCharset.getWeight()<=0) { break; }
            Charset charset = find(clientCharset.getValue(), disallowed);
            if (null!=charset) { return charset; }
        }

        return null;
    }


    private Charset find(final String charsetName,
                         final List<String> disallowed) {
        if ("*".equals(charsetName)) {
            for (Charset charset : _charsets) {
                if (isAllowed(charset, disallowed)) { return charset; }
            }
            return null;
        }

        try {
            return Charset.forName(charsetName);
        } catch (IllegalCharsetNameException e) {
            return null;
        } catch (UnsupportedCharsetException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    /**
     * TODO: Add a description for this method.
     *
     * @param aliases
     * @param disallowed
     * @return
     */
    private boolean isAllowed(final Charset charset,
                              final List<String> disallowed) {
        if (disallowed.contains(charset.name())) { return false; }
        for (String alias : charset.aliases()) {
            if (disallowed.contains(alias)) { return false; }
        }
        return true;
    }


    private boolean contains(final String charset,
                             final List<WeightedValue> weightedValues) {
        for (WeightedValue weightedValue : weightedValues) {
            if (charset.equals(weightedValue.getValue())) { return true; }
        }
        return false;
    }
}
