package net.elodina.mesos.api;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ExecTest {
    @Test
    public void init_parse() {
        // simple attrs
        Exec exec = new Exec("id:id, name:name, frameworkId:frameworkId");
        assertEquals("id", exec.id());
        assertEquals("name", exec.name());
        assertEquals("frameworkId", exec.frameworkId());

        // command
        exec = new Exec("command:[ls, uris:[u1, u2]]");
        assertEquals("ls", exec.command().value());
        assertEquals(Arrays.asList(new Command.URI("u1"), new Command.URI("u2")), exec.command().uris());
    }

    @Test
    public void _toString() {
        assertEquals("id:id, name:name, frameworkId:frameworkId", "" + new Exec("id:id, name:name, frameworkId:frameworkId"));
        assertEquals("command:[cmd]", "" + new Exec("command:[cmd]"));
    }

    @Test
    public void proto0() {
        Exec exec = new Exec("id:1, frameworkId:2, name:name, command:[ls], data:00");
        org.apache.mesos.Protos.ExecutorInfo message = exec.proto0();

        Exec read = new Exec().proto0(message);
        assertEquals("" + exec, "" + read);
    }

    @Test
    public void proto1() {
        Exec exec = new Exec("id:1, frameworkId:2, name:name, command:[ls], data:00");
        org.apache.mesos.v1.Protos.ExecutorInfo message = exec.proto1();

        Exec read = new Exec().proto1(message);
        assertEquals("" + exec, "" + read);
    }
}
