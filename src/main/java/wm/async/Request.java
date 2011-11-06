/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *---------------------------------------------------------------------------*/
package wm.async;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;


/**
 * Event Handler for a HTTP request.
 *
 * @author Keith Webster Johnston.
 */
public interface Request {

    void onRequestLine(String requestLine);

    void onHeaders(Map<String, List<String>> headers);

    void onBodyData(ByteBuffer bytes);

    void onEnd(Map<String, List<String>> trailers);
}
