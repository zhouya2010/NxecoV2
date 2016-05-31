package com.nxecoii.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nxecoii.R;
import com.nxecoii.activity.child.HistoryLogFragment;
import com.nxecoii.activity.child.HomeFragment;
import com.nxecoii.activity.child.ScheduleFragment;
import com.nxecoii.activity.child.SettingsFragment;
import com.nxecoii.activity.child.WateringFragment;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.greendao.WaterData;
import com.nxecoii.greendao.WaterDataDao;
import com.nxecoii.http.DeviceHttp;
import com.nxecoii.http.NetUtils;
import com.nxecoii.http.NxecoAPP;
import com.nxecoii.rabbitmq.QueueConsumer;
import com.nxecoii.rabbitmq.RabbitMqBase;
import com.nxecoii.schedule.ScheduleService;
import com.nxecoii.schedule.SprayService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

public class MainActivity extends Activity {

    private RadioGroup mRadioGroupMeun;
    private RadioButton wateringBtn, homeBtn, scheduleBtn, historyBtn,
            settingBtn;
    private TextView connect_state;
    private TextView sensor_state;
    private NetWorkStateReceiver connectionReceiver;
    private Timer timer;

    private HomeFragment homeFragment = null;
    private WateringFragment wateringFragment = null;
    private HistoryLogFragment historyFragment = null;
    private ScheduleFragment scheduleFragment = null;

    private SettingsFragment settingFragment = null;

    public static final String MSG_WEATHER = "com.nxecoii.http.GET_WEATHER";

    public static final String DOWNLOAD_URI = "http://www.rainupdate.com/upfile/xmc/touch.apk";
//    public static final String DOWNLOAD_URI = "http://www.rainupdate.com/upfile/xmc/q.apk";
    public static final String DOWNLOAD_TEST_URI = "http://dldir1.qq.com/music/clntupate/QQMusic.apk";
    public static final String FILE_NAME = "NxecoII.apk";
    public final String SAVE_DIRECTRY = Environment.getExternalStorageDirectory().getPath() + "/Download/";
    private static final String Msg_INSTALL = "com.example.nxecoii.autoinstall";
    public static final String MSG_Download = "com.nxecoii.http.DownloadSoftware";
    public static final String MSG_SENSOR = "com.example.nxecoii.sensor";

    private TextView download_info;
    private ImageView download_img;
    private AnimationDrawable animationDrawable;

    //    private DownloadManager downloadManager;
    private Handler handler;

    private Thread consumerThread;
    private WaterObserver waterObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        mRadioGroupMeun = (RadioGroup) findViewById(R.id.menu_group);
        wateringBtn = (RadioButton) findViewById(R.id.watering);
        homeBtn = (RadioButton) findViewById(R.id.home);
        scheduleBtn = (RadioButton) findViewById(R.id.schedule);
        historyBtn = (RadioButton) findViewById(R.id.history);
        settingBtn = (RadioButton) findViewById(R.id.setting);
        mRadioGroupMeun.setOnCheckedChangeListener(radiogpchange);
        homeBtn.setChecked(true);

        connect_state = (TextView) findViewById(R.id.wifi);
        sensor_state = (TextView) findViewById(R.id.sensor_img);

        download_info = (TextView) findViewById(R.id.download_info);
        download_img = (ImageView) findViewById(R.id.download_img);
        animationDrawable = (AnimationDrawable) download_img.getDrawable();

        startService(new Intent(this, SprayService.class));
        startService(new Intent(this, ScheduleService.class));


        waterObserver = new WaterObserver(new Handler());

        setDefaultFragment();

        getContentResolver().registerContentObserver(
                WaterData.URI_CONTENT_WATER, true, waterObserver);

        connectionReceiver = new NetWorkStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(MSG_WEATHER);
        intentFilter.addAction(MSG_Download);
        intentFilter.addAction(MSG_SENSOR);
        registerReceiver(connectionReceiver, intentFilter);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                connectCloud(MainActivity.this, DeviceBaseInfo.serialNumber);
            }

        }, 1000 * 60 * 4, 1000 * 60 * 4);

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                DeviceHttp.queryWeather();
            }

        }, 1000 * 60 * 60, 1000 * 60 * 60);

