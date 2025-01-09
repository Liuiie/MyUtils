/*
 * Copyright [2020-2030] [https://www.stylefeng.cn]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Guns采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Guns源码头部的版权声明。
 * 3.请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://gitee.com/stylefeng/guns
 * 5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/stylefeng/guns
 * 6.若您的项目无法满足以上几点，可申请商业授权
 */
package com.liuiie.demo.utils.storage.service.impl;

import com.liuiie.demo.utils.storage.helper.StorageHelper;
import com.liuiie.demo.utils.storage.service.StorageService;
import com.liuiie.demo.utils.storage.annotate.StorageType;
import com.liuiie.demo.utils.storage.StorageTypeEnum;
import com.liuiie.demo.utils.storage.config.ObsConfig;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.HttpMethodEnum;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import com.obs.services.model.TemporarySignatureRequest;
import com.obs.services.model.TemporarySignatureResponse;
import lombok.extern.log4j.Log4j2;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * 华为云文件操作的实现
 *
 * @author Liuiie
 * @since 2025/1/6 18:19
 */
@Log4j2
@StorageType("obs")
@Service("obsStorageService")
public class ObsStorageServiceImpl implements StorageService {
    /**
     * 华为云客户端
     */
    private final ObsClient obsClient;

    /**
     * 默认存储桶
     */
    private final String bucketName;

    /**
     * obs存储桶配置信息
     */
    private final ObsConfig obsConfig;

    @Autowired
    public ObsStorageServiceImpl(ObsClient obsClient, ObsConfig obsConfig) {
        this.obsClient = obsClient;
        this.obsConfig = obsConfig;
        this.bucketName = obsConfig.getBucketName();
    }

