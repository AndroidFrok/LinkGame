package swu.xl.linkgame.http;

import com.hjq.http.config.IRequestApi;

import swu.xl.linkgame.Constant.Constant;

public class SmsRegApi implements IRequestApi {
    @Override
    public String getApi() {
        return "/addons/shopro/user.user/smsRegister";
    }

    public void setAccount(String ad_slot_id) {
        this.mobile = ad_slot_id;
    }

    private String mobile;

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 由客户端生成的 用户token#ad_slot_id 并md5之后的字符串
     */
    private String code, password;
    private String source = Constant.adSource;

    public void setPass(String s) {
        password = s;
    }

}
