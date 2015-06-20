package com.theo.sdk.utils;


import android.content.Context;
import android.util.Log;

/**
 * 日志工具类
 * @author Theo
 *
 */
public class LogUtils {

	public static void e(Context context, String TAG, String content,
			boolean needIntoFile) {
		content = parseContent(content);
		if (AppUtils.canLog() && needIntoFile) {
			IOUtils.writeTxtToFile(content, AppUtils.getDirDirect(context)
					+ "/logs", "errorLog.txt");
			v(context, TAG, content, needIntoFile);
		}
		Log.e(TAG, content);
	}

	public static void i(Context context, String TAG, String content,
			boolean needIntoFile) {
		content = parseContent(content);
		if (AppUtils.canLog() && needIntoFile) {
			IOUtils.writeTxtToFile(content, AppUtils.getDirDirect(context)
					+ "/logs", "infoLog.txt");
			v(context, TAG, content, needIntoFile);
		}
		Log.i(TAG, content);
	}

	public static void d(Context context, String TAG, String content,
			boolean needIntoFile) {
		content = parseContent(content);
		if (AppUtils.canLog() && needIntoFile) {
			IOUtils.writeTxtToFile(content, AppUtils.getDirDirect(context)
					+ "/logs", "debugLog.txt");
			v(context, TAG, content, needIntoFile);
		}
		Log.d(TAG, content);
	}

	public static void w(Context context, String TAG, String content,
			boolean needIntoFile) {
		content = parseContent(content);
		if (AppUtils.canLog() && needIntoFile) {
			IOUtils.writeTxtToFile(content, AppUtils.getDirDirect(context)
					+ "/logs", "warmLog.txt");
			v(context, TAG, content, needIntoFile);
		}
		Log.w(TAG, content);
	}

	private static void v(Context context, String TAG, String content,
			boolean needIntoFile) {
		if (AppUtils.canLog() && needIntoFile) {
			IOUtils.writeTxtToFile(content, AppUtils.getDirDirect(context)
					+ "/logs", "verboseLog.txt");
		}
		Log.v(TAG, content);
	}

	
	public static void runtimeError(Context context, String TAG,
			String content, boolean needIntoFile) {
		content = parseContent("==runtime Exception :" + content);
		if (AppUtils.canLog() && needIntoFile) {
			IOUtils.writeTxtToFile(content, AppUtils.getDirDirect(context)
					+ "/logs", "runtimeExceptionLog.txt");
			v(context, TAG, content, needIntoFile);
		}
		Log.e(TAG, content);
	}

	
	private static String parseContent(final String content) {
		Long Currenttime = System.currentTimeMillis();
		return DateUtils.format(Currenttime, "yyyy:MM:dd   HH:mm:ss")
				+ "           " + content;

	}

}
