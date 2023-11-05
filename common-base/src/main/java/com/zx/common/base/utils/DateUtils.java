package com.zx.common.base.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author ZhaoXu
 * @date 2023/11/5 13:17
 */
public class DateUtils {
    public static Long getNowTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Instant instant = localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant();
        return Date.from(instant).getTime();
    }
}
