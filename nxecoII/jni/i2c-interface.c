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

#include "com_nxecoii_i2c_I2cInterface.h"

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
#include <linux/i2c.h>
#include "i2cbusses.h"
#include <android/log.h>
#include "i2c-interface.h"

/* This is the structure as used in the I2C_SMBUS ioctl call */


/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *
 */
char realAddress[5]={0x08,0x12,0x20,0x30,0x48};
char mapAddress[5]={0x04,0x09,0x10,0x18,0x24};
int  anchor=0;
struct tag i2c_detect_slave()
{

           int i2cbus, file, res,i,j;
           char filename[20];
           unsigned long funcs;
           int mode = MODE_AUTO;
           int first = 0x03, last = 0x77;
           int flags = 0;
           int yes = 0, version = 0, list = 0;
           i2cbus = lookup_i2c_bus("1");
           file = open_i2c_dev(i2cbus, filename, sizeof(filename), 0);
           res=ioctl(file, I2C_FUNCS, &funcs);
           res = scan_i2c_bus(file, mode, first, last);
           struct tag y=retArray();
    /*   for(j=0;j<10;j++){
     	 //  __android_log_print(ANDROID_LOG_INFO,"JNI"," before set value:y.a[%d]=%02x\n",j,y.a[j]);
           for( i=0;i<5;i++){

          	   if(y.a[j]==mapAddress[i]){
          		  y.a[j]=realAddress[i];
          	//	 __android_log_print(ANDROID_LOG_INFO,"JNI","after set value:y.a[%d]=%02x\n",j,y.a[j]);

          	   }

             }
       }   */
       return y;
}



char salveAddress[10]="";
jcharArray
 Java_com_nxecoii_i2c_I2cInterfaceTest_detectI2C(JNIEnv* env,
 		jobject thiz) {
			jcharArray  salve=NULL;
			int res;
			int i;
			salve = (*env)->NewByteArray(env,10);
			struct tag y=i2c_detect_slave();
			  for(i=0;i<10;i++){
                 salveAddress[i]=y.a[i];
			    	//   __android_log_print(ANDROID_LOG_INFO,"JNI","------------------------salveAddress[%d]=%d\n",i,y.a[i]);
			       }
			(*env)->SetByteArrayRegion(env,salve, 0, 10, salveAddress);
            return salve;
 }

 jint
  Java_com_nxecoii_i2c_I2cInterfaceTest_readI2C(JNIEnv* env,
  		jobject thiz,jstring chipAddress,jstring registerAddress) {

          int res;

         const char *chip = (*env)->GetStringUTFChars(env, chipAddress, 0);
         const char *registerAdd = (*env)->GetStringUTFChars(env, registerAddress, 0);
         res=	i2c_read_reg(chip,registerAdd);
         return res;
  }



 jint Java_com_nxecoii_i2c_I2cInterfaceTest_writeI2C(JNIEnv* env,
 		jobject thiz,jstring chipAddress,jstring registerAddress,jstring realValue) {

		 int res;
     	 const char *chip = (*env)->GetStringUTFChars(env, chipAddress, 0);
     	 const char *registerAdd = (*env)->GetStringUTFChars(env, registerAddress, 0);
     	 const char *value = (*env)->GetStringUTFChars(env, realValue, 0);
	 	 res=i2c_write_reg(chip,registerAdd,value);
 	 	 return res;

 }



int i2c_write_reg(const char *chip,  const char *regis,const char *value1)
{

	//i2cset -y -f 1 0x68 0x01 0x05
	        int i;
			char *end;
			const char *maskp = NULL;
			int res, i2cbus, address, size, file;
			int value, daddress, vmask = 0;
			char filename[20];
			int pec = 0;
			int flags = 0;
			int force = 0, yes = 0, version = 0, readback = 0;
			unsigned char block[I2C_SMBUS_BLOCK_MAX];
			int len;
			//查找总线以上的设备名称
			i2cbus = lookup_i2c_bus("1");

	/*		for (i =0;i<5;i++){
						  if(strtol(chip, &end, 0)==realAddress[i]){

							chip=(const char *)(long)mapAddress[i];
						      __android_log_print(ANDROID_LOG_INFO,"JNI","realAddress[%d]=%02x\n",i,mapAddress[i]);

						          	   }
					} */
			//填充要写入的I2C总线上芯片地址
			address = parse_i2c_address(chip);
			//填写要写入的芯片地址
			daddress = strtol(regis, &end, 0);
			size=I2C_SMBUS_WORD_DATA;
			force =1;
			pec=0;
			//vakue=5;
			value=strtol(value1, &end, 0);;
			yes=1;
		//	size=2;
			file = open_i2c_dev(i2cbus, filename, sizeof(filename), 0);

				res =check_funcs(file, size, daddress, pec);
				res =  set_slave_addr(file, address, force);
				res =  confirm1(filename, address, size, daddress,value, vmask, block, len, pec);



				//res = i2c_smbus_write_byte_data(file, daddress, value);
				res =i2c_smbus_write_word_data(file, daddress, value);
				close(file);
			return res;
}


int i2c_read_reg(const char *chip,  const char *regis)
{

		int 	i2cbus,address,file,size,res;
		char filename[20];
		char *end;
		int daddress;
		int pec = 0;
		int force = 0,yes = 1,version=0;
		int i =0;
		//查找总线以上的设备名称
		i2cbus = lookup_i2c_bus("1");
		//填充要写入的I2C总线上芯片地址
	/*	for (i =0;i<5;i++){
			  if(strtol(chip, &end, 0)==realAddress[i]){

				chip=(const char *)(long)mapAddress[i];
			      __android_log_print(ANDROID_LOG_INFO,"JNI","realAddress[%d]=%02x\n",i,mapAddress[i]);

			          	   }
		}*/
		address = parse_i2c_address(chip);

	//	__android_log_print(ANDROID_LOG_INFO,"JNI","------xiaolonghun x.a[k]-----------%02x ", address);

			//填写要读取的芯片地址
		daddress = strtol(regis, &end, 0);
		//size=I2C_SMBUS_WORD_DATA;
        force =1;
        size=2;
		pec=0;

		file = open_i2c_dev(i2cbus, filename, sizeof(filename), 0);
		res =check_funcs(file, size, daddress, pec);
		res =  set_slave_addr(file, address, force);

		res =confirm(filename, address, size, daddress, pec);

		/*	  __android_log_print(ANDROID_LOG_INFO,"JNI","-------daddress-----------------=%d\n",daddress);
			__android_log_print(ANDROID_LOG_INFO,"JNI","-------version-----------------=%d\n",version);
			__android_log_print(ANDROID_LOG_INFO,"JNI","-------force-----------------=%d",force);
			__android_log_print(ANDROID_LOG_INFO,"JNI","-------yes-----------------=%d",yes);
			__android_log_print(ANDROID_LOG_INFO,"JNI","-------iaddress-----------------=%d\n\n",address);
			__android_log_print(ANDROID_LOG_INFO,"JNI","-------pec-----------------=%d\n",pec);
			__android_log_print(ANDROID_LOG_INFO,"JNI","-------------i2cbus-----------=%d\n",i2cbus);
			__android_log_print(ANDROID_LOG_INFO,"JNI","-------------ifilename-----------=%s\n",filename);
			__android_log_print(ANDROID_LOG_INFO,"JNI","-------------sizeof(filename)-----------=%c\n",sizeof(filename)); */
          //res = i2c_smbus_read_byte_data(file, daddress);
			res = i2c_smbus_read_word_data(file, daddress);
			close(file);
        return res;

}




