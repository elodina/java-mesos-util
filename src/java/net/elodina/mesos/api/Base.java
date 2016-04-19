package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Range;

import java.util.ArrayList;
import java.util.List;

public abstract class Base {
    public abstract GeneratedMessage proto0();
    public abstract Base proto0(GeneratedMessage message);

    public abstract GeneratedMessage proto1();
    public abstract Base proto1(GeneratedMessage message);

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
