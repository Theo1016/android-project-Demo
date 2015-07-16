package com.theo.sdk.app;

import android.app.Application;
import android.content.Context;

import com.theo.sdk.exception.CatchRuntimeCrash;
import com.theo.sdk.manager.ImageManager;
import com.theo.sdk.manager.NetworkStatusManager;
import com.theo.sdk.thread.CacheSizeThread;
import com.theo.sdk.utils.AppUtils;

/**
 * Application基类
 * @author Theo
 *
 */
public class SDKApplication extends Application{
	public static String logSwitch;
	public static Context appContext ;
	private static CacheSizeThread myCacheSizeThread;
	@Override
	public void onCreate() {
		super.onCreate();
		getLogSwitch();
		appContext = getApplicationContext();
		ImageManager.initImageLoader(appContext);
		CatchRuntimeCrash crash = CatchRuntimeCrash.getInstance(appContext);
		crash.init();
		// 实时监测内存
		myCacheSizeThread = new CacheSizeThread();
		myCacheSizeThread.start();
		// 网络状态信息初始化
		NetworkStatusManager.init(appContext);
	}

	/**
	 * 获取日志开关
	 */
	private void getLogSwitch() {
		try {
			logSwitch = (String) AppUtils.getMetaData(getApplicationContext(), "log.switch");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
