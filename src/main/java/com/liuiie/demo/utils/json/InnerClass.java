package com.liuiie.demo.utils.json;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

/**
 * 引用类型
 *
 * @author Liuiie
 * @since 2022/4/27 20:14
 */
public interface InnerClass {
    /**
     * TypeReference泛型实现类
     *
     * @param <T>
     */
    class TypeReferenceImpl<T> extends TypeReference<T> {}

    /**
     * Map<String, Object> TypeReference实现类
     */
    class StrObjectMap extends TypeReference<Map<String, Object>> {}

    /**
     * Map<String, String> TypeReference实现类
     */
    class StrStrMap extends TypeReference<Map<String, String>> {}
}
