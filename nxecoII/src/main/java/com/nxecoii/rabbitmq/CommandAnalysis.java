package com.nxecoii.rabbitmq;

import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nxecoii.activity.MainActivity;
import com.nxecoii.activity.child.ScheduleFragment;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.greendao.ScheduleData;
import com.nxecoii.greendao.ScheduleDataDao;
import com.nxecoii.greendao.SprayDetails;
import com.nxecoii.greendao.SprayDetailsDao;
import com.nxecoii.greendao.WaterData;
import com.nxecoii.http.DeviceHttp;
import com.nxecoii.http.NxecoAPP;
import com.nxecoii.schedule.DaysOfWeek;
import com.nxecoii.schedule.SprayService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;

public class CommandAnalysis {
    public void commandAnalysis(String message) {

        String strError = "";
        String strData = "";

        try {
            JSONObject allData = new JSONObject(message);
            strError = allData.getString("error");
            Log.e("CommandAnalysis", strError);
            if (strError.equals("200")) {
                strData = allData.getString("data");
                Log.e("CommandAnalysis", strData);
                JSONArray jsonArray = new JSONArray(strData);
                JSONObject jsonData = jsonArray.getJSONObject(0);
                String command = jsonData.getString("command_name");

                String param = jsonData.getString("param");
                String cid = jsonData.getString("cid");

                Log.e("commandAnalysis", command);

                if (command.equals("RainOnce")) {
                    getRainOnceData(param);
                } else if (command.equals("RainClose")) {
                    getRainCloseData(param);
                } else if (command.equals("SetSchedule")) {
                    getSetScheduleData(param);
                } else if (command.equals("DeleteSchedule")) {
                    getDeleteSchData(param);
                } else if (command.equals("UpdateSchedule")) {
                    getUpdateScheduleData(param);
                } else if (command.equals("UpdateParam")) {
                    getUpdateParam(param);
                } else if (command.equals("UpdateFine")) {
                    System.out.println("UpdateFine");
                    DeviceHttp.queryWeather();
                } else if (command.equals("UpdateZipCode")) {
                    System.out.println("UpdateZipCode");
                } else if (command.equals("Upgrade")) {
                    apkDwonload();
                } else if (command.equals("SetMaster")) {
                    System.out.println("SetMaster");
                } else {
                    Log.e("commandAnalysis", "command unkown: " + command);
                }

                commandReturn(cid);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void commandReturn(String cid) {
        String strParam = NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/back?&cno=" + DeviceBaseInfo.connuid + "&cid=" + cid;
        Log.d("CommandAnalysis", strParam);

        StringRequest request = new StringRequest(strParam, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("CommandAnalysis", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        NxecoAPP.mRequestQueue.add(request);
    }

    private void getUpdateScheduleData(String param) {
        getDeleteSchData(param);
        getSetScheduleData(param);
    }

    private void getDeleteSchData(String param) {
        String[] params = param.split(";");

        int grop_id = 0;
        int zone = 0;
        int size = DeviceBaseInfo.programInfoList.size();

        for (String p : params) {
            String[] str = p.split(":");
            if (str[0].equals("num")) {
                zone = Integer.parseInt(str[1]);
            } else if (str[0].equals("modeid")) {
                int modeId = Integer.parseInt(str[1]);
                for (int i = 0; i < size; i++) {
                    if (DeviceBaseInfo.programInfoList.get(i).id == modeId) {
                        grop_id = i;
                    }
                }
            }
        }

        DeleteQuery dq = NxecoAPP.getDaoSession().getScheduleDataDao().queryBuilder()
                .where(ScheduleDataDao.Properties.GroupId.eq(grop_id),
                        ScheduleDataDao.Properties.Zone.eq(zone))
                .buildDelete();
        dq.executeDeleteWithoutDetachingEntities();

        NxecoAPP.getContext().getContentResolver().notifyChange(ScheduleFragment.URI_CONTENT_SCHEDULE, null);
    }

    private void getSetScheduleData(String param) {
        try {
            ScheduleData schData = new ScheduleData();
            String[] params = param.split(";");

//        for (String p: params) {
//            Log.d("CommandAnalysis", p);
//            String[] str = p.split(":");
//            if (str[0].equals("num")) {
//                schData.setZone(Integer.parseInt(str[1]));
//            }
//            else if (str[0].equals("weeks")) {
//
//            }
//            else if (str[0].equals("times")) {
//
//            }
//            else if (str[0].equals("howlong")) {
//
//            }
//            else if (str[0].equals("tag")) {
//
//            }
//            else if (str[0].equals("modeid")) {
//
//            }
//        }
            String whichValve[] = params[1].split(":");
            String weeks[] = params[2].split(":");
            String times[] = params[3].split(":");
            String howlong[] = params[4].split(":");
            String strTag[] = params[5].split(":");
            String strModeId[] = params[6].split(":");

            String strWeeks[] = weeks[1].split(",");
            String strTimes[] = times[1].replace("-", ":").split(",");
            String strHowLong[] = howlong[1].split(",");


            schData.setZone(Integer.parseInt(whichValve[1]));
            DaysOfWeek dow = new DaysOfWeek(0);
            int j;
            for (int i = 0; i < strWeeks.length; i++) {
                j = Integer.parseInt(strWeeks[i]);
                if (j == 0) {
                    dow.set(6, true);
                } else {
                    dow.set(j - 1, true);
                }
            }
            schData.setRepeat(dow.getCoded());
            schData.setIsUpload(true);
            int modeId = Integer.parseInt(strModeId[1]);
            int size = DeviceBaseInfo.programInfoList.size();
            for (int i = 0; i < size; i++) {
                if (DeviceBaseInfo.programInfoList.get(i).id == modeId) {
                    schData.setGroupId(i);
                }
            }
            //delete database schedule
            DeleteQuery dq = NxecoAPP.getDaoSession().getScheduleDataDao().queryBuilder()
                    .where(ScheduleDataDao.Properties.GroupId.eq(schData.getGroupId()),
                            ScheduleDataDao.Properties.Zone.eq(schData.getZone()))
                    .buildDelete();
            dq.executeDeleteWithoutDetachingEntities();

            schData.setAdd_time(new Date());
            //add new schedule
            for (int i = 0; i < strTimes.length; i++) {
                schData.setStart_time(strTimes[i] + ":00");
                schData.setInterval(Integer.parseInt(strHowLong[i]) * 60);
                schData.setId(null);

                ScheduleData sd = new ScheduleData();
                sd.setZone(schData.getZone());
                sd.setIsUpload(true);
                sd.setAdd_time(schData.getAdd_time());
                sd.setInterval(schData.getInterval());
                sd.setStart_time(schData.getStart_time());
                sd.setLable(schData.getLable());
                sd.setAdd_time(schData.getAdd_time());
                sd.setTimeId(schData.getTimeId());
                sd.setGroupId(schData.getGroupId());
                sd.setEnable(schData.getEnable());
                sd.setRepeat(schData.getRepeat());
                NxecoAPP.getDaoSession().getScheduleDataDao().insertOrReplace(sd);
            }

            NxecoAPP.getContext().getContentResolver().notifyChange(ScheduleFragment.URI_CONTENT_SCHEDULE, null);
        } catch (Exception e) {
            Log.e("CommandAnalysis", "getSetScheduleData command format error  " + e);
        }
    }

    private void getRainCloseData(String param) {

        String[] params = param.split(";");
        String[] valves = null;
        for (String p : params) {
            String[] str = p.split(":");
            if (str[0].equals("whichvalve")) {
                valves = str[1].split(",");
            }
        }

        if (valves != null) {
            for (String valve : valves) {
                List<SprayDetails> sprayList = new ArrayList<SprayDetails>();
                sprayList.addAll(
                        NxecoAPP.getDaoSession().getSprayDetailsDao().
                                queryBuilder().where(SprayDetailsDao.Properties.Zone.eq(Integer.parseInt(valve)))
                                .list());
                for (SprayDetails sd : sprayList) {
                    SprayService.saveWaterToDatabase(sd);
                }

                DeleteQuery dq = NxecoAPP.getDaoSession().getSprayDetailsDao().
                        queryBuilder().where(SprayDetailsDao.Properties.Zone.eq(Integer.parseInt(valve)))
                        .buildDelete();
                dq.executeDeleteWithoutDetachingEntities();
            }
            NxecoAPP.getContext().getContentResolver().notifyChange(WaterData.URI_CONTENT_WATER, null);
            NxecoAPP.getContext().getContentResolver().notifyChange(SprayService.URI_SPRAY_LIST_CHANGE, null);
        }
    }

    private void getRainOnceData(String param) {

        String[] params = param.split(";");
        String whichValve[] = params[2].split(":");
        String valves[] = whichValve[1].split(",");
        String strHowlong[] = params[3].split(":");
        String honglongs[] = strHowlong[1].split(",");
//        int time = Integer.parseInt(honglongs[0]) * 60;

        try {

            for (int i = 0; i < valves.length; i++) {

                SprayDetails sd = new SprayDetails();

                sd.setZone(Integer.parseInt(valves[i]));
                sd.setIntervalOrigin(Integer.parseInt(honglongs[i]) * 60);
                sd.setStartTimeTheory(System.currentTimeMillis() / 1000);
                sd.setAdjust(100);
                sd.setSprayType(SprayDetails.sprayTypeAuto);
                sd.setIsSpraying(true);
                sd.setIntervalActual(0);
                sd.setAdd_time(new Date());

                NxecoAPP.getDaoSession().getSprayDetailsDao().insertOrReplace(sd);
            }
            NxecoAPP.getContext().getContentResolver().notifyChange(SprayService.URI_SPRAY_LIST_CHANGE, null);
        } catch (Exception e) {
            Log.e("CommandAnalysis", "RainOnce command format error" + e);
        }
    }

    private void getUpdateParam(String param) {
        System.out.println("getUpdateParam");
    }

    private void apkDwonload() {
        Intent mIntent = new Intent().setAction(MainActivity.MSG_Download);
        NxecoAPP.getContext().sendBroadcast(mIntent);
    }
}
