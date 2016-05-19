package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Strings;

import java.util.*;

public class Command extends Message {
    private String value;
    private List<URI> uris = new ArrayList<>();
    private Map<String, String> env = new LinkedHashMap<>();

    public String value() { return value; }
    public Command value(String value) { this.value = value; return this; }

    public List<URI> uris() { return Collections.unmodifiableList(uris); }
    public Command uris(URI... uris) { return uris(Arrays.asList(uris)); }
    public Command uris(List<URI> uris) { this.uris.clear(); this.uris.addAll(uris); return this; }

    public Command addUri(URI uri) { uris.add(uri); return this; }
    public Command removeUri(URI uri) { uris.remove(uri); return this; }

    public Map<String, String> env() { return Collections.unmodifiableMap(env); }
    public Command env(Map<String, String> env) { this.env.clear(); this.env.putAll(env); return this; }

    public Command() {}
    public Command(String s) { parse(s); }

    @Override
    public org.apache.mesos.Protos.CommandInfo proto0() {
        org.apache.mesos.Protos.CommandInfo.Builder builder = org.apache.mesos.Protos.CommandInfo.newBuilder();
        if (value != null) builder.setValue(value);

        List<org.apache.mesos.Protos.CommandInfo.URI> u = new ArrayList<>();
        for (URI uri : uris) u.add(uri.proto0());
        builder.addAllUris(u);

        if (!env.isEmpty()) {
            org.apache.mesos.Protos.Environment.Builder envBuilder = org.apache.mesos.Protos.Environment.newBuilder();
            for (String name : env.keySet()) {
                String value = env.get(name);
                envBuilder.addVariables(org.apache.mesos.Protos.Environment.Variable.newBuilder().setName(name).setValue(value));
            }
            builder.setEnvironment(envBuilder);
        }

        return builder.build();
    }

    @Override
    public Command proto0(GeneratedMessage message) {
        org.apache.mesos.Protos.CommandInfo command = (org.apache.mesos.Protos.CommandInfo) message;
        value = command.getValue();

        uris.clear();
        for (org.apache.mesos.Protos.CommandInfo.URI uri : command.getUrisList())
            uris.add(new URI().proto0(uri));

        if (command.hasEnvironment()) {
            env.clear();
            for (org.apache.mesos.Protos.Environment.Variable var : command.getEnvironment().getVariablesList())
                env.put(var.getName(), var.getValue());
        }

        return this;
    }

    @Override
    public org.apache.mesos.v1.Protos.CommandInfo proto1() {
        org.apache.mesos.v1.Protos.CommandInfo.Builder builder = org.apache.mesos.v1.Protos.CommandInfo.newBuilder();
        if (value != null) builder.setValue(value);

        List<org.apache.mesos.v1.Protos.CommandInfo.URI> u = new ArrayList<>();
        for (URI uri : uris) u.add(uri.proto1());
        builder.addAllUris(u);

        if (!env.isEmpty()) {
            org.apache.mesos.v1.Protos.Environment.Builder envBuilder = org.apache.mesos.v1.Protos.Environment.newBuilder();
            for (String name : env.keySet()) {
                String value = env.get(name);
                envBuilder.addVariables(org.apache.mesos.v1.Protos.Environment.Variable.newBuilder().setName(name).setValue(value));
            }
            builder.setEnvironment(envBuilder);
        }

        return builder.build();
    }

    @Override
    public Command proto1(GeneratedMessage message) {
        org.apache.mesos.v1.Protos.CommandInfo command = (org.apache.mesos.v1.Protos.CommandInfo) message;
        value = command.getValue();

        uris = new ArrayList<>();
        for (org.apache.mesos.v1.Protos.CommandInfo.URI uri : command.getUrisList())
            uris.add(new URI().proto1(uri));

        if (command.hasEnvironment()) {
            env = new LinkedHashMap<>();
            for (org.apache.mesos.v1.Protos.Environment.Variable var : command.getEnvironment().getVariablesList())
                env.put(var.getName(), var.getValue());
        }

        return this;
    }

