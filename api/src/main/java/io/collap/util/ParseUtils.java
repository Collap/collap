package io.collap.util;

public class ParseUtils {

    /**
     * @return null when the supplied String did not lead to a proper Long.
     */
    public static Long parseLong (String value) {
        if (value == null) return null;

        Long number = null;
        try {
            number = Long.parseLong (value);
        } catch (NumberFormatException e) {
            /* Expected. */
        }
        return number;
    }

}
