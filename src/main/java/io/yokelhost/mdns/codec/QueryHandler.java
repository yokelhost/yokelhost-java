package io.yokelhost.mdns.codec;

import java.util.Collections;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.yokelhost.mdns.Registry;
import io.yokelhost.mdns.model.DNSMessage;
import io.yokelhost.mdns.model.DNSOpcode;
import io.yokelhost.mdns.model.QR;
import io.yokelhost.mdns.model.impl.SimpleDNSMessage;
import io.yokelhost.mdns.model.impl.rr.SimpleNSEC;
import io.yokelhost.mdns.model.impl.rr.SimpleOPT;
import io.yokelhost.mdns.model.rr.DNSRecord;

public class QueryHandler extends SimpleChannelInboundHandler<DNSMessage> {

    public QueryHandler(Registry registry) {
        this.registry = registry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DNSMessage msg) throws Exception {
        if (msg.qr() == QR.QUERY) {
            DNSMessage response = this.registry.respond(msg);
            if ( response != null && ! response.answers().isEmpty()) {
                System.err.println( msg + " >> " + response);
                ctx.pipeline().writeAndFlush(response);
            }
        }

        ctx.fireChannelRead(msg);
    }

    private final Registry registry;
}
