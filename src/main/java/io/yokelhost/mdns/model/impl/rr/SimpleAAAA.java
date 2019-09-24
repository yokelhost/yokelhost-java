package io.yokelhost.mdns.model.impl.rr;

import java.net.Inet6Address;

import io.yokelhost.mdns.model.DNSClass;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.rr.AAAA;

public class SimpleAAAA extends AbstractDNSRecord implements AAAA {

    public SimpleAAAA(DNSName name, DNSType type, DNSClass cls, long ttl, Inet6Address address) {
        super(name, type, cls, ttl);
        this.address = address;
    }

    @Override
    public Inet6Address address() {
        return this.address;
    }

    @Override
    public String toString() {
        return "[" + name() + " " + cls() + " " + type() + " " + address + " " + ttl() + "]";
    }

    private final Inet6Address address;
}
