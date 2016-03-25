/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.elodina.mesos.test;

import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestSchedulerDriver implements SchedulerDriver {
    public Protos.Status status = Protos.Status.DRIVER_RUNNING;

    public List<String> declinedOffers = new ArrayList<>();
    public List<String> acceptedOffers = new ArrayList<>();

    public List<Protos.TaskInfo> launchedTasks = new ArrayList<>();
    public List<String> killedTasks = new ArrayList<>();
    public List<String> reconciledTasks = new ArrayList<>();

    public List<Message> sentFrameworkMessages = new ArrayList<>();

    public Protos.Status declineOffer(Protos.OfferID id) {
        declinedOffers.add(id.getValue());
        return status;
    }

    public Protos.Status declineOffer(Protos.OfferID id, Protos.Filters filters) {
        declinedOffers.add(id.getValue());
        return status;
    }

    public Protos.Status launchTasks(Protos.OfferID offerId, Collection<Protos.TaskInfo> tasks) {
        acceptedOffers.add(offerId.getValue());
        launchedTasks.addAll(tasks);
        return status;
    }

    public Protos.Status launchTasks(Protos.OfferID offerId, Collection<Protos.TaskInfo> tasks, Protos.Filters filters) {
        acceptedOffers.add(offerId.getValue());
        launchedTasks.addAll(tasks);
        return status;
    }

    public Protos.Status launchTasks(Collection<Protos.OfferID> offerIds, Collection<Protos.TaskInfo> tasks) {
        for (Protos.OfferID offerId : offerIds) acceptedOffers.add(offerId.getValue());
        launchedTasks.addAll(tasks);
        return status;
    }

    public Protos.Status launchTasks(Collection<Protos.OfferID> offerIds, Collection<Protos.TaskInfo> tasks, Protos.Filters filters) {
        for (Protos.OfferID offerId : offerIds) acceptedOffers.add(offerId.getValue());
        launchedTasks.addAll(tasks);
        return status;
    }

    public Protos.Status stop() { return status = Protos.Status.DRIVER_STOPPED; }

    public Protos.Status stop(boolean failover) { return status = Protos.Status.DRIVER_STOPPED; }

    public Protos.Status killTask(Protos.TaskID id) {
        killedTasks.add(id.getValue());
        return status;
    }

    public Protos.Status requestResources(Collection<Protos.Request> requests) { throw new UnsupportedOperationException(); }

    public Protos.Status sendFrameworkMessage(Protos.ExecutorID executorId, Protos.SlaveID slaveId, byte[] data) {
        sentFrameworkMessages.add(new Message(executorId.getValue(), slaveId.getValue(), data));
        return status;
    }

    public Protos.Status join() { throw new UnsupportedOperationException(); }

    public Protos.Status reconcileTasks(Collection<Protos.TaskStatus> statuses) {
        if (statuses.isEmpty()) reconciledTasks.add("");
        for (Protos.TaskStatus status : statuses) reconciledTasks.add(status.getTaskId().getValue());
        return status;
    }

    public Protos.Status reviveOffers() { throw new UnsupportedOperationException(); }

    public Protos.Status run() { return status = Protos.Status.DRIVER_RUNNING; }

    public Protos.Status abort() { return status = Protos.Status.DRIVER_ABORTED; }

    public Protos.Status start() { return status = Protos.Status.DRIVER_RUNNING; }

    public Protos.Status acceptOffers(Collection<Protos.OfferID> offerIds, Collection<Protos.Offer.Operation> operations, Protos.Filters filters) { throw new UnsupportedOperationException(); }

    public Protos.Status acknowledgeStatusUpdate(Protos.TaskStatus status) { throw new UnsupportedOperationException(); }

    public Protos.Status suppressOffers() { throw new UnsupportedOperationException(); }

    public static class Message {
        public String executorId;
        public String slaveId;
        public byte[] data;

        public Message(String executorId, String slaveId, byte[] data) {
            this.executorId = executorId;
            this.slaveId = slaveId;
            this.data = data;
        }
    }
}
