package ncu.mac.commons.utils;

import ncu.mac.commons.constants.CommonConstants;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateTimeUtil {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter DATE_TIME_AS_STRING = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_AS_STRING2 = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");
    private static final DateTimeFormatter DATE_TIME_AS_PURE_NUMBER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter JUST_HOUR_AND_MIN = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter HOUR_MIN_AND_SEC = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateFormat DATE_AS_STRING = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat HTML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateTimeFormatter HTML_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter ISO8601_DOM_DATE_STRING = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final LocalDateTime FIRST_DAY_OF_21_CENTURY = LocalDateTime.parse("2000-01-01 00:00:00", DATE_TIME_AS_STRING);
    public static final Timestamp FIRST_DAY_OF_21_CENTURY_TIMESTAMP = Timestamp.valueOf(FIRST_DAY_OF_21_CENTURY);

    public static Timestamp currentTimestamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public static Timestamp localDateTime2Timestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }

    public static long secondsToNow(LocalDateTime then) {
        return Duration.between(then, LocalDateTime.now()).getSeconds();
    }

    public static long durationInSeconds(LocalDateTime first, LocalDateTime second) {
        return ChronoUnit.SECONDS.between(first, second);
    }

//    public static int daysToNow(LocalDate then) {
//        return Period.between(then, LocalDate.now()).getDays();
//    }
//
//    public static int daysToNow(Date then) {
//        return Period.between(convertToLocalDateTimeViaInstant(then).toLocalDate(), LocalDate.now()).getDays();
//    }

    public static long sqlDaysFromNow(java.sql.Date then) {
        return ChronoUnit.DAYS.between(LocalDate.now(), then.toLocalDate());
    }

    public static LocalDateTime parseDateTimeString(String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, DATE_TIME_AS_STRING);
        } catch (Exception e) {
            return LocalDateTime.parse(dateTimeString, DATE_TIME_AS_STRING2);
        }
    }

    public static Timestamp parseDateTimeStringAsTimestamp(String dateTimeString) {
        return Timestamp.from(parseDateTimeString(dateTimeString).atZone(CommonConstants.LOCAL_ZONE_ID).toInstant());
    }

    public static long daysBetween(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }

    public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(CommonConstants.LOCAL_ZONE_ID)
                .toLocalDateTime();
    }

    public static Duration timestampDuration(Timestamp then) {
        return Duration.between(then.toLocalDateTime(), LocalDateTime.now());
    }

    public static Period timestampPeriod(Timestamp then) {
        return Period.between(then.toLocalDateTime().toLocalDate(), LocalDate.now());
    }

    public static Duration timestampDurationFromNow(Timestamp then) {
        return Duration.between(LocalDateTime.now(), then.toLocalDateTime());
    }

    public static Period timestampPeriodFromNow(Timestamp then) {
        return Period.between(LocalDate.now(), then.toLocalDateTime().toLocalDate());
    }

    public static java.sql.Date currentSqlDate() {
        return new java.sql.Date(Calendar.getInstance().getTimeInMillis());
    }

    public static Date nDaysBefore(int n) {
        return Date.from(LocalDateTime.now().minusDays(n).atZone(CommonConstants.LOCAL_ZONE_ID).toInstant());
    }

    public static Date nMinutesAgo(long minutes) {
        return Date.from(LocalDateTime.now().minusMinutes(minutes).atZone(CommonConstants.LOCAL_ZONE_ID).toInstant());
    }

    public static Date nMinutesFromNow(long minutes) {
        return Date.from(LocalDateTime.now().plusMinutes(minutes).atZone(CommonConstants.LOCAL_ZONE_ID).toInstant());
    }

    public static String justHourAndMinute(LocalDateTime localDateTime) {
        return JUST_HOUR_AND_MIN.format(localDateTime);
    }

    public static String hourMinuteAndSecond(LocalDateTime localDateTime) {
        return HOUR_MIN_AND_SEC.format(localDateTime);
    }

    public static String dateToString(LocalDateTime dateTime) {
        return DATE_TIME_AS_STRING.format(dateTime);
    }

    public static String dateToString(Date dateTime) {
        return DATE_AS_STRING.format(dateTime);
    }

    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    public static String toHtmlDate(Date date) {
        return HTML_DATE_FORMAT.format(date);
    }

    public static Optional<LocalDate> fromHtmlDate2Local(String date) {
        try {
            return Optional.of(LocalDate.parse(date, ISO8601_DOM_DATE_STRING));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public static java.sql.Date sqlNow() {
        return new java.sql.Date(Calendar.getInstance().getTime().getTime());
    }

    public static Optional<Date> fromHtmlDate(String date) {
        return fromHtmlDate2Local(date)
                .map(localDate -> Date.from(localDate.atStartOfDay(CommonConstants.LOCAL_ZONE_ID).toInstant()));
    }

    public static Optional<java.sql.Date> fromHtmlDate2SqlDate(String date) {
        return fromHtmlDate2Local(date)
                .map(java.sql.Date::valueOf);
    }


    public static Optional<Integer> fromHtml2DateId(String date) {
        return fromHtmlDate2Local(date)
                .map(DateTimeUtil::localDate2DateId);
    }

    public static int localDate2DateId(LocalDate localDate) {
        return localDate.getYear() * 10000 +
                localDate.getMonthValue() * 100 +
                localDate.getDayOfMonth();
    }

    public static LocalDate dateId2LocalDate(int dateId) {
        return LocalDate.parse(dateId2HtmlDate(dateId), HTML_DATE_FORMATTER);
    }

    public static int currentDateId() {
        return localDate2DateId(LocalDate.now());
    }

    public static String pureNumberDateTime() {
        return LocalDateTime.now().format(DATE_TIME_AS_PURE_NUMBER);
    }

//    public static String fromDateId2Html(int dateId) {
//        return fromHtmlDate2Local(dateId)
//                .map(localDate -> localDate.getYear() * 10000 +
//                        localDate.getMonthValue() * 100 +
//                        localDate.getDayOfMonth());
//    }

    public static Stream<Integer> htmlDayStringToDateIdStream(String eventDays) {
        return Stream.of(eventDays.split(","))
                .map(String::trim)
                .map(DateTimeUtil::fromHtml2DateId)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public static String daysStreamToHtmlString(Stream<Integer> eventDays) {
        return eventDays.map(DateTimeUtil::dateId2HtmlDate)
                .collect(Collectors.joining(","));
    }

    public static String dateId2HtmlDate(int dateId) {
        return String.format("%04d-%02d-%02d", dateId / 10000,
                dateId % 10000 / 100,
                dateId % 100);
    }
}