    private void parse(String s) {
        List<String> parts = new ArrayList<>();

        StringBuilder buffer = new StringBuilder();
        boolean inExpr = false;
        for (char c : s.toCharArray()) {
            if (c == ',' && !inExpr) {
                parts.add("" + buffer);
                buffer.setLength(0);
            } else {
                if (c == '[') inExpr = true;
                else if (c == ']') inExpr = false;
                buffer.append(c);
            }
        }
        if (inExpr) throw new IllegalArgumentException(s);
        if (buffer.length() > 0) parts.add("" + buffer);

        Map<String, String> values = new HashMap<>();
        for (String part : parts) {
            int colon = part.indexOf(":");
            if (colon == -1 && !values.isEmpty()) throw new IllegalArgumentException(s);

            String name = colon == -1 ? "value" : part.substring(0, colon);
            String value = colon == -1 ? part : part.substring(colon + 1);
            values.put(name.trim(), value.trim());
        }

        value = values.get("value");

        String urisVal = values.get("uris");
        if (urisVal != null) uris = URI.parse(urisVal.substring(1, urisVal.length() - 1));

        String envVal = values.get("env");
        if (envVal != null) env = Strings.parseMap(envVal.substring(1, envVal.length() - 1));
    }

    public String toString(boolean _short) {
        String s = "";

        s += value;
        if (!uris.isEmpty()) s += ", uris:[" + URI.format(uris) + "]";
        if (!env.isEmpty()) s += ", env:[" + Strings.formatMap(env) + "]";

        return s;
    }

    public static class URI extends Message {
        private String value;
        private boolean extract = true;
        private boolean cache = true;
        private boolean executable;

        public URI() {}

        public URI(String s) {
            String[] parts = s.split(",");

            if (parts.length == 1)
                value = parts[0];
            else {
                value = parts[0].trim();

                Map<String, String> values = new HashMap<>();
                for (int i = 1; i < parts.length; i++) {
                    String part = parts[i].trim();

                    int colon = part.indexOf(":");
                    if (colon == -1) throw new IllegalArgumentException(s);

                    values.put(part.substring(0, colon).trim(), part.substring(colon + 1).trim());
                }

                if (values.containsKey("extract")) extract = Boolean.parseBoolean(values.get("extract"));
                if (values.containsKey("cache")) cache = Boolean.parseBoolean(values.get("cache"));
                if (values.containsKey("executable")) executable = Boolean.parseBoolean(values.get("executable"));
            }
        }

        public URI(String value, boolean extract) {
            value(value);
            extract(extract);
        }

        public String value() { return value; }
        public URI value(String value) { this.value = value; return this; }

        public boolean extract() { return extract; }
        public URI extract(boolean extract) { this.extract = extract; return this; }

        public boolean cache() { return cache; }
        public URI cache(boolean cache) { this.cache = cache; return this; }

        public boolean executable() { return executable; }
        public URI executable(boolean executable) { this.executable = executable; return this; }

        @Override
        public org.apache.mesos.Protos.CommandInfo.URI proto0() {
            org.apache.mesos.Protos.CommandInfo.URI.Builder builder = org.apache.mesos.Protos.CommandInfo.URI.newBuilder();

            builder.setValue(value);
            builder.setExtract(extract);
            builder.setCache(cache);
            builder.setExecutable(executable);

            return builder.build();
        }

        @Override
        public URI proto0(GeneratedMessage message) {
            org.apache.mesos.Protos.CommandInfo.URI uri = (org.apache.mesos.Protos.CommandInfo.URI) message;

            value = uri.getValue();
            extract = uri.getExtract();
            cache = uri.getCache();
            executable = uri.getExecutable();

            return this;
        }

        @Override
        public org.apache.mesos.v1.Protos.CommandInfo.URI proto1() {
            org.apache.mesos.v1.Protos.CommandInfo.URI.Builder builder = org.apache.mesos.v1.Protos.CommandInfo.URI.newBuilder();

            builder.setValue(value);
            builder.setExtract(extract);
            builder.setCache(cache);
            builder.setExecutable(executable);

            return builder.build();
        }

        @Override
        public URI proto1(GeneratedMessage message) {
            org.apache.mesos.v1.Protos.CommandInfo.URI uri = (org.apache.mesos.v1.Protos.CommandInfo.URI) message;

            value = uri.getValue();
            extract = uri.getExtract();
            cache = uri.getCache();
            executable = uri.getExecutable();

            return this;
        }

        public int hashCode() {
            return Arrays.asList(value, extract, cache, executable).hashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof URI)) return false;
            URI other = (URI) obj;
            return Arrays.asList(value, extract, cache, executable).equals(Arrays.asList(other.value, other.extract, other.cache, other.executable));
        }

        public String toString(boolean _short) {
            String s = "";

            s += value;
            if (!extract) s += ", extract:false";
            if (!cache) s += ", cache:false";
            if (executable) s += ", executable:true";

            return s;
        }

        public static String format(List<URI> uris) { return Strings.join(uris, ","); }

        public static List<URI> parse(String s) {
            List<URI> uris = new ArrayList<>();

            for (String p : s.split(","))
                uris.add(new URI(p.trim()));

            return uris;
        }
    }
}
