/*
 *     Copyright 2017 Jean-Pierre Hotz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.jeanpierrehotz.utils.error;

import de.jeanpierrehotz.utils.io.FileCom;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

/**
 * This class is used to log errors or messages to a text-file.<br>
 * These messages are each documented with precise timestamps and are supposed to clear users up about
 * any errors occurring in their code or IDE.<br>
 * The log is not synchronized at all times but saves messages internally to make logging faster.<br>
 * If you want to make sure that the log is synchronized call the {@link #writeLog()}-method.<br>
 * The {@link #logThrowable(Throwable)}-method logs a throwable as follows:
 * <ol>
 * <li>the type of throwable (its classname)</li>
 * <li>the message of the Throwable (if one exists)</li>
 * <li>the stacktrace of the error</li>
 * </ol>
 * Also the Logger keeps track of whether there was something changed in the logs content, and if there was no
 * change a call to {@link #writeLog()} simply won't do anything. This is also done to keep things faster.<br>
 * In case the log has been cleared by calling {@link #clearLog()} and saved thereafter using {@link #writeLog()} the
 * log file will simply be deleted, so you should be aware that the log file may cease to exist from time to time!
 */
public class ErrorLogger {

    /**
     * The LanguageTemplate to use for logging the messages and errors
     */
    private LanguageTemplate locale = new LanguageTemplate();
    /**
     * Whether this log has had activity since its creation
     */
    private boolean activity;
    /**
     * The path to the log file this ErrorLogger logs to
     */
    private String logFilePath;
    /**
     * The current content of the log.<br>
     * Is very likely to be out of synchronization with the actual content of the log file.
     */
    private StringBuilder logContent;

    /**
     * This method creates a log file inside of the given folder.<br>
     * The content of the log file (in case it already exists) is loaded, and messages will simply be appended.
     *
     * @param applicationFolder the folder this application is currently in
     */
    public ErrorLogger(String applicationFolder) {
        logFilePath = ((applicationFolder.endsWith(File.separator)) ? applicationFolder : applicationFolder + File.separator) + "log.txt";

        activity = false;

        if (new File(logFilePath).exists()) {
            logContent = getLogFileContent();
        } else {
            logContent = new StringBuilder();
        }
    }

    /**
     * This method gives you the path to the log file of this logger
     *
     * @return the logs file path
     */
    public String getLogFilePath() {
        return logFilePath;
    }

    /**
     * This method customizes the locale of the ErrorLogger you use.
     * It ignores any {@code null}-values.
     *
     * @param tmp The Locale to use
     */
    public void setLocale(LanguageTemplate tmp) {
        if(tmp != null) {
            this.locale = tmp;
        }
    }

    /**
     * This method logs the given message.<br>
     * If the message is multiline the message will be split up and a timestamp will be
     * added at the begining of every line
     *
     * @param msg the message to log
     */
    public void logMessage(String msg) {
        String[] messageLines = msg.split("\\r?\\n");

        for (String message : messageLines) {
            logContent.append(locale.getMessagePrefix(Calendar.getInstance().getTime()));
            logContent.append(message);
            logContent.append("\n");
        }

        activity = true;
    }

    /**
     * This method logs a given Throwable in following manner:
     * <ol>
     * <li>the name of the Throwable (the class name of it)</li>
     * <li>the Throwables message (if there is a message; split up into lines and with timestamp in front of every one)</li>
     * <li>the stack trace of the Throwable</li>
     * </ol>
     *
     * @param t the Throwable that is to log
     */
    public void logThrowable(Throwable t) {
        StringBuilder logText = new StringBuilder();

        logText.append(locale.getMessagePrefix(Calendar.getInstance().getTime()));
        logText.append(t.getClass().toString().substring(6));

        createMessageText(t, logText);

        for (StackTraceElement element : t.getStackTrace()) {
            logText.append("\n");
            logText.append(locale.getMessagePrefix(Calendar.getInstance().getTime()));
            logText.append(locale.getStacktrace(element.toString()));
        }

        logText.append("\n");

        if(t.getCause() != null){
            logText.append(logCause(t, t.getStackTrace().length));
            logText.append("\n");
        }

        logContent.append(logText);
        activity = true;
    }

    private void createMessageText(Throwable t, StringBuilder logText) {
        if (t.getMessage() != null && !t.getMessage().trim().equals("")) {
            String[] messageLines = t.getMessage().split("\\r?\\n");

            logText.append(": ");
            logText.append(messageLines[0]);

            for (int i = 1; i < messageLines.length; i++) {
                logText.append("\n");
                logText.append(locale.getMessagePrefix(Calendar.getInstance().getTime()));
                logText.append(locale.getMessage(messageLines[i]));
            }
        }
    }

    /**
     * This method logs the cause of the given Throwable, and recursively all the causes of the cause.
     *
     * @param t the Throwable whose cause we should log
     * @param alreadyLogged the amount of stacktrace elements that have already been logged
     * @return the content that has the cause and its causes logged
     */
    private String logCause(Throwable t, int alreadyLogged){
        Throwable cause = t.getCause();

        StringBuilder logText = new StringBuilder();

        logText.append(locale.getMessagePrefix(Calendar.getInstance().getTime()));
        logText.append(locale.getCause(cause.getClass().toString().substring(6)));

        createMessageText(cause, logText);

        StackTraceElement[] elements = cause.getStackTrace();
        for (int i = 0; i < elements.length - alreadyLogged; i++) {
            logText.append("\n");
            logText.append(locale.getMessagePrefix(Calendar.getInstance().getTime()));
            logText.append(locale.getStacktrace(elements[i].toString()));
        }
        logText.append("\n");
        logText.append(locale.getMessagePrefix(Calendar.getInstance().getTime()));
        logText.append(locale.getShortener(alreadyLogged));

        if(cause.getCause() != null) {
            logText.append("\n");
            logText.append(logCause(cause, elements.length));
        }

        return logText.toString();
    }

    /**
     * This method clears the log internally.<br>
     * To make sure the actual log file doesn't exist anymore call {@link #writeLog()} after calling this method
     */
    public void clearLog() {
        logContent = new StringBuilder();
        activity = true;
    }

    /**
     * This method writes the content of the log that has been stored internally
     * to the external file.<br>
     * This will only happen if there has been activity.<br>
     * If the log is empty the log file will be deleted by this method.
     *
     * @return whether the writing process has succeeded
     */
    public boolean writeLog() {
        if (activity) {
            if (logContent.length() != 0) {
                try (BufferedWriter write = new BufferedWriter(new FileWriter(new File(logFilePath)))) {
                    write.write(logContent.toString());
                } catch (IOException e) {
                    return false;
                }
            } else if (new File(logFilePath).exists()) {
                return new File(logFilePath).delete();
            }
        }

        activity = false;
        return true;
    }

    /**
     * This method gives you the content of the current log file
     *
     * @return the content of the log file
     */
    private StringBuilder getLogFileContent() {
        try {
            return new StringBuilder(FileCom.readFullText(new File(getLogFilePath())));
        } catch (IOException e) {
            return new StringBuilder();
        }
    }
}
