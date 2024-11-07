package com.liuiie.demo.utils.https;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * HttpUtils
 *
 * @author Liuiie
 * @since 2024/8/22 10:28
 */
public class HttpUtils {
    /**
     * 调用第三方服务器接口  能用于上传 附件
     *
     * @param url 路由
     * @param body 请求体
     * @return 响应体里的数据
     */
    public static <T> String doPostForData(String url, T body) throws Exception {
        ResponseEntity<String> response = doPostForResponse(url, body);
        return response.getBody();
    }

    /**
     * 调用第三方服务器接口  能用于上传 附件
     *
     * @param url 路由
     * @param body 请求体
     * @return 返回响应所有参数
     */
    public static <T> ResponseEntity<String> doPostForResponse(String url, T body) throws Exception {
        //访问第三方服务器
        return doPostForResponse(url, body, null);
    }

    /**
     * 调用第三方服务器接口  没有附件上传的那种
     *
     * @param url 路由
     * @param body 参数
     * @param stringList 请求头参数
     * @return 返回响应体body的数据
     */
    public static <T> String doPostForBody(String url, T body, List<String> stringList) throws Exception {
        RestTemplate restTemplate = new RestTemplate(RestTemplateConfig.generateHttpRequestFactory());
        //此处加编码格式转换
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        // 设置请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.put(HttpHeaders.AUTHORIZATION, stringList);
        //访问第三方服务器
        ResponseEntity<String> response = doPostForResponse(url, body, httpHeaders);
        return response.getBody();
    }

    /**
     * 调用第三方服务器接口  能用于上传 附件
     *
     * @param url 路由
     * @param body 请求体
     * @param httpHeaders 请求头
     * @return 响应体里的数据
     */
    public static <T> String doPostForBody(String url, T body, HttpHeaders httpHeaders) throws Exception {
        ResponseEntity<String> response = doPostForResponse(url, body, httpHeaders);
        return response.getBody();
    }

    /**
     * 调用第三方服务器接口  能用于上传 附件
     *
     * @param url 路由
     * @param body 请求体
     * @param httpHeaders 请求头
     * @return 响应体里的数据
     */
    public static <T> ResponseEntity<String> doPostForResponse(String url, T body, HttpHeaders httpHeaders) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestTemplate restTemplate = new RestTemplate(RestTemplateConfig.generateHttpRequestFactory());
        //此处加编码格式转换
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<T> httpEntity = new HttpEntity<>(body, httpHeaders);
        //访问第三方服务器
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
    }

    /**
     * 调用第三方服务器接口  get
     *
     * @param url 路由
     * @param responseType 响应类型
     * @return 返回相应所有参数
     */
    public static <T> T doGet(String url, ParameterizedTypeReference<T> responseType) throws Exception {
        ResponseEntity<T> response = doGet(url, new HttpHeaders(), responseType);
        return response.getBody();
    }

    /**
     * 调用第三方服务器接口  get
     *
     * @param url 路由
     * @param httpHeaders 请求头
     * @param responseType 响应类型
     * @return 返回相应所有参数
     */
    public static <T> ResponseEntity<T> doGet(String url, HttpHeaders httpHeaders, ParameterizedTypeReference<T> responseType) throws Exception {
        RestTemplate restTemplate = new RestTemplate(RestTemplateConfig.generateHttpRequestFactory());
        //此处加编码格式转换
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        // 设置请求头
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<T> httpEntity = new HttpEntity<>(httpHeaders);
        // 发送 GET 请求
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, responseType);
    }

    /**
     * 调用第三方服务器接口  get
     *
     * @param url 路由
     * @param httpHeaders 请求头
     * @return 返回响应体body的数据
     */
    public static String doGetBody(String url, HttpHeaders httpHeaders) throws Exception {
        ResponseEntity<String> responseEntity = doGet(url, httpHeaders);
        return responseEntity.getBody();
    }

    /**
     * 调用第三方服务器接口  get
     *
     * @param url         路由
     * @param httpHeaders 请求头
     * @return 返回相应所有参数
     */
    public static ResponseEntity<String> doGet(String url, HttpHeaders httpHeaders) throws Exception {
        RestTemplate restTemplate = new RestTemplate(RestTemplateConfig.generateHttpRequestFactory());
        //此处加编码格式转换
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        // 设置请求头
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        // 发送 GET 请求
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
    }

    /**
     * 在现有地址上增加传参
     *      接受多个查询参数的方式：Map<String, String>
     *
     * @param url 原地址
     * @param newParams 入参信息
     * @return 新地址
     */
    public static String addQueryParameters(String url, Map<String, String> newParams) {
        // 检查 URL 是否包含查询字符串和锚点
        int queryIndex = url.indexOf('?');
        int fragmentIndex = url.indexOf('#');
        // 如果没有查询部分并且有锚点
        if (queryIndex == -1 && fragmentIndex != -1) {
            // 处理带有锚点但是没有查询部分的情况
            return url + "?" + buildQueryParams(newParams);
        }
        // 提取查询部分和锚点部分
        String baseUrl = url;
        String query = "";
        String fragment = "";
        if (queryIndex != -1) {
            if (fragmentIndex != -1 && fragmentIndex > queryIndex) {
                // 基础部分不包含查询字符串和锚点
                baseUrl = url.substring(0, queryIndex);
                // 提取查询部分
                query = url.substring(queryIndex + 1, fragmentIndex);
                // 提取锚点部分
                fragment = url.substring(fragmentIndex);
            } else {
                // 基础部分不包含查询字符串
                baseUrl = url.substring(0, queryIndex);
                // 提取查询部分
                query = url.substring(queryIndex + 1);
            }
        } else {
            // 如果没有查询参数，处理锚点情况
            if (fragmentIndex != -1) {
                // 基础部分不包含锚点
                baseUrl = url.substring(0, fragmentIndex);
                // 提取锚点部分
                fragment = url.substring(fragmentIndex);
            }
        }
        // 构建新的查询参数字符串
        String newQuery = query.isEmpty() ? buildQueryParams(newParams) : query + "&" + buildQueryParams(newParams);
        // 如果有锚点部分，拼接在新的查询字符串后面
        return baseUrl + "?" + newQuery + fragment;
    }

    /**
     * 构建查询参数字符串
     *
     * @param newParams 新入参
     * @return 参数字符串
     */
    private static String buildQueryParams(Map<String, String> newParams) {
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : newParams.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            queryString.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return queryString.toString();
    }
}
