package io.yokelhost.mdns.model.impl.rr;

import java.util.List;

import io.yokelhost.mdns.model.DNSClass;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.rr.NSEC;

public class SimpleNSEC extends AbstractDNSRecord implements NSEC {
    public SimpleNSEC(DNSName name, long ttl, DNSName next, List<DNSType> types) {
        super(name, DNSType.NSEC, DNSClass.IN, ttl);
        this.next = next;
        this.types = types;
    }

    public DNSName next() {
        return this.next;
    }

    @Override
    public List<DNSType> types() {
        return this.types;
    }

    @Override
    public String toString() {
        return "[" + name() + " " + cls() + " " + type() + " " + this.next + " " + ttl() + "]";
    }

    private final DNSName next;

    private final List<DNSType> types;
}
