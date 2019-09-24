package io.yokelhost.mdns.model;

public enum QR {
    QUERY(0),
    RESPONSE(1),
    ;

    QR(int val) {
        this.val = val;
    }

    public int val() {
        return this.val;
    }

    private final int val;

    public static QR lookup(int val) {
        for (QR each : QR.values()) {
            if ( each.val == val ) {
                return each;
            }
        }
        return null;
    }
}
