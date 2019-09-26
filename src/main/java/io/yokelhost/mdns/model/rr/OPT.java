package io.yokelhost.mdns.model.rr;

import java.util.List;

import io.yokelhost.mdns.model.EDNSOption;

public interface OPT extends DNSRecord {
    List<EDNSOption> options();
}
