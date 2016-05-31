package com.nxecoii.activity.child.settings;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nxecoii.R;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class WifiSetFragment extends Fragment {

    private ListView wifi_lv;
    private TextView wifi_info_text;
    private WifiManager mWifiManager;
    private List<WifiConfiguration> wifiConfigList;
    private IntentFilter mIntentFilter;
    private List<ScanResult> wifiResultList;
    private WifiListAdapter arrayWifiAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_wifi_set, container, false);

        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifi_lv = (ListView) view.findViewById(R.id.wifi_list);
        wifi_info_text = (TextView) view.findViewById(R.id.wifi_info_text);

        mIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getActivity().registerReceiver(mReceiver, mIntentFilter);

        wifiConfigList = mWifiManager.getConfiguredNetworks();

        if (wifiResultList == null) {
            wifiResultList = new ArrayList<ScanResult>();
        }

        arrayWifiAdapter = new WifiListAdapter(getActivity(), wifiResultList, mWifiManager);
        wifi_lv.setAdapter(arrayWifiAdapter);

        ListOnItemClickListener wifiListListener = new ListOnItemClickListener();
        wifi_lv.setOnItemClickListener(wifiListListener);

		ToggleButton wifi_enble = (ToggleButton) view.findViewById(R.id.wifi_enable);
		wifi_enble.toggle();
	    //切换无动画
        wifi_enble.toggle(false);

        int wifiApState = mWifiManager.getWifiState();
        Log.d("WifiSettings", "wifiApState:" + wifiApState);
        if (wifiApState == WifiManager.WIFI_STATE_ENABLED || wifiApState == WifiManager.WIFI_STATE_ENABLING) {
            wifi_enble.setToggleOn();
            wifi_lv.setVisibility(View.VISIBLE);
            List<ScanResult> scanResults = mWifiManager.getScanResults();
            if (wifiResultList == null) {
                wifiResultList = new ArrayList<ScanResult>();
            }
            wifiResultList.clear();
            wifiResultList.addAll(scanResults);

            arrayWifiAdapter.notifyDataSetChanged();
        } else {
            wifi_enble.setToggleOff();
            wifi_lv.setVisibility(View.GONE);
            wifi_info_text.setVisibility(View.VISIBLE);
        }

		wifi_enble.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
			
			@Override
			public void onToggle(boolean isChecked) {
                if (isChecked) {
                    if (!mWifiManager.isWifiEnabled()) {
                        mWifiManager.setWifiEnabled(true);
                        mWifiManager.startScan();
                    }
                } else {
                    if (mWifiManager.isWifiEnabled())
                        mWifiManager.setWifiEnabled(false);
                }
            
			}
		});

		return view;
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		if(mReceiver != null) {
			getActivity().unregisterReceiver(mReceiver);
		}
		
	}



    class ListOnItemClickListener implements AdapterView.OnItemClickListener {
        private String wifiPassword;

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {


            final ScanResult sr = (ScanResult) arrayWifiAdapter.getItem(arg2);

            String wifiItem = sr.SSID;// 获得选中的设备
            Log.d("ListOnItemClickListener", wifiItem);
            int wifiItemId  = IsConfiguration(sr.SSID);
            Log.d("ListOnItemClickListener", "wifiItemId:" + wifiItemId);
            if (wifiItemId > -1) {
                mWifiManager.enableNetwork(wifiItemId, true);
                arrayWifiAdapter.notifyDataSetChanged();
            }
            else {
                WifiPswDialog pswDialog = new WifiPswDialog(getActivity(),
                        new WifiPswDialog.OnCustomDialogListener() {
                            @Override
                            public void back(String str) {
                                wifiPassword = str;
                                if (wifiPassword != null) {

                                    int netId = addNetWorks(sr.SSID, wifiPassword);
                                    Log.d("ListOnItemClickListener", "netId:" + netId);
                                    if (netId > -1) {
                                        wifiConfigList = mWifiManager.getConfiguredNetworks();
                                        if (wifiConfigList != null) {
                                            for(WifiConfiguration wc : wifiConfigList) {
                                                Log.d("ListOnItemClickListener", "WifiConfiguration----" +wc.SSID);
                                            }
                                            mWifiManager.enableNetwork(wifiConfigList.get(0).networkId, true);
                                            mWifiManager.saveConfiguration();
                                        }
                                        arrayWifiAdapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(getActivity(), "Connect error!", Toast.LENGTH_SHORT).show();
                                        arrayWifiAdapter.notifyDataSetChanged();
                                    }
                                } else {

                                }
                            }
                        });
                pswDialog.show();
            }
        }
    }

    public class WifiListAdapter extends BaseAdapter {

        List<ScanResult> listScan;
        WifiManager mWifiManager;
        private LayoutInflater mInflater;

        public WifiListAdapter(Context context, List<ScanResult> listScan, WifiManager mWifiManager) {
            this.listScan = listScan;
            this.mWifiManager = mWifiManager;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub

            if (listScan != null) {
                return listScan.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return listScan.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.wifi_lsit_item, null);
                holder = new ViewHolder();
                holder.ssid = (TextView) convertView
                        .findViewById(R.id.ssid);
                holder.connect_state = (TextView) convertView
                        .findViewById(R.id.connect_state);
                holder.wifi_icon = (ImageView) convertView
                        .findViewById(R.id.wifi_icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ScanResult sr = listScan.get(position);
            holder.ssid.setText(sr.SSID);
//			if(listScan.get(position).SSID.equals(localWifiUtils.getConnectedSSID())) {
            if (mWifiManager.getConnectionInfo() != null) {

                if (mWifiManager.getConnectionInfo().getSSID() != null) {

                }

                if ((mWifiManager.getConnectionInfo().getSSID() != null) &&(mWifiManager.getConnectionInfo().getSSID().contains(sr.SSID))) {
                    holder.connect_state.setVisibility(View.VISIBLE);
                    holder.connect_state.setText("Connected");
                } else {
                    holder.connect_state.setVisibility(View.GONE);
                }
            }


            holder.wifi_icon.getDrawable().setLevel(Math.abs(listScan.get(position).level));
            return convertView;
        }

        class ViewHolder {
            private TextView ssid;
            private TextView connect_state;
            private ImageView wifi_icon;
        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action) ||
                    WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action) ||
                    WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                updateAccessPoints();
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {

                List<ScanResult> scanResults = mWifiManager.getScanResults();
                if (wifiResultList == null) {
                    wifiResultList = new ArrayList<ScanResult>();
                }
                wifiResultList.clear();
                wifiResultList.addAll(scanResults);

                arrayWifiAdapter.notifyDataSetChanged();
            }
        }
    };


    int addNetWorks(String ssid, String pwd) {
        WifiConfiguration wifiCong = new WifiConfiguration();
        wifiCong.SSID = "\"" + ssid + "\"";
        wifiCong.preSharedKey = "\"" + pwd + "\"";
        wifiCong.hiddenSSID = false;
        wifiCong.status = WifiConfiguration.Status.ENABLED;
        return mWifiManager.addNetwork(wifiCong);
    }


    public int IsConfiguration(String SSID){
        if (wifiConfigList == null) {
            wifiConfigList = mWifiManager.getConfiguredNetworks();
        }

        if (wifiConfigList != null) {
            for(int i = 0; i < wifiConfigList.size(); i++){
                if(wifiConfigList.get(i).SSID.contains(SSID)){
                    return wifiConfigList.get(i).networkId;
                }
            }
        }
        return -1;
    }

    private void updateAccessPoints() {
        // Safeguard from some delayed event handling
        if (getActivity() == null) return;

        final int wifiState = mWifiManager.getWifiState();

        switch (wifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
                wifi_lv.setVisibility(View.VISIBLE);
                wifi_info_text.setVisibility(View.GONE);
                arrayWifiAdapter.notifyDataSetChanged();
                break;

            case WifiManager.WIFI_STATE_ENABLING:
                wifi_info_text.setText(getString(R.string.wifi_empty_list_wifi_on));
                break;

            case WifiManager.WIFI_STATE_DISABLING:
                wifi_lv.setVisibility(View.GONE);
                wifi_info_text.setVisibility(View.VISIBLE);
                wifi_info_text.setText(getString(R.string.wifi_stopping));
                break;

            case WifiManager.WIFI_STATE_DISABLED:
                wifi_info_text.setText(getString(R.string.wifi_empty_list_wifi_off));
                break;
        }
    }
}
