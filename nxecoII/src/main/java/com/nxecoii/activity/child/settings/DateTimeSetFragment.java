package com.nxecoii.activity.child.settings;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.nxecoii.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeSetFragment extends Fragment {

	RelativeLayout time_set;
	RelativeLayout date_set;
	TextView time_show;
	TextView date_show;
	LayoutInflater inflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.settings_date_set, container, false);

		time_set = (RelativeLayout) view.findViewById(R.id.time_set);
		date_set = (RelativeLayout) view.findViewById(R.id.date_set);
		time_set.setOnClickListener(clicklistener);
		date_set.setOnClickListener(clicklistener);

		time_show = (TextView) view.findViewById(R.id.current_time);
		date_show = (TextView) view.findViewById(R.id.current_date);

		time_show.setText(new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(new Date()));
		date_show.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(new Date()));
		
		return view;
	}

	View.OnClickListener clicklistener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.time_set:
				showTimeDialog();
				break;

			case R.id.date_set:
				showDateDialog();
				break;

			default:
				break;
			}
		}

	};

	private void showTimeDialog() {

		View setTimeDialog = inflater.inflate(R.layout.dialog_time_set, null);
		final TimePicker setTimePicker = (TimePicker) setTimeDialog
				.findViewById(R.id.set_time_picker);
		setTimePicker.setIs24HourView(false);

		AlertDialog ad = new AlertDialog.Builder(getActivity())
				.setView(setTimeDialog).setTitle("Set Time")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						setTime(setTimePicker.getCurrentHour(),setTimePicker.getCurrentMinute());
						time_show.setText(new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(new Date()));
					}
				}).setNegativeButton("Cancel", null).create();
		ad.show();
	}

	private void showDateDialog() {
		
		View setDateDialog = inflater.inflate(R.layout.dialog_date_set, null);
		final DatePicker datePicker = (DatePicker) setDateDialog
				.findViewById(R.id.set_date_picker);
		
		AlertDialog ad = new AlertDialog.Builder(getActivity())
		.setView(setDateDialog).setTitle("Set Date")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				setDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
				date_show.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(new Date()));
			}
		}).setNegativeButton("Cancel", null).create();
ad.show();
	}

	public void setDate(int year, int month, int day) {
		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			((AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE)).setTime(when);
		}
	}

	public void setTime(int hourOfDay, int minute) {
		Calendar c = Calendar.getInstance();

		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			((AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE)).setTime(when);
		}
	}
}
