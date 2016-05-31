package com.nxecoii.activity.child;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nxecoii.R;
import com.nxecoii.device.DeviceBaseInfo;
import com.nxecoii.history.WaterData;
import com.nxecoii.http.NxecoAPP;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryLogFragment extends Fragment {

    private Context mContext;

	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	private GraphicalView mChartView = null;
	private XYSeries mCurrentSeries;
	private ArrayList<WaterData> wdList = new ArrayList<WaterData>();
	private double max = 5;

	private final String Tag = "HistoryLogFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history, container, false);

		this.mContext = getActivity();

		mRenderer = getBarRenderer();
		mDataset = getBarDataset();

		getWaterLogDay();


		LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.water_chart);
		mChartView = ChartFactory.getBarChartView(mContext, mDataset,
				mRenderer, Type.DEFAULT);
		layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		String waterJson = getJson();
		if (!waterJson.equals("")) {
			wdList = getWaterList(waterJson);
			addDataToRenderer(wdList);
		}

		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		NxecoAPP.mRequestQueue.cancelAll(Tag);
	}

	private XYMultipleSeriesDataset getBarDataset() {

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		XYSeries series = new XYSeries("Water/mins"); // 声明一个柱形图
		mCurrentSeries = series;

		dataset.addSeries(series);// 添加该柱形图到数据设置列表

		return dataset;

	}

	public XYMultipleSeriesRenderer getBarRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		// 通过SimpleSeriesDenderer设置描绘器的颜色

		// r.setColor(Color.rgb(106, 198, 240));
		r.setColor(Color.argb(122, 255, 255, 255));
		r.setDisplayChartValues(true);
		r.setChartValuesTextSize(15);
		r.setChartValuesTextAlign(Align.RIGHT);
		r.setGradientEnabled(true);
		r.setGradientStart(0, Color.argb(76, 255, 255, 255));
		// r.setGradientStop(max, Color.argb(255,255, 255, 255));
		renderer.addSeriesRenderer(r);
		renderer.setChartTitle("Water consumption within 30 days");// 设置柱图名称
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.argb(0, 0xff, 0, 0));
		renderer.setMarginsColor(Color.argb(0, 45, 97, 38));
		renderer.setMargins(new int[] { 60, 0, 10, 0 });

		// renderer.setXTitle("Date");// 设置X轴名称
		renderer.setChartTitleTextSize(30);// 字体大小
		// renderer.setYTitle("Mintue(s)");// 设置Y轴名称
		renderer.setXLabelsColor(Color.WHITE);
		// renderer.setYLabelsColor(0, Color.rgb(60, 60, 60));
		renderer.setLabelsTextSize(20);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.CENTER);
		// renderer.setXLabelsAngle(-45f);
		// renderer.setFitLegend(true);

		renderer.setXAxisMin(0.5);// 设置X轴的最小值为0.5
		renderer.setXAxisMax(9.5);// 设置X轴的最大值为5
		renderer.setYAxisMin(0);// 设置Y轴的最小值为0
		// renderer.setYAxisMax(100);//设置Y轴最大值为500

		renderer.setShowGrid(true);// 设置是否在图表中显示网格
		renderer.setXLabels(0);// 设置X轴显示的刻度标签的个数
		renderer.setYLabels(5);
		renderer.setYLabelsColor(0, Color.argb(0, 255, 255, 255));
		renderer.setBarSpacing(0.5f);
		renderer.setZoomButtonsVisible(false);
		renderer.setZoomEnabled(false, false);// 设置渲染器允许放大缩小
		renderer.setAntialiasing(true);// 消除锯齿
		renderer.setPanEnabled(true, false);// 允许左右拖动,但不允许上下拖动.

		return renderer;
	}

	private ArrayList<WaterData> getWaterList(String waterJson) {
		ArrayList<WaterData> wdList = new ArrayList<WaterData>();
		String strError;

		try {

			JSONObject allData = new JSONObject(waterJson);
			strError = allData.getString("error");
			if (strError.equals("200")) {
				JSONArray jsWaterDataArray = new JSONArray(
						allData.getString("data"));

                String strDate;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date;

                for (int i = 0; i < jsWaterDataArray.length(); i++) {
                    JSONObject objWaterData = jsWaterDataArray
                            .getJSONObject(i);
                    WaterData wd = new WaterData();

                    strDate = objWaterData.getString("day");
                    date = sdf.parse(strDate);
                    wd.date = new SimpleDateFormat("MM-dd").format(date);

                    wd.value = objWaterData.getDouble("val");

                    wdList.add(wd);
                }
            } else {
				NxecoAPP.ShowToast(strError);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			NxecoAPP.ShowToast("Failed to get data!");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			NxecoAPP.ShowToast("Failed to get data!");
		}

		return wdList;
	}

	private void addDataToRenderer(ArrayList<WaterData> wdList) {
		int size = wdList.size();
		double value;
		if (size > 0) {
			mCurrentSeries.clear();
			mRenderer.clearXTextLabels();
			for (int i = 0; i < size; i++) {
				value = wdList.get(i).value;
				mCurrentSeries.add(i + 1, value);
				mRenderer.addXTextLabel(i + 1, wdList.get(i).date);
				if (value > max) {
					max = value;
				}
			}
			mRenderer.setYAxisMax(max + 5);
			r.setGradientStop(max, Color.argb(255, 255, 255, 255));
		} else {
			NxecoAPP.ShowToast("No data!");
		}
	}

	private String getJson() {
		String strJson;

		SharedPreferences sp = mContext.getSharedPreferences(NxecoAPP.sharedPreferencesNmae,
				Context.MODE_PRIVATE);
		strJson = sp.getString("WaterLog", "");
		return strJson;
	}

	private void saveJson(String json) {
		SharedPreferences sp = mContext.getSharedPreferences(NxecoAPP.sharedPreferencesNmae,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("WaterLog", json);
		editor.apply();
	}


	private void  getWaterLogDay(){
		String strParam = NxecoAPP.mstrMainHostAddr + "/api/rest/v2/device/getwaterlogday";
		Uri.Builder builder = Uri.parse(strParam).buildUpon();
		builder.appendQueryParameter("cno", DeviceBaseInfo.getConnuid());
		Log.d("HistoryLogFragment", builder.toString());

		StringRequest request = new StringRequest(builder.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
				saveJson(response);
				wdList = getWaterList(response);
				if (wdList.size() > 0) {
					addDataToRenderer(wdList);
					mChartView.repaint();
					Toast.makeText(mContext, "Updata sucessfully！",
							Toast.LENGTH_SHORT).show();
				}
			}
		}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
				Log.e("HistoryLogFragment", error.getMessage(), error);
            }
        });
		request.setTag(Tag);
		NxecoAPP.mRequestQueue.add(request);
	}
}
