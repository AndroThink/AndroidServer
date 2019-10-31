package com.androthink.server.core;

import android.content.Context;
import android.util.Log;

import com.androthink.server.callback.RequestCallBack;
import com.androthink.server.callback.ServerCallBack;
import com.androthink.server.helper.ServerHelper;
import com.androthink.server.model.Route;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Vector;

class ServerRunnable implements Runnable , RequestCallBack {

    private final Context context;
    private ServerSocket serverSocket;
    private final static String TAG = "ServerRunnable";

    private final ServerCallBack serverCallBack;

    // indicator to determine whether the server has isRunning or not
    private boolean isRunning = true;

    // Clients Threads ..
    private final Vector<ServerRequestThread> serverRequestThreads;

    ServerRunnable(Context context,int port, List<Route> routeList, ServerCallBack serverCallBack) throws IOException {
        this.context = context;
        this.serverCallBack = serverCallBack;

        this.serverRequestThreads = new Vector<>();
        this.serverSocket = new ServerSocket(port);

        RoutesHandler.getInstance().applyRoutes(routeList);
    }

    public void run() {

        Log.d(TAG  , "Server Started .");
        Log.d(TAG  , "Server Running : " + this.isRunning);

        while (this.isRunning) {

            try {
                Socket clientSocket = this.serverSocket.accept();

                if(clientSocket != null) {
                    serverRequestThreads.add(new ServerRequestThread(context,clientSocket, ServerHelper.generateRandomId(),this));
                }

            } catch (SocketException e) {
                if (!this.isRunning)
                    break;

                e.printStackTrace();

            } catch (IOException e) {
                if (!this.isRunning)
                    break;

                e.printStackTrace();
            }
        }

        serverCallBack.onServerStopped();
        Log.d(TAG, "Server Stopped !");
    }

    private void terminate() {

        Log.w(TAG, "Server Terminating ...");

        for (Thread serverThread : serverRequestThreads) {
            if (serverThread.isAlive()) serverThread.interrupt();
        }

        try {
            if (!this.serverSocket.isClosed())
                this.serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();

            Log.e(TAG, "Server Socket Exception While Terminating !");
        }

        Log.i(TAG, "Server Terminated !");
    }

    void stop() {
        Log.i(TAG, "Stopping Server ...");

        this.isRunning = false;
        terminate();
    }

    @Override
    public void onRequestComplete(String clientId) {
        Log.e(TAG,"Request Completed : " + clientId);
        Log.e(TAG,"Total Requests : " + serverRequestThreads.size());

        for (ServerRequestThread serverThread: serverRequestThreads) {
            if(serverThread.getRequest().getRequestId().equals(clientId)) {
                if(serverThread.isAlive())
                    serverThread.interrupt();

                serverRequestThreads.remove(serverThread);
                break;
            }
        }

        Log.e(TAG,"Total Requests : " + serverRequestThreads.size());
    }

    @Override
    public void onError(String clientId) {
        Log.e(TAG,"Request Error : " + clientId);

        for (ServerRequestThread serverThread: serverRequestThreads) {
            if(serverThread.getRequest().getRequestId().equals(clientId)) {
                serverThread.close();
                break;
            }
        }

        Log.e(TAG,"Total Requests : " + serverRequestThreads.size());
    }
}