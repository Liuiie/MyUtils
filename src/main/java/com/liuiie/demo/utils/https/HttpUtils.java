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
}
