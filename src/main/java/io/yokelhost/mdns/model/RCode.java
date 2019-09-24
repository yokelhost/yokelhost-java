package io.yokelhost.mdns.model;

public enum RCode {
    NO_ERROR(0),
    FORMAT_ERROR(1),
    SERVER_FAILURE(2),
    NAME_ERROR(3),
    NOT_IMPLEMENTED(4),
    REFUSED(5),
    ;

    RCode(int val) {
        this.val = val;
    }

    public int val() {
        return this.val;
    }

    public static RCode lookup(int val) {
        for (RCode each : RCode.values()) {
            if ( each.val == val ) {
                return each;
            }
        }
        return null;
    }

    private final int val;
}
