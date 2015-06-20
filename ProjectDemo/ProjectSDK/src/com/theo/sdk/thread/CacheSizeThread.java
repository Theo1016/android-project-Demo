package com.theo.sdk.thread;

import java.util.Collection;
import android.graphics.Bitmap;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theo.sdk.app.SDKApplication;
import com.theo.sdk.utils.LogUtils;


/**
 * 检测缓存
 * @author Theo
 *
 */
public class CacheSizeThread extends Thread {
	// 每隔Time ，检测一次内存
	int Time = 30000;
	
	@Override
	public void run() {
		super.run();
		try {
			checkCacheSize();
			Thread.sleep(Time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 检测缓存大小
	 * 
	 */
	public static void checkCacheSize() {
		MemoryCacheAware<String, Bitmap> memoryCacheAware = ImageLoader.getInstance().getMemoryCache();
		Collection<String> listStrings = ImageLoader.getInstance().getMemoryCache().keys();
		float size = 0;

		for (String element : listStrings) {
			Bitmap bitmap = memoryCacheAware.get(element);
			if (null != bitmap) {
				float itemSize = bitmap.getRowBytes() * bitmap.getHeight();
				size += itemSize;
				LogUtils.i(SDKApplication.appContext, "ImageManager", "===>cache itemSize byte:" + itemSize, true);
				LogUtils.i(SDKApplication.appContext, "ImageManager", "===>cache itemSize MB:" + itemSize
						/ (1024 * 1024), true);
			}
		}
		LogUtils.i(SDKApplication.appContext, "ImageManager", "===>cache size byte:" + size, true);
		LogUtils.i(SDKApplication.appContext, "ImageManager", "===>cache size MB:" + size / (1024 * 1024), true);
	}
}
