package io.yokelhost.mdns;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

public class DNSServerTest {

    @Test
    public void test() throws Throwable {
        DNSServer server = new DNSServer();

        InetSocketAddress bind = new InetSocketAddress(InetAddress.getByName( "224.0.0.251"), 5353);

        server.start(bind);

        new CountDownLatch(1).await();

    }
}
