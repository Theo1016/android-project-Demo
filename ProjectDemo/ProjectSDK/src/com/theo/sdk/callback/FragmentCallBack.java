package com.theo.sdk.callback;

public interface FragmentCallBack {

    void onEnter(Object data);

    void onLeave();

    void onBack();

    void onBackWithData(Object data);

    boolean processBackPressed();
}