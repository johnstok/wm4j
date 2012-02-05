/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
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
