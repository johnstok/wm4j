/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import wm.test.TestRequest;
import wm.test.TestResource;
import wm.test.TestResponse;


/**
 * Tests for the {@link Engine} class.
 *
 * @author Keith Webster Johnston.
 */
public class EngineTest {

    private Engine       _engine;
    private TestResponse _response;
    private TestRequest  _request;
    private static final String TARGET_URI = "http://localhost/foo";


    @Test
    public void deleteResourceCanBeEnacted() {

        // ARRANGE
        _request.setMethod(Method.DELETE);
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

            @Override public Set<String> allowed_methods() {
                return Collections.singleton(Method.DELETE);
            }

            @Override public boolean delete_resource() {
                return true;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.NO_CONTENT, _response.getStatus());
    }


    @Test
    public void deleteResourceCannotBeEnacted() {

        // ARRANGE
        _request.setMethod(Method.DELETE);
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

            @Override public Set<String> allowed_methods() {
                return Collections.singleton(Method.DELETE);
            }

            @Override public boolean delete_completed() {
                return false;
            }

            @Override public boolean delete_resource() {
                return true;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.ACCEPTED, _response.getStatus());
    }


    @Test
    public void disallowedMethodGivesNotImplemented() {

        // ARRANGE
        _request.setMethod(Method.DELETE);
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>());

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.METHOD_NOT_ALLOWED, _response.getStatus());
    }


    @Test
    public void entityTooLarge() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            null,
            new HashMap<String, Object>()) {

            @Override public boolean valid_entity_length() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.REQUEST_ENTITY_TOO_LARGE, _response.getStatus());
    }


    @Test
    public void forbidden() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            null,
            new HashMap<String, Object>()) {

            @Override public boolean forbidden() {
                return true;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.FORBIDDEN, _response.getStatus());
    }


    @Test
    public void getForNonExistentResourceGivesPreconditionFailed() {

        // ARRANGE
        _request.setHeader(Header.IF_MATCH, "*");
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

            @Override public boolean resource_exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.PRECONDITION_FAILED, _response.getStatus());
    }


    @Test
    public void getForNonExistentResourceGivesGone() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

            @Override public boolean previously_existed() {
                return true;
            }

            @Override public boolean resource_exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.GONE, _response.getStatus());
    }


    @Test
    public void getForNonExistentResourceGivesMovedPermanently() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

            @Override public URI moved_permanently() throws HttpException {
                try {
                    return new URI(TARGET_URI);
                } catch (final URISyntaxException e) {
                    throw new HttpException("Error constructing URI.", e);
                }
            }

            @Override public boolean previously_existed() {
                return true;
            }

            @Override public boolean resource_exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.MOVED_PERMANENTLY, _response.getStatus());
        Assert.assertEquals(TARGET_URI, _response.getHeader(Header.LOCATION));
    }


    @Test
    public void getForNonExistentResourceGivesMovedTemporarily() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

            @Override public URI moved_temporarily() throws HttpException {
                try {
                    return new URI(TARGET_URI);
                } catch (final URISyntaxException e) {
                    throw new HttpException("Error constructing URI.", e);
                }
            }

            @Override public boolean previously_existed() {
                return true;
            }

            @Override public boolean resource_exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.TEMPORARY_REDIRECT, _response.getStatus());
        Assert.assertEquals(TARGET_URI, _response.getHeader(Header.LOCATION));
    }


    @Test
    public void getForNonExistentResourceGivesNotFound() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public boolean resource_exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_FOUND, _response.getStatus());
    }


    @Test
    public void getResourceCanReturnOk() {

        // ARRANGE
        final Charset UTF8 = Charset.forName("UTF-8");
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

            @Override
            public Map<MediaType, BodyWriter> content_types_provided() {
                final BodyWriter bw = new BodyWriter() {
                    @Override public void write(final OutputStream outputStream) throws IOException {
                        outputStream.write("Hello, world!".getBytes(UTF8));
                    }
                };

                return Collections.singletonMap(MediaType.ANY,  bw);
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF8));
    }


    @Test
    public void malformed() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            null,
            new HashMap<String, Object>()) {

                @Override public boolean malformed_request() {
                    return true;
                }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.BAD_REQUEST, _response.getStatus());
    }


    @Test
    public void optionsRequested() {

        // ARRANGE
        _request.setMethod(Method.OPTIONS);
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>());

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals("0", _response.getHeader(Header.CONTENT_LENGTH));
        Assert.assertEquals(
            Utils.join(resource.allowed_methods(), ',').toString(),
            _response.getHeader(Header.ALLOW));
    }


    @Test
    public void postResourceCanReturnOk() {

        // ARRANGE
        _request.setMethod(Method.POST);
        _response.setBody(new byte[] {0});
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

                @Override public Set<String> allowed_methods() {
                    return Collections.singleton(Method.POST);
                }

                @Override public boolean post_is_create() {
                    return false;
                }

                @Override public void process_post() {
                    // No Op.
                }

                /** {@inheritDoc} */
                @Override
                public Map<MediaType, BodyWriter> content_types_provided() {
                    return Collections.<MediaType, BodyWriter>singletonMap(MediaType.ANY, new BodyWriter() {
                        @Override
                        public void write(final OutputStream outputStream) {
                            // NO OP.
                        }
                    });
                }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
    }


    @Test
    public void putForMissingResourceCanBeRedirected() {

        // ARRANGE
        _request.setMethod(Method.PUT);
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

            @Override public Set<String> allowed_methods() {
                return Collections.singleton(Method.PUT);
            }

            @Override public URI moved_permanently() throws HttpException {
                try {
                    return new URI(TARGET_URI);
                } catch (final URISyntaxException e) {
                    throw new HttpException("Error constructing URI.", e);
                }
            }

            @Override public boolean resource_exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.MOVED_PERMANENTLY, _response.getStatus());
        Assert.assertEquals(TARGET_URI, _response.getHeader(Header.LOCATION));
    }


    @Test
    public void putForMissingResourceCanCauseConflict() {

        // ARRANGE
        _request.setMethod(Method.PUT);
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

                @Override public Set<String> allowed_methods() {
                    return Collections.singleton(Method.PUT);
                }

                @Override
                public boolean is_conflict() {
                    return true;
                }

                @Override public boolean resource_exists() {
                    return false;
                }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.CONFLICT, _response.getStatus());
    }


    @Test
    public void putForMissingResourceCreatesResource() throws Exception {

        // ARRANGE
        _request.setMethod(Method.PUT);
        _response.setHeader(Header.LOCATION, "http://iamjohnstok.com/"); // FIXME: How does this get set by the resource?
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>()) {

            @Override public Set<String> allowed_methods() {
                return Collections.singleton(Method.PUT);
            }

            @Override public boolean resource_exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.CREATED, _response.getStatus());
    }


    @Test
    public void serviceUnavailable() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            null,
            new HashMap<String, Object>()) {

                @Override public boolean service_available() {
                    return false;
                }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.SERVICE_UNAVAILABLE, _response.getStatus());
    }


    /**
     * TODO: Add a description for this method.
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _request  = new TestRequest();
        _response = new TestResponse();
        _engine   = new Engine();
    }


    /**
     * TODO: Add a description for this method.
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        _engine   = null;
        _response = null;
        _request  = null;
    }

    @Test
    public void unacceptableRequestCharsetGivesNotAcceptable() {

        // ARRANGE
        _request.setHeader(Header.ACCEPT_CHARSET, Charset.defaultCharset().name());
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>());

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_ACCEPTABLE, _response.getStatus());
    }

    @Test
    public void unacceptableRequestEncodingGivesNotAcceptable() {

        // ARRANGE
        _request.setHeader(Header.ACCEPT_ENCODING, ContentEncoding.GZIP);
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>());

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_ACCEPTABLE, _response.getStatus());
    }


    @Test
    public void unacceptableRequestLanguageGivesNotAcceptable() {

        // ARRANGE
        _request.setHeader(Header.ACCEPT_LANGUAGE, Locale.UK.toString());
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>());

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_ACCEPTABLE, _response.getStatus());
    }


    @Test
    public void unacceptableRequestMediaTypeGivesNotAcceptable() {

        // ARRANGE
        _request.setHeader(Header.ACCEPT, "text/html"); // TODO: Add MediaType class.
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>());

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_ACCEPTABLE, _response.getStatus());
    }


    @Test
    public void unauthorized() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            null,
            new HashMap<String, Object>()) {

                @Override public boolean is_authorized() {
                    return false;
                }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.UNAUTHORIZED, _response.getStatus());
    }


    @Test
    public void unimplementedContentHeader() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            null,
            new HashMap<String, Object>()) {

            @Override public boolean valid_content_headers() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_IMPLEMENTED, _response.getStatus());
    }


    @Test
    public void unknownContentType() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            null,
            new HashMap<String, Object>()) {

            @Override public boolean known_content_type() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.UNSUPPORTED_MEDIA_TYPE, _response.getStatus());
    }


    @Test
    public void unknownMethodGivesNotImplemented() {

        // ARRANGE
        _request.setMethod("FOO");                                 //$NON-NLS-1$
        final Resource resource = new TestResource(
            new Properties(),
            _request,
            new HashMap<String, Object>());

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_IMPLEMENTED, _response.getStatus());
    }


    @Test
    public void uriTooLong() {

        // ARRANGE
        final Resource resource = new TestResource(
            new Properties(),
            null,
            new HashMap<String, Object>()) {

                @Override public boolean uri_too_long() {
                    return true;
                }
        };

        // ACT
        _engine.process(resource, _response);

        // ASSERT
        Assert.assertSame(Status.REQUEST_URI_TOO_LONG, _response.getStatus());
    }
}
