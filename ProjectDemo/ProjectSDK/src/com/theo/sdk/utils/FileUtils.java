package com.theo.sdk.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.theo.sdk.constant.Const;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

/**
 * File工具类
 * @author Theo
 *
 */
public class FileUtils {

	private String picpath = "calabar/User/";
	// SDcard文件路径;
	private static String SDPATH;

	/**
	 ** 构造方法 获取sd卡路径
	 * ***/
	public FileUtils() {
		// 获得当前外部存储设备的路径
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	/**
	 * 保存图片
	 * 
	 * @param bitmap
	 * @param fileName
	 * @throws IOException
	 */
	public File writeSDFromInput(Context mContext, String fileName,
			Bitmap bitmap) {
		if (bitmap == null)
			return null;
		File file = null;
		File tempf;
		try {
			// 创建SD卡上的目录
			tempf = createSDDir(picpath);
			synchronized (tempf) {
				Log.i(Const.LogTag, "directory in the sd card:" + tempf.exists());
				String nameString = picpath + fileName;
				// removeFile(nameString);
				// 添加标记
				RenameFile(fileName); 
				file = CreateSDFile(nameString);
				Log.i(Const.LogTag,"file in the sd card:" + file.exists());
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));
				String name = fileName.substring(fileName.lastIndexOf("."));
				if (name.equals(".jpg")) {
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				} else if (name.equals(".png")) {
					// PNG格式无法进行压缩
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos); 
				}
				bos.flush();
				bos.close();
				// 删除掉标记
				DeletemarkFile();
			}

			// removeFile(path+marks);
		} catch (FileNotFoundException e) {
			LogUtils.e(mContext, "writeSDFromInput", e.getMessage(), true);

		} catch (IOException e) {
			LogUtils.e(mContext, "writeSDFromInput", e.getMessage(), true);
		}
		return file;
	}

	/**
	 * 删除标记文件
	 */
	public void DeletemarkFile() {
		File f = new File(SDPATH + picpath);
		if (f.exists()) {
			if (f.isDirectory()) {
				File[] files = f.listFiles();
				File file;
				if (files == null)
					return;
				for (File file1 : files) {
					file = file1;
					String oldname = file.getName();
					if (oldname.contains(".lock")) {
						file.delete();
					}
				}
			}
		}

	}

	/**
	 * 在SD卡上面创建文件
	 * 
	 * @throws IOException
	 * */
	public File CreateSDFile(String fileName) throws IOException {
		Log.i(Const.LogTag,"filename:" + fileName);
		File file = new File(SDPATH + fileName);
		return file;
	}

	/**
	 * 修改以前的图片为标记文件
	 */
	public void RenameFile(String filename) {
		File f = new File(SDPATH + picpath);
		if (f.exists()) {
			if (f.isDirectory()) {
				File[] files = f.listFiles();
				if (files == null)
					return;
				for (File file : files) {
					String oldname = file.getName();
					// 只锁定相关图片，不相关则不需要lock
					if (oldname.contains(filename)) {
						String newname = oldname.substring(0, oldname.length());
						newname = newname + ".lock";
						if (!oldname.equals(newname)) {
							String path = file.getParent();
							File newfile = new File(path + "/" + newname);
							if (!newfile.exists()) {
								file.renameTo(newfile);
							}
						}
					}

				}
			}
		}

	}

	/**
	 * 
	 * 在SD卡上创建目录 
	 * 
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.i(Const.LogTag, "the result of making directory:" + dir.mkdirs());
		}
		return dir;
	}
}
