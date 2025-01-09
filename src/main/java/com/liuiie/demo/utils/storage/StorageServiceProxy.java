package com.liuiie.demo.utils.storage;

import com.liuiie.demo.utils.storage.context.StorageContext;
import com.liuiie.demo.utils.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * 存储服务代理
 *      默认存储服务接口
 *      指定存储服务接口
 *
 * @author Liuiie
 * @since 2025/1/6 18:19
 */
@Component
public class StorageServiceProxy {
    private final StorageContext storageContext;

    @Autowired
    public StorageServiceProxy(StorageContext storageContext) {
        this.storageContext = storageContext;
    }

    /**
     * 使用预签名URL上传文件
     *      使用默认存储服务
     *
     * @param objcetName   文件名称
     * @param filePath     文件路径
     * @param presignedUrl 预签名 URL
     * @return 上传结果
     */
    public String uploadByPresignedUrl(String objcetName, String filePath, String presignedUrl) {
        StorageService storageService = storageContext.getDefaultStorageService();
        return storageService.uploadByPresignedUrl(objcetName, filePath, presignedUrl);
    }

    /**
     * 使用预签名URL上传文件
     *
     * @param type          存储类型
     * @param objcetName   文件名称
     * @param filePath     文件路径
     * @param presignedUrl 预签名 URL
     * @return 上传结果
     */
    public String uploadByPresignedUrl(String type, String objcetName, String filePath, String presignedUrl) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.uploadByPresignedUrl(objcetName, filePath, presignedUrl);
    }

    /**
     * 获取上传文件的预签名url
     *      使用默认存储服务
     *      使用默认存储桶
     *
     * @param objectKey 文件唯一标识
     * @param duration  过期时间
     * @return 上传文件的预签名url
     */
    public String createPresignedUploadUrl(String objectKey, Integer duration) {
        StorageService storageService = storageContext.getDefaultStorageService();
        return storageService.createPresignedUploadUrl(objectKey, duration);
    }

    /**
     * 获取上传文件的预签名url
     *      使用默认存储桶
     *
     * @param type       存储类型
     * @param objectKey 文件唯一标识
     * @param duration  过期时间
     * @return 上传文件的预签名url
     */
    public String createPresignedUploadUrl(String type, String objectKey, Integer duration) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.createPresignedUploadUrl(objectKey, duration);
    }

    /**
     * 获取上传文件的预签名url
     *
     * @param type 存储类型
     * @param bucketName 文件桶
     * @param objectKey  文文件唯一标识
     * @param duration   过期时间
     * @return 上传文件的预签名url
     */
    public String createPresignedUploadUrl(String type, String bucketName, String objectKey, Integer duration) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.createPresignedUploadUrl(bucketName, objectKey, duration);
    }

    /**
     * 获取下载文件的预签名url
     *      使用默认存储服务
     *      使用默认存储桶
     *
     * @param objectKey 文件唯一标识
     * @param duration  过期时长
     * @return 下载文件的预签名url
     */
    public String createPresignedDownloadUrl(String objectKey, Integer duration) {
        StorageService storageService = storageContext.getDefaultStorageService();
        return storageService.createPresignedDownloadUrl(objectKey, duration);
    }

    /**
     * 获取下载文件的预签名url
     *      使用默认存储桶
     *
     * @param type 存储类型
     * @param objectKey 文件唯一标识
     * @param duration  过期时长
     * @return 下载文件的预签名url
     */
    public String createPresignedDownloadUrl(String type, String objectKey, Integer duration) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.createPresignedDownloadUrl(objectKey, duration);
    }

    /**
     * 获取下载文件的预签名url
     *
     * @param type 存储类型
     * @param bucketName 文件桶
     * @param objectKey  文件唯一标识
     * @param duration   过期时长
     * @return 下载文件的预签名url
     */
    public String createPresignedDownloadUrl(String type, String bucketName, String objectKey, Integer duration) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.createPresignedDownloadUrl(bucketName, objectKey, duration);
    }

    /**
     * 下载文件
     *      使用默认存储服务
     *      使用默认存储桶
     *
     * @param originalName 原始名称
     * @param objectKey    文件唯一标识
     * @param response     响应
     */
    public void download(String originalName, String objectKey, HttpServletResponse response) {
        StorageService storageService = storageContext.getDefaultStorageService();
        storageService.download(originalName, objectKey, response);
    }

    /**
     * 下载文件
     *      使用默认存储桶
     *
     * @param type 存储类型
     * @param originalName 原始名称
     * @param objectKey    文件唯一标识
     * @param response     响应
     */
    public void download(String type, String originalName, String objectKey, HttpServletResponse response) {
        StorageService storageService = storageContext.getStorageService(type);
        storageService.download(originalName, objectKey, response);
    }

    /**
     * 下载文件
     *
     * @param type 存储类型
     * @param originalName 原始名称
     * @param bucketName   桶名称
     * @param objectKey    文件唯一标识
     * @param response     响应
     */
    public void download(String type, String originalName, String bucketName, String objectKey, HttpServletResponse response) {
        StorageService storageService = storageContext.getStorageService(type);
        storageService.download(originalName, bucketName, objectKey, response);
    }

    /**
     * 获取文件
     *      使用默认存储服务
     *      使用默认存储桶
     *
     * @param objectKey 文件唯一标识
     * @return 文件输入流
     */
    public InputStream getObject(String objectKey) {
        StorageService storageService = storageContext.getDefaultStorageService();
        return storageService.getObject(objectKey);
    }

    /**
     * 获取文件
     *      使用默认存储桶
     *
     * @param type 存储类型
     * @param objectKey 文件唯一标识
     * @return 文件输入流
     */
    public InputStream getObject(String type, String objectKey) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.getObject(objectKey);
    }

    /**
     * 获取文件
     *
     * @param type 存储类型
     * @param bucketName 桶名称
     * @param objectKey  文件唯一标识
     * @return 文件输入流
     */
    public InputStream getObject(String type, String bucketName, String objectKey) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.getObject(bucketName, objectKey);
    }

    /**
     * 存储文件
     *      使用默认存储服务
     *      使用默认存储桶
     *
     * @param file      文件
     * @param objectKey 文件唯一标识
     * @return 预览url
     */
    public String upload(MultipartFile file, String objectKey) {
        StorageService storageService = storageContext.getDefaultStorageService();
        return storageService.upload(file, objectKey);
    }

    /**
     * 存储文件
     *      使用默认存储桶
     *
     * @param type 存储类型
     * @param file      文件
     * @param objectKey 文件唯一标识
     * @return 预览url
     */
    public String upload(String type, MultipartFile file, String objectKey) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.upload(file, objectKey);
    }

    /**
     * 存储文件
     *      使用默认存储服务
     *      使用默认存储桶
     *
     * @param file       文件
     * @param bucketName 桶名称
     * @param objectKey  文件唯一标识
     * @return 预览url
     */
    public String upload(MultipartFile file, String bucketName, String objectKey) {
        StorageService storageService = storageContext.getDefaultStorageService();
        return storageService.upload(file, bucketName, objectKey);
    }

    /**
     * 存储文件
     *      使用默认存储桶
     *
     * @param type 存储类型
     * @param file       文件
     * @param bucketName 桶名称
     * @param objectKey  文件唯一标识
     * @return 预览url
     */
    public String upload(String type, MultipartFile file, String bucketName, String objectKey) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.upload(file, bucketName, objectKey);
    }

    /**
     * 存储文件
     *      使用默认存储服务
     *      使用默认存储桶
     *
     * @param byteArray 文件字节数据
     * @param objectKey 文件唯一标识
     * @return 预览url
     */
    public String upload(byte[] byteArray, String objectKey) {
        StorageService storageService = storageContext.getDefaultStorageService();
        return storageService.upload(byteArray, objectKey);
    }

    /**
     * 存储文件
     *      使用默认存储桶
     *
     * @param type 存储类型
     * @param byteArray 文件字节数据
     * @param objectKey 文件唯一标识
     * @return 预览url
     */
    public String upload(String type, byte[] byteArray, String objectKey) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.upload(byteArray, objectKey);
    }

    /**
     * 存储文件（存放到指定的bucket里边）
     *
     * @param type 存储类型
     * @param byteArray  文件字节数据
     * @param bucketName 桶名称
     * @param objectKey  文件唯一标识
     * @return 预览url
     */
    public String upload(String type, byte[] byteArray, String bucketName, String objectKey) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.upload(byteArray, bucketName, objectKey);
    }

    /**
     * 删除文件
     *      使用默认存储服务
     *      使用默认存储桶
     *
     * @param objectKey 文件唯一标识
     */
    public void removeObject(String objectKey) {
        StorageService storageService = storageContext.getDefaultStorageService();
        storageService.removeObject(objectKey);
    }

    /**
     * 删除文件
     *      使用默认存储桶
     *
     * @param type 存储类型
     * @param objectKey 文件唯一标识
     */
    public void removeObject(String type, String objectKey) {
        StorageService storageService = storageContext.getStorageService(type);
        storageService.removeObject(objectKey);
    }

    /**
     * 删除文件
     *
     * @param type 存储类型
     * @param bucketName 文件桶
     * @param objectKey  文件唯一标识
     */
    public void removeObject(String type, String bucketName, String objectKey) {
        StorageService storageService = storageContext.getStorageService(type);
        storageService.removeObject(bucketName, objectKey);
    }

    /**
     * 获取默认存储服务的默认存储桶
     *
     * @return 默认存储桶
     */
    public String getBucket() {
        StorageService storageService = storageContext.getDefaultStorageService();
        return storageService.getBucket();
    }

    /**
     * 获取默认存储桶
     *
     * @param type 存储类型
     * @return 默认存储桶
     */
    public String getBucket(String type) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.getBucket();
    }

    /**
     * 查询存储桶是否存在，不存在直接创建
     *      使用默认存储服务
     *
     * @param bucketName 存储桶名称
     */
    public void existBucket(String bucketName) {
        StorageService storageService = storageContext.getDefaultStorageService();
        storageService.existBucket(bucketName);
    }

    /**
     * 查询存储桶是否存在，不存在直接创建
     *
     * @param type 存储类型
     * @param bucketName 存储桶名称
     */
    public void existBucket(String type, String bucketName) {
        StorageService storageService = storageContext.getStorageService(type);
        storageService.existBucket(bucketName);
    }

    /**
     * 获取预览地址
     *      使用默认存储服务
     *
     * @param objectKey 文件唯一标识
     * @return 预览地址
     */
    public String getShowUrl(String objectKey) {
        StorageService storageService = storageContext.getDefaultStorageService();
        return storageService.getShowUrl(objectKey);
    }

    /**
     * 获取预览地址
     *
     * @param type 存储类型
     * @param objectKey 文件唯一标识
     * @return 预览地址
     */
    public String getShowUrl(String type, String objectKey) {
        StorageService storageService = storageContext.getStorageService(type);
        return storageService.getShowUrl(objectKey);
    }
}
