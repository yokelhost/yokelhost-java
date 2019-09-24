package io.yokelhost.mdns.model;

public enum DNSOpcode {

    QUERY(0),
    IQUERY(1),
    STATUS(2),
    /* Unassigned(3) */
    NOTIFY(4),
    UPDATE(5),
    DSO(6),
    /* Unassigned(7-15) */
    ;

    DNSOpcode(int val) {
        this.val = val;
    }

    public int val() {
        return this.val;
    }

    private final int val;

    public static DNSOpcode lookup(int val) {
        for (DNSOpcode each : DNSOpcode.values()) {
            if ( each.val == val ) {
                return each;
            }
        }
        return null;
    }

}
