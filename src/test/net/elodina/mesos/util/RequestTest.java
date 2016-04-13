package net.elodina.mesos.util;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RequestTest {
    @Test
    public void encoding() {
        assertEquals(Charset.defaultCharset().name(), Request.encoding(null));
        assertEquals("utf-16", Request.encoding("text/html; charset=utf-16"));
    }

    @Test
    public void contentType() {
        Request request = new Request();
        request.header("Content-Type", "text/html");
        assertEquals("text/html", request.contentType());
    }

    @Test
    public void query() {
        Request request = new Request();
        assertNull(request.query());

        request.param("a", "1");
        request.param("b", "2", "3");
        request.param("c", (String) null);
        assertEquals("a=1&b=2&b=3&c", request.query());
    }

    @Test
    public void query_url_encoding() {
        Request request = new Request();
        request.param("a", "=");
        request.param("b", " ");
        assertEquals("a=%3D&b=+", request.query());
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
            Request request = new Request(Request.Method.GET, server.getUrl() + "/")
                .header("a", "1", "2").header("b", "3")
                .param("a", "1", "2").param("b", "3");

            Request.Response response = request.send();

            assertEquals("response", response.asText());
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

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            method = exchange.getRequestMethod();
            uri = exchange.getRequestURI();
            headers = exchange.getRequestHeaders();
            response(exchange);
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
