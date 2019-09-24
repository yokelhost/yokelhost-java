package io.yokelhost.mdns.model.rr;

import io.yokelhost.mdns.model.DNSName;

public interface NS extends DNSRecord {
    DNSName nsName();
}
