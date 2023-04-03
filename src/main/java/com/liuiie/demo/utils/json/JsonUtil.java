package com.liuiie.demo.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.google.json.JsonSanitizer;

/**
 * Json 工具类
 *
 * @author lirui
 * @since 2022-3-22
 */
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    private static <T> T doConvert(String json, CheckedFunction<String, T> strategy) throws Exception {
        json = JsonSanitizer.sanitize(json);
        return strategy.apply(json);
    }

    private static String doConvert(Object object, CheckedFunction<Object, String> strategy) throws Exception {
        return strategy.apply(object);
    }

    /**
     * 对象转 json
     *
     * @param object 对象
     * @return json 字符串
     * @throws ConvertException 转换异常
     */
    public static String convertObj2String(Object object) throws ConvertException {
        if (object instanceof String) {
            return (String) object;
        }
        CheckedFunction<Object, String> strategy = OBJECT_MAPPER::writeValueAsString;
        try {
            return doConvert(object, strategy);
        } catch (Exception e) {
            throw new ConvertException(e.getMessage());
        }
    }

    /**
     * 对象转 json
     *
     * @param object 对象
     * @param provider 自定义字段过滤、条件序列化的filter
     * @return json 字符串
     * @throws ConvertException 转换异常
     */
    public static String convertObj2String(Object object, FilterProvider provider) throws ConvertException {
        OBJECT_MAPPER.setFilterProvider(provider);
        return convertObj2String(object);
    }

    /**
     * json 转 对象
     *
     * @param objectJson json字符串
     * @param clazz 类
     * @return 类实体
     * @param <T> 泛型
     * @throws ConvertException 转换异常
     */
    public static <T> T convertString2Obj(String objectJson, Class<T> clazz) throws ConvertException {
        CheckedFunction<String, T> strategy = json -> OBJECT_MAPPER.readValue(objectJson, clazz);
        try {
            return doConvert(objectJson, strategy);
        } catch (Exception e) {
            throw new ConvertException(e.getMessage());
        }
    }

    /**
     * json 转 对象
     *
     * @param objectJson json 字符串
     * @param valTypeRef 泛型类型
     * @return 类实体
     * @param <T> 泛型
     * @throws ConvertException 转换异常
     */
    public static <T> T convertString2Obj(String objectJson, TypeReference<T> valTypeRef) throws ConvertException {
        CheckedFunction<String, T> strategy = json -> OBJECT_MAPPER.readValue(objectJson, valTypeRef);
        try {
            return doConvert(objectJson, strategy);
        } catch (Exception e) {
            throw new ConvertException(e.getMessage());
        }
    }

    /**
     * 将对象转换为特定类型值
     *
     * @param fromValue 对象
     * @param valType 特定类型
     * @return 特定类型对象
     * @param <T> 泛型
     */
    public static <T> T convertValue(Object fromValue, Class<T> valType) {
        return OBJECT_MAPPER.convertValue(fromValue, valType);
    }

    /**
     * 将对象转换为特定类型值
     *
     * @param fromValue 对象
     * @param valTypeRef 引用类型
     * @return 引用类型对象
     * @param <T> 泛型
     */
    public static <T> T convertValue(Object fromValue, TypeReference<T> valTypeRef) {
        return OBJECT_MAPPER.convertValue(fromValue, valTypeRef);
    }
}
