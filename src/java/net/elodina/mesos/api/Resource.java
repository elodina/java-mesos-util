package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Resource extends Base {
    private String name;
    private Type type;

    private Double scalar;
    private List<Range> ranges;

    private String role = "*";

    public Resource() {}
    public Resource(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String name() { return name; }
    public Resource name(String name) { this.name = name; return this; }


    public Type type() { return type; }
    public Resource type(Type type) { this.type = type; return this; }

    public Double scalar() { return scalar; }
    public Resource scalar(Double scalar) { this.scalar = scalar; return this; }

    public List<Range> ranges() { return ranges != null ? Collections.unmodifiableList(ranges) : null; }
    public Resource ranges(List<Range> ranges) { this.ranges = ranges != null ? new ArrayList<>(ranges) : null; return this; }


    public String role() { return role; }
    public Resource role(String role) { this.role = role; return this; }

    @Override
    public org.apache.mesos.Protos.Resource proto0() {
        org.apache.mesos.Protos.Resource.Builder builder = org.apache.mesos.Protos.Resource.newBuilder();
        builder.setName(name);

        builder.setType(org.apache.mesos.Protos.Value.Type.valueOf(type.name()));
        if (scalar != null) builder.setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder().setValue(scalar));
        if (ranges != null) builder.setRanges(ranges0(ranges));

        builder.setRole(role);
        return builder.build();
    }

    @Override
    public Resource proto0(GeneratedMessage message) {
        org.apache.mesos.Protos.Resource resource = (org.apache.mesos.Protos.Resource) message;

        name = resource.getName();
        type = Type.valueOf(resource.getType().name());

        if (resource.hasScalar()) scalar = resource.getScalar().getValue();
        if (resource.hasRanges()) ranges = ranges0(resource.getRanges());

        role = resource.getRole();
        return this;
    }

    @Override
    public org.apache.mesos.v1.Protos.Resource proto1() {
        org.apache.mesos.v1.Protos.Resource.Builder builder = org.apache.mesos.v1.Protos.Resource.newBuilder();
        builder.setName(name);

        builder.setType(org.apache.mesos.v1.Protos.Value.Type.valueOf(type.name()));
        if (scalar != null) builder.setScalar(org.apache.mesos.v1.Protos.Value.Scalar.newBuilder().setValue(scalar));
        if (ranges != null) builder.setRanges(ranges1(ranges));

        builder.setRole(role);
        return builder.build();
    }

    @Override
    public Resource proto1(GeneratedMessage message) {
        org.apache.mesos.v1.Protos.Resource resource = (org.apache.mesos.v1.Protos.Resource) message;

        name = resource.getName();
        type = Type.valueOf(resource.getType().name());

        if (resource.hasScalar()) scalar = resource.getScalar().getValue();
        if (resource.hasRanges()) ranges = ranges1(resource.getRanges());

        role = resource.getRole();
        return this;
    }
}
