package io.yokelhost.mdns;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.impl.rr.SimpleA;
import io.yokelhost.mdns.model.impl.rr.SimplePTR;
import io.yokelhost.mdns.model.rr.A;
import io.yokelhost.mdns.model.rr.AAAA;
import org.junit.Test;

public class ResponderTest {

    @Test
    public void test() throws Throwable {
        Registry registry = new Registry();

        NetworkInterface.networkInterfaces().forEach( e->{
            try {
                if ( e.isLoopback() ) {
                    return;
                }
                Enumeration<InetAddress> addrs = e.getInetAddresses();
                while ( addrs.hasMoreElements() ) {
                    InetAddress addr = addrs.nextElement();
                    if ( addr instanceof Inet4Address ) {
                        registry.add(A.create("tacos.local", 120, (Inet4Address) addr));
                    } else {
                        registry.add(AAAA.create("tacos.local", 120, (Inet6Address) addr));
                    }
                }
            } catch (SocketException ex) {
                ex.printStackTrace();
            }
        });

        registry.add( new SimplePTR(DNSName.of("tacos.local"), 120, DNSName.of("salsa.local")));

        Responder server = new Responder(registry);

        server.start();

        new CountDownLatch(1).await();

    }
}
