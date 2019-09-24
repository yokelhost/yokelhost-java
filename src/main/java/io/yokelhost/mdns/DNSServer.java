package io.yokelhost.mdns;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.yokelhost.mdns.codec.DNSResponderInitializer;

public class DNSServer {

    public DNSServer() {

    }

    public int start(InetSocketAddress bind) throws InterruptedException, SocketException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap(); // (1)

        NetworkInterface ni = NetworkInterface.getByName("en0");
        b.group(workerGroup); // (2)
        //b.channel(NioDatagramChannel.class); // (3)
        b.channelFactory(new ChannelFactory<NioDatagramChannel>() {
            @Override
            public NioDatagramChannel newChannel() {
                return new NioDatagramChannel(InternetProtocolFamily.IPv4);
            }
        });
        //b.option(ChannelOption.TCP_NODELAY, true); // (4)
        b.localAddress(InetAddress.getLoopbackAddress(), bind.getPort());
        b.handler(new DNSResponderInitializer());

        b.option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.IP_MULTICAST_LOOP_DISABLED, false)
                .option(ChannelOption.SO_RCVBUF, 2048)
                .option(ChannelOption.IP_MULTICAST_TTL, 255)
                .option(ChannelOption.IP_MULTICAST_IF, ni);


        System.err.println( "A: " + bind);
        this.channel = (NioDatagramChannel) b.bind(bind.getPort()).sync().channel();
        System.err.println( "B");
                this.channel.joinGroup(bind, ni);
        System.err.println( "C");
        return ((InetSocketAddress) this.channel.localAddress()).getPort();
    }

    public void stop() throws InterruptedException {
        this.channel.close().sync();
    }

    public Channel channel() {
        return this.channel;
    }


    private NioDatagramChannel channel;
}
