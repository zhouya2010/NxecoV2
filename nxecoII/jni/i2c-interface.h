#ifndef _HELLO_JNI_H
#define _HELLO_JNI_H
#define MODE_AUTO       0
#define MODE_QUICK      1
#define MODE_READ       2
struct i2c_smbus_ioctl_data {
	__u8 read_write;
	__u8 command;
	__u32 size;
	union i2c_smbus_data *data;
};

struct tag
{
    int a[10];
}x,y;


static inline __s32 i2c_smbus_access(int file, char read_write, __u8 command,
                                     int size, union i2c_smbus_data *data)
{
	struct i2c_smbus_ioctl_data args;

	args.read_write = read_write;
	args.command = command;
	args.size = size;
	args.data = data;
	return ioctl(file,I2C_SMBUS,&args);
}
static inline __s32 i2c_smbus_read_byte1(int file)
{
	union i2c_smbus_data data;
	if (i2c_smbus_access(file,I2C_SMBUS_READ,0,I2C_SMBUS_BYTE,&data))
		return -1;
	else
		return 0x0FF & data.byte;
}
static inline __s32 i2c_smbus_write_quick(int file, __u8 value)
{
	return i2c_smbus_access(file,value,0,I2C_SMBUS_QUICK,NULL);
}


static inline __s32 i2c_smbus_read_word_data(int file, __u8 command)
{
    union i2c_smbus_data data;
    if (i2c_smbus_access(file,I2C_SMBUS_READ,command,
                         I2C_SMBUS_WORD_DATA,&data))
        return -1;
    else
        return 0x0FFFF & data.word;
}
static  __s32 i2c_smbus_read_byte_data(int file, __u8 command)
{
	union i2c_smbus_data data;
	if (i2c_smbus_access(file,I2C_SMBUS_READ,command,
	                     I2C_SMBUS_BYTE_DATA,&data))
		return -1;
	else
		return 0x0FF & data.byte;
}
static inline __s32 i2c_smbus_write_byte_data(int file, __u8 command,
                                              __u8 value)
{
	union i2c_smbus_data data;
	data.byte = value;
	return i2c_smbus_access(file,I2C_SMBUS_WRITE,command,
	                        I2C_SMBUS_BYTE_DATA, &data);
}
#define        devic        "/dev/i2c-1"

#define I2C_FUNC_SMBUS_PEC      0x00000008


static int confirm(const char *filename, int address, int size, int daddress,
                   int pec)
{
        int dont = 0;

        fprintf(stderr, "WARNING! This program can confuse your I2C "
                "bus, cause data loss and worse!\n");

        /* Don't let the user break his/her EEPROMs */
        if (address >= 0x50 && address <= 0x57 && pec) {
                fprintf(stderr, "STOP! EEPROMs are I2C devices, not "
                        "SMBus devices. Using PEC\non I2C devices may "
                        "result in unexpected results, such as\n"
                        "trashing the contents of EEPROMs. We can't "
                        "let you do that, sorry.\n");
                return 0;
        }

        if (size == I2C_SMBUS_BYTE && daddress >= 0 && pec) {
                fprintf(stderr, "WARNING! All I2C chips and some SMBus chips "
                        "will interpret a write\nbyte command with PEC as a"
                        "write byte data command, effectively writing a\n"
                        "value into a register!\n");
                dont++;
        }

        fprintf(stderr, "I will read from device file %s, chip "
                "address 0x%02x, ", filename, address);
        if (daddress < 0)
                fprintf(stderr, "current data\naddress");
        else
                fprintf(stderr, "data address\n0x%02x", daddress);
        fprintf(stderr, ", using %s.\n",
                size == I2C_SMBUS_BYTE ? (daddress < 0 ?
                "read byte" : "write byte/read byte") :
                size == I2C_SMBUS_BYTE_DATA ? "read byte data" :
                "read word data");
        if (pec)
                fprintf(stderr, "PEC checking enabled.\n");

        fprintf(stderr, "Continue? [%s] ", dont ? "y/N" : "Y/n");
        fflush(stderr);
        if (!user_ack(!dont)) {
                fprintf(stderr, "Aborting on user request.\n");
                return 0;
        }


        return 1;
}

