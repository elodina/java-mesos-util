package net.elodina.mesos.util;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;

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
        request.param("b", "2");
        request.param("c", null);
        assertEquals("a=1&b=2&c", request.query());
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
                exchange.getResponseHeaders().set("h1", "1");
                exchange.getResponseHeaders().set("h2", "2");

                byte[] data = "response".getBytes();
                exchange.sendResponseHeaders(200, data.length);
                exchange.getResponseBody().write(data);
            }
        };

        try(HttpServer server = new HttpServer(handler)) {
            Request request = new Request(Request.Method.GET, server.getUrl() + "/")
                .headers(Strings.parseMap("h1=1,h2=2"))
                .params(Strings.parseMap("p1=1,p2=2"));

            Request.Response response = request.send();

            assertEquals("response", response.asText());
            assertEquals("GET", handler.method);

            // params sent
            assertEquals("/?p1=1&p2=2", "" + handler.uri);

            // request headers sent
            assertEquals("1", "" + handler.headers.getFirst("h1"));
            assertEquals("2", "" + handler.headers.getFirst("h2"));

            // response code & message
            assertEquals(200, response.code());
            assertEquals("OK", response.message());

            // response headers received
            assertEquals("1", response.header("H1"));
            assertEquals("2", response.header("H2"));
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
