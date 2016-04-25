package net.elodina.mesos.api;

import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public abstract class Scheduler {
    public abstract void registered(Driver driver, String id, Master master);

    public abstract void reregistered(Driver driver, Master master);

    public abstract void offers(List<Offer> offers);

    public abstract void status(Task.Status status);

    public abstract void message(String executorId, String slaveId, byte[] data);

    public abstract void disconnected();

    public static abstract class Driver {
        private Writer debug;

        public Writer getDebug() { return debug; }
        public void setDebug(Writer debug) { this.debug = debug; }

        public void debug(String message) {
            if (debug != null)
                try { debug.write(message + "\n"); }
                catch (IOException e) { throw new IOError(e); }
        }

        public abstract void declineOffer(String id);

        public abstract void launchTask(String offerId, Task task);

        public abstract void reconcileTasks(List<String> ids);

        public abstract void killTask(String id);

        public abstract boolean run() throws Exception;

        public abstract void stop();
    }
}
