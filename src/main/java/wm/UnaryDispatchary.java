package wm;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class UnaryDispatchary
    implements
        Dispatcher {

    private static final String FAILED_TO_CREATE_RESOURCE =
        "Failed to create resource.";                              //$NON-NLS-1$


    private final Class<? extends Resource> _clazz;
    private final Properties                _properties;


    /**
     * Constructor.
     *
     * @param clazz The resource class that will handle requests.
     */
    public UnaryDispatchary(final Class<? extends Resource> clazz,
                            final Properties properties) {
        _clazz = clazz; // FIXME: Check for NULL. Other checks too?
        _properties = properties; // FIXME: Make defensive copy. Check for NULL. Other checks too?
    }


    /** {@inheritDoc} */
    @Override
    public Resource dispatch(final Request request) throws HttpException {
        try {
            Map<String, Object> context = new HashMap<String, Object>();
            return _clazz.getConstructor(Properties.class, Request.class, Map.class)
                         .newInstance(_properties, request, context); // TODO: Defensive copy _properties
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
