package net.elodina.mesos.api;

import net.elodina.mesos.api.driver.ExecutorDriver;

public interface Executor {
    void registered(ExecutorDriver driver, Task.Executor executor, Framework framework, Slave slave);

    void disconnected();

    void launchTask(Task task);

    void killTask(String id);

    void message(byte[] data);

    void shutdown();

    void error(String message);
}
