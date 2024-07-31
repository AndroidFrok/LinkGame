package swu.xl.linkgame.http;

import com.hjq.http.config.IRequestApi;

public class AdlogApi implements IRequestApi {
    @Override
    public String getApi() {
        return "/addons/shopro/user.user/user_ad_log";
    }

    public void setAd_slot_id(String ad_slot_id) {
        this.ad_slot_id = ad_slot_id;
    }

    private String ad_slot_id;
    /**
     * 由客户端生成的 用户token#ad_slot_id 并md5之后的字符串
     */
    private String user_ad_log_sn;

    public void setUserAdSn(String s) {
        user_ad_log_sn = s;
    }

}
