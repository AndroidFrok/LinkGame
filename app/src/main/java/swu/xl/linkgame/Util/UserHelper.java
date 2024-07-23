package swu.xl.linkgame.Util;


import swu.xl.linkgame.http.model.LoginResp;

public class UserHelper {

    static public void login(LoginResp.DataDTO user) {
        MmkvUtil.save(MmkvUtil.Token, user.getToken());
    }

    static public void logout() {
        MmkvUtil.save(MmkvUtil.Token, "");
    }
}
