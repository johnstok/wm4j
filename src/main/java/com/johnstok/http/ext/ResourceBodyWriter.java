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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import com.johnstok.http.engine.BodyWriter;
import com.johnstok.http.engine.Utils;

/**
 * A body writer that reads from a URL.
 *
 * @author Keith Webster Johnston.
 */
public final class ResourceBodyWriter
    implements
        BodyWriter {

    private final URL _resourcePath;


    /**
     * Constructor.
     *
     * @param resource
     */
    ResourceBodyWriter(final URL resource) {
        _resourcePath = resource;
    }


    @Override
    public void write(final OutputStream os) throws IOException {
        InputStream is = _resourcePath.openStream();
        try {
            Utils.copy(is, os);
        } finally {
            Utils.close(os);
            Utils.close(is);
        }
    }
}