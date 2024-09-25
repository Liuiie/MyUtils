package com.liuiie.demo.utils.array;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ListUtil
 *
 * @author Liuiie
 * @since 2024/9/25 14:17
 */
public class ListUtil {
    /**
     * 取交集
     *
     * @param list1 集合1
     * @param list2 集合2
     * @return 交集
     */
    private static List<String> findIntersection(List<String> list1, List<String> list2) {
        Set<String> set1 = new HashSet<>(list1);
        return list2.stream()
                .filter(set1::contains)
                .collect(Collectors.toList());
    }

    /**
     * 取并集
     *
     * @param list1 集合1
     * @param list2 集合2
     * @return 并集
     */
    private static List<String> findUnion(List<String> list1, List<String> list2) {
        Set<String> set1 = new HashSet<>(list1);
        set1.addAll(list2);
        return new ArrayList<>(set1);
    }

    /**
     * 取差集 (list1 - list2)
     *
     * @param list1 集合1
     * @param list2 集合2
     * @return 差集
     */
    private static List<String> findDifference(List<String> list1, List<String> list2) {
        Set<String> set1 = new HashSet<>(list1);
        set1.removeAll(new HashSet<>(list2));
        return new ArrayList<>(set1);
    }

    /**
     * 取去重后的并集
     *
     * @param list1 集合1
     * @param list2 集合2
     * @return 去重后的并集
     */
    private static List<String> findUniqueUnion(List<String> list1, List<String> list2) {
        return Stream.concat(list1.stream(), list2.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
