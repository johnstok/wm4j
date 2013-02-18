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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import com.johnstok.http.engine.BodyWriter;
import com.johnstok.http.engine.Utils;
import com.johnstok.http.sync.Handler;
import com.johnstok.http.sync.Request;
import com.johnstok.http.sync.Response;


/**
 * Echo's the request line for debug purposes.
 *
 * @author Keith Webster Johnston.
 */
public class EchoHandler
    implements
        Handler {

    /** {@inheritDoc} */
    @Override
    public void handle(final Request request,
                       final Response response) throws IOException {
        new BodyWriter() {
            @Override
            public void write(final OutputStream outputStream) throws IOException {
                OutputStreamWriter w =
                    new OutputStreamWriter(outputStream, "UTF-8");
                w.write(request.getVersion()+" "+request.getMethod()+" "+request.getRequestUri());
                w.write('\n');
                w.write('\n');
                for (Map.Entry<String, List<String>> h : request.getHeaders().entrySet()) {
                    w.write(h.getKey());
                    w.write(": ");
                    w.write(Utils.join(h.getValue(), ',').toString());
                    w.write('\n');
                }
                w.flush();
            }
        }.write(response.getBody());
    }
}
