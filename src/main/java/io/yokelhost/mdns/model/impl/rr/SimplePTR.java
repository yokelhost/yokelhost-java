package io.yokelhost.mdns.model.impl.rr;

import io.yokelhost.mdns.model.DNSClass;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.rr.PTR;

public class SimplePTR extends AbstractDNSRecord implements PTR {

    public SimplePTR(DNSName name, long ttl, DNSName ptrName) {
        super(name, DNSType.PTR, DNSClass.IN, ttl);
        this.ptrName = ptrName;
    }

    @Override
    public DNSName ptrName() {
        return this.ptrName;
    }

    @Override
    public String toString() {
        return "[" + name() + " " + cls() + " " + type() + " " + ptrName + " " + ttl() + "]";
    }

    private final DNSName ptrName;
}
