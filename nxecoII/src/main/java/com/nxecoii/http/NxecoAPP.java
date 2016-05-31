package com.nxecoii.http;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.NetworkInfo.State;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.greendao.DaoMaster;
import com.nxecoii.greendao.DaoSession;
import com.nxecoii.greendao.MySQLiteOpenHelper;


import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class NxecoAPP extends Application {
    /**
     * DemoMode是演示模式的标志位，true为演示模式，false为真实数据
     */

    public static boolean DemoMode = false;
    private static NxecoAPP instance;
    private static List<Activity> mActivityList = new LinkedList<Activity>();
    private static List<String> moplog_content = new ArrayList<String>();

    // 当前设备的wifi状态
    public static int device_wifiState;
    // 当前网络wifi状态
    public static State network_wifiState;
    // 访问的服务器地址
    public static String mstrMainHostAddr = "http://45.33.46.130";
    public static String mstrURL = "/api/rest/newclient";

    public static String sharedPreferencesNmae = "NxecoII_SharedPreferences";

    public static boolean mCurrIsSubAccount = false;

    private static Context context;

    public static RequestQueue mRequestQueue;

    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;


    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null)
            instance = this;
        context = getApplicationContext();
        mRequestQueue = Volley.newRequestQueue(context);
        DeviceBaseInfo.serialNumber = DeviceUtils.getAndroidId();
        x.Ext.init(this);
        x.Ext.setDebug(false); // 是否输出debug日志
    }

    public static Context getContext() {
        return context;
    }

    public static NxecoAPP getInstance() {
        return instance;
    }

    public static boolean getCurrIsSub() {
        return mCurrIsSubAccount;
    }

    public static void setCurrIsSub(boolean bIsSub) {
        mCurrIsSubAccount = bIsSub;
    }


    public static DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, "NxecoII.db", null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public static DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    /**
     * 获取当前的Activity
     */
    public static Activity getFocusActivity() {
        Activity actFocus = null;
        int iCount = mActivityList.size();
        if (iCount > 0)
            actFocus = mActivityList.get(iCount - 1);

        return actFocus;
    }

    public static void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    /**
     * 获取屏幕尺寸的宽度
     */
    public static int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = NxecoAPP.getInstance().getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕尺寸的高度
     */
    public static int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = NxecoAPP.getInstance().getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取屏幕的密度
     */
    public static int getScreenDensity() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = NxecoAPP.getInstance().getApplicationContext().getResources().getDisplayMetrics();
        int scrDensity = dm.densityDpi;
        return scrDensity;
    }

    /**
     * 程序全局调用本函数，查询演示模式，演示模式时，程序调用演示数据进行展示
     */
    public static boolean isDemoMode() {
        return DemoMode;
    }

    /**
     * 用于修改演示模式的标志位
     */
    public static void setDemoMode(boolean bln) {
        DemoMode = bln;
    }

    // 一次Http访问timeout时间，毫秒
    public static int getHttpConnWaitTime() {
        return 20000;
    }

    // 一次Http访问timeout时间，毫秒
    public static int getHttpBackWaitTime() {
        return 10000;
    }

    // 这组函数用于WiFi状态广播，暂未使用
    public static int getDevice_wifiState() {
        return device_wifiState;
    }

    public static void setDevice_wifiState(int device_wifiState) {
        NxecoAPP.device_wifiState = device_wifiState;
    }

    public static State getNetwork_wifiState() {
        return network_wifiState;
    }

    public static void setNetwork_wifiState(State network_wifiState) {
        NxecoAPP.network_wifiState = network_wifiState;
    }

    public static String suffixString(final String str, final int maxLength) {

        if (str == null) {

            return str;

        }

        String suffix = "...";

        int suffixLen = suffix.length();

        final StringBuffer sbuffer = new StringBuffer();

        final char[] chr = str.trim().toCharArray();

        int len = 0;

        for (int i = 0; i < chr.length; i++) {

            if (chr[i] >= 0xa1) {

                len += 2;

            } else {

                len++;

            }

        }

        if (len <= maxLength) {

            return str;
        }

        len = 0;

        for (char aChr : chr) {

            if (aChr >= 0xa1) {

                len += 2;

                if (len + suffixLen > maxLength) {

                    break;

                } else {

                    sbuffer.append(aChr);

                }

            } else {

                len++;

                if (len + suffixLen > maxLength) {

                    break;

                } else {

                    sbuffer.append(aChr);

                }

            }

        }

        sbuffer.append(suffix);

        return sbuffer.toString();
    }

    /**
     * 退出整个应用程序时，关闭activity
     */
    public static void exit() {
        try {
            for (Activity activity : mActivityList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    /**
     * 通用Toast提示
     */
    public static void ShowToast(String strMsg) {
        Activity currAct = getFocusActivity();
        if (currAct != null && strMsg != null && strMsg.length() > 0)
            Toast.makeText(currAct.getApplicationContext(), strMsg, Toast.LENGTH_LONG).show();
    }

    /**
     * 按钮点击频率的控制
     */
    public static class FrequencyControl {
        private static long lastClickTime;

        public static boolean isFastDoubleClick() {
            long time = System.currentTimeMillis();
            long timeD = time - lastClickTime;
            if (0 < timeD && timeD < 3000) { // 500毫秒内按钮无效，这样可以控制快速点击，自己调整频率
                return true;
            }
            lastClickTime = time;
            return false;
        }
    }


    public static String getCurrentTime(String template) {
        SimpleDateFormat sdf = new SimpleDateFormat(template, Locale.ENGLISH);
        String currentTime = sdf.format(new Date());
        return currentTime;
    }

    public static boolean isProessRunning(Context context, String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
            }
        }

        return isRunning;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "Not get version";
        }
    }
}