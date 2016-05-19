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

import java.util.UUID;

import static org.apache.mesos.v1.executor.Protos.Call;
import static org.apache.mesos.v1.executor.Protos.Call.*;
import static org.apache.mesos.v1.executor.Protos.Event;

public class ExecutorDriverV1 extends AbstractDriverV1 implements ExecutorDriver {
    private Executor executor;

    public ExecutorDriverV1(Executor executor) {
        super(fixUrl(System.getenv("MESOS_AGENT_ENDPOINT")) + "/api/v1/executor");
        this.executor = executor;
    }

    @Override
    protected Call subscribeCall() { return newCall(Subscribe.newBuilder()); }

    @Override
    protected void onEvent(String json) {
        Event.Builder builder = Event.newBuilder();
        try { new JsonFormat().merge(json, ExtensionRegistry.getEmptyRegistry(), builder); }
        catch (JsonFormat.ParseException e) { throw new ApiException(e); }
        Event event = builder.build();

        switch (event.getType()) {
            case SUBSCRIBED:
                subscribed = true;
                Event.Subscribed subscribed = event.getSubscribed();
                executor.registered(this, new Task.Executor().proto1(subscribed.getExecutorInfo()), new Framework().proto1(subscribed.getFrameworkInfo()), new Slave().proto1(subscribed.getAgentInfo()));
                break;
            case LAUNCH:
                Event.Launch launch = event.getLaunch();
                executor.launchTask(new Task().proto1(launch.getTask()));
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
                break; // ignore
            default:
                throw new UnsupportedOperationException("Unsupported event: " + event);
        }
    }

    @Override
    public void stop() {
        stopped = true;
        throw new ApiException("stopped");
    }

    @Override
    public void sendStatus(Task.Status status) {
        Update.Builder update = Update.newBuilder();
        if (status.uuid() == null) status.uuid(Base64.encode("" + UUID.randomUUID()));
        if (status.source() == null) status.source(Task.Status.Source.EXECUTOR);

        update.setStatus(status.proto1());
        sendCall(newCall(update));
    }

    @Override
    public void sendMessage(byte[] data) {
        Call.Message.Builder message = Call.Message.newBuilder();
        message.setData(ByteString.copyFrom(data));
        sendCall(newCall(message));
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
