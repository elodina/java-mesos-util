package net.elodina.mesos.api.driver;

import net.elodina.mesos.api.*;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SchedulerDriverV0 extends net.elodina.mesos.api.driver.SchedulerDriver {
    private Scheduler scheduler;
    private Framework framework;
    private MesosSchedulerDriver driver;

    public SchedulerDriverV0(Scheduler scheduler, Framework framework, String master) { this(scheduler, framework, master, null); }

    public SchedulerDriverV0(Scheduler scheduler, Framework framework, String master, Cred cred) {
        this.scheduler = scheduler;
        this.framework = framework;

        if (cred != null) driver = new MesosSchedulerDriver(new TcpV0Scheduler(), framework.proto0(), master, cred.proto0());
        else driver = new MesosSchedulerDriver(new TcpV0Scheduler(), framework.proto0(), master);
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
            framework.id(framework.id());
            scheduler.subscribed(SchedulerDriverV0.this, frameworkId.getValue(), new Master().proto0(masterInfo));
        }

        @Override
        public void reregistered(SchedulerDriver driver, Protos.MasterInfo masterInfo) {
            scheduler.subscribed(SchedulerDriverV0.this, framework.id(), new Master().proto0(masterInfo));
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
