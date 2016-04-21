package net.elodina.mesos.api;

import net.elodina.mesos.util.Strings;
import org.junit.Test;

import java.util.Arrays;

import static net.elodina.mesos.api.Task.Executor;
import static net.elodina.mesos.api.Task.Status;
import static org.junit.Assert.*;

public class TaskTest {
    @Test
    public void init_parse() {
        // simple attrs
        Task task = new Task("id:id, name:name, slaveId:slaveId");
        assertEquals("id", task.id());
        assertEquals("name", task.name());
        assertEquals("slaveId", task.slaveId());

        // resources
        task = new Task("resources:[cpus:0.5; mem:1024]");
        assertEquals(Arrays.asList(new Resource("cpus:0.5"), new Resource("mem:1024")), task.resources());

        // executor
        task = new Task("executor:[id:id, name:name]");
        assertEquals("" + new Executor("id:id, name:name"), "" + task.executor());

        // command
        task = new Task("command:[cmd]");
        assertEquals("" + new Command("cmd"), "" + task.command());

        // data
        task = new Task("data:" + Strings.formatHex("abc".getBytes()));
        assertEquals("abc", new String(task.data()));
    }

    @Test
    public void proto0() {
        Task task = new Task("id:1, name:name, slaveId:2, data:00");
        org.apache.mesos.Protos.TaskInfo message = task.proto0();

        Task read = new Task().proto0(message);
        assertEquals("" + task, "" + read);
    }

    @Test
    public void proto1() {
        Task task = new Task("id:1, name:name, slaveId:2, data:00");
        org.apache.mesos.v1.Protos.TaskInfo message = task.proto1();

        Task read = new Task().proto1(message);
        assertEquals("" + task, "" + read);
    }

    @Test
    public void Status_init_parse() {
        Status status = new Status("id:id, state:running, message:message, slaveId:slaveId, executorId:executorId");
        assertEquals("id", status.id());
        assertEquals(Task.State.RUNNING, status.state());

        assertEquals("message", status.message());
        assertEquals(null, status.data());

        assertEquals("slaveId", status.slaveId());
        assertEquals("executorId", status.executorId());
    }

    @Test
    public void Status_toString() {
        assertEquals("", "" + new Status(""));
        assertEquals("id:1", "" + new Status("id:1"));
        assertEquals("id:1, state:running, executorId:2", "" + new Status("id:1, state:running, executorId:2"));
    }

    @Test
    public void Status_proto0() {
        Status status = new Status("id:1, state:starting, executorId:2, message:123, data:00");
        org.apache.mesos.Protos.TaskStatus message = status.proto0();

        Status read = new Status().proto0(message);
        assertEquals("" + status, "" + read);
    }

    @Test
    public void Status_proto1() {
        Status status = new Status("id:1, state:starting, executorId:2, message:123, data:00");
        org.apache.mesos.v1.Protos.TaskStatus message = status.proto1();

        Status read = new Status().proto1(message);
        assertEquals("" + status, "" + read);
    }

    @Test
    public void Executor_init_parse() {
        // simple attrs
        Executor executor = new Executor("id:id, name:name, frameworkId:frameworkId");
        assertEquals("id", executor.id());
        assertEquals("name", executor.name());
        assertEquals("frameworkId", executor.frameworkId());

        // command
        executor = new Executor("command:[ls, uris:[u1, u2]]");
        assertEquals("ls", executor.command().value());
        assertEquals(Arrays.asList(new Command.URI("u1"), new Command.URI("u2")), executor.command().uris());
    }

    @Test
    public void Executor_toString() {
        assertEquals("id:id, name:name, frameworkId:frameworkId", "" + new Executor("id:id, name:name, frameworkId:frameworkId"));
        assertEquals("command:[cmd]", "" + new Executor("command:[cmd]"));
    }

    @Test
    public void Executor_proto0() {
        Executor executor = new Executor("id:1, frameworkId:2, name:name, command:[ls], data:00");
        org.apache.mesos.Protos.ExecutorInfo message = executor.proto0();

        Executor read = new Executor().proto0(message);
        assertEquals("" + executor, "" + read);
    }

    @Test
    public void Executor_proto1() {
        Executor executor = new Executor("id:1, frameworkId:2, name:name, command:[ls], data:00");
        org.apache.mesos.v1.Protos.ExecutorInfo message = executor.proto1();

        Executor read = new Executor().proto1(message);
        assertEquals("" + executor, "" + read);
    }
}
