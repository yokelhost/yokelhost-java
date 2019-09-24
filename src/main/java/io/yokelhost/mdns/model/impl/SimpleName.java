package io.yokelhost.mdns.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.yokelhost.mdns.model.Label;
import io.yokelhost.mdns.model.DNSName;

public class SimpleName implements DNSName {

    public SimpleName() {
    }

    public void addLabel(SimpleLabel label) {
        SimpleLabel current = label;
        while ( current != null ) {
            this.labels.add( current );
            current = current.next();
        }
    }

    public void addPointer(DNSName pointer) {
        this.labels.addAll( pointer.labels() );
    }

    @Override
    public List<Label> labels() {
        return Collections.unmodifiableList(this.labels);
    }

    @Override
    public String toString() {
        return this.labels
                .stream()
                .map(e -> e.toString() + ".")
                .collect(Collectors.joining());
    }

    private final List<Label> labels = new ArrayList<>();
}
