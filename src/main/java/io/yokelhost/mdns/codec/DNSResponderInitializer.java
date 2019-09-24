package io.yokelhost.mdns.codec;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.dns.DatagramDnsQueryDecoder;

public class DNSResponderInitializer extends ChannelInitializer<NioDatagramChannel> {

    @Override
    protected void initChannel(NioDatagramChannel ch) throws Exception {
        //ch.pipeline().addLast(new DebugHandler("head"));
        //ch.pipeline().addLast(new DatagramDnsQueryDecoder());
        //ch.pipeline().addLast(new DatagramDnsQueryDecoder());
        ch.pipeline().addLast(new DatagramPacketDecoder(new DNSMessageDecoder()));
    }

}
