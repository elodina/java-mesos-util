package net.elodina.mesos.api.scheduler;

import net.elodina.mesos.api.Master;
import net.elodina.mesos.api.Offer;
import net.elodina.mesos.api.Task;

import java.util.List;

public abstract class Scheduler {
    public abstract void subscribed(SchedulerDriver driver, String id, Master master);

    public abstract void offers(List<Offer> offers);

    public abstract void status(Task.Status status);

    public abstract void message(String executorId, String slaveId, byte[] data);

    public abstract void disconnected();
}
