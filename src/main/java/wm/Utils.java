/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;


import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Collection;


/**
 * Utility methods.
 *
 * @author Keith Webster Johnston.
 */
public final class Utils {

    /**
     * Calculate the ETag for a file.
     *
     * @param file The file to analyse.
     *
     * @return The ETag, as a string.
     */
    public static String eTag(final File file) {
        try {
            final String uid =
                file.length()
                +":"+file.lastModified()                           //$NON-NLS-1$
                +":"+file.getCanonicalPath();                      //$NON-NLS-1$

            final MessageDigest m =
                MessageDigest.getInstance("MD5");                  //$NON-NLS-1$
            final byte[] data = uid.getBytes();
            m.update(data, 0, data.length);
            final BigInteger i = new BigInteger(1, m.digest());
            return String.format("%1$032X", i);                    //$NON-NLS-1$

        } catch (final Exception e) {
            // FIXME: Log error?
            return null;
        }
    }

    /**
     * Join a collection of strings with a specified delimiter.
     *
     * @param values    The values to join.
     * @param delimiter The delimiter character.
     *
     * @return The joined value, in a string buffer.
     */
    public static StringBuffer join(final Collection<String> values,
                                    final char delimiter) {
        final StringBuffer buffer = new StringBuffer();
        for (final String value : values) {
            buffer.append(value);
            buffer.append(delimiter);
        }
        final int length = buffer.length();
        if (length>0) {
            buffer.deleteCharAt(length-1);
        }
        return buffer;
    }


    private Utils() { super(); }
}
