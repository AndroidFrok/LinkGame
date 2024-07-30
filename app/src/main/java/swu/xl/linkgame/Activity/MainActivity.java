package swu.xl.linkgame.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdInteractionListener;
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.bytedance.sdk.openadsdk.mediation.init.MediationConfig;
import com.bytedance.sdk.openadsdk.mediation.init.MediationConfigUserInfoForSegment;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationAdEcpmInfo;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationBaseManager;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.zhangyue.we.x2c.X2C;
import com.zhangyue.we.x2c.ano.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swu.xl.linkgame.Constant.Constant;
import swu.xl.linkgame.Fragment.HelpFragment;
import swu.xl.linkgame.Fragment.SettingFragment;
import swu.xl.linkgame.Fragment.StoreFragment;
import swu.xl.linkgame.Model.XLLevel;
import swu.xl.linkgame.Model.XLProp;
import swu.xl.linkgame.Model.XLUser;
import swu.xl.linkgame.Music.BackgroundMusicManager;
import swu.xl.linkgame.Music.SoundPlayUtil;
import swu.xl.linkgame.R;
import swu.xl.linkgame.SelfView.XLTextView;
import swu.xl.linkgame.Util.AppConfig;
import swu.xl.linkgame.Util.MmkvUtil;
import swu.xl.linkgame.Util.PxUtil;
import swu.xl.linkgame.Util.TTAdManagerHolder;
import swu.xl.linkgame.Util.UserHelper;
import swu.xl.linkgame.http.AdlogApi;
import swu.xl.linkgame.http.model.HttpData;
import swu.xl.linkgame.http.model.RewardBundleModel;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private XLTextView main_title;
    //简单模式
    Button mode_easy;
    //普通模式
    Button mode_normal;
    //困难模式
    Button mode_hard;

    //设置按钮
    AppCompatImageView btn_setting;
    //帮助按钮
    AppCompatImageView btn_help;
    //商店按钮
    AppCompatImageView btn_store;

    //根布局
    RelativeLayout root_main;

    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Xml(layouts = "activity_main")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        X2C.setContentView(this, R.layout.activity_main);

        //提前加载资源，不然的话，资源没有加载好，会没有声音
        SoundPlayUtil.getInstance(this);

        //沉浸式状态栏
        ImmersionBar.with(this).init();

        //数据库 LitePal
        LitePal.initialize(this);
        SQLiteDatabase db = LitePal.getDatabase();

        //向数据库装入数据
        initSQLite3();

        //初始化数据
        initView();

        //设置模式按钮的drawableLeft
        setDrawableLeft(mode_easy, R.drawable.main_mode_easy);
        setDrawableLeft(mode_normal, R.drawable.main_mode_normal);
        setDrawableLeft(mode_hard, R.drawable.main_mode_hard);

        //播放音乐
        playMusic();

        //广播接受者
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action)) {
                    switch (action) {
                        case Intent.ACTION_SCREEN_OFF:
                            Log.d(Constant.TAG, "屏幕关闭，变黑");

                            if (BackgroundMusicManager.getInstance(getBaseContext()).isBackgroundMusicPlaying()) {
                                Log.d(Constant.TAG, "正在播放音乐，关闭");

                                //暂停播放
                                BackgroundMusicManager.getInstance(getBaseContext()).pauseBackgroundMusic();
                            }

                            break;
                        case Intent.ACTION_SCREEN_ON:
                            Log.d(Constant.TAG, "屏幕开启，变亮");
                            break;
                        case Intent.ACTION_USER_PRESENT:
                            Log.d(Constant.TAG, "解锁成功");
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
    }

    /**
     * 初始化数据库
     */
    private void initSQLite3() {
        //查找当前数据库的内容
        List<XLUser> users = LitePal.findAll(XLUser.class);
        List<XLLevel> levels = LitePal.findAll(XLLevel.class);
        List<XLProp> props = LitePal.findAll(XLProp.class);

        //如果用户数据为空，装入数据
        if (users.size() == 0) {
            XLUser user = new XLUser();
            user.setU_money(1000);
            user.setU_background(0);
            user.save();
        }

        //如果关卡数据为空，装入数据
        if (levels.size() == 0) {
            //简单模式
            for (int i = 1; i <= 40; i++) {
                XLLevel level = new XLLevel();
                //设置关卡号
                level.setL_id(i);
                //设置关卡模式
                level.setL_mode('1');
                //设置关卡的闯关状态
                if (i == 1) {
                    level.setL_new('4');
                } else {
                    level.setL_new('0');
                }
                //设置关卡的闯关时间
                level.setL_time(0);

                //插入
                level.save();
            }

            //普通模式
            for (int i = 1; i <= 40; i++) {
                XLLevel level = new XLLevel();
                //设置关卡号
                level.setL_id(i);
                //设置关卡模式
                level.setL_mode('2');
                //设置关卡的闯关状态
                if (i == 1) {
                    level.setL_new('4');
                } else {
                    level.setL_new('0');
                }
                //设置关卡的闯关时间
                level.setL_time(0);

                //插入
                level.save();
            }

            //困难模式
            for (int i = 1; i <= 40; i++) {
                XLLevel level = new XLLevel();
                //设置关卡号
                level.setL_id(i);
                //设置关卡模式
                level.setL_mode('3');
                //设置关卡的闯关状态
                if (i == 1) {
                    level.setL_new('4');
                } else {
                    level.setL_new('0');
                }
                //设置关卡的闯关时间
                level.setL_time(0);

                //插入
                level.save();
            }
        }

        //如果道具数据为空，装入数据
        if (props.size() == 0) {
            //1.装入拳头道具
            XLProp prop_fight = new XLProp();
            prop_fight.setP_kind('1');
            prop_fight.setP_number(9);
            prop_fight.setP_price(10);
            prop_fight.save();

            //2.装入炸弹道具
            XLProp prop_bomb = new XLProp();
            prop_bomb.setP_kind('2');
            prop_bomb.setP_number(9);
            prop_bomb.setP_price(10);
            prop_bomb.save();

            //3.装入刷新道具
            XLProp prop_refresh = new XLProp();
            prop_refresh.setP_kind('3');
            prop_refresh.setP_number(9);
            prop_refresh.setP_price(10);
            prop_refresh.save();
        }
    }

    /**
     * 数据的初始化
     */
    private void initView() {
        main_title = findViewById(R.id.main_title);
        mode_easy = findViewById(R.id.main_mode_easy);
        mode_easy.setOnClickListener(this);
        mode_normal = findViewById(R.id.main_mode_normal);
        mode_normal.setOnClickListener(this);
        mode_hard = findViewById(R.id.main_mode_hard);
        mode_hard.setOnClickListener(this);
        btn_setting = findViewById(R.id.main_setting);
        btn_setting.setOnClickListener(this);
        btn_help = findViewById(R.id.main_help);
        btn_help.setOnClickListener(this);
        btn_store = findViewById(R.id.main_store);
        btn_store.setOnClickListener(this);
        root_main = findViewById(R.id.root_main);

        main_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(MainActivity.this, AdminActivity.class));
                return false;
            }
        });
    }

    /**
     * 用给定资源设置指定按钮的drawableLeft
     */
    private void setDrawableLeft(Button btn, int main_mode_resource) {
        //获取指定的drawable
        Drawable drawable = getResources().getDrawable(main_mode_resource);
        //设置其drawable的左上右下
        drawable.setBounds(PxUtil.dpToPx(20, this), PxUtil.dpToPx(2, this), PxUtil.dpToPx(60, this), PxUtil.dpToPx(42, this));
        //设置放在控件的左上右下
        btn.setCompoundDrawables(drawable, null, null, null);
    }

    /**
     * 播放背景音乐
     */
    private void playMusic() {
        //判断是否正在播放
        if (!BackgroundMusicManager.getInstance(this).isBackgroundMusicPlaying()) {

            //播放
            BackgroundMusicManager.getInstance(this).playBackgroundMusic(R.raw.bg_music, true);
        }
    }

    @Override
    public void onClick(View v) {
        //播放点击音效
        SoundPlayUtil.getInstance(getBaseContext()).play(3);

        //fragment事务
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();

        //区分点击
        switch (v.getId()) {
            case R.id.main_mode_easy:
                Log.d(Constant.TAG, "简单模式按钮");

                //查询简单模式的数据
                List<XLLevel> XLLevels1 = LitePal.where("l_mode == ?", "1").find(XLLevel.class);
                Log.d(Constant.TAG, XLLevels1.size() + "");

                //依次查询每一个内容
                for (XLLevel xlLevel : XLLevels1) {
                    Log.d(Constant.TAG, xlLevel.toString());
                }

                //跳转界面
                Intent intent_easy = new Intent(this, LevelActivity.class);
                //加入数据
                Bundle bundle_easy = new Bundle();
                //加入关卡模式数据
                bundle_easy.putString("mode", "简单");
                //加入关卡数据
                bundle_easy.putParcelableArrayList("levels", (ArrayList<? extends Parcelable>) XLLevels1);
                intent_easy.putExtras(bundle_easy);
                //跳转
                startActivity(intent_easy);

                break;
            case R.id.main_mode_normal:
                Log.d(Constant.TAG, "普通模式按钮");

                //查询简单模式的数据
                List<XLLevel> XLLevels2 = LitePal.where("l_mode == ?", "2").find(XLLevel.class);
                Log.d(Constant.TAG, XLLevels2.size() + "");

                //依次查询每一个内容
                for (XLLevel xlLevel : XLLevels2) {
                    Log.d(Constant.TAG, xlLevel.toString());
                }

                //跳转界面
                Intent intent_normal = new Intent(this, LevelActivity.class);
                //加入数据
                Bundle bundle_normal = new Bundle();
                //加入关卡模式数据
                bundle_normal.putString("mode", "简单");
                //加入关卡数据
                bundle_normal.putParcelableArrayList("levels", (ArrayList<? extends Parcelable>) XLLevels2);
                intent_normal.putExtras(bundle_normal);
                //跳转
                startActivity(intent_normal);

                break;
            case R.id.main_mode_hard:
                Log.d(Constant.TAG, "困难模式按钮");

                //查询简单模式的数据
                List<XLLevel> XLLevels3 = LitePal.where("l_mode == ?", "3").find(XLLevel.class);
                Log.d(Constant.TAG, XLLevels3.size() + "");

                //依次查询每一个内容
                for (XLLevel xlLevel : XLLevels3) {
                    Log.d(Constant.TAG, xlLevel.toString());
                }

                //跳转界面
                Intent intent_hard = new Intent(this, LevelActivity.class);
                //加入数据
                Bundle bundle_hard = new Bundle();
                //加入关卡模式数据
                bundle_hard.putString("mode", "简单");
                //加入关卡数据
                bundle_hard.putParcelableArrayList("levels", (ArrayList<? extends Parcelable>) XLLevels3);
                intent_hard.putExtras(bundle_hard);
                //跳转
                startActivity(intent_hard);

                break;
            case R.id.main_setting:
                Log.d(Constant.TAG, "设置按钮");

                //添加一个fragment
                final SettingFragment setting = new SettingFragment();
                transaction.replace(R.id.root_main, setting, "setting");
                transaction.commit();

                break;
            case R.id.main_help:
                Log.d(Constant.TAG, "帮助按钮");

                //添加一个fragment
                final HelpFragment help = new HelpFragment();
                transaction.replace(R.id.root_main, help, "help");
                transaction.commit();

                break;
            case R.id.main_store:
                Log.d(Constant.TAG, "商店按钮");

                //添加一个fragment
                final StoreFragment store = new StoreFragment();
                transaction.replace(R.id.root_main, store, "store");
                transaction.commit();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserHelper.logout();
        BackgroundMusicManager.getInstance(this).end();
        unregisterReceiver(mBroadcastReceiver);
    }


    /*                下方 专门 设置广告  */
    /**
     *
     */
    private long startTime = 0;
    private TTAdNative mTTAdNative;

    private AdLoadListener mAdLoadListener;
    private long loadTime;
    private String token = "";

    private void initTTSDKConfig() {
        //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        // TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        //不使用穿山甲自带权限申请，不可控且申请过多

        XXPermissions.with(this).permission(Permission.READ_PHONE_STATE, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {

                loadAd();
            }

            @Override
            public void onDenied(List<String> permissions, boolean never) {
                OnPermissionCallback.super.onDenied(permissions, never);

            }
        });
    }

    /**
     * 加载广告   激励视频
     */
    private void loadAd() {
        if (mTTAdNative == null) {
            initTTSDKConfig();
        }
        //step5:创建广告请求参数AdSlot
        AdSlot adSlot = new AdSlot.Builder().setCodeId(Constant.mMediaId) // 广告代码位Id
                .setAdLoadType(TTAdLoadType.PRELOAD) // 本次广告用途：TTAdLoadType.LOAD实时；TTAdLoadType.PRELOAD预请求
                .setOrientation(TTAdConstant.ORIENTATION_VERTICAL).setRewardAmount(555).setUserData(getUserData()).setRewardName("金币").build();
        //step6:注册广告加载生命周期监听，请求广告
        mAdLoadListener = new AdLoadListener(this);
        mTTAdNative.loadRewardVideoAd(adSlot, mAdLoadListener);
    }

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

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return new Lifecycle() {
            @Override
            public void addObserver(@NonNull LifecycleObserver observer) {

            }

            @Override
            public void removeObserver(@NonNull LifecycleObserver observer) {

            }

            @NonNull
            @Override
            public State getCurrentState() {
                return null;
            }
        };
    }


    /**
     * 【必须】广告加载期间生命周期监听
     */

    private class AdLoadListener implements TTAdNative.RewardVideoAdListener {

        private final Activity mActivity;

        private TTRewardVideoAd mAd;

        private TTAdInteractionListener mInteractionListener = new TTAdInteractionListener() {
            @Override
            public void onAdEvent(int code, Map map) {

                if (code == TTAdConstant.AD_EVENT_AUTH_DOUYIN && map != null) {
                    // 抖音授权成功状态回调, 媒体可以通过map获取抖音openuid用以判断是否下发奖励
                    String uid = (String) map.get("open_uid");
//                    Timber.i( "授权成功 --> uid：" + uid);
                }
            }
        };

        public AdLoadListener(Activity activity) {
            mActivity = activity;
        }

        /**
         * 广告加载过程中出错
         */
        @Override
        public void onError(int code, String message) {
            Timber.e("Callback --> onError1: " + code + ", " + message);
            ToastUtils.show("onError " + message);
        }

        /**
         * 广告基础信息加载完成，此方法是回调后是广告可调用展示的最早时机
         *
         * @param ad 广告对象 在一次广告生命周期中onRewardVideoAdLoad与onRewardVideoCached回调中的ad是同一个对象
         */
        @Override

        public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
            Timber.e("Callback --> onRewardVideoAdLoad");
//            ToastUtils.show( "rewardVideoAd loaded 广告类型：" + getAdType(ad.getRewardVideoAdType()));
            handleAd(ad);
        }

        @Override

        public void onRewardVideoCached() {
            // 已废弃 请使用 onRewardVideoCached(TTRewardVideoAd ad) 方法
        }

        /**
         * 广告基础信息与素材缓存完成，此时调用广告展示流畅，是展示广告的最理想时机
         *
         * @param ad 广告对象 在一次广告生命周期中onRewardVideoAdLoad与onRewardVideoCached回调中的ad是同一个对象
         */
        @Override

        public void onRewardVideoCached(TTRewardVideoAd ad) {
            Timber.e("Callback --> onRewardVideoCached");
//            ToastUtils.show( "rewardVideoAd cached 广告类型：" + getAdType(ad.getRewardVideoAdType()));
            handleAd(ad);
        }

        /**
         * 处理广告对象
         */

        public void handleAd(TTRewardVideoAd ad) {
            if (mAd != null) {
                return;
            }
            mAd = ad;
            //【必须】广告展示时的生命周期监听

            mAd.setRewardAdInteractionListener(new AdLifeListener(mActivity));

            //【可选】再看一个展示时的生命状态监听

//            PlayAgainAdLifeListener playAgainAdLifeListener = new PlayAgainAdLifeListener(mActivity);
//            mAd.setRewardPlayAgainInteractionListener(playAgainAdLifeListener);
            //【可选】再看一个入口与奖励显示控制器

//            PlayAgainController playAgainController = new PlayAgainController();
//            playAgainController.setPlayAgainAdLifeListener(playAgainAdLifeListener);
//            mAd.setRewardPlayAgainController(playAgainController);
            //【可选】监听下载状态
//            mAd.setDownloadListener(new DownloadStatusListener());
            loadTime = System.currentTimeMillis();
            /**
             * 注册广告事件监听， 目前只有授权事件定义，后续会扩展
             */
            mAd.setAdInteractionListener(mInteractionListener);
        }

        /**
         * 触发展示广告
         */
        public void showAd() {
            if (mAd == null) {
                ToastUtils.show("当前广告未加载好，请先点击加载广告");
                loadAd();
                return;
            } else {
                mAd.showRewardVideoAd(mActivity);
            }
            // 广告使用后应废弃
            // mAd = null;
        }
    }

    /**
     * 【必须】广告生命状态监听器
     */

    private class AdLifeListener implements TTRewardVideoAd.RewardAdInteractionListener {

        private final WeakReference<Context> mContextRef;

        public AdLifeListener(Context context) {
            mContextRef = new WeakReference<>(context);
        }

        @Override

        public void onAdShow() {
            // 广告展示
//            Timber.d( "Callback --> rewardVideoAd show");
//            ToastUtils.show( "rewardVideoAd show");
            //为了确保数据的准确性，强烈建议在展示后获取展示广告的详细信息，包括广告位类型-getRitType、流量分组ID-getSegmentId、AB实验分组ID-getABTestId、渠道名称-getChannel、子渠道名称-getSubChannel、场景ID-getScenariold、价格-getEcpm、ADN平台-getSdkName
            if (mAdLoadListener == null || mAdLoadListener.mAd == null) return;
            adEcpmInfo(mAdLoadListener.mAd.getMediationManager());
        }

        @Override

        public void onAdVideoBarClick() {
            // 广告中产生了点击行为
//            Timber.d( "Callback --> rewardVideoAd bar click");
//            ToastUtils.show( "rewardVideoAd bar click");
        }

        @Override

        public void onAdClose() {
            // 广告整体关闭
            Timber.d("Callback --> rewardVideoAd close");
//            ToastUtils.show( "rewardVideoAd close");
//            watchComplete();
            reqADlog();

        }

        //视频播放完成回调
        @Override
        public void onVideoComplete() {
            // 广告素材播放完成，例如视频未跳过，完整的播放了
//            Timber.d( "Callback --> rewardVideoAd complete");
//            ToastUtils.show( "rewardVideoAd complete");
            reqADlog();
        }

        @Override
        public void onVideoError() {
            // 广告素材展示时出错
//            Timber.e( "Callback --> rewardVideoAd error");
            ToastUtils.show("rewardVideoAd error");
        }

        @Override
        public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
            // 已废弃 请使用 onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo)
        }

        @Override

        public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
            // 用户的观看行为满足了奖励条件
            RewardBundleModel rewardBundleModel = new RewardBundleModel(extraInfo);
            Timber.e("Callback --> rewardVideoAd has onRewardArrived " + "\n奖励是否有效：" + isRewardValid + "\n奖励类型：" + rewardType + "\n奖励名称：" + rewardBundleModel.getRewardName() + "\n奖励数量：" + rewardBundleModel.getRewardAmount() + "\n建议奖励百分比：" + rewardBundleModel.getRewardPropose());
