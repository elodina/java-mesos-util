package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Strings;

import java.util.Map;

public class Slave extends Message {
    private String id;
    private String hostname;
    private int port = -1;

    public Slave() {}

    public Slave(String s) {
        Map<String,String> values = Strings.parseMap(s, ',', ':');
        id = values.get("id");
        hostname = values.get("hostname");
        if (values.containsKey("port")) port = Integer.valueOf(values.get("port"));
    }

    public String id() { return id; }
    public Slave id(String id) { this.id = id; return this; }

    public String hostname() { return hostname; }
    public Slave hostname(String hostname) { this.hostname = hostname; return this; }

    public int port() { return port; }
    public Slave port(int port) { this.port = port; return this; }


    @Override
    public org.apache.mesos.Protos.SlaveInfo proto0() {
        org.apache.mesos.Protos.SlaveInfo.Builder builder = org.apache.mesos.Protos.SlaveInfo.newBuilder();
        builder.setId(org.apache.mesos.Protos.SlaveID.newBuilder().setValue(id));

        if (hostname != null) builder.setHostname(hostname);
        if (port != -1) builder.setPort(port);

        return builder.build();
    }

    @Override
    public Slave proto0(GeneratedMessage message) {
        org.apache.mesos.Protos.SlaveInfo slave = (org.apache.mesos.Protos.SlaveInfo)message;
        if (slave.hasId()) id = slave.getId().getValue();

        if (slave.hasPort()) port = slave.getPort();
        if (slave.hasHostname()) hostname = slave.getHostname();

        return this;
    }

    @Override
    public org.apache.mesos.v1.Protos.AgentInfo proto1() {
        org.apache.mesos.v1.Protos.AgentInfo.Builder builder = org.apache.mesos.v1.Protos.AgentInfo.newBuilder();
        builder.setId(org.apache.mesos.v1.Protos.AgentID.newBuilder().setValue(id));

        if (hostname != null) builder.setHostname(hostname);
        if (port != -1) builder.setPort(port);

        return builder.build();
    }

    @Override
    public Slave proto1(GeneratedMessage message) {
        org.apache.mesos.v1.Protos.AgentInfo slave = (org.apache.mesos.v1.Protos.AgentInfo)message;
        if (slave.hasId()) id = slave.getId().getValue();

        if (slave.hasPort()) port = slave.getPort();
        if (slave.hasHostname()) hostname = slave.getHostname();

        return this;
    }

    @Override
    public String toString(boolean _short) {
        String s = "";
        if (id != null) s += "id:" + shortId(id, _short);

        if (port != -1) s += ", port:" + port;
        if (hostname != null) s += ", hostname:" + hostname;
        return s.startsWith(", ") ? s.substring(2) : s;
    }
}
