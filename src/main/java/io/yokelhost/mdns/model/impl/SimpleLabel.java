package io.yokelhost.mdns.model.impl;

import io.yokelhost.mdns.model.Label;

public class SimpleLabel implements Label {

    public SimpleLabel(byte[] bytes) {
        this.bytes = bytes;
    }

    public void next(SimpleLabel next) {
        this.next = next;
    }

    public SimpleLabel next() {
        return this.next;
    }

    @Override
    public int length() {
        return bytes.length;
    }

    @Override
    public byte[] data() {
        return this.bytes;
    }

    @Override
    public String toString() {
        return new String(this.bytes);
    }

    private final byte[] bytes;
    private SimpleLabel next;
}
