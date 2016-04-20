package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Strings;

import java.util.ArrayList;
import java.util.List;

public class Attribute extends Base {
    private String name;
    private Value value;

    public Attribute() {}
    public Attribute(String name, Value value) { this.name = name; this.value = value; }

    public Attribute(String s) {
        int eq = s.indexOf("=");
        if (eq == -1) throw new IllegalArgumentException(s);

        name = s.substring(0, eq);
        value = new Value(s.substring(eq + 1));
    }


    public String name() { return name; }
    public Attribute name(String name) { this.name = name; return this; }

    public Value value() { return value; }
    public Attribute value(Value value) { this.value = value; return this; }

    @Override
    public org.apache.mesos.Protos.Attribute proto0() {
        org.apache.mesos.Protos.Attribute.Builder builder = org.apache.mesos.Protos.Attribute.newBuilder();
        builder.setName(name);
        builder.setType(org.apache.mesos.Protos.Value.Type.valueOf(value.type().name()));

        switch (value.type()) {
            case SCALAR:
                builder.setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder().setValue(value.asDouble()));
                break;
            case RANGES:
                builder.setRanges(ranges0(value.asRanges()));
                break;
            case TEXT:
                builder.setText(org.apache.mesos.Protos.Value.Text.newBuilder().setValue(value.asText()));
                break;
            default:
                throw new IllegalStateException("unsupported type " + value.type());
        }

        return builder.build();
    }

    @Override
    public Attribute proto0(GeneratedMessage message) {
        org.apache.mesos.Protos.Attribute attribute = (org.apache.mesos.Protos.Attribute) message;
        name = attribute.getName();

        Value.Type type = Value.Type.valueOf(attribute.getType().name());
        Object value = null;
        if (attribute.hasScalar()) value = attribute.getScalar().getValue();
        if (attribute.hasRanges()) value = ranges0(attribute.getRanges());
        if (attribute.hasText()) value = attribute.getText().getValue();
        if (value == null) throw new IllegalStateException("no value");

        this.value = new Value(type, value);
        return this;
    }

    @Override
    public org.apache.mesos.v1.Protos.Attribute proto1() {
        org.apache.mesos.v1.Protos.Attribute.Builder builder = org.apache.mesos.v1.Protos.Attribute.newBuilder();
        builder.setName(name);
        builder.setType(org.apache.mesos.v1.Protos.Value.Type.valueOf(value.type().name()));

        switch (value.type()) {
            case SCALAR:
                builder.setScalar(org.apache.mesos.v1.Protos.Value.Scalar.newBuilder().setValue(value.asDouble()));
                break;
            case RANGES:
                builder.setRanges(ranges1(value.asRanges()));
                break;
            case TEXT:
                builder.setText(org.apache.mesos.v1.Protos.Value.Text.newBuilder().setValue(value.asText()));
                break;
            default:
                throw new IllegalStateException("unsupported type " + value.type());
        }
        return builder.build();
    }

    @Override
    public Attribute proto1(GeneratedMessage message) {
        org.apache.mesos.v1.Protos.Attribute attribute = (org.apache.mesos.v1.Protos.Attribute) message;
        name = attribute.getName();

        Value.Type type = Value.Type.valueOf(attribute.getType().name());
        Object value = null;
        if (attribute.hasScalar()) value = attribute.getScalar().getValue();
        if (attribute.hasRanges()) value = ranges1(attribute.getRanges());
        if (attribute.hasText()) value = attribute.getText().getValue();
        if (value == null) throw new IllegalStateException("no value");

        this.value = new Value(type, value);
        return this;
    }

    public int hashCode() { return 31 * name.hashCode() + value.hashCode(); }

    public boolean equals(Object obj) {
        if (!(obj instanceof Attribute)) return false;
        Attribute other = (Attribute) obj;
        return name.equals(other.name) && value.equals(other.value);
    }

    public String toString() { return name + "=" + value; }

    public static List<Attribute> parse(String s) {
        // a=1;b=2;c=3
        List<Attribute> attributes = new ArrayList<>();

        for (String t : s.split(";")) {
            t = t.trim();
            if (!t.isEmpty())
                attributes.add(new Attribute(t));
        }

        return attributes;
    }

    public static String format(List<Attribute> attributes) {
        return Strings.join(attributes, ";");
    }
}
