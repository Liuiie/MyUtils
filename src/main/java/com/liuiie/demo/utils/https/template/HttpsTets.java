package com.liuiie.demo.utils.https.template;

import com.alibaba.fastjson.JSON;
import com.liuiie.demo.utils.https.HttpUtils;
import org.springframework.core.ParameterizedTypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpsTets
 *
 * @author Liuiie
 * @since 2024/8/22 10:33
 */
public class HttpsTets {
    public static void main(String[] args) throws Exception {
        // 调用用例
        ParkResponse<List<String>> parkResponse = HttpUtils.doGet("", new ParameterizedTypeReference<ParkResponse<List<String>>>(){});
        System.out.println(JSON.toJSONString(parkResponse));

        // 地址增加参数用例
        Map<String, String> newParams = new HashMap<>();
        newParams.put("param1", "value1");
        newParams.put("param2", "value2");
        String url1 = "https://app-rgtest.rizhaosteel.com:4443/pdisp/imc/mobile/";
        String updatedUrl1 = HttpUtils.addQueryParameters(url1, newParams);
        System.out.println(updatedUrl1);
        String url2 = "https://app-rgtest.rizhaosteel.com:4443/pdisp/imc/mobile/#/dispatchToUrl";
        String updatedUrl2 = HttpUtils.addQueryParameters(url2, newParams);
        System.out.println(updatedUrl2);
        String url3 = "https://app-rgtest.rizhaosteel.com:4443/pdisp/imc/mobile/#/dispatchToUrl?url=manufacturingDepartment";
        String updatedUrl3 = HttpUtils.addQueryParameters(url3, newParams);
        System.out.println(updatedUrl3);
        String url4 = "https://app-rgtest.rizhaosteel.com:4443/pdisp/imc/mobile?url=manufacturingDepartment#dispatchToUrl";
        String updatedUrl4 = HttpUtils.addQueryParameters(url4, newParams);
        System.out.println(updatedUrl4);
    }
}
