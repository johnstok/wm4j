/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
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
