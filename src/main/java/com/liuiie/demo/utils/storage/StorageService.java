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
package com.liuiie.demo.utils.storage;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * 存储服务
 *
 * @author Liuiie
 * @since 2025/1/6 18:19
 */
public interface StorageService {
    /**
     * 客户端路径分隔符
     */
    String SEPARATOR = "/";

    /**
     * 使用预签名URL上传文件
     *
     * @param objcetName 文件名称
     * @param filePath 文件路径
     * @param presignedUrl 预签名 URL
     * @return 上传结果
     */
    String uploadByPresignedUrl(String objcetName, String filePath, String presignedUrl);

    /**
     * 获取上传文件的预签名url
     *      使用配置的默认存储桶
     *
     * @param objectKey 文件唯一标识
     * @param duration 过期时间
     * @return 上传文件的预签名url
     */
    String createPresignedUploadUrl(String objectKey, Integer duration);

    /**
     * 获取上传文件的预签名url
     *
     * @param bucketName 文件桶
     * @param objectKey 文文件唯一标识
     * @param duration 过期时间
     * @return 上传文件的预签名url
     */
    String createPresignedUploadUrl(String bucketName, String objectKey, Integer duration);

    /**
     * 获取下载文件的预签名url
     *      使用配置的默认存储桶
     *
     * @param objectKey 文件唯一标识
     * @param duration   过期时长
     * @return 下载文件的预签名url
     */
    String createPresignedDownloadUrl(String objectKey, Integer duration);

    /**
     * 获取下载文件的预签名url
     *
     * @param bucketName 文件桶
     * @param objectKey 文件唯一标识
     * @param duration   过期时长
     * @return 下载文件的预签名url
     */
    String createPresignedDownloadUrl(String bucketName, String objectKey, Integer duration);

    /**
     * 下载文件
     *      使用配置的默认存储桶
     *
     * @param originalName 原始名称
     * @param objectKey 文件唯一标识
     * @param response 响应
     */
    void download(String originalName, String objectKey, HttpServletResponse response);

    /**
     * 下载文件
     *
     * @param originalName 原始名称
     * @param bucketName 桶名称
     * @param objectKey 文件唯一标识
     * @param response 响应
     */
    void download(String originalName, String bucketName, String objectKey, HttpServletResponse response);

    /**
     * 获取文件
     *      使用配置的默认存储桶
     *
     * @param objectKey 文件唯一标识
     * @return 文件输入流
     */
    InputStream getObject(String objectKey);

    /**
     * 获取文件
     *
     * @param bucketName 桶名称
     * @param objectKey 文件唯一标识
     * @return 文件输入流
     */
    InputStream getObject(String bucketName, String objectKey);

    /**
     * 存储文件
     *      使用配置的默认存储桶
     *
     * @param file 文件
     * @param objectKey  文件唯一标识
     * @return 预览url
     */
    String upload(MultipartFile file, String objectKey);

    /**
     * 存储文件（存放到指定的bucket里边）
     *
     * @param file 文件
     * @param bucketName  桶名称
     * @param objectKey  文件唯一标识
     * @return 预览url
     */
    String upload(MultipartFile file, String bucketName, String objectKey);

    /**
     * 存储文件（存放到指定的bucket里边）
     *      使用配置的默认存储桶
     *
     * @param byteArray 文件字节数据
     * @param objectKey  文件唯一标识
     * @return 预览url
     */
    String upload(byte[] byteArray, String objectKey);

    /**
     * 存储文件（存放到指定的bucket里边）
     *
     * @param byteArray 文件字节数据
     * @param bucketName  桶名称
     * @param objectKey  文件唯一标识
     * @return 预览url
     */
    String upload(byte[] byteArray, String bucketName, String objectKey);

    /**
     * 删除文件
     *      使用配置的默认存储桶
     *
     * @param objectKey 文件唯一标识
     */
    void removeObject(String objectKey);

    /**
     * 删除文件
     *
     * @param bucketName 文件桶
     * @param objectKey 文件唯一标识
     */
    void removeObject(String bucketName, String objectKey);

    /**
     * 获取默认存储桶
     *
     * @return 默认存储桶
     */
    String getBucket();

    /**
     * 查询存储桶是否存在，不存在直接创建
     *
     * @param bucketName 存储桶名称
     */
    void existBucket(String bucketName);

    /**
     * 获取预览地址
     *
     * @param objectKey 文件唯一标识
     * @return 预览地址
     */
    String getShowUrl(String objectKey);

    /**
     * 获取当前客户端类型
     *
     * @return 客户端类型
     */
    Integer clientType();
}
