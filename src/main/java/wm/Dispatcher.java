package wm;


/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public interface Dispatcher {

    /**
     * TODO: Add a description for this method.
     *
     * @param request
     * @param response
     *
     * @return
     *
     * @throws HttpException
     */
    Resource dispatch(Request request, Response response) throws HttpException;
}
