package com.theo.sdk.request;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.theo.sdk.constant.Const;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * 网络请求
 * 
 * @author Theo
 * 
 */
public class HttpRequestTask implements Runnable {
	/** log tag. */
	private final String TAG = Const.LogTag;

	/** if enabled, logcat will output the log. */
	private final boolean DEBUG = true & Const.DEBUG;

	/** 访问网络失败时，重试间隔时间 */
	private final long SLEEP_TIME_WHILE_REQUEST_FAILED = 1000L;

	private AbstractHttpClient client;
	
	private HttpUriRequest request;
	
	private HttpContext context;
	
	private int executionCount;
	
    private boolean isCancelled;
    
    private boolean isFinished;
    
    private boolean cancelIsNotified;

	/**
	 * 线程池
	 */
	private final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(5,
			new NamingThreadFactory("HttpRequestTask"));

	/**
	 * 参数提交方式
	 */
	public enum RequestType {
		/** GET方式提交 */
		GET,
		/** POST方式提交 */
		POST;
	}

	/**
	 * 参数提交试，默认Post提交
	 */
	private RequestType mRequestType = RequestType.POST;

	/**
	 * 任务是否已经删除
	 */
	private AtomicBoolean mIsCancel = new AtomicBoolean();

	/**
	 * 线程优先级
	 */
	private int mPriority;

	/**
	 * 访问Url
	 */
	private String mUrl;


	/**
	 * 网络请求结果反馈
	 */
	private OnHttpRequestTaskListener mOnHttpRequestTaskListener;
	
	/**
	 * 
	 */
	private ResponseHandlerInterface responseHandler;

	/** context */
	private Context mContext;

	/** Url过滤器 */
	private URLFilter mURLFilter;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            Context
	 * @param url
	 *            请求地址
	 * @param uriRequest
	 *            请求参数
	 * @param listener
	 *            回调Listener
	 */
	public HttpRequestTask(Context context, String url,
			HttpUriRequest uriRequest, ResponseHandlerInterface listener) {
		this(context, url, uriRequest, Process.THREAD_PRIORITY_DEFAULT, listener);
	}
	
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            Context
	 * @param url
	 *            请求地址
	 * @param uriRequest
	 *            请求参数
	 * @param priority
	 *            线程优先级
	 * @param responseHandlerInterface
	 *            回调responseHandlerInterface
	 */
	public HttpRequestTask(Context context, String url,
			HttpUriRequest uriRequest, int priority,
			ResponseHandlerInterface responseHandlerInterface) {
		mContext = context.getApplicationContext();
		mPriority = priority;
		mUrl = url;
		request = uriRequest;
		responseHandler = responseHandlerInterface;
	}

	/**
	 * @throws Exception
	 */
	@Override
	public final void run() {
		if (DEBUG) {
			Log.d(TAG,
					"---- start web request time:" + System.currentTimeMillis());
		}
		// 线程优化级
		Process.setThreadPriority(mPriority);
		if (mIsCancel.get()) {
			// 请求已经撤销
			return;
		}
		if (mUrl == null) {
			if (mOnHttpRequestTaskListener != null) {
				mOnHttpRequestTaskListener
						.onFailed(IRequestErrorCode.ERROR_CODE_NO_URL);
			}
			return;
		}
//		// 过滤Url
//		if (mURLFilter != null) {
//			mUrl = mURLFilter.filter(mUrl, mParams);
//		}
		try {
			makeRequestWithRetries();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (mOnHttpRequestTaskListener != null) {
			mOnHttpRequestTaskListener
					.onFailed(IRequestErrorCode.ERROR_CODE_NET_FAILED);
		}
	}

	private void makeRequestWithRetries() throws IOException {
		boolean retry = true;
		IOException cause = null;
		HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
		try {
			while (retry) {
				try {
					makeRequest();
					return;
				} catch (UnknownHostException e) {
					cause = new IOException("UnknownHostException exception: "
							+ e.getMessage());
					retry = (executionCount > 0)
							&& retryHandler.retryRequest(cause,
									++executionCount, context);
				} catch (NullPointerException e) {
					cause = new IOException("NPE in HttpClient: "
							+ e.getMessage());
					retry = retryHandler.retryRequest(cause, ++executionCount,
							context);
				} catch (IOException e) {
					if (isCancelled()) {
						return;
					}
					cause = e;
					retry = retryHandler.retryRequest(cause, ++executionCount,
							context);
				}
				if (retry && (responseHandler != null)) {
					responseHandler.sendRetryMessage(executionCount);
				}
			}
		} catch (Exception e) {
			// catch anything else to ensure failure message is propagated
			Log.e("AsyncHttpRequest", "Unhandled exception origin cause", e);
			cause = new IOException("Unhandled exception: " + e.getMessage());
		}
		// cleaned up to throw IOException
        throw (cause);
	}
	
	private void makeRequest() throws IOException {
        if (isCancelled()) {
            return;
        }

        // Fixes #115
        if (request.getURI().getScheme() == null) {
            // subclass of IOException so processed in the caller
            throw new MalformedURLException("No valid URI scheme was provided");
        }

        HttpResponse response = client.execute(request, context);

        //获取sessionid并设置进header
        Header h = response.getFirstHeader("Set-Cookie");
        if(h != null){
        	String sessionid = h.getValue();
        	
        }
        
        if (isCancelled() || responseHandler == null) {
            return;
        }

        // Carry out pre-processing for this response.
        responseHandler.onPreProcessResponse(responseHandler, response);

        if (isCancelled()) {
            return;
        }

        // The response is ready, handle it.
        responseHandler.sendResponseMessage(response);

        if (isCancelled()) {
            return;
        }

        // Carry out post-processing for this response.
        responseHandler.onPostProcessResponse(responseHandler, response);
    }
	
	public boolean isCancelled() {
        if (isCancelled) {
            sendCancelNotification();
        }
        return isCancelled;
    }

    private synchronized void sendCancelNotification() {
        if (!isFinished && isCancelled && !cancelIsNotified) {
            cancelIsNotified = true;
            if (responseHandler != null)
                responseHandler.sendCancelMessage();
        }
    }

	public boolean cancel(boolean mayInterruptIfRunning) {
        isCancelled = true;
        request.abort();
        return isCancelled();
    }
	/**
	 * 任务执行
	 */
	public void execute() {
		THREAD_POOL.execute(this);
	}

	/**
	 * @return 是否已经撤销
	 */
	public boolean isCancel() {
		return mIsCancel.get();
	}

	/**
	 * 撤销任务执行
	 */
	public void cancel() {
		mIsCancel.set(true);
	}

	/**
	 * @return the mRequestType
	 */
	public RequestType getRequestType() {
		return mRequestType;
	}

	/**
	 * @param requestType
	 *            the mRequestType to set
	 */
	public void setRequestType(RequestType requestType) {
		this.mRequestType = requestType;
	}

	/**
	 * 设置过滤器
	 * 
	 * @param urlFilter
	 *            过滤器
	 */
	public void setURLFilter(URLFilter urlFilter) {
		this.mURLFilter = urlFilter;
	}

	/**
	 * 获取数据结果的Listener
	 */
	public interface OnHttpRequestTaskListener {
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

	/**
	 * Url过滤器
	 */
	public interface URLFilter {
		/**
		 * 过滤Url
		 * 
		 * @param requestUrl
		 *            请求的Url
		 * @param params
		 *            请求参数
		 * @return 过滤后的Url
		 */
		String filter(String requestUrl, List<NameValuePair> params);
	}
}
