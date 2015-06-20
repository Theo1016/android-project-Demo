package com.theo.sdk.callback;

import com.theo.sdk.bean.ResponseBean;

/**
 * 请求回调
 * @author Theo
 *
 */
public interface HttpCallBack {

	void onSuccess(ResponseBean responseBeah);
	
	void onError(ResponseBean responseBeah);
}
