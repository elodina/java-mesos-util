package net.elodina.mesos.api;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Range;

import java.util.*;

public class Mesos {
    public static abstract class Base {
        public abstract GeneratedMessage proto0();
        public abstract Base proto0(GeneratedMessage message);

        public abstract GeneratedMessage proto1();
        public abstract Base proto1(GeneratedMessage message);
    }

    public static class Offer extends Base {
        private String id;
        private String frameworkId;
        private String slaveId;
        private String hostname;

        private List<Resource> resources = new ArrayList<>();
        private List<Attribute> attributes = new ArrayList<>();

        public String id() { return id; }
        public Offer id(String id) { this.id = id; return this; }

        public String frameworkId() { return frameworkId; }
        public Offer frameworkId(String id) { frameworkId = id; return this; }

        public String slaveId() { return slaveId; }
        public Offer slaveId(String id) { slaveId = id; return this; }

        public String hostname() { return hostname; }
        public Offer hostname(String hostname) { this.hostname = hostname; return this; }


        public List<Resource> resources() { return Collections.unmodifiableList(resources); }
        public Offer resources(List<Resource> resources) { this.resources.clear(); this.resources.addAll(resources); return this; }
        public Offer resources(Resource ... resources) { return resources(Arrays.asList(resources)); }

        public List<Attribute> attributes() { return Collections.unmodifiableList(attributes); }
        public Offer attributes(List<Attribute> attributes) { this.attributes.clear(); this.attributes.addAll(attributes); return this; }
        public Offer attributes(Attribute ... attributes) { attributes(Arrays.asList(attributes)); return this; }


        @Override
        public org.apache.mesos.Protos.Offer proto0() {
            org.apache.mesos.Protos.Offer.Builder builder = org.apache.mesos.Protos.Offer.newBuilder();
            builder.setId(org.apache.mesos.Protos.OfferID.newBuilder().setValue(id));
            builder.setFrameworkId(org.apache.mesos.Protos.FrameworkID.newBuilder().setValue(frameworkId));
            builder.setSlaveId(org.apache.mesos.Protos.SlaveID.newBuilder().setValue(frameworkId));
            builder.setHostname(hostname);

            List<org.apache.mesos.Protos.Resource> r = new ArrayList<>();
            for (Resource resource : resources) r.add(resource.proto0());
            builder.addAllResources(r);

            List<org.apache.mesos.Protos.Attribute> a = new ArrayList<>();
            for (Attribute attribute : attributes) a.add(attribute.proto0());
            builder.addAllAttributes(a);

            return builder.build();
        }

        @Override
        public Offer proto0(GeneratedMessage message) {
            org.apache.mesos.Protos.Offer offer = (org.apache.mesos.Protos.Offer) message;

            id = offer.getId().getValue();
            frameworkId = offer.getFrameworkId().getValue();
            slaveId = offer.getSlaveId().getValue();
            hostname = offer.getHostname();

            resources.clear();
            for (org.apache.mesos.Protos.Resource resource : offer.getResourcesList())
                resources.add(new Resource().proto0(resource));

            attributes.clear();
            for (org.apache.mesos.Protos.Attribute attribute : offer.getAttributesList())
                attributes.add(new Attribute().proto0(attribute));

            return this;
        }

        @Override
        public org.apache.mesos.v1.Protos.Offer proto1() {
            org.apache.mesos.v1.Protos.Offer.Builder builder = org.apache.mesos.v1.Protos.Offer.newBuilder();
            builder.setId(org.apache.mesos.v1.Protos.OfferID.newBuilder().setValue(id));
            builder.setFrameworkId(org.apache.mesos.v1.Protos.FrameworkID.newBuilder().setValue(frameworkId));
            builder.setAgentId(org.apache.mesos.v1.Protos.AgentID.newBuilder().setValue(frameworkId));
            builder.setHostname(hostname);

            List<org.apache.mesos.v1.Protos.Resource> r = new ArrayList<>();
            for (Resource resource : resources) r.add(resource.proto1());
            builder.addAllResources(r);

            List<org.apache.mesos.v1.Protos.Attribute> a = new ArrayList<>();
            for (Attribute attribute : attributes) a.add(attribute.proto1());
            builder.addAllAttributes(a);

            return builder.build();
        }