    /**
     * 使用预签名URL上传文件
     *
     * @param objcetName   文件名称
     * @param filePath     文件路径
     * @param presignedUrl 预签名 URL
     * @return 上传结果
     */
    @Override
    public String uploadByPresignedUrl(String objcetName, String filePath, String presignedUrl) {
        try {
            Request.Builder builder = new Request.Builder();
            // 使用PUT请求上传对象
            Request httpRequest =
                    builder.url(presignedUrl)
                            .put(RequestBody.create(MediaType.parse("text/plain"), objcetName.getBytes(StandardCharsets.UTF_8)))
                            .build();
            OkHttpClient httpClient =
                    new OkHttpClient.Builder()
                            .followRedirects(false)
                            .retryOnConnectionFailure(false)
                            .cache(null)
                            .build();
            Call c = httpClient.newCall(httpRequest);
            try (Response res = c.execute()) {
                int responseCode = res.code();
                if (res.body() != null) {
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        return this.getShowUrl(objcetName);
                    }
                    log.info("预签名URL上传文件响应内容: {}", res.body().string());
                }
            }
        } catch (IOException e) {
            log.error("获使用预签名URL上传文件失败: ", e);
        }
        return null;
    }

    /**
     * 获取上传文件的预签名url
     *      使用配置的默认存储桶
     *
     * @param objectKey 文件名
     * @param duration   过期时间
     * @return 上传文件的预签名url
     */
    @Override
    public String createPresignedUploadUrl(String objectKey, Integer duration) {
        return this.createPresignedUploadUrl(this.bucketName, objectKey, duration);
    }

    /**
     * 获取上传文件的预签名url
     *
     * @param bucketName 文件桶
     * @param objectKey 文件名
     * @param duration   过期时间
     * @return 上传文件的预签名url
     */
    @Override
    public String createPresignedUploadUrl(String bucketName, String objectKey, Integer duration) {
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Content-Type", "text/plain");
        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.PUT, duration);
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setHeaders(headers);
        TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
        return response.getSignedUrl();
    }

    /**
     * 获取下载文件的预签名url
     *      使用配置的默认存储桶
     *
     * @param objectKey 文件唯一标识
     * @param duration   过期时长
     * @return 下载文件的预签名url
     */
    @Override
    public String createPresignedDownloadUrl(String objectKey, Integer duration) {
        return this.createPresignedDownloadUrl(this.bucketName, objectKey, duration);
    }

    /**
     * 获取下载文件的预签名url
     *
     * @param bucketName 文件桶
     * @param objectKey 文件唯一标识
     * @return 下载文件的预签名url
     */
    @Override
    public String createPresignedDownloadUrl(String bucketName, String objectKey, Integer duration) {
        try {
            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.PUT, duration);
            request.setBucketName(bucketName);
            request.setObjectKey(objectKey);
            TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
            return response.getSignedUrl();
        } catch (Exception e) {
            log.error("获取预签名url失败: ", e);
        }
        return null;
    }

    /**
     * 下载文件
     *      使用配置的默认存储桶
     *
     * @param originalName 原始名称
     * @param objectKey    文件唯一标识
     * @param response     响应
     */
    @Override
    public void download(String originalName, String objectKey, HttpServletResponse response) {
        this.download(originalName, this.bucketName, objectKey, response);
    }

    /**
     * 下载文件
     *
     * @param bucketName 桶名称
     * @param objectKey  文件唯一标识
     * @param response 响应
     */
    @Override
    public void download(String originalName, String bucketName, String objectKey, HttpServletResponse response) {
        // 流式下载
        ObsObject obsObject = obsClient.getObject(bucketName, objectKey);
        try (InputStream inputStream = obsObject.getObjectContent()) {
            String encodedFileName = StorageHelper.encodeFileName(originalName, response);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (ObsException e) {
            // 请求失败,打印http状态码、服务端错误码、详细错误信息、请求id
            log.error("获取文件失败 \nHTTP Code: {} \nError Code: {} \nError Message: {} \nRequest ID: {} \nHost ID: {}",
                    e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());
        } catch (Exception e) {
            log.error("获取文件失败", e);
        }
    }

    /**
     * 获取文件
     *      使用配置的默认存储桶
     *
     * @param objectKey 文件唯一标识
     * @return 文件输入流
     */
    @Override
    public InputStream getObject(String objectKey) {
        return this.getObject(this.bucketName, objectKey);
    }

    /**
     * 获取文件
     *
     * @param bucketName 桶名称
     * @param objectKey  文件唯一标识
     * @return 文件输入流
     */
    @Override
    public InputStream getObject(String bucketName, String objectKey) {
        return obsClient.getObject(bucketName, objectKey).getObjectContent();
    }

    /**
     * 存储文件
     *      使用配置的默认存储桶
     *
     * @param file       文件
     * @param objectKey 唯一标示id
     */
    @Override
    public String upload(MultipartFile file, String objectKey) {
        return this.upload(file, this.bucketName, objectKey);
    }

    /**
     * 存储文件（存放到指定的bucket里边）
     *
     * @param bucketName 桶名称
     * @param objectKey 唯一标示id
     * @return 预览url
     */
    @Override
    public String upload(MultipartFile file, String bucketName, String objectKey) {
        String fileName = file.getOriginalFilename();
        String suffix = "";
        if (fileName != null) {
            suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        this.existBucket(bucketName);
        fileName = UUID.randomUUID() + "." + suffix;
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream);
            PutObjectResult result = obsClient.putObject(putObjectRequest);
            return result.getObjectUrl();
        } catch (Exception e) {
            log.error("文件{}上传异常: ", fileName, e);
        }
        return null;
    }

    /**
     * 存储文件（存放到指定的bucket里边）
     *      使用配置的默认存储桶
     *
     * @param byteArray 文件字节数据
     * @param objectKey   文件唯一标识
     * @return 预览url
     */
    @Override
    public String upload(byte[] byteArray, String objectKey) {
        return this.upload(byteArray, this.bucketName, objectKey);
    }

    /**
     * 存储文件（存放到指定的bucket里边）
     *
     * @param byteArray 文件字节数据
     * @param bucketName  桶名称
     * @param objectKey   文件唯一标识
     * @return 预览url
     */
    @Override
    public String upload(byte[] byteArray, String bucketName, String objectKey) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray)) {
            PutObjectResult result = obsClient.putObject(bucketName, objectKey, inputStream);
            return result.getObjectUrl();
        } catch (ObsException e) {
            // 请求失败,打印http状态码、服务端错误码、详细错误信息、请求id
            log.error("获取文件失败 \nHTTP Code: {} \nError Code: {} \nError Message: {} \nRequest ID: {} \nHost ID: {}",
                    e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());
        } catch (Exception e) {
            log.error("获取文件失败", e);
        }
        return null;
    }

    /**
     * 删除文件
     *      使用配置的默认存储桶
     *
     * @param objectKey 文件唯一标识
     */
    @Override
    public void removeObject(String objectKey) {
        this.removeObject(this.bucketName, objectKey);
    }

    /**
     * 删除文件
     *
     * @param bucketName 文件桶
     * @param objectKey 文件唯一标识
     */
    @Override
    public void removeObject(String bucketName, String objectKey) {
        try {
            obsClient.deleteObject(bucketName, objectKey);
        } catch (ObsException e) {
            log.error("删除文件败: ", e);
        }
    }

    /**
     * 获取默认存储桶
     *
     * @return 默认存储桶
     */
    @Override
    public String getBucket() {
        return this.bucketName;
    }

    /**
     * 查询存储桶是否存在，不存在直接创建
     *
     * @param bucketName 存储桶名称
     */
    @Override
    public void existBucket(String bucketName) {
        try {
            boolean exist = obsClient.headBucket(bucketName);
            if (!exist) {
                obsClient.createBucket(bucketName);
            }
        } catch (ObsException e) {
            log.error("创建桶{}失败: ", bucketName, e);
        }
    }

    /**
     * 获取预览地址
     *
     *
     * @param objectKey 文件唯一标识
     * @return 预览地址
     */
    @Override
    public String getShowUrl(String objectKey) {
        String endpoint = this.obsConfig.getEndpoint();
        if (endpoint.endsWith(SEPARATOR)) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return "https://" + this.bucketName + "." + endpoint + SEPARATOR + objectKey;
    }

    /**
     * 获取当前客户端类型
     *
     * @return 客户端类型
     */
    @Override
    public String clientType() {
        return StorageTypeEnum.OBS.getType();
    }
}
