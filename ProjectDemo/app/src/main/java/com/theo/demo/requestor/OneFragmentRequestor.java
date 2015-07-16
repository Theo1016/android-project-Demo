package com.theo.demo.requestor;

import android.content.Context;

import com.theo.sdk.request.AbstractRequestor;
import com.theo.sdk.request.HttpRequestHandler;
import com.theo.sdk.request.JsonParseData;
import com.theo.sdk.request.RequestParams;

/**
 * Created by Theo on 15/6/29.
 */
public class OneFragmentRequestor extends AbstractRequestor{
    JsonParseData parseData;
    HttpRequestHandler requestHandler;
    String url="";
    /**
     * 构造函数
     * 构造请求参数，URL
     * @param context Context
     */
    public OneFragmentRequestor(Context context) {
        super(context);
        String testParam="";
        RequestParams params = new RequestParams();
        params.add("测试",testParam);
        params.setUseJsonStreamer(true);
        requestHandler = new HttpRequestHandler(context,url,params);
        //设置解析方式和请求方式
        this.setParseDataMethod(parseData);
        this.setRequestMethod(requestHandler);
    }



}
