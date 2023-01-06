package com.liuiie.demo.utils.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 * ObjectUtils
 *
 * @author liuzijie
 * @since 2023/1/6 16:25
 */
public class ObjectUtil {
    /**
     * 检查对象中是否有属性为空
     *      基本类型会赋初始值，所以未进行判断（属性最好使用包装类型）
     *
     * @param object 对象
     * @return 判断结果（存在空属性则返回 true，不存在则返回 false）
     * @throws IllegalAccessException 异常
     */
    public static boolean checkObjFieldIsNull(Object object) throws IllegalAccessException {
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            // 这一句是让检查忽略掉 final static 修饰的属性，当然这个如果你的业务场景不需要，也可以不加。
            if (Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            if (isEmpty(f.get(object))) {
                return true;
            }
            f.setAccessible(false);
        }
        //父类public属性
        for (Field f : object.getClass().getFields()) {
            f.setAccessible(true);
            // 这一句是让检查忽略掉 final static 修饰的属性，当然这个如果你的业务场景不需要，也可以不加。
            if (Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            if (isEmpty(f.get(object))) {
                return true;
            }
            f.setAccessible(false);
        }
        return false;
    }

    /**
     * 判断对象是否为空
     *
     * @param object 对象
     * @return 判断结果
     */
    private static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof String && (object.toString().trim().equals(""))) {
            return true;
        }
        if (object instanceof Collection && ((Collection) object).isEmpty()) {
            return true;
        }
        if (object instanceof Map && ((Map) object).isEmpty()) {
            return true;
        }
        if (object instanceof Object[] && ((Object[]) object).length == 0) {
            return true;
        }
        return false;
    }
}
