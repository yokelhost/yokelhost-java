package io.yokelhost.mdns.codec;

import java.net.InetSocketAddress;
import java.util.List;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.yokelhost.mdns.model.DNSMessage;

public class DNSMessageAddresser extends MessageToMessageEncoder<DNSMessage> {

    public DNSMessageAddresser(int port) {
        this.port = port;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, DNSMessage msg, List<Object> out) throws Exception {
        AddressedEnvelope<DNSMessage, InetSocketAddress> envelope = new DefaultAddressedEnvelope<>(msg, msg.recipient(), msg.sender());
        out.add( envelope );
    }

    private final int port;
}
