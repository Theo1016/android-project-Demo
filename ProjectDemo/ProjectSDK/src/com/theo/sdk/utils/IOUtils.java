package com.theo.sdk.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;

import com.theo.sdk.constant.Const;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

/**
 * IO工具类
 * @author Theo
 *
 */
public class IOUtils {


	/**
	 * 文本的写入操做
	 * 
	 * @param filePath
	 *            文件路径。一定要加上文件名字 <br>
	 *            例如../a/a.txt
	 * @param content
	 *            写入内容
	 */
	public static void write(String filePath, String content) {
		BufferedWriter bufw = null;
		try {
			bufw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath)));
			bufw.write(content);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (bufw != null) {
				try {
					bufw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文本的读取
	 * 
	 * @param path
	 *            文件路径,一定要加上文件名字<br>
	 *            例如../a/a.txt
	 * @return
	 */
	public static String read(String path) {
		BufferedReader bufr = null;
		try {
			bufr = new BufferedReader(new InputStreamReader(
					new FileInputStream(path)));
			StringBuffer sb = new StringBuffer();
			String str = null;
			while ((str = bufr.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufr != null) {
				try {
					bufr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 文本的读取操做
	 * 
	 * @param InputStream
	 *            输入流
	 * @return
	 */
	public static String read(InputStream is) {
		BufferedReader bufr = null;
		try {
			bufr = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String str = null;
			while ((str = bufr.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufr != null) {
				try {
					bufr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 获取/data/data/youPackageName/文件
	 * 
	 * @param context
	 * @return
	 */
	public static File getDirDrectly(Context context) {
		return context.getFilesDir();
	}

	// 将字符串写入到文本文件中
	public static void writeTxtToFile(String strcontent, String filePath,
			String fileName) {
		// 生成文件夹之后，再生成文件，不然会出�?
		File fileStr = makeFilePath(filePath, fileName);

		String strFilePath = fileStr.getAbsolutePath();
		// 每次写入时，都换行写
		String strContent = strcontent + "\r\n";
		try {
			File file = new File(strFilePath);
			if (!file.exists()) {
				Log.d(Const.LogTag, "Create the file:" + strFilePath);
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rwd");
			raf.seek(file.length());
			raf.write(strContent.getBytes());
			raf.close();
		} catch (Exception e) {
			Log.e(Const.LogTag, "Error on write File:" + e);
		}
	}

	// 生成文件
	public static File makeFilePath(String filePath, String fileName) {
		File file = null;
		makeRootDirectory(filePath);
		try {
			if (!TextUtils.isEmpty(filePath) && !filePath.endsWith("/")) {
				filePath = filePath + "/";
			}
			file = new File(filePath + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	// 生成文件
	public static void makeRootDirectory(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (Exception e) {

		}
	}

	/**
	 * 异常信息提取
	 * 
	 * @param ex
	 * @return
	 */
	public static String parseExcption2String(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		// 将此 throwable 及其追踪输出到指定的 PrintWriter
		ex.printStackTrace(printWriter);
		// getCause() 返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		// toString() 以字符串的形式返回该缓冲区的当前值。
		String result = info.toString();
		printWriter.close();
		return result;
	}


}
