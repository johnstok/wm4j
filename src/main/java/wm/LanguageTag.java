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

import java.util.Locale;


/**
 * Identifies a natural language spoken, written, etc. by human beings.
 *
 * A language tag is composed of 1 or more parts: A primary language tag and a
 * possibly empty series of subtags:
 *
 *      language-tag  = primary-tag *( "-" subtag )
 *      primary-tag   = 1*8ALPHA
 *      subtag        = 1*8ALPHA
 *
 * White space is not allowed within the tag and all tags are case-insensitive.
 *
 * Implements RFC-2616, section 3.10.
 * Implements RFC-1766
 *
 * @author Keith Webster Johnston.
 */
public class LanguageTag {

    private final String _value;


    /**
     * Constructor.
     *
     * @param value
     */
    public LanguageTag(final String value) {
        /*
         * White space is not allowed within the tag and all tags are
         * case-insensitive.
         */
        _value = Contract.require().matches("\\p{Alpha}{1,8}(-\\p{Alpha}{1,8})*", value);
    }


    /**
     * TODO: Add a description for this method.
     *
     * @param languageRange
     *
     * @return
     */
    public boolean matchedBy(final String languageRange) { // See 14.4
        /*
         * A language-range matches a language-tag if it exactly equals the tag,
         * or if it exactly equals a prefix of the tag such that the first tag
         * character following the prefix is "-"
         */
        String ciTag   = _value.toLowerCase(Locale.US);
        String ciRange = languageRange.toLowerCase(Locale.US);
        return ciTag.equals(ciRange)
               || (ciTag.startsWith(ciRange)
                   && '-'==ciTag.charAt(languageRange.length()));
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((_value == null) ? 0 : _value.hashCode());
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
        LanguageTag other = (LanguageTag) obj;
        if (_value == null) {
            if (other._value != null) {
                return false;
            }
        } else if (!_value.equalsIgnoreCase(other._value)) {
            return false;
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        return _value;
    }


    /**
     * TODO: Add a description for this method.
     *
     * @param languageRange
     *
     * @return
     */
    public int matchDepth(final String languageRange) {
        return (matchedBy(languageRange)) ? languageRange.split("-").length : 0 ;
    }
}
