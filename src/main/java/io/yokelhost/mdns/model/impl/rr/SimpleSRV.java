package io.yokelhost.mdns.model.impl.rr;

import io.yokelhost.mdns.model.DNSClass;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.rr.SRV;

public class SimpleSRV extends AbstractDNSRecord implements SRV {
    public SimpleSRV(DNSName name, DNSType type, DNSClass cls, long ttl, int prio, int weight, int port, DNSName target) {
        super(name, type, cls, ttl);
        this.prio = prio;
        this.weight = weight;
        this.port = port;
        this.target = target;
    }

    @Override
    public int prio() {
        return this.prio;
    }

    @Override
    public int weight() {
        return this.weight;
    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public DNSName target() {
        return this.target;
    }

    @Override
    public String toString() {
        return "[" + name() + " " + cls() + " " + type() + " " + this.prio + " " + this.weight + " " + this.port + " " + this.target + " " + ttl() + "]";
    }

    private final int prio;

    private final int weight;

    private final int port;

    private final DNSName target;
}
