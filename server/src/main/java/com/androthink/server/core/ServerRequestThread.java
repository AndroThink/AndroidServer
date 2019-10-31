package com.androthink.server.core;

import android.content.Context;

import com.androthink.server.callback.RequestCallBack;
import com.androthink.server.handler.ResponseHandler;
import com.androthink.server.helper.ServerHelper;
import com.androthink.server.model.Request;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class ServerRequestThread extends Thread {

    private final Context context;
    private final Socket clientSocket;

    private ResponseHandler responseHandler = null;

    private Request request;

    ServerRequestThread(Context context, Socket socket, String requestId, RequestCallBack requestCallBack) {

        this.context = context;
        this.request = new Request(requestId,requestCallBack);
        this.clientSocket = socket;

        start();
    }

    public void run() {

        try {
            responseHandler = ResponseHandler
                    .getInstance(context, new DataOutputStream(clientSocket.getOutputStream()));
        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            request = RequestHandler.getInstance(request,
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))).extract();
            // Handle Request ==> Response ..
            RoutesHandler.getInstance().Handle(request,responseHandler);
        }
        catch (IOException e) {
            try {
                if (responseHandler != null)
                    responseHandler.sendJsonResponse(ServerHelper.RESPONSE_CODE.NOT_FOUND,
                            "{\"status\":false,\"error\":\"FileNotFound : " + e.toString()+ "\"}");
            }catch (IOException ex){ ex.printStackTrace(); }

            e.printStackTrace();
        }
        catch (Exception e) {
            try {
                if (responseHandler != null) {
                    String route = request.getRoutePath();
                    if (route != null && route.startsWith("/api"))
                        responseHandler.sendJsonResponse(ServerHelper.RESPONSE_CODE.INTERNAL_SERVER_ERROR, "{\"status\":false,\"error\":\"Internal Server Error : " + e.toString() + "\"}");
                    else {
                        Map<String, String> placeHolders = new HashMap<>();
                        placeHolders.put("error_place_holder", "" + e.toString());
                        responseHandler.sendHtmlFileResponse(ServerHelper.RESPONSE_CODE.NOT_FOUND, "html/500.html", placeHolders);
                    }
                }
            }catch (IOException ex){ ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            close();
        }
    }

    void close(){
        if (this.clientSocket != null) {
            try {
                if (!this.clientSocket.isClosed())
                    this.clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        request.onComplete();
    }

    Request getRequest(){return this.request;}
}