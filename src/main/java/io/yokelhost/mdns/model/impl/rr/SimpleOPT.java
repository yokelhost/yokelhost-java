package io.yokelhost.mdns.model.impl.rr;

import java.util.ArrayList;
import java.util.List;

import io.yokelhost.mdns.model.DNSClass;
import io.yokelhost.mdns.model.DNSName;
import io.yokelhost.mdns.model.DNSType;
import io.yokelhost.mdns.model.EDNSOption;
import io.yokelhost.mdns.model.rr.OPT;

public class SimpleOPT extends AbstractDNSRecord implements OPT {
    public SimpleOPT(List<EDNSOption> options) {
        super(DNSName.of(""), DNSType.OPT, 0, 0);
        this.options.addAll( options );
    }

    @Override
    public List<EDNSOption> options() {
        return null;
    }

    private List<EDNSOption> options = new ArrayList<>();
}
