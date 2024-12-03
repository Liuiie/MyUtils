package com.liuiie.demo.utils.object;

import cn.hutool.core.bean.DynaBean;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * ObjectUtils
 *
 * @author liuzijie
 * @since 2023/1/6 16:25
 */
public class ObjectUtil {
    /**
     * 设置构建属性的值
     *      根据需要增加
     *
     * @param t 对象
     * @return 更新后的对象
     * @param <T> 泛型
     */
    public static <T> T setCreateAttValue(T t) {
        if (ObjectUtils.isEmpty(t)) {
            return null;
        }
        DynaBean dynaBean = new DynaBean(t);
        // 判断对象是否包含创建时间
        boolean createTimeTag = dynaBean.containsProp("createTime");
        if (createTimeTag) {
            dynaBean.set("createTime", new Date());
        }
        return setUpdateAttValue(t);
    }

    /**
     * 设置更新属性的值
     *      根据需要增加
     *
     * @param t 对象
     * @return 更新后的对象
     * @param <T> 泛型
     */
    public static <T> T setUpdateAttValue(T t) {
        if (ObjectUtils.isEmpty(t)) {
            return null;
        }
        DynaBean dynaBean = new DynaBean(t);
        // 判断对象是否包含修改时间
        boolean createTimeTag = dynaBean.containsProp("updateTime");
        if (createTimeTag) {
            dynaBean.set("updateTime", new Date());
        }
        return t;
    }

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
     * @param obj 对象
     * @return 判断结果
     */
    private static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Optional) {
            return !((Optional)obj).isPresent();
        } else if (obj instanceof CharSequence) {
            return ((CharSequence)obj).length() == 0;
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection) {
            return ((Collection)obj).isEmpty();
        } else {
            return obj instanceof Map && ((Map) obj).isEmpty();
        }
    }
}
