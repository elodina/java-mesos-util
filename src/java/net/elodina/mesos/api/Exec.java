package net.elodina.mesos.api;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Exec() {}
    public Exec(String s) { parse(s); }

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
