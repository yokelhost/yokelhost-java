package io.yokelhost.mdns.model;

public interface Label {
    public static final Label NULL = new Label() {
        @Override
        public int length() {
            return 0;
        }

        @Override
        public byte[] data() {
            return new byte[0];
        }
    };

    int length();
    byte[] data();
}
