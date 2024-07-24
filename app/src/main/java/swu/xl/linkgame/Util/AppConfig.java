package swu.xl.linkgame.Util;


import java.security.SecureRandom;

import swu.xl.linkgame.BuildConfig;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/09/02
 * desc   : App 配置管理类
 */
public final class AppConfig {

    /**
     * 当前是否为调试模式
     */
    public static boolean isDebug() {
        Boolean isUserDebug = MmkvUtil.getBool(MmkvUtil.DeveloperOpenDebug);
        if (!isUserDebug) {
            return true;
        }
        return true;
    }

    /**
     * 获取当前构建的模式
     */
    public static String getBuildType() {
        return "";
    }

    /**
     * 当前是否要开启日志打印功能
     */
    public static boolean isLogEnable() {
        return isDebug();
    }

    /**
     * 获取当前应用的包名
     */
    public static String getPackageName() {
        String s = "com.ssmlf_xmbs0717.juhui2024"; //com.sanshumiliangfang0419.juhui2024
        s = BuildConfig.APPLICATION_ID;
        return s;
    }

    /**
     * 获取当前应用的版本名
     */
    public static String getVersionName() {
        return "20240715";
    }

    /**
     * 获取当前应用的版本码
     */
    public static int getVersionCode() {
        return 14;
    }

    /**
     * 获取 Bugly Id
     */
    public static String getBuglyId() {
        return "687a7e6a11";
    }

    /**
     * 获取服务器主机地址
     */
    public static String getHostUrl() {
//        return "http://ssmlf.customer.jhwangluo.com"; //测试域名   不适用  ssl
        return "https://app.sanshumiliangfang.com";
    }

    public static String UserProtocol = AppConfig.getHostUrl() + "/uniapp/#/pages/public/richtext?id=1";
    public static String PrivacyProtocol = AppConfig.getHostUrl() + "/uniapp/#/pages/public/richtext?id=2";

    static public Long getRandomDelay() {
        if (!isDebug()) {
            return 0L;
        }
        SecureRandom se = new SecureRandom();
        int s = se.nextInt(5000) + 500;
        return 0L;
//        return Long.getLong(s + "");
    }
}