package net.elodina.mesos.api;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Task extends Base {
    private String name;
    private String id;
    private String slaveId;

    private List<Resource> resources = new ArrayList<>();
    private Exec exec;
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

    public Exec exec() { return exec; }
    public Task exec(Exec exec) { this.exec = exec; return this; }

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

        if (exec != null) builder.setExecutor(exec.proto0());
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

        if (task.hasExecutor()) exec = new Exec().proto0(task.getExecutor());
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

        if (exec != null) builder.setExecutor(exec.proto1());
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

        if (task.hasExecutor()) exec = new Exec().proto1(task.getExecutor());
        if (task.hasCommand()) command = new Command().proto1(task.getCommand());

        if (task.hasData()) data = task.getData().toByteArray();
        return this;
    }
}
