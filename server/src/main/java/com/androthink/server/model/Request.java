package com.androthink.server.model;

import com.androthink.server.callback.RequestCallBack;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private String apiKey;
    private String requestId;
    private String requestMethod;
    private String requestProtocol;
    private String requestRoutePath;

    private String requestClientAddress;

    private final RequestCallBack requestCallBack;

    private Object requestPayload;
    private Map<String,String> requestHeaders;
    private Map<String,String> requestCookies;

    public Request(String requestId,String clientAddress,RequestCallBack callBack){
        this.requestId = requestId;
        this.requestClientAddress = clientAddress;
        this.requestCallBack = callBack;
        this.requestHeaders = new HashMap<>();
        this.requestCookies = new HashMap<>();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getMethod() {
        return requestMethod;
    }

    public void setMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getProtocol() {
        return requestProtocol;
    }

    public void setProtocol(String requestProtocol) {
        this.requestProtocol = requestProtocol;
    }

    public String getRoutePath() {
        return requestRoutePath;
    }

    public void setRoutePath(String requestRoutePath) {
        this.requestRoutePath = requestRoutePath;
    }

    public String getClientAddress() { return requestClientAddress; }

    public Object getPayload() {
        return requestPayload;
    }

    public void setPayload(Object requestPayload) {
        this.requestPayload = requestPayload;
    }

    public Map<String, String> getHeaders() {
        return requestHeaders;
    }

    public void setHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public void setRequestCookies(Map<String, String> requestCookies) {
        this.requestCookies = requestCookies;
    }

    public Map<String, String> getRequestCookies() {
        return requestCookies;
    }

    public void onError(){
        if(this.requestCallBack != null)
            this.requestCallBack.onError(this.requestId);
    }

    public void onComplete(){
        if(this.requestCallBack != null)
            this.requestCallBack.onRequestComplete(this.requestId);
    }
}
