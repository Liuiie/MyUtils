package com.liuiie.demo.utils.storage.service.impl;

import com.liuiie.demo.utils.storage.helper.StorageHelper;
import com.liuiie.demo.utils.storage.service.StorageService;
import com.liuiie.demo.utils.storage.annotate.StorageType;
import com.liuiie.demo.utils.storage.StorageTypeEnum;
import com.liuiie.demo.utils.storage.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * minio工具类
 *
 * @author Liuiie
 * @since 2025/1/6 18:19
 */
@Log4j2
@StorageType("minio")
@Service("minioStorageService")
public class MinioStorageServiceImpl implements StorageService {
    /**
     * Minio客户端
     */
    private final MinioClient minioClient;

    /**
     * 默认存储桶
     */
    private final String bucketName;

    /**
     * minio存储桶配置信息
     */
    private final MinioConfig minioConfig;

    @Autowired
    public MinioStorageServiceImpl(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
        this.bucketName = minioConfig.getBucketName();
    }

    /**
     * 使用预签名URL上传文件
     *
     * @param objcetName 文件名称
     * @param filePath 文件路径
     * @param presignedUrl 预签名 URL
     * @return 上传结果
     */
    @Override
    public String uploadByPresignedUrl(String objcetName, String filePath, String presignedUrl) {
        try {
            URL url = new URL(presignedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");

            FileInputStream fileInputStream = new FileInputStream(filePath);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            fileInputStream.close();

            // 检查响应码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_CREATED) {
                return this.getShowUrl(objcetName);
            }
        } catch (Exception e) {
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
     * @param duration 过期时间
     * @return 上传文件的预签名url
     */
    @Override
    public String createPresignedUploadUrl(String bucketName, String objectKey, Integer duration) {
        try {

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(duration, TimeUnit.HOURS)
                            .build());
        } catch (Exception e) {
            log.error("获取上传文件的预签名url异常", e);
        }
        return null;
    }

    /**
     * 获取下载文件的预签名url
     *      使用配置的默认存储桶
     *
     * @param objectKey 文件唯一标识
     * @param duration 过期时长
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
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .method(Method.GET)
                            .expiry(duration, TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            log.error("获取下载文件的预签名url异常", e);
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
        InputStream inputStream = null;
        try {
            inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey).build());
            String encodedFileName = StorageHelper.encodeFileName(originalName, response);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("Failed to download file: {}", objectKey, e);
        } finally {
            //关闭流
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                response.getOutputStream().flush();
            } catch (IOException e) {
                log.error("Failed to close input stream", e);
            }
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
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("获取文件异常", e);
        }
        return null;
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
     * @param file 文件
     * @param bucketName  桶名称
     * @param objectKey  唯一标示id
     * @return 预览url
     */
    @Override
    public String upload(MultipartFile file, String bucketName, String objectKey) {
        String fileName = file.getOriginalFilename();
        String suffix = "";
        if (fileName != null) {
            suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        // 判断bucket是否存在，不存在则创建
        this.existBucket(bucketName);
        // 判定如果是图片就重命名
        fileName = UUID.randomUUID() + "." + suffix;
        try (InputStream in = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(in, file.getSize(), -1)
                    .build());
        } catch (NoSuchAlgorithmException | IOException | ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | ServerException | XmlParserException e) {
            log.error("Failed to upload file: {}", fileName, e);
        }
        return this.getShowUrl(fileName);
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
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(inputStream, byteArray.length, -1)
                    .build());
        } catch (NoSuchAlgorithmException | IOException | ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | ServerException | XmlParserException e) {
            log.error("Failed to upload file: {}", objectKey, e);
        }
        return this.getShowUrl(objectKey);
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
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("获取文件失败", e);
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
     * @param name 存储桶名称
     */
    @Override
    public void existBucket(String name) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
            }
        } catch (Exception e) {
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
        String showUrl = this.minioConfig.getEndpoint();
        if (showUrl.endsWith(SEPARATOR)) {
            showUrl = showUrl.substring(0, showUrl.length() - 1);
        }
        return showUrl + SEPARATOR + this.bucketName + SEPARATOR + objectKey;
    }

    /**
     * 获取当前客户端类型
     *
     * @return 客户端类型
     */
    @Override
    public String clientType() {
        return StorageTypeEnum.MINIO.getType();
    }
}
