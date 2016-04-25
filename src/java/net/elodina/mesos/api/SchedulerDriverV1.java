package net.elodina.mesos.api;

import com.google.protobuf.ExtensionRegistry;
import com.googlecode.protobuf.format.JsonFormat;
import net.elodina.mesos.util.Request;
import org.apache.mesos.v1.Protos;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.apache.mesos.v1.scheduler.Protos.Call;
import static org.apache.mesos.v1.scheduler.Protos.Event;

public class SchedulerDriverV1 extends Scheduler.Driver {
    private Scheduler scheduler;
    private Framework framework;
    private String masterUrl;

    public SchedulerDriverV1(Scheduler scheduler, Framework framework, String masterUrl) {
        this.scheduler = scheduler;
        this.framework = framework;
        this.masterUrl = masterUrl;
    }

    @Override
    public void declineOffer(String id) { sendCall(declineCall(id)); }

    @Override
    public void launchTask(String offerId, Task task) { sendCall(launchTaskCall(offerId, task)); }

    @Override
    public void reconcileTasks(List<String> ids) { sendCall(reconcileTasksCall(ids)); }

    @Override
    public void killTask(String id) { sendCall(killTaskCall(id)); }

    private void sendCall(Call call) {
        try {
            StringWriter body = new StringWriter();
            new JsonFormat().print(call, body);

            Request.Response response = new Request(apiUrl())
                .method(Request.Method.POST)
                .contentType("application/json; charset=utf-8")
                .body(("" + body).getBytes("utf-8"))
                .send();

            if (response.code() != 202)
                throw new IllegalStateException("Response code: " + response.code());

        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    @Override
    public boolean run() throws IOException {
        String url = apiUrl();

        HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
        try {
            c.setRequestMethod("POST");
            c.setRequestProperty("Content-Type", "application/json");
            c.setRequestProperty("Accept", "application/json");
            c.setDoOutput(true);

            StringWriter request = new StringWriter();
            new JsonFormat().print(subscribeCall(), request);
            c.getOutputStream().write(request.toString().getBytes("utf-8"));

            InputStream stream = c.getInputStream();
            for (;;) {
                int size = readChunkSize(stream);
                byte[] buffer = readChunk(stream, size);

                String response = new String(buffer).replaceAll("\\\\/", "/");
                Event.Builder event = Event.newBuilder();
                new JsonFormat().merge(response, ExtensionRegistry.getEmptyRegistry(), event);

                onEvent(event.build());
            }
        } finally {
            c.disconnect();
        }
    }

    private String apiUrl() {
        return masterUrl + (masterUrl.endsWith("/") ? "" : "/") + "api/v1/scheduler";
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
                scheduler.registered(this, subscribed.getFrameworkId().getValue(), null);
                break;
            case OFFERS:
                List<Offer> offers = new ArrayList<>();
                for (Protos.Offer o : event.getOffers().getOffersList()) offers.add(new Offer().proto1(o));
                scheduler.offers(offers);
                break;
            case UPDATE:
                Protos.TaskStatus status = event.getUpdate().getStatus();
                scheduler.status(new Task.Status().proto1(status));
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

    private Call declineCall(String offerId) {
        Call.Decline.Builder decline = Call.Decline.newBuilder();
        decline.addOfferIds(Protos.OfferID.newBuilder().setValue(offerId));

        return Call.newBuilder()
            .setDecline(decline)
            .setType(Call.Type.DECLINE)
            .build();
    }

    private Call launchTaskCall(String offerId, Task task) {
        Protos.Offer.Operation.Builder operation = Protos.Offer.Operation.newBuilder()
            .setType(Protos.Offer.Operation.Type.LAUNCH)
            .setLaunch(Protos.Offer.Operation.Launch.newBuilder().addTaskInfos(task.proto1()));

        Call.Accept.Builder accept = Call.Accept.newBuilder();
        accept.addOfferIds(Protos.OfferID.newBuilder().setValue(offerId));
        accept.addOperations(operation);

        return Call.newBuilder()
            .setType(Call.Type.DECLINE)
            .setAccept(accept)
            .build();
    }

    private Call killTaskCall(String id) {
        Call.Kill.Builder kill = Call.Kill.newBuilder();
        kill.setTaskId(Protos.TaskID.newBuilder().setValue(id));

        return Call.newBuilder()
            .setKill(kill)
            .setType(Call.Type.KILL)
            .build();
    }

    private Call reconcileTasksCall(List<String> ids) {
        Call.Reconcile.Builder reconcile = Call.Reconcile.newBuilder();

        for (String id : ids)
            reconcile.addTasks(Call.Reconcile.Task.newBuilder().setTaskId(Protos.TaskID.newBuilder().setValue(id)));

        return Call.newBuilder()
            .setReconcile(reconcile)
            .setType(Call.Type.RECONCILE)
            .build();
    }

    private Call tearDownCall() {
        return Call.newBuilder()
            .setType(Call.Type.TEARDOWN)
            .build();
    }

    @Override
    public void stop() {
        sendCall(tearDownCall());
    }
}
