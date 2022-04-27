package com.liuiie.demo.utils;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

/**
 * 引用类型
 *
 * @author Liuiie
 * @since 2022/4/27 20:14
 */
public interface InnerClass {
    TypeReference<Map<String, Object>> MAP_OBJECT = new TypeReference<Map<String, Object>>() {};

    TypeReference<Map<String, String>> MAP_STRING = new TypeReference<Map<String, String>>() {};
}
