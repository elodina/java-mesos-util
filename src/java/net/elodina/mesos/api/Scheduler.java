package net.elodina.mesos.api;

import net.elodina.mesos.api.driver.SchedulerDriver;

import java.util.List;

public interface Scheduler {
    void subscribed(SchedulerDriver driver, String id, Master master);

    void offers(List<Offer> offers);

    void status(Task.Status status);

    void message(String executorId, String slaveId, byte[] data);

    void disconnected();
}
