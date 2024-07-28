package github.aaroniz.util;

public class StringHelper {
    /**
     * Checks if a given string is in the given range.
     *
     * @param s the string
     * @param min inclusive
     * @param max inclusive
     * @return s
     * @throws IllegalArgumentException if {@code s > max || s < min}.
     */
    public static String checkLengthThrow(String s, int min, int max) {
        if(min <= s.length() && s.length() <= max) {
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

    /**
     * Returns the first tilda in a string.
     *
     * @param content the string
     * @return the index
     */
    public static int getFirstTilda(String content) {
        int f = content.indexOf("~");
        return f = f == -1 ? 0 :  f;
    }
}
