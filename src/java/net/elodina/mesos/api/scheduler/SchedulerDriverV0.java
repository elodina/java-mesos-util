package net.elodina.mesos.api.scheduler;

import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import net.elodina.mesos.api.*;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SchedulerDriverV0 extends net.elodina.mesos.api.scheduler.SchedulerDriver {
    private Scheduler scheduler;
    private Framework framework;

    private MesosSchedulerDriver driver;

    public SchedulerDriverV0(Scheduler scheduler, Framework framework, String master) { this(scheduler, framework, master, null); }

    public SchedulerDriverV0(Scheduler scheduler, Framework framework, String master, Cred cred) {
        this.scheduler = scheduler;
        this.framework = framework;

        if (master.startsWith("http://")) master = master.substring("http://".length());

        if (cred != null) driver = new MesosSchedulerDriver(new SchedulerV0(), framework.proto0(), master, cred.proto0());
        else driver = new MesosSchedulerDriver(new SchedulerV0(), framework.proto0(), master);
    }

    @Override
    public void declineOffer(String id) {
        Protos.OfferID offerId = Protos.OfferID.newBuilder().setValue(id).build();

        debug("[declineOffer] " + json(offerId));
        driver.declineOffer(offerId);
    }

    @Override
    public void launchTask(String offerId, Task task) {
        Protos.OfferID _offerId = Protos.OfferID.newBuilder().setValue(offerId).build();
        List<Protos.TaskInfo> tasks = Arrays.asList(task.proto0());

        debug("[launchTasks] offerId:" + json(_offerId) + ", tasks:" + json(tasks));
        driver.launchTasks(_offerId, tasks);
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

        debug("[reconcileTasks] " + json(statuses));
        driver.reconcileTasks(statuses);
    }

    @Override
    public void killTask(String id) {
        debug("[killTask] " + id);
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

    private String json(List<? extends Message> messages) {
        String s = "";

        for (Message message : messages) {
            if (!s.isEmpty()) s += ", ";
            s += json(message);
        }
        s = "[" + s + "]";

        return s;
    }

    private static String json(Message message) {
        StringBuilder buffer = new StringBuilder();
        try { new JsonFormat().print(message, buffer); }
        catch (IOException e) { throw new IOError(e); }
        return "" + buffer;
    }

    private class SchedulerV0 implements org.apache.mesos.Scheduler {
        @Override
        public void registered(SchedulerDriver driver, Protos.FrameworkID frameworkId, Protos.MasterInfo masterInfo) {
            debug("[registered] id:" + json(frameworkId) + ", master:" + json(masterInfo));
            framework.id(framework.id());
            scheduler.subscribed(SchedulerDriverV0.this, frameworkId.getValue(), new Master().proto0(masterInfo));
        }

        @Override
        public void reregistered(SchedulerDriver driver, Protos.MasterInfo masterInfo) {
            debug("[reregistered] " + json(masterInfo));
            scheduler.subscribed(SchedulerDriverV0.this, framework.id(), new Master().proto0(masterInfo));
        }

        @Override
        public void resourceOffers(SchedulerDriver driver, List<Protos.Offer> offers) {
            debug("[resourceOffers] " + json(offers));
            List<Offer> _offers = new ArrayList<>();
            for (Protos.Offer offer : offers) _offers.add(new Offer().proto0(offer));
            scheduler.offers(_offers);
        }

        @Override
        public void offerRescinded(SchedulerDriver driver, Protos.OfferID offerId) {
            debug("[offerRescinded] " + json(offerId));
        }

        @Override
        public void statusUpdate(SchedulerDriver driver, Protos.TaskStatus status) {
            debug("[statusUpdate] " + json(status));
            scheduler.status(new Task.Status().proto0(status));
        }

        @Override
        public void frameworkMessage(SchedulerDriver driver, Protos.ExecutorID executorId, Protos.SlaveID slaveId, byte[] data) {
            debug("[frameworkMessage] executor:" + json(executorId) + ", slave:" + json(slaveId) + ", data:" + new String(data));
            scheduler.message(executorId.getValue(), slaveId.getValue(), data);
        }

        @Override
        public void disconnected(SchedulerDriver driver) {
            debug("[disconnected]");
            scheduler.disconnected();
        }

        @Override
        public void slaveLost(SchedulerDriver driver, Protos.SlaveID slaveId) {
            debug("[slaveLost] " + json(slaveId));
        }

        @Override
        public void executorLost(SchedulerDriver driver, Protos.ExecutorID executorId, Protos.SlaveID slaveId, int status) {
            debug("[executorLost] executor" + json(executorId) + ", slave:" + json(slaveId) + ", status:" + status);
        }

        @Override
        public void error(SchedulerDriver driver, String message) {
            debug("[error] " + message);
        }
    }
}
