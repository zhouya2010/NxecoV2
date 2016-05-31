package com.nxecoii.history;

import java.util.ArrayList;

public class WaterStatistic {
	
	public final long dayInterval = 24 * 3600;
	public final long weekInterval = dayInterval * 7;
	
	public int countDays;
	public Type type;
	
	private ArrayList<WaterData> wd = new ArrayList<WaterData>();
	
	public enum Type{
		DailyType,
		WeekType,
		MonthType
	}
	
	public WaterStatistic(int countDays, Type type) {
		this.countDays = countDays;
		this.type = type;
	}
	
	public WaterStatistic() {
		//Default statistics for 60 days
		this.countDays = 60;
		//Default statistics by daily
		this.type = Type.DailyType;
	}
	
	public ArrayList<WaterData> getDaliyStatistic() {
		
		ArrayList<WaterData> wd = new ArrayList<WaterData>();
		
		
		
		return null;
		
	}
}
