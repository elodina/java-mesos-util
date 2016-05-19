package net.elodina.mesos.api.driver;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.api.Executor;
import net.elodina.mesos.api.Framework;
import net.elodina.mesos.api.Slave;
import net.elodina.mesos.api.Task;
import net.elodina.mesos.util.Period;
import org.apache.mesos.v1.executor.Protos;

public class ExecutorDriverV1 extends AbstractDriverV1 implements ExecutorDriver {
    private Executor executor;
    private String agentUrl;

    private Period reconnectDelay = new Period("5s");
    private volatile boolean stopped;


    public ExecutorDriverV1(Executor executor, String agentUrl) {
        super(fixUrl(agentUrl) + "/api/v1/executor");
        this.executor = executor;
        this.agentUrl = agentUrl;
    }

    public Period getReconnectDelay() { return reconnectDelay; }
    public void setReconnectDelay(Period reconnectDelay) { this.reconnectDelay = reconnectDelay; }

    @Override
    protected GeneratedMessage subscribeCall() {
        return null; // todo
    }

    @Override
    protected void onEvent(GeneratedMessage obj) {
        Protos.Event event = (Protos.Event) obj;

        switch (event.getType()) {
            case SUBSCRIBED:
                subscribed = true;
                Protos.Event.Subscribed subscribed = event.getSubscribed();
                executor.registered(this, new Task.Executor().proto1(subscribed.getExecutorInfo()), new Framework().proto1(subscribed.getFrameworkInfo()), new Slave().proto1(subscribed.getAgentInfo()));
                break;
            case LAUNCH:
                Protos.Event.Launch launch = event.getLaunch();
                executor.launchTask(new Task().proto1(launch.getTask()));
                break;
            case KILL:
                Protos.Event.Kill kill = event.getKill();
                executor.killTask(kill.getTaskId().getValue());
                break;
            case MESSAGE:
                Protos.Event.Message message = event.getMessage();
                executor.message(message.getData().toByteArray());
                break;
            case ERROR:
                Protos.Event.Error error = event.getError();
                executor.error(error.getMessage());
                break;
            case SHUTDOWN:
                executor.shutdown();
                break;
            case ACKNOWLEDGED:
                break; // ignore
            default:
                throw new UnsupportedOperationException("Unsupported event: " + event);
        }
    }

    @Override
    public void stop() {
        // todo
    }

    @Override
    public void sendStatus(Task.Status status) {
        // todo
    }

    @Override
    public void sendMessage(byte[] data) {
        // todo
    }
}
