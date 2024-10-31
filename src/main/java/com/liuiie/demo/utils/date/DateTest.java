package com.liuiie.demo.utils.date;

import cn.hutool.core.date.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * DateTest
 *
 * @author Liuiie
 * @since 2024/10/31 9:46
 */
public class DateTest {
    public static void main(String[] args) {
        List<Event> events = new ArrayList<>();
        // 添加事件
        events.add(new Event("Meeting",
                DateUtil.parseDateTime("2024-10-30 08:00:00"),
                DateUtil.parseDateTime("2024-10-30 09:00:00")));
        events.add(new Event("Meeting",
                DateUtil.parseDateTime("2024-10-30 08:10:00"),
                DateUtil.parseDateTime("2024-10-30 08:30:00")));
        events.add(new Event("Workshop",
                DateUtil.parseDateTime("2024-10-30 08:00:00"),
                DateUtil.parseDateTime("2024-10-30 09:00:00")));
        DateUtils.timeConflictCheck(events);
    }
}
