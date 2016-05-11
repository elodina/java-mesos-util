package net.elodina.mesos.api.scheduler;

import net.elodina.mesos.api.Task;

import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public abstract class SchedulerDriver {
    private Writer debug;

    public Writer getDebug() { return debug; }
    public void setDebug(Writer debug) { this.debug = debug; }

    public void debug(String message) {
        if (debug != null)
            try { debug.write(message + System.lineSeparator()); }
            catch (IOException e) { throw new IOError(e); }
    }

    public abstract void declineOffer(String id);

    public abstract void launchTask(String offerId, Task task);

    public abstract void reconcileTasks(List<String> ids);

    public abstract void killTask(String id);

    public abstract boolean run() throws Exception;

    public abstract void stop();
}
