package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Period;
import net.elodina.mesos.util.Strings;

import java.util.Map;

public class Framework extends Message {
    private String id;
    private String name;

    private String user;
    private Period timeout;

    private boolean checkpoint;
    private String role = "*";

    private String principal;


    public Framework() {}
    public Framework(String s) {
        Map<String,String> values = Strings.parseMap(s, ',', ':');
        id = values.get("id");
        name = values.get("name");

        user = values.get("user");
        if (values.containsKey("timeout")) timeout = new Period(values.get("timeout"));

        if (values.containsKey("checkpoint")) checkpoint = Boolean.parseBoolean(values.get("checkpoint"));
        if (values.containsKey("role")) role = values.get("role");

        principal = values.get("principal");
    }


    public String id() { return id; }
    public Framework id(String id) { this.id = id; return this; }

    public String name() { return name; }
    public Framework name(String name) { this.name = name; return this; }


    public String user() { return user; }
    public Framework user(String user) { this.user = user; return this; }

    public Period timeout() { return timeout; }
    public Framework timeout(Period timeout) { this.timeout = timeout; return this; }


    public boolean checkpoint() { return checkpoint; }
    public Framework checkpoint(boolean checkpoint) { this.checkpoint = checkpoint; return this; }

    public String role() { return role; }
    public Framework role(String role) { this.role = role; return this; }


    public String principal() { return principal; }
    public Framework principal(String principal) { this.principal = principal; return this; }

    @Override
    public org.apache.mesos.Protos.FrameworkInfo proto0() {
        org.apache.mesos.Protos.FrameworkInfo.Builder builder = org.apache.mesos.Protos.FrameworkInfo.newBuilder();

        if (id != null) builder.setId(org.apache.mesos.Protos.FrameworkID.newBuilder().setValue(id));
        if (name != null) builder.setName(name);

        if (user != null) builder.setUser(user);
        if (timeout != null) builder.setFailoverTimeout(timeout.ms() / 1000);

        builder.setCheckpoint(checkpoint);
        builder.setRole(role);

        if (principal != null) builder.setPrincipal(principal);
        return builder.build();
    }

    @Override
    public Framework proto0(GeneratedMessage message) {
        org.apache.mesos.Protos.FrameworkInfo framework = (org.apache.mesos.Protos.FrameworkInfo) message;

        if (framework.hasId()) id = framework.getId().getValue();
        if (framework.hasName()) name = framework.getName();

        if (framework.hasUser()) user = framework.getUser();
        if (framework.hasFailoverTimeout()) timeout = new Period((long)framework.getFailoverTimeout() + "s");

        checkpoint = framework.getCheckpoint();
        role = framework.getRole();

        if (framework.hasPrincipal()) principal = framework.getPrincipal();
        return this;
    }

    @Override
    public org.apache.mesos.v1.Protos.FrameworkInfo proto1() {
        org.apache.mesos.v1.Protos.FrameworkInfo.Builder builder = org.apache.mesos.v1.Protos.FrameworkInfo.newBuilder();

        if (id != null) builder.setId(org.apache.mesos.v1.Protos.FrameworkID.newBuilder().setValue(id));
        if (name != null) builder.setName(name);

        if (user != null) builder.setUser(user);
        if (timeout != null) builder.setFailoverTimeout(timeout.ms() / 1000);

        builder.setCheckpoint(checkpoint);
        builder.setRole(role);

        if (principal != null) builder.setPrincipal(principal);
        return builder.build();
    }

    @Override
    public Framework proto1(GeneratedMessage message) {
        org.apache.mesos.v1.Protos.FrameworkInfo framework = (org.apache.mesos.v1.Protos.FrameworkInfo) message;

        if (framework.hasId()) id = framework.getId().getValue();
        if (framework.hasName()) name = framework.getName();

        if (framework.hasUser()) user = framework.getUser();
        if (framework.hasFailoverTimeout()) timeout = new Period((long)framework.getFailoverTimeout() + "s");

        checkpoint = framework.getCheckpoint();
        role = framework.getRole();

        if (framework.hasPrincipal()) principal = framework.getPrincipal();
        return this;
    }

    @Override
    public String toString(boolean _short) {
        String s = "";

        if (id != null) s += "id:" + shortId(id, _short);
        if (name != null) s += ", name:" + name;

        if (user != null) s += ", user:" + user;
        if (timeout != null) s += ", timeout:" + timeout;

        s += ", checkpoint:" + checkpoint;
        s += ", role:" + role;

        if (principal != null) s += ", principal:" + principal;
        return s.startsWith(", ") ? s.substring(2) : s;
    }
}
