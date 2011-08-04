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
package wm.multipart;

import static wm.Header.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.activation.MimetypesFileTypeMap;
import wm.Range;


/**
 * A multipart/byteranges resource.
 *
 * @author Keith Webster Johnston.
 */
public class ByteRanges {

    private static final String SLASH_F = "/";                     //$NON-NLS-1$
    private static final String DASH = "-";                        //$NON-NLS-1$
    private static final String BYTES = "bytes";                   //$NON-NLS-1$
    private static final String SP = " ";                          //$NON-NLS-1$
    private static final String UTF_8 = "UTF-8";                   //$NON-NLS-1$
    private static final String READ = "r";                        //$NON-NLS-1$
    private static final String HEADER_DELIM = ": ";               //$NON-NLS-1$
    private static final String CRLF = "\r\n";                     //$NON-NLS-1$
    private static final String BOUNDARY_PREFIX = "--";            //$NON-NLS-1$


    /**
     * Collapse the supplied ranges, removing unsatisfiable ranges.
     * <br>The collapse algorithm only collapses correctly ordered, consecutive,
     * overlapping ranges. For example: <code>0-10,5-20</code>.
     *
     * @param ranges The ranges to collapse.
     * @param length The length of the source data.
     *
     * @return A collapsed set of ranges.
     */
    public static List<Range> collapse(final List<Range> ranges,
                                       final long length) {
        final List<Range> collapsed = new ArrayList<Range>();


        for (int i=0; i<ranges.size(); i++) {
            final Range r = ranges.get(i);

            if (!r.isSatisfiable(length)) { continue; }

            final long from = r.getFirstByte(length);
            long to   = r.getLastByte(length);

            for (int j=i+1; j<ranges.size(); j++) {
                final Range next = ranges.get(j);
                if (!next.isSatisfiable(length)) { break; }
                final long nextFrom = next.getFirstByte(length);
                final long nextTo   = next.getLastByte(length);
                if (nextFrom>=from && nextFrom<=to) {
                    to = (nextTo>to) ? nextTo : to;
                    i=j;
                } else {
                    break;
                }
            }
            collapsed.add(new Range(from, to));
        }

        return collapsed;
    }
    private final String           _charset;
    private final String           _boundary;
    private final RandomAccessFile _file;


    private final String           _mimeType;


    /**
     * Constructor.
     *
     * @param file The file to write data from.
     *
     * @throws FileNotFoundException If the specified file cannot be read.
     */
    public ByteRanges(final File file) throws FileNotFoundException {
        _charset  = UTF_8;
        _boundary = UUID.randomUUID().toString();
        _file     = new RandomAccessFile(file, READ);
        _mimeType = new MimetypesFileTypeMap().getContentType(file);
    }


    private byte[] bytesFor(final String string)
                                           throws UnsupportedEncodingException {
        return string.getBytes(_charset);
    }


    /**
     * Accessor.
     *
     * @return Returns the boundary.
     */
    public String getBoundary() {
        return _boundary;
    }


    /**
     * Get the mime type this range.
     *
     * @return The mime type, as a string.
     */
    public String getMimeType() {
        return
            "multipart/byteranges"                                 //$NON-NLS-1$
                + "; boundary="+ _boundary                         //$NON-NLS-1$
                + "; charset=" + _charset;                         //$NON-NLS-1$
    }


    /**
     * Write a range of bytes to an output stream.
     *
     * @param ranges The byte ranges to write.
     * @param outputStream The output stream.
     *
     * @throws IOException If the write fails.
     */
    public void write(final OutputStream outputStream,
                      final List<Range> ranges) throws IOException {

        // Determine content length?
        for (final Range r : ranges) {

            if (!r.isSatisfiable(_file.length())) { continue; }

            final long from = r.getFirstByte(_file.length());
            final long to   = r.getLastByte(_file.length());

            outputStream.write(bytesFor(BOUNDARY_PREFIX));
            outputStream.write(bytesFor(_boundary));
            outputStream.write(bytesFor(CRLF));
            outputStream.write(bytesFor(CONTENT_TYPE+HEADER_DELIM+_mimeType));
            outputStream.write(bytesFor(CRLF));
            // Transfer encoding?
            // binary - see http://tools.ietf.org/html/rfc2045#section-2.9
            outputStream.write(
                bytesFor(
                    CONTENT_RANGE+HEADER_DELIM
                    +BYTES+SP+from
                    +DASH+to
                    +SLASH_F+_file.length()));
            outputStream.write(bytesFor(CRLF+CRLF));
            _file.seek(from);

            // BAD - Need to use buffer for large ranges.
            final byte[] bytes = new byte[(int) r.getSize(_file.length())];
            final int read = _file.read(bytes);
            if (read<bytes.length) {
                throw
                    new RuntimeException("Unsatisfiable range!"); //$NON-NLS-1$
            }
            outputStream.write(bytes);
            outputStream.write(bytesFor(CRLF));
        }
        outputStream.write(bytesFor(BOUNDARY_PREFIX));
        outputStream.write(bytesFor(_boundary));
        outputStream.write(bytesFor(BOUNDARY_PREFIX));
    }
}
