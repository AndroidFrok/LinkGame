package swu.xl.linkgame.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;
import com.hjq.http.EasyConfig;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.kongzue.dialogx.dialogs.PopTip;

import swu.xl.linkgame.R;
import swu.xl.linkgame.Util.MmkvUtil;
import swu.xl.linkgame.Util.UserHelper;
import swu.xl.linkgame.http.LoginApi;
import swu.xl.linkgame.http.MobLoginApi;
import swu.xl.linkgame.http.SmsApi;
import swu.xl.linkgame.http.model.LoginResp;

public class LoginAct extends BaseActivity {
    private MaterialTextView tv_sms, tv_account; // 用于切换短信或密码登录视图
    private MaterialCheckBox check_agree;
    private MaterialTextView tv_reg, tv_user;
    private ConstraintLayout root_pass, root_sms;
    private AppCompatEditText et_account, et_password, et_password1;
    private MaterialButton btn_login;
    private MaterialTextView code_btn;
    private boolean isSmsLogin;

    /*@Override
    protected int getLayoutId() {
        return R.layout.layout_login;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        initView();
    }

    //    @Override
    protected void initView() {
        UserHelper.logout();
        tv_sms = findViewById(R.id.tv_sms);
        check_agree = findViewById(R.id.check_agree);
        tv_account = findViewById(R.id.tv_account);
        root_pass = findViewById(R.id.root_pass);
        root_sms = findViewById(R.id.root_sms);
        tv_reg = findViewById(R.id.tv_reg);
        et_password = findViewById(R.id.et_password);
        et_account = findViewById(R.id.et_account);
        et_password1 = findViewById(R.id.et_password1);
        btn_login = findViewById(R.id.btn_login);
        code_btn = findViewById(R.id.code_btn);
        tv_user = findViewById(R.id.tv_user);
        tv_user.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                startActivity(ToolsActivity.class);
                return false;
            }
        });
        tv_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginAct.this, RegAct.class));
                finish();
            }
        });
        tv_sms.setOnClickListener(v -> {
            isSmsLogin = true;
            root_pass.setVisibility(View.GONE);
            root_sms.setVisibility(View.VISIBLE);
        });
        tv_account.setOnClickListener(v -> {
            isSmsLogin = false;
            root_pass.setVisibility(View.VISIBLE);
            root_sms.setVisibility(View.GONE);
        });
        btn_login.setOnClickListener(v -> {
            if (isSmsLogin) {
                reqSmsLogin();
            } else {
                reqLogin();
            }
        });
        code_btn.setOnClickListener(v -> {
            reqSms();
        });
    }

    private void reqSmsLogin() {
        if (!check_agree.isChecked()) {
            PopTip.show("请在阅读隐私协议和服务协议后勾选同意");
            return;
        }
        String account = et_account.getText().toString().trim();
        String pass = et_password1.getText().toString().trim();
        if (account.equals("") || pass.equals("")) {
            PopTip.show("请填写完整");
            return;
        }
        MobLoginApi l = new MobLoginApi();
        l.setAccount(account);
        l.setPass(pass);
        EasyHttp.post(this).api(l).request(new OnHttpListener<LoginResp>() {

            @Override
            public void onSucceed(LoginResp result) {
                PopTip.show(result.getMsg());
//                Timber.d(result.getCode() + "");
                if (result.getCode() == 1) {
                    UserHelper.login(result.getData()) ;
                    EasyConfig.getInstance().addHeader(MmkvUtil.Token, MmkvUtil.getString(MmkvUtil.Token, ""));
                    startActivity(new Intent(LoginAct.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    private void reqSms() {
        String account = et_account.getText().toString().trim();
        if (account.equals("")) {
            PopTip.show("请填写完整");
            return;
        }
        SmsApi l = new SmsApi();
        l.setAccount(account);
        l.setEvent(SmsApi.EventMobileLogin);
        EasyHttp.post(this).api(l).request(new OnHttpListener<LoginResp>() {

            @Override
            public void onSucceed(LoginResp result) {
                PopTip.show(result.getMsg());
//                Timber.d(result.getCode() + "");
                if (result.getCode() == 1) {
                    initCheck();
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    private void reqLogin() {
        if (!check_agree.isChecked()) {
            PopTip.show("请在阅读隐私协议和服务协议后勾选同意");
            return;
        }
        String account = et_account.getText().toString().trim();
        String pass = et_password.getText().toString().trim();
        if (account.equals("") || pass.equals("")) {
            PopTip.show("请填写完整");
            return;
        }
        LoginApi l = new LoginApi();
        l.setAccount(account);
        l.setPass(pass);
        EasyHttp.post(this).api(l).request(new OnHttpListener<LoginResp>() {

            @Override
            public void onSucceed(LoginResp result) {
                PopTip.show(result.getMsg());
//                Timber.d(result.getCode() + "");
                if (result.getCode() == 1) {
                    UserHelper.login(result.getData());
                    EasyConfig.getInstance().addHeader(MmkvUtil.Token, MmkvUtil.getString(MmkvUtil.Token, ""));
                    startActivity(new Intent(LoginAct.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }


    // 短信间隔时间
    private final int smsSeconds = 60;
    private int smsSecondsCount = smsSeconds;
    private final int oneSeconds = 1000;
    private CountDownTimer countDownTimer;

    private void initCheck() {

        if (countDownTimer == null) {
            // 倒计时
            countDownTimer = new CountDownTimer(smsSeconds * oneSeconds, oneSeconds) {
                @Override
                public void onTick(long millisUntilFinished) {
                    smsSecondsCount--;
                    code_btn.setEnabled(false);
                    code_btn.setText(smsSecondsCount + " s");
                }

                @Override
                public void onFinish() {
                    // 倒计时结束时的回调
                    smsSecondsCount = smsSeconds;
                    code_btn.setEnabled(true);
                    code_btn.setText("重新获取");
                    countDownTimer.cancel();
                }
            };

        }
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
