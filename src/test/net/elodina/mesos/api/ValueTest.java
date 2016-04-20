package net.elodina.mesos.api;

import net.elodina.mesos.util.Range;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ValueTest {
    @Test
    public void init_parse() {
        // double
        Value value = new Value("1.1");
        assertEquals(Value.Type.SCALAR, value.type());
        assertEquals(1.1, value.asDouble(), 0.001);

        // int
        value = new Value("1");
        assertEquals(Value.Type.SCALAR, value.type());
        assertEquals(1, value.asDouble(), 0.001);

        // range
        value = new Value("0..100, 100..200");
        assertEquals(Value.Type.RANGES, value.type());
        assertEquals(Arrays.asList(new Range(0,100), new Range(100,200)), value.asRanges());

        // text
        value = new Value("abc");
        assertEquals(Value.Type.TEXT, value.type());
        assertEquals("abc", value.asText());
    }

    @Test
    public void _toString() {
        assertEquals("1.1", "" + new Value(Value.Type.SCALAR, 1.1d));
        assertEquals("1", "" + new Value(Value.Type.SCALAR, 1.0d));
        assertEquals("100..200,300..400", "" + new Value(Value.Type.RANGES, Arrays.asList(new Range(100,200), new Range(300,400))));
        assertEquals("abc", "" + new Value(Value.Type.TEXT, "abc"));
    }
}
