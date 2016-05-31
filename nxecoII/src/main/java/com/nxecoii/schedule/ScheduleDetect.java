package com.nxecoii.schedule;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.greendao.ScheduleData;
import com.nxecoii.greendao.ScheduleDataDao;
import com.nxecoii.greendao.SprayDetails;
import com.nxecoii.greendao.SprayDetailsDao;
import com.nxecoii.http.NxecoAPP;
import com.nxecoii.i2c.I2cComm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleDetect extends ContentObserver {

    private ArrayList<ScheduleData> schList;

    private int programIdTemp = DeviceBaseInfo.currentGroupId;

    private Handler mHandler = new Handler();
    private Receiver receiver = new Receiver();
    private Context context;
    private ScheduleDataDao scheduleDataDao;
    private SprayDetailsDao sprayDetailsDao;

    public ScheduleDetect(Context context, Handler handler) {
        // TODO Auto-generated constructor stub
        super(handler);
        this.context = context;

        scheduleDataDao = NxecoAPP.getDaoSession().getScheduleDataDao();
        sprayDetailsDao = NxecoAPP.getDaoSession().getSprayDetailsDao();

        schList = new ArrayList<ScheduleData>();
        schList.addAll(scheduleDataDao.loadAll());
    }

    public void start() {
        mHandler.post(receiver);
    }

    public void close() {
        mHandler.removeCallbacks(receiver);
    }

    private class Receiver implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (programIdTemp != DeviceBaseInfo.currentGroupId) {
                readScheduleList();
                programIdTemp = DeviceBaseInfo.currentGroupId;
            }
            match_sch_time();
//			I2cComm.getExBoardState();
            I2cComm.detect();
            mHandler.postDelayed(receiver, 1000);
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
                    context.getContentResolver().notifyChange(SprayService.URI_SPRAY_LIST_CHANGE, null);
                    Log.i("sch", "time get! zone:" + sch.getZone() + " start!******");
                }
            }
        }
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.d("ScheduleDetect", "onChange");
        readScheduleList();
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
