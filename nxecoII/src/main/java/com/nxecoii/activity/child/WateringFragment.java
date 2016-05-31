package com.nxecoii.activity.child;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nxecoii.R;
import com.nxecoii.adapter.CommonAdapter;
import com.nxecoii.adapter.MyNumberPicker;
import com.nxecoii.adapter.SprayListAdapter;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.device.ZoneInfo;
import com.nxecoii.greendao.SprayDetails;
import com.nxecoii.greendao.SprayDetailsDao;
import com.nxecoii.http.NxecoAPP;
import com.nxecoii.schedule.SprayService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WateringFragment extends Fragment implements OnTouchListener, Formatter, OnClickListener {

    /**
     * 滚动显示和隐藏menu时，手指滑动需要达到的速度。
     */
    public static final int SNAP_VELOCITY = 200;

    /**
     * 屏幕宽度值。
     */
    private int screenWidth = 648;

    /**
     * menu最多可以滑动到的左边缘。值由menu布局的宽度来定，marginLeft到达此值之后，不能再减少。
     */
    private int leftEdge;

    /**
     * menu最多可以滑动到的右边缘。值恒为0，即marginLeft到达0之后，不能增加。
     */
    private int rightEdge = 0;

    /**
     * menu完全显示时，留给content的宽度值。
     */
    private int menuPadding = 35;

    /**
     * 主内容的布局。
     */
    private View content;

    /**
     * menu的布局。
     */
    private View menu;

    /**
     * menu布局的参数，通过此参数来更改leftMargin的值。
     */
    private LinearLayout.LayoutParams menuParams;

    /**
     * 记录手指按下时的横坐标。
     */
    private float xDown;

    /**
     * 记录手指移动时的横坐标。
     */
    private float xMove;

    /**
     * 记录手机抬起时的横坐标。
     */
    private float xUp;

    /**
     * menu当前是显示还是隐藏。只有完全显示或隐藏menu时才会更改此值，滑动过程中此值无效。
     */
    private boolean isMenuVisible;

    /**
     * 用于计算手指滑动的速度。
     */
    private VelocityTracker mVelocityTracker;

    private LayoutInflater inflates;
    private Context mcontext;
    private Animation anim;
    private ImageView iamge;

    private CheckBox btnPull;
    private TextView zones_btn;
    private TextView alread_finish_timer;
    private TextView total_timer;
    private TextView remainingTime;
//    private TextView timeUnit;

    private TextView btnCancel;
    private TextView btnOk;
    private TextView wateringLeftNum;
    private TextView add_watering_text;
    private LinearLayout add_queue_background;

    private CheckBox box;
    private TextView selectZoneSpinner;
    private int selectItem;
    List<ZoneInfo> zoneList;
    private List<SprayDetails> sprayList = null;
    AlertDialog ad;

    private MyNumberPicker hourPicker;
    private MyNumberPicker minPicker;

    private SprayListAdapter wateringAdapter;
    private ListView scheduleList;
    private View view;

    private SprayDetailsDao sprayDetailsDao;
    private Observer observer;

    private static final String Tag = "WateringFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.my_watering, container, false);
        this.inflates = inflater;
        this.mcontext = getActivity();

        observer = new Observer(new Handler());
        mcontext.getContentResolver().registerContentObserver(SprayService.URI_CONTENT_SPRAY, true, observer);
        sprayDetailsDao = NxecoAPP.getDaoSession().getSprayDetailsDao();

        btnPull = (CheckBox) view.findViewById(R.id.btnStart);
        btnCancel = (TextView) view.findViewById(R.id.btnCancel);
        btnOk = (TextView) view.findViewById(R.id.btnOk);
        add_watering_text = (TextView) view.findViewById(R.id.add_watering_text);
        add_queue_background = (LinearLayout) view.findViewById(R.id.add_queue_background);

        btnPull.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        iamge = (ImageView) view.findViewById(R.id.prograssbar);
        box = (CheckBox) view.findViewById(R.id.chekBox);

        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.circle);
        LinearInterpolator lin = new LinearInterpolator();
        anim.setInterpolator(lin);

        zoneList = new ArrayList<ZoneInfo>();
        getDefaultList();

        scheduleList = (ListView) view
                .findViewById(R.id.wait_schedule);
        sprayList = new ArrayList<SprayDetails>();
        sprayList.addAll(sprayDetailsDao.loadAll());
        wateringAdapter = new SprayListAdapter(mcontext,
                sprayList, zoneList);
        scheduleList.setAdapter(wateringAdapter);
        wateringLeftNum = (TextView) view.findViewById(R.id.waterin_left_num);
        wateringLeftNum.setText("" + sprayDetailsDao.count());

        box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                Log.d("WateringFragment", "setOnCheckedChangeListener");
                if (arg1) {
                    if (sprayList.size() > 0) {
                        sprayList.get(0).setIsSpraying(true);
                        sprayDetailsDao.insertOrReplace(sprayList.get(0));
                        getActivity().getContentResolver().notifyChange(SprayService.URI_SPRAY_RESTART, null);
                        startAnimation(iamge);
                    }
                } else {
                    if (sprayList.size() > 0) {
                        sprayList.get(0).setIsSpraying(false);
                        sprayDetailsDao.insertOrReplace(sprayList.get(0));
                        getActivity().getContentResolver().notifyChange(SprayService.URI_SPRAY_PAUSE, null);
                        endAnimation(iamge);
                    }

                }
            }
        });

        selectZoneSpinner = (TextView) view.findViewById(R.id.zones_select);
        if(zoneList.size() > 0) {
            selectZoneSpinner.setText(zoneList.get(0).zonename);
        }
        else {
            getZoneInfo();
            selectZoneSpinner.setText("Not get zone");
        }

        selectZoneSpinner.setOnClickListener(this);
        selectItem = 0;

        hourPicker = (MyNumberPicker) view
                .findViewById(R.id.time_picker_hour);
        minPicker = (MyNumberPicker) view.findViewById(R.id.time_picker_min);
        numberPickerInit();

        zones_btn = (TextView) view.findViewById(R.id.zones_btn);
        alread_finish_timer = (TextView) view.findViewById(R.id.alread_finish_timer);
        total_timer = (TextView) view.findViewById(R.id.total_timer);
        remainingTime = (TextView) view.findViewById(R.id.remainingTime);