//        downloadManager = DownloadManager.getInstance(getApplicationContext());
    }

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        transaction.replace(R.id.inflate, homeFragment);
        transaction.commit();
    }

    private RadioGroup.OnCheckedChangeListener radiogpchange = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();

            if (checkedId == wateringBtn.getId()) {
                if (wateringFragment == null) {
                    wateringFragment = new WateringFragment();
                }
                transaction.replace(R.id.inflate, wateringFragment);
            } else if (checkedId == scheduleBtn.getId()) {
                if (scheduleFragment == null) {
                    scheduleFragment = new ScheduleFragment();
                }
                transaction.replace(R.id.inflate, scheduleFragment);
            } else if (checkedId == homeBtn.getId()) {
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                transaction.replace(R.id.inflate, homeFragment);
            } else if (checkedId == historyBtn.getId()) {
                if (historyFragment == null) {
                    historyFragment = new HistoryLogFragment();
                }
                transaction.replace(R.id.inflate, historyFragment);
//                TestFragment fragment = new TestFragment();
//                transaction.replace(R.id.inflate, fragment);

            } else if (checkedId == settingBtn.getId()) {
                if (settingFragment == null) {
                    settingFragment = new SettingsFragment();
                }
                transaction.replace(R.id.inflate, settingFragment);
            }
            transaction.commit();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();

        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }
        timer.cancel();

        stopService(new Intent(this, SprayService.class));
        stopService(new Intent(this, ScheduleService.class));
    }


    public class NetWorkStateReceiver extends BroadcastReceiver {

        private static final String TAG = "NetworkStateReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, intent.getAction());
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (!NetUtils.isNetworkAvailable(context)) {
                    Log.i(TAG, "network unconnected.");
                    connect_state.setBackgroundResource(R.drawable.wifi_off);
                } else {
                    Log.i(TAG, "network connected.");
                    connect_state.setBackgroundResource(R.drawable.wifi);
                    connectCloud(MainActivity.this, DeviceBaseInfo.serialNumber);
                }
            } else if (intent.getAction().equals(MSG_Download)) {
                downLoad();
            } else if (intent.getAction().equals(MSG_SENSOR)) {
                boolean sensor = intent.getBooleanExtra("sensor", false);
                Log.e(TAG, "sensor changed " + sensor);
                if (sensor) {
                    sensor_state.setVisibility(View.VISIBLE);
                    Log.d(TAG, "sensor changed true");
                } else {
                    sensor_state.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "sensor changed false");
                }
            }
        }

    }


    void downLoad() {
        RequestParams params = new RequestParams(DOWNLOAD_URI);
        params.setAutoResume(true);
        params.setAutoRename(true);
        params.setSaveFilePath(SAVE_DIRECTRY + FILE_NAME);
        params.setCancelFast(true);

        Callback.Cancelable cancelable
                = x.http().get(params,
                new Callback.ProgressCallback<File>() {

                    @Override
                    public void onSuccess(File result) {
                        Log.d("DownLoadActivity", "onSuccess");
                        download_info.setVisibility(View.INVISIBLE);
                        download_img.setVisibility(View.INVISIBLE);
                        animationDrawable.stop();
                        Intent intent = new Intent();
                        intent.setAction(Msg_INSTALL);
                        sendBroadcast(intent);
                        Toast.makeText(MainActivity.this, "Download Sucessful!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.d("DownLoadActivity", "onError");
                        download_img.setVisibility(View.INVISIBLE);
                        download_info.setText("Update error!");
                        animationDrawable.stop();
                        Toast.makeText(MainActivity.this, "Download error!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Log.d("DownLoadActivity", "onCancelled");
                    }

                    @Override
                    public void onFinished() {
                        Log.d("DownLoadActivity", "onFinished");
                    }

                    @Override
                    public void onWaiting() {
                        Log.d("DownLoadActivity", "onWaiting");
                    }

                    @Override
                    public void onStarted() {
                        Log.d("DownLoadActivity", "onStarted");
                        download_info.setVisibility(View.VISIBLE);
                        download_info.setText("0%");
                        download_img.setVisibility(View.VISIBLE);
                        animationDrawable.start();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isDownloading) {
//                      Log.d("DownLoadActivity", "onLoading" + " total " + total + " current " + current + "  isDownloading " + isDownloading);
                        download_info.setText((int) (current * 100 / total) + "% ");
                    }
                });
    }

    public void connectCloud(final Context context, String serialNumber) {

        String strParam = NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/conncloud?&dno=" + serialNumber;
        Log.d("MyIntentService", strParam);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MyIntentService", "response:" + response);
                        try {
                            String strError = response.getString("error");
                            if (strError.equals("200")) {
                                JSONObject data = response.getJSONObject("data");
                                if (data != null) {
                                    DeviceBaseInfo.connuid = data.getString("connuid");
//                                    RabbitMqBase.routingKey = data.getString("key");
                                    RabbitMqBase.routingKey = "ACCF235AAE4Epub";
                                    if (DeviceBaseInfo.deviceId == 0) {
                                        sentNo();
                                        DeviceHttp.sentDeviceType();
                                        DeviceHttp.queryWeather();
                                    }

                                    if (consumerThread == null) {
                                        connectMq();
                                    }

                                    if (DeviceBaseInfo.programInfoList.size() == 0) {
                                        getProgramInfo();
                                    }

                                    uploadWaterList();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Toast.makeText(context, "Connect cloud error!", Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                DeviceHttp.TimeoutMs,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        NxecoAPP.mRequestQueue.add(jsonObjectRequest);
    }

    void sentNo() {
        String strParam = NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/setno?&cno=" + DeviceBaseInfo.connuid
                + "&ver=" + NxecoAPP.getVersion() + "&dtype="
                + DeviceBaseInfo.deviceType;
        Log.d("MainActivity", strParam);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String strError = response.getString("error");
                            if (strError.equals("200") || strError.equals("1102")) {
                                JSONObject data = response.getJSONObject("data");
                                if (data != null) {
                                    DeviceBaseInfo.deviceId = data.getInt("devid");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });

        NxecoAPP.mRequestQueue.add(jsonObjectRequest);
    }

    private void connectMq() {
        System.out.println("connectMq");
        consumerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    QueueConsumer consumer = new QueueConsumer(
                            DeviceBaseInfo.serialNumber);
                    consumer.run();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
        consumerThread.start();
    }

    public class WaterObserver extends ContentObserver {

        public WaterObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            uploadWaterList();
        }
    }

    void uploadWaterList() {

        List<WaterData> waterDataList = new ArrayList<>();
        waterDataList.addAll(NxecoAPP.getDaoSession().getWaterDataDao().queryBuilder().where(WaterDataDao.Properties.IsUpload.eq(false)).list());
        if (waterDataList.size() > 0) {
            for (WaterData wd : waterDataList) {
                uploadWater(wd);
            }
        }
    }

    void uploadWater(final WaterData wd) {
        String strParam = NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/uploadwater?cno="
                + DeviceBaseInfo.connuid + "&zone=" + wd.getZone() + "&long="
                + wd.getIntervalActual() + "&type=" + wd.getSprayType() + "&fine="
                + wd.getAdjust() + "&begintime=" + wd.getStartTimeActually()
                + "&endtime=" + wd.getEndTimeActually() + "&stoptype=" + wd.getStopType()
                + "&obegintime=" + wd.getStartTimeTheory() + "&olong="
                + wd.getIntervalOrigin();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("SprayQueue", "response:" + response);
                        wd.setIsUpload(true);
                        NxecoAPP.getDaoSession().getWaterDataDao().update(wd);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Toast.makeText(MainActivity.this, "Sent water log error!", Toast.LENGTH_SHORT).show();
            }
        });
        NxecoAPP.mRequestQueue.add(jsonObjectRequest);
    }

    private void getProgramInfo() {
        String strParam = NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/getmode?cno=" + DeviceBaseInfo.connuid;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("data"));
                            JSONObject jsonData;
                            DeviceBaseInfo.programInfoList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonData = jsonArray.getJSONObject(i);
                                DeviceBaseInfo.ProgramInfo proInfo = new DeviceBaseInfo.ProgramInfo();
                                proInfo.id = jsonData.getInt("id");
                                proInfo.name = jsonData.getString("name");
                                DeviceBaseInfo.programInfoList.add(proInfo);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ScheduleFragment", error.getMessage(), error);
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                DeviceHttp.TimeoutMs,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        NxecoAPP.mRequestQueue.add(jsonObjectRequest);
    }

}
