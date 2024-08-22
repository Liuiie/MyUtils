package com.liuiie.demo;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

//@SpringBootTest
class DemoApplicationTests {
    @Test
    public void test() {

    }

    public static String removeNumberAndHash(String input) {
        // 使用正则表达式匹配并替换#及其前面的数字
        return input.replaceAll("\\d+#", "");
    }

    @Test
    public void calculateHourlyOverlapTest() {
        // 示例开始时间和结束时间（可以根据需要修改）
        String startTime = "2024-06-12 09:15:30";
        String endTime = "2024-06-14 12:15:15";

        System.out.println("Hourly overlap minutes:");
        int[] overlapMinutes = calculateHourlyOverlap2(DateUtil.parse(startTime), DateUtil.parse(endTime));
        for (int i = 0; i < overlapMinutes.length; i++) {
            System.out.println("Hour " + (i + 1) + ": " + overlapMinutes[i] + " minutes");
        }
    }

    public static int[] calculateHourlyOverlap2(Date startTime, Date endTime) {
        int[] hourlyMinutes = new int[24];

        LocalDateTime startDateTime = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endDateTime = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime beginOfNow = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfNow = LocalDateTime.now().with(LocalTime.MAX);

        int startHour = startDateTime.getHour();
        int endHour = endDateTime.getHour();

        if (startDateTime.isBefore(beginOfNow) && endDateTime.isAfter(endOfNow)) {
            for (int hour = 0; hour < 24; hour++) {
                hourlyMinutes[hour] = 60;
            }
        } else if (startDateTime.isBefore(beginOfNow) && endDateTime.isBefore(endOfNow)) {
            for (int hour = 0; hour <= endHour; hour++) {
                hourlyMinutes[hour] = 60;
            }
            hourlyMinutes[endHour] = endDateTime.getMinute();
        } else if (startDateTime.isAfter(beginOfNow) && endDateTime.isBefore(endOfNow)) {
            hourlyMinutes[startHour] = 60 - startDateTime.getMinute();
            for (int hour = startHour + 1; hour < endHour; hour++) {
                hourlyMinutes[hour] = 60;
            }
            hourlyMinutes[endHour] = endDateTime.getMinute();
        } else if (startDateTime.isAfter(beginOfNow) && endDateTime.isAfter(endOfNow)) {
            for (int hour = startHour; hour < 24; hour++) {
                hourlyMinutes[hour] = 60;
            }
            hourlyMinutes[startHour] = 60 - startDateTime.getMinute();
        }

        return hourlyMinutes;
    }

    public static int[] calculateHourlyOverlap(Date startTime, Date endTime) {
        // 创建一个数组来存储每小时的重叠分钟数
        int[] hourlyMinutes = new int[24];

        Date now = new Date();
        DateTime beginOfNow = DateUtil.beginOfDay(now);
        DateTime endOfNow = DateUtil.endOfDay(now);
        DateTime startDateTime = DateUtil.date(startTime);
        DateTime endDateTime = DateUtil.date(endTime);
        int startHour = DateUtil.hour(startDateTime, true);
        int endHour = DateUtil.hour(endTime, true);
        // 情况1: 开始时间在当天之前，结束时间在当天之后
        if (startDateTime.isBefore(beginOfNow) && endDateTime.isAfter(endOfNow)) {
            for (int hour = 0; hour < 24; hour++) {
                hourlyMinutes[hour] = 60;
            }
            // 情况2: 开始时间在当天之前，结束时间在当天结束之前
        } else if (startDateTime.isBefore(beginOfNow) && endDateTime.isBefore(endOfNow)) {
            for (int hour = 0; hour < 24; hour++) {
                if (hour < endHour) {
                    hourlyMinutes[hour] = 60;
                } else if (hour == endHour) {
                    hourlyMinutes[hour] = DateUtil.minute(endTime);
                } else {
                    hourlyMinutes[hour] = 0;
                }
            }
        } else if (startDateTime.isAfter(beginOfNow) && endDateTime.isBefore(endOfNow)) {
            // 情况3: 开始时间在当天，结束时间也在当天
            for (int hour = 0; hour < 24; hour++) {
                if (hour == startHour) {
                    hourlyMinutes[hour] = 60 - DateUtil.minute(startTime);
                } else if (startHour < hour && hour < endHour) {
                    hourlyMinutes[hour] = 60;
                } else if (hour == endHour) {
                    hourlyMinutes[hour] = DateUtil.minute(endDateTime);
                } else {
                    hourlyMinutes[hour] = 0;
                }
            }
        } else if (startDateTime.isAfter(beginOfNow) && endDateTime.isAfter(endOfNow)) {
            // 情况4: 开始时间在当天，结束时间在当天之后
            for (int hour = 0; hour < 24; hour++) {
                if (hour < startHour) {
                    hourlyMinutes[hour] = 0;
                } else if (hour == startHour) {
                    hourlyMinutes[hour] = 60 - DateUtil.minute(startTime);
                } else {
                    hourlyMinutes[hour] = 60;
                }
            }
        } else {
            for (int hour = 0; hour < 24; hour++) {
                hourlyMinutes[hour] = 0;
            }
        }

        return hourlyMinutes;
    }

