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

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import com.johnstok.http.ContentCoding;
import com.johnstok.http.RequestURI;
import com.johnstok.http.Status;
import com.johnstok.http.engine.Utils;
import com.johnstok.http.sync.Handler;
import com.johnstok.http.sync.Request;
import com.johnstok.http.sync.Response;


/**
 * A handler that serves resources from the classpath.
 *
 * @author Keith Webster Johnston.
 */
public class ClasspathHandler
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


    public ClasspathHandler() {
        supported.add(ContentCoding.GZIP.toString());
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final Request request,
                       final Response response) throws IOException {

        // TODO: Ideally we would call URI#resolve() rather than string manip.
        final String path =
            RequestURI.parse(request.getRequestUri()).toUri().getRawPath();
        final String resourcePath = "/META-INF/resources"+path;
        URL resource = Utils.getResource(resourcePath); // FIXME: Use version that requests all path matches and warn of duplicates.

        if (null==resource) {
            response.setStatus(Status.NOT_FOUND.getCode(), Status.NOT_FOUND.getReasonPhrase());
        } else {
            new ResourceBodyWriter(resource).write(response.getBody());
        }
    }
}
