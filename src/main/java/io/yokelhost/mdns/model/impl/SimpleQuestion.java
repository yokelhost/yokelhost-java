package io.yokelhost.mdns.model.impl;

import io.yokelhost.mdns.model.DNSQuestion;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;

public class SimpleQuestion implements DNSQuestion {
    public SimpleQuestion(DNSName name, DNSType type, int cls, boolean unicast) {
        this.name = name;
        this.type = type;
        this.cls = cls;
        this.unicast = unicast;
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
    public int cls() {
        return this.cls;
    }

    @Override
    public boolean unicast() {
        return this.unicast;
    }

    @Override
    public String toString() {
        return "[Question: " + name + " " + cls + " " + type + " " + (unicast ? "QU" : "QM") + "@" + System.identityHashCode(this) + "]";
    }

    private final DNSName name;

    private final DNSType type;

    private final int cls;

    private final boolean unicast;
}
