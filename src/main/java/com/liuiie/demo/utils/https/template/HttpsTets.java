package com.liuiie.demo.utils.https.template;

import com.alibaba.fastjson.JSON;
import com.liuiie.demo.utils.https.HttpUtils;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

/**
 * HttpsTets
 *
 * @author Liuiie
 * @since 2024/8/22 10:33
 */
public class HttpsTets {
    public static void main(String[] args) throws Exception {
        ParkResponse<List<String>> parkResponse = HttpUtils.doGet("", new ParameterizedTypeReference<ParkResponse<List<String>>>(){});
        System.out.println(JSON.toJSONString(parkResponse));
    }
}
