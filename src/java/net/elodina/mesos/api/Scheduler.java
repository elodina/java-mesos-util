package net.elodina.mesos.api;

import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;

import java.util.ArrayList;
import java.util.Arrays;
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

        public abstract boolean run();

        public abstract void stop();
    }

    public static class TcpV0Driver extends Driver {
        private Scheduler scheduler;
        private MesosSchedulerDriver driver;

        public TcpV0Driver(Scheduler scheduler, Framework framework, String master) {
            this.scheduler = scheduler;
            driver = new MesosSchedulerDriver(new TcpV0Scheduler(), framework.proto0(), master);
        }

        @Override
        public void declineOffer(String id) {
            Protos.OfferID offerId = Protos.OfferID.newBuilder().setValue(id).build();
            driver.declineOffer(offerId);
        }

        @Override
        public void launchTask(String offerId, Task task) {
            Protos.OfferID _offerId = Protos.OfferID.newBuilder().setValue(offerId).build();
            driver.launchTasks(_offerId, Arrays.asList(task.proto0()));
        }

        @Override
        public void reconcileTasks(List<String> ids) {
            List<Protos.TaskStatus> statuses = new ArrayList<>();

            for (String id : ids) {
                statuses.add(Protos.TaskStatus.newBuilder()
                    .setTaskId(Protos.TaskID.newBuilder().setValue(id))
                    .setState(Protos.TaskState.TASK_RUNNING)
                    .build()
                );
            }

            driver.reconcileTasks(statuses);
        }

        @Override
        public void killTask(String id) {
            driver.killTask(Protos.TaskID.newBuilder().setValue(id).build());
        }

        @Override
        public boolean run() {
            return driver.run() == Protos.Status.DRIVER_STOPPED;
        }

        @Override
        public void stop() {
            driver.stop();
        }

        private class TcpV0Scheduler implements org.apache.mesos.Scheduler {
            @Override
            public void registered(SchedulerDriver driver, Protos.FrameworkID frameworkId, Protos.MasterInfo masterInfo) {
                scheduler.registered(TcpV0Driver.this, frameworkId.getValue(), new Master().proto0(masterInfo));
            }

            @Override
            public void reregistered(SchedulerDriver driver, Protos.MasterInfo masterInfo) {
                scheduler.reregistered(TcpV0Driver.this, new Master().proto0(masterInfo));
            }

            @Override
            public void resourceOffers(SchedulerDriver driver, List<Protos.Offer> offers) {
                List<Offer> _offers = new ArrayList<>();
                for (Protos.Offer offer : offers) _offers.add(new Offer().proto0(offer));
                scheduler.offers(_offers);
            }

            @Override
            public void offerRescinded(SchedulerDriver driver, Protos.OfferID offerId) {}

            @Override
            public void statusUpdate(SchedulerDriver driver, Protos.TaskStatus status) {
                scheduler.status(new Task.Status().proto0(status));
            }

            @Override
            public void frameworkMessage(SchedulerDriver driver, Protos.ExecutorID executorId, Protos.SlaveID slaveId, byte[] data) {
                scheduler.message(executorId.getValue(), slaveId.getValue(), data);
            }

            @Override
            public void disconnected(SchedulerDriver driver) {
                scheduler.disconnected();
            }

            @Override
            public void slaveLost(SchedulerDriver driver, Protos.SlaveID slaveId) {}

            @Override
            public void executorLost(SchedulerDriver driver, Protos.ExecutorID executorId, Protos.SlaveID slaveId, int status) {}

            @Override
            public void error(SchedulerDriver driver, String message) {}
        }
    }

    public static class HttpV1Driver extends Driver {
        @Override
        public void declineOffer(String id) {
            // todo
        }

        @Override
        public void launchTask(String offerId, Task task) {
            // todo
        }

        @Override
        public void reconcileTasks(List<String> ids) {
            // todo
        }

        @Override
        public void killTask(String id) {
            // todo
        }

        @Override
        public boolean run() {
            return true;
        }

        @Override
        public void stop() {
            // todo
        }
    }
}
