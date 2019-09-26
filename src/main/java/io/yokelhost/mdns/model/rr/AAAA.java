package io.yokelhost.mdns.model.rr;

import java.net.Inet4Address;
import java.net.Inet6Address;

import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.impl.rr.SimpleA;
import io.yokelhost.mdns.model.impl.rr.SimpleAAAA;

public interface AAAA extends DNSRecord {
    Inet6Address address();

    static AAAA create(String name, int ttl, Inet6Address addr) {
        return new SimpleAAAA(DNSName.of(name), ttl, addr);
    }
}
