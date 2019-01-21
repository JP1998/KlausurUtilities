package de.jeanpierrehotz.utils.io;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides you with some basic functions for communication with the file system.
 */
public class FileCom {

    /**
     * This method fully reads the given file, and returns
     * the contents of said file in an byte-array.
     *
     * @param f the file to read from
     * @return the contents of given file as a byte-array;
     *         an empty array in case there is an error with the i/o
     */
    public static byte[] readFullByte(File f) {
        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            return new byte[0];
        }
    }

    /**
     * This method fully reads the given file, and returns
     * the contents of said file as text.
     *
     * @param f the file to read from
     * @return the contents of given file in form of text;
     *         {@code ""} in case there is an error with the i/o
     */
    public static String readFullText(File f) {
        try(BufferedReader reader = new BufferedReader(new FileReader(f))) {
            return reader.lines().reduce("", (a, b) -> {
                if(a.equals("")) {
                    return b;
                } else if(b.equals("")) {
                    return a;
                } else {
                    return a + System.lineSeparator() + b;
                }
            });
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * This method reads all the lines contained in the given file,
     * and returns them within a list.
     *
     * @param f the file to read from
     * @return the contents of the given file with the single lines
     *         in a list; an empty list in case there was an error
     *         with the i/o
     */
    public static List<String> readLines(File f) {
        try(BufferedReader reader = new BufferedReader(new FileReader(f))) {
            return reader.lines().collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * This method fully writes the given byte array into the given file.
     *
     * @param f the file to write to.
     * @param arr the array to write to the given file.
     * @return whether or not the write was successful.
     */
    public static boolean writeFullByte(File f, byte[] arr) {
        try {
            Files.write(f.toPath(), arr);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * This method writes the full within the given file.
     * It will not preserve any contents that are already in the given file.
     *
     * @param f the file to write to
     * @param str the text to write to
     * @return whether the writing was successful
     */
    public static boolean writeFullText(File f, String str) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            writer.write(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * This method appends the given strings in their given order
     * as single lines to the given file.
     *
     * @param f the file to append the lines to
     * @param lines the lines to append to the file
     * @return whether or not the writing was (fully) successful
     */
    public static boolean appendLines(File f, String... lines) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(f, true))) {
            for(String str : lines) {
                writer.println(str);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
