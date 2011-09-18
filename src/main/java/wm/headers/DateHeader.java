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
 * along with wm4j. If not, see <http://www.gnu.org/licenses/>.
 *---------------------------------------------------------------------------*/
package wm.headers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Helper class for working with date headers.
 *
 * @author Keith Webster Johnston.
 */
public class DateHeader {

    private static final SimpleDateFormat FORMATTER;


    static {
        FORMATTER =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz"); //$NON-NLS-1$
        FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT"));   //$NON-NLS-1$
    }


    /**
     * Look up the current value of an outgoing request header.
     *
     * @param value The header value, as a string.
     *
     * @return The header as a date.
     */
    public static Date parse(final String value) {
        try {
            return FORMATTER.parse(value);
        } catch (final ParseException e) {
            return null;
        }
    }


    /**
     * Confirm the value of the specified header is a HTTP date.
     *
     * @param value The header value, as a string.
     *
     * @return True if the date is valid; false otherwise.
     */
    public static boolean isValidDate(final String value) {
        try {
            FORMATTER.parse(value);
            return true;
        } catch (final ParseException e) {
            return false;
        }
    }


    /**
     * Format the specified date as an HTTP date string.
     *
     * @param date The date to format.
     *
     * @return The date formatted as an HTTP date string.
     */
    public static String format(final Date date) {
        return FORMATTER.format(date);
    }
}
