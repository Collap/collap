package io.collap.std.format;

public class Format {

    public String groupDigits (int value) {
        return String.format ("%,d", value);
    }

    public String groupDigits (long value) {
        return String.format ("%,d", value);
    }

    public String groupDigits (float value) {
        return String.format ("%,f", value);
    }

    public String groupDigits (double value) {
        return String.format ("%,f", value);
    }

}