static int check_funcs(int file, int size, int daddress, int pec)
{
        unsigned long funcs;

        /* check adapter functionality */
        if (ioctl(file, I2C_FUNCS, &funcs) < 0) {
    //            fprintf(stderr, "Error: Could not get the adapter "
      //                  "functionality matrix: %s\n", strerror(errno));

        	 __android_log_print(ANDROID_LOG_INFO,"JNI","Error: Could not get the adapter,functionality matrix: %s\n",strerror(errno));
                return -1;
        }

        switch (size) {
        case I2C_SMBUS_BYTE:
                if (!(funcs & I2C_FUNC_SMBUS_READ_BYTE)) {
                //        fprintf(stderr, MISSING_FUNC_FMT, "SMBus receive byte");
                        __android_log_print(ANDROID_LOG_INFO,"JNI","SMBus receive byte");
                        return -1;
                }
                if (daddress >= 0
                 && !(funcs & I2C_FUNC_SMBUS_WRITE_BYTE)) {
                    //    fprintf(stderr, MISSING_FUNC_FMT, "SMBus send byte");
                        __android_log_print(ANDROID_LOG_INFO,"JNI","SMBus send byte");
                        return -1;
                }
                break;

        case I2C_SMBUS_BYTE_DATA:
                if (!(funcs & I2C_FUNC_SMBUS_READ_BYTE_DATA)) {
                      //  fprintf(stderr, MISSING_FUNC_FMT, "SMBus read byte");
                	  __android_log_print(ANDROID_LOG_INFO,"JNI","SMBus read byte");
                        return -1;
                }
                break;

        case I2C_SMBUS_WORD_DATA:
                if (!(funcs & I2C_FUNC_SMBUS_READ_WORD_DATA)) {
                   //     fprintf(stderr, MISSING_FUNC_FMT, "SMBus read word");
                	 __android_log_print(ANDROID_LOG_INFO,"JNI","SMBus read word");
                        return -1;
                }
                break;
        }

        if (pec
         && !(funcs & (I2C_FUNC_SMBUS_PEC | I2C_FUNC_I2C))) {
              //  fprintf(stderr, "Warning: Adapter does "
                //        "not seem to support PEC\n");
        	 __android_log_print(ANDROID_LOG_INFO,"JNI","Warning: Adapter does not seem to support PEC");
        }

        return 0;
}

static int lookup_i2c_bus_by_name(const char *bus_name)
{
	struct i2c_adap *adapters;
	int i, i2cbus = -1;

	adapters = gather_i2c_busses();
	if (adapters == NULL) {
		fprintf(stderr, "Error: Out of memory!\n");
		return -3;
	}

	/* Walk the list of i2c busses, looking for the one with the
	   right name */
	for (i = 0; adapters[i].name; i++) {
		if (strcmp(adapters[i].name, bus_name) == 0) {
			if (i2cbus >= 0) {
				fprintf(stderr,
					"Error: I2C bus name is not unique!\n");
				i2cbus = -4;
				goto done;
			}
			i2cbus = adapters[i].nr;
		}
	}

	if (i2cbus == -1)
		fprintf(stderr, "Error: I2C bus name doesn't match any "
			"bus present!\n");

done:
	free_adapters(adapters);
	return i2cbus;
}

