package net.elodina.mesos.api;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TaskTest {
    @Test
    public void Executor_init_parse() {
        // simple attrs
        Task.Executor executor = new Task.Executor("id:id, name:name, frameworkId:frameworkId");
        assertEquals("id", executor.id());
        assertEquals("name", executor.name());
        assertEquals("frameworkId", executor.frameworkId());

        // command
        executor = new Task.Executor("command:[ls, uris:[u1, u2]]");
        assertEquals("ls", executor.command().value());
        assertEquals(Arrays.asList(new Command.URI("u1"), new Command.URI("u2")), executor.command().uris());
    }

    @Test
    public void Executor_toString() {
        assertEquals("id:id, name:name, frameworkId:frameworkId", "" + new Task.Executor("id:id, name:name, frameworkId:frameworkId"));
        assertEquals("command:[cmd]", "" + new Task.Executor("command:[cmd]"));
    }

    @Test
    public void Executor_proto0() {
        Task.Executor executor = new Task.Executor("id:1, frameworkId:2, name:name, command:[ls], data:00");
        org.apache.mesos.Protos.ExecutorInfo message = executor.proto0();

        Task.Executor read = new Task.Executor().proto0(message);
        assertEquals("" + executor, "" + read);
    }

    @Test
    public void Executor_proto1() {
        Task.Executor executor = new Task.Executor("id:1, frameworkId:2, name:name, command:[ls], data:00");
        org.apache.mesos.v1.Protos.ExecutorInfo message = executor.proto1();

        Task.Executor read = new Task.Executor().proto1(message);
        assertEquals("" + executor, "" + read);
    }
}
