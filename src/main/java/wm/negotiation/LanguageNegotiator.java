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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import wm.LanguageTag;
import wm.Value;
import wm.WeightedValue;


/**
 * Negotiates the preferred language for a resource.
 *
 * Implements RFC-2616, section 14.4.
 *
 * @author Keith Webster Johnston.
 */
public class LanguageNegotiator {

    private final Set<LanguageTag> _availableLanguages;

    /**
     * Constructor.
     *
     * @param availableLanguages Languages provided by a resource.
     */
    public LanguageNegotiator(final Set<LanguageTag> availableLanguages) {
        _availableLanguages = new HashSet<LanguageTag>(availableLanguages);
    }


    /**
     * Negotiate a language.
     *
     * @param languageRanges The range of accepted languages.
     *
     * @return The selected language.
     */
    public LanguageTag selectLanguage(final List<WeightedValue> languageRanges) {

        Collections.sort(languageRanges);

        /*
         * The special range "*", if present in the Accept-Language field,
         * matches every tag not matched by any other range present in the
         * Accept-Language field.
         */

        /*
         * The language quality factor assigned to a language-tag by the
         * Accept-Language field is the quality value of the longest
         * language-range in the field that matches the language-tag.
         *
         * Accept-Language: en-gb;q=0.8, en;q=0.7
         * Quality for lTag 'en-gb' is 0.8 even though 'en-gb' is "matched by" 'en'.
         */

        /*
         * If no language-range in the field matches the tag, the language
         * quality factor assigned is 0.
         */

        /*
         * Spec indicates an alternative algorithm:
         *  - For each supported language assign a q factor based on the Accept-Languages header.
         *  - Choose the lang with the highest q factor.
         */

        for (final WeightedValue lRange : languageRanges) {
            if (0>=lRange.getWeight()) { break; } // q <=0 is unacceptable.
            for (final LanguageTag lTag : _availableLanguages) {
                if (lTag.matchedBy(lRange.getValue())) {
                    return lTag;
                }
            }
        }

        return null;
    }


    /**
     * Negotiate a language.
     *
     * @param languageRanges The range of accepted languages.
     *
     * @return The selected language.
     */
    public LanguageTag selectLanguage(final WeightedValue... languageRanges) {
        return selectLanguage(new ArrayList<WeightedValue>(Arrays.asList(languageRanges)));
    }


    /**
     * Parse an 'Accept-Language' header into a list of weighted values.
     *
     * Accept-Language = "Accept-Language" ":"
     *                   1#( language-range [ ";" "q" "=" qvalue ] )
     * language-range  = ( ( 1*8ALPHA *( "-" 1*8ALPHA ) ) | "*" )
     *
     * Each language-range MAY be given an associated quality value which
     * represents an estimate of the user's preference for the languages
     * specified by that range. The quality value defaults to "q=1".
     *
     * Handle:
     *  - duplicate lRange (incl case variations).
     *  - malformed lRange
     *  - malformed field
     *
     * TODO: Add a description for this method.
     *
     * @param get_req_header
     * @return
     */
    public static List<WeightedValue> parse(final String value) {
        final List<WeightedValue> wValues = new ArrayList<WeightedValue>();

        if (null==value || 1>value.trim().length()) { return wValues; }

        final String[] lRanges = value.split(",");
        for (final String lRange : lRanges) {
            if (null==lRange || 1>lRange.trim().length()) { continue; }
            wValues.add(Value.parse(lRange).asWeightedValue("q",1f));
        }

        return wValues;
    }
}
