package com.liuiie.demo.utils.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-GCM 对称加密工具类
 *
 * <p>提供基于AES-GCM算法的加密解密功能，特征：
 * <ul>
 *   <li>每次加密生成随机IV（初始化向量）</li>
 *   <li>IV自动前置到密文中，无需单独传输</li>
 *   <li>使用认证加密（AEAD），提供数据完整性和真实性验证</li>
 *   <li>采用URL安全的Base64编码，适合网络传输</li>
 * </ul>
 *
 * <p><b>安全要求：</b>
 * <ol>
 *   <li>必须使用256-bit（32字节）的AES密钥</li>
 *   <li>密钥必须通过安全渠道存储和传输（如KMS/Vault）</li>
 *   <li>必须启用HTTPS等安全传输协议</li>
 * </ol>
 *
 * @author Liuiie
 * @since 2025/5/27 16:30
 */
public class SecureCryptoUtils {

    private SecureCryptoUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * 加密算法配置（AES-GCM）
     *
     * <p>格式说明：算法/模式/填充方式<br>
     * GCM模式提供认证加密功能，不需要单独填充</p>
     */
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    /**
     * GCM认证标签长度（bits）
     *
     * <p>NIST SP 800-38D建议使用128-bit认证标签，提供强完整性保证</p>
     */
    private static final int TAG_LENGTH_BIT = 128;

    /**
     * 初始化向量（IV）长度（字节）
     *
     * <p>GCM标准推荐12字节（96位）IV，平衡安全性和性能</p>
     */
    private static final int IV_LENGTH_BYTE = 12;

    /**
     * AES-GCM加密方法
     *
     * @param secretKey 加密密钥，必须为256-bit（32字节）
     * @param plaintext 待加密的明文数据
     * @return 经过Base64Url编码的字符串，格式：IV(12字节) + 密文
     * @throws Exception 可能出现以下异常：
     *                   <ul>
     *                     <li>InvalidKeyException - 无效密钥长度/格式</li>
     *                     <li>IllegalArgumentException - 空明文或格式错误</li>
     *                     <li>IllegalBlockSizeException - 加密数据块异常</li>
     *                   </ul>
     *
     * <p><b>加密流程：</b>
     * <ol>
     *   <li>生成密码学安全的随机IV</li>
     *   <li>使用GCM模式初始化加密器</li>
     *   <li>加密明文数据</li>
     *   <li>将IV前置到密文中</li>
     *   <li>进行URL安全的Base64编码</li>
     * </ol>
     */
    public static String encrypt(byte[] secretKey, String plaintext) throws Exception {
        // 前置条件检查
        validateKey(secretKey);
        if (plaintext == null || plaintext.isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be empty");
        }

        // --- 生成随机IV ---
        // 使用密码学安全的随机数生成器（CSPRNG）
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        // --- 初始化加密器 ---
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

        // --- 执行加密 ---
        // AES-GCM会自动添加认证标签（Authentication Tag）
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // --- 合并IV和密文 ---
        // 结构：IV(12字节) + 实际密文（包含认证标签）
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

        // --- Base64编码 ---
        // 使用URL安全编码（替换+/为-_），并且省略填充字符=
        return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
    }

    /**
     * AES-GCM解密方法
     *
     * @param secretKey 解密密钥，必须与加密密钥相同
     * @param encodedCombined Base64Url编码的字符串（IV+密文）
     * @return 解密后的原始明文
     * @throws Exception 可能出现以下异常：
     *                   <ul>
     *                     <li>AEADBadTagException - 认证标签验证失败（数据被篡改）</li>
     *                     <li>IllegalArgumentException - 无效输入格式</li>
     *                     <li>IllegalBlockSizeException - 解密数据块异常</li>
     *                   </ul>
     *
     * <p><b>解密流程：</b>
     * <ol>
     *   <li>Base64解码获取字节数组</li>
     *   <li>分离IV和密文数据</li>
     *   <li>初始化解密器</li>
     *   <li>执行解密并验证认证标签</li>
     * </ol>
     */
    public static String decrypt(byte[] secretKey, String encodedCombined) throws Exception {
        // 前置条件检查
        validateKey(secretKey);
        if (encodedCombined == null || encodedCombined.isEmpty()) {
            throw new IllegalArgumentException("Encrypted data cannot be empty");
        }

        // --- Base64解码 ---
        byte[] combined = Base64.getUrlDecoder().decode(encodedCombined);

        // --- 分离IV和密文 ---
        // 输入长度校验：至少包含IV + 1字节密文 + 16字节认证标签
        if (combined.length < IV_LENGTH_BYTE + 1 + TAG_LENGTH_BIT/8) {
            throw new IllegalArgumentException("Invalid encrypted data length");
        }

        byte[] iv = new byte[IV_LENGTH_BYTE];
        byte[] ciphertext = new byte[combined.length - IV_LENGTH_BYTE];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, ciphertext, 0, ciphertext.length);

        // --- 初始化解密器 ---
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

        // --- 执行解密 ---
        // 自动验证认证标签，如果失败会抛出AEADBadTagException
        byte[] plaintextBytes = cipher.doFinal(ciphertext);
        return new String(plaintextBytes, StandardCharsets.UTF_8);
    }

    /**
     * 验证AES密钥有效性
     *
     * @param key 待验证的密钥字节数组
     * @throws IllegalArgumentException 如果密钥不符合要求
     */
    private static void validateKey(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("AES密钥不能为null");
        }
        // 检查密钥长度是否为256-bit（32字节）
        if (key.length != 32) {
            throw new IllegalArgumentException(
                    "无效的AES密钥长度。预期256位（32个字节），实际: " + key.length * 8 + "-bit"
            );
        }
    }

    /**
     * <b>安全注意事项：</b>
     * <ol>
     *   <li>密钥管理：密钥必须通过安全方式存储（如密钥管理系统），禁止硬编码在代码中</li>
     *   <li>时效性：建议加密数据中包含时间戳并验证有效期，防止重放攻击</li>
     *   <li>错误处理：避免将详细的错误信息返回给客户端（如密钥长度错误）</li>
     *   <li>协议安全：必须配合HTTPS使用，防止中间人攻击</li>
     * </ol>
     */
}
