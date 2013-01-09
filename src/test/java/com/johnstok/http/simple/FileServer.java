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
package com.johnstok.http.simple;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.regex.Pattern;
import com.johnstok.http.ext.DispatchingHandler;
import com.johnstok.http.ext.EchoHandler;
import com.johnstok.http.ext.FileHandler;
import com.johnstok.http.sync.Handler;


/**
 * A test file server.
 *
 * @author Keith Webster Johnston.
 */
public class FileServer {


    /**
     * Application entry point.
     *
     * @param args Command line arguments.
     *
     * @throws IOException If the server fails to start.
     */
    public static void main(final String[] args) throws IOException {

        HashMap<Pattern, Handler> handlers = new HashMap<Pattern, Handler>();
        handlers.put(Pattern.compile("/static/.*"), new FileHandler());
        handlers.put(Pattern.compile("/echo/.*"), new EchoHandler());

        Handler dispatcher = new DispatchingHandler(handlers);

        SimpleDaemon webserver = new SimpleDaemon(dispatcher);
        webserver.startup(new InetSocketAddress(8080));
    }
}
