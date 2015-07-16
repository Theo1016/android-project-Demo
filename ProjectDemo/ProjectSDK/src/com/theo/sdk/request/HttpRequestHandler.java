package com.theo.sdk.request;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.theo.sdk.constant.Const;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

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
	private RequestParams requestParam;
	
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

	private HttpUriRequest mUriRequest;
	
	/** 
	 * DEBUG
	 */
	public static final boolean DEBUG = true & Const.DEBUG;

    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;
    public static final int DEFAULT_MAX_RETRIES = 5;
    public static final int DEFAULT_RETRY_SLEEP_TIME_MILLIS = 1500;
    public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;

    private int maxConnections = DEFAULT_MAX_CONNECTIONS;
    private int connectTimeout = DEFAULT_SOCKET_TIMEOUT;
    private int responseTimeout = DEFAULT_SOCKET_TIMEOUT;

    private DefaultHttpClient httpClient;
    private HttpContext httpContext;

    public static final String ENCODING_GZIP = "gzip";

    private ExecutorService threadPool;
    private boolean isUrlEncodingEnabled = true;

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_RANGE = "Content-Range";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

    private Map<String, String> clientHeaderMap;


    /**
	 * 参数提交试，默认Post提交
	 */
	private HttpRequestTask.RequestType mRequestType = HttpRequestTask.RequestType.POST;
	
	
	
	public HttpRequestHandler(Context ctx,String url,RequestParams param){
		this.mContext= ctx;
		this.requestUrl = url;
		this.requestParam = param;
		mUriRequest=addEntityToRequestBase(new HttpPost(URI.create(url).normalize()), paramsToEntity(requestParam, mTextHttpResponseHandler));
        SchemeRegistry schemeRegistry = getDefaultSchemeRegistry(false, 80, 443);
        initHttpRequest(schemeRegistry);
    }

    private void initHttpRequest(SchemeRegistry schemeRegistry) {
        BasicHttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setTimeout(httpParams, connectTimeout);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnections));
        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);

        HttpConnectionParams.setSoTimeout(httpParams, responseTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeout);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

        threadPool = getDefaultThreadPool();

        httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        httpClient = new DefaultHttpClient(cm, httpParams);
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) {
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
                for (String header : clientHeaderMap.keySet()) {
                    if (request.containsHeader(header)) {
                        Header overwritten = request.getFirstHeader(header);
                        //remove the overwritten header
                        request.removeHeader(overwritten);
                    }
                    request.addHeader(header, clientHeaderMap.get(header));
                }
            }
        });

        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext context) {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(entity));
                            break;
                        }
                    }
                }
            }
        });

        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
                AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                        ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);

                if (authState.getAuthScheme() == null) {
                    AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
                    Credentials creds = credsProvider.getCredentials(authScope);
                    if (creds != null) {
                        authState.setAuthScheme(new BasicScheme());
                        authState.setCredentials(creds);
                    }
                }
            }
        }, 0);

        httpClient.setHttpRequestRetryHandler(new RetryHandler(DEFAULT_MAX_RETRIES, DEFAULT_RETRY_SLEEP_TIME_MILLIS));
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
				mUriRequest, mPriority, mTextHttpResponseHandler);
		mHttpRequestTask.setRequestType(mRequestType);
