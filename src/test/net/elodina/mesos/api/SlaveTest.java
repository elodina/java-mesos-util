package net.elodina.mesos.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SlaveTest {
    @Test
    public void init_parse() {
        Slave slave = new Slave("id:id, hostname:hostname, port:5051");
        assertEquals("id", slave.id());
        assertEquals("hostname", slave.hostname());
        assertEquals(5051, slave.port());
    }

    @Test
    public void _toString() {
        assertEquals("", "" + new Slave(""));
        assertEquals("id:id", "" + new Slave("id:id"));
        assertEquals("id:id, hostname:hostname", "" + new Master("id:id, hostname:hostname"));
    }

    @Test
    public void proto0() {
        Slave slave = new Slave("id:1, hostname:slave");
        org.apache.mesos.Protos.SlaveInfo message = slave.proto0();

        Slave read = new Slave().proto0(message);
        assertEquals("" + slave, "" + read);
    }

    @Test
    public void proto1() {
        Slave slave = new Slave("id:1, hostname:master");
        org.apache.mesos.v1.Protos.AgentInfo message = slave.proto1();

        Slave read = new Slave().proto1(message);
        assertEquals("" + slave, "" + read);
    }
}
