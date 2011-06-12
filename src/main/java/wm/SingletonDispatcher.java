/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;


/**
 * A dispatcher that sends all requests to a single resource.
 *
 * @author Keith Webster Johnston.
 */
public class SingletonDispatcher
    implements
        Dispatcher {

    private final Resource _resource;


    public SingletonDispatcher(final Resource resource) {
        _resource = resource;
    }


    /** {@inheritDoc} */
    @Override
    public Resource dispatch(final Request request) throws HttpException {
        return _resource;
    }
}
