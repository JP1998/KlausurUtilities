package de.jeanpierrehotz.utils.io;

import java.io.*;
import java.nio.file.Files;

/**
 * This class provides you with some basic functions for communication with the file system.
 */
public class FileCom {

    /**
     * This method fully reads the given file, and returns
     * the contents of said file in an byte-array.
     *
     * @param f the file to read from
     * @return the contents of given file as a byte-array
     */
    public static byte[] readFullByte(File f) throws IOException {
        return Files.readAllBytes(f.toPath());
    }

    /**
     * This method fully reads the given file, and returns
     * the contents of said file as text.
     *
     * @param f the file to read from
     * @return the contents of given file in form of text
     */
    public static String readFullText(File f) throws IOException {
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
        }
    }

    public static String[] readLines(File f) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(f))) {
            return reader.lines().toArray(String[]::new);
        }
    }

    public static boolean writeFullByte(File f, byte[] arr) {
        try {
            Files.write(f.toPath(), arr);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean writeFullText(File f, String str) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            writer.write(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

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
