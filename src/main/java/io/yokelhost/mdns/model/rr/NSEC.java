package io.yokelhost.mdns.model.rr;

import java.util.List;

import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;

public interface NSEC extends DNSRecord {
    DNSName next();
    List<DNSType> types();
}
