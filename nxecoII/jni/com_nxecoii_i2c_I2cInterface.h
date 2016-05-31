
#include <jni.h>


#ifndef _Included_com_nxecoii_i2c_I2cInterface
#define _Included_com_nxecoii_i2c_I2cInterface
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_com_nxecoii_i2c_I2cInterfaceTest_detectI2C
  (JNIEnv *, jobject);

JNIEXPORT jint JNICALL Java_com_nxecoii_i2c_I2cInterfaceTest_readI2C
  (JNIEnv *, jclass,jstring,jstring);
JNIEXPORT jint JNICALL Java_com_nxecoii_i2c_I2cInterfaceTest_writeI2C
  (JNIEnv *, jclass,jstring,jstring,jstring);


#ifdef __cplusplus
}
#endif
#endif
