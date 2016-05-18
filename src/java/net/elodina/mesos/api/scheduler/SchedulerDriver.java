package net.elodina.mesos.api.scheduler;

import net.elodina.mesos.api.Task;

import java.util.List;

public abstract class SchedulerDriver {
    public abstract void declineOffer(String id);

    public abstract void launchTask(String offerId, Task task);

    public abstract void reconcileTasks(List<String> ids);

    public abstract void killTask(String id);

    public abstract boolean run() throws Exception;

    public abstract void stop();
}
