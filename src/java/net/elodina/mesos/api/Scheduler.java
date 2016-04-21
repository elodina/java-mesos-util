package net.elodina.mesos.api;

import java.util.List;

public abstract class Scheduler {
    public abstract void registered(Driver driver, String id, Master master);

    public abstract void reregistered(Driver driver, Master master);

    public abstract void offers(List<Offer> offers);

    public abstract void status(Task.Status status);

    public abstract void message(String executorId, String slaveId, byte[] data);

    public abstract void disconnected();

    public static abstract class Driver {
        public static final int TCP_V0 = 0;
        public static final int HTTP_V1 = 1;

        public abstract void declineOffer(String id);

        public abstract void launchTask(String offerId, Task task);

        public abstract void reconcileTasks(List<String> ids);

        public abstract void killTask(String id);

        public static Object getInstance(int type) {
            switch (type) {
                case TCP_V0: return new TcpV0Driver();
                case HTTP_V1: return new HttpV1Driver();
                default: throw new IllegalArgumentException("" + type);
            }
        }

        public abstract void run(Framework framework, Scheduler scheduler);
    }
}
