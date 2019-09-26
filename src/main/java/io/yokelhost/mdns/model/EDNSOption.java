package io.yokelhost.mdns.model;

public interface EDNSOption {
    EDNSOptionCode code();
    byte[] data();
}
