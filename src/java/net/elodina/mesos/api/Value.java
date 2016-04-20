package net.elodina.mesos.api;

import net.elodina.mesos.util.Range;
import net.elodina.mesos.util.Strings;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Value {
    private Type type;
    private Object value;

    public Value(String s) {
        if (Strings.isDouble(s)) type = Type.SCALAR;
        else if (s.contains("..")) type = Value.Type.RANGES;
        else type = Value.Type.TEXT;

        value = type.parse(s);
    }

    public Value(Type type, Object value) {
        if (type == null || value == null) throw new NullPointerException();
        this.type = type;
        this.value = value;
    }

    public Type type() { return type; }
    public Object value() { return value; }

    public double asDouble() { return type == Type.SCALAR ? (double) value : 0; }
    public int asInt() { return (int) asDouble(); }
    public int asLong() { return (int) asDouble(); }

    public String asText() { return type == Type.TEXT ? (String)value : ""; }

    @SuppressWarnings("unchecked")
    public List<Range> asRanges() { return type == Type.RANGES ? (List<Range>)value : Collections.<Range>emptyList(); }

    public int hashCode() { return 31 * type.hashCode() + value.hashCode(); }

    public boolean equals(Object obj) {
        if (!(obj instanceof Value)) return false;
        Value other = (Value) obj;
        return type == other.type && value.equals(other.value);
    }

    public String toString() { return type.format(value); }

    public static enum Type {
        SCALAR {
            public String format(Object o) { return new DecimalFormat("#.###").format(o); }
            public Double parse(String s) { return Double.valueOf(s); }
        },
        RANGES {
            @SuppressWarnings("unchecked") public String format(Object o) { return Strings.join((List<Range>) o, ","); }
            public List<Range> parse(String s) {
                List<Range> ranges = new ArrayList<>();
                for (String part : s.split(",")) ranges.add(new Range(part.trim()));
                return ranges;
            }
        },
        SET {
            public String format(Object o) { throw new UnsupportedOperationException(); }
            public Object parse(String s) { throw new UnsupportedOperationException(); }
        },
        TEXT {
            public String format(Object o) { return "" + o; }
            public String parse(String s) { return s; }
        };

        public abstract String format(Object o);
        public abstract Object parse(String s);
    }
}
