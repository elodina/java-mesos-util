package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;

public class Cred extends Message {
    private String principal;
    private String secret;

    public Cred() {} // deserialization
    public Cred(String principal, String secret) {
        if (principal == null || secret == null) throw new NullPointerException();
        this.principal = principal;
        this.secret = secret;
    }

    public Cred(String s) {
        int colon = s.indexOf(":");
        if (colon == -1) throw new IllegalArgumentException(s);

        principal = s.substring(0, colon);
        secret = s.substring(colon + 1, s.length());
    }

    public String principal() { return principal; }
    public String secret() { return secret; }

    @Override
    public org.apache.mesos.Protos.Credential proto0() {
        org.apache.mesos.Protos.Credential.Builder builder = org.apache.mesos.Protos.Credential.newBuilder();

        builder.setPrincipal(principal);
        builder.setSecret(secret);

        return builder.build();
    }

    @Override
    public Cred proto0(GeneratedMessage message) {
        org.apache.mesos.Protos.Credential cred = (org.apache.mesos.Protos.Credential) message;

        principal = cred.getPrincipal();
        secret = cred.getSecret();

        return this;
    }

    @Override
    public org.apache.mesos.v1.Protos.Credential proto1() {
        org.apache.mesos.v1.Protos.Credential.Builder builder = org.apache.mesos.v1.Protos.Credential.newBuilder();

        builder.setPrincipal(principal);
        builder.setSecret(secret);

        return builder.build();
    }

    @Override
    public Cred proto1(GeneratedMessage message) {
        org.apache.mesos.v1.Protos.Credential cred = (org.apache.mesos.v1.Protos.Credential) message;

        principal = cred.getPrincipal();
        secret = cred.getSecret();

        return this;
    }

    public int hashCode() {
        return 31 * principal.hashCode() + secret.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Cred)) return false;
        Cred other = (Cred) obj;
        return principal.equals(other.principal) && secret.equals(other.secret);
    }

    public String toString(boolean _short) {
        return principal + ":" + secret;
    }
}
