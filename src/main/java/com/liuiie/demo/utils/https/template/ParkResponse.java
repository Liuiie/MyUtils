package com.liuiie.demo.utils.https.template;

import lombok.Data;

/**
 * ParkResponse
 *
 * @author Liuiie
 * @since 2024/8/22 10:33
 */
@Data
public class ParkResponse<T>  {
    Boolean success;
    String errMsg;
    T data;
}
