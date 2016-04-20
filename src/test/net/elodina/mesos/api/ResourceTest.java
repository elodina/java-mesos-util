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
        assertEquals(0.5d, resource.value().asDouble(), 0.001);
        assertEquals("*", resource.role());

        // ranges
        resource = new Resource("ports:0..100,110..200");
        assertEquals("ports", resource.name());
        assertEquals(Arrays.asList(new Range(0,100), new Range(110,200)), resource.value().asRanges());

        // role
        resource = new Resource("mem(kafka):1024");
        assertEquals("mem", resource.name());
        assertEquals(1024, resource.value().asInt());
        assertEquals("kafka", resource.role());

        // no name
        try { new Resource("10"); fail(); }
        catch (IllegalArgumentException e) {}
    }

    @Test
    public void _toString() {
        assertEquals("cpus:0.5", "" + new Resource("cpus:0.5"));
        assertEquals("mem(kafka):1024", "" + new Resource("mem(kafka):1024"));
        assertEquals("ports:0..10,20..30", "" + new Resource("ports:0..10,20..30"));
    }

    @Test
    public void proto0() {
        Resource resource = new Resource("cpus(kafka):0.5");
        org.apache.mesos.Protos.Resource message = resource.proto0();

        Resource read = new Resource().proto0(message);
        assertEquals(resource, read);
    }

    @Test
    public void proto1() {
        Resource resource = new Resource("cpus(kafka):0.5");
        org.apache.mesos.v1.Protos.Resource message = resource.proto1();

        Resource read = new Resource().proto1(message);
        assertEquals(resource, read);
    }
}
