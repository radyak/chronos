package net.fvogel.chronos.data.utils;

import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChronosDateSpecValidator {

    /**
     * Explanation:
     * ^-? — start, optional minus sign (allows negative years)
     * \d{1,4} — 1 to 4 digits for the year (so 0 .. 9999, or -0 .. -9999 are allowed)
     * (?:-(0[1-9]|1[0-2]) ... )? — optional month part:
     * - Month must be 01–12
     * - If month is present, the optional day part -(0[1-9]|[12]\d|3[01]) may follow (i.e. day 01–31)
     * $ — end of string
     */
    private static final Pattern PATTERN = Pattern.compile("^-?\\d{1,4}(?:-(0[1-9]|1[0-2])(?:-(0[1-9]|[12]\\d|3[01]))?)?$");

    public static boolean isValid(String dateString, ConstraintValidatorContext constraintValidatorContext) {
        if (dateString == null) {
            return true;
        }
        Matcher m = PATTERN.matcher(dateString);
        return m.matches();
    }
}
