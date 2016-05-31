package com.nxecoii.schedule;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nxecoii.activity.child.ScheduleFragment;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.greendao.ScheduleData;
import com.nxecoii.greendao.ScheduleDataDao;
import com.nxecoii.greendao.SprayDetails;
import com.nxecoii.greendao.SprayDetailsDao;
import com.nxecoii.http.NxecoAPP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/3/24.
 */
public class ScheduleService extends Service {

    private ArrayList<ScheduleData> schList;

    private int programIdTemp = DeviceBaseInfo.currentGroupId;
    private Handler mHandler;
    private Context context;
    private ScheduleDataDao scheduleDataDao;
    private SprayDetailsDao sprayDetailsDao;
    private SchObserver schObserver;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ScheduleService", "onCreate()");
        scheduleDataDao = NxecoAPP.getDaoSession().getScheduleDataDao();
        sprayDetailsDao = NxecoAPP.getDaoSession().getSprayDetailsDao();
        schList = new ArrayList<ScheduleData>();
        schList.addAll(scheduleDataDao.loadAll());
        schObserver = new SchObserver(new Handler());
        getContentResolver().registerContentObserver(ScheduleFragment.URI_CONTENT_SCHEDULE, true, schObserver);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (programIdTemp != DeviceBaseInfo.currentGroupId) {
                    readScheduleList();
                    programIdTemp = DeviceBaseInfo.currentGroupId;
                }
                match_sch_time();
//			    I2cComm.getExBoardState();
//                I2cComm.detect();
            }
        }, 1000, 1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (schObserver != null) {
            getContentResolver().unregisterContentObserver(schObserver);
        }

        if (timer != null) {
            timer.cancel();
        }

    }

    public class SchObserver extends ContentObserver {

        public SchObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            readScheduleList();
        }
    }

    private void match_sch_time() {
        int size = schList.size();
        ScheduleData sch;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        String currentTime = sdf.format(new Date());
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        DaysOfWeek dow = new DaysOfWeek(0);
        for (int i = 0; i < size; i++) {
            sch = schList.get(i);
            dow.set(sch.getRepeat());
            if (dow.isToday(day)) {
                if (currentTime.equals(""
                        + schList.get(i).getStart_time())) {

                    SprayDetails sd = new SprayDetails();
                    sd.setZone(sch.getZone());
                    sd.setStartTimeTheory(System.currentTimeMillis() / 1000);
                    if (DeviceBaseInfo.weather.size() > 0) {
                        sd.setAdjust(DeviceBaseInfo.weather.get(0).adjust);
                    } else {
                        sd.setAdjust(100);
                    }

                    sd.setIntervalOrigin(sch.getInterval() * sd.getAdjust() / 100);
//					sd.setIntervalOrigin(sch.getInterval());
                    sd.setIntervalActual(0);
                    sd.setIsSpraying(true);
                    sd.setSprayType(SprayDetails.sprayTypeSchedule);
                    sd.setIsUpload(false);
                    sd.setAdd_time(new Date());

                    sprayDetailsDao.insertOrReplace(sd);
                    Log.d("ScheduleDetect", "URI_SPRAY_LIST_CHANGE");
                    getContentResolver().notifyChange(SprayService.URI_SPRAY_LIST_CHANGE, null);
                    Log.i("sch", "time get! zone:" + sch.getZone() + " start!******");
                }
            }
        }
    }

    void readScheduleList() {
        if (schList == null) {
            schList = new ArrayList<ScheduleData>();
        } else {
            schList.clear();
        }

        schList.addAll(NxecoAPP.getDaoSession().getScheduleDataDao().
                queryBuilder().where(ScheduleDataDao.Properties.GroupId.eq(DeviceBaseInfo.currentGroupId)).list());

    }
}
