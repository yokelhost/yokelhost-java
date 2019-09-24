package io.yokelhost.mdns.model;

import java.util.List;

import io.yokelhost.mdns.model.rr.DNSRecord;

public interface DNSMessage {
    int id();
    QR qr();
    DNSOpcode opcode();
    List<DNSQuestion> questions();
    List<DNSRecord> answers();
    List<DNSRecord> nameservers();
    List<DNSRecord> additional();
}
