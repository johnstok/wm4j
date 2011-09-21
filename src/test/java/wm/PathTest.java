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

import static org.junit.Assert.*;
import java.nio.charset.Charset;
import org.junit.Test;



/**
 * Tests for the {@link Path} Class.
 *
 * @author Keith Webster Johnston.
 */
public class PathTest {

    /*
absolute URIs always begin with a scheme name followed by a colon

Get examples from RFC 2396:
URI-reference
absoluteURI
relativeURI
port
host
abs_path
rel_path
authority

Enumerate character sets from RFC 2396:
reserved
unsafe

Path tests:
 * spaces - trailing, within
 * control chars

RFC 2396

 * RESERVED   = ;/?:@&=+$, // characters that are allowed within a URI, but which may not be allowed within a particular component of the generic URI syntax
 * MARK       = -_.!~*'()       // punctuation
 * UNRESERVED = MARK + ALPHANUM // allowed in a URI and do not have a reserved purpose; can be escaped without changing the semantics of the URI
 * ESCAPED    = ^UNRESERVED     // data must be escaped if it does not have a representation using an unreserved character
 * EXCLUDED   = CONTROL + SPACE + DELIMS + UNWISE // excluded characters must be escaped in order to be properly represented within a URI
 * CONTROL    = #00-#1F + #7F   // they are non-printable and because they are likely to be misinterpreted
 * SPACE      = <SPACE>         // significant spaces may disappear and insignificant spaces may be introduced when URI are transcribed or typeset
 * DELIMS     = <>#%"           // often used as the delimiters in or around URI
 * UNWISE     = {}|\^[]`        // gateways and other transport agents are known to sometimes modify such characters

RFC 2396; sec 3.3
segment       = *pchar *( ";" param ) // Within a path segment, the characters "/", ";", "=", and "?" are reserved.
pchar         = unreserved | escaped | ":" | "@" | "&" | "=" | "+" | "$" | ","

   Each path segment may include a
   sequence of parameters, indicated by the semicolon ";" character.
   The parameters are not significant to the parsing of relative
   references.


JAVA URI

 * PUNCT      = ,;:$&+=
 * RESERVED   = ?/[]@ + PUNCT
 * UNRESERVED = _-!.~'()* + ALPHANUM
 * ESCAPED    = %xx

Java URLEncoder

 * SAFE       = .-*_ + ALPHANUM

See also: http://tools.ietf.org/html/rfc3986#section-3.3
     */

    @Test
    public void nullPathHasSizeZero() {

        // ARRANGE
        final Path p = new Path(null, Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(0, size);
    }

    @Test
    public void zlsPathHasSizeZero() {

        // ARRANGE
        final Path p = new Path("", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(0, size);
    }

    @Test
    public void rootPathHasSizeZero() {

        // ARRANGE
        final Path p = new Path("/", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(0, size);
    }

    @Test
    public void encodedSegmentsDecoded() {

        // ARRANGE
        final Path p = new Path("/%C2%A3/%C2%A3/%C2%A3", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(3, size);
        assertEquals("£", p.getSegment(0));
        assertEquals("£", p.getSegment(1));
        assertEquals("£", p.getSegment(2));
    }

    @Test
    public void encodedSegmentDecoded() {

        // ARRANGE
        final Path p = new Path("/foo%2Fbar%2Fbaz", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(1, size);
        assertEquals("foo/bar/baz", p.getSegment(0));
    }

    @Test
    public void singlePeriodNormalised() {

        // ARRANGE
        final Path p = new Path("/foo/./bar", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(2, size);
        assertEquals("foo", p.getSegment(0));
        assertEquals("bar", p.getSegment(1));
    }

    @Test
    public void doublePeriodNormalised() {

        // ARRANGE
        final Path p = new Path("/foo/../bar", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(1, size);
        assertEquals("bar", p.getSegment(0));
    }

    @Test
    public void normalisationSupportsEncodedPeriods() {

        // ARRANGE
        final Path p = new Path("/foo/%2E%2E/bar/%2E/baz", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(2, size);
        assertEquals("bar", p.getSegment(0));
        assertEquals("baz", p.getSegment(1));
    }

    @Test
    public void multipleDoublePeriodNormalised() {

        // ARRANGE
        final Path p = new Path("/foo/../bar/../baz", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(1, size);
        assertEquals("baz", p.getSegment(0));
    }

    @Test
    public void leadingDoublePeriodKept() {

        // ARRANGE
        final Path p = new Path("/../foo", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(2, size);
        assertEquals("..", p.getSegment(0));
        assertEquals("foo", p.getSegment(1));
    }

    @Test
    public void multipleLeadingDoublePeriodKept() {

        // ARRANGE
        final Path p = new Path("/../../foo", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(3, size);
        assertEquals("..", p.getSegment(0));
        assertEquals("..", p.getSegment(1));
        assertEquals("foo", p.getSegment(2));
    }

    @Test
    public void consecutiveDoublePeriodNormalised() {

        // ARRANGE
        final Path p = new Path("/foo/bar/../../baz", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(1, size);
        assertEquals("baz", p.getSegment(0));
    }

    @Test
    public void unencodedNormalisedSimplePath() {

        // ARRANGE
        final Path p = new Path("/foo", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(1, size);
        assertEquals("foo", p.getSegment(0));
    }

    @Test
    public void unencodedNormalisedLongPath() {

        // ARRANGE
        final Path p = new Path("/foo/bar/baz", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(3, size);
        assertEquals("foo", p.getSegment(0));
        assertEquals("bar", p.getSegment(1));
        assertEquals("baz", p.getSegment(2));
    }

    @Test
    public void emptySegmentsDiscarded() {

        // ARRANGE
        final Path p = new Path("/foo//baz", Charset.forName("UTF-8"));

        // ACT
        final int size = p.getSize();

        // ASSERT
        assertEquals(2, size);
        assertEquals("foo", p.getSegment(0));
        assertEquals("baz", p.getSegment(1));
    }
}
