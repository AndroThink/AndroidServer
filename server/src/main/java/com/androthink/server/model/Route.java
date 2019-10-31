package com.androthink.server.model;

import com.androthink.server.callback.RouteCallBack;

public class Route {
    private String path;
    private String method;
    private boolean isAuth;
    private RouteCallBack callBack;

    public Route(String path, String method, RouteCallBack callBack) {
        this.path = path;
        this.isAuth = false;
        this.method = method;
        this.callBack = callBack;
    }

    public Route(String path, String method,boolean isAuth, RouteCallBack callBack) {
        this.path = path;
        this.isAuth = isAuth;
        this.method = method;
        this.callBack = callBack;
    }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    public String getMethod() { return method; }

    public void setMethod(String method) { this.method = method; }

    public boolean isAuth() { return isAuth; }

    public void setAuth(boolean auth) { isAuth = auth; }

    public RouteCallBack getCallBack() { return callBack; }

    public void setCallBack(RouteCallBack callBack) { this.callBack = callBack; }
}
