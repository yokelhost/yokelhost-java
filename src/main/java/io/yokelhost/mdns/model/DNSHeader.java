package io.yokelhost.mdns.model;

public interface DNSHeader {

    byte[] id();

    QR qr();

    DNSOpcode opcode();

    boolean aa();

    boolean tc();

    boolean rd();

    int qdcount();

    int ancount();

    int arcount();
}