//        timeUnit = (TextView) view.findViewById(R.id.timeUnit);

        initValues();
        content.setOnTouchListener(this);

        return view;
    }


    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
        NxecoAPP.mRequestQueue.cancelAll(Tag);
        getActivity().getContentResolver().unregisterContentObserver(
                observer);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        System.out.println("#########Mywatering fragment  onSaveInstanceState");

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时，记录按下时的横坐标
                xDown = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                // 手指移动时，对比按下时的横坐标，计算出移动的距离，来调整menu的leftMargin值，从而显示和隐藏menu
                xMove = event.getRawX();
                int distanceX = (int) (xMove - xDown);
                if (isMenuVisible) {
                    menuParams.leftMargin = distanceX;
                } else {
                    menuParams.leftMargin = leftEdge + distanceX;
                }
                if (menuParams.leftMargin < leftEdge) {
                    menuParams.leftMargin = leftEdge;
                } else if (menuParams.leftMargin > rightEdge) {
                    menuParams.leftMargin = rightEdge;
                }
                menu.setLayoutParams(menuParams);
                break;
            case MotionEvent.ACTION_UP:
                // 手指抬起时，进行判断当前手势的意图，从而决定是滚动到menu界面，还是滚动到content界面
                xUp = event.getRawX();
                if (wantToShowMenu()) {
                    if (shouldScrollToMenu()) {
                        scrollToMenu();
                    } else {
                        scrollToContent();
                    }
                } else if (wantToShowContent()) {
                    if (shouldScrollToContent()) {
                        scrollToContent();
                    } else {
                        scrollToMenu();
                    }
                }
                recycleVelocityTracker();
                break;
        }
        return true;
    }

    /**
     * 初始化一些关键性数据。包括获取屏幕的宽度，给content布局重新设置宽度，给menu布局重新设置宽度和偏移距离等。
     */
    private void initValues() {
        content = view.findViewById(R.id.content);
        menu = view.findViewById(R.id.menu);
        menuParams = (LinearLayout.LayoutParams) menu.getLayoutParams();
        menuParams.width = screenWidth - menuPadding;
        leftEdge = -menuParams.width;
        content.getLayoutParams().width = screenWidth;

        menuParams.leftMargin = 0;
        isMenuVisible = true;

        if (sprayList.size() > 0) {
            box.setEnabled(true);
            if (sprayList.get(0).getIsSpraying()) {
                box.setChecked(true);
                startAnimation(iamge);
            }
            btnPull.setChecked(false);
            add_watering_text.setText("       Add a watering");
            add_queue_background.setBackgroundColor(0x00ffffff);
        } else {
            box.setEnabled(false);
            btnPull.setChecked(true);
            new ScrollTask().execute(-20);
        }
    }

    /**
     * 判断当前手势的意图是不是想显示content。如果手指移动的距离是负数，且当前menu是可见的，则认为当前手势是想要显示content。
     *
     * @return 当前手势想显示content返回true，否则返回false。
     */
    private boolean wantToShowContent() {
        return xUp - xDown < 0 && isMenuVisible;
    }

    /**
     * 判断当前手势的意图是不是想显示menu。如果手指移动的距离是正数，且当前menu是不可见的，则认为当前手势是想要显示menu。
     *
     * @return 当前手势想显示menu返回true，否则返回false。
     */
    private boolean wantToShowMenu() {
        return xUp - xDown > 0 && !isMenuVisible;
    }

    /**
     * 判断是否应该滚动将menu展示出来。如果手指移动距离大于屏幕的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该滚动将menu展示出来。
     *
     * @return 如果应该滚动将menu展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToMenu() {
        return xUp - xDown > screenWidth / 2
                || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 判断是否应该滚动将content展示出来。如果手指移动距离加上menuPadding大于屏幕的1/2，
     * 或者手指移动速度大于SNAP_VELOCITY， 就认为应该滚动将content展示出来。
     *
     * @return 如果应该滚动将content展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToContent() {
        return xDown - xUp + menuPadding > screenWidth / 2
                || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 将屏幕滚动到menu界面，滚动速度设定为30.
     */
    private void scrollToMenu() {
        btnPull.setChecked(false);
        add_watering_text.setText("       Add a watering");
        add_queue_background.setBackgroundColor(0x00ffffff);
        new ScrollTask().execute(30);
    }

    /**
     * 将屏幕滚动到content界面，滚动速度设定为-30.
     */
    private void scrollToContent() {
        btnPull.setChecked(true);
        add_watering_text.setText("Add a watering");
        add_queue_background.setBackgroundResource(R.drawable.add_queue_bg);
        new ScrollTask().execute(-30);
    }

    /**
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event content界面的滑动事件
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 获取手指在content界面滑动的速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    class ScrollTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int leftMargin = menuParams.leftMargin;
            // 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环。
            while (true) {
                leftMargin = leftMargin + speed[0];
                if (leftMargin > rightEdge) {
                    leftMargin = rightEdge;
                    break;
                }
                if (leftMargin < leftEdge) {
                    leftMargin = leftEdge;
                    break;
                }
                publishProgress(leftMargin);
                // 为了要有滚动效果产生，每次循环使线程睡眠20毫秒，这样肉眼才能够看到滚动动画。
                sleep(20);
            }
            if (speed[0] > 0) {
                isMenuVisible = true;
            } else {
                isMenuVisible = false;
            }
            return leftMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... leftMargin) {
            menuParams.leftMargin = leftMargin[0];
            menu.setLayoutParams(menuParams);
        }

        @Override
        protected void onPostExecute(Integer leftMargin) {
            menuParams.leftMargin = leftMargin;
            menu.setLayoutParams(menuParams);
        }
    }

    /**
     * 使当前线程睡眠指定的毫秒数。
     *
     * @param millis 指定当前线程睡眠多久，以毫秒为单位
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startAnimation(ImageView view) {
        view.startAnimation(anim);
    }

    public void endAnimation(ImageView view) {
        view.clearAnimation();
    }

    @Override
    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    void numberPickerInit() {
        hourPicker.setMaxValue(23);
        hourPicker.setMinValue(0);
        minPicker.setMaxValue(59);
        minPicker.setMinValue(0);
        hourPicker.setFormatter(this);
        minPicker.setFormatter(this);
        minPicker.setValue(1);
        setNumberPickerDividerColor(hourPicker);
        setNumberPickerDividerColor(minPicker);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnStart:
                if (isMenuVisible) {
                    scrollToContent();
                } else {
                    scrollToMenu();
                }
                break;

            case R.id.btnCancel:
                hourPicker.setValue(0);
                minPicker.setValue(1);
                scrollToMenu();
                break;

            case R.id.btnOk:

                int setTime = (hourPicker.getValue() * 60 + minPicker.getValue()) * 60;

                if (setTime > 0) {
                    SprayDetails sd = new SprayDetails();

                    sd.setZone(selectItem + 1);
                    sd.setIntervalOrigin((hourPicker.getValue() * 60 + minPicker.getValue()) * 60);
                    sd.setIntervalActual(0);
                    sd.setStartTimeTheory(System.currentTimeMillis() / 1000);
                    sd.setAdjust(100);
                    sd.setIsSpraying(true);
                    sd.setIsUpload(false);
                    sd.setSprayType(SprayDetails.sprayTypeManual);
                    sd.setAdd_time(new Date());

                    sprayDetailsDao.insertOrReplace(sd);
                    Log.d("WateringFragment", "URI_SPRAY_LIST_CHANGE");
                    getActivity().getContentResolver().notifyChange(SprayService.URI_SPRAY_LIST_CHANGE, null);
                    scrollToMenu();
                } else {
                    Toast.makeText(mcontext, "Please set time duration.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.zones_select:
                Log.d("WateringFragment", "click zone_select");
                showDialog();
                break;

            default:
                break;
        }
    }

    void showDialog() {

        ListView listView;
        CommonAdapter adaper;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.watering_zone_select, null);
        listView = (ListView) view.findViewById(R.id.zone_list_view);

        adaper = new CommonAdapter<ZoneInfo>(getActivity(), zoneList, R.layout.zone_list_item) {
            @Override
            public void convert(ViewHolder holder, ZoneInfo item, int position) {
                holder.setText(R.id.zone_name, item.zonename);
                holder.setImage(R.id.zone_image, item.imageUrl, null);
            }
        };

        listView.setAdapter(adaper);

        listView.setOnItemClickListener(listener);

        ad = new AlertDialog.Builder(getActivity())
                .setTitle("Selet zone")
                .setView(view)
                .create();
        ad.show();

        Window dialogWindow = ad.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
        lp.width = (int) (d.getWidth() * 0.45); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(lp);
    }


    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            System.out.println("click ------>" + position);
            System.out.println(zoneList.get(position).imageUrl);
            selectZoneSpinner.setText(zoneList.get(position).zonename);
            selectItem = position;
//		Picasso.with(getActivity()).load(zoneList.get(position).imageUrl).error(R.drawable.default_zone).placeholder(R.drawable.default_zone).resize(109, 76).into(img, null);
            ad.dismiss();
        }
    };


    void getDefaultList() {
        String data = getJson();

        for (int i = 0; i < 12; i++) {
            ZoneInfo zi = new ZoneInfo("zone " + (i + 1), "path");
            zoneList.add(zi);
        }

        if ((data != null) && (!data.equals(""))) {
            Log.d("WateringFragment", data);
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

    void getZoneInfoFromJson(String data) {
        if (zoneList != null) {
            zoneList.clear();

            try {
                JSONArray jsonArray = new JSONArray(data);
                JSONObject jsonData;
                String strImage;
                String zoneName;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonData = jsonArray.getJSONObject(i);

                    zoneName = jsonData.getString("zonename");
                    strImage = jsonData.getString("image");
                    if (strImage.equals("") || strImage == null) {
                        strImage = "path";
                    }

                    ZoneInfo zi = new ZoneInfo(zoneName, strImage);
                    zoneList.add(zi);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setNumberPickerDividerColor(NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, new ColorDrawable(this.getResources().getColor(R.color.white_trans_30)));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private class Observer extends ContentObserver {
        public Observer(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(SprayService.URI_SPRAY_LIST_CHANGE)) {
                if (sprayList == null) {
                    sprayList = new ArrayList<SprayDetails>();
                } else {
                    sprayList.clear();
                }
                sprayList.addAll(sprayDetailsDao.loadAll());
                if (sprayList.size() > 0) {
                    if (sprayList.get(0).getIsSpraying()) {
                        box.setChecked(true);
                        box.setEnabled(true);
                        startAnimation(iamge);
                    }
                    scrollToMenu();
                } else {
                    box.setChecked(false);
                    box.setEnabled(false);
                    endAnimation(iamge);
                    scrollToContent();
                    zones_btn.setText("zone ");
                    alread_finish_timer.setText("0");
                    total_timer.setText("0");
                    remainingTime.setText("00:00:00");
                }
                wateringLeftNum.setText("" + sprayList.size());
            } else if (uri.equals(SprayService.URI_SPRAY_UPDATE_TIME)) {
                if ((sprayList != null) && (sprayList.size() > 0)) {
                    SprayDetails sd = sprayList.get(0);
                    zones_btn.setText("zone " + sd.getZone());
                    alread_finish_timer.setText("" + ((sd.getIntervalActual() - 1) / 60));
                    total_timer.setText("" + sd.getIntervalOrigin() / 60);
                    remainingTime.setText(getActivity().getString(R.string.H_M_S, sd.getRemainTime() / 3600,
                            (sd.getRemainTime() % 3600) / 60, sd.getRemainTime() % 60));
                }
                else {
                    if (zones_btn != null) {
                        zones_btn.setText(" ");
                        alread_finish_timer.setText("");
                        total_timer.setText("");
                        remainingTime.setText("");
                    }
                }
            }
            if (wateringAdapter != null){
                wateringAdapter.notifyDataSetChanged();
            }
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

    private void saveJson(String json) {
        SharedPreferences sp = getActivity().getSharedPreferences(NxecoAPP.sharedPreferencesNmae,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("ZoneInfo", json);
        editor.apply();
    }
}
