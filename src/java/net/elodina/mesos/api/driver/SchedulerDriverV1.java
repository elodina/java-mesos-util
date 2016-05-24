package net.elodina.mesos.api.driver;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import net.elodina.mesos.api.Framework;
import net.elodina.mesos.api.Offer;
import net.elodina.mesos.api.Scheduler;
import net.elodina.mesos.api.Task;
import org.apache.mesos.v1.Protos;

import java.util.ArrayList;
import java.util.List;

import static org.apache.mesos.v1.scheduler.Protos.Call;
import static org.apache.mesos.v1.scheduler.Protos.Event;

public class SchedulerDriverV1 extends AbstractDriverV1 implements SchedulerDriver {
    private Scheduler scheduler;
    private Framework framework;

    public SchedulerDriverV1(Scheduler scheduler, Framework framework, String masterUrl) {
        super(fixUrl(masterUrl) + "/api/v1/scheduler");
        this.scheduler = scheduler;
        this.framework = framework;
    }

    @Override
    public void declineOffer(String id) {
        Call.Decline.Builder decline = Call.Decline.newBuilder();
        decline.addOfferIds(Protos.OfferID.newBuilder().setValue(id));
        sendCall(newCall(decline));
    }

    @Override
    public void launchTask(String offerId, Task task) {
        Protos.Offer.Operation.Builder operation = Protos.Offer.Operation.newBuilder()
            .setType(Protos.Offer.Operation.Type.LAUNCH)
            .setLaunch(Protos.Offer.Operation.Launch.newBuilder().addTaskInfos(task.proto1()));

        Call.Accept.Builder accept = Call.Accept.newBuilder()
            .addOfferIds(Protos.OfferID.newBuilder().setValue(offerId))
            .addOperations(operation);

        sendCall(newCall(accept));
    }

    @Override
    public void reconcileTasks(List<String> ids) {
        Call.Reconcile.Builder reconcile = Call.Reconcile.newBuilder();

        for (String id : ids)
            reconcile.addTasks(Call.Reconcile.Task.newBuilder().setTaskId(Protos.TaskID.newBuilder().setValue(id)));

        sendCall(newCall(reconcile));
    }

    @Override
    public void killTask(String id) {
        Call.Kill.Builder kill = Call.Kill.newBuilder()
            .setTaskId(Protos.TaskID.newBuilder().setValue(id));
        sendCall(newCall(kill));
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
                framework.id(subscribed.getFrameworkId().getValue());
                scheduler.subscribed(this, subscribed.getFrameworkId().getValue(), null);
                break;
            case OFFERS:
                List<Offer> offers = new ArrayList<>();
                for (Protos.Offer o : event.getOffers().getOffersList()) offers.add(new Offer().proto1(o));
                scheduler.offers(offers);
                break;
            case UPDATE:
                Protos.TaskStatus status = event.getUpdate().getStatus();
                scheduler.status(new Task.Status().proto1(status));
                if (status.hasUuid()) sendCall(acknowledgeCall(status));
                break;
            case MESSAGE:
                Event.Message message = event.getMessage();
                scheduler.message(message.getExecutorId().getValue(), message.getAgentId().getValue(), message.getData().toByteArray());
                break;
            case ERROR:
                Event.Error error = event.getError();
                throw new DriverException(error.getMessage(), isUnrecoverable(error));
            case RESCIND: case FAILURE: case HEARTBEAT:
                break; // ignore
            default:
                throw new UnsupportedOperationException("Unsupported event: " + event);
        }
    }

    private boolean isUnrecoverable(Event.Error error) {
        return this.state != State.SUBSCRIBED && error.getMessage().equals("Framework has been removed");
    }

    @Override
    protected Call subscribeCall() {
        Call.Subscribe.Builder subscribe = Call.Subscribe.newBuilder()
            .setFrameworkInfo(framework.proto1());
        return newCall(subscribe);
    }

    private Call acknowledgeCall(Protos.TaskStatus status) {
        Call.Acknowledge.Builder acknowledge = Call.Acknowledge.newBuilder()
            .setAgentId(status.getAgentId())
            .setTaskId(status.getTaskId())
            .setUuid(status.getUuid());

        return newCall(acknowledge);
    }

    private Call newCall(GeneratedMessage.Builder builder) {
        Message obj = builder != null ? builder.build() : null;
        Protos.FrameworkInfo framework = this.framework.proto1();

        Call.Builder call = Call.newBuilder();
        if (framework.hasId()) call.setFrameworkId(framework.getId());

        if (obj == null) {
            call.setType(Call.Type.TEARDOWN);
        } else if (obj instanceof Call.Subscribe) {
            call.setSubscribe((Call.Subscribe) obj);
            call.setType(Call.Type.SUBSCRIBE);
        } else if (obj instanceof Call.Acknowledge) {
            call.setAcknowledge((Call.Acknowledge) obj);
            call.setType(Call.Type.ACKNOWLEDGE);
        } else if (obj instanceof Call.Kill) {
            call.setKill((Call.Kill) obj);
            call.setType(Call.Type.KILL);
        } else if (obj instanceof Call.Reconcile) {
            call.setReconcile((Call.Reconcile) obj);
            call.setType(Call.Type.RECONCILE);
        } else if (obj instanceof Call.Accept) {
            call.setAccept((Call.Accept) obj);
            call.setType(Call.Type.ACCEPT);
        } else if (obj instanceof Call.Decline) {
            call.setDecline((Call.Decline) obj);
            call.setType(Call.Type.DECLINE);
        } else
            throw new UnsupportedOperationException("Unsupported object " + obj);

        return call.build();
    }

    @Override
    public void stop() {
        sendCall(newCall(null));
        state = State.STOPPED;
    }
}
