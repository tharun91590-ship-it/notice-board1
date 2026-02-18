package com.tpgit.noticeboard.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for handling date and time conversions and formatting.
 */
public class DateTimeUtil {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

    /**
     * Format a LocalDateTime object to a string using the default pattern.
     *
     * @param dateTime the LocalDateTime to format
     * @return formatted string, or null if input is null
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }

    /**
     * Parse a string into a LocalDateTime using the default pattern.
     *
     * @param dateTimeStr the string to parse
     * @return parsed LocalDateTime, or null if parsing fails
     */
    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
        } catch (DateTimeParseException e) {
            // Optionally log the error
            return null;
        }
    }

    /**
     * Get the current date and time as a formatted string.
     *
     * @return current date-time string
     */
    public static String nowFormatted() {
        return format(LocalDateTime.now());
    }

    /**
     * Convert a LocalDateTime to a string with a custom pattern.
     *
     * @param dateTime the LocalDateTime
     * @param pattern  the desired pattern
     * @return formatted string, or null if input is null
     */
    public static String formatWithPattern(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Parse a string into a LocalDateTime using a custom pattern.
     *
     * @param dateTimeStr the string
     * @param pattern     the pattern to use
     * @return parsed LocalDateTime, or null on failure
     */
    public static LocalDateTime parseWithPattern(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty() || pattern == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}