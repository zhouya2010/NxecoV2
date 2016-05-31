package com.nxecoii.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nxecoii.R;
import com.nxecoii.activity.child.HomeFragment;
import com.nxecoii.device.DeviceBaseInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HorizontalListViewAdapter extends BaseAdapter{

	public HorizontalListViewAdapter(Context con){
		mInflater=LayoutInflater.from(con);
	}
	@Override
	public int getCount() {
		return 5;
	}
	private LayoutInflater mInflater;
	@Override
	public Object getItem(int position) {
		return position;
	}
	private ViewHolder vh 	 =new ViewHolder();
	private  class ViewHolder {
		private TextView weekday ;
		private TextView temp ;
//		private TextView raining ;
//		private TextView humidity ;
		private TextView adjust ;
		private ImageView imWeather;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.weather_item_2, null);
			vh.weekday=(TextView)convertView.findViewById(R.id.weather1_week_text);
			vh.temp=(TextView)convertView.findViewById(R.id.weather1_temp_text);
//			vh.raining=(TextView)convertView.findViewById(R.id.weather1_raining_text);
//			vh.humidity=(TextView)convertView.findViewById(R.id.weather1_humidity_text);
			vh.adjust=(TextView)convertView.findViewById(R.id.weather1_adjust_text);
			vh.imWeather=(ImageView)convertView.findViewById(R.id.weather1_image);
			convertView.setTag(vh);
		}else{
			vh=(ViewHolder)convertView.getTag();
		}

		if(position == 0){
			vh.weekday.setText("Today");
		}
		else{
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("EE", Locale.ENGLISH);
			cal.add(Calendar.DATE, position);
			vh.weekday.setText(""+ sdf.format(cal.getTime()));
		}
		
		if((DeviceBaseInfo.weatherUpdateTime != null) && (!DeviceBaseInfo.weatherUpdateTime.equals(""))) {
			vh.temp.setText("" + DeviceBaseInfo.weather.get(position).maxTemp + "-" + DeviceBaseInfo.weather.get(position).minTemp + "°F");
			HomeFragment.SetWeatherImage(vh.imWeather, DeviceBaseInfo.weather.get(position));
//			vh.raining.setText(""+ DeviceBaseInfo.weather.get(position).raining);
//			vh.humidity.setText(""+ DeviceBaseInfo.weather.get(position).humidity);
			vh.adjust.setText(""+ DeviceBaseInfo.weather.get(position).adjust);
		}
		else {
			vh.temp.setText("" + 0 + "-" + 0 + "°F");
//			vh.raining.setText(""+ 0);
//			vh.humidity.setText(""+ 0);
			vh.adjust.setText(""+ 100);
		}
		
		return convertView;
	}
	
}