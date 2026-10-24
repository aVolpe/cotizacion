package py.com.volpe.cotizacion;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Checks the hours of {@link Scheduler#triggerQuery()}: both crons are combined, the next execution is the
 * earliest of them.
 */
class SchedulerTest {

    private static final ZoneId ZONE = ZoneId.of(Scheduler.ZONE);

    // 2026-10-23 is a Friday
    private static final LocalDateTime FRIDAY = LocalDateTime.of(2026, 10, 23, 0, 0);
    private static final LocalDateTime SATURDAY = FRIDAY.plusDays(1);
    private static final LocalDateTime SUNDAY = FRIDAY.plusDays(2);
    private static final LocalDateTime MONDAY = FRIDAY.plusDays(3);

    @Test
    void weekdaysRunUntil2050() {
        assertThat(nextAfter(FRIDAY.withHour(20).withMinute(40))).isEqualTo(FRIDAY.withHour(20).withMinute(50));
    }

    @Test
    void weekdaysStartAt7() {
        assertThat(nextAfter(MONDAY.withHour(6).withMinute(59))).isEqualTo(MONDAY.withHour(7));
    }

    @Test
    void afterFriday2050TheNextRunIsSaturdayAt7() {
        assertThat(nextAfter(FRIDAY.withHour(20).withMinute(50))).isEqualTo(SATURDAY.withHour(7));
    }

    @Test
    void saturdaysRunUntil1550() {
        assertThat(nextAfter(SATURDAY.withHour(15).withMinute(40))).isEqualTo(SATURDAY.withHour(15).withMinute(50));
    }

    @Test
    void afterSaturday1550TheNextRunIsMondayAt7() {
        assertThat(nextAfter(SATURDAY.withHour(15).withMinute(50))).isEqualTo(MONDAY.withHour(7));
        assertThat(nextAfter(SATURDAY.withHour(16))).isEqualTo(MONDAY.withHour(7));
    }

    @Test
    void sundaysDontRun() {
        assertThat(nextAfter(SUNDAY)).isEqualTo(MONDAY.withHour(7));
        assertThat(nextAfter(SUNDAY.withHour(12))).isEqualTo(MONDAY.withHour(7));
    }

    @Test
    void triggerQueryUsesBothCrons() throws NoSuchMethodException {
        Scheduled[] scheduled = Scheduler.class.getMethod("triggerQuery").getAnnotationsByType(Scheduled.class);

        assertThat(scheduled)
                .extracting(Scheduled::cron, Scheduled::zone)
                .containsExactlyInAnyOrder(
                        tuple(Scheduler.WEEKDAYS_CRON, Scheduler.ZONE),
                        tuple(Scheduler.SATURDAY_CRON, Scheduler.ZONE));
    }

    private static LocalDateTime nextAfter(LocalDateTime time) {
        ZonedDateTime from = time.atZone(ZONE);
        return Stream.of(Scheduler.WEEKDAYS_CRON, Scheduler.SATURDAY_CRON)
                .map(cron -> CronExpression.parse(cron).next(from))
                .min(ZonedDateTime::compareTo)
                .orElseThrow()
                .toLocalDateTime();
    }
}
