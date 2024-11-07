package com.liuiie.demo.utils.array;

import org.springframework.util.ObjectUtils;

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
     * 将一个 List 拆分成多个列表
     *
     * @param originalList 原始列表
     * @param chunkCount 拆分数量
     * @return 拆分后的列表集合
     * @param <T> 泛型
     */
    public static <T> List<List<T>> splitListIntoChunks(List<T> originalList, int chunkCount) {
        List<List<T>> result = new ArrayList<>();
        // 原始列表为空时直接返回空数组
        if (ObjectUtils.isEmpty(originalList)) {
            return result;
        }
        // 原始列表大小为1时直接返回单个数组
        if (originalList.size() == 1) {
            result.add(originalList);
            return result;
        }
        int size = originalList.size();
        // 每个子列表的基本大小
        int chunkSize = size / chunkCount;
        // 多余的元素数量
        int remainder = size % chunkCount;
        int start = 0;
        for (int i = 0; i < chunkCount; i++) {
            // 计算每个子列表的结束位置
            // 如果有剩余的元素，先分配给前面的子列表
            int end = start + chunkSize + (i < remainder ? 1 : 0);
            List<T> chunk = new ArrayList<>(originalList.subList(start, end));
            result.add(chunk);
            // 更新下一段的起始位置
            start = end;
        }
        return result;
    }

    /**
     * 取交集
     *      使用HashSet快速检查元素是否存在于另一个集合中
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
     *      将两个List转换为Set以自动去除重复元素，然后将它们合并
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
     *      从第一个集合中移除第二个集合中的所有元素
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
     *      将两个列表合并成一个流，使用distinct()方法去除重复项
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
