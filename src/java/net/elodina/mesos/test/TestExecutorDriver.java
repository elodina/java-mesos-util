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

import org.apache.mesos.ExecutorDriver;
import org.apache.mesos.Protos;

import java.util.ArrayList;
import java.util.List;

public class TestExecutorDriver implements ExecutorDriver {
    public Protos.Status status = Protos.Status.DRIVER_RUNNING;
    public final List<Protos.TaskStatus> statusUpdates = new ArrayList<>();

    public Protos.Status start() {
        status = Protos.Status.DRIVER_RUNNING;
        return status;
    }

    public Protos.Status stop() {
        status = Protos.Status.DRIVER_STOPPED;
        return status;
    }

    public Protos.Status abort() {
        status = Protos.Status.DRIVER_ABORTED;
        return status;
    }

    public Protos.Status join() { return status; }

    public Protos.Status run() {
        status = Protos.Status.DRIVER_RUNNING;
        return status;
    }

    public Protos.Status sendStatusUpdate(Protos.TaskStatus status) {
        synchronized (statusUpdates) {
            statusUpdates.add(status);
            statusUpdates.notify();
        }

        return this.status;
    }

    public void waitForStatusUpdates(int count) throws InterruptedException {
        synchronized (statusUpdates) {
            while (statusUpdates.size() < count)
                statusUpdates.wait();
        }
    }

    public Protos.Status sendFrameworkMessage(byte[] message) { throw new UnsupportedOperationException(); }
}
