package com.nxecoii.activity.child;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.nxecoii.R;
import com.nxecoii.activity.child.settings.DateTimeSetFragment;
import com.nxecoii.activity.child.settings.LightSetFragment;
import com.nxecoii.activity.child.settings.UserFragment;
import com.nxecoii.activity.child.settings.WifiSetFragment;

public class SettingsFragment extends Fragment {

	LinearLayout wifi_set;
	LinearLayout light_set;
	LinearLayout date_set;
	LinearLayout user_set;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.settings_main, container, false);
		
		wifi_set = (LinearLayout)view.findViewById(R.id.wifi_set);
		light_set = (LinearLayout)view.findViewById(R.id.light_set);
		date_set = (LinearLayout)view.findViewById(R.id.date_set);
		user_set = (LinearLayout)view.findViewById(R.id.user_set);
		
		wifi_set.setOnClickListener(clickListener);
		light_set.setOnClickListener(clickListener);
		date_set.setOnClickListener(clickListener);
		user_set.setOnClickListener(clickListener);
		
		setDefaultFragment();
		
		return view;
	}
	
	 @SuppressLint("NewApi") 
	 private void setDefaultFragment()  {
		 
		 FragmentManager fm = getFragmentManager();  
	     FragmentTransaction transaction = fm.beginTransaction();
	     wifi_set.setBackground(getActivity().getResources()
					.getDrawable(R.color.program_pressed));
			
		WifiSetFragment wifiSetFragment = new WifiSetFragment();
		transaction.replace(R.id.setting_content,wifiSetFragment);
		transaction.commit();
	 }
	
	OnClickListener clickListener = new OnClickListener() {
		
		@SuppressLint("NewApi") @Override
		public void onClick(View v) {
			wifi_set.setBackground(getActivity().getResources()
					.getDrawable(R.color.settings_menu_bg));
			light_set.setBackground(getActivity().getResources()
					.getDrawable(R.color.settings_menu_bg));
			date_set.setBackground(getActivity().getResources()
					.getDrawable(R.color.settings_menu_bg));
			user_set.setBackground(getActivity().getResources()
					.getDrawable(R.color.settings_menu_bg));
			
			FragmentManager fm = getFragmentManager();  
	        FragmentTransaction transaction = fm.beginTransaction();
			
			switch (v.getId()) {
			case R.id.wifi_set:
				wifi_set.setBackground(getActivity().getResources()
						.getDrawable(R.color.program_pressed));
				
				WifiSetFragment wifiSetFragment = new WifiSetFragment();
				transaction.replace(R.id.setting_content,wifiSetFragment);
				break;
				
			case R.id.light_set:
				light_set.setBackground(getActivity().getResources()
						.getDrawable(R.color.program_pressed));
				LightSetFragment lightSetFragment = new LightSetFragment();
				transaction.replace(R.id.setting_content,lightSetFragment);
				break;
				
			case R.id.date_set:
				date_set.setBackground(getActivity().getResources()
						.getDrawable(R.color.program_pressed));
				DateTimeSetFragment dateSetFragment = new DateTimeSetFragment();
				transaction.replace(R.id.setting_content,dateSetFragment);
				break;
				
			case R.id.user_set:
				user_set.setBackground(getActivity().getResources()
						.getDrawable(R.color.program_pressed));
				UserFragment userFragment = new UserFragment();
				transaction.replace(R.id.setting_content,userFragment);
				break;

			default:
				break;
			}
			transaction.commit();
		}
	};
}