//		mHttpRequestTask.setURLFilter(new HttpRequestTask.URLFilter() {
//			@Override
//			public String filter(String requestUrl, List<NameValuePair> params) {
//				// 过滤并添加统一需要的Url参数
//				String url = filterParams(requestUrl, params);
//				return url;
//			}
//		});
		mHttpRequestTask.execute();
	}

	/**
	 * Returns HttpEntity containing data from RequestParams included with request declaration.
	 * Allows also passing progress from upload via provided ResponseHandler
	 *∂
	 *∂
	 * @param params          additional request params
	 * @param responseHandler ResponseHandlerInterface or its subclass to be notified on progress
	 */
	private HttpEntity paramsToEntity(RequestParams params, ResponseHandlerInterface responseHandler) {
		HttpEntity entity = null;

		try {
			if (params != null) {
				entity = params.getEntity(responseHandler);
			}
		} catch (IOException e) {
			if (responseHandler != null) {
				responseHandler.sendFailureMessage(0, null, null, e);
			} else {
				e.printStackTrace();
			}
		}

		return entity;
	}


    /**
     * Get the default threading pool to be used for this HTTP client.
     *
     * @return The default threading pool to be used
     */
    protected ExecutorService getDefaultThreadPool() {
        return Executors.newCachedThreadPool();
    }
	/**
	 * Applicable only to HttpRequest methods extending HttpEntityEnclosingRequestBase, which is for
	 * example not DELETE
	 *
	 * @param entity      entity to be included within the request
	 * @param requestBase HttpRequest instance, must not be null
	 */
	private HttpEntityEnclosingRequestBase addEntityToRequestBase(HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
		if (entity != null) {
			requestBase.setEntity(entity);
		}

		return requestBase;
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
	 * Returns default instance of SchemeRegistry
	 *
	 * @param fixNoHttpResponseException Whether to fix issue or not, by omitting SSL verification
	 * @param httpPort                   HTTP port to be used, must be greater than 0
	 * @param httpsPort                  HTTPS port to be used, must be greater than 0
	 */
	private static SchemeRegistry getDefaultSchemeRegistry(boolean fixNoHttpResponseException, int httpPort, int httpsPort) {
		if (fixNoHttpResponseException) {
			Log.d(Const.LogTag, "Beware! Using the fix is insecure, as it doesn't verify SSL certificates.");
		}

		if (httpPort < 1) {
			httpPort = 80;
			Log.d(Const.LogTag, "Invalid HTTP port number specified, defaulting to 80");
		}

		if (httpsPort < 1) {
			httpsPort = 443;
			Log.d(Const.LogTag, "Invalid HTTPS port number specified, defaulting to 443");
		}

		// Fix to SSL flaw in API < ICS
		// See https://code.google.com/p/android/issues/detail?id=13117
		SSLSocketFactory sslSocketFactory;
		if (fixNoHttpResponseException) {
			sslSocketFactory = MySSLSocketFactory.getFixedSocketFactory();
		} else {
			sslSocketFactory = SSLSocketFactory.getSocketFactory();
		}

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), httpPort));
		schemeRegistry.register(new Scheme("https", sslSocketFactory, httpsPort));

		return schemeRegistry;
	}

    /**
     * Checks the InputStream if it contains  GZIP compressed data
     *
     * @param inputStream InputStream to be checked
     * @return true or false if the stream contains GZIP compressed data
     * @throws java.io.IOException
     */
    public static boolean isInputStreamGZIPCompressed(final PushbackInputStream inputStream) throws IOException {
        if (inputStream == null)
            return false;

        byte[] signature = new byte[2];
        int readStatus = inputStream.read(signature);
        inputStream.unread(signature);
        int streamHeader = ((int) signature[0] & 0xff) | ((signature[1] << 8) & 0xff00);
        return readStatus == 2 && GZIPInputStream.GZIP_MAGIC == streamHeader;
    }

    /**
     * A utility function to close an input stream without raising an exception.
     *
     * @param is input stream to close safely
     */
    public static void silentCloseInputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            Log.w(Const.LogTag, "Cannot close input stream", e);
        }
    }

    /**
     * A utility function to close an output stream without raising an exception.
     *
     * @param os output stream to close safely
     */
    public static void silentCloseOutputStream(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            Log.w(Const.LogTag, "Cannot close output stream", e);
        }
    }

    /**
     * Enclosing entity to hold stream of gzip decoded data for accessing HttpEntity contents
     */
    private static class InflatingEntity extends HttpEntityWrapper {

        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        InputStream wrappedStream;
        PushbackInputStream pushbackStream;
        GZIPInputStream gzippedStream;

        @Override
        public InputStream getContent() throws IOException {
            wrappedStream = wrappedEntity.getContent();
            pushbackStream = new PushbackInputStream(wrappedStream, 2);
            if (isInputStreamGZIPCompressed(pushbackStream)) {
                gzippedStream = new GZIPInputStream(pushbackStream);
                return gzippedStream;
            } else {
                return pushbackStream;
            }
        }

        @Override
        public long getContentLength() {
            return -1;
        }

        @Override
        public void consumeContent() throws IOException {
            silentCloseInputStream(wrappedStream);
            silentCloseInputStream(pushbackStream);
            silentCloseInputStream(gzippedStream);
            super.consumeContent();
        }
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
