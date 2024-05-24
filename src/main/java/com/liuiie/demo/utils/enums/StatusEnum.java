package com.liuiie.demo.utils.enums;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * StatusEnum
 *
 * @author Liuiie
 * @since 2024/5/24 8:51
 */
public enum StatusEnum {
    /**
     * key: 0, value: 待提交
     */
    EXPENSE_UNSUBMIT(0, "待提交"),
    /**
     * key: 2, value: 待审核
     */
    EXPENSE_UNAPPROVAL(1, "待审核"),
    /**
     * key: 2, value: 审核通过
     */
    EXPENSE_ADOPTED(2, "审核通过"),
    /**
     * key: 3, value: 驳回
     */
    EXPENSE_REJECT(3, "驳回");

    private Integer key;
    private String value;

    StatusEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 是否包含某个值
     *
     * @param value 值
     * @return true=包含 false=不包含
     */
    public static boolean containsValue(String value) {
        for (StatusEnum e : StatusEnum.values()) {
            if (e.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否包含某个键
     *
     * @param key 键
     * @return true=包含 false=不包含
     */
    public static boolean containsKey(Integer key) {
        for (StatusEnum e : StatusEnum.values()) {
            if (e.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过键获取值
     *
     * @param key 键
     * @return 值
     */
    public static String getValue(Integer key) {
        for (StatusEnum e : StatusEnum.values()) {
            if (e.getKey().equals(key)) {
                return e.getValue();
            }
        }
        return null;
    }

    /**
     * 通过值获取键
     *
     * @param value 值
     * @return 键
     */
    public static Integer getKey(String value) {
        for (StatusEnum e : StatusEnum.values()) {
            if (e.getValue().equals(value)) {
                return e.getKey();
            }
        }
        return null;
    }

    /**
     * 转化为 Map
     *
     * @return map
     */
    public static Map<Integer, String> getOption() {
        Map<Integer, String> option = new LinkedHashMap<>();
        for (StatusEnum e : StatusEnum.values()) {
            option.put(e.getKey(), e.getValue());
        }
        return option;
    }

    /**
     * 获取 key集合
     *
     * @return key集合
     */
    public static List<Integer> getKeyList() {
        List<Integer> keyList = new ArrayList<>();
        for (StatusEnum e : StatusEnum.values()) {
            keyList.add(e.getKey());
        }
        return keyList;
    }

    /**
     * 获取 value集合
     *
     * @return value集合
     */
    public static List<String> getValueList() {
        List<String> valueList = new ArrayList<>();
        for (StatusEnum e : StatusEnum.values()) {
            valueList.add(e.getValue());
        }
        return valueList;
    }

    /**
     * 通过键获取枚举对象
     *
     * @param key 键
     * @return 枚举
     */
    public static StatusEnum getEnum(Integer key) {
        for (StatusEnum e : StatusEnum.values()) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        return null;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
