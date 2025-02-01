package de.freeschool.api.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFunctions {

    public static String getReadableTimestamp() {
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return now.atZone(ZoneId.of("UTC")).format(formatter);
    }

    public static Date instantToDate(Instant instant) {
        return Date.from(instant);
    }
}
