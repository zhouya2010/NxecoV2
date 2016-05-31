package com.nxecoii.activity.child;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nxecoii.R;
import com.nxecoii.activity.MainActivity;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.device.DeviceBaseInfo.WeatherInfo;
import com.nxecoii.http.NxecoAPP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment implements OnClickListener {

    private View view;
    private RelativeLayout forecast;
    private RelativeLayout adjustment;
    private RelativeLayout rate;
    private WeatherReceiver weatherReceiver;

    private ImageView forecast_image;
    private ImageView adjustment_image;
    private TextView forecast_text;
    private TextView adjustment_text;
    private TextView raining_text;

    private final String Tag = "HomeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.inflate_main, container, false);

        forecast = (RelativeLayout) view.findViewById(R.id.forecast);
        adjustment = (RelativeLayout) view.findViewById(R.id.adjustment);
        rate = (RelativeLayout) view.findViewById(R.id.rate);

        forecast.setOnClickListener(this);
        adjustment.setOnClickListener(this);
        rate.setOnClickListener(this);

        forecast_text = (TextView) view.findViewById(R.id.forecast_text);
        forecast_image = (ImageView) view.findViewById(R.id.forecast_image);
        adjustment_image = (ImageView) view.findViewById(R.id.adjustment_image);
        adjustment_text = (TextView) view.findViewById(R.id.rate_text);
        raining_text = (TextView) view.findViewById(R.id.adjustment_text);

        if ((DeviceBaseInfo.weatherUpdateTime != null) && (!DeviceBaseInfo.weatherUpdateTime.equals(""))) {
            forecast_text.setText("" + DeviceBaseInfo.weather.get(0).averageTemp);
            raining_text.setText("" + DeviceBaseInfo.weather.get(0).raining);
            setRainingImage();
            adjustment_text.setText("" + DeviceBaseInfo.weather.get(0).adjust);
            SetWeatherImage(forecast_image, DeviceBaseInfo.weather.get(0));
        }

        weatherReceiver = new WeatherReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.MSG_WEATHER);
        getActivity().registerReceiver(weatherReceiver, intentFilter);

        if (!DeviceBaseInfo.connuid.equals("")) {
            queryWeather();
        }
        return view;
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
        getActivity().unregisterReceiver(weatherReceiver);
        NxecoAPP.mRequestQueue.cancelAll(Tag);
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        switch (v.getId()) {
            case R.id.forecast:
                transaction.replace(R.id.inflate, new WeatherDetailsFragment());
//			transaction.addToBackStack(null);
                transaction.commit();
                break;

            case R.id.adjustment:
                transaction.replace(R.id.inflate, new WeatherDetailsFragment());
                transaction.commit();
                break;
            case R.id.rate:
                transaction.replace(R.id.inflate, new WeatherDetailsFragment());
                transaction.commit();
                break;

            default:
                break;
        }

    }

    public class WeatherReceiver extends BroadcastReceiver {
        private static final String TAG = "WeatherReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            if (intent.getAction().equals(MainActivity.MSG_WEATHER)) {
                Log.i(TAG, "WeatherReceiver");
                forecast_text.setText("" + DeviceBaseInfo.weather.get(0).averageTemp);
                raining_text.setText("" + DeviceBaseInfo.weather.get(0).raining);
                adjustment_text.setText("" + DeviceBaseInfo.weather.get(0).adjust);
                SetWeatherImage(forecast_image, DeviceBaseInfo.weather.get(0));
                setRainingImage();
            }
        }
    }

    @SuppressLint("NewApi")
    private void setRainingImage() {

        if (DeviceBaseInfo.weather.get(0).raining > 75) {
            adjustment_image.setBackground(getResources().getDrawable(R.drawable.icon_wateringconsumption4));
        } else if (DeviceBaseInfo.weather.get(0).raining > 50) {
            adjustment_image.setBackground(getResources().getDrawable(R.drawable.icon_wateringconsumption3));
        } else if (DeviceBaseInfo.weather.get(0).raining > 25) {
            adjustment_image.setBackground(getResources().getDrawable(R.drawable.icon_wateringconsumption2));
        } else {
            adjustment_image.setBackground(getResources().getDrawable(R.drawable.icon_wateringconsumption1));
        }
    }

    public static void SetWeatherImage(ImageView imageView, WeatherInfo weather) {
        boolean isDay = true;
        Calendar current = Calendar.getInstance();
        Calendar calSunrise = Calendar.getInstance();
        Calendar calSunset = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        try {
            current.setTime(sdf.parse(sdf.format(new Date())));
            calSunrise.setTime(sdf.parse(weather.sunrise));
            calSunset.setTime(sdf.parse(weather.sunset));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if ((current.compareTo(calSunrise) > 0) && (current.compareTo(calSunset) < 0)) {
            isDay = true;
        } else {
            isDay = false;
        }

        if (weather.descript.equalsIgnoreCase("broken clouds")
                || weather.descript.equalsIgnoreCase("few clouds")
                || weather.descript.equalsIgnoreCase("cloudy")
                || weather.descript.equalsIgnoreCase("partly cloudy")) {
            if (isDay) {
                imageView.setBackgroundResource(R.drawable.weather_clouds_day);
            } else {
                imageView.setBackgroundResource(R.drawable.weather_clouds_night);
            }
        } else if (weather.descript.contains("clear") || weather.descript.equalsIgnoreCase("sunny")) {
            if (isDay) {
                imageView.setBackgroundResource(R.drawable.weather_clear_day);
            } else {
                imageView.setBackgroundResource(R.drawable.weather_clear_night);
            }
        } else if (weather.descript.contains("mist") || weather.descript.contains("haze")) {
            if (isDay) {
                imageView.setBackgroundResource(R.drawable.weather_mist_day);
            } else {
                imageView.setBackgroundResource(R.drawable.weather_mist_night);
            }
        } else if (weather.descript.contains("rain") || weather.descript.equalsIgnoreCase("patchy light drizzle")
                || weather.descript.equalsIgnoreCase("light drizzle")) {

            if (isDay) {
                imageView.setBackgroundResource(R.drawable.weather_rain_day);
            } else {
                imageView.setBackgroundResource(R.drawable.weather_rain_night);
            }
        } else if (weather.descript.equalsIgnoreCase("scattered clouds")
                || weather.descript.equalsIgnoreCase("cloudy")) {
            if (isDay) {
                imageView.setBackgroundResource(R.drawable.weather_scatteredclouds_day);
            } else {
                imageView.setBackgroundResource(R.drawable.weather_scatteredclouds_night);
            }
        } else if (weather.descript.contains("snow")
                || weather.descript.equalsIgnoreCase("moderate or heavy sleet")) {

            if (isDay) {
                imageView.setBackgroundResource(R.drawable.weather_snow_day);
            } else {
                imageView.setBackgroundResource(R.drawable.weather_snow_night);
            }
        } else if (weather.descript.equalsIgnoreCase("thunderstorm")) {

            if (isDay) {
                imageView.setBackgroundResource(R.drawable.weather_thunderstorms_day);
            } else {
                imageView.setBackgroundResource(R.drawable.weather_thunderstorms_night);
            }
        } else {
            if (isDay) {
                imageView.setBackgroundResource(R.drawable.weather_clear_day);
            } else {
                imageView.setBackgroundResource(R.drawable.weather_clear_night);
            }
        }

    }


    void queryWeather() {

        String strParam = NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/getweatherbydevid?cno=" + DeviceBaseInfo.connuid;
        Log.d("HomeFragment", strParam);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("HomeFragment", "response:" + response);
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

                                    forecast_text.setText(getString(R.string.average_temp, DeviceBaseInfo.weather.get(0).averageTemp));
                                    raining_text.setText(getString(R.string.raining, DeviceBaseInfo.weather.get(0).raining));
                                    adjustment_text.setText(getString(R.string.adjust, DeviceBaseInfo.weather.get(0).adjust));
                                    SetWeatherImage(forecast_image, DeviceBaseInfo.weather.get(0));
                                    setRainingImage();
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
        jsonObjectRequest.setTag(Tag);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        NxecoAPP.mRequestQueue.add(jsonObjectRequest);
    }
}
