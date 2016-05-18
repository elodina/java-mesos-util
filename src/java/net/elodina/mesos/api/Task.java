package net.elodina.mesos.api;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Strings;

import java.util.*;

public class Task extends Message {
    private String id;
    private String name;
    private String slaveId;

    private List<Resource> resources = new ArrayList<>();
    private Executor executor;
    private Command command;

    private byte[] data;

    public Task() {}
    public Task(String s) { parse(s); }

    public String id() { return id; }
    public Task id(String id) { this.id = id; return this; }

    public String name() { return name; }
    public Task name(String name) { this.name = name; return this; }

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
    public org.apache.mesos.v1.Protos.TaskInfo proto1() {
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
    public Task proto1(GeneratedMessage message) {
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

    private void parse(String s) {
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
        slaveId = values.get("slaveId");

        String resourcesVal = values.get("resources");
        if (resourcesVal != null) resources = Resource.parse(resourcesVal.substring(1, resourcesVal.length() - 1));

        String executorVal = values.get("executor");
        if (executorVal != null) executor = new Executor(executorVal.substring(1, executorVal.length() - 1));

        String commandVal = values.get("command");
        if (commandVal != null) command = new Command(commandVal.substring(1, commandVal.length() - 1));

        if (values.containsKey("data")) data = Strings.parseHex(values.get("data"));
    }

    @Override
    public String toString(boolean _short) {
        List<String> s = new ArrayList<>();

        if (id != null) s.add("id:" + shortId(id, _short));
        if (name != null) s.add("name:" + name);
        if (slaveId != null) s.add("slaveId:" + shortId(slaveId, _short));

        if (!resources.isEmpty()) s.add("resources:[" + Resource.format(resources) + "]");
        if (executor != null) s.add("executor:[" + executor + "]");
        if (command != null) s.add("command:[" + command + "]");

        if (data != null) s.add("data:" + Strings.formatHex(data));
        return Strings.join(s, ", ");
    }

    public static class Status extends Message {
        private String id;
        private State state;

        private String message;
        private byte[] data;

        private String slaveId;
        private String executorId;


        public Status() {}

        public Status(String id, State state) {
            this.id = id;
            this.state = state;
        }

        public Status(String s) {
            Map<String, String> values = Strings.parseMap(s, ',', ':');
            id = values.get("id");
            if (values.containsKey("state")) state = State.valueOf(values.get("state").toUpperCase());

            message = values.get("message");
            if (values.containsKey("data")) data = Strings.parseHex(values.get("data"));

            slaveId = values.get("slaveId");
            executorId = values.get("executorId");
        }


        public String id() { return id; }
        public Status id(String id) { this.id = id; return this; }

        public State state() { return state; }
        public Status state(State state) { this.state = state; return this; }


        public String message() { return message; }
        public Status message(String message) { this.message = message; return this; }

        public byte[] data() { return data; }
        public Status data(byte[] data) { this.data = data; return this; }


        public String slaveId() { return slaveId; }
        public Status slaveId(String slaveId) { this.slaveId = slaveId; return this; }

        public String executorId() { return executorId; }
        public Status executorId(String executorId) { this.executorId = executorId; return this; }


        @Override
        public org.apache.mesos.Protos.TaskStatus proto0() {
            org.apache.mesos.Protos.TaskStatus.Builder builder = org.apache.mesos.Protos.TaskStatus.newBuilder();

            builder.setTaskId(org.apache.mesos.Protos.TaskID.newBuilder().setValue(id));
            if (state != null) builder.setState(org.apache.mesos.Protos.TaskState.valueOf("TASK_" + state.name()));

            if (message != null) builder.setMessage(message);
            if (data != null) builder.setData(ByteString.copyFrom(data));

            if (slaveId != null) builder.setSlaveId(org.apache.mesos.Protos.SlaveID.newBuilder().setValue(slaveId));
            if (executorId != null) builder.setExecutorId(org.apache.mesos.Protos.ExecutorID.newBuilder().setValue(executorId));

            return builder.build();
        }

        @Override
        public Status proto0(GeneratedMessage message) {
            org.apache.mesos.Protos.TaskStatus status = (org.apache.mesos.Protos.TaskStatus) message;

            id = status.getTaskId().getValue();
            state = State.valueOf(status.getState().name().substring("TASK_".length()));

            if (status.hasMessage()) this.message = status.getMessage();
            if (status.hasData()) data = status.getData().toByteArray();

            if (status.hasSlaveId()) slaveId = status.getSlaveId().getValue();
            if (status.hasExecutorId()) executorId = status.getExecutorId().getValue();

            return this;
        }

        @Override
        public org.apache.mesos.v1.Protos.TaskStatus proto1() {
            org.apache.mesos.v1.Protos.TaskStatus.Builder builder = org.apache.mesos.v1.Protos.TaskStatus.newBuilder();

            builder.setTaskId(org.apache.mesos.v1.Protos.TaskID.newBuilder().setValue(id));
            if (state != null) builder.setState(org.apache.mesos.v1.Protos.TaskState.valueOf("TASK_" + state.name()));

            if (message != null) builder.setMessage(message);
            if (data != null) builder.setData(ByteString.copyFrom(data));

            if (slaveId != null) builder.setAgentId(org.apache.mesos.v1.Protos.AgentID.newBuilder().setValue(slaveId));
            if (executorId != null) builder.setExecutorId(org.apache.mesos.v1.Protos.ExecutorID.newBuilder().setValue(executorId));

            return builder.build();
        }

        @Override
        public Status proto1(GeneratedMessage message) {
            org.apache.mesos.v1.Protos.TaskStatus status = (org.apache.mesos.v1.Protos.TaskStatus) message;

            id = status.getTaskId().getValue();
            state = State.valueOf(status.getState().name().substring("TASK_".length()));

            if (status.hasMessage()) this.message = status.getMessage();
            if (status.hasData()) data = status.getData().toByteArray();

            if (status.hasAgentId()) slaveId = status.getAgentId().getValue();
            if (status.hasExecutorId()) executorId = status.getExecutorId().getValue();

            return this;
        }

        public String toString(boolean _short) {
            String s = "";

            if (id != null) s += "id:" + shortId(id, _short);
            if (state != null) s += ", state:" + state.name().toLowerCase();

            if (message != null) s += ", message:" + message;
            if (data != null) s += ", data:" + Strings.formatHex(data);

            if (slaveId != null) s += ", slaveId:" + shortId(slaveId, _short);
            if (executorId != null) s += ", executorId:" + shortId(executorId, _short);

            return s.startsWith(", ") ? s.substring(2) : s;
        }
    }

    public static enum State {
        STAGING,  // Initial state. Framework status updates should not use.
        STARTING, // The task is being launched by the executor.
        RUNNING,

        // NOTE: This should only be sent when the framework has
        // the TASK_KILLING_STATE capability.
        KILLING,  // The task is being killed by the executor.

        FINISHED, // TERMINAL: The task finished successfully.
        FAILED,   // TERMINAL: The task failed to finish successfully.
        KILLED,   // TERMINAL: The task was killed by the executor.
        LOST,     // TERMINAL: The task failed but can be rescheduled.
        ERROR,    // TERMINAL: The task description contains an error.
    }

    public static class Executor extends Message {
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
            if (frameworkId != null) builder.setFrameworkId(org.apache.mesos.Protos.FrameworkID.newBuilder().setValue(frameworkId));

            if (command != null) builder.setCommand(command.proto0());
            if (data != null) builder.setData(ByteString.copyFrom(data));

            return builder.build();
        }

        @Override
        public Executor proto0(GeneratedMessage message) {
            org.apache.mesos.Protos.ExecutorInfo executor = (org.apache.mesos.Protos.ExecutorInfo) message;

            id = executor.getExecutorId().getValue();
            if (executor.hasName()) name = executor.getName();
            if (executor.hasFrameworkId()) frameworkId = executor.getFrameworkId().getValue();

            if (executor.hasCommand()) command = new Command().proto0(executor.getCommand());
            if (executor.hasData()) data = executor.getData().toByteArray();

            return this;
        }

        @Override
        public org.apache.mesos.v1.Protos.ExecutorInfo proto1() {
            org.apache.mesos.v1.Protos.ExecutorInfo.Builder builder = org.apache.mesos.v1.Protos.ExecutorInfo.newBuilder();

            builder.setExecutorId(org.apache.mesos.v1.Protos.ExecutorID.newBuilder().setValue(id));
            if (name != null) builder.setName(name);
            if (frameworkId != null) builder.setFrameworkId(org.apache.mesos.v1.Protos.FrameworkID.newBuilder().setValue(frameworkId));

            if (command != null) builder.setCommand(command.proto1());
            if (data != null) builder.setData(ByteString.copyFrom(data));

            return builder.build();
        }

        @Override
        public Executor proto1(GeneratedMessage message) {
            org.apache.mesos.v1.Protos.ExecutorInfo executor = (org.apache.mesos.v1.Protos.ExecutorInfo) message;

            id = executor.getExecutorId().getValue();
            if (executor.hasName()) name = executor.getName();
            if (executor.hasFrameworkId()) frameworkId = executor.getFrameworkId().getValue();

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

        public String toString(boolean _short) {
            List<String> s = new ArrayList<>();

            if (id != null) s.add("id:" + shortId(id, _short));
            if (name != null) s.add("name:" + name);
            if (frameworkId != null) s.add("frameworkId:" + shortId(frameworkId, _short));

            if (command != null) s.add("command:[" + command + "]");
            if (data != null) s.add("data:" + Strings.formatHex(data));

            return Strings.join(s, ", ");
        }
    }
}
