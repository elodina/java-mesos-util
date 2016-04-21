package net.elodina.mesos.api;

import net.elodina.mesos.util.Period;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FrameworkTest {
    @Test
    public void init_parse() {
        Framework framework = new Framework("id:id, name:name, user:user, timeout:1d");
        assertEquals("id", framework.id());
        assertEquals("name", framework.name());
        assertEquals("user", framework.user());
        assertEquals(new Period("1d"), framework.timeout());
    }

    @Test
    public void _toString() {
        assertEquals("checkpoint:false, role:*", "" + new Framework(""));
        assertEquals("timeout:1d, checkpoint:false, role:*", "" + new Framework("timeout:1d"));
    }

    @Test
    public void proto0() {
        Framework framework = new Framework("id:id, name:name, user:user, timeout:1d");
        org.apache.mesos.Protos.FrameworkInfo message = framework.proto0();

        Framework read = new Framework().proto0(message);
        assertEquals("id:id, name:name, user:user, timeout:86400s, checkpoint:false, role:*", "" + read);
    }

    @Test
    public void proto1() {
        Framework framework = new Framework("id:id, name:name, user:user, timeout:1d");
        org.apache.mesos.v1.Protos.FrameworkInfo message = framework.proto1();

        Framework read = new Framework().proto1(message);
        assertEquals("id:id, name:name, user:user, timeout:86400s, checkpoint:false, role:*", "" + read);
    }
}
