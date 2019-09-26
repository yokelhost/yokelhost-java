package io.yokelhost.mdns;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.yokelhost.mdns.codec.DNSResponderInitializer;

public class Responder {

    public static final InetSocketAddress MDNS_SOCK_ADDR;

    static {
        InetSocketAddress tmp = null;
        try {
            tmp = new InetSocketAddress(InetAddress.getByName("224.0.0.251"), 5353);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        MDNS_SOCK_ADDR = tmp;
    }


    public Responder(Registry registry) {
        this.registry = registry;
    }

    public Registry registry() {
        return registry;
    }

    public void start() throws InterruptedException, SocketException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();

        b.group(workerGroup);
        b.channelFactory((ChannelFactory<NioDatagramChannel>) () -> new NioDatagramChannel(InternetProtocolFamily.IPv4));
        b.handler(new DNSResponderInitializer(this.registry, MDNS_SOCK_ADDR.getPort()));

        b.option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.IP_MULTICAST_LOOP_DISABLED, false)
                .option(ChannelOption.SO_RCVBUF, 2048)
                .option(ChannelOption.IP_MULTICAST_TTL, 255);


        this.channel = (NioDatagramChannel) b.bind(MDNS_SOCK_ADDR.getPort()).sync().channel();
        NetworkInterface.networkInterfaces().forEach( ni->{
            this.channel.joinGroup(MDNS_SOCK_ADDR, ni);
        });
    }

    public void stop() throws InterruptedException {
        this.channel.close().sync();
    }

    public Channel channel() {
        return this.channel;
    }

    private final Registry registry;

    private NioDatagramChannel channel;
}
