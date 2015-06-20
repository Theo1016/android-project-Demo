package com.theo.sdk.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import com.theo.sdk.app.SDKApplication;
import com.theo.sdk.constant.Const;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 工具类
 * @author Theo
 * 
 */
public class AppUtils {
	/** 1024,用于计算app大小 */
    public static final int NUM_1024 = 1024;
	/** DEBUG开关*/
	private static final boolean DEBUG = false & Const.DEBUG;
	/** 定义buffer的大小 */
    private static final int BUFFERSIZE = 1024;

	@SuppressWarnings("unchecked")
	public static <T> T getMetaData(Context context, String name) {
		try {
			final ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);

			if (ai.metaData != null) {
				return (T) ai.metaData.get(name);
			}
		} catch (Exception e) {
		}

		return null;
	}

	/**
	 * 日志开关
	 * 
	 * @return
	 */
	public static boolean canLog() {

		if (TextUtils.isEmpty(SDKApplication.logSwitch)) {
			return false;
		} else {
			if (TextUtils.equals("open", SDKApplication.logSwitch))
				return true;
			return false;
		}

	}

	/**
	 * 获取/data/data/youPackageName/路径
	 * 
	 * @param context
	 * @return String
	 */
	public static String getDirDirect(Context context) {
		File file = IOUtils.getDirDrectly(context);
		if (null != file)
			return file.getAbsolutePath();
		return null;
	}

	/** DP转换为像素值 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/** 像素转换为DP值 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getDisplayWidth(Activity context) {
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getDisplayHeight(Activity context) {
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}

	

	/**
	 * 获取设备的imei号
	 * 
	 * @param context
	 *            Context
	 * @return imei号
	 */
	public static String getDeviceId(Context context) {
		String deviceId = "";
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm != null) {
				deviceId = tm.getDeviceId();
				// 模拟器会返回 000000000000000
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (TextUtils.isEmpty(deviceId)) {
			// 如果imei为空，使用随机数产生一个 12 位 deviceid，
			final int imeiLength = 12; // gsm imei 号长度 12
			final int ten = 10; // 产生10以内的随机数
			Random random = new Random();
			StringBuffer sb = new StringBuffer(imeiLength);
			for (int i = 0; i < imeiLength; i++) {
				int r = random.nextInt(ten);
				sb.append(r);
			}
			deviceId = "777"+sb.toString();
		} 
		return deviceId;
	}

	/**
	 * 是否安装了sdcard。
	 * 
	 * @return true表示有，false表示没有
	 */
	public static boolean haveSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}
	
	/**
     * 接收网络数据流,兼容gzip与正常格式内容。
     * 
     * @param is
     *            读取网络数据的流
     * @return 字符串类型数据
     */
    public static String recieveData(InputStream is) {
        String s = null;
        boolean isGzip = false;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (is == null) {
            if (DEBUG) {
                Log.d(Const.LogTag, "recieveData inputstream is null.");
            }
            return null;
        }

        try {
            byte[] filetype = new byte[4]; // SUPPRESS CHECKSTYLE
            // os = new BufferedOutputStream();
            byte[] buff = new byte[BUFFERSIZE];
            int readed = -1;
            while ((readed = is.read(buff)) != -1) {
                baos.write(buff, 0, readed);
            }
            byte[] result = baos.toByteArray();
            //判断是否是gzip格式的内容。
            System.arraycopy(result, 0, filetype, 0, 4); // SUPPRESS CHECKSTYLE
            if ("1F8B0800".equalsIgnoreCase(bytesToHexString(filetype))) {
                isGzip = true;
            } else {
                isGzip = false;
            }
            if (DEBUG) {
                Log.d(Const.LogTag, " received file is gzip:" + isGzip);
            }
            if (isGzip) {
                result = AppUtils.unGZip(result);
            } 
            if (result == null) {
                return null;
            }
            s = new String(result, "utf-8");
            is.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DEBUG) {
            Log.i(Const.LogTag, "服务器下发数据:" + s);
        }
        return s;
    }
    
    /**
     * byte数组转换成16进制字符串
     * 
     * @param src
     *            数据源
     * @return byte转为16进制
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF; // SUPPRESS CHECKSTYLE
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    
    /***
	 * 解压GZip
	 * 
	 * @param data
	 *            要解压的数据
	 * @return 解压过的数据
	 */
    public static byte[] unGZip(byte[] data) {

        if (data == null) {
            if (DEBUG) {
                Log.d(Const.LogTag, "unGZip data:" + null);
            }
            return null;
        }
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[NUM_1024];
            int num = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            b = baos.toByteArray();
            baos.flush();
            baos.close();
            gzip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }
}
