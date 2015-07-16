package com.theo.sdk.request;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.theo.sdk.constant.Const;
import com.theo.sdk.request.CacheRequestTask.OnCacheRequestListener;
import com.theo.sdk.request.HttpRequestHandler.OnHttpRequestHandlerListener;

import org.json.JSONException;

/**
 * 
 * Requestor从它来获取网络访问信息，并解析得到的数据 Requestor的基类，所有的接口Requestor本质上都从它继承
 * 执行doRequest()方法开始异步获取数据，并由onReqeustListener监听回调结果状态
 * 
 * @author Theo
 */
public abstract class AbstractRequestor{

	/** context */
	protected Context mContext;
	/**
	 * 主线程Handler
	 */
	private Handler mHandler;

	/** Cache 工具 */
	private DataCache mDataCache;
	
	/** 是否开启读缓存 */
	private boolean mReadCacheFlag;
	
	/** 是否开启写缓存 */
	private boolean mWriteCacheFlag;

	/** 解析数据接口 */
	private ParseDataInterface parseDateInterface;

	/** 请求数据接口 */
	private RequestInterface requestInterface;

	/** 是否延时读取联网返回的数据以防止阻塞UI */
	private boolean mParseNetDataDelayed = false;
	
	/** 读取本地缓存和网上缓存的最小时间间隔 */
	private static final long TIME_INTERVAL = 2 * DateUtils.SECOND_IN_MILLIS;
	/**
	 * 数据请求结果回调Listener
	 */
	protected OnRequestListener mOnRequestListener;
	
	/**
	 * Http请求结果回调Listener
	 */
	protected OnHttpRequestHandlerListener mOnHttpRequestHandlerListener;

	/** 获取cache listener */
	private OnCacheLoadListener mCacheLoadListener;

	/** 错误码 */
	private int mErrorCode = IRequestErrorCode.ERROR_CODE_UNKNOW;

	/** DEBUG */
	public static final boolean DEBUG = true & Const.DEBUG;

	/**
	 * 是否任务已经取消
	 */
	private boolean mIsCanceled;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            Context
	 */
	public AbstractRequestor(Context context) {
		mContext = context.getApplicationContext();
	}

	/**
	 * 设置解析方式
	 * 
	 * @param parseData
	 */
	public void setParseDataMethod(ParseDataInterface parseData) {
		this.parseDateInterface = parseData;
	}
	
	/**
	 * 设置请求方式
	 * @param request
	 */
	public void setRequestMethod(RequestInterface request){
		this.requestInterface = request;
	}

	/**
	 * 是否有可用缓存数据
	 * 
	 * @return 是否有可用缓存数据
	 */
	boolean canUseCache() {
		return mDataCache != null && mReadCacheFlag;
	}

	/**
	 * 设置是否延时读取联网返回的数据以防止阻塞UI，sleep线程
	 * 
	 * @param parseNetDataDelayed
	 *            true:延时解析
	 */
	public void setParseNetDataDelayed(boolean parseNetDataDelayed) {
		this.mParseNetDataDelayed = parseNetDataDelayed;
	}

	/**
	 * 获取主线程的handler
	 * 
	 * @return 主线程的handler
	 */
	public synchronized Handler getHandler() {
		if (mHandler == null) {
			mHandler = new Handler(Looper.getMainLooper());
		}
		return mHandler;
	}

	/**
	 * 是否需要缓冲网络数据
	 * 
	 * @return 是否需要缓冲网络数据
	 */
	boolean needCacheData() {
		return mDataCache != null && mWriteCacheFlag;
	}

	/**
	 * 打开写缓存功能
	 * 
	 * @param cacheId
	 *            缓存文件名称
	 */
	public void turnOnWriteCache(String cacheId) {
		turnOnCache(cacheId, null, mReadCacheFlag, true);
	}

	/**
	 * 打开读缓存功能
	 * 
	 * @param cacheId
	 *            缓存文件名称
	 * @param cacheLoadListener
	 *            缓存读取成功的回调
	 */
	public void turnOnReadCache(String cacheId,
			OnCacheLoadListener cacheLoadListener) {
		turnOnCache(cacheId, cacheLoadListener, true, mWriteCacheFlag);
	}

	/**
	 * 执行打开缓存功能的方法，私有不对外开放
	 * 
	 * @param cacheId
	 *            缓存文件名
	 * @param cacheLoadListener
	 *            Listener，在读取缓存成功时会调用
	 * @param readFlag
	 *            是否开启读缓存功能
	 * @param writeFlag
	 *            是否开启定缓存功能
	 */
	private void turnOnCache(String cacheId,
			OnCacheLoadListener cacheLoadListener, boolean readFlag,
			boolean writeFlag) {
		if (TextUtils.isEmpty(cacheId)) {
			return;
		}
		mCacheLoadListener = cacheLoadListener;
		mDataCache = new DataCache(cacheId, mContext.getCacheDir(),
				mContext.getAssets());
		mReadCacheFlag = readFlag;
		mWriteCacheFlag = writeFlag;
	}

