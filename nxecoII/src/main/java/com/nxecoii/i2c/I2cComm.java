package com.nxecoii.i2c;

import android.content.Intent;

import com.nxecoii.activity.MainActivity;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.http.NxecoAPP;

public class I2cComm {
    private static final String Tag = "I2cComm";
    private static final int ERROR = 1;
    private static final int SUCCESS = 0;

    private static final String CMD_CTR_VALVE = "0x01";
    private static final String CMD_CHANGE_ADDRESS = "0x02";
    private static final String CMD_READ_VALVE_STATE = "0x03";
    private static final String CMD_CTR_ALL_VALVES = "0x04";

    private static final String COMM_ADDRESS = "0x1f";

    private static I2cInterfaceTest i2c = new I2cInterfaceTest();
    private static int exboardNum;

    public static boolean sprayOpen(int valve) {
        String exBoardId = new String();
        String zone;
        int res;

        if (valve > 0) {
            if (valve <= 18) {
                exBoardId = "0x05";
            } else if (valve <= 36) {
                valve -= 18;
                exBoardId = "0x06";
            }

            zone = String.format("0x%02X", valve);

            res = i2c.writeI2C(exBoardId, CMD_CTR_VALVE, zone + "01");
            if (res == ERROR) {
//                Log.i(Tag, "ERROR-->" + "Open: " + exBoardId + "0x01" + zone + "01");
                return false;
            } else if (res == SUCCESS) {
//                Log.i(Tag, "SUCCESS-->" + "Open: " + exBoardId + "0x01" + zone + "01");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean sprayClose(int valve) {
        String exBoardId = "";
        String zone;
        int res;
        if (valve > 0) {
            if (valve <= 18) {
                exBoardId = "0x05";
            } else if (valve <= 36) {
                valve -= 18;
                exBoardId = "0x06";
            }
            zone = String.format("0x%02X", valve);
            res = i2c.writeI2C(exBoardId, CMD_CTR_VALVE, zone + "02");
            if (res == ERROR) {
//                Log.i(Tag, "ERROR-->" + "Close: " + exBoardId + "0x01" + zone + "02");
                return false;
            } else if (res == SUCCESS) {
//                Log.i(Tag, "SUCCESS-->" + "Close: " + exBoardId + "0x01" + zone + "02");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

//    public static void getExBoardId() {
//        byte[] address = i2c.detectI2C();
//        for (int i = 0; i < address.length; i++) {
//            if (address[i] == 0xFF) {
//                System.out.println("GET A NEW EXBOARD!!!");
//            }
//        }
//    }


    public static void getExBoardState() {
        int res;
        String exBoardId = "0x05";

        res = i2c.readI2C(exBoardId, CMD_READ_VALVE_STATE);

        if (res == -1) {
//            Log.e("I2cComm getExBoardState", "Connect Exboard Error!");
            return;
        }

        int zone = res & 0x000000ff;
//        Log.d("I2cComm getExBoardState", "get zone：" + zone);

//        boolean master = ((res >> 15) & 1) > 0;
//        Log.d("I2cComm getExBoardState", "get master:" + master);
//        if (master != DeviceBaseInfo.isMaster) {
//            Log.e("I2cComm getExBoardState", "master not match:" + master);
//        }


        boolean sensor = (((res >> 14) & 1) == 0);
//        Log.d("I2cComm getExBoardState", "get sensor：" + sensor);
        if (sensor != DeviceBaseInfo.isSensor) {
//            Log.e("I2cComm getExBoardState", "sensor changed:" + sensor);
            DeviceBaseInfo.isSensor = sensor;
            Intent mIntent = new Intent().setAction(MainActivity.MSG_SENSOR);
            mIntent.putExtra("sensor", sensor);
            NxecoAPP.getContext().sendBroadcast(mIntent);
        }
    }


    public static void detect() {
        byte[] res = i2c.detectI2C();
        String message = "";
        for (byte re : res) {
            message = message + "0x" + Integer.toHexString(re) + " ";
        }
        int j = 0;
        for (int i = 0; i < res.length; i++) {
            if (res[i] > 0) {
                j++;
            }
        }
        exboardNum = j;
        if (exboardNum > 0) {

//            String value = "0X000" + Integer.toHexString(exboardNum + 4);
            String value = String.format("0x00%02X", exboardNum+4);
            i2c.writeI2C(COMM_ADDRESS, CMD_CHANGE_ADDRESS,value );
        }

//        Log.d("I2cComm", "res:" + message);
    }
}
