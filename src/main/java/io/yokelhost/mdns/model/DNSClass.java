package io.yokelhost.mdns.model;

public enum DNSClass {
    RESERVED(0),
    IN(1),
    CS(2),
    CH(3),
    HS(4),
    /* Unassigned(5-253) */
    NONE(254),
    ANY(255),
    ;

    DNSClass(int val) {
        this.val = val;
    }

    private final int val;

    public static DNSClass lookup(int val) {
        for (DNSClass each : DNSClass.values()) {
            if ( each.val == ( val & 0x7FFF) ) {
                return each;
            }
        }
        return null;
    }
}
