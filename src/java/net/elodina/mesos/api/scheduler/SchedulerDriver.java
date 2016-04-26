package net.elodina.mesos.api.scheduler;

import net.elodina.mesos.api.Task;

import java.io.PrintWriter;
import java.util.List;

public abstract class SchedulerDriver {
    private PrintWriter debug;

    public PrintWriter getDebug() { return debug; }
    public void setDebug(PrintWriter debug) { this.debug = debug; }

    public void debug(String message) {
        if (debug != null) debug.println(message);
    }

    public abstract void declineOffer(String id);

    public abstract void launchTask(String offerId, Task task);

    public abstract void reconcileTasks(List<String> ids);

    public abstract void killTask(String id);

    public abstract boolean run() throws Exception;

    public abstract void stop();
}
