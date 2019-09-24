package io.yokelhost.mdns.model;

public interface DNSQuestion {
    DNSName name();
    DNSType type();
    DNSClass cls();
}
