package io.yokelhost.mdns.model.rr;

import java.net.Inet4Address;

public interface A extends DNSRecord {
    Inet4Address address();
}
