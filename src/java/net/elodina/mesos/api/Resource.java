package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Range;
import net.elodina.mesos.util.Strings;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Resource extends Base {
    private String name;
    private Type type;

    private Double value;
    private List<Range> ranges;

    private String role = "*";

    public Resource() {}
    public Resource(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public Resource(String expr) { parse(expr); }

    public String name() { return name; }
    public Resource name(String name) { this.name = name; return this; }


    public Type type() { return type; }
    public Resource type(Type type) { this.type = type; return this; }

    public Double value() { return value; }
    public Resource value(Double value) { this.value = value; return this; }

    public double doubleValue() { return value != null ? value : 0; }
    public int intValue() { return value != null ? value.intValue() : 0; }
    public long longValue() { return value != null ? value.longValue() : 0; }

    public List<Range> ranges() { return ranges != null ? Collections.unmodifiableList(ranges) : null; }
    public Resource ranges(List<Range> ranges) { this.ranges = ranges != null ? new ArrayList<>(ranges) : null; return this; }


    public String role() { return role; }
    public Resource role(String role) { this.role = role; return this; }

    @Override
    public org.apache.mesos.Protos.Resource proto0() {
        org.apache.mesos.Protos.Resource.Builder builder = org.apache.mesos.Protos.Resource.newBuilder();
        builder.setName(name);

        builder.setType(org.apache.mesos.Protos.Value.Type.valueOf(type.name()));
        if (value != null) builder.setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder().setValue(value));
        if (ranges != null) builder.setRanges(ranges0(ranges));

        builder.setRole(role);
        return builder.build();
    }

    @Override
    public Resource proto0(GeneratedMessage message) {
        org.apache.mesos.Protos.Resource resource = (org.apache.mesos.Protos.Resource) message;

        name = resource.getName();
        type = Type.valueOf(resource.getType().name());

        if (resource.hasScalar()) value = resource.getScalar().getValue();
        if (resource.hasRanges()) ranges = ranges0(resource.getRanges());

        role = resource.getRole();
        return this;
    }

    @Override
    public org.apache.mesos.v1.Protos.Resource proto1() {
        org.apache.mesos.v1.Protos.Resource.Builder builder = org.apache.mesos.v1.Protos.Resource.newBuilder();
        builder.setName(name);

        builder.setType(org.apache.mesos.v1.Protos.Value.Type.valueOf(type.name()));
        if (value != null) builder.setScalar(org.apache.mesos.v1.Protos.Value.Scalar.newBuilder().setValue(value));
        if (ranges != null) builder.setRanges(ranges1(ranges));

        builder.setRole(role);
        return builder.build();
    }

    @Override
    public Resource proto1(GeneratedMessage message) {
        org.apache.mesos.v1.Protos.Resource resource = (org.apache.mesos.v1.Protos.Resource) message;

        name = resource.getName();
        type = Type.valueOf(resource.getType().name());

        if (resource.hasScalar()) value = resource.getScalar().getValue();
        if (resource.hasRanges()) ranges = ranges1(resource.getRanges());

        role = resource.getRole();
        return this;
    }

    private void parse(String expr) {
        // cpus:0.5
        // mem:1024
        // disk:73390
        // ports:0..100,110..200

        int colon = expr.indexOf(":");
        if (colon == -1) throw new IllegalArgumentException(expr);

        String name = expr.substring(0, colon);
        String value = expr.substring(colon + 1);

        int bracket = name.indexOf("(");
        if (bracket != -1) {
            role = name.substring(bracket + 1, name.length() - 1);
            name = name.substring(0, bracket);
        }

        this.name = name;
        switch (name) {
            case "cpus":case "mem":case "disk":
                type = Type.SCALAR;
                this.value = Double.parseDouble(value);
                break;
            case "ports":
                type = Type.RANGES;
                ranges = new ArrayList<>();
                for (String part : value.split(",")) ranges.add(new Range(part));
                break;
            default:
                throw new IllegalArgumentException(expr);
        }
    }

    private String format() {
        // see parse
        String s = "";

        s += name;
        if (!role.equals("*")) s += "(" + role + ")";
        s += ":";

        switch (type) {
            case SCALAR:
                s += new DecimalFormat("#.###").format(value);
                break;
            case RANGES:
                s += Strings.join(ranges, ",");
                break;
            default:
                throw new IllegalStateException("" + type);
        }

        return s;
    }

    @Override
    public String toString() { return format(); }
}
