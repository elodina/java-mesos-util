package net.elodina.mesos.api;

import net.elodina.mesos.util.Range;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ResourceTest {
    @Test
    public void init_parse() {
        // simple
        Resource resource = new Resource("cpus:0.5");
        assertEquals("cpus", resource.name());
        assertEquals(Type.SCALAR, resource.type());
        assertEquals(0.5d, resource.value(), 0.001);
        assertEquals("*", resource.role());

        // ranges
        resource = new Resource("ports:0..100,110..200");
        assertEquals("ports", resource.name());
        assertEquals(Type.RANGES, resource.type());
        assertEquals(Arrays.asList(new Range(0,100), new Range(110,200)), resource.ranges());

        // role
        resource = new Resource("mem(kafka):1024");
        assertEquals("mem", resource.name());
        assertEquals(Type.SCALAR, resource.type());
        assertEquals(1024, resource.intValue());
        assertEquals("kafka", resource.role());

        // no name
        try { new Resource("10"); fail(); }
        catch (IllegalArgumentException e) {}

        // unsupported name
        try { new Resource("unknown:10"); fail(); }
        catch (IllegalArgumentException e) {}
    }

    @Test
    public void _toString() {
        assertEquals("cpus:0.5", "" + new Resource("cpus:0.5"));
        assertEquals("mem(kafka):1024", "" + new Resource("mem(kafka):1024"));
        assertEquals("ports:0..10,20..30", "" + new Resource("ports:0..10,20..30"));
    }
}
