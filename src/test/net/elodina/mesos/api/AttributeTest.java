package net.elodina.mesos.api;

import net.elodina.mesos.util.Range;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AttributeTest {
    @Test
    public void init_parse() {
        // correct
        Attribute attribute = new Attribute("a=1");
        assertEquals("a", attribute.name());
        assertEquals(1, attribute.value().asInt());

        // invalid
        try { new Attribute("1"); fail(); }
        catch (IllegalArgumentException e) {}
    }

    @Test
    public void _toString() {
        assertEquals("a=1", "" + new Attribute("a", new Value(Value.Type.SCALAR, 1.0)));
        assertEquals("a=0..10", "" + new Attribute("a", new Value(Value.Type.RANGES, Arrays.asList(new Range(0, 10)))));
        assertEquals("a=text", "" + new Attribute("a", new Value(Value.Type.TEXT, "text")));
    }

    @Test
    public void proto0() {
        Attribute attribute = new Attribute("a=1");
        org.apache.mesos.Protos.Attribute message = attribute.proto0();

        Attribute read = new Attribute().proto0(message);
        assertEquals("" + attribute, "" + read);
    }

    @Test
    public void proto1() {
        Attribute attribute = new Attribute("a=1");
        org.apache.mesos.v1.Protos.Attribute message = attribute.proto1();

        Attribute read = new Attribute().proto1(message);
        assertEquals("" + attribute, "" + read);
    }
}
