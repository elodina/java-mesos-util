package net.elodina.mesos.util;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RequestTest {
    @Test
    public void encoding() {
        assertEquals(Charset.defaultCharset().name(), Request.encoding(null));
        assertEquals("utf-16", Request.encoding("text/html; charset=utf-16"));
    }

    @Test
    public void contentType_get() {
        Request request = new Request();
        request.header("Content-Type", "text/html");
        assertEquals("text/html", request.contentType());
    }

    @Test
    public void uri_set() {
        Request request = new Request();
        request.uri("/path?a=1&b=2");

        assertEquals("/path", request.uri());
        assertEquals("1", request.param("a"));
        assertEquals("2", request.param("b"));
    }

    @Test
    public void query_get() {
        Request request = new Request();
        assertNull(request.query());

        // all types of params
        request.param("a", "1");
        request.param("b", "2", "3");
        request.param("c", (String) null);
        assertEquals("a=1&b=2&b=3&c", request.query());

        // url encoding
        request.params().clear();
        request.param("a", "=");
        request.param("b", " ");
        assertEquals("a=%3D&b=+", request.query());
    }

    @Test
    public void query_set() {
        Request request = new Request();

        // all types of params
        request.query("a=1&b=2&b=3&c");
        assertEquals("1", request.param("a"));
        assertEquals(Arrays.asList("2", "3"), request.params("b"));
        assertEquals(null, request.param("c"));

        // url decoding
        request.params().clear();
        request.query("a=%3D&b=+");
        assertEquals("=", request.param("a"));
        assertEquals(" ", request.param("b"));
    }

    @Test
    public void send() throws IOException {
        HttpHandler handler = new HttpHandler() {
            @Override
            public void response(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("a", "1");
                exchange.getResponseHeaders().add("a", "2");
                exchange.getResponseHeaders().add("b", "3");

                byte[] data = "response".getBytes();
                exchange.sendResponseHeaders(200, data.length);
                exchange.getResponseBody().write(data);
            }
        };

        try(HttpServer server = new HttpServer(handler)) {
            Request request = new Request(server.getUrl() + "/")
                .header("a", "1", "2").header("b", "3")
                .param("a", "1", "2").param("b", "3");

            Request.Response response = request.send();

            assertEquals("response", response.text());
            assertEquals("GET", handler.method);

            // params sent
            assertEquals("/?a=1&a=2&b=3", "" + handler.uri);

            // request headers sent
            assertEquals(Arrays.asList("1", "2"), handler.headers.get("a"));
            assertEquals(Arrays.asList("3"), handler.headers.get("b"));

            // response code & message
            assertEquals(200, response.code());
            assertEquals("OK", response.message());

            // response headers received
            List<String> aValues = new ArrayList<>(response.headers("A"));
            Collections.sort(aValues);

            assertEquals(Arrays.asList("1", "2"), aValues);
            assertEquals("3", response.header("B"));
        }
    }

    @Test
    public void send_keepOpen() throws IOException {
        HttpHandler handler = new HttpHandler() {
            @Override
            public void response(HttpExchange exchange) throws IOException {
                byte[] data = "123\n".getBytes();
                exchange.sendResponseHeaders(200, data.length);
                exchange.getResponseBody().write(data);
            }
        };

        try(HttpServer server = new HttpServer(handler); Request request = new Request(server.getUrl() + "/")) {
            Request.Response response = request
                .contentType("text/plain")
                .send(true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.stream()));
            String line = reader.readLine();
            assertEquals("123", line);
        }
    }

    @Test
    public void send_404_500() throws IOException {
        final AtomicInteger code = new AtomicInteger(0);

        HttpHandler handler = new HttpHandler() {
            @Override
            public void response(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(code.get(), 5); exchange.getResponseBody().write("error".getBytes());
            }
        };

        try(HttpServer server = new HttpServer(handler)) {
            // 404
            code.set(404);
            Request.Response response = new Request(server.getUrl() + "/").send();
            assertEquals(404, response.code());
            assertEquals("Not Found", response.message());

            // 500
            handler.reset();
            code.set(500);
            response = new Request(server.getUrl() + "/").send();
            assertEquals(500, response.code());
            assertEquals("error", new String(response.body()));
            assertEquals("Internal Server Error", response.message());
        }
    }

    @Test
    public void send_body() throws IOException {
        HttpHandler handler = new HttpHandler() {
            @Override
            public void response(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("Content-Length", "0");
                exchange.sendResponseHeaders(200, 0);
            }
        };

        try(HttpServer server = new HttpServer(handler)) {
            Request.Response response = new Request(server.getUrl() + "/")
                .method(Request.Method.PUT)
                .body("body".getBytes())
                .send();

            assertEquals(200, response.code());
            assertEquals("PUT", handler.method);
            assertEquals("body", new String(handler.body));
        }
    }

    // Response
    @Test
    public void Response_contentType() {
        Request.Response response = new Request.Response();
        assertNull(response.contentType());

        response.header("Content-Type", "text/plain");
        assertEquals("text/plain", response.contentType());
    }

    private static class HttpHandler implements com.sun.net.httpserver.HttpHandler {
        private String method;
        private URI uri;

        private Headers headers;
        private byte[] body;

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            method = exchange.getRequestMethod();
            uri = exchange.getRequestURI();
            headers = exchange.getRequestHeaders();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            IO.copyAndClose(exchange.getRequestBody(), bytes);
            body = bytes.toByteArray();

            response(exchange);
        }

        public void reset() {
            method = null;
            uri = null;
            headers = null;
        }

        public void response(HttpExchange exchange) throws IOException {}
    }

    private static class HttpServer implements Closeable {
        private com.sun.net.httpserver.HttpHandler handler;
        private com.sun.net.httpserver.HttpServer server;

        private HttpServer(com.sun.net.httpserver.HttpHandler handler) throws IOException {
            this.handler = handler;
            start();
        }

        public void start() throws IOException {
            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(Net.findAvailPort()), 0);
            server.createContext("/", handler);
            server.setExecutor(null);
            server.start();
        }

        public void stop() {
            server.stop(0);
            server = null;
        }

        public String getUrl() { return "http://localhost:" + server.getAddress().getPort(); }

        @Override
        public void close() {
            if (server != null) stop();
        }
    }
}
