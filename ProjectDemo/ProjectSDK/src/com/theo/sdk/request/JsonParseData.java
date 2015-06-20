package com.theo.sdk.request;

import org.json.JSONException;

public class JsonParseData extends JsonHttpResponseHandler implements ParseDataInterface {

	public static final String UTF8_BOM = "\uFEFF";

	@Override
	public boolean parseResult(String result) throws JSONException, Exception {
		return false;
	}

}
