package io.yokelhost.mdns.codec;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.yokelhost.mdns.model.DNSMessage;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSQuestion;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.Label;
import io.yokelhost.mdns.model.rr.A;
import io.yokelhost.mdns.model.rr.AAAA;
import io.yokelhost.mdns.model.rr.CNAME;
import io.yokelhost.mdns.model.rr.DNSRecord;
import io.yokelhost.mdns.model.rr.NSEC;
import io.yokelhost.mdns.model.rr.OPT;
import io.yokelhost.mdns.model.rr.PTR;
import io.yokelhost.mdns.model.rr.SRV;
import io.yokelhost.mdns.model.rr.TXT;

public class DNSMessageEncoder extends MessageToMessageEncoder<DNSMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, DNSMessage msg, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();

        buf.writeShort(msg.id());

        int tmp = 0;
        tmp |= msg.qr().val() << 15;
        tmp |= msg.opcode().val() << 11;
        tmp |= 1 << 10;

        buf.writeShort(tmp);

        buf.writeShort(msg.questions().size());
        buf.writeShort(msg.answers().size());
        buf.writeShort(msg.nameservers().size());
        buf.writeShort(msg.additional().size());

        LabelIndex cache = new LabelIndex();
        encodeQuestions(msg.questions(), cache, buf);
        encodeRecords(msg.answers(), cache, buf);
        encodeRecords(msg.nameservers(), cache, buf);
        encodeRecords(msg.additional(), cache, buf);

        out.add(buf);
    }

    private void encodeQuestions(List<DNSQuestion> questions, LabelIndex cache, ByteBuf buf) {
        for (DNSQuestion question : questions) {
            encodeQuestion(question, cache, buf);
        }
    }

    private void encodeQuestion(DNSQuestion question, LabelIndex cache, ByteBuf buf) {
        encodeName(question.name(), cache, buf);
        buf.writeShort(question.type().val());
        buf.writeShort(question.cls());
    }

    private void encodeRecords(List<DNSRecord> records, LabelIndex cache, ByteBuf buf) {
        for (DNSRecord record : records) {
            encodeRecord(record, cache, buf);
        }
    }

    private void encodeRecord(DNSRecord record, LabelIndex cache, ByteBuf buf) {
        encodeName(record.name(), cache, buf);
        buf.writeShort(record.type().val());
        //buf.writeShort((record.cls() | 0x8000));
        buf.writeShort(record.cls());

        buf.writeInt((int) record.ttl());

        ByteBuf rdata = buf.alloc().buffer();
        encodeRdata(record, cache, rdata);

        buf.writeShort(rdata.readableBytes());
        buf.writeBytes(rdata);
        rdata.release();
    }

    private void encodeRdata(DNSRecord record, LabelIndex cache, ByteBuf buf) {
        DNSType type = record.type();

        switch (type) {
            case A:
                encodeA((A) record, cache, buf);
                return;
            case AAAA:
                encodeAAAA((AAAA) record, cache, buf);
                return;
            case CNAME:
                encodeCNAME((CNAME) record, cache, buf);
                return;
            case PTR:
                encodePTR((PTR) record, cache, buf);
                return;
            case TXT:
                encodeTXT((TXT) record, cache, buf);
                return;
            case SRV:
                encodeSRV((SRV) record, cache, buf);
                return;
            case NSEC:
                encodeNSEC((NSEC) record, cache, buf);
                return;
            case OPT:
                encodeOPT((OPT) record, cache, buf);
                return;
            default:
                System.err.println("Unhandled record type: " + type);
                //return Unpooled.EMPTY_BUFFER;
        }
    }

    private void encodeA(A record, LabelIndex cache, ByteBuf buf) {
        Inet4Address addr = record.address();
        buf.writeBytes(addr.getAddress());
    }

    private void encodeAAAA(AAAA record, LabelIndex cache, ByteBuf buf) {
        Inet6Address addr = record.address();
        buf.writeBytes(addr.getAddress());
    }

    private void encodeCNAME(CNAME record, LabelIndex cache, ByteBuf buf) {
        encodeName(record.cname(), cache, buf);
    }

    private void encodePTR(PTR record, LabelIndex cache, ByteBuf buf) {
        encodeName(record.ptrName(), cache, buf);
    }

    private void encodeTXT(TXT record, LabelIndex cache, ByteBuf buf) {
        buf.writeBytes(record.txt());
    }

    private void encodeSRV(SRV record, LabelIndex cache, ByteBuf buf) {
        buf.writeShort(record.prio());
        buf.writeShort(record.weight());
        buf.writeShort(record.port());
        encodeName(record.target(), cache, buf);
    }

    private void encodeOPT(OPT record, LabelIndex cache, ByteBuf buf) {
        // nothing for now.
    }

    private void encodeNSEC(NSEC record, LabelIndex cache, ByteBuf buf) {
        encodeName(record.next(), cache, buf);

        List<DNSType> types = record.types();
        List<Integer> bits = types.stream().map(e -> e.val()).collect(Collectors.toList());
        bits.sort(Integer::compare);

        Map<Integer,NSECBitMapField> fields = new HashMap<>();

        for (Integer bit : bits) {
            int window = (bit / 256);
            NSECBitMapField field = fields.get(window);
            if ( field == null ) {
                field = new NSECBitMapField();
                fields.put(window, field);
                field.window = window;
            }
            int position = bit % 256;
            field.map |= (1 << position);
        }

        for (NSECBitMapField field : fields.values()) {
            buf.writeByte(field.window);
            int mapLen = (32 - Integer.numberOfLeadingZeros(field.map)) / 8 ;
            if ( ( Integer.numberOfLeadingZeros(field.map) % 8 ) != 0 ) {
                ++mapLen;
            }
            buf.writeByte(mapLen);
            for ( int i = 0 ; i < mapLen ; ++i ) {
                byte b = (byte) (field.map >>> (i*8));
                byte y=0;
                for(int position=7; position>0; position--){
                    y+=((b&1)<<position);
                    b >>= 1;
                }
                buf.writeByte(y);
            }
        }
    }

    static class NSECBitMapField {
        int window;
        int map = 0;
    }


    private void encodeName(DNSName name, LabelIndex cache, ByteBuf buf) {
        // No compression yet implemented
        for (Label label : name.labels()) {
            byte[] data = label.data();
            if ( data.length > 0 ) {
                buf.writeByte(data.length);
                buf.writeBytes(data);
            }
        }
        buf.writeByte(0);
    }

    private static class LabelIndex {

        // no compression implemented yet.
    }
}
