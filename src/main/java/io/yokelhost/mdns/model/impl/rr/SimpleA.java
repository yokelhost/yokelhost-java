package io.yokelhost.mdns.model.impl.rr;

import java.net.Inet4Address;

import io.yokelhost.mdns.model.DNSClass;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.rr.A;

public class SimpleA extends AbstractDNSRecord implements A {

    public SimpleA(DNSName name, long ttl, Inet4Address address) {
        super(name, DNSType.A, DNSClass.IN, ttl);
        this.address = address;
    }

    @Override
    public Inet4Address address() {
        return this.address;
    }

    @Override
    public String toString() {
        return "[" + name() + " " + cls() + " " + type() + " " + address + " " + ttl() + "]";
    }

    private final Inet4Address address;
}
