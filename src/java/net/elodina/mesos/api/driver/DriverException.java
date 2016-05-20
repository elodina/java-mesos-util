package net.elodina.mesos.api.driver;

@SuppressWarnings("UnusedDeclaration")
public class DriverException extends RuntimeException {
    private boolean unrecoverable;

    public DriverException(String message) { this(message, null, false); }

    public DriverException(String message, boolean unrecoverable) { this(message, null, unrecoverable); }

    public DriverException(Throwable cause) { this(null, cause); }

    public DriverException(Throwable cause, boolean unrecoverable) { this(null, cause, unrecoverable); }

    public DriverException(String message, Throwable cause) { this(message, cause, false); }

    public DriverException(String message, Throwable cause, boolean unrecoverable) {
        super(message, cause);
        this.unrecoverable = unrecoverable;
    }

    public boolean isUnrecoverable() { return unrecoverable; }
}
