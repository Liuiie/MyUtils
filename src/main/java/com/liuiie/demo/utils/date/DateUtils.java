package com.liuiie.demo.utils.date;

import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DateUtils
 *
 * @author liuzijie
 * @since 2024/4/2 8:41
 */
public class DateUtils {
    /**
     * 验证日期格式为 yyyy 的正则表达式为
     */
    private static final String YEAR_REGEX = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})";

    /**
     * 验证日期格式为 yyyy-MM 的正则表达式为
     */
    private static final String YEAR_MONTH_REGEX = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(([0]+[1-9])|([1]+[0-2])))";

    /**
     * 验证日期格式为 yyyy-MM-dd 的正则表达式为
     *      可以区分平润年
     */
    private static final String DATE_REGEX = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";

    /**
     * 验证日期格式为 yyyy-MM-dd 的正则表达式为
     *      不能区分平润年
     */
    private static final String DATE_SIMPLE_REGEX = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";

    /**
     * 日期格式验证
     *
     * @param date 日期
     * @return 格式是否正确
     */
    public boolean dateCheck(String date) {
        return date.matches(DATE_REGEX);
    }

    /**
     * 计算两个时间的重叠时长
     *
     * @param start1 开始时间1
     * @param end1 结束时间1
     * @param start2 开始时间2
     * @param end2 结束时间2
     * @return 重叠时长
     */
    public static long calculateOverlapMinutes(Date start1, Date end1, Date start2, Date end2) {
        // 计算重叠的开始和结束时间
        Date overlapStart = start1.after(start2) ? start1 : start2;
        Date overlapEnd = end1.before(end2) ? end1 : end2;

        // 计算重叠时间的分钟数
        if (overlapStart.before(overlapEnd)) {
            long diffInMillis = overlapEnd.getTime() - overlapStart.getTime();
            // 转换为分钟
            return diffInMillis / (60 * 1000);
        } else {
            // 没有重叠
            return 0;
        }
    }

    /**
     * 时间段冲突校验
     *
     * @param events 时间段信息
     */
    public static Set<String> timeConflictCheck(List<Event> events) {
        if (ObjectUtils.isEmpty(events)) {
            return null;
        }
        Map<String, List<Event>> groupedEvents = new HashMap<>();
        // 按名字分组
        for (Event event : events) {
            groupedEvents.computeIfAbsent(event.getName(), k -> new ArrayList<>())
                    .add(event);
        }
        Set<String> conflictNameSet = new HashSet<>();
        // 检查时间冲突
        for (Map.Entry<String, List<Event>> entry : groupedEvents.entrySet()) {
            String name = entry.getKey();
            List<Event> eventList = entry.getValue();

            if (hasTimeConflict(eventList)) {
                conflictNameSet.add(name);
                System.out.println("时间冲突在名称: " + name);
            } else {
                System.out.println("没有时间冲突在名称: " + name);
            }
        }
        return conflictNameSet;
    }

    /**
     * 判断是否存在时间段冲突
     *
     * @param events 时间段信息
     * @return true=存在冲突 false=不存在冲突
     */
    public static boolean hasTimeConflict(List<Event> events) {
        if (ObjectUtils.isEmpty(events) || events.size() == 1) {
            return false;
        }
        // 按开始时间排序
        events.sort((e1, e2) -> e1.getStartTime().compareTo(e2.getStartTime()));
        for (int i = 0; i < events.size() - 1; i++) {
            Event current = events.get(i);
            Event next = events.get(i + 1);
            // 检查时间是否重叠
            if (current.getEndTime().after(next.getStartTime())) {
                // 存在冲突
                return true;
            }
        }
        // 无冲突
        return false;
    }
}
