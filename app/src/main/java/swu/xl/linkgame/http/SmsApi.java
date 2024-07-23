package swu.xl.linkgame.http;

import com.hjq.http.config.IRequestApi;

public class SmsApi implements IRequestApi {
    @Override
    public String getApi() {
        return "/addons/shopro/index/send";
    }

    public void setAccount(String ad_slot_id) {
        this.mobile = ad_slot_id;
    }

    private String mobile;
    /**
     * 由客户端生成的 用户token#ad_slot_id 并md5之后的字符串
     */
    private String event;

    public static String EventMobileLogin = "mobilelogin";
    public static String EventReg = "register";
    public static String EventChangePwd = "changepwd";
    public static String EventChangeMobile = "changemobile";

    public void setEvent(String s) {
        event = s;
    }

}
