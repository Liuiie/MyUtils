package com.liuiie.demo.utils.enums;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

/**
 * EnumIntegerValidator
 *
 * @author Liuiie
 * @since 2024/5/24 8:47
 */
public class EnumIntegerValidator implements ConstraintValidator<ValidateEnum, Integer> {
    private Class<? extends Enum> enumClass;
    private int[] enumValues;

    private String keyMethod;

    @Override
    public void initialize(ValidateEnum validateEnum) {
        enumClass = validateEnum.enumClass();
        enumValues = validateEnum.enumValues();
        keyMethod = validateEnum.keyMethod();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        try {
            //enumValues 优先级高
            if(enumValues.length ==0 ){
                Enum[] enumArr = enumClass.getEnumConstants();
                if(enumArr.length>0){
                    for (Enum e : enumArr) {
                        Method method = e.getDeclaringClass().getMethod(keyMethod);
                        Object keyValue = method.invoke(e);
                        if(keyValue != null){
                            if(value.equals(keyValue)){
                                return true;
                            }
                        }
                    }
                    return false;
                }

            }else {
                for (int enumValue : enumValues) {
                    if(enumValue == value){
                        return true;
                    }
                }
                return false;
            }
            return true;
        }catch (Exception e){
            return false;
        }

    }
}


