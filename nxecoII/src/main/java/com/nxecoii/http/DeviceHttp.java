package com.nxecoii.http;

import android.content.Intent;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nxecoii.activity.MainActivity;
import com.nxecoii.device.DeviceBaseInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DeviceHttp {

    private static final String TAG = "DeviceHttp";
    public static final int TimeoutMs = 5000;

    public static void getFine() {
        String strParam =NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/getfine?&cno=" + DeviceBaseInfo.getConnuid();

        Log.d(TAG, strParam);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response:" + response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                TimeoutMs,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        NxecoAPP.mRequestQueue.add(jsonObjectRequest);
    }


    public static void queryWeather() {

        String strParam =NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/getweatherbydevid?cno=" + DeviceBaseInfo.connuid;
        Log.d("MyIntentService", strParam);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MainActivity", "response:" + response);
                        try {
                            String strError = response.getString("error");
                            if (strError.equals("200")) {
                                JSONObject data = response.getJSONObject("data");
                                if (data != null) {
                                    DeviceBaseInfo.region = data.getString("region");
                                    JSONArray jsWeathers = data.getJSONArray("weather");
                                    for (int i = 0; i < jsWeathers.length(); i++) {
                                        JSONObject objWeather = jsWeathers.getJSONObject(i);
                                        DeviceBaseInfo.weather.get(4 - i).descript = objWeather
                                                .getString("descript");
                                        DeviceBaseInfo.weather.get(4 - i).humidity = objWeather
                                                .getInt("humidity");
                                        DeviceBaseInfo.weather.get(4 - i).day = objWeather
                                                .getString("day");
                                        DeviceBaseInfo.weather.get(4 - i).adjust = objWeather
                                                .getInt("finetune");
                                        DeviceBaseInfo.weather.get(4 - i).sunrise = objWeather
                                                .getString("sunrise");
                                        DeviceBaseInfo.weather.get(4 - i).sunset = objWeather
                                                .getString("sunset");
                                        DeviceBaseInfo.weather.get(4 - i).operateMode = objWeather
                                                .getInt("tag");
                                        DeviceBaseInfo.weather.get(4 - i).averageTemp = objWeather
                                                .getInt("ave_temp");
                                        DeviceBaseInfo.weather.get(4 - i).minTemp = objWeather
                                                .getInt("min_temp");
                                        DeviceBaseInfo.weather.get(4 - i).maxTemp = objWeather
                                                .getInt("max_temp");
                                        DeviceBaseInfo.weather.get(4 - i).raining = objWeather
                                                .getInt("rain_probability");
                                    }
                                    DeviceBaseInfo.weatherUpdateTime = new SimpleDateFormat(
                                            "dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).format(new Date());
                                    Intent mIntent = new Intent().setAction(MainActivity.MSG_WEATHER);
                                    NxecoAPP.getContext().sendBroadcast(mIntent);
                                }
                            } else {
                                Log.e("DeviceHttp", strError);
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                TimeoutMs,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        NxecoAPP.mRequestQueue.add(jsonObjectRequest);
    }

    public static void sentDeviceType() {
        String strParam = NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/type?&cno=" + DeviceBaseInfo.connuid
                + "&s=" + DeviceBaseInfo.series + "&m="
                + DeviceBaseInfo.model + "&v=" + NxecoAPP.getVersion();
        Log.d("MainActivity", strParam);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MainActivity", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                TimeoutMs,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        NxecoAPP.mRequestQueue.add(jsonObjectRequest);
    }

}
