package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Strings;

import java.util.*;

public class Offer extends Message {
    private String id;
    private String frameworkId;
    private String slaveId;
    private String hostname;

    public Offer() { }
    public Offer(String expr) { parse(expr); }

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
    public Offer resources(Resource... resources) { return resources(Arrays.asList(resources)); }

    public List<Attribute> attributes() { return Collections.unmodifiableList(attributes); }
    public Offer attributes(List<Attribute> attributes) { this.attributes.clear(); this.attributes.addAll(attributes); return this; }
    public Offer attributes(Attribute... attributes) { attributes(Arrays.asList(attributes)); return this; }


    @Override
    public org.apache.mesos.Protos.Offer proto0() {
        org.apache.mesos.Protos.Offer.Builder builder = org.apache.mesos.Protos.Offer.newBuilder();
        builder.setId(org.apache.mesos.Protos.OfferID.newBuilder().setValue(id));
        builder.setFrameworkId(org.apache.mesos.Protos.FrameworkID.newBuilder().setValue(frameworkId));
        builder.setSlaveId(org.apache.mesos.Protos.SlaveID.newBuilder().setValue(slaveId));
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
        builder.setAgentId(org.apache.mesos.v1.Protos.AgentID.newBuilder().setValue(slaveId));
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
    public Offer proto1(GeneratedMessage message) {
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

    public Offer parse(String s) {
        List<String> parts = new ArrayList<>();

        StringBuilder buffer = new StringBuilder();
        boolean inExpr = false;
        for (char c : s.toCharArray()) {
            if (c == ',' && !inExpr) {
                parts.add("" + buffer);
                buffer.setLength(0);
            } else {
                if (c == '[') inExpr = true;
                else if (c == ']') inExpr = false;
                buffer.append(c);
            }
        }
        if (inExpr) throw new IllegalArgumentException(s);
        if (buffer.length() > 0) parts.add("" + buffer);

        Map<String, String> values = new HashMap<>();
        for (String part : parts) {
            int colon = part.indexOf(":");
            if (colon == -1) throw new IllegalArgumentException(s);

            String name = part.substring(0, colon).trim();
            String value = part.substring(colon + 1).trim();
            values.put(name, value);
        }

        id = values.get("id");
        frameworkId = values.get("frameworkId");
        slaveId = values.get("slaveId");
        hostname = values.get("hostname");

        String resValue = values.get("resources");
        if (resValue != null) resources = Resource.parse(resValue.substring(1, resValue.length() - 1));

        String attrsValue = values.get("attributes");
        if (attrsValue != null) attributes = Attribute.parse(attrsValue.substring(1, attrsValue.length() - 1));

        return this;
    }

    @Override
    public String toString(boolean _short) {
        List<String> s = new ArrayList<>();

        if (id != null) s.add("id:" + shortId(id, _short));
        if (frameworkId != null && !_short) s.add("frameworkId:" + frameworkId);
        if (slaveId != null) s.add("slaveId:" + shortId(slaveId, _short));
        if (hostname != null) s.add("hostname:" + hostname);

        if (!resources.isEmpty()) s.add("resources:[" + Resource.format(resources) + "]");
        if (!attributes.isEmpty()) s.add("attributes:[" + Attribute.format(attributes) + "]");

        return Strings.join(s, ", ");
    }
}