        @Override
        public Base proto1(GeneratedMessage message) {
            org.apache.mesos.v1.Protos.Offer offer = (org.apache.mesos.v1.Protos.Offer) message;

            id = offer.getId().getValue();
            frameworkId = offer.getFrameworkId().getValue();
            slaveId = offer.getAgentId().getValue();
            hostname = offer.getHostname();

            resources.clear();
            for (org.apache.mesos.v1.Protos.Resource resource : offer.getResourcesList())
                resources.add(new Resource().proto1(resource));

            attributes.clear();
            for (org.apache.mesos.v1.Protos.Attribute attribute : offer.getAttributesList())
                attributes.add(new Attribute().proto1(attribute));

            return this;
        }
    }

    public static class Resource extends Base {
        private String name;
        private Type type;

        private Double scalar;
        private List<Range> ranges;

        private String role = "*";

        public Resource() {}
        public Resource(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        public String name() { return name; }
        public Resource name(String name) { this.name = name; return this; }


        public Type type() { return type; }
        public Resource type(Type type) { this.type = type; return this; }

        public Double scalar() { return scalar; }
        public Resource scalar(Double scalar) { this.scalar = scalar; return this; }

        public List<Range> ranges() { return ranges != null ? Collections.unmodifiableList(ranges) : null; }
        public Resource ranges(List<Range> ranges) { this.ranges = ranges != null ? new ArrayList<>(ranges) : null; return this; }


        public String role() { return role; }
        public Resource role(String role) { this.role = role; return this; }

        @Override
        public org.apache.mesos.Protos.Resource proto0() {
            org.apache.mesos.Protos.Resource.Builder builder = org.apache.mesos.Protos.Resource.newBuilder();
            builder.setName(name);

            builder.setType(org.apache.mesos.Protos.Value.Type.valueOf(type.name()));
            if (scalar != null) builder.setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder().setValue(scalar));
            if (ranges != null) builder.setRanges(ranges0(ranges));

            builder.setRole(role);
            return builder.build();
        }

        @Override
        public Resource proto0(GeneratedMessage message) {
            org.apache.mesos.Protos.Resource resource = (org.apache.mesos.Protos.Resource) message;

            name = resource.getName();
            type = Type.valueOf(resource.getType().name());

            if (resource.hasScalar()) scalar = resource.getScalar().getValue();
            if (resource.hasRanges()) ranges = ranges0(resource.getRanges());

            role = resource.getRole();
            return this;
        }

        @Override
        public org.apache.mesos.v1.Protos.Resource proto1() {
            org.apache.mesos.v1.Protos.Resource.Builder builder = org.apache.mesos.v1.Protos.Resource.newBuilder();
            builder.setName(name);

            builder.setType(org.apache.mesos.v1.Protos.Value.Type.valueOf(type.name()));
            if (scalar != null) builder.setScalar(org.apache.mesos.v1.Protos.Value.Scalar.newBuilder().setValue(scalar));
            if (ranges != null) builder.setRanges(ranges1(ranges));

            builder.setRole(role);
            return builder.build();
        }

        @Override
        public Resource proto1(GeneratedMessage message) {
            org.apache.mesos.v1.Protos.Resource resource = (org.apache.mesos.v1.Protos.Resource) message;

            name = resource.getName();
            type = Type.valueOf(resource.getType().name());

            if (resource.hasScalar()) scalar = resource.getScalar().getValue();
            if (resource.hasRanges()) ranges = ranges1(resource.getRanges());

            role = resource.getRole();
            return this;
        }
    }

    public static class Attribute extends Base {
        private String name;
        private Type type;

        private Double scalar;
        private List<Range> ranges;
        private String text;

        public String name() { return name; }
        public Attribute name(String name) { this.name = name; return this; }

