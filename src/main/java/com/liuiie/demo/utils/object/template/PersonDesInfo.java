package com.liuiie.demo.utils.object.template;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;

/**
 * PersonDesInfo
 *
 * @author Liuiie
 * @since 2024/12/27 16:08
 */
@Data
@FieldNameConstants
public class PersonDesInfo {
    private Long id;

    private String personName;
    private Integer age;
    private LocalDate dateOfBirth;
    private String emailAddress;
}