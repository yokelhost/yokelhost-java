package io.yokelhost.mdns;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.yokelhost.mdns.model.DNSMessage;
import io.yokelhost.mdns.model.DNSOpcode;
import io.yokelhost.mdns.model.DNSQuestion;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.QR;
import io.yokelhost.mdns.model.impl.SimpleDNSMessage;
import io.yokelhost.mdns.model.impl.rr.SimpleNSEC;
import io.yokelhost.mdns.model.rr.DNSRecord;
import io.yokelhost.mdns.model.rr.NSEC;

public class Registry {

    public Registry() {

    }

    public void add(DNSRecord record) {
        this.records.add(record);
    }

    public DNSMessage respond(DNSMessage query) {
        List<DNSRecord> answers = new ArrayList<>();
        List<DNSRecord> additional = new ArrayList<>();
        boolean unicast = false;
        for (DNSQuestion each : query.questions()) {
            unicast = each.unicast() | unicast;
            this.records.stream()
                    .filter(e -> {
                        if (((each.cls() & 0x7FFF) == e.cls()) &&
                                (each.type() == DNSType._STAR_ || e.type() == each.type()) &&
                                (each.name().equals(e.name()))) {
                            return true;
                        }
                        return false;
                    }).forEach(e -> {
                answers.add(e);
                additional.add(nsecFor(e));
            });
        }
        return new SimpleDNSMessage(query.recipient(),
                                    recipient(query.sender(), unicast),
                                    query.id(),
                                    QR.RESPONSE,
                                    DNSOpcode.QUERY,
                                    query.questions(),
                                    answers,
                                    Collections.emptyList(),
                                    additional);
    }

    InetSocketAddress recipient(InetSocketAddress sender, boolean unicast) {
        if ( unicast ) {
            return sender;
        }
        if ( sender.getPort() != 5353 ) {
            return sender;
        }
        return Responder.MDNS_SOCK_ADDR;
    }

    private NSEC nsecFor(DNSRecord record) {
        return new SimpleNSEC(record.name(), record.ttl(), record.name(), Collections.singletonList(record.type()));
    }

    private List<DNSRecord> records = new ArrayList<>();
}
