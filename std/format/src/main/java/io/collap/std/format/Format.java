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

    /**
     * Capitalizes the first character of the string.
     *
     * This should only be used for single words; the
     * method might be extended to support full sentences
     * and in this case the API stays, but the functionality
     * changes.
     */
    public String capitalize (String in) {
        return Character.toUpperCase (in.charAt (0)) + in.substring (1);
    }

}
