package com.theo.sdk.constant;

/**
 * 常量
 * 
 * @author Theo
 * 
 */
public class Const {
	/** 是否初始化完毕*/
	public static boolean isInit=false;
	/** 图片缓存 */
	public static final int memeryCacheSize = 20 * 1024 * 1024;
	public static final int disCacheSize = 100 * 1024 * 1024;
	public static final int disCacheCount = 500;
	/** 字符数据缓存 */
	public static final int DISCACHESIZESTRING = 50 * 1024 * 1024;
	/** 字符数据缓存时间 */
	public static final long CACHEALIVETIME = 5 * 60 * 1000;
	/** 缓存名称 */
	public static final String CACHENAME = "TEMP_CACHE";
	public static final int CACHE_TIME_DEFAULT = 60 * 1000;
	public static final int TIME_HOUR = 60 * 60;
	public static final int TIME_DAY = TIME_HOUR * 24;
	/** 自动缓存时间，默认缓存时间为CACHE_TIME_DEFAULT */
	public static final boolean isAutoTime = true;
	/** 日志统一Tag */
	public static final String LogTag = "Calabar";
	/** DEBUG开关 */
	public static final boolean DEBUG = false;
	/** Localbroadcast 标识*/
	public static final String LocalBroadCastTag ="LocalBroadCast";
	/** 生命周期*/
	public static final boolean DEBUG_LIFE_CYCLE = false;
	/** 显示适配基本参数*/
	public static float SCREEN_DENSITY;
    /** 屏幕像素*/
	public static int SCREEN_WIDTH_PIXELS;
	public static int SCREEN_HEIGHT_PIXELS;
	/** 屏幕DP*/
	public static int SCREEN_WIDTH_DP;
	public static int SCREEN_HEIGHT_DP;

}
