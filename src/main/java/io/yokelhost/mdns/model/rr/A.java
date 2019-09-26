package io.yokelhost.mdns.model.rr;

import java.net.Inet4Address;

import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.impl.rr.SimpleA;

public interface A extends DNSRecord {
    static A create(String name, int ttl, Inet4Address addr) {
        return new SimpleA(DNSName.of(name), ttl, addr);
    }

    Inet4Address address();
}
