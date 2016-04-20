package net.elodina.mesos.api;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Strings;

import java.util.*;

public class Task extends Base {
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

    public static class Executor extends Base {
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

        public Executor() {}
        public Executor(String s) { parse(s); }

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

        public void parse(String s) {
            List<String> parts = new ArrayList<>();

            StringBuilder buffer = new StringBuilder();
            int brackets = 0;
            for (char c : s.toCharArray()) {
                if (c == ',' && brackets == 0) {
                    parts.add("" + buffer);
                    buffer.setLength(0);
                } else {
                    if (c == '[') brackets ++;
                    else if (c == ']') brackets --;
                    buffer.append(c);
                }
            }
            if (brackets != 0) throw new IllegalArgumentException(s);
            if (buffer.length() > 0) parts.add("" + buffer);

            Map<String, String> values = new HashMap<>();
            for (String part : parts) {
                int colon = part.indexOf(":");
                if (colon == -1) throw new IllegalArgumentException(s);

                String name = part.substring(0, colon);
                String value = part.substring(colon + 1);
                values.put(name.trim(), value.trim());
            }

            id = values.get("id");
            name = values.get("name");
            frameworkId = values.get("frameworkId");

            String commandVal = values.get("command");
            if (commandVal != null) command = new Command(commandVal.substring(1, commandVal.length() - 1));

            if (values.containsKey("data")) data = Strings.parseHex(values.get("data"));

        }

        public String toString() {
            List<String> s = new ArrayList<>();

            if (id != null) s.add("id:" + id);
            if (name != null) s.add("name:" + name);
            if (frameworkId != null) s.add("frameworkId:" + frameworkId);

            if (command != null) s.add("command:[" + command + "]");
            if (data != null) s.add("data:" + Strings.formatHex(data));

            return Strings.join(s, ", ");
        }
    }
}