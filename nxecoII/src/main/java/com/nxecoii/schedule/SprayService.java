package com.nxecoii.schedule;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.greendao.SprayDetails;
import com.nxecoii.greendao.SprayDetailsDao;
import com.nxecoii.greendao.WaterData;
import com.nxecoii.http.NxecoAPP;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SprayService extends Service {

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.nxecoii.spray";
    public static final Uri URI_CONTENT_SPRAY = Uri.parse(SCHEME + AUTHORITY);
    public static final Uri URI_SPRAY_UPDATE_TIME = Uri.parse(SCHEME + AUTHORITY + "/"
            + "update_time");
    public static final Uri URI_SPRAY_LIST_CHANGE = Uri.parse(SCHEME + AUTHORITY
            + "/" + "list_change");
    public static final Uri URI_SPRAY_PAUSE = Uri.parse(SCHEME + AUTHORITY
            + "/" + "PAUSE");
    public static final Uri URI_SPRAY_RESTART = Uri.parse(SCHEME + AUTHORITY
            + "/" + "RESTART");

    private Timer timer;
    private SprayDetailsDao sprayDetailsDao;
    private List<SprayDetails> sprayList;
    private Observer observer;
    private static final String Tag = "SprayService";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SprayService", "onCreate");
        sprayList = new ArrayList<SprayDetails>();
        sprayDetailsDao = NxecoAPP.getDaoSession().getSprayDetailsDao();
        sprayList.addAll(sprayDetailsDao.loadAll());
        sprayStart();
        observer = new Observer(new Handler());
        getContentResolver().registerContentObserver(URI_CONTENT_SPRAY, true, observer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SprayService", "onDestroy");
        if (observer != null)
            getContentResolver().unregisterContentObserver(observer);
        if (timer != null)
            timer.cancel();
    }


    private void sprayStart() {
        Log.d("SprayService", "sprayList.size():" + sprayList.size());
        if (sprayList.size() > 0) {
            final SprayDetails sd = sprayList.get(0);
            if (sd.getIsSpraying() == null || sd.getIsSpraying()) {
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {


                        sd.intervlActualAdd(1);
                        if (sd.getRemainTime() > 0) {
                            sprayDetailsDao.insertOrReplace(sd);
                            getContentResolver().notifyChange(SprayService.URI_SPRAY_UPDATE_TIME, null);
//                            I2cComm.sprayOpen(sd.getZone());
                        }
                        else {
//                            I2cComm.sprayClose(sd.getZone());
                            saveWaterToDatabase(sd);
                            getContentResolver().notifyChange(WaterData.URI_CONTENT_WATER,null);
                            getContentResolver().notifyChange(SprayService.URI_SPRAY_LIST_CHANGE, null);
                        }

                    }
                }, 1000, 1000);

//                timer.schedule(new TimerTask() {
//                    public void run() {
//                        Log.d("SprayService", "TIME ARRIVE###");
//                        Log.d("SprayService", "sprayList.get(0).getId():" + sprayList.get(0).getId());
//
//                        saveWaterToDatabase(sprayList.get(0));
//
//                        Log.d("SprayService", "URI_SPRAY_LIST_CHANGE");
//                        getContentResolver().notifyChange(SprayService.URI_SPRAY_LIST_CHANGE, null);
//
//                    }
//                }, sprayList.get(0).getRemainTime() * 1000);

                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        Log.d("scheduleAtFixedRate 31", "sentQueueState");
                        sentQueueState();
                    }
                }, 1000 * 33, 1000 * 33);

            }
            else {
                Log.d("SprayService", "spray is paused!");
            }
        }
        else {
            Log.d("SprayService", "sprayList size is 0!");
        }
    }

    public class Observer extends ContentObserver {

        public Observer(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(SprayService.URI_SPRAY_LIST_CHANGE)) {
                if (timer != null) {
                    timer.cancel();
                    Log.d("SprayService", "timer.cancel()");
                }
                if (sprayList != null) {
                    sprayList.clear();
                    Log.d("SprayService", "sprayDetailsDao.count():" + sprayDetailsDao.count());
                    sprayList.addAll(sprayDetailsDao.loadAll());
                    if ((sprayList.size() > 0) && (sprayList.get(0).getRemainTime() <= 0)) {
                        sprayDetailsDao.deleteByKey(sprayList.get(0).getId());
                        sprayList.remove(0);
                    }
                    sprayStart();
                    Log.d("Observer", "sentQueueState");
                    sentQueueState();
                }
            }
            else if(uri.equals(URI_SPRAY_PAUSE)) {
                if (timer != null) {
                    timer.cancel();
                }
                Log.d("Observer", "set water log");
                if (sprayList.size() > 0) {
                    sprayList.get(0).setEndTimeActually(System.currentTimeMillis()/1000);
                    sprayList.get(0).setIntervalOrigin(sprayList.get(0).getRemainTime());
                    sprayList.get(0).setIntervalActual(0);
                    sprayList.get(0).setIsSpraying(false);
                    sprayDetailsDao.insertOrReplace(sprayList.get(0));
                }
            }
            else if (uri.equals(URI_SPRAY_RESTART)) {
                if (timer != null) {
                    timer.cancel();
                    Log.d("SprayService", "timer.cancel()");
                }
                sprayStart();
                Log.d("URI_SPRAY_RESTART", "sentQueueState");
                sentQueueState();
            }
        }
    }


    private void sentQueueState() {
        StringBuilder queueState = new StringBuilder();
        StringBuilder queueTime = new StringBuilder();
        StringBuilder queueZone = new StringBuilder();

        if(sprayList.size() > 0) {
            for(int i = 0; i < sprayList.size(); i++) {
                if(i == 0) {
                    queueState.append(sprayList.get(i).getZone()).append("-").append(sprayList.get(i).getRemainTime()).append(";");
                }
                else {
                    if(i == 1) {
                        queueZone.append(sprayList.get(i).getZone());
                        queueTime.append("-").append(sprayList.get(i).getIntervalOrigin());
                    }
                    else {
                        queueZone.append(",").append(sprayList.get(i).getZone());
                        queueTime.append(",").append(sprayList.get(i).getIntervalOrigin());
                    }
                }
            }

            queueState.append(queueZone.toString()).append(queueTime.toString());
        }
        else {
            queueState.append("0-0;");
        }

        sdevent(queueState.toString());
    }


    void sdevent( String envent) {

        String strParam = NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/sdevent?&cno=" + DeviceBaseInfo.connuid + "&parm=" + envent;

        Log.d("SprayQueueThread", strParam);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("SprayQueue", "response:" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        jsonObjectRequest.setTag(Tag);
        NxecoAPP.mRequestQueue.add(jsonObjectRequest);
    }

    public  static void saveWaterToDatabase(SprayDetails sd) {
        Log.d("SprayService", "save water to database!");
        WaterData wd = new WaterData();
        wd.setZone(sd.getZone());
        wd.setAdjust(sd.getAdjust());
        wd.setIntervalActual(sd.getIntervalActual());
        wd.setIntervalOrigin(sd.getIntervalOrigin());
        wd.setStartTimeActually(sd.getStartTimeActually());
        wd.setStartTimeTheory(sd.getStartTimeTheory());
        wd.setEndTimeActually(sd.getEndTimeActually());
        wd.setIsUpload(false);
        wd.setStopType(sd.getStopType());
        wd.setSprayType(sd.getSprayType());
        wd.setAdd_time(new Date());
        NxecoAPP.getDaoSession().getWaterDataDao().insertOrReplace(wd);
    }

}