        public Type type() { return type; }
        public Attribute type(Type type) { this.type = type; return this; }

        public Double scalar() { return scalar; }
        public Attribute scalar(Double scalar) { this.scalar = scalar; return this; }

        public List<Range> ranges() { return Collections.unmodifiableList(ranges); }
        public Attribute ranges(List<Range> ranges) { this.ranges = ranges != null ? new ArrayList<>(ranges) : null; return this; }

        public String text() { return text; }
        public Attribute text(String text) { this.text = text; return this; }

        @Override
        public org.apache.mesos.Protos.Attribute proto0() {
            org.apache.mesos.Protos.Attribute.Builder builder = org.apache.mesos.Protos.Attribute.newBuilder();
            builder.setName(name);
            builder.setType(org.apache.mesos.Protos.Value.Type.valueOf(type.name()));

            if (scalar != null) builder.setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder().setValue(scalar));
            if (ranges != null) builder.setRanges(ranges0(ranges));
            if (text != null) builder.setText(org.apache.mesos.Protos.Value.Text.newBuilder().setValue(text));

            return builder.build();
        }

        @Override
        public Attribute proto0(GeneratedMessage message) {
            org.apache.mesos.Protos.Attribute attribute = (org.apache.mesos.Protos.Attribute) message;
            name = attribute.getName();
            type = Type.valueOf(attribute.getType().name());

            if (attribute.hasScalar()) scalar = attribute.getScalar().getValue();
            if (attribute.hasRanges()) ranges = ranges0(attribute.getRanges());
            if (attribute.hasText()) text = attribute.getText().getValue();

            return this;
        }

        @Override
        public org.apache.mesos.v1.Protos.Attribute proto1() {
            org.apache.mesos.v1.Protos.Attribute.Builder builder = org.apache.mesos.v1.Protos.Attribute.newBuilder();
            builder.setName(name);
            builder.setType(org.apache.mesos.v1.Protos.Value.Type.valueOf(type.name()));

            if (scalar != null) builder.setScalar(org.apache.mesos.v1.Protos.Value.Scalar.newBuilder().setValue(scalar));
            if (ranges != null) builder.setRanges(ranges1(ranges));
            if (text != null) builder.setText(org.apache.mesos.v1.Protos.Value.Text.newBuilder().setValue(text));

            return builder.build();
        }

