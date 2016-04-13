package net.elodina.mesos.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

public class Request {
    private String uri;
    private Method method = Method.GET;
    private Values headers = new Values();

    private Values params = new Values(true);
    private byte[] body;

    private boolean followRedirects;

    public Request() {}

    public Request(String uri) { this(Method.GET, uri); }

    public Request(Method method, String uri) {
        this.method = method;
        uri(uri);
    }

    public String uri() { return uri; }
    public Request uri(String uri) {
        int qIdx = uri.indexOf("?");

        if (qIdx != -1) {
            query(uri.substring(qIdx + 1));
            uri = uri.substring(0, qIdx);
        }

        this.uri = uri;
        return this;
    }


    public Method method() { return method; }
    public Request method(Method method) { this.method = method; return this; }


    public Values headers() { return headers; }
    public String header(String name) { return headers.get(name); }
    public List<String> headers(String name) { return headers.all(name); }
    public Request header(String name, String value) { headers.set(name, value); return this; }
    public Request header(String name, String ... values) { headers.set(name, values); return this; }
    public Request headers(Map<String, String> values) { headers.set(values); return this; }


    public Values params() { return params; }
    public String param(String name) { return params.get(name); }
    public List<String> params(String name) { return params.all(name); }
    public Request param(String name, String value) { params.set(name, value); return this; }
    public Request param(String name, String ... values) { params.set(name, values); return this; }
    public Request params(Map<String, String> values) { params.set(values); return this; }


    public byte[] body() { return body; }
    public Request body(byte[] body) { this.body = body; return this; }


    public boolean followRedirects() { return followRedirects; }
    public Request followRedirects(boolean followRedirects) { this.followRedirects = followRedirects; return this; }


    public String contentType() { return header("Content-Type"); }
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

        for (String name : params.names()) {
            List<String> values = params.all(name);

            for (String v : values) {
                if (!qs.isEmpty()) qs += "&";

                try {
                    qs += URLEncoder.encode(name, encoding);

                    if (v != null) {
                        qs += "=";
                        qs += URLEncoder.encode(v, encoding);
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new IOError(e);
                }
            }
        }

        return qs.isEmpty() ? null : qs;
    }

    public Request query(String query) {
        String encoding = encoding();
        Values params = new Values(true);

        String[] parts = query.split("&");
        for (String part : parts) {
            if (part.isEmpty()) throw new IllegalArgumentException(query);

            String[] nameValue = part.split("=");
            if (nameValue.length > 2) throw new IllegalArgumentException(query);

            try {
                String name = URLDecoder.decode(nameValue[0], encoding);
                String value = nameValue.length > 1 ? URLDecoder.decode(nameValue[1], encoding) : null;
                params.add(name, value);
            } catch (UnsupportedEncodingException e) {
                throw new IOError(e);
            }
        }

        this.params.setAll(params.all());
        return this;
    }

    public Response send() throws IOException {
        String uri = this.uri;

        String query = query();
        if (query != null && method == Method.GET) uri += "?" + query;

        HttpURLConnection c = (HttpURLConnection) new URL(uri).openConnection();
        try {
            c.setInstanceFollowRedirects(followRedirects);
            c.setRequestMethod(method.name());

            for (String name : headers.names()) {
                List<String> values = headers.all(name);
                for (String value : values) c.addRequestProperty(name, value);
            }

            if (method == Method.POST && query != null) {
                byte[] bytes = query.getBytes("latin1");
                c.setDoOutput(true);
                c.setRequestProperty("Content-Length", "" + bytes.length);
                c.getOutputStream().write(bytes);
            } else if (body != null) {
                c.setDoOutput(true);
                c.setRequestProperty("Content-Length", "" + body.length);
                c.getOutputStream().write(body);
            }

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            try { IO.copyAndClose(c.getInputStream(), bytes); }
            catch (FileNotFoundException ignore) {}
            catch (IOException e) { if (!e.getMessage().contains("Server returned HTTP response code:")) throw e; }

            Response response = new Response()
                .code(c.getResponseCode())
                .message(c.getResponseMessage());

            response.headers().setAll(c.getHeaderFields());
            response.body(bytes.size() > 0 ? bytes.toByteArray() : null);

            return response;
        } finally {
            c.disconnect();
        }
    }

    public enum Method {
        GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS
    }

    public static class Values {
        private boolean allowNulls;

        public Values() { this(false); }
        public Values(boolean allowNulls) {
            this.allowNulls = allowNulls;
        }

        private Map<String, List<String>> values = new LinkedHashMap<>();

        public Set<String> names() { return Collections.unmodifiableSet(values.keySet()); }

        public String get(String name) {
            List<String> v = values.get(name);
            return v != null && !v.isEmpty() ? v.get(0) : null;
        }

        public List<String> all(String name) {
            List<String> v = values.get(name);
            return v != null ? Collections.unmodifiableList(v) : null;
        }

        public Map<String, List<String>> all() { return Collections.unmodifiableMap(values); }

        public void setAll(Map<String, List<String>> values) {
            this.values.clear();
            for (String name : values.keySet())
                set(name, values.get(name));
        }

        public void set(Map<String, String> values) {
            for (String name : values.keySet())
                set(name, values.get(name));
        }

        public void set(String name, String value) { values.put(name, new ArrayList<>(Arrays.asList(checkNull(value)))); }

        public void set(String name, String ... values) { set(name, Arrays.asList(values)); }

        public void set(String name, List<String> values) { this.values.put(name, checkNulls(new ArrayList<>(values))); }

        public void add(String name, String value) {
            if (!values.containsKey(name)) values.put(name, new ArrayList<String>());
            values.get(name).add(checkNull(value));
        }

        public void add(String name, String ... values) { add(name, Arrays.asList(values)); }

        public void add(String name, List<String> values) {
            if (!this.values.containsKey(name)) this.values.put(name, new ArrayList<String>());
            this.values.get(name).addAll(checkNulls(values));
        }

        private String checkNull(String value) {
            if (!allowNulls && value == null)
                throw new NullPointerException();
            return value;
        }

        private List<String> checkNulls(List<String> values) {
            for (String value : values) {
                if (!allowNulls && value == null)
                    throw new NullPointerException();
            }
            return values;
        }

        public void remove(String name) { values.remove(name); }

        public void clear() { values.clear(); }

    }

    public static class Response {
        private int code;
        private String message;

        private Values headers = new Values();

        private byte[] body;

        public int code() { return code; }
        public Response code(int code) { this.code = code; return this; }

        public String message() { return message; }
        public Response message(String message) { this.message = message; return this; }


        public Values headers() { return headers; }
        public String header(String name) { return headers.get(name); }
        public List<String> headers(String name) { return headers.all(name); }
        public Response header(String name, String value) { headers.set(name, value); return this; }
        public Response header(String name, String ... values) { headers.set(name, values); return this; }
        public Response headers(Map<String, String> values) { headers.set(values); return this; }

        public String contentType() { return header("Content-Type"); }
        public Response contentType(String contentType) { header("Content-Type", contentType); return this; }

        public String encoding() { return Request.encoding(contentType()); }


        public byte[] body() { return body; }
        public Response body(byte[] body) { this.body = body; return this; }


        public String text() { return body != null ? new String(body, Charset.forName(encoding())) : null; }
    }
}
