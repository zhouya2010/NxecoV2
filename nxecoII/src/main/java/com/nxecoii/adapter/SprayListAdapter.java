package com.nxecoii.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nxecoii.R;
import com.nxecoii.device.ZoneInfo;
import com.nxecoii.greendao.SprayDetails;
import com.nxecoii.greendao.WaterData;
import com.nxecoii.http.NxecoAPP;
import com.nxecoii.i2c.I2cComm;
import com.nxecoii.schedule.SprayService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SprayListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	public  List<SprayDetails> sprayList = new ArrayList<SprayDetails>();
    List<ZoneInfo> zoneList;

	public SprayListAdapter(Context context,List<SprayDetails> sprayList, List<ZoneInfo> zoneList) {
		this.mInflater = LayoutInflater.from(context);
		this.mContext = context;
		this.sprayList = sprayList;
        this.zoneList = zoneList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sprayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		final int position2 = position;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.schedule_list_adapter,
					null);
			holder = new ViewHolder();
			holder.number = (TextView) convertView
					.findViewById(R.id.serial_number);
			holder.name = (TextView) convertView
					.findViewById(R.id.schedule_zones_name);
			holder.minLeft = (TextView) convertView
					.findViewById(R.id.sechdule_time_min);
            holder.img = (ImageView) convertView.findViewById(R.id.watering_zone_img);
//			holder.deviderText = (TextView) convertView
//					.findViewById(R.id.deviderText);
//			holder.minTotal = (TextView) convertView
//					.findViewById(R.id.sechdule_time_total);
//			holder.minText = (TextView) convertView
//					.findViewById(R.id.minText);
//			holder.waitingText = (TextView) convertView.findViewById(R.id.waitingText);
			holder.queueDlete = (ImageButton) convertView.findViewById(R.id.queue_delete);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SprayDetails sd = sprayList.get(position);
		holder.queueDlete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SprayService.saveWaterToDatabase(sprayList.get(position2));
				NxecoAPP.getContext().getContentResolver().notifyChange(WaterData.URI_CONTENT_WATER,null);
				I2cComm.sprayClose(sprayList.get(position2).getZone());
				NxecoAPP.getDaoSession().getSprayDetailsDao().delete(sprayList.get(position2));
				mContext.getContentResolver().notifyChange(SprayService.URI_SPRAY_LIST_CHANGE, null);
			}
		});
		holder.number.setText(mContext.getString(R.string.to_int, (position + 1)));
		holder.name.setText(zoneList.get(sd.getZone()-1).zonename);
        Picasso.with(mContext).load(zoneList.get(sd.getZone()-1).imageUrl).resize(65, 45).error(R.drawable.default_zone).placeholder(R.drawable.default_zone).into(holder.img, null);

//		holder.minLeft.setText(sd.getRemainTime()/3600+ ":"+ (sd.getRemainTime()%3600)/60+ ":"+ sd.getRemainTime()%60);
		holder.minLeft.setText(mContext.getString(R.string.H_M_S,sd.getRemainTime()/3600,(sd.getRemainTime()%3600)/60,(sd.getRemainTime())%60));
//		if(position == 0){
//			holder.minLeft.setText("" + ((sprayList.get(position).intervalActual - 1)/60));
//
//			if(sprayList.get(position).intervalOrigin > 0) {
//				holder.minTotal.setText("" + ((sprayList.get(position).intervalOrigin- 1)/60 + 1));
//			} else {
//				holder.minTotal.setText("0");
//			}
//
//		}
//		else {
//			holder.minLeft.setVisibility(View.INVISIBLE);
//			holder.deviderText.setVisibility(View.INVISIBLE);
//			holder.minTotal.setVisibility(View.INVISIBLE);
//			holder.minText.setVisibility(View.INVISIBLE);
//			holder.waitingText.setVisibility(View.VISIBLE);
////			holder.waitingText.setText("" + (sprayList.get(position).intervalOrigin/60));
//		}
		return convertView;
	}

	class ViewHolder {
		private TextView number;
		private TextView name;
		private TextView minLeft;
		private TextView deviderText;
		private TextView minTotal;
		private TextView minText;
		private TextView waitingText;
		private ImageButton queueDlete;
        private ImageView img;
	}

}
