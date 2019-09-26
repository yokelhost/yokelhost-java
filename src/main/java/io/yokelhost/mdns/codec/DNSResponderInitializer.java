package io.yokelhost.mdns.codec;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketEncoder;
import io.yokelhost.mdns.Registry;

public class DNSResponderInitializer extends ChannelInitializer<NioDatagramChannel> {

    public DNSResponderInitializer(Registry registry, int port) {
        this.registry = registry;
        this.port = port;
    }

    @Override
    protected void initChannel(NioDatagramChannel ch) throws Exception {
        ch.pipeline().addLast(new DatagramPacketEncoder<>(new DNSMessageEncoder()));
        ch.pipeline().addLast(new DNSMessageAddresser(this.port));
        ch.pipeline().addLast(new DNSMessageDecoder());
        ch.pipeline().addLast(new QueryHandler(this.registry));
    }

    private final Registry registry;

    private final int port;
}
