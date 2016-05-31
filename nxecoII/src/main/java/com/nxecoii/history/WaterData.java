package com.nxecoii.history;

public class WaterData {

	public double value = 0;
	public String date;
	
	private long beginTime;
	private long endTime;
	
	public void add(int value) {
		this.value += value;
	}
	
	public String getDateText() {
		String date = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date(beginTime * 1000));
		return date;
	}
	
	public String getWeekText() {
		return null;
		
	}
	
	public boolean isPeriod(int timeStamp) {
		if((timeStamp >= beginTime) && (timeStamp < endTime)) {
			return true;
		}
		else {
			return false;
		}
	}

}
