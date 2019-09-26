package io.yokelhost.mdns.model.impl.rr;

import io.yokelhost.mdns.model.DNSClass;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.rr.TXT;

public class SimpleTXT extends AbstractDNSRecord implements TXT {
    public SimpleTXT(DNSName name, long ttl, byte[] txt) {
        super(name, DNSType.TXT, DNSClass.IN, ttl);
        this.txt = txt;
    }

    @Override
    public byte[] txt() {
        return this.txt;
    }

    @Override
    public String toString() {
        return "[" + name() + " " + cls() + " " + type() + " " + new String(this.txt) + " " + ttl() + "]";
    }

    private final byte[] txt;
}
