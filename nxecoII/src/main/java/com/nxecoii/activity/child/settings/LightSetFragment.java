package com.nxecoii.activity.child.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.nxecoii.R;

public class LightSetFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_light_set, container, false);
		
		SeekBar seekBar = (SeekBar)view.findViewById(R.id.seek_bar);
		seekBar.setMax(255);
		
		int normal = Settings.System.getInt(getActivity().getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS, 255);
        //进度条绑定当前亮度
        seekBar.setProgress(normal);
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
			int tmpInt = seekBar.getProgress();
			
			//当进度小于80时，设置成80，防止太黑看不见的后果。
			if (tmpInt < 80) {
				tmpInt = 80;
			}
			//根据当前进度改变亮度
			Settings.System.putInt(getActivity().getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, tmpInt);
            }
        });
        
		return view;
	}
}
