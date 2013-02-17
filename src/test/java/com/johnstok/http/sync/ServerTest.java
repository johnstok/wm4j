/*-----------------------------------------------------------------------------
 * Copyright © 2013 Keith Webster Johnston.
 * All rights reserved.
 *
 * This file is part of wm4j.
 *
 * wm4j is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * wm4j is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with wm4j. If not, see <http://www.gnu.org/licenses/>.
 *---------------------------------------------------------------------------*/
package com.johnstok.http.sync;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import com.johnstok.http.client.SynchronousClient;
import com.johnstok.http.ext.HelloWorldHandler;


/**
 * Abstract class for server tests.
 *
 * @author Keith Webster Johnston.
 */
public abstract class ServerTest<T extends Server> {

    protected static final InetSocketAddress LOCALHOST =
        new InetSocketAddress("localhost", 4444);                  //$NON-NLS-1$
    protected T _server;


    @Test
    public void getRequest() throws IOException {

        // ARRANGE
        Server s = createServer(new HelloWorldHandler());
        s.startup(LOCALHOST);

        // ACT
        String body = SynchronousClient.get("/");

        // ASSERT
        Assert.assertEquals("Hello World!", body);

    }


    /** Tear down. */
    @After
    public void tearDown() {
        if (null!=_server) {
            try {
                _server.shutdown();
            } catch (IOException e) {
                System.err.println("Error stopping server: "+e);   //$NON-NLS-1$
            }
        }
    }


    /**
     * Create a default server object.
     *
     * @return The  server.
     */
    protected abstract T createServer(Handler handler);
}
