
#include <jni.h>


#ifndef _Included_com_nxecoii_watchdog_WatchDog
#define _Included_com_nxecoii_watchdog_WatchDog
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_com_nxecoii_watchdog_WatchDog_openWatchDog
 (JNIEnv *, jobject);

JNIEXPORT jint JNICALL Java_com_example_i2c_watchdog_WatchDog_feedDog
 (JNIEnv *, jobject);



#ifdef __cplusplus
}
#endif
#endif
