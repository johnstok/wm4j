package wm;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * {@link Dispatcher} implementation that selects a resource type by regex.
 *
 * This implementation matches patterns based on the order that they were bound.
 * Regular expressions match against the encoded, non-normalised path from the
 * request URI.
 *
 * @param <T> The type of configuration object passed to the resource.
 *
 * @author Keith Webster Johnston.
 */
public class RegexDispatcher<T>
    implements
        Dispatcher {

    private final Map<Pattern, Class<? extends Resource>> _bindings;
    private final T                                       _configuration;


    /**
     * Constructor.
     *
     * @param clazz The resource class that will handle requests.
     */
    public RegexDispatcher(final T configuration) {
        _bindings      = new LinkedHashMap<Pattern, Class<? extends Resource>>();
        _configuration = configuration; // FIXME: Check for NULL.
    }


    /** {@inheritDoc} */
    @Override
    public Resource dispatch(final Request request,
                             final Response response) throws HttpException {

            final String encodedPath = request.getRequestUri().getRawPath();

            final Class<? extends Resource> clazz = selectResourceBinding(encodedPath);
            if (null==clazz) {
                throw new ClientHttpException(Status.NOT_FOUND);
            }

            return constructResource(request, response, clazz);
    }


    /**
     * Bind a resource type to a regular expression.
     *
     * @param regex        The matching regular expression.
     * @param resourceType The type of resource dispatched to.
     */
    public void bind(final String regex, final Class<? extends Resource> resourceType) {
        // FIXME: Check param's for null.
        _bindings.put(Pattern.compile(regex), resourceType);
    }


    private Resource constructResource(final Request request,
                                       final Response response,
                                       final Class<? extends Resource> clazz) {
        try {
            final Map<String, Object> context = new HashMap<String, Object>();
            return clazz.getConstructor(Map.class).newInstance(context);
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


    private Class<? extends Resource> selectResourceBinding(final String encodedPath) {
        for (final Map.Entry<Pattern, Class<? extends Resource>> binding : _bindings.entrySet()) {
            final Matcher m = binding.getKey().matcher(encodedPath);
            if (m.matches()) { return binding.getValue(); }
        }
        return null;
    }
}
