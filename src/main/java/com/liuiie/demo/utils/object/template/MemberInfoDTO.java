package com.liuiie.demo.utils.object.template;

import lombok.Data;

/**
 * MemberInfoDTO
 *
 * @author Liuiie
 * @since 2024/12/27 16:06
 */
@Data
public class MemberInfoDTO {
    private Long id;

    private String name;
    private Integer age;
    private String email;

    private String identity;
    private String group;
}