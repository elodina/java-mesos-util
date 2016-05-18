package net.elodina.mesos.api.scheduler;

import net.elodina.mesos.api.Master;
import net.elodina.mesos.api.Offer;
import net.elodina.mesos.api.Task;

import java.util.List;

public interface Scheduler {
    void subscribed(SchedulerDriver driver, String id, Master master);

    void offers(List<Offer> offers);

    void status(Task.Status status);

    void message(String executorId, String slaveId, byte[] data);

    void disconnected();
}
