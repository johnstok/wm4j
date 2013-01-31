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
package com.johnstok.http.netty;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

/**
 * Wraps a Netty Channel in an OutputStream.
 *
 * TODO: Replace with official version:
 * https://issues.jboss.org/browse/NETTY-307
 *
 * @author Keith Webster Johnston.
 */
public final class ChannelOutputStream
    extends
        OutputStream {

    private final Channel _channel;

    /**
     * Constructor.
     *
     * @param channel The channel to wrap.
     */
    public ChannelOutputStream(final Channel channel) {
        _channel = channel; // FIXME: Test for null.
    }


    /** {@inheritDoc} */
    @Override
    public void write(final int b) throws IOException {
        _channel.write(ChannelBuffers.copiedBuffer(new byte[] {(byte) b}));
    }


    /** {@inheritDoc} */
    @Override
    public void write(final byte[] b, final int off, final int len)
        throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                   ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        _channel.write(
            ChannelBuffers.copiedBuffer(ByteBuffer.wrap(b, off, len)));
    }
}