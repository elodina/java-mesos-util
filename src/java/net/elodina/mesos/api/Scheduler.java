package net.elodina.mesos.api;

import java.util.List;

public abstract class Scheduler {
    public abstract void registered(Driver driver, String id, Master master);

    public abstract void reregistered(Driver driver, Master master);

    public abstract void offers(List<Offer> offers);

    public abstract void taskStatus(Task.Status status);

    public class Driver {

    }
}
