package com.theo.sdk.exception;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.theo.sdk.constant.Const;
import com.theo.sdk.manager.ActivityTaskManager;
import com.theo.sdk.utils.AppUtils;
import com.theo.sdk.utils.IOUtils;
import com.theo.sdk.utils.LogUtils;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * 崩溃异常捕获
 * @author Theo
 *
 */
public class CatchRuntimeCrash implements UncaughtExceptionHandler {

	private Context mContext;

	private UncaughtExceptionHandler crashHandler;

	private static CatchRuntimeCrash instance;

	// 存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	public static CatchRuntimeCrash getInstance(Context context) {

		if (null == instance) {
			instance = new CatchRuntimeCrash();
		}
		return instance;
	}

	public void init(Context ctx) {
		mContext = ctx;
		crashHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && crashHandler != null) {
			crashHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				LogUtils.e(mContext, Const.LogTag, e.getMessage(), false);
			}
			// 退出
			ActivityTaskManager.removeAllActivity4Task();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
	}
	/**
	 * 收集异常信息，可处理异常
	 * @param ex
	 * @return
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "很抱歉,程序出现异常.", Toast.LENGTH_LONG)
						.show();

				Looper.loop();
			}
		}.start();
		// 获取异常信息
		final String msg = IOUtils.parseExcption2String(ex);
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 完善异常信息
		String info =completeErrorMsg(msg);
		// 日志输出
		LogUtils.runtimeError(mContext, Const.LogTag, info, true);
		return false;
	}
	
	
	/**
	 * 完善异常信息
	 * @param msg
	 */
	private String completeErrorMsg(String msg){
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + ":" + value + "\n");
		}
		sb.append("\n"  +"DeviceID:"+AppUtils.getDeviceId(mContext));
		sb.append("\n" + "ErrorMsg:"+msg);
		return sb.toString();
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	private void collectDeviceInfo(Context ctx) {
		try {
			// 获取versionName和Code
			getVersionNameAndCode(ctx);
		} catch (NameNotFoundException e) {
			Log.e(Const.LogTag, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(Const.LogTag, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(Const.LogTag, "an error occured when collect crash info", e);
			}
		}
	}
	
	/**
	 * 获取VersionCode和Name
	 * @param ctx
	 * @throws NameNotFoundException
	 */
	private void getVersionNameAndCode(Context ctx)
			throws NameNotFoundException {
		PackageManager pm = ctx.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
				PackageManager.GET_ACTIVITIES);
		if (pi != null) {
			String versionName = pi.versionName == null ? "null"
					: pi.versionName;
			String versionCode = pi.versionCode + "";
			infos.put("versionName", versionName);
			infos.put("versionCode", versionCode);
		}
	}

}
