package io.yokelhost.mdns.model.impl.rr;

import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.rr.DNSRecord;

public class AbstractDNSRecord implements DNSRecord {
    protected AbstractDNSRecord(DNSName name, DNSType type, int cls, long ttl) {
        this.name = name;
        this.type = type;
        this.cls = cls;
        this.ttl = ttl;
    }
    @Override
    public DNSName name() {
        return this.name;
    }

    @Override
    public DNSType type() {
        return this.type;
    }

    @Override
    public int cls() {
        return this.cls;
    }

    @Override
    public long ttl() {
        return this.ttl;
    }

    private final DNSName name;

    private final DNSType type;

    private final int cls;

    private final long ttl;
}
