package com.example.dorandroan.global;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ConvertDateUtil {
    public static String getLastChatTime(LocalDateTime lastChatTime) {
        if (lastChatTime.toLocalDate().isEqual(LocalDate.now())) {
            return lastChatTime.atZone(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("a hh:mm"));
        } else {
            return lastChatTime.atZone(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
}
