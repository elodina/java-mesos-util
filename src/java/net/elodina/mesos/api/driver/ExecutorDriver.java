package net.elodina.mesos.api.driver;

import net.elodina.mesos.api.Task;

public interface ExecutorDriver {
    boolean run();

    void stop();

    void sendStatus(Task.Status status);

    void sendMessage(byte[] data);
}
