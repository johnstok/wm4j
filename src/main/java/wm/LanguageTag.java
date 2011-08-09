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

    // TODO: Replace with java.util.Locale ?

    private final String _primary;

    /**
     * Constructor.
     *
     * @param primary
     */
    public LanguageTag(final String primary) {
        /*
         * White space is not allowed within the tag and all tags are
         * case-insensitive.
         */
        _primary = primary; // TODO: Check for empty.
    }


    public boolean matchedBy(final String languageRange) { // See 14.4
        /*
         * A language-range matches a language-tag if it exactly equals the tag,
         * or if it exactly equals a prefix of the tag such that the first tag
         * character following the prefix is "-"
         */
        return _primary.equals(languageRange); // Also, 'tag starts with range' - eg 'en' matches 'en-gb'.
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((_primary == null) ? 0 : _primary.hashCode());
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
        if (_primary == null) {
            if (other._primary != null) {
                return false;
            }
        } else if (!_primary.equalsIgnoreCase(other._primary)) {
            return false;
        }
        return true;
    }
}
