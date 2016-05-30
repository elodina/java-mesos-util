package net.elodina.mesos.api.driver;

import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import net.elodina.mesos.api.Executor;
import net.elodina.mesos.api.Framework;
import net.elodina.mesos.api.Slave;
import net.elodina.mesos.api.Task;
import net.elodina.mesos.util.Base64;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.mesos.v1.executor.Protos.Call;
import static org.apache.mesos.v1.executor.Protos.Call.*;
import static org.apache.mesos.v1.executor.Protos.Event;

public class ExecutorDriverV1 extends AbstractDriverV1 implements ExecutorDriver {
    private List<Task.Status> unackedStatuses = new CopyOnWriteArrayList<>();
    private List<Task> unackedTasks = new CopyOnWriteArrayList<>();

    private Executor executor;

    public ExecutorDriverV1(Executor executor) {
        super(fixUrl(System.getenv("MESOS_AGENT_ENDPOINT")) + "/api/v1/executor");
        this.executor = executor;
    }

    @Override
    protected Call subscribeCall() {
        Subscribe.Builder subscribe = Subscribe.newBuilder();

        for (Task.Status status : unackedStatuses) {
            Update.Builder update = Update.newBuilder();
            update.setStatus(status.proto1());
            subscribe.addUnacknowledgedUpdates(update);
        }

        for (Task task : unackedTasks)
            subscribe.addUnacknowledgedTasks(task.proto1());

        return newCall(subscribe);
    }

    @Override
    protected void onEvent(String json) {
        Event.Builder builder = Event.newBuilder();
        try { new JsonFormat().merge(json, ExtensionRegistry.getEmptyRegistry(), builder); }
        catch (JsonFormat.ParseException e) { throw new DriverException(e); }
        Event event = builder.build();

        switch (event.getType()) {
            case SUBSCRIBED:
                state = State.SUBSCRIBED;
                Event.Subscribed subscribed = event.getSubscribed();
                executor.registered(this, new Task.Executor().proto1(subscribed.getExecutorInfo()), new Framework().proto1(subscribed.getFrameworkInfo()), new Slave().proto1(subscribed.getAgentInfo()));
                break;
            case LAUNCH:
                Event.Launch launch = event.getLaunch();
                Task task = new Task().proto1(launch.getTask());
                unackedTasks.add(task);
                executor.launchTask(task);
                break;
            case KILL:
                Event.Kill kill = event.getKill();
                executor.killTask(kill.getTaskId().getValue());
                break;
            case MESSAGE:
                Event.Message message = event.getMessage();
                executor.message(message.getData().toByteArray());
                break;
            case ERROR:
                Event.Error error = event.getError();
                executor.error(error.getMessage());
                break;
            case SHUTDOWN:
                executor.shutdown();
                break;
            case ACKNOWLEDGED:
                Event.Acknowledged acknowledged = event.getAcknowledged();
                onAcknowledged(acknowledged);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported event: " + event);
        }
    }

    @Override
    public void stop() {
        state = State.STOPPED;
    }

    @Override
    public void sendStatus(Task.Status status) {
        if (status.time() == null) status.time(new Date());
        if (status.uuid() == null) status.uuid(new String(Base64.encode(nextUuid())).getBytes());

        if (status.source() == null) status.source(Task.Status.Source.EXECUTOR);
        if (status.executorId() == null) status.executorId(System.getenv("MESOS_EXECUTOR_ID"));

        unackedStatuses.add(status);

        Update.Builder update = Update.newBuilder();
        update.setStatus(status.proto1());
        sendCall(newCall(update));
    }

    private static byte[] nextUuid() {
        UUID uuid = UUID.randomUUID();
        long hi = uuid.getMostSignificantBits();
        long lo = uuid.getLeastSignificantBits();
        return ByteBuffer.allocate(16).putLong(hi).putLong(lo).array();
    }

    @Override
    public void sendMessage(byte[] data) {
        Call.Message.Builder message = Call.Message.newBuilder();
        message.setData(ByteString.copyFrom(data));
        sendCall(newCall(message));
    }

    private void onAcknowledged(Event.Acknowledged acknowledged) {
        int ackedTasks = 0, ackedStatuses = 0;
        for (Task.Status status : unackedStatuses)
            if (Arrays.equals(status.uuid(), acknowledged.getUuid().toByteArray())) {
                unackedStatuses.remove(status);
                ackedStatuses ++;
            }

        for (Task task : unackedTasks)
            if (task.id().equals(acknowledged.getTaskId().getValue())) {
                unackedTasks.remove(task);
                ackedTasks ++;
            }

        logger.debug("ackedStatuses: " + ackedStatuses + ", ackedTasks: " + ackedTasks + ", unackedStatuses: " + unackedStatuses.size() + ", unackedTasks: " + unackedTasks.size());
    }

    private Call newCall(GeneratedMessage.Builder builder) {
        Message obj = builder.build();

        Call.Builder call = newBuilder();
        call.setExecutorId(org.apache.mesos.v1.Protos.ExecutorID.newBuilder().setValue(System.getenv("MESOS_EXECUTOR_ID")));
        call.setFrameworkId(org.apache.mesos.v1.Protos.FrameworkID.newBuilder().setValue(System.getenv("MESOS_FRAMEWORK_ID")));

        if (obj instanceof Subscribe) {
            call.setSubscribe((Subscribe) obj);
            call.setType(Call.Type.SUBSCRIBE);
        } else if (obj instanceof Call.Message) {
            call.setMessage((Call.Message) obj);
            call.setType(Call.Type.MESSAGE);
        } else if (obj instanceof Update) {
            call.setUpdate((Update) obj);
            call.setType(Call.Type.UPDATE);
        } else
            throw new UnsupportedOperationException("Unsupported object " + obj);

        return call.build();
    }
}
