package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Offer extends Base {
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
    public Offer resources(Resource... resources) { return resources(Arrays.asList(resources)); }

    public List<Attribute> attributes() { return Collections.unmodifiableList(attributes); }
    public Offer attributes(List<Attribute> attributes) { this.attributes.clear(); this.attributes.addAll(attributes); return this; }
    public Offer attributes(Attribute... attributes) { attributes(Arrays.asList(attributes)); return this; }


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
