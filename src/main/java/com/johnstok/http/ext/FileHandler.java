/*-----------------------------------------------------------------------------
 * Copyright © 2013 Keith Webster Johnston.
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
package com.johnstok.http.ext;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import com.johnstok.http.ContentCoding;
import com.johnstok.http.Header;
import com.johnstok.http.Status;
import com.johnstok.http.WeightedValue;
import com.johnstok.http.engine.Utils;
import com.johnstok.http.negotiation.ContentNegotiator;
import com.johnstok.http.sync.BodyWriter;
import com.johnstok.http.sync.Handler;
import com.johnstok.http.sync.Request;
import com.johnstok.http.sync.Response;


/**
 * A handler that serves files from a Jar.
 *
 * @author Keith Webster Johnston.
 */
public class FileHandler
    implements
        Handler {

    /*
     * TODO
     * ====
     *  - etags
     *  - cache-control
     *  - compilation (LESS, CoffeScript, Google Closure Compiler)
     */

    final Set<String> supported = new HashSet<String>();


    public FileHandler() {
        supported.add(ContentCoding.GZIP.toString());
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final Request request, final Response response) {

        response.addVariance(Header.CONTENT_ENCODING);
        final List<WeightedValue> clientEncodings =
            Header.parseAcceptEncoding(
                request.getHeader(Header.ACCEPT_ENCODING));
        ContentNegotiator cn = new ContentNegotiator(supported);
        final ContentCoding cc = cn.select(clientEncodings);

        try {
            response.write(
                    new BodyWriter() {
                    @Override
                    public void write(OutputStream os) throws IOException {

                        final String path = request.getPath().toString();
                        String resourcePath = "/META-INF/resources"+path;
                        InputStream is = getClass().getResourceAsStream(resourcePath); // TODO: Are paths always absolute?

                        if (null==is) {
                            response.setStatus(Status.NOT_FOUND);
                        } else {
                            try {
                                if (ContentCoding.GZIP.equals(cc)) {
                                    response.setContentEncoding(cc.toString());
                                    os = new GZIPOutputStream(new BufferedOutputStream(os, 256)); // GZip OS, writes a header that commits the response – work around by buffering.
                                }
                                response.setHeader(
                                    Header.VARY,
                                    Utils.join(Arrays.asList(response.getVariances()), ',').toString());
                                Utils.copy(is, os);
                            } finally {
                                os.close(); // TODO: Surround with try / catch.
                                is.close(); // TODO: Surround with try / catch.
                            }
                        }
                    }
                }
            );
        } catch (final IOException e) {
            e.printStackTrace(); // FIXME.
        }
    }
}