	/**
	 * 关闭cache,默认为关闭状态
	 */
	public void turnOffCache() {
		mDataCache = null;
		mCacheLoadListener = null;

		mReadCacheFlag = false;
		mWriteCacheFlag = false;
	}

	/**
	 * 发起数据请求
	 * 
	 * @param listener
	 *            数据请求结果Listener
	 */
	public void doRequest(OnRequestListener listener) {
		// 条件符合,则使用缓存
		useCacheIfCould();
		//初始化网络请求回调
		init(listener);
		// 网络请求
		requestInterface.request(mOnHttpRequestHandlerListener);

	}

	/**
	 * 初始化请求的回调，及其它必要内容
	 */
	private void init(OnRequestListener listener) {
		mOnRequestListener = listener;
		// 请求数据时，要求回调
		if (mOnRequestListener != null) {
			getHandler();
			mOnHttpRequestHandlerListener = new HttpRequestHandler.OnHttpRequestHandlerListener() {
				@Override
				public void onSuccess(String result) {
					// 此请求已经被Cancel
					if (mIsCanceled) {
						return;
					}
					// 解析数据
					boolean parseResult = false;
					try {
						if (DEBUG) {
							Log.d(Const.LogTag, "abs requestor result:"
									+ result);
						}
						parseResult = parseDateInterface.parseResult(result);
						if (parseResult) {
							responseRequestSuccess();
						} else {
							responseRequestFailed(mErrorCode);
						}
					} catch (JSONException je) {
						responseRequestFailed(IRequestErrorCode.ERROR_CODE_RESULT_IS_NOT_JSON_STYLE);
						je.printStackTrace();
					} catch (Exception e) {
						responseRequestFailed(IRequestErrorCode.ERROR_CODE_PARSE_DATA_ERROR);
						e.printStackTrace();
					}

					// 解析成功再进行存储
					if (parseResult) {
						cacheDataIfNeed(result);
					}
				}

				@Override
				public void onFailed(final int errorCode) {

					// 此请求已经被Cancel
					if (mIsCanceled) {
						return;
					}
					responseRequestFailed(errorCode);
				}
			};
		}
	}

	/**
	 * 如果需要,缓存数据
	 * 
	 * @param data
	 *            数据
	 */
	private void cacheDataIfNeed(String data) {
		if (DEBUG) {
			Log.d(Const.LogTag, "cacheDataIfNeed data:" + data);
		}
		if (mDataCache == null) {
			return;
		}

		if (needCacheData()) {
			mDataCache.save(data);
		}
	}

	/**
	 * 返回数据拉取成功的结果给Listener
	 */
	private void responseRequestSuccess() {
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mOnRequestListener.onSuccess(AbstractRequestor.this);
				}
			});
		} else {
			mOnRequestListener.onSuccess(AbstractRequestor.this);
		}
	}

	/**
	 * 返回数据拉取失败的结果给Listener
	 * 
	 * @param errorCode
	 *            错误码
	 */
	private void responseRequestFailed(final int errorCode) {
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mOnRequestListener.onFailed(AbstractRequestor.this,
							errorCode);
				}
			});
		} else {
			mOnRequestListener.onFailed(AbstractRequestor.this, errorCode);
		}
	}

	/**
	 * 如何可以,使用缓存数据
	 */
	private void useCacheIfCould() {
		if (mDataCache == null) {
			return;
		}
		if (canUseCache()) {
			new CacheRequestTask(mDataCache, new OnCacheRequestListener() {
				@Override
				public void onSuccess(String result) {
					// 解析数据
					boolean isParseSuccess = false;
					try {
						isParseSuccess = parseDateInterface.parseResult(result);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (isParseSuccess) {
						// 回调
						if (mHandler != null) {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									if (mCacheLoadListener != null) {
										mCacheLoadListener
												.onCacheLoaded(AbstractRequestor.this);
									}
								}
							});
						} else {
							if (mCacheLoadListener != null) {
								mCacheLoadListener
										.onCacheLoaded(AbstractRequestor.this);
							}
						}
					}
				}

				@Override
				public void onFailed(int errorCode) {
				}
			}).execute();
		}
	}


	/**
	 * 缓存回调
	 * 
	 * @author Theo
	 * 
	 */
	public interface OnCacheLoadListener {

		/**
		 * cache获取成功
		 * 
		 * @param requestor
		 *            requestor
		 */
		void onCacheLoaded(AbstractRequestor requestor);
	}

	/**
	 * 前端数据回调
	 * 
	 * @author Theo
	 * 
	 */
	public interface OnRequestListener {

		/**
		 * 获取成功
		 * 
		 * @param requestor
		 *            requestor
		 */
		void onSuccess(AbstractRequestor requestor);

		/**
		 * 获取失败
		 * 
		 * @param requestor
		 *            requestor
		 * @param errorCode
		 *            错误码
		 */
		void onFailed(AbstractRequestor requestor, int errorCode);

	}

}
