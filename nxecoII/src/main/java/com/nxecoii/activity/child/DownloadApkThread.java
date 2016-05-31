package com.nxecoii.activity.child;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadApkThread extends Thread {

	boolean cancelUpdate = false;
	int updateKey = 0;
	Handler mHandler = new Handler();

	@Override
	public void run() {
		try {

			if (cancelUpdate == true) {
				return;
			}
			// if
			// (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			// {
			// 获得存储卡的路径
			String mSavePath;
			String sdpath = Environment.getExternalStorageDirectory() + "/";
			mSavePath = sdpath + "download";
			// URL url = new
			// URL("http://121.199.14.6:9000/staticPage/downloadFile");
			// 创建连接
			URL url = new URL(
					"http://121.199.14.6:9000/staticPage/downloadFile");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			// 获取文件大小
			int length = conn.getContentLength();
			// 创建输入流
			InputStream is = conn.getInputStream();

			File file = new File(mSavePath);
			// 判断文件目录是否存在
			if (!file.exists()) {
				file.mkdir();
			}

			File apkFile = new File(mSavePath, "NxecoII.apk");
			// if (!apkFile.exists())
			// {
			// apkFile.mkdir();
			// }

			FileOutputStream fos = new FileOutputStream(apkFile);
			int count = 0;
			int progress = 0;
			// 缓存
			byte buf[] = new byte[1024];
			// 写入到文件中
			do {
				int numread = is.read(buf);
				count += numread;
				// 计算进度条位置
				progress = (int) (((float) count / length) * 100);
				// 写入文件

				if (numread <= 0) {
					// 下载完成
					Message msg = new Message();

					cancelUpdate = true;
					msg.what = updateKey;
					mHandler.sendMessage(msg);
					// break;
					return;
				}
				fos.write(buf, 0, numread);
			} while (!cancelUpdate);// 点击取消就停止下载.
			fos.close();
			is.close();
			// }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 取消下载对话框显示
	}
};
