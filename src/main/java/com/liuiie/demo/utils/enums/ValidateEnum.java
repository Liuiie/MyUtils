package com.liuiie.demo.utils.enums;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 枚举参数校验注解
 *      使用说明： 在对应字段上加上注解 @ValidateEnum(enumClass = ExpenseApprovalStatusEnum.class, enumValues = {1,2}, message = "枚举值不正确",keyMethod = "getKey")
 *      如果要校验的枚举获取属性值的方法是getValue()，那么keyMethod应该传getValue()
 *      一定一定要在入参上加上@Valid 示例: public void test(@Valid  ExpenseQueryBo expenseQueryBo){}
 *
 * @author Liuiie
 * @since 2024/5/24 8:46
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnumIntegerValidator.class})
@Documented
public @interface ValidateEnum {
    /**
     *  默认错误信息
     *
     * @return 错误信息
     */
    String message() default "枚举值错误，请检查后重试！";

    /**
     * 默认属性名称为key，可自定义
     *
     * @return 属性
     */
    String keyMethod() default "getKey";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 允许的枚举
     *
     * @return 枚举
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 允许的枚举值, 约定为int类型, 判断优先级高于enumClass
     *
     * @return 枚举
     */
    int[] enumValues() default {};

}
