package com.nxecoii.activity.child;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.nxecoii.R;
import com.nxecoii.adapter.AutoScaleTextView;
import com.nxecoii.adapter.CommonAdapter;
import com.nxecoii.adapter.LineGridView;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.device.ZoneInfo;
import com.nxecoii.greendao.ScheduleData;
import com.nxecoii.greendao.ScheduleDataDao;
import com.nxecoii.http.NxecoAPP;
import com.nxecoii.schedule.DaysOfWeek;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.DeleteQuery;

public class ScheduleFragment extends Fragment {

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.nxecoii.scheduleprovider";
    public static final Uri URI_CONTENT_SCHEDULE = Uri.parse(SCHEME + AUTHORITY);

    private TextView repeat_summary;
    private TextView zone_name;

    private List<ZoneInfo> zoneArraylist;
    CommonAdapter adaper;
    private int zoneSelected;

    private DaysOfWeek mDaysOfWeek = new DaysOfWeek(0);

    String[] weekdays = new DateFormatSymbols(Locale.ENGLISH).getWeekdays();
    String[] values = new String[]{weekdays[Calendar.MONDAY],
            weekdays[Calendar.TUESDAY], weekdays[Calendar.WEDNESDAY],
            weekdays[Calendar.THURSDAY], weekdays[Calendar.FRIDAY],
            weekdays[Calendar.SATURDAY], weekdays[Calendar.SUNDAY]};

    private ArrayList<ScheduleData> schList;
    SchListAdapter schAdapter;
    private ListView scheduleListView;

    private ScheduleObserver schObserver;

    private RelativeLayout springLayout;
    private RelativeLayout summerLayout;
    private RelativeLayout fallLayout;
    private RelativeLayout winterLayout;
    private int programFlag = 0;

    private ImageView programSetSpring;
    private ImageView programSetSummer;
    private ImageView programSetFall;
    private ImageView programSetWinter;

    private ArrayList<TextView> programNameList;

    private final String Tag = "ScheduleFragment";

