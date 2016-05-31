package com.nxecoii.activity.child;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nxecoii.R;
import com.nxecoii.adapter.HorizontalListView;
import com.nxecoii.adapter.HorizontalListViewAdapter;

public class WeatherDetailsFragment extends Fragment{

	private HorizontalListViewAdapter hlva;
	private HorizontalListView hlv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.horizontallistview_act, container, false);
		
		hlv=(HorizontalListView)view.findViewById(R.id.horizontallistview1);
		hlva=new HorizontalListViewAdapter(view.getContext());
		hlva.notifyDataSetChanged();
		hlv.setAdapter(hlva);
		
		return view;
	}
	
	
}
