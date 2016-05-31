package com.nxecoii.activity.child;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.nxecoii.R;
import com.nxecoii.activity.MainActivity;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.http.NxecoAPP;

public class TestFragment extends Fragment implements View.OnClickListener {

    private final String uri = "http://45.33.46.130/upfile/apk/NxecoII.apk";
    final String fileName = "NxecoII.apk";
    private static Toast mToast = null;
    public static final String saveDirectry = Environment.getExternalStorageDirectory().getPath() + "/Download";
    public static final String Msg_INSTALL = "com.example.nxecoii.autoinstall";

    Button open, close, detect, sensor;

//    I2cInterfaceTest iic = new I2cInterfaceTest();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test, container, false);

        open = (Button) view.findViewById(R.id.open);
        close = (Button) view.findViewById(R.id.close);
        detect = (Button) view.findViewById(R.id.detect);
        sensor = (Button) view.findViewById(R.id.sensor);

        open.setOnClickListener(this);
        close.setOnClickListener(this);
        detect.setOnClickListener(this);
        sensor.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        String chipValue = "0x08";
        String regisValue = "0x01";
        String dataValue = "0x0101";
        int result;
        switch (view.getId()) {
            case R.id.open:
                Log.d("TestFragment", "open");
//                chipValue = "0x08";
//                regisValue = "0x01";
//                dataValue = "0x0101";
//                result = iic.writeI2C(chipValue, regisValue, dataValue);
//                Log.d("TestFragment", "result:" + result);
                break;
            case R.id.close:
                Log.d("TestFragment", "close");
//                chipValue = "0x08";
//                regisValue = "0x01";
//                dataValue = "0x0102";
//                result = iic.writeI2C(chipValue, regisValue, dataValue);
//                Log.d("TestFragment", "result:" + result);

                break;
            case R.id.detect:

                Log.d("TestFragment", "detect");

//                byte[] res = iic.detectI2C();
//                String message = "";
//                for (int i = 0; i < res.length; i++) {
//                    message = message + "0X" + Integer.toHexString(res[i]) + " ";
//                }
//                Log.d("TestFragment", "res:" + message);

                break;

            case R.id.sensor:
                int res2 =0x8013;
                int zone = res2 & 0x000000ff;
                Log.d("I2cComm getExBoardState", "get zone：" +zone );

                boolean master = ((res2 >> 15) & 1) > 0;
                Log.d("I2cComm getExBoardState", "get master:" +master );
                if(master != DeviceBaseInfo.isMaster){
                    Log.e("I2cComm getExBoardState", "master not match:" +master );
                }


                boolean sensor = ((res2 >> 14) & 1) == 0;
                Log.d("I2cComm getExBoardState", "get sensor：" +sensor );
                if (sensor != DeviceBaseInfo.isSensor){
                    Log.e("I2cComm getExBoardState", "sensor changed:" +sensor );
                    DeviceBaseInfo.isSensor = sensor;
                    Intent mIntent = new Intent().setAction(MainActivity.MSG_SENSOR);
                    mIntent.putExtra("sensor", sensor);
                    NxecoAPP.getContext().sendBroadcast(mIntent);
                    Log.d("TestFragment", "sendBroadcast");
                }
                break;

            default:
                break;
        }
    }
}