    private ScheduleDataDao scheduleDataDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule, container, false);
        RelativeLayout repeat = (RelativeLayout) view.findViewById(R.id.repeat);
        repeat.setOnClickListener(new RepeatClickListener());
        repeat_summary = (TextView) view.findViewById(R.id.repeat_summary);
        scheduleDataDao = NxecoAPP.getDaoSession().getScheduleDataDao();
        zoneSelected = 1;
        zoneArraylist = new ArrayList<ZoneInfo>();
        getDefaultList();

        LineGridView zoneListView = (LineGridView) view.findViewById(R.id.zone_list);
        adaper = new CommonAdapter<ZoneInfo>(getActivity(), zoneArraylist, R.layout.grid_item) {
            @Override
            public void convert(ViewHolder holder, ZoneInfo item, int position) {
                holder.setText(R.id.text_item, item.zonename);
                holder.setImage(R.id.image_item, item.imageUrl, null);
                if (position == (zoneSelected - 1)) {
                    holder.setBackgroundColorr(R.id.whole_item, getResources().getColor(R.color.white_trans_20));
                    holder.setTextColor(R.id.text_item, getResources().getColor(R.color.dark_green));
                    holder.setBackgroundColorr(R.id.bottom_line, getResources().getColor(R.color.dark_green));
                } else {
                    holder.setBackgroundColorr(R.id.whole_item, getResources().getColor(R.color.transparent));
                    holder.setTextColor(R.id.text_item, getResources().getColor(R.color.white));
                    holder.setBackgroundColorr(R.id.bottom_line, getResources().getColor(R.color.transparent));
                }
            }
        };

        zoneListView.setAdapter(adaper);

        zoneListView.setOnItemClickListener(listener);

        getZoneInfo();

        zone_name = (TextView) view.findViewById(R.id.zone_name);
        zone_name.setText(zoneArraylist.get(0).zonename);


        TextView btnSave = (TextView) view.findViewById(R.id.btn_save);
        TextView btnDelete = (TextView) view.findViewById(R.id.btn_delete);
        btnSave.setOnClickListener(myClicklistener);
        btnDelete.setOnClickListener(myClicklistener);

        getSchListFromDatabase(zoneSelected);

        scheduleListView = (ListView) view.findViewById(R.id.time_list);
        schAdapter = new SchListAdapter(getActivity());
        scheduleListView.setAdapter(schAdapter);

        schObserver = new ScheduleObserver(new Handler());
        getActivity().getContentResolver().registerContentObserver(
                URI_CONTENT_SCHEDULE, true, schObserver);

        springLayout = (RelativeLayout) view.findViewById(R.id.spring);
        summerLayout = (RelativeLayout) view.findViewById(R.id.summer);
        fallLayout = (RelativeLayout) view.findViewById(R.id.fall);
        winterLayout = (RelativeLayout) view.findViewById(R.id.winter);
        springLayout.setOnClickListener(myClicklistener);
        summerLayout.setOnClickListener(myClicklistener);
        fallLayout.setOnClickListener(myClicklistener);
        winterLayout.setOnClickListener(myClicklistener);
        setProgramBackground(programFlag);

        AutoScaleTextView programName1 = (AutoScaleTextView) view.findViewById(R.id.program_name_1);
        AutoScaleTextView programName2 = (AutoScaleTextView) view.findViewById(R.id.program_name_2);
        AutoScaleTextView programName3 = (AutoScaleTextView) view.findViewById(R.id.program_name_3);
        AutoScaleTextView programName4 = (AutoScaleTextView) view.findViewById(R.id.program_name_4);

        programNameList = new ArrayList<TextView>();
        programNameList.clear();
        programNameList.add(programName1);
        programNameList.add(programName2);
        programNameList.add(programName3);
        programNameList.add(programName4);
        getProgramInfo();

        if (DeviceBaseInfo.programInfoList.size() == 4) {
            for (int i = 0; i < 4; i++) {
                programNameList.get(i).setText(DeviceBaseInfo.programInfoList.get(i).name);
            }
        }

        programSetSpring = (ImageView) view.findViewById(R.id.program_set_1);
        programSetSummer = (ImageView) view.findViewById(R.id.program_set_2);
        programSetFall = (ImageView) view.findViewById(R.id.program_set_3);
        programSetWinter = (ImageView) view.findViewById(R.id.program_set_4);
        programSetSpring.setOnClickListener(myClicklistener);
        programSetSummer.setOnClickListener(myClicklistener);
        programSetFall.setOnClickListener(myClicklistener);
        programSetWinter.setOnClickListener(myClicklistener);
        setStartBackground(DeviceBaseInfo.currentGroupId);

        return view;
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
        schList.clear();
        programNameList.clear();
        getActivity().getContentResolver().unregisterContentObserver(
                schObserver);
        NxecoAPP.mRequestQueue.cancelAll(Tag);
    }


    class RepeatClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {

            AlertDialog ad = new AlertDialog.Builder(getActivity())
                    .setTitle("Repeat")
                    .setMultiChoiceItems(values, mDaysOfWeek.getBooleanArray(),
                            new DialogInterface.OnMultiChoiceClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton, boolean isChecked) {
                                    mDaysOfWeek.set(whichButton, isChecked);
                                }
                            })
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    repeat_summary.setText(mDaysOfWeek
                                            .toString(getActivity(), true));
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("Cancel", null).create();
            ad.show();
        }
    }


    View.OnClickListener myClicklistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            switch (v.getId()) {
                case R.id.btn_save:
                    saveSchedule();
                    break;

                case R.id.btn_delete:
                    deleteScheduleDatabase();
                    break;

                case R.id.spring:
                    programFlag = 0;
                    setProgramBackground(programFlag);
                    updateListShow();
                    break;

                case R.id.summer:
                    programFlag = 1;
                    setProgramBackground(programFlag);
                    updateListShow();
                    break;

                case R.id.fall:
                    programFlag = 2;
                    setProgramBackground(programFlag);
                    updateListShow();
                    break;

                case R.id.winter:
                    programFlag = 3;
                    setProgramBackground(programFlag);
                    updateListShow();
                    break;

                case R.id.program_set_1:
                    DeviceBaseInfo.currentGroupId = 0;
                    setStartBackground(DeviceBaseInfo.currentGroupId);
                    break;

                case R.id.program_set_2:
                    DeviceBaseInfo.currentGroupId = 1;
                    setStartBackground(DeviceBaseInfo.currentGroupId);
                    break;

                case R.id.program_set_3:
                    DeviceBaseInfo.currentGroupId = 2;
                    setStartBackground(DeviceBaseInfo.currentGroupId);
                    break;

                case R.id.program_set_4:
                    DeviceBaseInfo.currentGroupId = 3;
                    setStartBackground(DeviceBaseInfo.currentGroupId);
                    break;
                default:
                    break;
            }
        }

    };

    @SuppressLint("NewApi")
    private void setProgramBackground(int programFlag) {
        if (programFlag == 0) {
            springLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_pressed));
            summerLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_default));
            fallLayout.setBackground(getActivity().getResources().getDrawable(
                    R.color.program_default));
            winterLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_default));
        } else if (programFlag == 1) {
            springLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_default));
            summerLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_pressed));
            fallLayout.setBackground(getActivity().getResources().getDrawable(
                    R.color.program_default));
            winterLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_default));
        } else if (programFlag == 2) {
            springLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_default));
            summerLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_default));
            fallLayout.setBackground(getActivity().getResources().getDrawable(
                    R.color.program_pressed));
            winterLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_default));
        } else if (programFlag == 3) {
            springLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_default));
            summerLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_default));
            fallLayout.setBackground(getActivity().getResources().getDrawable(
                    R.color.program_default));
            winterLayout.setBackground(getActivity().getResources()
                    .getDrawable(R.color.program_pressed));
        }
    }

    @SuppressLint("NewApi")
    private void setStartBackground(int currentProgram) {
        if (currentProgram == 0) {
            programSetSpring.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set_bg));
            programSetSummer.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
            programSetFall.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
            programSetWinter.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
        } else if (currentProgram == 1) {
            programSetSpring.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
            programSetSummer.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set_bg));
            programSetFall.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
            programSetWinter.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
        } else if (currentProgram == 2) {
            programSetSpring.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
            programSetSummer.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
            programSetFall.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set_bg));
            programSetWinter.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
        } else if (currentProgram == 3) {
            programSetSpring.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
            programSetSummer.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
            programSetFall.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set));
            programSetWinter.setBackground(getActivity().getResources()
                    .getDrawable(R.drawable.program_set_bg));
        }
    }

    private void saveSchedule() {

        LinearLayout layout;
        TextView strDuration;
        TextView timeText;
        int count = 0;
        int interval;
        int index = 0;

        if (mDaysOfWeek.getCoded() < 1) {
            Toast.makeText(getActivity(), "Please set repeat!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < 4; i++) {
            layout = (LinearLayout) scheduleListView.getChildAt(i);
            strDuration = (TextView) layout.findViewById(R.id.duration);
            interval = Integer.parseInt(strDuration.getText().toString()) * 60;
            if (interval == 0) {
                count++;
                if (count == 4) {
                    Toast.makeText(getActivity(), "Please set spray time!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                break;
            }
        }

        for (int i = 0; i < 4; i++) {
            layout = (LinearLayout) scheduleListView.getChildAt(i);
            strDuration = (TextView) layout.findViewById(R.id.duration);
            timeText = (TextView) layout.findViewById(R.id.time_repeat);

            interval = Integer.parseInt(strDuration.getText().toString()) * 60;
            if (interval != 0) {

                if (index < schList.size()) {
                    schList.get(index).setStart_time(timeText.getText()
                            + ":00");
                    schList.get(index).setRepeat(mDaysOfWeek.getCoded());
                    schList.get(index).setInterval(interval);
                    schList.get(index).setIsUpload(false);
                    scheduleDataDao.insertOrReplace(schList.get(index));
                } else {
                    ScheduleData sd = new ScheduleData();
                    sd.setZone(zoneSelected);
                    sd.setInterval(interval);
                    sd.setStart_time(timeText.getText() + ":00");
                    sd.setRepeat(mDaysOfWeek.getCoded());
                    sd.setGroupId(programFlag);
                    sd.setIsUpload(false);
                    scheduleDataDao.insertOrReplace(sd);
                }

                index++;
            }
        }
        getActivity().getContentResolver().notifyChange(URI_CONTENT_SCHEDULE, null);
        updateListShow();
        uploadSchedule();
    }


    private void deleteScheduleDatabase() {
        int zone = zoneSelected;
        Log.d("ScheduleFragment", "programFlag:" + programFlag);
        Log.d("ScheduleFragment", "zone:" + zone);
        DeleteQuery dq = scheduleDataDao.queryBuilder().where(ScheduleDataDao.Properties.GroupId.eq(programFlag), ScheduleDataDao.Properties.Zone.eq(zone)).buildDelete();
        dq.executeDeleteWithoutDetachingEntities();

        getActivity().getContentResolver().notifyChange(URI_CONTENT_SCHEDULE, null);
        upLoadDeleteSch(zone, programFlag);
    }

    private void upLoadDeleteSch(int zone, int groupId) {
        if ((schList.size() > 0) && (!DeviceBaseInfo.connuid.equals(""))
                && (DeviceBaseInfo.programInfoList.size() > 0)) {
            int modeid = DeviceBaseInfo.programInfoList.get(groupId).id;
            String strParam = NxecoAPP.mstrMainHostAddr
                    + "/api/rest/v2/device/deleteschedule?&cno="
                    + DeviceBaseInfo.connuid + "&num=" + zone + "&modeid="
                    + modeid;
            StringRequest stringRequest = new StringRequest(strParam, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Toast.makeText(getActivity(), "Reset server successfully!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Reset server failed!", Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setTag(Tag);
            NxecoAPP.mRequestQueue.add(stringRequest);
        } else {
            Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
        }
    }

    public class SchListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        String[] timeInit = new String[]{"08:00", "10:00", "14:00", "18:00"};

        public SchListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.schedule_time_list,
                        null);
                holder = new ViewHolder();
                holder.time = (TextView) convertView
                        .findViewById(R.id.time_repeat);
                holder.duration = (TextView) convertView
                        .findViewById(R.id.duration);
                holder.timeListItem = (LinearLayout) convertView
                        .findViewById(R.id.time_list_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position < schList.size()) {
                try {
                    Date date = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
                            .parse(schList.get(position).getStart_time());
                    String strTime = new SimpleDateFormat("HH:mm").format(date);
                    holder.time.setText("" + strTime);
                    holder.duration.setText("" + schList.get(position).getInterval()
                            / 60);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                holder.time.setText("" + timeInit[position]);
                holder.duration.setText("0");
            }

            onclick(holder.timeListItem, holder.time, holder.duration);

            return convertView;
        }

        class ViewHolder {
            private TextView time;
            private TextView duration;
            private LinearLayout timeListItem;
        }

        public void onclick(LinearLayout but, final TextView time,
                            final TextView duration) {
            try {
                but.setOnClickListener(new OnClickListener() {
                    private View setTimeDialog;
                    private TimePicker setTimePicker;
                    private TimePicker setDurationPicker;

                    private int defaultDuration = Integer.parseInt(duration
                            .getText().toString());

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",
                            Locale.ENGLISH);

                    Date date = sdf.parse(time.getText().toString());

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        setTimeDialog = mInflater.inflate(
                                R.layout.set_time_dialog, null);
                        setTimePicker = (TimePicker) setTimeDialog
                                .findViewById(R.id.set_time_picker);
                        setDurationPicker = (TimePicker) setTimeDialog
                                .findViewById(R.id.set_duration_picker);
                        setTimePicker.setIs24HourView(true);
                        setDurationPicker.setIs24HourView(true);
                        setDurationPicker.setCurrentHour(defaultDuration / 60);
                        setDurationPicker
                                .setCurrentMinute(defaultDuration % 60);
                        setTimePicker.setCurrentHour(date.getHours());
                        setTimePicker.setCurrentMinute(date.getMinutes());

                        AlertDialog ad = new AlertDialog.Builder(getActivity())
                                .setView(setTimeDialog)
                                // .setTitle("Start Time")
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int whichButton) {
                                                time.setText(""
                                                        + String.format(
                                                        "%02d:%02d",
                                                        setTimePicker
                                                                .getCurrentHour(),
                                                        setTimePicker
                                                                .getCurrentMinute()));
                                                duration.setText(""
                                                        + (setDurationPicker
                                                        .getCurrentHour() * 60 + setDurationPicker
                                                        .getCurrentMinute()));
                                                dialog.dismiss();
                                            }
                                        }).setNegativeButton("Cancel", null)
                                .create();
                        ad.show();

                    }
                });
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void getSchListFromDatabase(int zone) {
        if (schList == null) {
            schList = new ArrayList<ScheduleData>();
        }
        schList.clear();

        schList.addAll(scheduleDataDao.queryBuilder()
                .where(ScheduleDataDao.Properties.GroupId.eq(programFlag),
                        ScheduleDataDao.Properties.Zone.eq(zone))
                .list());

        if (schList.size() > 0) {
            mDaysOfWeek.set(schList.get(0).getRepeat());
        } else {
            mDaysOfWeek.set(new DaysOfWeek(0));
        }
        repeat_summary.setText(mDaysOfWeek.toString(getActivity(), true));
    }

    private class ScheduleObserver extends ContentObserver {

        public ScheduleObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d("ScheduleObserver", "onchange");
            updateListShow();
        }
    }

    private void updateListShow() {
        getSchListFromDatabase(zoneSelected);
        schAdapter.notifyDataSetChanged();
    }

    private void getProgramInfo() {
        String strParam = NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/getmode?cno=" + DeviceBaseInfo.connuid;
        Log.d("ScheduleFragment", strParam);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ScheduleFragment", response.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("data"));
                            JSONObject jsonData;
                            DeviceBaseInfo.programInfoList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonData = jsonArray.getJSONObject(i);
                                DeviceBaseInfo.ProgramInfo proInfo = new DeviceBaseInfo.ProgramInfo();
                                proInfo.id = jsonData.getInt("id");
                                proInfo.name = jsonData.getString("name");
                                DeviceBaseInfo.programInfoList.add(proInfo);
                                if (programNameList.size() > i) {
                                    programNameList.get(i).setText(jsonData.getString("name"));
                                }
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ScheduleFragment", error.getMessage(), error);
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(Tag);
        NxecoAPP.mRequestQueue.add(jsonObjectRequest);
    }


    private void uploadSchedule() {

        int listSize = schList.size();
        if ((listSize > 0) && (!DeviceBaseInfo.connuid.equals(""))
                && (DeviceBaseInfo.programInfoList.size() > 0)) {

            StringBuilder times = new StringBuilder();
            ScheduleData sd = schList.get(0);
            int size = schList.size();
            int modeid = DeviceBaseInfo.programInfoList.get(sd.getGroupId()).id;

            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            Date date;

            int count = size;
            String strTime;


            for (int i = 0; i < size; i++) {
                try {
                    date = sdf1.parse(schList.get(i).getStart_time());
                    strTime = sdf2.format(date);
                    times.append(strTime + "-" + schList.get(i).getInterval() / 60);

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                count -= 1;
                if (count > 0) {
                    times.append(",");
                }
            }

            DaysOfWeek dow = new DaysOfWeek(sd.getRepeat());

            String strParam = NxecoAPP.mstrMainHostAddr
                    + "/api/rest/v2/device/setschedule?&cno="
                    + DeviceBaseInfo.connuid + "&zone=" + sd.getZone() + "&weeks="
                    + dow.toNumberString() + "&times=" + times.toString() + "&modeid=" + modeid;

            Log.d("ScheduleFragment", strParam);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Uri resUri;
                            ContentValues values;
                            for (int i = 0; i < schList.size(); i++) {
                                schList.get(i).setIsUpload(true);
//                                resUri = ContentUris.withAppendedId(
//                                        ScheduleProvider.URI_SCHEDULE_ID,
//                                        schList.get(i)._id);
//                                values = schList.get(i).getContentValues();
//                                getActivity().getContentResolver().update(resUri, values,
//                                        null, null);
                                scheduleDataDao.insertOrReplace(schList.get(i));

                            }
                            Toast.makeText(getActivity(), "Saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Upload server failed", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", error.getMessage(), error);
                }
            });

            jsonObjectRequest.setTag(Tag);
            NxecoAPP.mRequestQueue.add(jsonObjectRequest);
        }
    }

