package com.androthink.server.callback;

import com.androthink.server.handler.ResponseHandler;
import com.androthink.server.model.Request;

import java.io.IOException;

public interface RouteCallBack {
    void onRequested(Request request, ResponseHandler responseHandler)throws IOException;
}
