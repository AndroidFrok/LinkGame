package swu.xl.linkgame.http;

import com.hjq.http.config.IRequestApi;

public class MobLoginApi implements IRequestApi {
    @Override
    public String getApi() {
        return "/addons/shopro/user.user/smsLogin";
    }

    public void setAccount(String ad_slot_id) {
        this.mobile = ad_slot_id;
    }

    private String mobile;
    /**
     * 由客户端生成的 用户token#ad_slot_id 并md5之后的字符串
     */
    private String code;

    public void setPass(String s) {
        code = s;
    }

}
