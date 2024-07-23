package swu.xl.linkgame.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;
import com.hjq.http.EasyConfig;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.kongzue.dialogx.dialogs.PopTip;

import swu.xl.linkgame.R;
import swu.xl.linkgame.Util.MmkvUtil;
import swu.xl.linkgame.http.SmsApi;
import swu.xl.linkgame.http.SmsRegApi;
import swu.xl.linkgame.http.model.LoginResp;

public class RegAct extends BaseActivity {
    private MaterialCheckBox check_agree;
    private MaterialTextView tv_login;
    private MaterialTextView code_btn;
    private AppCompatEditText et_password1, et_password;
    private AppCompatEditText et_tel;
    private MaterialButton btn_reg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_reg);
    }

   /* @Override
    protected int getLayoutId() {
        return R.layout.layout_reg;
    }*/


    protected void initView() {
        tv_login = findViewById(R.id.tv_login);
        check_agree = findViewById(R.id.check_agree);
        et_tel = findViewById(R.id.et_tel);
        code_btn = findViewById(R.id.code_btn);
        et_password = findViewById(R.id.et_password);
        et_password1 = findViewById(R.id.et_password1);
        btn_reg = findViewById(R.id.btn_reg);
        tv_login.setOnClickListener(v -> {
            startActivity(new Intent(RegAct.this, LoginAct.class));
            finish();
        });
        code_btn.setOnClickListener(v -> {
            reqSms();
        });
        btn_reg.setOnClickListener(v -> {
            reqSmsReg();
        });
    }

    private void reqSms() {
        String account = et_tel.getText().toString().trim();
        if (account.equals("")) {
            PopTip.show("请填写完整");
            return;
        }
        SmsApi l = new SmsApi();
        l.setAccount(account);
        l.setEvent(SmsApi.EventReg);
        EasyHttp.post(this).api(l).request(new OnHttpListener<LoginResp>() {

            @Override
            public void onSucceed(LoginResp result) {
                PopTip.show(result.getMsg());
                if (result.getCode() == 1) {
                    initCheck();
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

    private void reqSmsReg() {
        if (!check_agree.isChecked()) {
            PopTip.show("请在阅读隐私协议和服务协议后勾选同意");
            return;
        }
        String account = et_tel.getText().toString().trim();
        String code = et_password1.getText().toString().trim();
        String pass = et_password.getText().toString().trim();
        if (account.equals("") || pass.equals("")) {
            PopTip.show("请填写完整");
            return;
        }
        SmsRegApi l = new SmsRegApi();
        l.setAccount(account);
        l.setPass(pass);
        l.setCode(code);
        EasyHttp.post(this).api(l).request(new OnHttpListener<LoginResp>() {

            @Override
            public void onSucceed(LoginResp result) {
                PopTip.show(result.getMsg());
//                Timber.d(result.getCode() + "");
                if (result.getCode() == 1) {
//                    UserHelper.login(result.getData()) ;
                    EasyConfig.getInstance().addHeader(MmkvUtil.Token, MmkvUtil.getString(MmkvUtil.Token, ""));
                    startActivity(new Intent(RegAct.this, LoginAct.class));
                    finish();
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}
