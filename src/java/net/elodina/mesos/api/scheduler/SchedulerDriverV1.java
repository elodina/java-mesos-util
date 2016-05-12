package net.elodina.mesos.api.scheduler;

import com.google.protobuf.ExtensionRegistry;
import com.googlecode.protobuf.format.JsonFormat;
import net.elodina.mesos.api.Framework;
import net.elodina.mesos.api.Offer;
import net.elodina.mesos.api.Task;
import net.elodina.mesos.util.Request;
import org.apache.mesos.v1.Protos;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.apache.mesos.v1.scheduler.Protos.Call;
import static org.apache.mesos.v1.scheduler.Protos.Event;

public class SchedulerDriverV1 extends SchedulerDriver {
    private Scheduler scheduler;
    private Framework framework;
    private String masterUrl;

    public SchedulerDriverV1(Scheduler scheduler, Framework framework, String masterUrl) {
        this.scheduler = scheduler;
        this.framework = framework;
        this.masterUrl = masterUrl;
    }

    @Override
    public void declineOffer(String id) {
        Call.Decline.Builder decline = Call.Decline.newBuilder();
        decline.addOfferIds(Protos.OfferID.newBuilder().setValue(id));

        Call call = Call.newBuilder()
            .setFrameworkId(Protos.FrameworkID.newBuilder().setValue(framework.id()))
            .setType(Call.Type.DECLINE)
            .setDecline(decline)
            .build();

        sendCall(call);
    }

    @Override
    public void launchTask(String offerId, Task task) {
        Protos.Offer.Operation.Builder operation = Protos.Offer.Operation.newBuilder()
            .setType(Protos.Offer.Operation.Type.LAUNCH)
            .setLaunch(Protos.Offer.Operation.Launch.newBuilder().addTaskInfos(task.proto1()));

        Call.Accept.Builder accept = Call.Accept.newBuilder()
            .addOfferIds(Protos.OfferID.newBuilder().setValue(offerId))
            .addOperations(operation);

        Call call = Call.newBuilder()
            .setFrameworkId(Protos.FrameworkID.newBuilder().setValue(framework.id()))
            .setType(Call.Type.ACCEPT)
            .setAccept(accept)
            .build();

        sendCall(call);
    }

    @Override
    public void reconcileTasks(List<String> ids) {
        Call.Reconcile.Builder reconcile = Call.Reconcile.newBuilder();

        for (String id : ids)
            reconcile.addTasks(Call.Reconcile.Task.newBuilder().setTaskId(Protos.TaskID.newBuilder().setValue(id)));

        Call call = Call.newBuilder()
            .setFrameworkId(Protos.FrameworkID.newBuilder().setValue(framework.id()))
            .setType(Call.Type.RECONCILE)
            .setReconcile(reconcile)
            .build();

        sendCall(call);
    }

    @Override
    public void killTask(String id) {
        Call.Kill.Builder kill = Call.Kill.newBuilder();
        kill.setTaskId(Protos.TaskID.newBuilder().setValue(id));

        Call call = Call.newBuilder()
            .setFrameworkId(Protos.FrameworkID.newBuilder().setValue(framework.id()))
            .setType(Call.Type.KILL)
            .setKill(kill)
            .build();

        sendCall(call);
    }

    private void sendCall(Call call) {
        try {
            StringWriter body = new StringWriter();
            new JsonFormat().print(call, body);
            debug("[call] " + body);

            Request request = new Request(apiUrl())
                .method(Request.Method.POST)
                .contentType("application/json")
                .accept("application/json")
                .body(("" + body).getBytes("utf-8"));

            Request.Response response = request.send();
            debug("[response] " + response.code() + " - " + response.message() + (response.body() != null ? ": " + new String(response.body()) : ""));
            if (response.code() != 202)
                throw new IllegalStateException("Response: " + response.code() + " - " + response.message() + (response.body() != null ? ": " + new String(response.body()) : ""));

        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    @Override
    public boolean run() throws IOException {
        String url = apiUrl();

        try (Request request = new Request(url)) {
            request.method(Request.Method.POST)
                .contentType("application/json")
                .accept("application/json");

            StringWriter requestJson = new StringWriter();
            new JsonFormat().print(subscribeCall(), requestJson);
            request.body(requestJson.toString().getBytes("utf-8"));
            debug("[subscribe] " + requestJson);

            InputStream stream = request.send(true).stream();
            for (;;) {
                int size = readChunkSize(stream);
                byte[] buffer = readChunk(stream, size);

                String responseJson = new String(buffer).replaceAll("\\\\/", "/");
                Event.Builder event = Event.newBuilder();
                new JsonFormat().merge(responseJson, ExtensionRegistry.getEmptyRegistry(), event);

                debug("[event] " + responseJson);
                onEvent(event.build());
            }
        }
    }

    private String apiUrl() {
        String url = masterUrl;

        if (!url.startsWith("http://")) url = "http://" + url;
        if (!url.endsWith("/")) url += "/";
        url += "api/v1/scheduler";

        return url;
    }

    private int readChunkSize(InputStream stream) throws IOException {
        byte b;

        String s = "";
        while ((b = (byte) stream.read()) != '\n')
            s += (char)b;

        return Integer.parseInt(s);
    }

    private byte[] readChunk(InputStream stream, int size) throws IOException {
        byte[] buffer = new byte[size];

        for (int i = 0; i < size; i++)
            buffer[i] = (byte) stream.read();

        return buffer;
    }

    private void onEvent(Event event) {
        switch (event.getType()) {
            case SUBSCRIBED:
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
                sendCall(acknowledgeCall(status));
                break;
            case MESSAGE:
                Event.Message message = event.getMessage();
                scheduler.message(message.getExecutorId().getValue(), message.getAgentId().getValue(), message.getData().toByteArray());
                break;
            case RESCIND: case FAILURE: case ERROR: case HEARTBEAT:
                break; // ignore
            default:
                throw new UnsupportedOperationException("Unsupported event: " + event);
        }
    }

    private Call subscribeCall() {
        Protos.FrameworkInfo framework = this.framework.proto1();

        Call.Subscribe.Builder subscribe = Call.Subscribe.newBuilder();
        subscribe.setFrameworkInfo(framework);

        Call.Builder call = Call.newBuilder();
        call.setSubscribe(subscribe);
        call.setType(Call.Type.SUBSCRIBE);
        if (framework.hasId()) call.setFrameworkId(framework.getId());

        return call.build();
    }

    private Call acknowledgeCall(Protos.TaskStatus status) {
        Protos.FrameworkInfo framework = this.framework.proto1();

        Call.Acknowledge.Builder acknowledge = Call.Acknowledge.newBuilder()
            .setAgentId(status.getAgentId())
            .setTaskId(status.getTaskId())
            .setUuid(status.getUuid());

        Call.Builder call = Call.newBuilder();
        call.setAcknowledge(acknowledge);
        call.setType(Call.Type.ACKNOWLEDGE);
        if (framework.hasId()) call.setFrameworkId(framework.getId());

        return call.build();
    }

    @Override
    public void stop() {
        Call call = Call.newBuilder()
            .setType(Call.Type.TEARDOWN)
            .build();

        sendCall(call);
    }
}
