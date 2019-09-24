package io.yokelhost.mdns.model;

import java.util.List;

public interface DNSName {
    List<Label> labels();
}
