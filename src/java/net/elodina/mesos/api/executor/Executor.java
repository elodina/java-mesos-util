package net.elodina.mesos.api.executor;

import net.elodina.mesos.api.Framework;
import net.elodina.mesos.api.Slave;
import net.elodina.mesos.api.Task;

public interface Executor {
    void registered(ExecutorDriver driver, Task.Executor executor, Framework framework, Slave slave);

    void disconnected();

    void launchTask(Task task);

    void killTask(String id);

    void message(byte[] data);

    void shutdown();

    void error(String message);
}
