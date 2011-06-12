package wm;


public class UnaryDispatchary
    implements
        Dispatcher {

    private static final String FAILED_TO_CREATE_RESOURCE =
        "Failed to create resource.";                              //$NON-NLS-1$


    private final Class<? extends Resource> _clazz;


    /**
     * Constructor.
     *
     * @param clazz The resource class that will handle requests.
     */
    private UnaryDispatchary(final Class<? extends Resource> clazz) {
        _clazz = clazz; // FIXME: Check for NULL. Other checks too?
    }


    /** {@inheritDoc} */
    @Override
    public Resource dispatch(final Request request) throws HttpException {
        try {
            return _clazz.newInstance();
        } catch (InstantiationException e) {
            throw new HttpException(FAILED_TO_CREATE_RESOURCE, e);
        } catch (IllegalAccessException e) {
            throw new HttpException(FAILED_TO_CREATE_RESOURCE, e);
        } catch (RuntimeException e) {
            throw new HttpException(FAILED_TO_CREATE_RESOURCE, e);
        }
    }
}
