package com.liuiie.demo.utils.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * AES-GCM 字符串密钥加密工具类
 *
 * <p>安全说明：建议使用真正随机的密钥字符串（至少32字符）</p>
 *
 * @author Liuiie
 * @since 2025/5/27 16:28
 */
public class StringKeyCryptoUtils {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    // PBKDF2参数
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    private StringKeyCryptoUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * 将字符串密钥转为安全的AES密钥
     *
     * @param keyString 原始密钥字符串（建议长度>=32字符）
     * @param salt      盐值（需安全存储，建议16字节以上）
     * @return 符合AES-256规范的密钥
     *
     * <p>使用PBKDF2WithHmacSHA256进行密钥扩展，增强弱密钥安全性</p>
     */
    public static byte[] convertToAESKey(String keyString, byte[] salt) throws Exception {
        if (keyString == null || keyString.length() < 16) {
            throw new IllegalArgumentException("密钥字符串至少需要16个字符");
        }

        char[] keyChars = keyString.toCharArray();

        // 使用PBKDF2进行密钥派生
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(
                keyChars,
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * 加密方法（自动包含盐值和IV）
     *
     * @param keyString 原始密钥字符串
     * @param plaintext 明文数据
     * @return 加密后的字符串格式：Base64(盐值 + IV + 密文)
     */
    public static String encryptWithStringKey(String keyString, String plaintext) throws Exception {
        // 生成随机盐值（16字节）
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        // 生成实际加密密钥
        byte[] aesKey = convertToAESKey(keyString, salt);

        // 生成随机IV（12字节）
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);

        // 执行加密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // 组合盐值 + IV + 密文
        byte[] combined = new byte[salt.length + iv.length + ciphertext.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(iv, 0, combined, salt.length, iv.length);
        System.arraycopy(ciphertext, 0, combined, salt.length + iv.length, ciphertext.length);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
    }

    /**
     * 解密方法
     *
     * @param keyString 原始密钥字符串
     * @param encrypted 加密后的字符串
     * @return 解密后的明文
     */
    public static String decryptWithStringKey(String keyString, String encrypted) throws Exception {
        byte[] combined = Base64.getUrlDecoder().decode(encrypted);

        // 解析盐值（前16字节）
        if (combined.length < 16 + IV_LENGTH_BYTE + 1) {
            throw new IllegalArgumentException("无效的加密数据");
        }
        byte[] salt = new byte[16];
        System.arraycopy(combined, 0, salt, 0, 16);

        // 解析IV（接下来的12字节）
        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(combined, 16, iv, 0, IV_LENGTH_BYTE);

        // 解析实际密文
        int ciphertextLength = combined.length - 16 - IV_LENGTH_BYTE;
        byte[] ciphertext = new byte[ciphertextLength];
        System.arraycopy(combined, 16 + IV_LENGTH_BYTE, ciphertext, 0, ciphertextLength);

        // 生成实际加密密钥
        byte[] aesKey = convertToAESKey(keyString, salt);

        // 执行解密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
    }
}
