package net.elodina.mesos.api;

import net.elodina.mesos.api.driver.SchedulerDriver;

import java.util.List;

public abstract class Scheduler {
    public abstract void subscribed(SchedulerDriver driver, String id, Master master);

    public abstract void offers(List<Offer> offers);

    public abstract void status(Task.Status status);

    public abstract void message(String executorId, String slaveId, byte[] data);

    public abstract void disconnected();

}
