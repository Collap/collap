package io.collap.std.user.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public class ValidationResult {
        public boolean passed;
        public String error;
    }

    private Pattern userNamePattern = Pattern.compile ("\\A(\\w)+\\z"); // TODO: Make the pattern configurable

    public ValidationResult validateUserName (String name) {
        ValidationResult result = new ValidationResult ();
        result.passed = true;

        final int minimumNameLength = 1;
        if (name.length () < minimumNameLength) { // TODO: Minimum threshold in config
            result.passed = false;
            result.error = "The name must be at least " + minimumNameLength + " characters long!";
            return result;
        }

        Matcher matcher = userNamePattern.matcher (name);
        if (!matcher.find ()) {
            result.passed = false;
            result.error = "The name may only consist of alphabetic characters, digits and underscores.";
            return result;
        }

        return result;
    }

}
