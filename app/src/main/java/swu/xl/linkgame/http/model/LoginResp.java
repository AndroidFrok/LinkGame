package swu.xl.linkgame.http.model;

import com.google.gson.annotations.SerializedName;

public class LoginResp {

    /**
     * code
     */
    @SerializedName("code")
    private Integer code;
    /**
     * msg
     */
    @SerializedName("msg")
    private String msg;
    /**
     * time
     */
    @SerializedName("time")
    private String time;
    /**
     * data
     */
    @SerializedName("data")
    private DataDTO data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        /**
         * token
         */
        @SerializedName("token")
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
