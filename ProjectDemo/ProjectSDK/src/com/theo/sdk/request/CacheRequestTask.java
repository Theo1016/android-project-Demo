package com.theo.sdk.request;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.theo.sdk.constant.Const;
import android.text.TextUtils;
import android.util.Log;

/**
 * 缓存处理Task
 * 
 * @author Theo
 */
public class CacheRequestTask implements Runnable {

	/** log tag. */
	private static final String TAG = CacheRequestTask.class.getSimpleName();

	/** if enabled, logcat will output the log. */
	private static final boolean DEBUG = true & Const.DEBUG;

	/**
	 * 线程池
	 */
	private static final ExecutorService THREAD_POOL = Executors
			.newFixedThreadPool(5,
					new NamingThreadFactory("CacheRequestTask"));

	/** cache 工具 */
	private DataCache mDataCache;

	/**
	 * 获取数据结果的Listener
	 */
	private OnCacheRequestListener mOnHttpRequestListener;

	/**
	 * 构造函数
	 * 
	 * @param dataCache
	 *            dataCache
	 * @param listener
	 *            回调Listener
	 */
	public CacheRequestTask(DataCache dataCache, OnCacheRequestListener listener) {
		mDataCache = dataCache;
		mOnHttpRequestListener = listener;
	}

	/**
	 * @throws Exception
	 */
	@Override
	public final void run() {

		if (mDataCache == null || !mDataCache.exist()) {
			return;
		}

		if (DEBUG) {
			Log.d(Const.LogTag,
					"---- start cache request time:"
							+ System.currentTimeMillis());
		}

		if (mOnHttpRequestListener == null) {
			return;
		}

		String ret = mDataCache.load();

		if (TextUtils.isEmpty(ret)) {
			mOnHttpRequestListener
					.onFailed(IRequestErrorCode.ERROR_CODE_NET_FAILED);
		} else {
			mOnHttpRequestListener.onSuccess(ret);
		}

	}

	/**
	 * 任务执行
	 */
	public void execute() {
		THREAD_POOL.execute(this);
	}

	/**
	 * 获取数据结果的Listener
	 */
	public interface OnCacheRequestListener {
		/**
		 * 获取数据成功
		 * 
		 * @param result
		 *            获取到的String数据
		 */
		void onSuccess(String result);

		/**
		 * 获取数据失败
		 * 
		 * @param errorCode
		 *            错误码
		 */
		void onFailed(int errorCode);
	}

	
}
