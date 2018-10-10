package de.jeanpierrehotz.utils.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a Utility class for processing strings. It contains a method to easily format
 * strings and one that tokenizes a String into the tokens needed for a command.
 */
public class StringProcessing {

    /**
     * The pattern to validate the syntax of a formatting template with.
     */
    private static final Pattern sFormatValidatorPattern =
            Pattern.compile("^([^{}]|(\\{\\{)|(}}))*(\\{\\s*\\d+\\s*}([^{}]|(\\{\\{)|(}}))*)*$");

    /**
     * The pattern to extract wildcards from a formatting template with.
     */
    private static final Pattern sWildcardExtractionPattern =
            Pattern.compile("(\\{\\{)|(}})|(\\{\\s*\\d+\\s*})");

    /**
     * This method makes it easy for you to insert values of variables into a string,
     * by using a format. The formatter for now only supports the inserting of the toString-value
     * of an object given. This can be accomplished by using the wildcard-syntax '{i}' to insert
     * the toString-value of the object given at index i of the replacements parameters.
     * Since curly braces are used for wildcards you'll have to escape them by typing them double.
     * Thus the method-call {@code StrinProcessing.format("{{ - {0} - }}", "Hello");} will produce
     * the result {@code "{ - Hello - }"}.
     * <table>
     *     <tr>
     *         <th style="padding:5px; border: 1px solid black;">Format example</th>
     *         <th style="padding:5px; border: 1px solid black;">Focus on</th>
     *         <th style="padding:5px; border: 1px solid black;">Description</th>
     *     </tr>
     *     <tr>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>some string</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>N/A</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;">Any normal string (except some special cases as explained below) will simply be taken into the result at the specified position.</td>
     *     </tr>
     *     <tr>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>some {{ - string - }}</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>"{{" and "}}"</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;">Since curly braces are used to qualify a wildcard you need to escape curly braces you want printed in the result.</td>
     *     </tr>
     *     <tr>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>I want some {0}.</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>"{0}"</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;">A simple wildcard. The wildcard will be replaced by the toString-value of the object given at the index written within the curly braces.</td>
     *     </tr>
     * </table>
     *
     * @param format       the format with the syntax as specified above.
     * @param replacements the objects whose values are to be inserted into the wildcards.
     * @return the formatted string.
     * @throws StringProcessingFormatException in case the syntax is not valid.
     */
    public static String format(String format, Object... replacements)
            throws StringProcessingFormatException {
        if (sFormatValidatorPattern.matcher(format).matches()) {
            StringBuilder resultBuilder = new StringBuilder();

            Matcher wildcards = sWildcardExtractionPattern.matcher(format);

            boolean found = wildcards.find();

            resultBuilder.append(format, 0, (found) ? wildcards.start() : format.length());

            while (found) {
                // replace while saving end
                String replacement = parseWildcard(wildcards.group(), replacements);
                resultBuilder.append(replacement);
                int start = wildcards.end();
                // find next
                found = wildcards.find();
                // add plain text in between
                resultBuilder.append(format, start, (found) ? wildcards.start() : format.length());
            }

            return resultBuilder.toString();
        } else {
            throw new StringProcessingFormatException(
                    "The given format is not valid. Please refer to the documentation for further information.");
        }
    }

    /**
     * This method parses a wildcard and returns the value that is to be written into the wildcard.
     * It also handles special formatting (which is soon to be implemented) like 0- or whitespace-padding,
     * binary, octal or hexadecimal formatting of numbers and such.
     *
     * @param wildcard     the wildcard that is to be parsed
     * @param replacements the replacements that were given into the format-method
     * @return the text that is to be put into the wildcards position
     */
    private static String parseWildcard(String wildcard, Object[] replacements) {
        switch (wildcard) {
            case "{{": return "{";
            case "}}": return "}";
            default:
                int replacementIndex = Integer.parseInt(wildcard.substring(1, wildcard.length() - 1).trim());

                if (replacementIndex >= replacements.length) {
                    throw new StringProcessingFormatException(StringProcessing.format(
                            "Format requires more replacements than you gave us. Expected (at least):{0}; Given:{1}",
                            replacementIndex,
                            replacements.length
                    ));
                }

                Object replacement = replacements[replacementIndex];
                return (replacement != null)? replacement.toString() : "null";
        }
    }

    /**
     * This method brings a given numbers string representation to a certain length by zero padding it.
     * This also takes into account any negative numbers. If you want the number not to be zero padded,
     * you could either not call this method, or simply give 0 or 1 as the numbers length.
     *
     * @param number the number to pad to the given length
     * @param length the length the given number should be padded to
     * @return the padded representation of the number
     */
    public static String zeroPadding(int number, int length) {
        boolean negative = number < 0;

        if (negative) {
            length--;
        }

        StringBuilder result = new StringBuilder(
                Integer.toString(Math.abs(number))
        );

        while (result.length() < length) {
            result.insert(0, "0");
        }

        if (negative) {
            result.insert(0, "-");
        }

        return result.toString();
    }

    /**
     * This method stretches the given string to the given length while using
     * the given character as a filler. This character is appended or prefixed
     * depending on the value of {@code insertBefore}.
     *
     * @param toStretch the String to stretch
     * @param filler the character to use as filler
     * @param length the length the string is supposed to have afterwards
     * @param insertBefore whether to insert the filler character before
     *                    or after the string
     * @return the string brought to given length with given filler character
     */
    public static String stretch(String toStretch, char filler, int length, boolean insertBefore) {
        StringBuilder result = new StringBuilder(toStretch);

        String toInsert = multiply(Character.toString(filler), length - toStretch.length());

        if(insertBefore) {
            result.insert(0, toInsert);
        } else {
            result.append(toInsert);
        }

        return result.toString();
    }

    /**
     * This method concatenates the string with itself n times and returns the result.
     *
     * @param str the string to concatenate with itself
     * @param n how often the string is supposed to be concatenated with itself
     * @return the given string concatenated n times with itself
     */
    public static String multiply(String str, int n) {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < n; i++) {
            result.append(str);
        }

        return result.toString();
    }

    /**
     * The private default-constructor to keep anyone from instantiating this class,
     * since it is to be used solely in a static context.
     */
    private StringProcessing() {
    }


    public static class StringProcessingFormatException extends RuntimeException {
        public StringProcessingFormatException(String message) {
            super(message);
        }
    }
}
