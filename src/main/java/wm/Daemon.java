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
 * along with wm4j. If not, see <http://www.gnu.org/licenses/>.
 *---------------------------------------------------------------------------*/
package wm;

import java.io.IOException;
import java.net.InetSocketAddress;


/**
 * Manages the life-cycle of a network daemon.
 *
 * @author Keith Webster Johnston.
 */
public interface Daemon {

    /**
     * Start the daemon.
     *
     * @param address The network address on which the daemon should listen.
     *
     * @throws IOException If connection to the specified address fails.
     */
    void startup(InetSocketAddress address) throws IOException; // TODO: Use a library specific exception.

    /**
     * Stop the daemon.
     *
     * @throws IOException If disconnection from the network address fails.
     */
    void shutdown() throws IOException; // TODO: Use a library specific exception.
}