static int confirm1(const char *filename, int address, int size, int daddress,
                   int value, int vmask, const unsigned char *block, int len,
                   int pec)
{
        int dont = 0;

        fprintf(stderr, "WARNING! This program can confuse your I2C "
                "bus, cause data loss and worse!\n");

        if (address >= 0x50 && address <= 0x57) {
                fprintf(stderr, "DANGEROUS! Writing to a serial "
                        "EEPROM on a memory DIMM\nmay render your "
                        "memory USELESS and make your system "
                        "UNBOOTABLE!\n");
                dont++;
        }

        fprintf(stderr, "I will write to device file %s, chip address "
                "0x%02x, data address\n0x%02x, ", filename, address, daddress);
        if (size == I2C_SMBUS_BYTE)
                fprintf(stderr, "no data.\n");
        else if (size == I2C_SMBUS_BLOCK_DATA ||
                 size == I2C_SMBUS_I2C_BLOCK_DATA) {
                int i;

                fprintf(stderr, "data");
                for (i = 0; i < len; i++)
                        fprintf(stderr, " 0x%02x", block[i]);
                fprintf(stderr, ", mode %s.\n", size == I2C_SMBUS_BLOCK_DATA
                        ? "smbus block" : "i2c block");
        } else
                fprintf(stderr, "data 0x%02x%s, mode %s.\n", value,
                        vmask ? " (masked)" : "",
                        size == I2C_SMBUS_BYTE_DATA ? "byte" : "word");
        if (pec)
                fprintf(stderr, "PEC checking enabled.\n");

        fprintf(stderr, "Continue? [%s] ", dont ? "y/N" : "Y/n");
        fflush(stderr);
        if (!user_ack(!dont)) {
                fprintf(stderr, "Aborting on user request.\n");
                return 0;
        }

        return 1;
}
//static int (*retArray(int file, int mode, int first, int last))[10]
struct  tag test;
static int scan_i2c_bus(int file, int mode, int first, int last)
{

    int i, j;
    int k=0;
    int res;

 //   __android_log_print(ANDROID_LOG_INFO,"JNI","     0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f\n");

    for (i = 0; i < 128; i += 16) {
            printf("%02x: ", i);
            for(j = 0; j < 16; j++) {
                    fflush(stdout);

                    /* Skip unwanted addresses */
                    if (i+j < first || i+j > last) {
                            printf("   ");
                        //    __android_log_print(ANDROID_LOG_INFO,"JNI"," ");
                            continue;
                    }

                    /* Set slave address */
                    if (ioctl(file, I2C_SLAVE, i+j) < 0) {
                            if (errno == EBUSY) {
                                  //  printf("UU ");
                      //              __android_log_print(ANDROID_LOG_INFO,"JNI","UU ");
                                    continue;
                            } else {
                                    fprintf(stderr, "Error: Could not set "
                                            "address to 0x%02x: %s\n", i+j,
                                            strerror(errno));
                                    return -1;
                            }
                    }

                    /* Probe this address */
                    switch (mode) {
                    case MODE_QUICK:
                            /* This is known to corrupt the Atmel AT24RF08
                               EEPROM */
                            res = i2c_smbus_write_quick(file,
                                  I2C_SMBUS_WRITE);
                            break;
                    case MODE_READ:
                            /* This is known to lock SMBus on various
                               write-only chips (mainly clock chips) */

                           res = i2c_smbus_read_byte1(file);


                            break;
                    default:
                            if ((i+j >= 0x30 && i+j <= 0x37)
                                    || (i+j >= 0x50 && i+j <= 0x5F))

                                          res = i2c_smbus_read_byte1(file);
                                   else
                                           res = i2c_smbus_write_quick(file,
                                                 I2C_SMBUS_WRITE);
                           }

                           if (res < 0){}
                                 //  printf("-- ");
                      //     __android_log_print(ANDROID_LOG_INFO,"JNI","-- ");
                           else

                           {
                        //	__android_log_print(ANDROID_LOG_INFO,"JNI","%02x ", i+j);

                           x.a[k]=i+j;
                          // __android_log_print(ANDROID_LOG_INFO,"JNI","------xiaolonghun i-----------%d ", i);
                         //  __android_log_print(ANDROID_LOG_INFO,"JNI","------xiaolonghun x.a[%d]----------- ",k);
                         //  __android_log_print(ANDROID_LOG_INFO,"JNI","------xiaolonghun x.a[k]-----------%02x ", i+j);


                           k=k+1;

                           }}
                   printf("\n");
           }

           return 0;
       }
static inline __s32 i2c_smbus_write_word_data(int file, __u8 command,
                                              __u16 value)
{
	union i2c_smbus_data data;
	data.word = value;
	return i2c_smbus_access(file,I2C_SMBUS_WRITE,command,
	                        I2C_SMBUS_WORD_DATA, &data);
}
struct tag retArray()
{
 return x;
}
/*
 *
struct tag retArray()
{
	for(int i=0;i<10;i++)
		x.a[i]=i;
	//__android_log_print(ANDROID_LOG_INFO,"JNI","-------testArray_xiaolonghun-----------------=%d\n",test.a[i]);


    return test ;
}
*/
#endif /* _UTIL_H */
