package org.example.jobapplicationtracker.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReminderUtilsTest {

    @Test
    void isDeadlineApproaching_withinThreeDays_returnsTrue() {
        LocalDate deadline = LocalDate.now().plusDays(2);
        assertTrue(ReminderUtils.isDeadlineApproaching(deadline));
    }

    @Test
    void isDeadlineApproaching_pastDate_returnsFalse() {
        assertFalse(ReminderUtils.isDeadlineApproaching(LocalDate.now().minusDays(1)));
    }

    @Test
    void isMeetingApproaching_withinThreeDays_returnsTrue() {
        LocalDateTime meeting = LocalDateTime.now().plusDays(1);
        assertTrue(ReminderUtils.isMeetingApproaching(meeting));
    }
}