//    public class ZoneInfo {
//        String zonename = "";
//        String imageUrl = "";
//
//        public ZoneInfo(String zonename, String imageUrl) {
//            this.zonename = zonename;
//            this.imageUrl = imageUrl;
//        }
//    }

    void getDefaultList() {

        String data = getJson();

        for (int i = 0; i < 12; i++) {
            ZoneInfo zi = new ZoneInfo("zone " + (i + 1), "path");
            zoneArraylist.add(zi);
        }

        if ((data != null) && (!data.equals(""))) {
            getZoneInfoFromJson(data);
        }

    }

    private String getJson() {
        String strJson;

        SharedPreferences sp = getActivity().getSharedPreferences(NxecoAPP.sharedPreferencesNmae,
                Context.MODE_PRIVATE);
        strJson = sp.getString("ZoneInfo", "");
        return strJson;
    }

    private void saveJson(String json) {
        SharedPreferences sp = getActivity().getSharedPreferences(NxecoAPP.sharedPreferencesNmae,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("ZoneInfo", json);
        editor.apply();
    }

    void getZoneInfoFromJson(String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonData;
            String strImage;
            String zoneName;
            Log.d("ScheduleFragment", "jsonArray.length():" + jsonArray.length());
            if (jsonArray.length() > 0) {
                if (zoneArraylist != null) {
                    zoneArraylist.clear();
                }
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonData = jsonArray.getJSONObject(i);

                zoneName = jsonData.getString("zonename");
                strImage = jsonData.getString("image");
                if (strImage.equals("")) {
                    strImage = "path";
                }

                ZoneInfo zi = new ZoneInfo(zoneName, strImage);
                zoneArraylist.add(zi);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public void getZoneInfo() {
        String strParam = NxecoAPP.mstrMainHostAddr
                + "/api/rest/v2/device/getzone?&devid=" + DeviceBaseInfo.deviceId;
        Log.d("ScheduleFragment", strParam);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strParam, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String data = response.getString("data");
                            saveJson(data);
                            getZoneInfoFromJson(data);
                            if (adaper != null) {
                                adaper.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
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
        NxecoAPP.mRequestQueue.add(jsonObjectRequest);
    }

    OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            zoneSelected = i + 1;
            getSchListFromDatabase(zoneSelected);
            schAdapter.notifyDataSetChanged();
            zone_name.setText(zoneArraylist.get(i).zonename);
            adaper.notifyDataSetChanged();
        }
    };
}
