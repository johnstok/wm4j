package wm;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class UnaryDispatchary<T>
    implements
        Dispatcher {

    private static final String FAILED_TO_CREATE_RESOURCE =
        "Failed to create resource.";                              //$NON-NLS-1$


    private final Class<? extends Resource> _clazz;
    private final T                         _configuration;


    /**
     * Constructor.
     *
     * @param clazz The resource class that will handle requests.
     */
    public UnaryDispatchary(final Class<? extends Resource> clazz,
                            final T configuration) {
        _clazz = clazz; // FIXME: Check for NULL. Other checks too?
        _configuration = configuration; // FIXME: Check for NULL. Other checks too?
    }


    /** {@inheritDoc} */
    @Override
    public Resource dispatch(final Request request) throws HttpException {
        try {
            Map<String, Object> context = new HashMap<String, Object>();
            return _clazz.getConstructor(_configuration.getClass(), Request.class, Map.class)
                         .newInstance(_configuration, request, context);
        } catch (InstantiationException e) {
            throw new HttpException(FAILED_TO_CREATE_RESOURCE, e);
        } catch (IllegalAccessException e) {
            throw new HttpException(FAILED_TO_CREATE_RESOURCE, e);
        } catch (InvocationTargetException e) {
            throw new HttpException(FAILED_TO_CREATE_RESOURCE, e);
        } catch (NoSuchMethodException e) {
            throw new HttpException(FAILED_TO_CREATE_RESOURCE, e);
        } catch (RuntimeException e) {
            throw new HttpException(FAILED_TO_CREATE_RESOURCE, e);
        }
    }
}
