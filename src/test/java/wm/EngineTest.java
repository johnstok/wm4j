/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import wm.test.TestRequest;
import wm.test.TestResource;
import wm.test.TestResponse;
import com.johnstok.http.ContentEncoding;
import com.johnstok.http.ETag;
import com.johnstok.http.Header;
import com.johnstok.http.HttpException;
import com.johnstok.http.LanguageTag;
import com.johnstok.http.MediaType;
import com.johnstok.http.Method;
import com.johnstok.http.ServerHttpException;
import com.johnstok.http.Status;
import com.johnstok.http.headers.DateHeader;
import com.johnstok.http.sync.BodyReader;
import com.johnstok.http.sync.BodyWriter;
import com.johnstok.http.sync.Response;


/**
 * Tests for the {@link Engine} class.
 *
 * @author Keith Webster Johnston.
 */
public class EngineTest {


    /* MISSING TESTS
     * =============
     *
     *  + All date comparisons should occur at millisecond precision.
     *  + Multiple values for conditional request headers.
     *  + Comma separated values for conditional request headers.
     *
     * ===========*/


    public class ByteArrayBodyWriter
        implements
            BodyWriter {

        private final byte[] _body;


        /**
         * Constructor.
         *
         * @param body The body to write.
         */
        public ByteArrayBodyWriter(final byte[] body) {
            _body = body;
        }


        /** {@inheritDoc} */
        @Override
        public void write(final OutputStream outputStream) throws IOException {
            outputStream.write(_body);
        }
    }


    private static final class SimpleBodyReader
        implements
            BodyReader {

        private final ByteArrayOutputStream _baos;
        private final String                _createdPath;
        private final Resource              _resource;
        private final Response              _response;


        /**
         * Constructor.
         *
         * @param baos
         * @param createdPath
         * @param resource
         * @param response
         */
        SimpleBodyReader(final ByteArrayOutputStream baos,
                         final String createdPath,
                         final Resource resource,
                         final Response response) {
            _baos = baos;
            _createdPath = createdPath;
            _resource = resource;
            _response = response;
        }


        @Override
        public void read(final InputStream inputStream) throws IOException, HttpException {
            if (!_resource.exists()) {
                _response.setHeader(Header.LOCATION, _createdPath);
            }

            Utils.copy(inputStream, _baos);
        }
    }


    private static final class HelloWorldWriter
        implements
            BodyWriter {

        private final Charset _charset;


        /**
         * Constructor.
         *
         * @param charset
         */
        HelloWorldWriter(final Charset charset) {
            _charset = charset;
        }


        @Override public void write(final OutputStream outputStream) throws IOException {
            outputStream.write("Hello, world!".getBytes(_charset));
        }
    }


    private Engine       _engine;
    private TestResponse _response;
    private TestRequest  _request;
    private static final String TARGET_URI = "http://localhost/foo";
    static final Charset UTF_8 = Charset.forName("UTF-8");
    static final Charset UTF_16 = Charset.forName("UTF-16");


    @Test
    public void deleteResourceCanBeEnacted() {

        // ARRANGE
        _request.setMethod(Method.DELETE);
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public Set<Method> getAllowedMethods() {
                return Collections.singleton(Method.DELETE);
            }

            @Override public boolean delete() {
                return true;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NO_CONTENT, _response.getStatus());
    }


    @Test
    public void deleteResourceCannotBeEnacted() {

        // ARRANGE
        _request.setMethod(Method.DELETE);
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public Set<Method> getAllowedMethods() {
                return Collections.singleton(Method.DELETE);
            }

            @Override public boolean isDeleted() {
                return false;
            }

            @Override public boolean delete() {
                return true;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.ACCEPTED, _response.getStatus());
    }


