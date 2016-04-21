package net.elodina.mesos.api;

import net.elodina.mesos.util.Version;
import org.junit.Test;

import static org.junit.Assert.*;

public class MasterTest {
    @Test
    public void ipAsInt() {
        Master master = new Master();
        assertNull(master.ip());
        assertEquals(0, master.ipAsInt());

        master.ip("127.0.0.1");
        assertEquals(2130706433, master.ipAsInt());

        master.ipAsInt(3232235876l);
        assertEquals("192.168.1.100", master.ip());
    }

    @Test
    public void init_parse() {
        Master master = new Master("id:id, hostname:hostname, version:1.0");
        assertEquals("id", master.id());
        assertEquals("hostname", master.hostname());
        assertEquals(new Version(1,0), master.version());
    }

    @Test
    public void _toString() {
        assertEquals("id:id", "" + new Master("id:id"));
        assertEquals("id:id, hostname:hostname, version:1.0", "" + new Master("id:id, hostname:hostname, version:1.0"));
    }

    @Test
    public void proto0() {
        Master master = new Master("id:1, hostname:master");
        org.apache.mesos.Protos.MasterInfo message = master.proto0();

        Master read = new Master().proto0(message);
        assertEquals("" + master, "" + read);
    }

    @Test
    public void proto1() {
        Master master = new Master("id:1, hostname:master");
        org.apache.mesos.v1.Protos.MasterInfo message = master.proto1();

        Master read = new Master().proto1(message);
        assertEquals("" + master, "" + read);
    }
}
