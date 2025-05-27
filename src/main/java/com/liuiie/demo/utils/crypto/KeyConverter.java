package com.liuiie.demo.utils.crypto;

import java.util.Base64;

/**
 * AES密钥转换工具
 *
 * @author Liuiie
 * @since 2025/5/27 17:04
 */
public class KeyConverter {
    /**
     * 私有构造函数，防止实例化
     */
    private KeyConverter() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * 将Base64编码的AES密钥转换为字节数组
     *
     * @param encodedKey Base64编码的AES密钥
     * @return 字节数组形式的AES密钥
     */
    public static byte[] convertToAESBytes(String encodedKey) {
        byte[] keyBytes = Base64.getUrlDecoder().decode(encodedKey);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid AES-256 key length");
        }
        return keyBytes;
    }
}
