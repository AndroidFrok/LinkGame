package swu.xl.linkgame.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kongzue.dialogx.dialogs.PopTip;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import swu.xl.linkgame.R;
import swu.xl.linkgame.SelfView.BrowserView;
import timber.log.Timber;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 浏览器界面
 */
public final class BrowserActivity extends Activity implements OnRefreshListener, Runnable {

    private static final String INTENT_KEY_IN_URL = "url";
    private String devOaid = "";
    private long loadTime;

    public static void start(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(INTENT_KEY_IN_URL, url);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private ProgressBar mProgressBar;
    private SmartRefreshLayout mRefreshLayout;
    private BrowserView mBrowserView;


    protected void initView() {

        mProgressBar = findViewById(R.id.pb_browser_progress);
        mRefreshLayout = findViewById(R.id.sl_browser_refresh);
        mBrowserView = findViewById(R.id.wv_browser_view);
//        mBrowserView.addJavascriptInterface(new JSHook(), "hook");


        // 设置网页刷新监听
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    protected void initData() {

        mBrowserView.setBrowserViewClient(new AppBrowserViewClient());
        mBrowserView.setBrowserChromeClient(new AppBrowserChromeClient(mBrowserView));
        String url = getIntent().getStringExtra(INTENT_KEY_IN_URL);
        Timber.d("uuu " + url);
        mBrowserView.loadUrl(url);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mBrowserView.canGoBack()) {
            // 后退网页并且拦截该事件
            mBrowserView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 是否禁止刷新
     */
    private boolean forbiddenRefresh;

    /**
     * 重新加载当前页
     */
    private void reload() {
        if (!forbiddenRefresh) {
            mBrowserView.reload();
        } else {
            PopTip.show("本页面不能刷新哟");
        }
    }

    /**
     * {@link OnRefreshListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        reload();
    }

    @Override
    public void run() {

    }


    private class AppBrowserViewClient extends BrowserView.BrowserViewClient {

        /**
         * 网页加载错误时回调，这个方法会在 onPageFinished 之前调用
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // 这里为什么要用延迟呢？因为加载出错之后会先调用 onReceivedError 再调用 onPageFinished

        }


        private void sendPic(File file) {

//                                ToastUtils.show("" + file.getPath());

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Timber.tag("拦截").d("拦截到的url：" + url);
            //url如果包含doScan参数值，就是h5和我们定义的传值协议
            if (url.contains("doScan")) {
//                mBrowserView.loadUrl("javascript:showPic(" + a + ");");
//                url = URLEncoder.encode(url);
                return true;
            } else if (url.startsWith("tel:")) {
                call(url.split(":")[1]);
            } else {
//                url = url.replace("?", "");
                view.loadUrl(url);
            }
            return true;
        }


        /**
         * 开始加载网页
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        /**
         * 完成加载网页
         */
        @Override
        public void onPageFinished(WebView view, String url) {
//            ToastUtils.show("加载完成" + url);
            mProgressBar.setVisibility(View.GONE);
            mRefreshLayout.finishRefresh();
            forbiddenRefresh = false;

        }
    }

    /**
     * 保存图片到sdcard
     *
     * @param bitmap
     */
    public static void savePicToSdcard(String path, Bitmap bitmap) {
        if (bitmap != null) {
            try {
                FileOutputStream out = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG, 10, out);
                out.flush();
                out.close();
                bitmap.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class AppBrowserChromeClient extends BrowserView.BrowserChromeClient {

        private AppBrowserChromeClient(BrowserView view) {
            super(view);
        }

        /**
         * 收到网页标题
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (title == null) {
                return;
            }
            setTitle("");
//            setTitle(title);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            if (icon == null) {
                return;
            }

        }

        /**
         * 收到加载进度变化
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TTAdManagerHolder.init(this);
        setContentView(R.layout.browser_activity);
        initView();
        initData();
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);

    }
    /*                下方 专门 设置广告  */
    /**
     *
     */
    private long startTime = 0;


    private String getUserData() {
        JSONArray jsonArray = new JSONArray();
        JSONObject object = new JSONObject();
        try {
            object.put("name", "auth_reward_gold");
            object.put("value", "3000");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        jsonArray.put(object);

        return jsonArray.toString();
    }


    /*                上方 专门 设置广告  */
    private void call(String phoneNum) {
        XXPermissions.with(this).permission(Permission.CALL_PHONE).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + phoneNum);
                intent.setData(data);
                startActivity(intent);
            }
        });
    }

    private final String js = "javascript:";

    /**
     * 通知web前端  广告看完了
     */
//    @SuppressLint("SetJavaScriptEnabled")
//    @SuppressLint("JavascriptInterface")
    public void watchComplete() {

        mBrowserView.post(new Runnable() {
            @Override
            public void run() {
                mBrowserView.loadUrl(js + "watchComplete();");
            }
        });
    }
}