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

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Test;
import wm.multipart.ByteRanges;



/**
 * Tests for the {@link ByteRanges} class.
 *
 * @author Keith Webster Johnston.
 */
public class ByteRangesTest {

    /**
     * Test.
     *
     * @throws Exception If the test fails.
     */
    @Test
    public void testMimeTypeGeneratedCorrectly() throws Exception {

        // ACT
        final File f =
            new File("src/test/resources/iaj/web/acceptance/index.html");
        final ByteRanges ranges = new ByteRanges(f);

        assertEquals(
            "multipart/byteranges; boundary="
                + ranges.getBoundary()
                + "; charset=UTF-8",
            ranges.getMimeType());
    }
}
