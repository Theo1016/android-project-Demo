package com.theo.sdk.request;

import org.json.JSONException;

public interface ParseDataInterface {
	
	/**
     * 解析获取到的JSON数据
     * @param result result
     * @return 是否解析成功，注意：如果解析失败，请设置错误码mErrorCode
     * @throws JSONException JSONException
     * @throws Exception Exception
     */
	public boolean parseResult(String result) throws JSONException, Exception;

}
