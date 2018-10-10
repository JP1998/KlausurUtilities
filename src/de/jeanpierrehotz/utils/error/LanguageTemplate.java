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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class can be used as a easy way to change the language of logged messages.
 */
public class LanguageTemplate {

    /**
     * The placeholder used for the cause of an exception.
     */
    public static final String CAUSE_PLACEHOLDER = "{cause}";
    /**
     * The placeholder used for the message of an exception.
     */
    public static final String MESSAGE_PLACEHOLDER = "{message}";
    /**
     * The placeholder used for a line of the stack trace of an exception.
     */
    public static final String STACKTRACE_PLACEHOLDER = "{stacktrace}";
    /**
     * The placeholder for the number of further stack items, when they are omitted.
     */
    public static final String SHORTENER_PLACEHOLDER = "{value}";

    private static final String DEF_EN_MESSAGE_TEMPLATE = "[EEE yyyy-MM-dd HH:mm:ss.SSS zzz]: ";
    private static final String DEF_EN_CAUSE_TEMPLATE = "Caused by: {cause}";
    private static final String DEF_EN_STACKTRACE_MESSAGE_TEMPLATE = "{message}";
    private static final String DEF_EN_STACKTRACE_TEMPLATE = "at {stacktrace}";
    private static final String DEF_EN_CAUSE_MORE_TEMPLATE = "... {value} more";

    private final String message_template;
    private final String cause_template;
    private final String stacktrace_message_template;
    private final String stacktrace_template;
    private final String cause_more_template;

    LanguageTemplate(){
        this(
                DEF_EN_MESSAGE_TEMPLATE,
                DEF_EN_CAUSE_TEMPLATE,
                DEF_EN_STACKTRACE_MESSAGE_TEMPLATE,
                DEF_EN_STACKTRACE_TEMPLATE,
                DEF_EN_CAUSE_MORE_TEMPLATE
        );
    }

    /**
     * The LanguageTemplate will handle any indentations for you, and provides you for every type of template a
     * placeholder that will be replaced by the needed value / text.<br>
     *
     * @param c_tmp         The cause with the placeholder "{cause}"<br>
     *                      Standard value: "Caused by: {cause}"
     * @param stmsg_tmp     The message of the exception with the placeholder "{message}"<br>
     *                      Standard value: "{message}"
     * @param st_tmp        One stacktrace showing where an error ocurred with the placeholder "{stacktrace}"<br>
     *                      Standard value: "at {stacktrace}"
     * @param cm_tmp        The template to shorten the stacktrace with the placeholder "value" for the amount of further stacktrace elements
     *                      Standard value: "... {value} more"
     */
    public LanguageTemplate(String c_tmp, String stmsg_tmp, String st_tmp, String cm_tmp) {
        this(DEF_EN_MESSAGE_TEMPLATE, c_tmp, stmsg_tmp, st_tmp, cm_tmp);
    }

    /**
     * The LanguageTemplate will handle any indentations for you, and provides you for every type of template a
     * placeholder that will be replaced by the needed value / text.<br>
     *
     * @param msg_tmp       The time at the front of every line<br>
     *                      has to be conforming the date formatting regulations (see {@link java.util.Formatter})<br>
     *                      Standard value: "[EEE yyyy-MM-dd HH:mm:ss.SSS zzz]: "
     * @param c_tmp         The cause with the placeholder "{cause}"<br>
     *                      Standard value: "Caused by: {cause}"
     * @param stmsg_tmp     The message of the exception with the placeholder "{message}"<br>
     *                      Standard value: "{message}"
     * @param st_tmp        One stacktrace showing where an error ocurred with the placeholder "{stacktrace}"<br>
     *                      Standard value: "at {stacktrace}"
     * @param cm_tmp        The template to shorten the stacktrace with the placeholder "value" for the amount of further stacktrace elements
     *                      Standard value: "... {value} more"
     */
    public LanguageTemplate(String msg_tmp, String c_tmp, String stmsg_tmp, String st_tmp, String cm_tmp) {
        message_template                =               msg_tmp;
        cause_template                  =               c_tmp;
        stacktrace_message_template     = "        " +  stmsg_tmp;
        stacktrace_template             = "    " +      st_tmp;
        cause_more_template             = "    " +      cm_tmp;
    }

    /**
     * @param time          the Date object representing the current time
     * @return              the datastamp for the beginning of the line
     */
    public final String getMessagePrefix(Date time) {
        return new SimpleDateFormat(message_template).format(time);
    }

    /**
     * @param cause         the name of the class of the cause
     * @return              the cause formatted accordingly
     */
    public final String getCause(String cause) {
        return cause_template.replace(CAUSE_PLACEHOLDER, cause);
    }

    /**
     * @param message       the message to be formatted
     * @return              the message formatted accordingly
     */
    public final String getMessage(String message) {
        return stacktrace_message_template.replace(MESSAGE_PLACEHOLDER, message);
    }

    /**
     * @param stacktrace    the line of the stack trace to be formatted
     * @return              the stack trace line formatted accordingly
     */
    public final String getStacktrace(String stacktrace) {
        return stacktrace_template.replace(STACKTRACE_PLACEHOLDER, stacktrace);
    }

    /**
     * @param value         the amount of further elements in the stack trace
     * @return              the shortener formatted accordingly
     */
    public final String getShortener(int value) {
        return cause_more_template.replace(SHORTENER_PLACEHOLDER, Integer.toString(value));
    }

}
