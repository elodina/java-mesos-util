package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;

import java.util.*;

public class Command extends Base {
    private String value;
    private List<URI> uris = new ArrayList<>();
    private Map<String, String> environment = new LinkedHashMap<>();

    public String value() { return value; }
    public Command value(String value) { this.value = value; return this; }

    public List<URI> uris() { return Collections.unmodifiableList(uris); }
    public Command uris(URI... uris) { return uris(Arrays.asList(uris)); }
    public Command uris(List<URI> uris) { this.uris.clear(); this.uris.addAll(uris); return this; }

    public Map<String, String> environment() { return Collections.unmodifiableMap(environment); }
    public Command environment(Map<String, String> environment) { this.environment.clear(); this.environment.putAll(environment); return this; }

    @Override
    public org.apache.mesos.Protos.CommandInfo proto0() {
        org.apache.mesos.Protos.CommandInfo.Builder builder = org.apache.mesos.Protos.CommandInfo.newBuilder();
        if (value != null) builder.setValue(value);

        List<org.apache.mesos.Protos.CommandInfo.URI> u = new ArrayList<>();
        for (URI uri : uris) u.add(uri.proto0());
        builder.addAllUris(u);

        if (!environment.isEmpty()) {
            org.apache.mesos.Protos.Environment.Builder envBuilder = org.apache.mesos.Protos.Environment.newBuilder();
            for (String name : environment.keySet()) {
                String value = environment.get(name);
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
            environment.clear();
            for (org.apache.mesos.Protos.Environment.Variable var : command.getEnvironment().getVariablesList())
                environment.put(var.getName(), var.getValue());
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

        if (!environment.isEmpty()) {
            org.apache.mesos.v1.Protos.Environment.Builder envBuilder = org.apache.mesos.v1.Protos.Environment.newBuilder();
            for (String name : environment.keySet()) {
                String value = environment.get(name);
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
            uris.add(new URI().proto0(uri));

        if (command.hasEnvironment()) {
            environment = new LinkedHashMap<>();
            for (org.apache.mesos.v1.Protos.Environment.Variable var : command.getEnvironment().getVariablesList())
                environment.put(var.getName(), var.getValue());
        }

        return this;
    }

    public static class URI extends Base {
        private String value;
        private boolean extract = true;

        public URI() { this(null); }

        public URI(String value) { this(value, false); }

        public URI(String value, boolean extract) {
            value(value);
            extract(extract);
        }

        public String value() { return value; }
        public URI value(String value) { this.value = value; return this; }

        public boolean extract() { return extract; }
        public URI extract(boolean extract) { this.extract = extract; return this; }

        @Override
        public org.apache.mesos.Protos.CommandInfo.URI proto0() {
            org.apache.mesos.Protos.CommandInfo.URI.Builder builder = org.apache.mesos.Protos.CommandInfo.URI.newBuilder();

            builder.setValue(value);
            builder.setExtract(extract);

            return builder.build();
        }

        @Override
        public URI proto0(GeneratedMessage message) {
            org.apache.mesos.Protos.CommandInfo.URI uri = (org.apache.mesos.Protos.CommandInfo.URI) message;

            value = uri.getValue();
            extract = uri.getExtract();

            return this;
        }

        @Override
        public org.apache.mesos.v1.Protos.CommandInfo.URI proto1() {
            org.apache.mesos.v1.Protos.CommandInfo.URI.Builder builder = org.apache.mesos.v1.Protos.CommandInfo.URI.newBuilder();

            builder.setValue(value);
            builder.setExtract(extract);

            return builder.build();
        }

        @Override
        public URI proto1(GeneratedMessage message) {
            org.apache.mesos.v1.Protos.CommandInfo.URI uri = (org.apache.mesos.v1.Protos.CommandInfo.URI) message;

            value = uri.getValue();
            extract = uri.getExtract();

            return this;
        }
    }
}
