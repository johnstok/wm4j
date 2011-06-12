package wm;


public interface Dispatcher {

    Resource dispatch(Request request) throws HttpException;
}