    @Test
    public void parseTest() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        System.out.println(dateFormat.format(new Date()));
    }

    @Test
    public void stringCompare() {
        List<String> versions = Arrays.asList("20241", "20242", "20243", "20244", "20245");
        versions.sort((a, b) -> b.compareTo(a));
        System.out.println(versions.get(0));
    }
    
    @Test
    public void dateCheck() {
        // 验证日期格式为YYYY-MM-DD的正则表达式为
        String yearRegex = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})";
        String yearMonthRegex = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(([0]+[1-9])|([1]+[0-2])))";
        String dateRegex = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";
        String dateSimpleRegex = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";
        System.out.println("2023-02-29".matches(dateRegex));
    }

    @Test
    public void createUUID() {
        System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    @Test
    public void demoApplicationTests() {
        File file = new File("first", "second");
        System.out.println(file);
    }

    @Test
    public void test01() {
        int[] arr = {1,2,3,4,5,6,7};
        int targetValue = 6;
        getTarget(arr,targetValue);
    }

    /**
     * 找出整型数组中（无重复元素且数组size>1），两个数加和为目标值（targetValue）的整型数对？
     *
     * 示例：
     *
     * 输入：数组=[1,2,3,4,5,6,7]，目标值= 10
     *
     * 输出：[3,7],[4,6]
     */
    public void getTarget(int[] arr, int targetValue) {
        for (int i = 0; i <= arr.length -1; i++) {
            int num = targetValue - arr[i];
            for (int j = i + 1; j <= arr.length - 1; j++) {
                if (num == arr[j]) {
                    System.out.println(Arrays.toString(new int[]{arr[i], num}));
                }
            }
        }
    }

    @Test
    public void test02() {
        List<Integer> newList = new ArrayList();
        newList.add(1);
        newList.add(2);
        newList.add(3);
        newList.add(4);
        newList.add(5);
        List<Integer> oldList = new ArrayList();
        oldList.add(3);
        oldList.add(4);
        oldList.add(5);
        oldList.add(6);
        oldList.add(7);
        getDiff(newList, oldList);
    }

    /**
     * 输入2个List，返回diff结果，不能使用diff工具类，例：
     *
     * 输入
     *
     * newList: [1,2,3,4,5]
     *
     * oldList: [3,4,5,6,7]
     *
     * 输出
     *
     * {
     *
     * "inserList":[1,2],   // newList多出的部分
     *
     * "updateList":[3,4,5], // 重合部分
     *
     * "deleteList":[6,7]  // oldList多出的部分
     *
     * }
     */
    public void getDiff(List<Integer> list1, List<Integer> list2) {
        List<Integer> list = new ArrayList<>();
        ListIterator<Integer> integer1 = list1.listIterator();
        while (integer1.hasNext()) {
            Integer num1 = integer1.next();
            ListIterator<Integer> integer2 = list2.listIterator();
            while (integer2.hasNext()) {
                Integer num2 = integer2.next();
                if (num1.equals(num2)) {
                    list.add(num1);
                    integer1.remove();
                    integer2.remove();
                }
            }
        }
        String response = MessageFormat.format("{\n\"inserList\":{0}\n\"updateList\":{1}\n\"deleteList\":{2}", list1, list, list2);
        System.out.println(response);
        System.out.println("{\n\"inserList\":" + list1 + "\n\"updateList\":" + list + "\n\"deleteList\":" + list2 + "\n}");
    }
}
