package com.liuiie.demo.utils.common;

/**
 * PwdCheckUtil
 *
 * @author Liuiie
 * @since 2022/11/24 17:22
 */
public class PwdCheckUtil {
    /**
     * 数字
     */
    public static final String REG_NUMBER = ".*\\d+.*";

    /**
     * 小写字母
     */
    public static final String REG_UPPERCASE = ".*[A-Z]+.*";

    /**
     * 大写字母
     */
    public static final String REG_LOWERCASE = ".*[a-z]+.*";

    /**
     * 特殊符号
     */
    public static final String REG_SYMBOL = ".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*";

    /**
     * 固定密码长度最短为8，最长为20，必须含有大小写字母、数字、特殊字符中两种以上组合
     *
     * @param password 密码
     * @return 是否通过验证
     */
    public static boolean pwdCheck(String password) {
        return pwdCheck(password, 8, 20, 2);
    }

    /**
     * 长度至少minLength位,且最大长度不超过maxlength,须包含大小写字母,数字,特殊字符matchCount种以上组合
     *
     * @param password   输入的密码
     * @param minLength  最小长度
     * @param maxLength  最大长度
     * @param matchCount 满足条件个数
     * @return 是否通过验证
     */
    public static boolean pwdCheck(String password, int minLength, int maxLength, int matchCount) {
        // 密码为空或者长度小于8位则返回false
        if (password == null || password.length() < minLength || password.length() > maxLength) {
            return false;
        }
        int i = 0;
        if (password.matches(REG_NUMBER)) {
            i++;
        }
        if (password.matches(REG_LOWERCASE)) {
            i++;
        }
        if (password.matches(REG_UPPERCASE)) {
            i++;
        }
        if (password.matches(REG_SYMBOL)) {
            i++;
        }
        return i >= matchCount;
    }
}