    @Test
    public void disallowedMethodGivesNotImplemented() {

        // ARRANGE
        _request.setMethod(Method.DELETE);
        final Resource resource = new TestResource(
            new HashMap<String, Object>());

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.METHOD_NOT_ALLOWED, _response.getStatus());
    }


    @Test
    public void entityTooLarge() {

        // ARRANGE
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public boolean isEntityLengthValid() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.REQUEST_ENTITY_TOO_LARGE, _response.getStatus());
    }


    @Test
    public void forbidden() {

        // ARRANGE
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public boolean isForbidden() {
                return true;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.FORBIDDEN, _response.getStatus());
    }


    @Test
    public void deleteWithWildcardIfnonematchGivesPreconditionFailed() { // I13, J18

        // ARRANGE
        _request.setHeader(Header.IF_NONE_MATCH, "*");
        _request.setMethod(Method.DELETE);
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public Set<Method> getAllowedMethods() {
                return Collections.singleton(Method.DELETE);
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.PRECONDITION_FAILED, _response.getStatus());
    }



    @Test
    public void deleteWithMatchedIfnonematchGivesPreconditionFailed() { // K13, J18

        // ARRANGE
        _request.setHeader(Header.IF_NONE_MATCH, "foo"); // FIXME: This value should be quoted - engine needs to parse as an entity-tag.
        _request.setMethod(Method.DELETE);
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public Set<Method> getAllowedMethods() {
                return Collections.singleton(Method.DELETE);
            }

            /** {@inheritDoc} */
            @Override
            public ETag generateEtag(final String base) { return ETag.parse("\"foo\""); }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.PRECONDITION_FAILED, _response.getStatus());
    }



    @Test
    public void getWithMatchedIfnonematchGivesNotModified() { // K13, J18

        // ARRANGE
        _request.setHeader(Header.IF_NONE_MATCH, "foo");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public ETag generateEtag(final String base) { return ETag.parse("\"foo\""); }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_MODIFIED, _response.getStatus());
    }


    @Test
    public void getWithLessRecentIfunmodifiedsinceGivesPreconditionFailed() { // H12

        // ARRANGE
        _request.setHeader(Header.IF_UNMODIFIED_SINCE, DateHeader.format(new Date(0)));
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public Date getLastModifiedDate() { return new Date(); }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.PRECONDITION_FAILED, _response.getStatus());
    }


    @Test
    public void getWithUnmatchedIfmatchGivesPreconditionFailed() { // G11

        // ARRANGE
        _request.setHeader(Header.IF_MATCH, "foo");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public ETag generateEtag(final String base) { return ETag.parse("\"bar\""); }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.PRECONDITION_FAILED, _response.getStatus());
    }


    @Test
    public void getWithFutureIfmodifiedsinceReturnsOk() { // L15

        // ARRANGE
        final Date d = new Date(Long.MAX_VALUE);
        _request.setHeader(Header.IF_MODIFIED_SINCE, DateHeader.format(d));
        final Charset UTF8 = Charset.forName("UTF-8");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public Date getLastModifiedDate() { return new Date(0); }

            /** {@inheritDoc} */
            @Override
            public BodyWriter getWriter(final MediaType mediaType) {
                final BodyWriter bw = new BodyWriter() {
                    @Override public void write(final OutputStream outputStream) throws IOException {
                        outputStream.write("Hello, world!".getBytes(UTF8));
                    }
                };
                return bw;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF8));
    }


    @Test
    public void getWithLessRecentIfmodifiedsinceReturnsOk() { // L17

        // ARRANGE
        final Date d = new Date(0);
        _request.setHeader(Header.IF_MODIFIED_SINCE, DateHeader.format(d));
        final Charset UTF8 = Charset.forName("UTF-8");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public Date getLastModifiedDate() { return new Date(2000); }

            /** {@inheritDoc} */
            @Override
            public BodyWriter getWriter(final MediaType mediaType) {
                final BodyWriter bw = new BodyWriter() {
                    @Override public void write(final OutputStream outputStream) throws IOException {
                        outputStream.write("Hello, world!".getBytes(UTF8));
                    }
                };
                return bw;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF8));
    }


    @Test
    public void getWithSameIfmodifiedsinceReturnsNotModified() { // L17

        // ARRANGE
        final Date d = new Date(1000);
        _request.setHeader(Header.IF_MODIFIED_SINCE, DateHeader.format(d));
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public Date getLastModifiedDate() { return d; }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_MODIFIED, _response.getStatus());
    }


    @Test
    public void getWithMoreRecentIfmodifiedsinceReturnsNotModified() { // L17

        // ARRANGE
        final Date d = new Date(2000);
        _request.setHeader(Header.IF_MODIFIED_SINCE, DateHeader.format(d));
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public Date getLastModifiedDate() { return new Date(0); }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_MODIFIED, _response.getStatus());
    }


    @Test
    public void getWithMoreRecentIfunmodifiedsinceReturnsOk() { // H12

        // ARRANGE
        final Date d = new Date();
        _request.setHeader(Header.IF_UNMODIFIED_SINCE, DateHeader.format(d));
        final Charset UTF8 = Charset.forName("UTF-8");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public Date getLastModifiedDate() { return new Date(0); }

            /** {@inheritDoc} */
            @Override
            public BodyWriter getWriter(final MediaType mediaType) {
                final BodyWriter bw = new BodyWriter() {
                    @Override public void write(final OutputStream outputStream) throws IOException {
                        outputStream.write("Hello, world!".getBytes(UTF8));
                    }
                };
                return bw;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF8));
    }


    @Test
    public void getWithSameIfunmodifiedsinceReturnsOk() { // H12

        // ARRANGE
        final Date d = new Date(1000); // HTTP uses millisecond precision ;-)
        _request.setHeader(Header.IF_UNMODIFIED_SINCE, DateHeader.format(d));
        final Charset UTF8 = Charset.forName("UTF-8");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public Date getLastModifiedDate() { return d; }

            /** {@inheritDoc} */
            @Override
            public BodyWriter getWriter(final MediaType mediaType) {
                final BodyWriter bw = new BodyWriter() {
                    @Override public void write(final OutputStream outputStream) throws IOException {
                        outputStream.write("Hello, world!".getBytes(UTF8));
                    }
                };
                return bw;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF8));
    }


    @Test
    public void getWithInvalidIfmodifiedsinceReturnsOk() { // L14

        // ARRANGE
        _request.setHeader(Header.IF_MODIFIED_SINCE, "foo");
        final Charset UTF8 = Charset.forName("UTF-8");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public BodyWriter getWriter(final MediaType mediaType) {
                final BodyWriter bw = new BodyWriter() {
                    @Override public void write(final OutputStream outputStream) throws IOException {
                        outputStream.write("Hello, world!".getBytes(UTF8));
                    }
                };
                return bw;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF8));
    }


    @Test
    public void getWithInvalidIfunmodifiedsinceReturnsOk() { // H11

        // ARRANGE
        _request.setHeader(Header.IF_UNMODIFIED_SINCE, "foo");
        final Charset UTF8 = Charset.forName("UTF-8");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public BodyWriter getWriter(final MediaType mediaType) {
                final BodyWriter bw = new BodyWriter() {
                    @Override public void write(final OutputStream outputStream) throws IOException {
                        outputStream.write("Hello, world!".getBytes(UTF8));
                    }
                };
                return bw;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF8));
    }


    @Test
    public void getWithMatchedIfmatchReturnsOk() { // G11

        // ARRANGE
        _request.setHeader(Header.IF_MATCH, "foo");
        final Charset UTF8 = Charset.forName("UTF-8");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public ETag generateEtag(final String base) { return ETag.parse("\"foo\""); }

            /** {@inheritDoc} */
            @Override
            public BodyWriter getWriter(final MediaType mediaType) {
                final BodyWriter bw = new BodyWriter() {
                    @Override public void write(final OutputStream outputStream) throws IOException {
                        outputStream.write("Hello, world!".getBytes(UTF8));
                    }
                };
                return bw;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF8));
    }


    @Test
    public void getWithWildcardIfmatchReturnsOk() { // G09

        // ARRANGE
        _request.setHeader(Header.IF_MATCH, "*");
        final Charset UTF8 = Charset.forName("UTF-8");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public ETag generateEtag(final String base) { return ETag.parse("\"bar\""); }

            /** {@inheritDoc} */
            @Override
            public BodyWriter getWriter(final MediaType mediaType) {
                final BodyWriter bw = new BodyWriter() {
                    @Override public void write(final OutputStream outputStream) throws IOException {
                        outputStream.write("Hello, world!".getBytes(UTF8));
                    }
                };
                return bw;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF8));
    }


    @Test
    public void getForNonExistentResourceGivesPreconditionFailed() {

        // ARRANGE
        _request.setHeader(Header.IF_MATCH, "*");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public boolean exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.PRECONDITION_FAILED, _response.getStatus());
    }


    @Test
    public void getForNonExistentResourceGivesGone() {

        // ARRANGE
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public boolean existedPreviously() {
                return true;
            }

            @Override public boolean exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.GONE, _response.getStatus());
    }


    @Test
    public void getForNonExistentResourceGivesMovedPermanently() {

        // ARRANGE
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public URI movedPermanentlyTo() {
                try {
                    return new URI(TARGET_URI);
                } catch (final URISyntaxException e) {
                    throw new ServerHttpException(
                        Status.INTERNAL_SERVER_ERROR, e);
                }
            }

            @Override public boolean existedPreviously() {
                return true;
            }

            @Override public boolean exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.MOVED_PERMANENTLY, _response.getStatus());
        Assert.assertEquals(TARGET_URI, _response.getHeader(Header.LOCATION));
    }


    @Test
    public void getForNonExistentResourceGivesMovedTemporarily() {

        // ARRANGE
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public URI movedTemporarilyTo() {
                try {
                    return new URI(TARGET_URI);
                } catch (final URISyntaxException e) {
                    throw new ServerHttpException(
                        Status.INTERNAL_SERVER_ERROR, e);
                }
            }

            @Override public boolean existedPreviously() {
                return true;
            }

            @Override public boolean exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.TEMPORARY_REDIRECT, _response.getStatus());
        Assert.assertEquals(TARGET_URI, _response.getHeader(Header.LOCATION));
    }


    @Test
    public void getForNonExistentResourceGivesNotFound() {

        // ARRANGE
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public boolean exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_FOUND, _response.getStatus());
    }


    @Test
    public void getResourceCanReturnOk() {

        // ACT
        final Resource resource =
            new TestResource(
                new HashMap<String, Object>()) {

                @Override
                public BodyWriter getWriter(final MediaType mediaType) {
                    return new HelloWorldWriter(UTF_8);
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertNull(_response.getHeader(Header.CONTENT_LANGUAGE));
        Assert.assertNull(_response.getHeader(Header.CONTENT_ENCODING));
        Assert.assertNull(_response.getHeader(Header.CONTENT_TYPE));
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF_8));
    }


    @Test
    public void requestConnegWithoutAcceptSelectsDefaults() {

        // ACT
        final Resource resource =
            new TestResource(
                new HashMap<String, Object>()) {

                @Override
                public Set<MediaType> getContentTypesProvided() {
                    return Collections.singleton(MediaType.JSON);
                }

                @Override
                public Set<LanguageTag> getLanguages() {
                    return Collections.singleton(new LanguageTag("da"));
                }

                @Override
                public Set<String> getEncodings() {
                    return Collections.singleton(ContentEncoding.GZIP.toString());
                }

                @Override
                public Set<Charset> getCharsetsProvided() {
                    return Collections.singleton(UTF_16);
                }

                @Override
                public BodyWriter getWriter(final MediaType mediaType) {
                    return new HelloWorldWriter(UTF_8);
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals("da", _response.getHeader(Header.CONTENT_LANGUAGE));
        Assert.assertEquals(ContentEncoding.GZIP.toString(), _response.getHeader(Header.CONTENT_ENCODING));
        Assert.assertEquals(MediaType.JSON.toString()+"; charset="+UTF_16, _response.getHeader(Header.CONTENT_TYPE));
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF_8));
    }


    @Test
    public void okWithConnegReturnsLanguage() {

        // ACT
        _request.setHeader(Header.ACCEPT_LANGUAGE, "en");
        final Resource resource =
            new TestResource(
                new HashMap<String, Object>()) {

                @Override
                public Set<MediaType> getContentTypesProvided() {
                    return Collections.singleton(MediaType.ANY);
                }

                @Override
                public BodyWriter getWriter(final MediaType mediaType) {
                    return new HelloWorldWriter(UTF_8);
                }

                @Override
                public Set<LanguageTag> getLanguages() {
                    return Collections.singleton(new LanguageTag("en"));
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals("en", _response.getHeader(Header.CONTENT_LANGUAGE));
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF_8));
    }

    @Test
    public void acceptWithUnknownLanguageGivesNoHeader() {

        // ACT
        _request.setHeader(Header.ACCEPT_LANGUAGE, "en");
        final Resource resource =
            new TestResource(
                new HashMap<String, Object>()) {

            @Override
            public Set<MediaType> getContentTypesProvided() {
                return Collections.singleton(MediaType.ANY);
            }

            @Override
            public BodyWriter getWriter(final MediaType mediaType) {
                return new HelloWorldWriter(UTF_8);
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertNull(_response.getHeader(Header.CONTENT_LANGUAGE));
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF_8));
    }


    @Test
    public void okWithConnegReturnsEncoding() {

        // ACT
        _request.setHeader(
            Header.ACCEPT_ENCODING, ContentEncoding.GZIP.toString());
        final Resource resource =
            new TestResource(
                new HashMap<String, Object>()) {

                @Override
                public Set<MediaType> getContentTypesProvided() {
                    return Collections.singleton(MediaType.ANY);
                }

                @Override
                public BodyWriter getWriter(final MediaType mediaType) {
                    return new HelloWorldWriter(UTF_8);
                }

                @Override
                public Set<String> getEncodings() {
                    return Collections.singleton(ContentEncoding.GZIP.toString());
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(
            ContentEncoding.GZIP.toString(), _response.getHeader(Header.CONTENT_ENCODING));
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF_8));
    }


    @Test
    public void acceptWithUnknownEncodingGivesNoHeader() {

        // ACT
        _request.setHeader(Header.ACCEPT_ENCODING, ContentEncoding.GZIP.toString());
        final Resource resource =
            new TestResource(
                new HashMap<String, Object>()) {

            @Override
            public Set<MediaType> getContentTypesProvided() {
                return Collections.singleton(MediaType.ANY);
            }

            @Override
            public BodyWriter getWriter(final MediaType mediaType) {
                return new HelloWorldWriter(UTF_8);
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertNull(_response.getHeader(Header.CONTENT_ENCODING));
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF_8));
    }


    @Test
    public void okWithConnegReturnsMediaType() {

        // ACT
        _request.setHeader(Header.ACCEPT, MediaType.HTML.toString());
        final Resource resource =
            new TestResource(
                new HashMap<String, Object>()) {

                @Override
                public Set<MediaType> getContentTypesProvided() {
                    return Collections.singleton(MediaType.HTML);
                }

                @Override
                public BodyWriter getWriter(final MediaType mediaType) {
                    return new HelloWorldWriter(UTF_8);
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(MediaType.HTML.toString(), _response.getHeader(Header.CONTENT_TYPE));
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF_8));
    }


    @Test
    public void okWithConnegReturnsCharset() {

        // ACT
        _request.setHeader(Header.ACCEPT_CHARSET, "utf-8");
        final Resource resource =
            new TestResource(
                new HashMap<String, Object>()) {

                @Override
                public Set<MediaType> getContentTypesProvided() {
                    return Collections.singleton(MediaType.HTML);
                }

                @Override
                public BodyWriter getWriter(final MediaType mediaType) {
                    return new HelloWorldWriter(UTF_8);
                }

                @Override
                public Set<Charset> getCharsetsProvided() {
                    return Collections.singleton(UTF_8);
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(MediaType.HTML.toString()+"; charset="+UTF_8, _response.getHeader(Header.CONTENT_TYPE));
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF_8));
    }


    @Test
    public void acceptWithUnknownCharsetGivesNoCharset() {

        // ACT
        _request.setHeader(Header.ACCEPT_CHARSET, "utf-8");
        final Resource resource =
            new TestResource(
                new HashMap<String, Object>()) {

            @Override
            public Set<MediaType> getContentTypesProvided() {
                return Collections.singleton(MediaType.HTML);
            }

            @Override
            public BodyWriter getWriter(final MediaType mediaType) {
                return new HelloWorldWriter(UTF_8);
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals(MediaType.HTML.toString(), _response.getHeader(Header.CONTENT_TYPE));
        Assert.assertEquals(
            "Hello, world!",
            _response.getBodyAsString(UTF_8));
    }


    @Test
    public void malformed() {

        // ARRANGE
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

                @Override public boolean isMalformed() {
                    return true;
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.BAD_REQUEST, _response.getStatus());
    }


    @Test
    public void optionsRequested() {

        // ARRANGE
        _request.setMethod(Method.OPTIONS);
        final Resource resource = new TestResource(
            new HashMap<String, Object>());

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
        Assert.assertEquals("0", _response.getHeader(Header.CONTENT_LENGTH));
        Assert.assertEquals(
            Utils.join(resource.getAllowedMethods(), ',').toString(),
            _response.getHeader(Header.ALLOW));
    }


    @Test
    public void postResourceCanReturnOk() {

        // ARRANGE
        _request.setMethod(Method.POST);
        _response.setBody(new byte[] {0});
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

                @Override public Set<Method> getAllowedMethods() {
                    return Collections.singleton(Method.POST);
                }

                @Override public boolean isPostCreate() {
                    return false;
                }

                @Override public void processPost() {
                    // No Op.
                }

                /** {@inheritDoc} */
                @Override
                public BodyWriter getWriter(final MediaType mediaType) {
                    return new ByteArrayBodyWriter(new byte[] {0});
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.OK, _response.getStatus());
    }


    @Test
    public void putForExistingResourceGivesNoContent() throws Exception {

        // ARRANGE
        final byte[] body = new byte[] {0};
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        _request.setMethod(Method.PUT);
        _request.setBody(body);
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public Set<Method> getAllowedMethods() {
                return Collections.singleton(Method.PUT);
            }

            @Override public boolean exists() {
                return true;
            }

            @Override
            public Map<MediaType, ? extends BodyReader> getContentTypesAccepted() {
                return Collections.singletonMap(MediaType.ANY, new SimpleBodyReader(baos, null, this, _response));
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NO_CONTENT, _response.getStatus());
        Assert.assertTrue(Arrays.equals(body, baos.toByteArray()));
        // TODO: Assert last modified attached?
        // TODO: Assert ETag added?
    }


    @Test
    public void putForMissingResourceCanBeRedirected() {

        // ARRANGE
        _request.setMethod(Method.PUT);
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public Set<Method> getAllowedMethods() {
                return Collections.singleton(Method.PUT);
            }

            @Override public URI movedPermanentlyTo() {
                try {
                    return new URI(TARGET_URI);
                } catch (final URISyntaxException e) {
                    throw new ServerHttpException(
                        Status.INTERNAL_SERVER_ERROR, e);
                }
            }

            @Override public boolean exists() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.MOVED_PERMANENTLY, _response.getStatus());
        Assert.assertEquals(TARGET_URI, _response.getHeader(Header.LOCATION));
    }


    @Test
    public void putForMissingResourceCanCauseConflict() {

        // ARRANGE
        _request.setMethod(Method.PUT);
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

                @Override public Set<Method> getAllowedMethods() {
                    return Collections.singleton(Method.PUT);
                }

                @Override
                public boolean isInConflict() {
                    return true;
                }

                @Override public boolean exists() {
                    return false;
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.CONFLICT, _response.getStatus());
    }


    @Test
    public void putForMissingResourceCreatesResource() throws Exception {

        // ARRANGE
        final byte[] body = new byte[] {0};
        final String createdPath = "/foo";                         //$NON-NLS-1$
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        _request.setMethod(Method.PUT);
        _request.setBody(body);
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public Set<Method> getAllowedMethods() {
                return Collections.singleton(Method.PUT);
            }

            @Override public boolean exists() {
                return false;
            }

            @Override
            public Map<MediaType, ? extends BodyReader> getContentTypesAccepted() {
                return Collections.singletonMap(MediaType.ANY, new SimpleBodyReader(baos, createdPath, this, _response));
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.CREATED, _response.getStatus());
        Assert.assertTrue(Arrays.equals(body, baos.toByteArray()));
        Assert.assertEquals(createdPath, _response.getHeader(Header.LOCATION));
        // TODO: Assert last modified attached?
        // TODO: Assert ETag added?
    }


    @Test
    public void serviceUnavailable() {

        // ARRANGE
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

                @Override public boolean isServiceAvailable() {
                    return false;
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

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
        _request.setHeader(Header.ACCEPT_CHARSET, "UTF-8");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {
            /** {@inheritDoc} */
            @Override
            public Set<Charset> getCharsetsProvided() {
                return Collections.singleton(Charset.forName("UTF-16"));
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_ACCEPTABLE, _response.getStatus());
    }

    @Test
    public void unacceptableRequestEncodingGivesNotAcceptable() {

        // ARRANGE
        _request.setHeader(
            Header.ACCEPT_ENCODING,
            ContentEncoding.IDENTITY+";q=0,"+ContentEncoding.GZIP);
        final Resource resource = new TestResource(
            new HashMap<String, Object>());

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_ACCEPTABLE, _response.getStatus());
    }


    @Test
    public void unacceptableRequestLanguageGivesNotAcceptable() {

        // ARRANGE
        _request.setHeader(Header.ACCEPT_LANGUAGE, Locale.UK.toString());
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

                @Override public Set<LanguageTag> getLanguages() {
                    return Collections.singleton(new LanguageTag("fr"));
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_ACCEPTABLE, _response.getStatus());
    }


    @Test
    public void unacceptableRequestMediaTypeGivesNotAcceptable() {

        // ARRANGE
        _request.setHeader(Header.ACCEPT, "text/html");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            /** {@inheritDoc} */
            @Override
            public Set<MediaType> getContentTypesProvided() {
                return Collections.singleton(MediaType.XML);
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_ACCEPTABLE, _response.getStatus());
    }


    @Test
    public void unauthorized() {

        // ARRANGE
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {
                @Override public String authorize() { return "foo"; }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.UNAUTHORIZED, _response.getStatus());
    }


    @Test
    public void unimplementedContentHeader() {

        // ARRANGE
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public boolean hasValidContentHeaders() {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_IMPLEMENTED, _response.getStatus());
    }


    @Test
    public void unknownContentType() {

        // ARRANGE
        _request.setHeader(Header.CONTENT_TYPE, "text/html");
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

            @Override public boolean isContentTypeKnown(final MediaType mediaType) {
                return false;
            }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.UNSUPPORTED_MEDIA_TYPE, _response.getStatus());
    }


    @Test
    public void unknownMethodGivesNotImplemented() {

        // ARRANGE
        _request.setMethod(Method.parse("FOO"));                   //$NON-NLS-1$
        final Resource resource = new TestResource(
            new HashMap<String, Object>());

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.NOT_IMPLEMENTED, _response.getStatus());
    }


    @Test
    public void uriTooLong() {

        // ARRANGE
        final Resource resource = new TestResource(
            new HashMap<String, Object>()) {

                @Override public boolean isUriTooLong() {
                    return true;
                }
        };

        // ACT
        _engine.process(resource, _request, _response);

        // ASSERT
        Assert.assertSame(Status.REQUEST_URI_TOO_LONG, _response.getStatus());
    }
}
