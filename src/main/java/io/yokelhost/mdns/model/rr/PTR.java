package io.yokelhost.mdns.model.rr;

import io.yokelhost.mdns.model.DNSName;

public interface PTR extends DNSRecord {
    DNSName ptrName();
}
