package io.yokelhost.mdns.model;

public interface DNSQuestion {
    DNSName name();
    DNSType type();
    int cls();
    boolean unicast();
}
