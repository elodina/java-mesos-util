package net.elodina.mesos.api.executor;

import net.elodina.mesos.api.Framework;
import net.elodina.mesos.api.Slave;
import net.elodina.mesos.api.Task;
import org.apache.mesos.MesosExecutorDriver;
import org.apache.mesos.Protos;

public class ExecutorDriverV0 implements ExecutorDriver {
    private Executor executor;
    private MesosExecutorDriver driver;

    public ExecutorDriverV0(Executor executor) {
        this.executor = executor;
        driver = new MesosExecutorDriver(new ExecutorV0());
    }

    @Override
    public boolean run() {
        Protos.Status status = driver.run();
        return status == Protos.Status.DRIVER_STOPPED;
    }

    @Override
    public void stop() {
        driver.stop();
    }

    @Override
    public void sendStatus(Task.Status status) {
        driver.sendStatusUpdate(status.proto0());
    }

    @Override
    public void sendMessage(byte[] data) {
        driver.sendFrameworkMessage(data);
    }

    private class ExecutorV0 implements org.apache.mesos.Executor {
        @Override
        public void registered(org.apache.mesos.ExecutorDriver driver, Protos.ExecutorInfo executorInfo, Protos.FrameworkInfo frameworkInfo, Protos.SlaveInfo slaveInfo) {
            executor.registered(ExecutorDriverV0.this, new Task.Executor().proto0(executorInfo), new Framework().proto0(frameworkInfo), new Slave().proto0(slaveInfo));
        }

        @Override
        public void reregistered(org.apache.mesos.ExecutorDriver driver, Protos.SlaveInfo slaveInfo) {
            executor.registered(ExecutorDriverV0.this, null, null, new Slave().proto0(slaveInfo));
        }

        @Override
        public void disconnected(org.apache.mesos.ExecutorDriver driver) {
            executor.disconnected();
        }

        @Override
        public void launchTask(org.apache.mesos.ExecutorDriver driver, Protos.TaskInfo task) {
            executor.launchTask(new Task().proto0(task));
        }

        @Override
        public void killTask(org.apache.mesos.ExecutorDriver driver, Protos.TaskID taskId) {
            executor.killTask(taskId.getValue());
        }

        @Override
        public void frameworkMessage(org.apache.mesos.ExecutorDriver driver, byte[] data) {
            executor.message(data);
        }

        @Override
        public void shutdown(org.apache.mesos.ExecutorDriver driver) {
            executor.shutdown();
        }

        @Override
        public void error(org.apache.mesos.ExecutorDriver driver, String message) {
            executor.error(message);
        }
    }
}
