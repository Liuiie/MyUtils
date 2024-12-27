package com.liuiie.demo.utils.object.template;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;

/**
 * PersonSrcInfo
 *
 * @author Liuiie
 * @since 2024/12/27 16:07
 */
@Data
@FieldNameConstants
public class PersonSrcInfo {
    private Long id;

    private String name;
    private Integer age;
    private LocalDate birth;
    private String email;

    // 无用字段，用于演示拷贝时指定排除字段
    private String removeVal;
}
