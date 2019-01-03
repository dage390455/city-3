package com.sensoro.smartcity.util;

import android.text.TextUtils;

import java.util.regex.Pattern;

public class RegexUtils {


    /**
     * 校验电话号码
     *
     * @param phoneNum
     * @return
     */
    public static boolean checkPhone(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return false;
        }
        String regex;
        if (AppUtils.isChineseLanguage()) {
            regex = "^((\\+86){0,1}1[2|3|4|5|6|7|8|9](\\d){9}|(\\d{3,4}-){0,1}(\\d{7,8})(-\\d{1,4}){0,1})$";
        } else {
            regex = "^\\d+$";
        }
//        final String regex = "^(\\+86){0,1}1[3|4|5|6|7|8|9](\\d){9}$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(phoneNum).matches();
    }

    /**
     * 校验企业名称
     *
     * @param enterpriseName
     * @return
     */
    public static boolean checkEnterpriseName(String enterpriseName) {
        if (TextUtils.isEmpty(enterpriseName)) {
            return false;
        }
        final String regex = "^[A-Za-z0-9\\u4e00-\\u9fa5]{2,20}$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(enterpriseName).matches();
    }

    /**
     * 校验统一社会信用代码
     *
     * @param enterpriseCardID
     * @return
     */
    public static boolean checkEnterpriseCardID(String enterpriseCardID) {
        if (TextUtils.isEmpty(enterpriseCardID)) {
            return false;
        }
        final String regex = "[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}";
        Pattern p = Pattern.compile(regex);
        return p.matcher(enterpriseCardID).matches();
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        final String regex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        Pattern p = Pattern.compile(regex);
        return p.matcher(email).matches();
    }

    /**
     * 校验身份证
     *
     * @param userID
     * @return
     */
    public static boolean checkUserID(String userID) {
        if (TextUtils.isEmpty(userID)) {
            return false;
        }
        final String regex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
        Pattern p = Pattern.compile(regex);
        return p.matcher(userID).matches();
    }

    /**
     * 校验IP地址
     *
     * @param IPAddress
     * @return
     */
    public static boolean checkIPAddress(String IPAddress) {
        if (TextUtils.isEmpty(IPAddress)) {
            return false;
        }
        final String regex = "((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))";
        Pattern p = Pattern.compile(regex);
        return p.matcher(IPAddress).matches();
    }

    /**
     * 校验车牌号
     *
     * @param carId
     * @return
     */
    public static boolean checkCarID(String carId) {
        if (TextUtils.isEmpty(carId)) {
            return false;
        }
        final String regex = "^[\\u4e00-\\u9fa5]{1}[a-hj-zA-HJ-Z]{1}[a-hj-zA-HJ-Z_0-9]{4}[a-hj-zA-HJ-Z_0-9_\\u4e00" +
                "-\\u9fa5]$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(carId).matches();
    }

    /**
     * 校验企业注册号
     *
     * @param registerCode
     * @return
     */
    public static boolean checkRegisterCode(String registerCode) {
        if (TextUtils.isEmpty(registerCode)) {
            return false;
        }
        final String regex = "^[0-9\\u4e00-\\u9fa5]{0,7}[0-9]{6,13}[u4e00-\\u9fa5]{0,1}$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(registerCode).matches();
    }

    /**
     * 校验合同内容不为空
     *
     * @param content
     * @return
     */
    public static boolean checkContractNotEmpty(String content) {
        return !TextUtils.isEmpty(content) && !"无".equals(content);
    }

    /**
     * 不能包含字母和数字
     *
     * @param customName
     * @return
     */
    public static boolean checkContractName(String customName) {
        if (TextUtils.isEmpty(customName)) {
            return false;
        }
        final String regex = ".*[a-zA-z0-9].*";
        Pattern p = Pattern.compile(regex);
        return !p.matcher(customName).matches();
    }
}
