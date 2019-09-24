package io.yokelhost.mdns.model.rr;

import java.net.Inet6Address;

public interface AAAA extends DNSRecord {
    Inet6Address address();
}
