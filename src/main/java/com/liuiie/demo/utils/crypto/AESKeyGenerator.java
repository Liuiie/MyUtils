package com.liuiie.demo.utils.crypto;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * AES密钥生成与管理工具类
 *
 * <p>功能说明：
 * 1. 生成随机AES密钥
 * 2. 从字符串派生安全密钥（PBKDF2）
 * 3. 密钥编解码（Base64）
 * 4. 密钥有效性验证
 *
 * <p>安全规范：
 * 1. AES密钥长度强制256位
 * 2. 使用NIST推荐的PBKDF2算法加强字符串密钥
 * 3. 通过安全随机数生成器生成密钥材料
 *
 * @author Liuiie
 * @since 2025/5/27 16:30
 */
public class AESKeyGenerator {

    private static final String AES_ALGORITHM = "AES";
    /**
     * 单位：bits
     */
    private static final int AES_KEY_SIZE = 256;
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int DEFAULT_ITERATIONS = 65536;
    /**
     * 单位：bytes
     */
    private static final int SALT_LENGTH = 16;

    /**
     * 生成随机AES密钥
     *
     * @return 符合AES-256规范的密钥
     * @throws CryptoException 密钥生成失败时抛出
     */
    public static SecretKey generateRandomKey() throws CryptoException {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_SIZE, SecureRandom.getInstanceStrong());
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("AES密钥生成失败", e);
        }
    }

    /**
     * 从字符串派生AES密钥（PBKDF2算法）
     *
     * @param password  密码字符串（建议长度≥8字符）
     * @param salt      盐值（推荐16字节）
     * @return 符合AES-256规范的密钥
     * @throws CryptoException 密钥派生失败时抛出
     */
    public static SecretKey deriveKeyFromString(String password, byte[] salt) throws CryptoException {
        validatePassword(password);
        validateSalt(salt);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            KeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    DEFAULT_ITERATIONS,
                    AES_KEY_SIZE
            );
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AES_ALGORITHM);
        } catch (Exception e) {
            throw new CryptoException("密钥派生失败", e);
        }
    }

    /**
     * 生成随机盐值
     *
     * @return 16字节的安全随机盐值
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * 将密钥编码为Base64字符串
     *
     * @param key AES密钥
     * @return Base64编码的密钥字符串
     */
    public static String encodeKey(SecretKey key) {
        validateKey(key);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(key.getEncoded());
    }

    /**
     * 从Base64字符串解码密钥
     *
     * @param encodedKey Base64编码的密钥字符串
     * @return 解码后的AES密钥
     * @throws CryptoException 解码失败时抛出
     */
    public static SecretKey decodeKey(String encodedKey) throws CryptoException {
        try {
            byte[] keyData = Base64.getUrlDecoder().decode(encodedKey);
            return new SecretKeySpec(keyData, AES_ALGORITHM);
        } catch (IllegalArgumentException e) {
            throw new CryptoException("密钥解码失败", e);
        }
    }

    // 验证方法
    private static void validateKey(SecretKey key) {
        if (!key.getAlgorithm().equalsIgnoreCase(AES_ALGORITHM)) {
            throw new IllegalArgumentException("无效的密钥类型：" + key.getAlgorithm());
        }
        if (key.getEncoded().length * 8 != AES_KEY_SIZE) {
            throw new IllegalArgumentException("密钥长度不符合AES-256规范");
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("密码长度至少需要8个字符");
        }
    }

    private static void validateSalt(byte[] salt) {
        if (salt == null || salt.length < 8) {
            throw new IllegalArgumentException("盐值长度至少需要8字节");
        }
    }

    /**
     * 自定义加密异常类
     */
    public static class CryptoException extends Exception {
        public CryptoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
