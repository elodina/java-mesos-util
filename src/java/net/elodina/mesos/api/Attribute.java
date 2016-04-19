package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Attribute extends Base {
    private String name;
    private Type type;

    private Double scalar;
    private List<Range> ranges;
    private String text;

    public String name() { return name; }
    public Attribute name(String name) { this.name = name; return this; }

    public Type type() { return type; }
    public Attribute type(Type type) { this.type = type; return this; }

    public Double scalar() { return scalar; }
    public Attribute scalar(Double scalar) { this.scalar = scalar; return this; }

    public List<Range> ranges() { return Collections.unmodifiableList(ranges); }
    public Attribute ranges(List<Range> ranges) { this.ranges = ranges != null ? new ArrayList<>(ranges) : null; return this; }

    public String text() { return text; }
    public Attribute text(String text) { this.text = text; return this; }

    @Override
    public org.apache.mesos.Protos.Attribute proto0() {
        org.apache.mesos.Protos.Attribute.Builder builder = org.apache.mesos.Protos.Attribute.newBuilder();
        builder.setName(name);
        builder.setType(org.apache.mesos.Protos.Value.Type.valueOf(type.name()));

        if (scalar != null) builder.setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder().setValue(scalar));
        if (ranges != null) builder.setRanges(ranges0(ranges));
        if (text != null) builder.setText(org.apache.mesos.Protos.Value.Text.newBuilder().setValue(text));

        return builder.build();
    }

    @Override
    public Attribute proto0(GeneratedMessage message) {
        org.apache.mesos.Protos.Attribute attribute = (org.apache.mesos.Protos.Attribute) message;
        name = attribute.getName();
        type = Type.valueOf(attribute.getType().name());

        if (attribute.hasScalar()) scalar = attribute.getScalar().getValue();
        if (attribute.hasRanges()) ranges = ranges0(attribute.getRanges());
        if (attribute.hasText()) text = attribute.getText().getValue();

        return this;
    }

    @Override
    public org.apache.mesos.v1.Protos.Attribute proto1() {
        org.apache.mesos.v1.Protos.Attribute.Builder builder = org.apache.mesos.v1.Protos.Attribute.newBuilder();
        builder.setName(name);
        builder.setType(org.apache.mesos.v1.Protos.Value.Type.valueOf(type.name()));

        if (scalar != null) builder.setScalar(org.apache.mesos.v1.Protos.Value.Scalar.newBuilder().setValue(scalar));
        if (ranges != null) builder.setRanges(ranges1(ranges));
        if (text != null) builder.setText(org.apache.mesos.v1.Protos.Value.Text.newBuilder().setValue(text));

        return builder.build();
    }

    @Override
    public Attribute proto1(GeneratedMessage message) {
        org.apache.mesos.v1.Protos.Attribute attribute = (org.apache.mesos.v1.Protos.Attribute) message;
        name = attribute.getName();
        type = Type.valueOf(attribute.getType().name());

        if (attribute.hasScalar()) scalar = attribute.getScalar().getValue();
        if (attribute.hasRanges()) ranges = ranges1(attribute.getRanges());
        if (attribute.hasText()) text = attribute.getText().getValue();

        return this;
    }
}
