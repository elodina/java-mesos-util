package net.elodina.mesos.api;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;

public class Exec extends Base {
    private String id;
    private String name;
    private String frameworkId;

    private Command command;
    private byte[] data;

    public String id() { return id; }
    public Exec id(String id) { this.id = id; return this; }

    public String name() { return name; }
    public Exec name(String name) { this.name = name; return this; }

    public String frameworkId() { return frameworkId; }
    public Exec frameworkId(String id) { frameworkId = id; return this; }


    public Command command() { return command; }
    public Exec command(Command command) { this.command = command; return this; }

    public byte[] data() { return data; }
    public Exec data(byte[] data) { this.data = data; return this; }

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
    public Exec proto0(GeneratedMessage message) {
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
    public Exec proto1(GeneratedMessage message) {
        org.apache.mesos.v1.Protos.ExecutorInfo executor = (org.apache.mesos.v1.Protos.ExecutorInfo) message;

        id = executor.getExecutorId().getValue();
        if (executor.hasName()) name = executor.getName();
        frameworkId = executor.getFrameworkId().getValue();

        if (executor.hasCommand()) command = new Command().proto1(executor.getCommand());
        if (executor.hasData()) data = executor.getData().toByteArray();

        return this;
    }
}
