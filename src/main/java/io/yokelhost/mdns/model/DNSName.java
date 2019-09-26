package io.yokelhost.mdns.model;

import java.util.List;

import io.yokelhost.mdns.model.impl.SimpleName;

public interface DNSName {
    static DNSName of(String name) {
        return new SimpleName(name);
    }

    List<Label> labels();
}
