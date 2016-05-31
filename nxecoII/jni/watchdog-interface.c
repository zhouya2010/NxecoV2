/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <sys/time.h>
#include <unistd.h>
#include <time.h>
#include <getopt.h>
#include <termios.h>
#include "com_nxecoii_watchdog_WatchDog.h"

#include <stdio.h>
#include <linux/types.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <assert.h>
#include <string.h>
#include <android/log.h>
  int fd =0;


#define WATCHDOG_IOCTL_BASE 'W'
#define WDIOC_GETSUPPORT _IOR(WATCHDOG_IOCTL_BASE, 0, struct watchdog_info)
#define WDIOC_SETTIMEOUT _IOWR(WATCHDOG_IOCTL_BASE, 6, int)
#define WDIOC_GETTIMEOUT _IOR(WATCHDOG_IOCTL_BASE, 7, int) 27
#define WDIOS_DISABLECARD 0x0001        /* Turn off the watchdog timer */
#define WDIOS_ENABLECARD 0x0002 /* Turn on the watchdog timer */
#define WDIOC_SETOPTIONS _IOR(WATCHDOG_IOCTL_BASE, 4, int)
#define WDIOC_KEEPALIVE _IOR(WATCHDOG_IOCTL_BASE, 5, int)
#define WATCHDOG_IOCTL_BASE 'W'
#define WDIOC_KEEPALIVE _IOR(WATCHDOG_IOCTL_BASE, 5, int)
 jint
 Java_com_nxecoii_watchdog_WatchDog_openWatchDog(JNIEnv* env,
  		jobject thiz) {

	 int feedtime;
          fd = open("/dev/watchdog", O_WRONLY);
             int ret = 0;
             if (fd == -1) {
                 perror("watchdog");
                 exit(EXIT_FAILURE);
             }

             feedtime=15;
             ioctl(fd,WDIOC_SETTIMEOUT,&feedtime);
             //超过feedtime系统就会重新启动

         return fd;
  }
 jint
 Java_com_nxecoii_watchdog_WatchDog_feedDog(JNIEnv* env,
  		jobject thiz) {

          int ret;
          int i,j;
          i=WDIOS_DISABLECARD;
          ioctl(fd,WDIOC_SETOPTIONS,&i);
          i=WDIOS_ENABLECARD;
          //使能看门狗
          ioctl(fd,WDIOC_SETOPTIONS,&i);
          //喂狗
          ioctl(fd,WDIOC_KEEPALIVE,NULL);

      //    __android_log_print(ANDROID_LOG_INFO,"JNI","----feedingDog-------------vale=(%d) ", ret);

         return ret;
  }
