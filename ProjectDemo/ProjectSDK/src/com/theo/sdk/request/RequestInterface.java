package com.theo.sdk.request;

import com.theo.sdk.request.HttpRequestHandler.OnHttpRequestHandlerListener;

public interface RequestInterface {
	/**
     * 发起数据请求
     * @param listener 数据请求结果Listener
     */
    public void request(final OnHttpRequestHandlerListener listener);
}
