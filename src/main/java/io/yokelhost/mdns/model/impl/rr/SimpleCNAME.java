package io.yokelhost.mdns.model.impl.rr;

import io.yokelhost.mdns.model.DNSClass;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.rr.CNAME;

public class SimpleCNAME extends AbstractDNSRecord implements CNAME {
    public SimpleCNAME(DNSName name, long ttl, DNSName cname) {
        super(name, DNSType.CNAME, DNSClass.IN, ttl);
        this.cname = cname;
    }

    @Override
    public DNSName cname() {
        return this.cname;
    }

    @Override
    public String toString() {
        return "[" + name() + " " + cls() + " " + type() + " " + cname + " " + ttl() + "]";
    }

    private final DNSName cname;
}
