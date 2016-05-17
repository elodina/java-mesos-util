package net.elodina.mesos.api.executor;

import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import net.elodina.mesos.api.Framework;
import net.elodina.mesos.api.Slave;
import net.elodina.mesos.api.Task;
import org.apache.log4j.Logger;
import org.apache.mesos.MesosExecutorDriver;
import org.apache.mesos.Protos;

import java.io.IOError;
import java.io.IOException;

public class ExecutorDriverV0 implements ExecutorDriver {
    private static final Logger logger = Logger.getLogger(ExecutorDriverV0.class);

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
        Protos.TaskStatus _status = status.proto0();
        logger.debug("[sendStatus] " + _status);
        driver.sendStatusUpdate(_status);
    }

    @Override
    public void sendMessage(byte[] data) {
        logger.info("[sendMessage] " + new String(data));
        driver.sendFrameworkMessage(data);
    }

    private static String json(Message message) {
        StringBuilder buffer = new StringBuilder();
        try { new JsonFormat().print(message, buffer); }
        catch (IOException e) { throw new IOError(e); }
        return "" + buffer;
    }

    private class ExecutorV0 implements org.apache.mesos.Executor {
        @Override
        public void registered(org.apache.mesos.ExecutorDriver driver, Protos.ExecutorInfo executorInfo, Protos.FrameworkInfo frameworkInfo, Protos.SlaveInfo slaveInfo) {
            logger.debug("[registered] executor:" + json(executorInfo) + ", framework:" + json(frameworkInfo) + ", slave:" + json(slaveInfo));
            executor.registered(ExecutorDriverV0.this, new Task.Executor().proto0(executorInfo), new Framework().proto0(frameworkInfo), new Slave().proto0(slaveInfo));
        }

        @Override
        public void reregistered(org.apache.mesos.ExecutorDriver driver, Protos.SlaveInfo slaveInfo) {
            logger.debug("[reregistered] " + json(slaveInfo));
            executor.registered(ExecutorDriverV0.this, null, null, new Slave().proto0(slaveInfo));
        }

        @Override
        public void disconnected(org.apache.mesos.ExecutorDriver driver) {
            logger.debug("[disconnected]");
            executor.disconnected();
        }

        @Override
        public void launchTask(org.apache.mesos.ExecutorDriver driver, Protos.TaskInfo task) {
            logger.debug("[launchTask] " + json(task));
            executor.launchTask(new Task().proto0(task));
        }

        @Override
        public void killTask(org.apache.mesos.ExecutorDriver driver, Protos.TaskID taskId) {
            logger.debug("[killTask] " + json(taskId));
            executor.killTask(taskId.getValue());
        }

        @Override
        public void frameworkMessage(org.apache.mesos.ExecutorDriver driver, byte[] data) {
            logger.debug("[frameworkMessage] " + new String(data));
            executor.message(data);
        }

        @Override
        public void shutdown(org.apache.mesos.ExecutorDriver driver) {
            logger.debug("[shutdown]");
            executor.shutdown();
        }

        @Override
        public void error(org.apache.mesos.ExecutorDriver driver, String message) {
            logger.debug("[error] " + message);
            executor.error(message);
        }
    }
}
