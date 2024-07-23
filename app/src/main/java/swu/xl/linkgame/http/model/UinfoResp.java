package swu.xl.linkgame.http.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UinfoResp {
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
         * id
         */
        @SerializedName("id")
        private Integer id;
        /**
         * groupId
         */
        @SerializedName("group_id")
        private Integer groupId;
        /**
         * username
         */
        @SerializedName("username")
        private String username;
        /**
         * nickname
         */
        @SerializedName("nickname")
        private String nickname;
        /**
         * email
         */
        @SerializedName("email")
        private String email;
        /**
         * mobile
         */
        @SerializedName("mobile")
        private String mobile;
        /**
         * avatar
         */
        @SerializedName("avatar")
        private String avatar;
        /**
         * level
         */
        @SerializedName("level")
        private Integer level;
        /**
         * gender
         */
        @SerializedName("gender")
        private Integer gender;
        /**
         * birthday
         */
        @SerializedName("birthday")
        private Object birthday;
        /**
         * bio
         */
        @SerializedName("bio")
        private String bio;
        /**
         * money
         */
        @SerializedName("money")
        private String money;
        /**
         * commission
         */
        @SerializedName("commission")
        private String commission;
        /**
         * score
         */
        @SerializedName("score")
        private Integer score;
        /**
         * successions
         */
        @SerializedName("successions")
        private Integer successions;
        /**
         * maxsuccessions
         */
        @SerializedName("maxsuccessions")
        private Integer maxsuccessions;
        /**
         * prevtime
         */
        @SerializedName("prevtime")
        private String prevtime;
        /**
         * logintime
         */
        @SerializedName("logintime")
        private String logintime;
        /**
         * loginip
         */
        @SerializedName("loginip")
        private String loginip;
        /**
         * loginfailure
         */
        @SerializedName("loginfailure")
        private Integer loginfailure;
        /**
         * joinip
         */
        @SerializedName("joinip")
        private String joinip;
        /**
         * jointime
         */
        @SerializedName("jointime")
        private String jointime;
        /**
         * token
         */
        @SerializedName("token")
        private String token;
        /**
         * status
         */
        @SerializedName("status")
        private String status;
        /**
         * verification
         */
        @SerializedName("verification")
        private VerificationDTO verification;
        /**
         * parentUserId
         */
        @SerializedName("parent_user_id")
        private Object parentUserId;
        /**
         * totalConsume
         */
        @SerializedName("total_consume")
        private String totalConsume;
        /**
         * type
         */
        @SerializedName("type")
        private Integer type;
        /**
         * inviteCode
         */
        @SerializedName("invite_code")
        private Object inviteCode;
        /**
         * picPath
         */
        @SerializedName("pic_path")
        private Object picPath;
        /**
         * source
         */
        @SerializedName("source")
        private String source;
        /**
         * actCommission
         */
        @SerializedName("act_commission")
        private String actCommission;
        /**
         * parentUser
         */
        @SerializedName("parent_user")
        private Object parentUser;
        /**
         * thirdOauth
         */
        @SerializedName("third_oauth")
        private List<?> thirdOauth;
        /**
         * statusText
         */
        @SerializedName("status_text")
        private String statusText;
        /**
         * genderText
         */
        @SerializedName("gender_text")
        private String genderText;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getGroupId() {
            return groupId;
        }

        public void setGroupId(Integer groupId) {
            this.groupId = groupId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public Integer getGender() {
            return gender;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        public Object getBirthday() {
            return birthday;
        }

        public void setBirthday(Object birthday) {
            this.birthday = birthday;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getCommission() {
            return commission;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public Integer getSuccessions() {
            return successions;
        }

        public void setSuccessions(Integer successions) {
            this.successions = successions;
        }

        public Integer getMaxsuccessions() {
            return maxsuccessions;
        }

        public void setMaxsuccessions(Integer maxsuccessions) {
            this.maxsuccessions = maxsuccessions;
        }

        public String getPrevtime() {
            return prevtime;
        }

        public void setPrevtime(String prevtime) {
            this.prevtime = prevtime;
        }

        public String getLogintime() {
            return logintime;
        }

        public void setLogintime(String logintime) {
            this.logintime = logintime;
        }

        public String getLoginip() {
            return loginip;
        }

        public void setLoginip(String loginip) {
            this.loginip = loginip;
        }

        public Integer getLoginfailure() {
            return loginfailure;
        }

        public void setLoginfailure(Integer loginfailure) {
            this.loginfailure = loginfailure;
        }

        public String getJoinip() {
            return joinip;
        }

        public void setJoinip(String joinip) {
            this.joinip = joinip;
        }

        public String getJointime() {
            return jointime;
        }

        public void setJointime(String jointime) {
            this.jointime = jointime;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public VerificationDTO getVerification() {
            return verification;
        }

        public void setVerification(VerificationDTO verification) {
            this.verification = verification;
        }

        public Object getParentUserId() {
            return parentUserId;
        }

        public void setParentUserId(Object parentUserId) {
            this.parentUserId = parentUserId;
        }

        public String getTotalConsume() {
            return totalConsume;
        }

        public void setTotalConsume(String totalConsume) {
            this.totalConsume = totalConsume;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Object getInviteCode() {
            return inviteCode;
        }

        public void setInviteCode(Object inviteCode) {
            this.inviteCode = inviteCode;
        }

        public Object getPicPath() {
            return picPath;
        }

        public void setPicPath(Object picPath) {
            this.picPath = picPath;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getActCommission() {
            return actCommission;
        }

        public void setActCommission(String actCommission) {
            this.actCommission = actCommission;
        }

        public Object getParentUser() {
            return parentUser;
        }

        public void setParentUser(Object parentUser) {
            this.parentUser = parentUser;
        }

        public List<?> getThirdOauth() {
            return thirdOauth;
        }

        public void setThirdOauth(List<?> thirdOauth) {
            this.thirdOauth = thirdOauth;
        }

        public String getStatusText() {
            return statusText;
        }

        public void setStatusText(String statusText) {
            this.statusText = statusText;
        }

        public String getGenderText() {
            return genderText;
        }

        public void setGenderText(String genderText) {
            this.genderText = genderText;
        }

        public static class VerificationDTO {
            /**
             * username
             */
            @SerializedName("username")
            private Integer username;
            /**
             * password
             */
            @SerializedName("password")
            private Integer password;
            /**
             * mobile
             */
            @SerializedName("mobile")
            private Integer mobile;

            public Integer getUsername() {
                return username;
            }

            public void setUsername(Integer username) {
                this.username = username;
            }

            public Integer getPassword() {
                return password;
            }

            public void setPassword(Integer password) {
                this.password = password;
            }

            public Integer getMobile() {
                return mobile;
            }

            public void setMobile(Integer mobile) {
                this.mobile = mobile;
            }
        }
    }
}
