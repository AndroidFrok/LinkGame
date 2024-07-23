package swu.xl.linkgame.Activity;

import android.app.Application;

import androidx.core.content.ContextCompat;

import com.hjq.http.EasyConfig;
import com.hjq.http.config.RequestServer;
import com.hjq.toast.ToastUtils;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.style.MIUIStyle;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.mmkv.MMKV;

import org.litepal.LitePalApplication;

import okhttp3.OkHttpClient;
import swu.xl.linkgame.R;
import swu.xl.linkgame.Util.AppConfig;
import swu.xl.linkgame.Util.DebugLoggerTree;
import swu.xl.linkgame.Util.SmartBallPulseFooter;
import swu.xl.linkgame.Util.ToastStyle;
import swu.xl.linkgame.http.RequestHandler;
import timber.log.Timber;

public class MyApp extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        initSdk(this);
    }

    private boolean isDebug = true;


    public void initSdk(Application application) {
        DialogX.init(application);
        DialogX.globalStyle = new MIUIStyle();
        // 设置标题栏初始化器
//        TitleBar.setDefaultStyle(new TitleBarStyle());
        // MMKV 初始化
        MMKV.initialize(application);
        // 设置全局的 Header 构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((cx, layout) -> new MaterialHeader(application).setColorSchemeColors(ContextCompat.getColor(application, R.color.colorAccent)));
        // 设置全局的 Footer 构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((cx, layout) -> new SmartBallPulseFooter(application));
        // 设置全局初始化器
        SmartRefreshLayout.setDefaultRefreshInitializer((cx, layout) -> {
            // 刷新头部是否跟随内容偏移
            layout.setEnableHeaderTranslationContent(true)
                    // 刷新尾部是否跟随内容偏移
                    .setEnableFooterTranslationContent(true)
                    // 加载更多是否跟随内容偏移
                    .setEnableFooterFollowWhenNoMoreData(true)
                    // 内容不满一页时是否可以上拉加载更多
                    .setEnableLoadMoreWhenContentNotFull(false)
                    // 仿苹果越界效果开关
                    .setEnableOverScrollDrag(false);
        });

        // 初始化吐司
        ToastUtils.init(application, new ToastStyle());
        // 设置调试模式
        ToastUtils.setDebugMode(isDebug);
        // 设置 Toast 拦截器
//        ToastUtils.setInterceptor(new ToastLogInterceptor());

        // 本地异常捕捉
//        CrashHandler.register(application);

        // Activity 栈管理初始化
//        ActivityManager.getInstance().init(application);


        // 网络请求框架初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        EasyConfig.with(okHttpClient)
                // 是否打印日志
                .setServer(new RequestServer(AppConfig.getHostUrl())).setLogEnabled(AppConfig.isLogEnable())
                // 设置服务器配置
                // 设置请求处理策略
                .setHandler(new RequestHandler(application))
                .addHeader("source", "sxqy")
                // 设置请求重试次数
                /*.setRetryCount(1).setInterceptor((api, params, headers) -> {
                    // 添加全局请求头
                    headers.put("token", MmkvUtil.getString(MmkvUtil.Token, ""));
                    headers.put("deviceOaid", UmengClient.getDeviceOaid());
                    headers.put("versionName", AppConfig.getVersionName());
                    headers.put("platform", "App");
                    headers.put("source", "xmbs");
                    headers.put("phone", "" + Build.BRAND + "-" + Build.MODEL + "-" + Build.PRODUCT + "-" + Build.BOARD + "-"
                            + Build.DEVICE + "-Android" + Build.VERSION.RELEASE + "-API" + Build.VERSION.SDK_INT);
                    headers.put("versionCode", String.valueOf(AppConfig.getVersionCode()));
                    // 添加全局请求参数
                    // params.put("6666666", "6666666");
                })*/

                .into();


        // 初始化日志打印
        if (isDebug) {
            Timber.plant(new DebugLoggerTree());// 报错不影响
        }
        // 注册网络状态变化监听
        /*ConnectivityManager connectivityManager = getSystemService(getApplicationContext(), ConnectivityManager.class);
        if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLost(@NonNull Network network) {
                    Activity topActivity = ActivityManager.getInstance().getTopActivity();
                    if (!(topActivity instanceof LifecycleOwner)) {
                        return;
                    }

                    LifecycleOwner lifecycleOwner = ((LifecycleOwner) topActivity);
                    if (lifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.RESUMED) {
                        return;
                    }

                    ToastUtils.show(R.string.common_network_error);
                }
            });
        }*/
    }
}
