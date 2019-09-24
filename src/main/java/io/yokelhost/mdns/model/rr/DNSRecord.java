package io.yokelhost.mdns.model.rr;

import io.yokelhost.mdns.model.DNSClass;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;

public interface DNSRecord {
    DNSName name();
    DNSType type();
    DNSClass cls();
    long ttl();
}