//            ToastUtils.show( "ad onRewardArrived valid:" + isRewardValid + " type:" + rewardType + " errorCode:" + rewardBundleModel.getServerErrorCode());
            if (!isRewardValid) {
                Timber.d("发送奖励失败 code：" + rewardBundleModel.getServerErrorCode() + "\n msg：" + rewardBundleModel.getServerErrorMsg());
                return;
            }


            if (rewardType == TTRewardVideoAd.REWARD_TYPE_DEFAULT) {
                Timber.d("普通奖励发放，name:" + rewardBundleModel.getRewardName() + "\namount:" + rewardBundleModel.getRewardAmount() + "\n 建议比例" + rewardBundleModel.getRewardPropose());
            }
        }

        @Override
        public void onSkippedVideo() {
            // 用户在观看素材时点击了跳过
            Timber.e("Callback --> rewardVideoAd has onSkippedVideo");
            ToastUtils.show("rewardVideoAd has onSkippedVideo");
        }
    }


    /**
     * 获取广告的价格等信息
     */
    public void adEcpmInfo(MediationBaseManager mediationManager) {
        if (mediationManager == null) return;
        MediationAdEcpmInfo item = mediationManager.getShowEcpm();
        if (item == null) return;
        Timber.i("EcpmInfo: \n" + "adn名称 SdkName: " + item.getSdkName() + ",\n" + "自定义adn名称 CustomSdkName: " + item.getCustomSdkName() + ",\n" + "代码位Id SlotId: " + item.getSlotId() + ",\n" + "广告价格 Ecpm(单位：分): " + item.getEcpm() + ",\n" + "广告竞价类型 ReqBiddingType: " + item.getReqBiddingType() + ",\n" + "多阶底价标签 LevelTag: " + item.getLevelTag() + ",\n" + "多阶底价标签解析失败原因 ErrorMsg: " + item.getErrorMsg() + ",\n" + "adn请求Id RequestId: " + item.getRequestId() + ",\n" + "广告类型 RitType: " + item.getRitType() + ",\n" + "AB实验Id AbTestId: " + item.getAbTestId() + ",\n" + "场景Id ScenarioId: " + item.getScenarioId() + ",\n" + "流量分组Id SegmentId: " + item.getSegmentId() + ",\n" + "流量分组渠道 Channel: " + item.getChannel() + ",\n" + "流量分组子渠道 SubChannel: " + item.getSubChannel() + ",\n" + "开发者传入的自定义数据 customData: " + item.getCustomData());
    }

    private void initChuanshanjia() {
        TTAdSdk.init(this, new TTAdConfig.Builder().appId(Constant.csjId).useTextureView(true) //默认使用SurfaceView播放视频广告,当有SurfaceView冲突的场景，可以使用TextureView
                .appName(getResources().getString(R.string.app_name)).titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)//落地页主题
                .allowShowNotify(true) //是否允许sdk展示通知栏提示,若设置为false则会导致通知栏不显示下载进度
                .debug(AppConfig.isDebug()) //测试阶段打开，可以通过日志排查问题，上线时去除该调用 AppConfig.isDebug()
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI) //允许直接下载的网络状态集合,没有设置的网络下点击下载apk会有二次确认弹窗，弹窗中会披露应用信息
                .supportMultiProcess(false) //是否支持多进程，true支持
                .useMediation(true) //如果您需要设置隐私策略请参考该api
                .setMediationConfig(new MediationConfig.Builder() //可设置聚合特有参数详细设置请参考该api
                        .setMediationConfigUserInfoForSegment(getUserInfoForSegment())//如果您需要配置流量分组信息请参考该api
                        .build())
                //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
