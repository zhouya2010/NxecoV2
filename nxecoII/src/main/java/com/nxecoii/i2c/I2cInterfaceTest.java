package com.nxecoii.i2c;

public class I2cInterfaceTest {

    public native int readI2C(String chipAddress, String registerAddress);

    public native int writeI2C(String chipAddress, String registerAddress, String value);

    public native byte[] detectI2C();

//    static {
//        System.loadLibrary("i2c_wd-interface");
//    }
}
