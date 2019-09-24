package io.yokelhost.mdns.model.impl;

import io.yokelhost.mdns.model.DNSClass;
import io.yokelhost.mdns.model.DNSQuestion;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;

public class SimpleQuestion implements DNSQuestion {
    public SimpleQuestion(DNSName name, DNSType type, DNSClass cls) {
        this.name = name;
        this.type = type;
        this.cls = cls;
    }
    @Override
    public DNSName name() {
        return this.name;
    }

    @Override
    public DNSType type() {
        return this.type;
    }

    @Override
    public DNSClass cls() {
        return this.cls;
    }

    @Override
    public String toString() {
        return "[Question: " + name + " " + cls + " " + type + "]";
    }

    private final DNSName name;

    private final DNSType type;

    private final DNSClass cls;
}
