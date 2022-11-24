package com.liuiie.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

@SpringBootTest
class DemoApplicationTests {

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
