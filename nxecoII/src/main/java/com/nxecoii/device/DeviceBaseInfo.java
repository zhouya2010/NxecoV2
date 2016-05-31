package com.nxecoii.device;

import java.util.ArrayList;

/***
 * Touch 版本与Q版本区别
 * 1、Q版本去掉Manifest里面的桌面设置
 * 2、修改设备型号和系列号
 * 3、修改MainActivity下载路径
 */

public class DeviceBaseInfo {

    public static int deviceId = 0;
    public static int currentGroupId = 0;
    public static String region = "";
    public static String serialNumber ;
    public static String connuid = "";
    public static String deviceType = "1";
    public static String zipcode = "";
    public static String weatherUpdateTime = "";
    public static String series = "Touch";
    public static String model = "HWT18-100";
//    public static String series = "Q";
//    public static String model = "HWQ12-100";
    public static boolean isMaster = false;
    public static boolean isSensor = false;
    public static ArrayList<ProgramInfo> programInfoList = new ArrayList<ProgramInfo>();
    public static ArrayList<WeatherInfo> weather = new ArrayList<WeatherInfo>() {
        {
            add(new WeatherInfo());
        }

        ;

        {
            add(new WeatherInfo());
        }

        ;

        {
            add(new WeatherInfo());
        }

        ;

        {
            add(new WeatherInfo());
        }

        ;

        {
            add(new WeatherInfo());
        }

        ;
    };

    public static class WeatherInfo {

        public String descript;
        public String sunrise;
        public String sunset;
        public String day;
        public int humidity;
        public int adjust;
        public int raining;
        public int averageTemp;
        public int minTemp;
        public int maxTemp;
        /**
         * 0:By weather 1:By manual
         */
        public int operateMode;

        public WeatherInfo() {
            adjust = 100;
        }

        @Override
        public String toString() {
            return "WeatherInfo [weatherDescript=" + descript
                    + ", sunrise=" + sunrise + ", sunset=" + sunset
                    + ", humidity=" + humidity + ", day=" + day + ", adjust="
                    + adjust + ", raining=" + raining + ", averageTemp="
                    + averageTemp + ", minTemp=" + minTemp + ", maxTemp="
                    + maxTemp + ", operateMode=" + operateMode
                    + "]";
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }

    public static class ProgramInfo {
        public int id;
        public String name;
    }

    public static int getCurrentGroupId() {
        return currentGroupId;
    }

    public static void setCurrentGroupId(int currentGroupId) {
        DeviceBaseInfo.currentGroupId = currentGroupId;
    }

    public static String getSerialNumber() {
        return serialNumber;
    }

    public static void setSerialNumber(String serialNumber) {
        DeviceBaseInfo.serialNumber = serialNumber;
    }

    public static String getConnuid() {
        return connuid;
    }

    public static void setConnuid(String connuid) {
        DeviceBaseInfo.connuid = connuid;
    }


    public static String getDeviceType() {
        return deviceType;
    }

    public static void setDeviceType(String deviceType) {
        DeviceBaseInfo.deviceType = deviceType;
    }

    public static String getSeries() {
        return series;
    }

    public static void setSeries(String series) {
        DeviceBaseInfo.series = series;
    }

    public static String getModel() {
        return model;
    }

    public static void setModel(String model) {
        DeviceBaseInfo.model = model;
    }

    public static boolean isMaster() {
        return isMaster;
    }

    public static boolean isSensor() {
        return isSensor;
    }

    public static void setIsMaster(boolean isMaster) {
        DeviceBaseInfo.isMaster = isMaster;
    }

    public static void setIsSensor(boolean isSensor) {
        DeviceBaseInfo.isSensor = isSensor;
    }


    public static ArrayList<WeatherInfo> getWeather() {
        return weather;
    }

    public static void setWeather(ArrayList<WeatherInfo> weather) {
        DeviceBaseInfo.weather = weather;
    }

    public static ArrayList<ProgramInfo> getProgramInfoList() {
        return programInfoList;
    }

    public static void setProgramInfoList(ArrayList<ProgramInfo> programInfoList) {
        DeviceBaseInfo.programInfoList = programInfoList;
    }

    public static String getWeatherUpdateTime() {
        return weatherUpdateTime;
    }

    public static void setWeatherUpdateTime(String weatherUpdateTime) {
        DeviceBaseInfo.weatherUpdateTime = weatherUpdateTime;
    }

    public static String getZipcode() {
        return zipcode;
    }

    public static void setZipcode(String zipcode) {
        DeviceBaseInfo.zipcode = zipcode;
    }

    public static String getRegion() {
        return region;
    }

    public static void setRegion(String region) {
        DeviceBaseInfo.region = region;
    }

    public static int getDeviceId() {
        return deviceId;
    }

    public static void setDeviceId(int deviceId) {
        DeviceBaseInfo.deviceId = deviceId;
    }
}