//                        .updateAdConfig(ttAdConfig)//参数类型为TTAdConfig；注意使用该方法会覆盖之前初始化sdk的配置的data值；个性化推荐设置详见：https://www.csjplatform.com/supportcenter/26234
                .build());
        //如果明确某个进程不会使用到广告SDK，可以只针对特定进程初始化广告SDK的content
        //if (PROCESS_NAME_XXXX.equals(processName)) {
        //   TTAdSdk.init(context, config);
        //}
        TTAdSdk.start(new TTAdSdk.Callback() {
            @Override
            public void success() {
                Timber.i("穿山甲初始化成功");
                //聚合初次会拉取配置文件可能导致很慢，Activity中广告请求可能早于了广告SDK初始化
//                EventBus.getDefault().postSticky(new AdInitSuccessEvent());
                initTTSDKConfig();
            }

            @Override
            public void fail(int i, String s) {
                Timber.w("穿山甲初始化失败了！");
            }
        });
    }

    private MediationConfigUserInfoForSegment getUserInfoForSegment() {
        MediationConfigUserInfoForSegment userInfo = new MediationConfigUserInfoForSegment();
//        userInfo.setUserId("msdk-demo");
        userInfo.setGender(MediationConfigUserInfoForSegment.GENDER_MALE);
        String devOaid = "ssmlf-xxl";
        userInfo.setChannel(devOaid);
        userInfo.setSubChannel("aabbccc");
        userInfo.setUserId("userid");
//        userInfo.setAge(999);
        userInfo.setUserValueGroup("msdk-demo-user-value-group");
        Map<String, String> customInfos = new HashMap<>();
//        customInfos.put("aaaa", "test111");
        customInfos.put("channel", "channelchannel");
        userInfo.setCustomInfos(customInfos);
        return userInfo;
    }

    /**
     * 广告播放结束 通知后端
     */
    private void reqADlog() {
        String source = MmkvUtil.getString(MmkvUtil.UserSource, "");
        if (source.equals("xmbs")) {
            return;
        }
        String slotId = Constant.mMediaId;
        String p = token + "#" + loadTime;
        Timber.d("拼接  " + p);
//        String pp = MD5Utils.getMD5Code(p);
        AdlogApi a = new AdlogApi();
        a.setAd_slot_id(slotId);
//        a.setUserAdSn(pp);
        EasyHttp.post(this).api(a).delay(AppConfig.getRandomDelay()).request(new OnHttpListener<HttpData>() {
            @Override
            public void onSucceed(HttpData result) {

            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

}
