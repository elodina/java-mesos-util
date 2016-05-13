package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Range;

import java.util.ArrayList;
import java.util.List;

public abstract class Base {
    public abstract Object proto0();
    public abstract Base proto0(GeneratedMessage message);

    public abstract GeneratedMessage proto1();
    public abstract Base proto1(GeneratedMessage message);

    public abstract String toString(boolean _short);
    public final String toString() { return toString(false); }

    public static String shortId(String id) { return id != null ? "#" + suffix(id, 5) : null; }
    public static String shortId(String id, boolean _short) { return _short ? shortId(id) : id; }

    static String suffix(String s, int maxLen) { return s.length() <= maxLen ? s : s.substring(s.length() - maxLen); }

    static org.apache.mesos.Protos.Value.Ranges ranges0(List<Range> ranges) {
        List<org.apache.mesos.Protos.Value.Range> result = new ArrayList<>();
        for (Range range : ranges)
            result.add(org.apache.mesos.Protos.Value.Range.newBuilder().setBegin(range.start()).setEnd(range.end()).build());
        return org.apache.mesos.Protos.Value.Ranges.newBuilder().addAllRange(result).build();
    }

    static List<Range> ranges0(org.apache.mesos.Protos.Value.Ranges ranges) {
        List<Range> result = new ArrayList<>();

        for (org.apache.mesos.Protos.Value.Range range : ranges.getRangeList())
            result.add(new Range((int)range.getBegin(), (int)range.getEnd()));

        return result;
    }

    static org.apache.mesos.v1.Protos.Value.Ranges ranges1(List<Range> ranges) {
        List<org.apache.mesos.v1.Protos.Value.Range> result = new ArrayList<>();
        for (Range range : ranges)
            result.add(org.apache.mesos.v1.Protos.Value.Range.newBuilder().setBegin(range.start()).setEnd(range.end()).build());
        return org.apache.mesos.v1.Protos.Value.Ranges.newBuilder().addAllRange(result).build();
    }

    static List<Range> ranges1(org.apache.mesos.v1.Protos.Value.Ranges ranges) {
        List<Range> result = new ArrayList<>();

        for (org.apache.mesos.v1.Protos.Value.Range range : ranges.getRangeList())
            result.add(new Range((int)range.getBegin(), (int)range.getEnd()));

        return result;
    }
}
