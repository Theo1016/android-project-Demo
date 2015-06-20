package com.theo.sdk.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.theo.sdk.R;
import com.theo.sdk.app.SDKApplication;
import com.theo.sdk.constant.Const;
import com.theo.sdk.utils.LogUtils;

import java.io.File;
import java.util.Collection;

/**
 * 图片缓存管理
 * @author Theo
 *
 */
public class ImageManager {

	private static String IMAGE_CACHE_PATH = "calabar/image/cache";
	private static DisplayImageOptions displyOptions;
	private static ImageLoader imageLoader;
	
	
	/**
	 * 初始化ImageLoader
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils.getOwnCacheDirectory(context,
				IMAGE_CACHE_PATH);
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
				.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new UsingFreqLimitedMemoryCache(Const.memeryCacheSize))
				.memoryCacheSize(Const.memeryCacheSize).discCacheSize(Const.disCacheSize)
				.denyCacheImageMultipleSizesInMemory().discCacheFileCount(Const.disCacheSize)
				.discCache(new UnlimitedDiscCache(cacheDir)).threadPriority(Thread.NORM_PRIORITY - 2)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	public static ImageLoader getImageLoader(Context context) {

		if (null == imageLoader)
			initImageLoader(context);
		return imageLoader;
	}

	/**
	 * 获取常用的图片的信息设置
	 * 
	 * @param option
	 *            图片的解码配置
	 * @param loadingSrc
	 *            图片正在加载的时候显示的图片 可为空
	 * @param emptyUriSrc
	 *            图片地址为空的时候显示的图片 可为空
	 * @param failLoadSrc
	 *            图片加载失败显示的图片 可为空
	 */
	public static DisplayImageOptions getCommonOption(Options option, Integer loadingSrc, Integer emptyUriSrc,
			Integer failLoadSrc) {
		checkCacheSize();
		if (null != option) {
			displyOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(null != loadingSrc ? loadingSrc : R.drawable.ic_launcher)
					.showImageForEmptyUri(null != emptyUriSrc ? emptyUriSrc : R.drawable.ic_launcher)
					.showImageOnFail(null != failLoadSrc ? failLoadSrc : R.drawable.ic_launcher).cacheInMemory(true)
					.cacheOnDisc(true).decodingOptions(option).imageScaleType(ImageScaleType.EXACTLY).build();
		} else {
			displyOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(null != loadingSrc ? loadingSrc : R.drawable.ic_launcher)
					.showImageForEmptyUri(null != emptyUriSrc ? emptyUriSrc : R.drawable.ic_launcher)
					.showImageOnFail(null != failLoadSrc ? failLoadSrc : R.drawable.ic_launcher).cacheInMemory(true)
					.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY).build();
		}

		return displyOptions;
	}

	/**
	 * 获取常用的图片的信息设置
	 * 
	 * @param Options
	 *            图片的解码配置
	 * @see DisplayImageOptions
	 */
	public static DisplayImageOptions getCommonOption(Options option) {
		checkCacheSize();
		if (null != option) {
			displyOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher)
					.showImageForEmptyUri(R.drawable.ic_launcher).showImageOnFail(R.drawable.ic_launcher)
					.cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).decodingOptions(option)
					.imageScaleType(ImageScaleType.EXACTLY).build();
		} else {
			displyOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher)
					.showImageForEmptyUri(R.drawable.ic_launcher).showImageOnFail(R.drawable.ic_launcher)
					.cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
					.imageScaleType(ImageScaleType.EXACTLY).build();
		}
		return displyOptions;
	}

	/**
	 * 清除所有图片缓存
	 */
	public static void cleanCache() {
		ImageLoader.getInstance().clearDiscCache();
		ImageLoader.getInstance().clearMemoryCache();
	}

	/**
	 * 检查缓存消耗大小
	 */
	public static void checkCacheSize() {
		Collection<String> listStrings = ImageLoader.getInstance().getMemoryCache().keys();
		LogUtils.i(SDKApplication.appContext, "ImageManager", "===>cache count:" + listStrings.size(), true);
	}

}
