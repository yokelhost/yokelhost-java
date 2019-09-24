package io.yokelhost.mdns.codec;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.yokelhost.mdns.LabelIndex;
import io.yokelhost.mdns.model.DNSClass;
import io.yokelhost.mdns.model.DNSQuestion;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSOpcode;
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
import io.yokelhost.mdns.model.impl.rr.SimplePTR;
import io.yokelhost.mdns.model.impl.rr.SimpleSRV;
import io.yokelhost.mdns.model.impl.rr.SimpleTXT;
import io.yokelhost.mdns.model.rr.DNSRecord;

public class DNSMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
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

        out.add(new SimpleDNSMessage(id, qr, opcode,
                                     questions,
                                     answers,
                                     nameservers,
                                     additional));
    }

    private List<DNSQuestion> decodeQuestions(int qdcount, LabelIndex cache, ByteBuf buf) {
        List<DNSQuestion> questions = new ArrayList<>();
        for (int i = 0; i < qdcount; ++i) {
            DNSName name = decodeName(cache, buf);
            DNSType type = DNSType.lookup(buf.readUnsignedShort());
            int clsBit = buf.readUnsignedShort();
            DNSClass cls = DNSClass.lookup(clsBit);
            SimpleQuestion question = new SimpleQuestion(name, type, cls);
            System.err.println("Q: " + question);
            questions.add(question);
        }
        return questions;
    }

    private List<DNSRecord> decodeRecords(int ancount, LabelIndex cache, ByteBuf buf) {
        List<DNSRecord> answers = new ArrayList<>();
        for (int i = 0; i < ancount; ++i) {
            DNSName name = decodeName(cache, buf);
            DNSType type = DNSType.lookup(buf.readUnsignedShort());
            int clsBit = buf.readUnsignedShort();
            DNSClass cls = DNSClass.lookup(clsBit);
            long ttl = buf.readUnsignedInt();
            DNSRecord answer = decodeRecord(name, type, cls, ttl, cache, buf);
            if (answer != null) {
                answers.add(answer);
                System.err.println("R: " + answer);
            }
        }
        return answers;
    }

    private DNSRecord decodeRecord(DNSName name, DNSType type, DNSClass cls, long ttl, LabelIndex cache, ByteBuf buf) {
        int rdlength = buf.readUnsignedShort();
        ByteBuf rdata = buf.readBytes(rdlength);
        switch (type) {
            case A:
                return decodeA(name, type, cls, ttl, cache, rdata);
            case AAAA:
                return decodeAAAA(name, type, cls, ttl, cache, rdata);
            case CNAME:
                return decodeCNAME(name, type, cls, ttl, cache, rdata);
            case PTR:
                return decodePTR(name, type, cls, ttl, cache, rdata);
            case TXT:
                return decodeTXT(name, type, cls, ttl, cache, rdata);
            case SRV:
                return decodeSRV(name, type, cls, ttl, cache, rdata);
            default:
                System.err.println("Unhandled record type: " + type);
        }
        return null;
    }

    private DNSRecord decodeA(DNSName name, DNSType type, DNSClass cls, long ttl, LabelIndex cache, ByteBuf buf) {
        byte[] addressBytes = new byte[4];
        buf.readBytes(addressBytes);
        try {
            Inet4Address address = (Inet4Address) InetAddress.getByAddress(addressBytes);
            return new SimpleA(name, type, cls, ttl, address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DNSRecord decodeAAAA(DNSName name, DNSType type, DNSClass cls, long ttl, LabelIndex cache, ByteBuf buf) {
        byte[] addressBytes = new byte[16];
        buf.readBytes(addressBytes);
        try {
            Inet6Address address = (Inet6Address) InetAddress.getByAddress(addressBytes);
            return new SimpleAAAA(name, type, cls, ttl, address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DNSRecord decodeCNAME(DNSName name, DNSType type, DNSClass cls, long ttl, LabelIndex cache, ByteBuf rdata) {
        DNSName cname = decodeName(cache, rdata);
        return new SimpleCNAME(name, type, cls, ttl, cname);
    }

    private DNSRecord decodePTR(DNSName name, DNSType type, DNSClass cls, long ttl, LabelIndex cache, ByteBuf rdata) {
        DNSName ptrName = decodeName(cache, rdata);
        return new SimplePTR(name, type, cls, ttl, ptrName);
    }

    private DNSRecord decodeTXT(DNSName name, DNSType type, DNSClass cls, long ttl, LabelIndex cache, ByteBuf rdata) {
        byte[] txt = new byte[rdata.readableBytes()];
        rdata.readBytes(txt);
        return new SimpleTXT(name, type, cls, ttl, txt);
    }

    private DNSRecord decodeSRV(DNSName name, DNSType type, DNSClass cls, long ttl, LabelIndex cache, ByteBuf rdata) {
        int prio = rdata.readUnsignedShort();
        int weight = rdata.readUnsignedShort();
        int port = rdata.readUnsignedShort();
        DNSName target = decodeName(cache, rdata);

        return new SimpleSRV(name, type, cls, ttl, prio, weight, port, target);

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
                System.err.println("UNHANDLED: " + lengthOctet);
            }
        }
    }

}
