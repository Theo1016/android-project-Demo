package com.theo.sdk.request;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import com.theo.sdk.constant.Const;

public class HttpRequestHandler implements RequestInterface{
	
	/**
	 *  context 
	 */
	protected Context mContext;
	
	/** 
	 * 请求Url
	 */
	private String requestUrl;
	
	/** 
	 * 请求参数
	 */
	private List<NameValuePair> requestParam;
	
	/**
	 * 是否任务已经取消
	 */
	private boolean mIsCanceled;
	
	/**
	 * 数据请求结果回调Listener
	 */
	private OnHttpRequestHandlerListener mOnHttpRequestHandlerListener;
	
	/**
	 * 异步Http回调
	 */
	private TextHttpResponseHandler mTextHttpResponseHandler;
	
	/**
	 * 网络数据请求Task
	 */
	private HttpRequestTask mHttpRequestTask;
	
	/**
	 * 线程优先级
	 */
	private int mPriority = Process.THREAD_PRIORITY_DEFAULT;
	
	/** 
	 * DEBUG
	 */
	public static final boolean DEBUG = true & Const.DEBUG;
	
	/**
	 * 参数提交试，默认Post提交
	 */
	private HttpRequestTask.RequestType mRequestType = HttpRequestTask.RequestType.POST;
	
	
	
	public HttpRequestHandler(Context ctx,String url,List<NameValuePair> param){
		this.mContext= ctx;
		this.requestUrl = url;
		this.requestParam = param;
	}

	
	/**
	 * 发起数据请求
	 * 
	 * @param listener
	 *            数据请求结果Listener
	 */
	@Override
	public void request(final OnHttpRequestHandlerListener listener) {
		//初始化请求的回调，及其它必要内容
		init();
		mOnHttpRequestHandlerListener = listener;
		mHttpRequestTask = new HttpRequestTask(mContext, requestUrl,
				requestParam, mPriority, mTextHttpResponseHandler);
		mHttpRequestTask.setRequestType(mRequestType);
		mHttpRequestTask.setURLFilter(new HttpRequestTask.URLFilter() {
			@Override
			public String filter(String requestUrl, List<NameValuePair> params) {
				// 过滤并添加统一需要的Url参数
				String url = filterParams(requestUrl, params);
				return url;
			}
		});
	}
	
	/**
	 * 初始化请求的回调，及其它必要内容
	 */
	private void init() {
		// 请求数据时，要求回调
		if (mOnHttpRequestHandlerListener != null) {
			mTextHttpResponseHandler = new TextHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String responseString) {
					mOnHttpRequestHandlerListener.onSuccess(responseString);
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					mOnHttpRequestHandlerListener.onFailed(statusCode);
				}
			};
		}
	}
	
	/**
	 * 过滤参数，将Url中那些在Param中出现的参数过滤掉
	 * 
	 * @param orginalUrl
	 *            原始请求Url
	 * @param params
	 *            请求参数
	 * @return 过滤后的Url
	 */
	private String filterParams(String orginalUrl, List<NameValuePair> params) {

		String url = orginalUrl;

		if (params != null) {
			for (NameValuePair param : params) {
				Pattern pattern = Pattern.compile("[\\?\\&]" + param.getName()
						+ "\\=[^\\&\\?]*");
				Matcher matcher = pattern.matcher(url);
				if (matcher.find()) {
					String group = matcher.group();
					if (group.startsWith("?")) {
						url = matcher.replaceAll("?");
					} else {
						url = matcher.replaceAll("");
					}
				}
			}
		}

		return url;
	}
	
	/**
	 * 数据回调
	 * 
	 * @author Theo
	 * 
	 */
	public interface OnHttpRequestHandlerListener {
		
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
