package io.yokelhost.mdns.codec;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.yokelhost.mdns.model.DNSQuestion;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSOpcode;
import io.yokelhost.mdns.model.EDNSOption;
import io.yokelhost.mdns.model.EDNSOptionCode;
import io.yokelhost.mdns.model.QR;
import io.yokelhost.mdns.model.RCode;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.impl.SimpleDNSMessage;
import io.yokelhost.mdns.model.impl.SimpleLabel;
import io.yokelhost.mdns.model.impl.SimpleName;
import io.yokelhost.mdns.model.impl.SimpleQuestion;
import io.yokelhost.mdns.model.impl.rr.SimpleA;
import io.yokelhost.mdns.model.impl.rr.SimpleAAAA;
import io.yokelhost.mdns.model.impl.rr.SimpleCNAME;
import io.yokelhost.mdns.model.impl.rr.SimpleNSEC;
import io.yokelhost.mdns.model.impl.rr.SimpleOPT;
import io.yokelhost.mdns.model.impl.rr.SimplePTR;
import io.yokelhost.mdns.model.impl.rr.SimpleSRV;
import io.yokelhost.mdns.model.impl.rr.SimpleTXT;
import io.yokelhost.mdns.model.rr.DNSRecord;

public class DNSMessageDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws Exception {
        ByteBuf buf = packet.content();
        InetSocketAddress sender = packet.sender();
        InetSocketAddress recipient = packet.recipient();

        int id = buf.readUnsignedShort();
        int tmp = buf.readUnsignedShort();
        QR qr = QR.lookup(tmp >>> 15 & 0x1);
        DNSOpcode opcode = DNSOpcode.lookup(tmp >> 14 & 0xF);
        boolean aa = (tmp >> 10 & 0x1) == 1;
        boolean tc = (tmp >> 9 & 0x1) == 1;
        boolean rd = (tmp >> 8 & 0x1) == 1;
        int z = tmp >> 4 & 0x7;
        RCode rcode = RCode.lookup(tmp & 0xF);
        int qdcount = buf.readUnsignedShort();
        int ancount = buf.readUnsignedShort();
        int nscount = buf.readUnsignedShort();
        int arcount = buf.readUnsignedShort();

        LabelIndex cache = new LabelIndex();

        List<DNSQuestion> questions = decodeQuestions(qdcount, cache, buf);
        List<DNSRecord> answers = decodeRecords(ancount, cache, buf);
        List<DNSRecord> nameservers = decodeRecords(nscount, cache, buf);
        List<DNSRecord> additional = decodeRecords(arcount, cache, buf);

        SimpleDNSMessage msg = new SimpleDNSMessage(sender, recipient,
                                                    id, qr, opcode,
                                                    questions,
                                                    answers,
                                                    nameservers,
                                                    additional);

