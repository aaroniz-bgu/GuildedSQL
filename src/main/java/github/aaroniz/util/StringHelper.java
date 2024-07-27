package github.aaroniz.util;

public class StringHelper {
    /**
     * Checks if a given string is in the given range additionally performs the check:
     * {@code 0 < min -> !s.isBlank()}
     * @param s the string
     * @param min inclusive
     * @param max inclusive
     * @return s
     * @throws IllegalArgumentException if {@code s > max || s < min || 0 < min -> s.isBlank()}.
     */
    public static String checkLengthThrow(String s, int min, int max) {
        if((!(0 < min) && !s.isBlank()) && (min <= s.length() && s.length() <= max)) {
            return s;
        }
        throw new IllegalArgumentException("String " + s + " length is not in the allowed range: " +
                "min="+min+", max="+max);
    }

    /**
     * Checks if the given string is null or blank.
     *
     * @param s the string
     * @return s
     * @throws IllegalArgumentException if the given string is null or blank.
     */
    public static String nullOrBlank(String s) {
        if(s != null && !s.isBlank()) return s;
        throw new IllegalArgumentException("Given a null/blank string: " + s);
    }
}
