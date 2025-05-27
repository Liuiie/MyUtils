package com.liuiie.demo.utils.crypto;

import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;

/**
 * CryptoTest
 *
 * @author Liuiie
 * @since 2025/5/27 16:39
 */
public class CryptoTest {
    /**
     * 时效窗口（5分钟）
     */
    private static final long TIMESTAMP_VALID_WINDOW = 300_000;

    public static void main(String[] args) {
        String encodedKey = "";

        // =============== 安全加密  =============
        try {
            // 随机生成AES密钥
            SecretKey key = AESKeyGenerator.generateRandomKey();
            // 生成的AES密钥
            encodedKey = AESKeyGenerator.encodeKey(key);
            System.out.println("随机生成的AES密钥：" + encodedKey);
        } catch (AESKeyGenerator.CryptoException e) {
            e.printStackTrace();
        }

        byte[] secretKey = null;
        String ciphertext = null;
        try {
            // AES密钥转换为字节数组
            secretKey = KeyConverter.convertToAESBytes(encodedKey);
            // 进行安全加密
            ciphertext = SecureCryptoUtils.encrypt(secretKey, "敏感数据|"+System.currentTimeMillis());
            System.out.println("安全加密后的数据：" + ciphertext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 解密示例
        try {
            // 解密数据
            String decrypted = SecureCryptoUtils.decrypt(secretKey, ciphertext);
            String[] parts = decrypted.split("\\|");
            // 验证数据完整性
            if (parts.length != 2) {
                throw new SecurityException("无效的数据格式");
            }
            // 解析字段
            String workCode = parts[0];
            long timestamp = Long.parseLong(parts[1]);
            // 验证时间戳
            validateTimestamp(timestamp);
            System.out.println("解密后的数据：" + workCode);
        } catch (AEADBadTagException e) {
            // 处理数据篡改情况
            System.out.println("检测到篡改的加密数据: {" + ciphertext + "}");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // =============== 字符串加密  =============

        // 字符串生成AES密钥
        String password = "MySecurePassword123!@#";
        byte[] salt = AESKeyGenerator.generateSalt();

        try {
            SecretKey derivedKey = AESKeyGenerator.deriveKeyFromString(password, salt);
            encodedKey = AESKeyGenerator.encodeKey(derivedKey);
            System.out.println("派生的AES密钥：" + AESKeyGenerator.encodeKey(derivedKey));
        } catch (AESKeyGenerator.CryptoException e) {
            e.printStackTrace();
        }

        // 加密数据
        try {
            String plaintext = String.join("|", "敏感数据", String.valueOf(System.currentTimeMillis()));
            ciphertext = StringKeyCryptoUtils.encryptWithStringKey(encodedKey, plaintext);
            System.out.println("字符串加密后的数据：" + ciphertext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 解密数据
        try {
            String decrypted = StringKeyCryptoUtils.decryptWithStringKey(encodedKey, ciphertext);
            String[] parts = decrypted.split("\\|");
            // 验证数据完整性
            if (parts.length != 2) {
                throw new SecurityException("无效的数据格式");
            }
            // 解析字段
            String workCode = parts[0];
            long timestamp = Long.parseLong(parts[1]);
            // 验证时间戳
            validateTimestamp(timestamp);
            System.out.println("解密后的数据：" + workCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证时间戳
     *
     * @param timestamp 时间戳
     */
    private static void validateTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - timestamp) > TIMESTAMP_VALID_WINDOW) {
            throw new SecurityException("请求已过期");
        }
    }
}
