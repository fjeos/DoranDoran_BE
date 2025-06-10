package com.example.dorandroan.global;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ConvertDateUtil {
    public static String getLastChatTime(Instant lastChatTime) {
        ZonedDateTime sendAtKST = lastChatTime.atZone(ZoneId.of("Asia/Seoul"));

        if (Duration.between(sendAtKST.toLocalDate().atStartOfDay(), ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .toLocalDate().atStartOfDay()).toDays() < 1) {
            return sendAtKST.format(DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREAN));
        } else {
            return sendAtKST.format(DateTimeFormatter.ofPattern("yy-MM-dd"));
        }
    }
}
