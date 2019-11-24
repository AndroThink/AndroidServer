package com.androthink.server.model;

import com.androthink.server.callback.RouteCallBack;

public class Route {
    private String path;
    private String method;
    private String Description;
    private boolean isAuth;
    private boolean isRouteStartWith;
    private RouteCallBack callBack;

    /**
     * @param path     route path
     * @param method   request method
     * @param callBack response callback
     */
    public Route(String path, String method, RouteCallBack callBack) {
        this.path = path;
        this.isAuth = false;
        this.isRouteStartWith = false;
        this.method = method;
        this.callBack = callBack;
    }

    /**
     * @param path     route path
     * @param method   request method
     * @param isAuth   is authenticated
     * @param callBack response callback
     */
    public Route(String path, String method, boolean isAuth, RouteCallBack callBack) {
        this.path = path;
        this.isAuth = isAuth;
        this.method = method;
        this.callBack = callBack;
        this.isRouteStartWith = false;
    }

    /**
     * @param path             route path
     * @param method           request method
     * @param isAuth           is authenticated
     * @param isRouteStartWith is route paths is a start with
     * @param callBack         response callback
     */
    public Route(String path, String method, boolean isAuth, boolean isRouteStartWith, RouteCallBack callBack) {
        this.path = path;
        this.method = method;
        this.isAuth = isAuth;
        this.isRouteStartWith = isRouteStartWith;
        this.callBack = callBack;
    }

    public boolean isRouteStartWith() {
        return isRouteStartWith;
    }

    public void setRouteStartWith(boolean routeStartWith) {
        isRouteStartWith = routeStartWith;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public RouteCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(RouteCallBack callBack) {
        this.callBack = callBack;
    }
}
