package net.elodina.mesos.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Request {
    private String uri;
    private Method method = Method.GET;
    private Map<String, String> params = new LinkedHashMap<>();
    private Map<String, String> headers = new LinkedHashMap<>();

    public Request() {}

    public Request(String uri) { this(Method.GET, uri); }

    public Request(Method method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public String uri() { return uri; }
    public Request uri(String uri) { this.uri = uri; return this; }


    public Method method() { return method; }
    public Request method(Method method) { this.method = method; return this; }


    public Map<String, String> params() { return Collections.unmodifiableMap(params); }
    public Request params(Map<String, String> params) { this.params.clear(); this.params.putAll(params); return this; }

    public Request param(String name, String value) { this.params.put(name, value); return this; }
    public String param(String name) { return params.get(name); }


    public Map<String, String> headers() { return Collections.unmodifiableMap(headers); }
    public Request headers(Map<String, String> headers) { this.headers.clear(); this.headers.putAll(headers); return this; }

    public Request header(String name, String value) { headers.put(name, value); return this; }
    public String header(String name) { return headers.get(name); }


    public String contentType() { return headers.get("Content-Type"); }
    public Request contentType(String contentType) { header("Content-Type", contentType); return this; }

    public String encoding() { return encoding(contentType()); }

    static String encoding(String contentType) {
        String defEnc = Charset.defaultCharset().name();
        if (contentType == null) return defEnc;

        // Content-Type: text/html; charset=utf-8
        int eqIdx = contentType.indexOf("=");
        return eqIdx == -1 ? defEnc : contentType.substring(eqIdx + 1);
    }

    public String query() {
        String qs = "";
        String encoding = encoding();

        for (String name : params.keySet()) {
            String value = params.get(name);

            if (!qs.isEmpty()) qs += "&";
            try {
                qs += URLEncoder.encode(name, encoding);

                if (value != null) {
                    qs += "=";
                    qs += URLEncoder.encode(params.get(name), encoding);
                }
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        return qs.isEmpty() ? null : qs;
    }

    public Response send() throws IOException {
        String uri = this.uri;

        String query = query();
        if (query != null && method == Method.GET) uri += "?" + query;

        HttpURLConnection c = (HttpURLConnection) new URL(uri).openConnection();
        try {
            c.setRequestMethod(method.name());
            for (String name : headers.keySet())
                c.setRequestProperty(name, headers.get(name));

            if (method == Method.POST && query != null) {
                c.setDoOutput(true);
                c.getOutputStream().write(query.getBytes("latin-1"));
            }

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            IO.copyAndClose(c.getInputStream(), bytes);

            Response response = new Response()
                .code(c.getResponseCode())
                .message(c.getResponseMessage());

            Map<String, String> headers = new LinkedHashMap<>();
            for (String name : c.getHeaderFields().keySet())
                headers.put(name, c.getHeaderField(name));

            response.headers(headers);
            response.bytes(bytes.toByteArray());

            return response;
        } finally {
            c.disconnect();
        }
    }

    public enum Method {
        GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS
    }

    public static class Response {
        private int code;
        private String message;

        private Map<String, String> headers = new LinkedHashMap<>();

        private byte[] bytes;

        public int code() { return code; }
        public Response code(int code) { this.code = code; return this; }

        public String message() { return message; }
        public Response message(String message) { this.message = message; return this; }


        public Map<String, String> headers() { return Collections.unmodifiableMap(headers); }
        public Response headers(Map<String, String> headers) { this.headers.clear(); this.headers.putAll(headers); return this; }

        public Response header(String name, String value) { headers.put(name, value); return this; }
        public String header(String name) { return headers.get(name); }

        public String contentType() { return header("Content-Type"); }
        public Response contentType(String contentType) { header("Content-Type", contentType); return this; }

        public String encoding() { return Request.encoding(contentType()); }


        public byte[] bytes() { return bytes; }
        public Response bytes(byte[] bytes) { this.bytes = bytes; return this; }


        public String asText() { return new String(bytes, Charset.forName(encoding())); }
    }
}
