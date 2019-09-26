package io.yokelhost.mdns.model.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.yokelhost.mdns.model.DNSMessage;
import io.yokelhost.mdns.model.DNSOpcode;
import io.yokelhost.mdns.model.DNSQuestion;
import io.yokelhost.mdns.model.QR;
import io.yokelhost.mdns.model.rr.DNSRecord;

public class SimpleDNSMessage implements DNSMessage {
    public SimpleDNSMessage(InetSocketAddress sender,
                            InetSocketAddress recipient,
                            int id, QR qr, DNSOpcode opcode,
                            List<DNSQuestion> questions,
                            List<DNSRecord> answers,
                            List<DNSRecord> nameservers,
                            List<DNSRecord> additional) {
        this.sender = sender;
        this.recipient = recipient;
        this.id = id;
        this.qr = qr;
        this.opcode = opcode;
        this.questions.addAll(questions);
        this.answers.addAll(answers);
        this.nameservers.addAll(nameservers);
        this.additional.addAll(additional);
    }

    public InetSocketAddress sender() {
        return this.sender;
    }

    public InetSocketAddress recipient() {
        return this.recipient;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public QR qr() {
        return this.qr;
    }

    @Override
    public DNSOpcode opcode() {
        return this.opcode;
    }

    @Override
    public List<DNSQuestion> questions() {
        return Collections.unmodifiableList(this.questions);
    }

    @Override
    public List<DNSRecord> answers() {
        return Collections.unmodifiableList(this.answers);
    }

    @Override
    public List<DNSRecord> nameservers() {
        return Collections.unmodifiableList(this.nameservers);
    }

    @Override
    public List<DNSRecord> additional() {
        return Collections.unmodifiableList(this.additional);
    }

    @Override
    public String toString() {
        return "[DNSMessage: " + sender + ">" + recipient + "; q="+ questions + "; a=" + answers + "]";
    }

    private final InetSocketAddress sender;
    private final InetSocketAddress recipient;

    private final int id;

    private final QR qr;

    private final DNSOpcode opcode;

    private final List<DNSQuestion> questions = new ArrayList<>();

    private final List<DNSRecord> answers = new ArrayList<>();
    private final List<DNSRecord> nameservers = new ArrayList<>();
    private final List<DNSRecord> additional = new ArrayList<>();
}
