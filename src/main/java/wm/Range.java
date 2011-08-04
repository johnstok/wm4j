/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * A range of bytes.
 *
 * @author Keith Webster Johnston.
 */
public class Range {

    /**
     * Parse a 'Range' header value.
     * <br>Invalid ranges are discarded.
     *
     * @param rangeString The string to parse.
     *
     * @return The corresponding list of valid headers.
     */
    public static List<Range> parse(final String rangeString) {

        final List<Range> r = new ArrayList<Range>();

        if (null==rangeString) { return r; }

        final String[] parts = rangeString.split("=");
        if (2!=parts.length) { return r; }
        if (!"bytes".equals(parts[0].trim())) { return r; }

        final String[] ranges = parts[1].trim().split(",");

        for (final String range : ranges) {
            if (0==range.trim().length()) { continue; }

            final Pattern rangePattern =
                Pattern.compile("(\\d*)-(\\d*)");                  //$NON-NLS-1$
            final Matcher m = rangePattern.matcher(range.trim());

            if (!m.matches() || (2!=m.groupCount())) { return r; }

            try {

                final String fromString = m.group(1);
                final Long from =
                    (0==fromString.length())
                        ? null
                        : Long.valueOf(fromString);
                final String toString = m.group(2);
                final Long to =
                    (0==toString.length())
                        ? null
                        : Long.valueOf(toString);

                r.add(new Range(from, to));

            } catch (final NumberFormatException e) {
                continue;
            }
        }

        return r;
    }


    private final Long _from;
    private final Long _to;


    /**
     * Constructor.
     *
     * @param from The first byte.
     * @param to   The last byte.
     */
    public Range(final long from, final long to) {
        this(Long.valueOf(from), Long.valueOf(to));
    }


    /**
     * Constructor.
     *
     * @param from The first byte.
     * @param to   The last byte.
     */
    public Range(final Long from, final Long to) {
        super();
        _from = from;
        _to = to;
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
        final Range other = (Range) obj;
        if (getFrom() == null) {
            if (other.getFrom() != null) {
                return false;
            }
        } else if (!getFrom().equals(other.getFrom())) {
            return false;
        }
        if (getTo() == null) {
            if (other.getTo() != null) {
                return false;
            }
        } else if (!getTo().equals(other.getTo())) {
            return false;
        }
        return true;
    }


    /**
     * Calculate the first byte for this range.
     *
     * @param length The length of the resource in bytes.
     *
     * @return The first byte, as a long.
     */
    public long getFirstByte(final long length) {
        if (null!=getFrom()) { return getFrom().longValue();  }
        final long from = length-getTo().longValue();
        return (from<0) ? 0 : from;
    }


    /**
     * Accessor.
     *
     * @return Returns the from.
     */
    public Long getFrom() {
        return _from;
    }


    /**
     * Calculate the last byte for this range.
     *
     * @param length The length of the resource in bytes.
     *
     * @return The last byte, as a long.
     */
    public long getLastByte(final long length) {
        if (null==getFrom()) { return length-1; }
        if ((null==getTo()) || (getTo().longValue()>=length)) {
            return length-1;
        }
        return getTo().longValue();
    }


    /**
     * Get the size of this range in bytes.
     *
     * @param length The length of the resource in bytes.
     *
     * @return The size of the range.
     */
    public long getSize(final long length) {
        final long from = getFirstByte(length);
        final long to   = getLastByte(length);
        return to-from+1;
    }


    /**
     * Accessor.
     *
     * @return Returns the to.
     */
    public Long getTo() {
        return _to;
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((getFrom() == null) ? 0 : getFrom().hashCode());
        result =
            prime * result + ((getTo() == null) ? 0 : getTo().hashCode());
        return result;
    }


    /**
     * Can this range be satisfied by a resource of the specified length.
     *
     * @param contentLength The length of the resource in bytes.
     *
     * @return True if the range can be satisfied, false otherwise.
     */
    public boolean isSatisfiable(final long contentLength) {
        /*
         * If a syntactically valid byte-range-set includes at least one
         * byte-range-spec whose first-byte-pos is less than the current length
         * of the entity-body, or at least one suffix-byte-range-spec with a
         * non-zero suffix-length, then the byte-range-set is satisfiable.
         */
        return
            isValid()
            && (((null!=getFrom()) && (getFrom().longValue()<contentLength))
                || ((null==getFrom())
                    && (null!=getTo()) && (getTo().longValue()>0)));
    }


    /**
     * Is this range valid.
     *
     * @return True if the range is valid, false otherwise.
     */
    public boolean isValid() {

        final boolean fromOrToPresent =
            (((null!=getFrom()) && (getFrom().longValue()>=0))
            || ((null!=getTo()) && (getTo().longValue()>0)));

        final boolean toGreaterThanFrom =
            ((getFrom()==null)
            || (getTo()==null)
            || (getTo().longValue()>=getFrom().longValue()));

        return fromOrToPresent && toGreaterThanFrom;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        return
            "Range "
            + ((null==getFrom()) ? "" : getFrom())
            + "-"
            + ((null==getTo()) ? "" : getTo());
    }
}
