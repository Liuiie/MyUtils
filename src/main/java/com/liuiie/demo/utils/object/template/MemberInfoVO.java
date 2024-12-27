package com.liuiie.demo.utils.object.template;

import lombok.Data;

/**
 * MemberInfoVO
 *
 * @author Liuiie
 * @since 2024/12/27 16:07
 */
@Data
public class MemberInfoVO {
    private Long id;

    private String name;
    private Integer age;
    private String email;

    private String identity;
    private String group;
}
