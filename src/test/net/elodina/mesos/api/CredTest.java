package net.elodina.mesos.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CredTest {
    @Test
    public void parse_toString() {
        // parse
        Cred cred = new Cred("user:password");
        assertEquals("user", cred.principal());
        assertEquals("password", cred.secret());

        // toString
        assertEquals("user:password", cred.toString());
    }

    @Test
    public void proto0() {
        Cred cred = new Cred("user:password");
        org.apache.mesos.Protos.Credential message = cred.proto0();

        Cred read = new Cred().proto0(message);
        assertEquals(cred, read);
    }

    @Test
    public void proto1() {
        Cred cred = new Cred("user:password");
        org.apache.mesos.v1.Protos.Credential message = cred.proto1();

        Cred read = new Cred().proto1(message);
        assertEquals(cred, read);
    }
}
