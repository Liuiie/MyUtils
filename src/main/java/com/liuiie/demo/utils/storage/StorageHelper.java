package com.liuiie.demo.utils.storage;

import com.technologymanagement.common.enums.StorageTypeEnum;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * StorageHelper
 *
 * @author Liuiie
 * @since 2025/1/8 11:32
 */
public class StorageHelper {
    /**
     * 对文件名进行编码
     *
     * @param fileName 文件名
     * @param response 响应
     * @return 编码后的文件名
     */
    public static String encodeFileName(String fileName, HttpServletResponse response) {
        String userAgent = response.getHeader("User-Agent");
        if (userAgent == null) {
            userAgent = "";
        }

        try {
            if (userAgent.toLowerCase().contains("firefox")) {
                // Firefox浏览器
                return new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            } else {
                // 其他浏览器（如Chrome）
                return URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查传入类型是否是minio
     *      默认无值也当作是minio
     *
     * @param type 类型
     * @return 如果是minio则返回true，否则返回false
     */
    public static boolean minioCheck(Integer type) {
        return null == type || StorageTypeEnum.MINIO.getType().compareTo(type) == 0;
    }
}
