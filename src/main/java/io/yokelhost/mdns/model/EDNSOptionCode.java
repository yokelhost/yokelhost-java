package io.yokelhost.mdns.model;

public enum EDNSOptionCode {
    /* Reserved(0) */
    LLQ(1),
    UL(2),
    NSID(3),
    /* Reserved(4) */
    DAU(5),
    DHU(6),
    N3U(7),
    EDNS_CLIENT_SUBNET(8),
    EDNS_EXPIRE(9),
    COOKIE(10),
    EDNS_TCP_KEEPALIVE(11),
    PADDING(12),
    CHAIN(13),
    EDNS_KEY_TAG(14),
    /* Unassigned(15) */
    EDNS_CLIENT_TAG(16),
    EDNS_SERVER_TAG(17),
    /* Unassigned(18-26945) */
    DEVICE_ID(26946),
    ;

    EDNSOptionCode(int val) {
        this.val = val;
    }

    public int val() {
        return this.val;
    }

    public static EDNSOptionCode lookup(int val) {
        for (EDNSOptionCode each : EDNSOptionCode.values()) {
            if ( each.val == val ) {
                return each;
            }
        }
        return null;
    }


    private final int val;
}