        out.add(msg);
    }

    private List<DNSQuestion> decodeQuestions(int qdcount, LabelIndex cache, ByteBuf buf) {
        List<DNSQuestion> questions = new ArrayList<>();
        for (int i = 0; i < qdcount; ++i) {
            DNSName name = decodeName(cache, buf);
            DNSType type = DNSType.lookup(buf.readUnsignedShort());
            int cls = buf.readUnsignedShort();
            boolean unicast = (cls & 0x8000) != 0;
            SimpleQuestion question = new SimpleQuestion(name, type, cls, unicast);
            questions.add(question);
        }
        return questions;
    }

    private List<DNSRecord> decodeRecords(int ancount, LabelIndex cache, ByteBuf buf) {
        List<DNSRecord> answers = new ArrayList<>();
        for (int i = 0; i < ancount; ++i) {
            DNSName name = decodeName(cache, buf);
            DNSType type = DNSType.lookup(buf.readUnsignedShort());
            int cls = buf.readUnsignedShort();
            long ttl = buf.readUnsignedInt();
            DNSRecord answer = decodeRecord(name, type, cls, ttl, cache, buf);
            if (answer != null) {
                answers.add(answer);
            }
        }
        return answers;
    }

    private DNSRecord decodeRecord(DNSName name, DNSType type, int cls, long ttl, LabelIndex cache, ByteBuf buf) {
        int rdlength = buf.readUnsignedShort();
        ByteBuf rdata = buf.readBytes(rdlength);
        switch (type) {
            case A:
                return decodeA(name, ttl, cache, rdata);
            case AAAA:
                return decodeAAAA(name, ttl, cache, rdata);
            case CNAME:
                return decodeCNAME(name, ttl, cache, rdata);
            case PTR:
                return decodePTR(name, ttl, cache, rdata);
            case TXT:
                return decodeTXT(name, ttl, cache, rdata);
            case SRV:
                return decodeSRV(name, ttl, cache, rdata);
            case NSEC:
                return decodeNSEC(name, ttl, cache, rdata);
            case OPT:
                return decodeOPT(name, cls, ttl, cache, rdata);
            default:
                System.err.println("Unhandled record type: " + type);
        }
        return null;
    }

    private DNSRecord decodeA(DNSName name, long ttl, LabelIndex cache, ByteBuf buf) {
        byte[] addressBytes = new byte[4];
        buf.readBytes(addressBytes);
        try {
            Inet4Address address = (Inet4Address) InetAddress.getByAddress(addressBytes);
            return new SimpleA(name, ttl, address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DNSRecord decodeAAAA(DNSName name, long ttl, LabelIndex cache, ByteBuf buf) {
        byte[] addressBytes = new byte[16];
        buf.readBytes(addressBytes);
        try {
            Inet6Address address = (Inet6Address) InetAddress.getByAddress(addressBytes);
            return new SimpleAAAA(name, ttl, address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DNSRecord decodeCNAME(DNSName name, long ttl, LabelIndex cache, ByteBuf rdata) {
        DNSName cname = decodeName(cache, rdata);
        return new SimpleCNAME(name, ttl, cname);
    }

    private DNSRecord decodePTR(DNSName name, long ttl, LabelIndex cache, ByteBuf rdata) {
        DNSName ptrName = decodeName(cache, rdata);
        return new SimplePTR(name, ttl, ptrName);
    }

    private DNSRecord decodeTXT(DNSName name, long ttl, LabelIndex cache, ByteBuf rdata) {
        byte[] txt = new byte[rdata.readableBytes()];
        rdata.readBytes(txt);
        return new SimpleTXT(name, ttl, txt);
    }

    private DNSRecord decodeSRV(DNSName name, long ttl, LabelIndex cache, ByteBuf rdata) {
        int prio = rdata.readUnsignedShort();
        int weight = rdata.readUnsignedShort();
        int port = rdata.readUnsignedShort();
        DNSName target = decodeName(cache, rdata);

        return new SimpleSRV(name, ttl, prio, weight, port, target);
    }

    private DNSRecord decodeOPT(DNSName name, int payloadSize, long rcodeAndFlags, LabelIndex cache, ByteBuf rdata) {
        List<EDNSOption> options = new ArrayList<>();
        while ( rdata.readableBytes() > 0 ) {
            int code = rdata.readUnsignedShort();
            EDNSOptionCode ednsOptionCode = EDNSOptionCode.lookup(code);
            int len = rdata.readUnsignedShort();
            ByteBuf optionData = rdata.readBytes(len);
        }
        return new SimpleOPT(options);
    }

    private DNSRecord decodeNSEC(DNSName name, long ttl, LabelIndex cache, ByteBuf rdata) {
        DNSName next = decodeName(cache, rdata);
        return new SimpleNSEC(name, ttl, next, Collections.emptyList());
    }

    private DNSName decodeName(LabelIndex cache, ByteBuf buf) {
        SimpleName name = new SimpleName();
        SimpleLabel current = null;
        while (true) {
            int index = buf.readerIndex();
            short lengthOctet = buf.readUnsignedByte();
            if (lengthOctet == 0) {
                return name;
            }
            if (lengthOctet <= 63) {
                byte[] bytes = new byte[lengthOctet];
                buf.readBytes(bytes);
                SimpleLabel label = new SimpleLabel(bytes);
                if (current != null) {
                    current.next(label);
                }
                current = label;
                cache.put(index, current);
                name.addLabel(current);
            } else if ((lengthOctet >>> 6 & 0x3) == 0x3) {
                int offset = (lengthOctet & 0x3F) << 8;
                offset += buf.readUnsignedByte();
                SimpleLabel label = cache.get(offset);
                if (current != null) {
                    current.next(label);
                }
                name.addLabel(label);
                return name;
            } else {
                System.err.println("Unhandled: " + lengthOctet);
            }
        }
    }

    // just for less typing
    private static class LabelIndex extends HashMap<Integer, SimpleLabel> {

    }
}
