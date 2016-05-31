package com.nxecoii.activity.child.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nxecoii.R;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.http.NxecoAPP;

public class UserFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_user, container, false);
		
		TextView dev_id = (TextView)view.findViewById(R.id.devid_text);
		dev_id.setText("Device ID : " + DeviceBaseInfo.serialNumber);

		TextView ver_tv = (TextView)view.findViewById(R.id.ver_text);
		ver_tv.setText("Version : "+NxecoAPP.getVersion());

		return view;
	}
}