        @Override
        public Attribute proto1(GeneratedMessage message) {
            org.apache.mesos.v1.Protos.Attribute attribute = (org.apache.mesos.v1.Protos.Attribute) message;
            name = attribute.getName();
            type = Type.valueOf(attribute.getType().name());

            if (attribute.hasScalar()) scalar = attribute.getScalar().getValue();
            if (attribute.hasRanges()) ranges = ranges1(attribute.getRanges());
            if (attribute.hasText()) text = attribute.getText().getValue();

            return this;
        }
    }

    private static org.apache.mesos.Protos.Value.Ranges ranges0(List<Range> ranges) {
        List<org.apache.mesos.Protos.Value.Range> result = new ArrayList<>();
        for (Range range : ranges)
            result.add(org.apache.mesos.Protos.Value.Range.newBuilder().setBegin(range.start()).setEnd(range.end()).build());
        return org.apache.mesos.Protos.Value.Ranges.newBuilder().addAllRange(result).build();
    }

    private static List<Range> ranges0(org.apache.mesos.Protos.Value.Ranges ranges) {
        List<Range> result = new ArrayList<>();

        for (org.apache.mesos.Protos.Value.Range range : ranges.getRangeList())
            result.add(new Range((int)range.getBegin(), (int)range.getEnd()));

        return result;
    }

    private static org.apache.mesos.v1.Protos.Value.Ranges ranges1(List<Range> ranges) {
        List<org.apache.mesos.v1.Protos.Value.Range> result = new ArrayList<>();
        for (Range range : ranges)
            result.add(org.apache.mesos.v1.Protos.Value.Range.newBuilder().setBegin(range.start()).setEnd(range.end()).build());
        return org.apache.mesos.v1.Protos.Value.Ranges.newBuilder().addAllRange(result).build();
    }

    private static List<Range> ranges1(org.apache.mesos.v1.Protos.Value.Ranges ranges) {
        List<Range> result = new ArrayList<>();

        for (org.apache.mesos.v1.Protos.Value.Range range : ranges.getRangeList())
            result.add(new Range((int)range.getBegin(), (int)range.getEnd()));

        return result;
    }

    public enum Type {
        SCALAR, RANGES, SET, TEXT
    }

    public static class Task extends Base {
        private String name;
        private String id;
        private String slaveId;

        private List<Resource> resources = new ArrayList<>();
        private Executor executor;
        private Command command;

        private byte[] data;

        public String name() { return name; }
        public Task name(String name) { this.name = name; return this; }

        public String id() { return id; }
        public Task id(String id) { this.id = id; return this; }

        public String slaveId() { return slaveId; }
        public Task slaveId(String id) { slaveId = id; return this; }


        public List<Resource> resources() { return Collections.unmodifiableList(resources); }
        public Task resources(List<Resource> resources) { this.resources.clear(); this.resources.addAll(resources); return this; }
        public Task resources(Resource ... resources) { return resources(Arrays.asList(resources)); }

        public Executor executor() { return executor; }
        public Task executor(Executor executor) { this.executor = executor; return this; }

        public Command command() { return command; }
        public Task command(Command command) { this.command = command; return this; }

        public byte[] data() { return data; }
        public Task data(byte[] data) { this.data = data; return this; }

        @Override
        public org.apache.mesos.Protos.TaskInfo proto0() {
            org.apache.mesos.Protos.TaskInfo.Builder builder = org.apache.mesos.Protos.TaskInfo.newBuilder();

            builder.setName(name);
            builder.setTaskId(org.apache.mesos.Protos.TaskID.newBuilder().setValue(id));
            builder.setSlaveId(org.apache.mesos.Protos.SlaveID.newBuilder().setValue(slaveId));

            List<org.apache.mesos.Protos.Resource> r = new ArrayList<>();
            for (Resource resource : resources) r.add(resource.proto0());
            builder.addAllResources(r);

            if (executor != null) builder.setExecutor(executor.proto0());
            if (command != null) builder.setCommand(command.proto0());

            if (data != null) builder.setData(ByteString.copyFrom(data));
            return builder.build();
        }

        @Override
        public Task proto0(GeneratedMessage message) {
            org.apache.mesos.Protos.TaskInfo task = (org.apache.mesos.Protos.TaskInfo) message;

            name = task.getName();
            id = task.getTaskId().getValue();
            slaveId = task.getSlaveId().getValue();

            resources.clear();
            for (org.apache.mesos.Protos.Resource resource : task.getResourcesList())
                resources.add(new Resource().proto0(resource));

            if (task.hasExecutor()) executor = new Executor().proto0(task.getExecutor());
            if (task.hasCommand()) command = new Command().proto0(task.getCommand());

            if (task.hasData()) data = task.getData().toByteArray();
            return this;
        }

        @Override
        public GeneratedMessage proto1() {
            org.apache.mesos.v1.Protos.TaskInfo.Builder builder = org.apache.mesos.v1.Protos.TaskInfo.newBuilder();

            builder.setName(name);
            builder.setTaskId(org.apache.mesos.v1.Protos.TaskID.newBuilder().setValue(id));
            builder.setAgentId(org.apache.mesos.v1.Protos.AgentID.newBuilder().setValue(slaveId));

            List<org.apache.mesos.v1.Protos.Resource> r = new ArrayList<>();
            for (Resource resource : resources) r.add(resource.proto1());
            builder.addAllResources(r);

            if (executor != null) builder.setExecutor(executor.proto1());
            if (command != null) builder.setCommand(command.proto1());

            if (data != null) builder.setData(ByteString.copyFrom(data));
            return builder.build();
        }

        @Override
        public Base proto1(GeneratedMessage message) {
            org.apache.mesos.v1.Protos.TaskInfo task = (org.apache.mesos.v1.Protos.TaskInfo) message;

            name = task.getName();
            id = task.getTaskId().getValue();
            slaveId = task.getAgentId().getValue();

            resources.clear();
            for (org.apache.mesos.v1.Protos.Resource resource : task.getResourcesList())
                resources.add(new Resource().proto1(resource));

            if (task.hasExecutor()) executor = new Executor().proto1(task.getExecutor());
            if (task.hasCommand()) command = new Command().proto1(task.getCommand());

            if (task.hasData()) data = task.getData().toByteArray();
            return this;
        }
    }

    private static class Executor extends Base {
        private String id;
        private String name;
        private String frameworkId;

        private Command command;
        private byte[] data;

        public String id() { return id; }
        public Executor id(String id) { this.id = id; return this; }

        public String name() { return name; }
        public Executor name(String name) { this.name = name; return this; }

        public String frameworkId() { return frameworkId; }
        public Executor frameworkId(String id) { frameworkId = id; return this; }


        public Command command() { return command; }
        public Executor command(Command command) { this.command = command; return this; }

        public byte[] data() { return data; }
        public Executor data(byte[] data) { this.data = data; return this; }

        @Override
        public org.apache.mesos.Protos.ExecutorInfo proto0() {
            org.apache.mesos.Protos.ExecutorInfo.Builder builder = org.apache.mesos.Protos.ExecutorInfo.newBuilder();

            builder.setExecutorId(org.apache.mesos.Protos.ExecutorID.newBuilder().setValue(id));
            if (name != null) builder.setName(name);
            builder.setFrameworkId(org.apache.mesos.Protos.FrameworkID.newBuilder().setValue(frameworkId));

            if (command != null) builder.setCommand(command.proto0());
            if (data != null) builder.setData(ByteString.copyFrom(data));

            return builder.build();
        }

        @Override
        public Executor proto0(GeneratedMessage message) {
            org.apache.mesos.Protos.ExecutorInfo executor = (org.apache.mesos.Protos.ExecutorInfo) message;

            id = executor.getExecutorId().getValue();
            if (executor.hasName()) name = executor.getName();
            frameworkId = executor.getFrameworkId().getValue();

            if (executor.hasCommand()) command = new Command().proto0(executor.getCommand());
            if (executor.hasData()) data = executor.getData().toByteArray();

            return this;
        }

        @Override
        public org.apache.mesos.v1.Protos.ExecutorInfo proto1() {
            org.apache.mesos.v1.Protos.ExecutorInfo.Builder builder = org.apache.mesos.v1.Protos.ExecutorInfo.newBuilder();

            builder.setExecutorId(org.apache.mesos.v1.Protos.ExecutorID.newBuilder().setValue(id));
            if (name != null) builder.setName(name);
            builder.setFrameworkId(org.apache.mesos.v1.Protos.FrameworkID.newBuilder().setValue(frameworkId));

            if (command != null) builder.setCommand(command.proto1());
            if (data != null) builder.setData(ByteString.copyFrom(data));

            return builder.build();
        }

        @Override
        public Executor proto1(GeneratedMessage message) {
            org.apache.mesos.v1.Protos.ExecutorInfo executor = (org.apache.mesos.v1.Protos.ExecutorInfo) message;

            id = executor.getExecutorId().getValue();
            if (executor.hasName()) name = executor.getName();
            frameworkId = executor.getFrameworkId().getValue();

            if (executor.hasCommand()) command = new Command().proto1(executor.getCommand());
            if (executor.hasData()) data = executor.getData().toByteArray();

            return this;
        }
    }

    private static class Command extends Base {
        private String value;
        private List<URI> uris = new ArrayList<>();
        private Map<String, String> environment = new LinkedHashMap<>();

        public String value() { return value; }
        public Command value(String value) { this.value = value; return this; }

        public List<URI> uris() { return Collections.unmodifiableList(uris); }
        public Command uris(URI ... uris) { return uris(Arrays.asList(uris)); }
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
}
