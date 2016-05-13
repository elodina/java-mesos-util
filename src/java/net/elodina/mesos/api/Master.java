package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Strings;
import net.elodina.mesos.util.Version;

import java.util.Map;

public class Master extends Message {
    private String id;

    private String ip;
    private int port = -1;
    private String hostname;

    private Version version;

    public String id() { return id; }
    public Master id(String id) { this.id = id; return this; }


    public String hostname() { return hostname; }
    public Master hostname(String hostname) { this.hostname = hostname; return this; }

    public String ip() { return ip; }
    public Master ip(String ip) { this.ip = ip; return this; }

    public long ipAsInt() {
        if (ip == null) return 0;

        long v = 0;
        for (String n : ip.split("\\.")) {
            v <<= 8;
            v |= Integer.parseInt(n);
        }

        return v;
    }

    public Master ipAsInt(long ip) {
        if (ip == 0) { this.ip = null; return this; }

        String v = "";
        for (int i = 0; i < 4; i++) {
            if (!v.isEmpty()) v = "." + v;
            v = (ip & 0xff) + v;
            ip >>= 8;
        }

        this.ip = v;
        return this;
    }

    public int port() { return port; }
    public Master port(int port) { this.port = port; return this; }


    public Version version() { return version; }
    public Master version(Version version) { this.version = version; return this; }

    public Master() {}

    public Master(String s) {
        Map<String,String> values = Strings.parseMap(s, ',', ':');
        id = values.get("id");

        ip = values.get("ip");
        if (values.containsKey("port")) port = Integer.valueOf(values.get("port"));
        hostname = values.get("hostname");

        if (values.containsKey("version")) version = new Version(values.get("version"));
    }


    @Override
    public org.apache.mesos.Protos.MasterInfo proto0() {
        org.apache.mesos.Protos.MasterInfo.Builder builder = org.apache.mesos.Protos.MasterInfo.newBuilder();
        builder.setId(id);

        builder.setIp((int)ipAsInt());
        builder.setPort(port);
        if (hostname != null) builder.setHostname(hostname);

        if (version != null) builder.setVersion("" + version);

        return builder.build();
    }

    @Override
    public Master proto0(GeneratedMessage message) {
        org.apache.mesos.Protos.MasterInfo master = (org.apache.mesos.Protos.MasterInfo)message;
        id = master.getId();

        ipAsInt(master.getIp());
        port = master.getPort();
        if (master.hasHostname()) hostname = master.getHostname();

        if (master.hasVersion()) version = new Version(master.getVersion());

        return this;
    }

    @Override
    public org.apache.mesos.v1.Protos.MasterInfo proto1() {
        org.apache.mesos.v1.Protos.MasterInfo.Builder builder = org.apache.mesos.v1.Protos.MasterInfo.newBuilder();
        builder.setId(id);

        builder.setIp((int)ipAsInt());
        builder.setPort(port);
        if (hostname != null) builder.setHostname(hostname);

        if (version != null) builder.setVersion("" + version);
        return builder.build();
    }

    @Override
    public Master proto1(GeneratedMessage message) {
        org.apache.mesos.v1.Protos.MasterInfo master = (org.apache.mesos.v1.Protos.MasterInfo)message;
        id = master.getId();

        ipAsInt(master.getIp());
        port = master.getPort();
        if (master.hasHostname()) hostname = master.getHostname();

        if (master.hasVersion()) version = new Version(master.getVersion());
        return this;
    }

    @Override
    public String toString(boolean _short) {
        String s = "";
        if (id != null) s += "id:" + shortId(id, _short);

        if (ip != null) s += ", ip:" + ip;
        if (port != -1) s += ", port:" + port;
        if (hostname != null) s += ", hostname:" + hostname;

        if (version != null) s += ", version:" + version;
        return s;
    }
}
