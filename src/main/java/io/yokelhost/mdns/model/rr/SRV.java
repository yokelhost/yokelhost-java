package io.yokelhost.mdns.model.rr;

import io.yokelhost.mdns.model.DNSName;

public interface SRV extends DNSRecord {
    int prio();
    int weight();
    int port();
    DNSName target();
}
