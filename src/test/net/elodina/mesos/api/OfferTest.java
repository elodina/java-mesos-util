package net.elodina.mesos.api;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class OfferTest {
    @Test
    public void parse() {
        // simple attributes
        Offer offer = new Offer("id:id, frameworkId:frameworkId, slaveId:slaveId, hostname:hostname");
        assertEquals("id", offer.id());
        assertEquals("frameworkId", offer.frameworkId());
        assertEquals("slaveId", offer.slaveId());
        assertEquals("hostname", offer.hostname());

        assertEquals(0, offer.resources().size());
        assertEquals(0, offer.attributes().size());

        // resources
        offer = new Offer("resources:[cpus:0.5;mem:1024]");
        assertEquals(Arrays.asList(new Resource("cpus:0.5"), new Resource("mem:1024")), offer.resources());

        // attributes
        offer = new Offer("attributes:[a=0.5;t=abc]");
        assertEquals(Arrays.asList(new Attribute("a=0.5"), new Attribute("t=abc")), offer.attributes());
    }

    @Test
    public void _toString() {
        assertEquals("id:id", "" + new Offer("id:id"));
        assertEquals("resources:[cpus:0.5]", "" + new Offer("resources:[cpus:0.5]"));
        assertEquals("attributes:[a=123]", "" + new Offer("attributes:[a=123]"));
    }

    @Test
    public void proto0_to_from() {
        Offer offer = new Offer("id:1, frameworkId:2, slaveId:3, hostname:host, resources:[mem:1024], attributes:[a=1]");
        org.apache.mesos.Protos.Offer message = offer.proto0();

        Offer read = new Offer().proto0(message);
        assertEquals("" + offer, "" + read);
    }

    @Test
    public void proto1_to_from() {
        Offer offer = new Offer("id:1, frameworkId:2, slaveId:3, hostname:host, resources:[mem:1024], attributes:[a=1]");
        org.apache.mesos.v1.Protos.Offer message = offer.proto1();

        Offer read = new Offer().proto1(message);
        assertEquals("" + offer, "" + read);
    }
}
