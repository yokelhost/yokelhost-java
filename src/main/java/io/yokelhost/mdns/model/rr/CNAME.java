package io.yokelhost.mdns.model.rr;

import io.yokelhost.mdns.model.DNSName;

public interface CNAME extends DNSRecord {
    DNSName cname();
}
