package de.jeanpierrehotz.utils.numbers;

import java.util.Random;

/**
 * This class gives you some capabilities of creating random numbers / values.
 */
public class RngUtils {

    static {
        sRandomNumberGenerator = new Random();
    }

    private static Random sRandomNumberGenerator;

    /**
     * Generates a random character between min and max
     * (in the standard Java character encoding).
     * {@code min} and {@code max} are therefore seen as inclusive bounds.
     * In case that {@code min > max}, the bounds will be interchanged.
     *
     * @param min the smallest value that will be allowed
     * @param max the highest value that will be allowed
     * @return a random value between {@code min} and {@code max}
     */
    public static char random(char min, char max) {
        return (char) random((double) min, (double) max);
    }

    /**
     * Generates a random integer between {@code min} and {@code max}.
     * {@code min} and {@code max} are therefore seen as inclusive bounds.
     * In case that {@code min > max}, the bounds will be interchanged.
     *
     * @param min the smallest value that will be allowed
     * @param max the highest value that will be allowed
     * @return a random value between {@code min} and {@code max}
     */
    public static int random(int min, int max) {
        return (int) random((double) min, (double) max);
    }

    /**
     * Generates a random double between {@code min} and {@code max}.
     * {@code min} and {@code max} are therefore seen as inclusive bounds.
     * In case that {@code min > max}, the bounds will be interchanged.
     *
     * @param min the smallest value that will be allowed
     * @param max the highest value that will be allowed
     * @return a random value between {@code min} and {@code max}
     */
    public static double random(double min, double max) {
        if(max < min) {
            double temp = max;
            max = min;
            min = temp;
        }

        return (int) ((sRandomNumberGenerator.nextFloat() * (max - min + 1)) + min);
    }

}
