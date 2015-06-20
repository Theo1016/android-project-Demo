package com.theo.sdk.bean;

import java.util.Arrays;

import org.apache.http.Header;

/**
 * 请求BEAN
 * @author Theo
 *
 */
public class ResponseBean {
	// 响应码
	public int code;
	// 处理过的返回数据
	public Object data;
	// 错误信息
	public String errorMsg;
	// 投头段
	public Header[] headers;
	// 每一个请求的唯一标志符
	public String flagId;
	// 数据是否来自缓存
	public int isFromCache;
	// 错误异常信息
	public Throwable throwable;

	@Override
	public String toString() {
		return "ResponseBean [code=" + code + ", data=" + data + ", errorMsg="
				+ errorMsg + ", headers=" + Arrays.toString(headers)
				+ ", flagId=" + flagId + ", isFromCache=" + isFromCache + "]";
	}

}
