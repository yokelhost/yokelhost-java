package io.yokelhost.mdns.model;

import java.net.InetSocketAddress;
import java.util.List;

import io.yokelhost.mdns.model.rr.DNSRecord;

public interface DNSMessage {
    InetSocketAddress sender();
    InetSocketAddress recipient();
    int id();
    QR qr();
    DNSOpcode opcode();
    List<DNSQuestion> questions();
    List<DNSRecord> answers();
    List<DNSRecord> nameservers();
    List<DNSRecord> additional();
}
