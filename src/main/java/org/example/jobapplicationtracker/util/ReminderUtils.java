package org.example.jobapplicationtracker.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ReminderUtils {

    private static final int REMINDER_DAYS = 3;

    private ReminderUtils() {}

    public static boolean isDeadlineApproaching(LocalDate deadline) {
        if (deadline == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(REMINDER_DAYS);
        return !deadline.isBefore(today) && !deadline.isAfter(limit);
    }

    public static boolean isDeadlineOverdue(LocalDate deadline) {
        return deadline != null && deadline.isBefore(LocalDate.now());
    }

    public static boolean isMeetingApproaching(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.plusDays(REMINDER_DAYS);
        return !dateTime.isBefore(now) && !dateTime.isAfter(limit);
    }
}
