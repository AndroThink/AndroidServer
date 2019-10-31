package com.androthink.server.callback;

public interface RequestCallBack {
    void onRequestComplete(String requestId);
    void onError(String requestId);
}
