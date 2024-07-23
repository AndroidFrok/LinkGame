package swu.xl.linkgame.http;

import com.hjq.http.config.IRequestApi;

public class LoginApi implements IRequestApi {
    @Override
    public String getApi() {
        return "/addons/shopro/user.user/accountLogin";
    }

    public void setAccount(String ad_slot_id) {
        this.account = ad_slot_id;
    }

    private String account;
    /**
     * 由客户端生成的 用户token#ad_slot_id 并md5之后的字符串
     */
    private String password;

    public void setPass(String s) {
        password = s;
    }

}
