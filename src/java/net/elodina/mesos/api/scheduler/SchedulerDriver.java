package net.elodina.mesos.api.scheduler;

import net.elodina.mesos.api.Task;

import java.util.List;

public interface SchedulerDriver {
    void declineOffer(String id);

    void launchTask(String offerId, Task task);

    void reconcileTasks(List<String> ids);

    void killTask(String id);

    boolean run() throws Exception;

    void stop();
}
