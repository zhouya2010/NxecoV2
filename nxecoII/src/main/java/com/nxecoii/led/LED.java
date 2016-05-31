package com.nxecoii.led;

public class LED {
	
	public static int red = 0;
	public static int green = 1;
	public static int blue = 2;
	
	public native int  openDevice();
	/*
	 * fun GpioControl()
	 * @arg led
	 *      led: 0  red
	 *      led: 1  green
	 *      led: 2  blue
	 *  @arg cmd 
	 *       cmd : 1  open
	 *       cmd : 0  close 
	 *  return 
	 *          0 success
	 *          -1 fail  
	 * 
	 * */
	public native int  ledControl(int led,int cmd);
	/*
	 * fun gpio_twinkling()
	 * @arg led
	 *      led: 0  red
	 *      led: 1  green
	 *      led: 2  blue
	 *  @arg second 
	 *       second : 1  0.1 miao
	 *  @arg times 
	 *        times :1 1chi
	 *  
	 *  
	 *  return 
	 *          0 success
	 *          -1 fail  
	 * 
	 * */
	public native int  ledTwinkling(int led,int second,int times);
	/*
	 * fun gpio_twinkling()
	 * @arg led
	 *      led: 0  red
	 *      led: 1  green
	 *      led: 2  blue
	 * @arg second
	 * 	100ms
	 * 
	 *  @arg times
	 *       
	 *  return 
	 *          0 success
	 *          -1 fail  
	 * 
	 * */
	//public native int  ledStopTwinkling(int led);
	public native int  closeDevice();

//	 static
//		{
//			System.loadLibrary("gpio-interface");
//		}
}
