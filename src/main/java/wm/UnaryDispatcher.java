package wm;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class UnaryDispatcher<T>
    implements
        Dispatcher {


    private final Class<? extends Resource> _clazz;
    private final T                         _configuration;


    /**
     * Constructor.
     *
     * @param clazz The resource class that will handle requests.
     */
    public UnaryDispatcher(final Class<? extends Resource> clazz,
                            final T configuration) {
        _clazz = clazz; // FIXME: Check for NULL. Other checks too?
        _configuration = configuration; // FIXME: Check for NULL. Other checks too?
    }


    /** {@inheritDoc} */
    @Override
    public Resource dispatch(final Request request,
                             final Response response) throws HttpException {
        try {
            final Map<String, Object> context = new HashMap<String, Object>();
            return _clazz.getConstructor(_configuration.getClass(), Request.class, Response.class, Map.class)
                         .newInstance(_configuration, request, response, context);
        } catch (final InstantiationException e) {
            throw new ServerHttpException(Status.INTERNAL_SERVER_ERROR, e);
        } catch (final IllegalAccessException e) {
            throw new ServerHttpException(Status.INTERNAL_SERVER_ERROR, e);
        } catch (final InvocationTargetException e) {
            throw new ServerHttpException(Status.INTERNAL_SERVER_ERROR, e);
        } catch (final NoSuchMethodException e) {
            throw new ServerHttpException(Status.INTERNAL_SERVER_ERROR, e);
        } catch (final RuntimeException e) {
            throw new ServerHttpException(Status.INTERNAL_SERVER_ERROR, e);
        }
    }
}
