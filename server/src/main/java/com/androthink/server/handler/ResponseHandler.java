package com.androthink.server.handler;

import android.content.Context;

import androidx.annotation.NonNull;

import com.androthink.server.helper.ServerHelper;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class ResponseHandler {

    private final Context context;
    private final DataOutputStream responseStream;

    public static ResponseHandler getInstance(Context context, DataOutputStream responseStream) {
        return new ResponseHandler(context, responseStream);
    }

    private ResponseHandler(Context context, DataOutputStream responseStream) {
        this.context = context;
        this.responseStream = responseStream;
    }

    /**
     * @param json Json to be sent as Response .
     * @throws IOException throws exception if response channel closed .
     */
    // JSON Responses ..
    public void sendJsonResponse(String json) throws IOException {
        if (json != null) {
            byte[] data = json.getBytes("UTF-8");
            sendResponseHeader(ServerHelper.RESPONSE_CODE.OK, ServerHelper.CONTENT_TYPE.JSON, data.length);
            this.responseStream.write(data);
        }
        this.responseStream.flush();
        this.responseStream.close();
    }

    /**
     * @param json          Json to be sent as Response .
     * @param customHeaders headers to be sent with the response .
     * @throws IOException throws exception if response channel closed .
     */
    public void sendJsonResponse(String json, Map<String, String> customHeaders) throws IOException {
        if (json != null) {
            byte[] data = json.getBytes("UTF-8");
            sendResponseHeader(ServerHelper.RESPONSE_CODE.OK, ServerHelper.CONTENT_TYPE.JSON, data.length, customHeaders);
            this.responseStream.write(data);
        }
        this.responseStream.flush();
        this.responseStream.close();
    }

    /**
     * @param code          custom response code
     * @param json          Json to be sent as Response .
     * @param customHeaders headers to be sent with the response .
     * @throws IOException throws exception if response channel closed .
     */
    public void sendJsonResponse(int code, String json, Map<String, String> customHeaders) throws IOException {
        if (json != null) {
            byte[] data = json.getBytes("UTF-8");
            sendResponseHeader(code, ServerHelper.CONTENT_TYPE.JSON, data.length, customHeaders);
            this.responseStream.write(data);
        }
        this.responseStream.flush();
        this.responseStream.close();
    }

    /**
     * @param code custom response code
     * @param json Json to be sent as Response .
     * @throws IOException throws exception if response channel closed .
     */
    public void sendJsonResponse(int code, String json) throws IOException {
        if (json != null) {
            byte[] data = json.getBytes("UTF-8");
            sendResponseHeader(code, ServerHelper.CONTENT_TYPE.JSON, data.length);
            this.responseStream.write(data);
        }
        this.responseStream.flush();
        this.responseStream.close();
    }

    /**
     * @param code          custom response code
     * @param filename      filename for the html file to be sent .
     * @param customHeaders headers to be sent with the response .
     * @throws IOException throws exception if response channel closed .
     */
    // HTML Response ..
    public void sendHtmlFileResponseWithCustomHeader(int code, String filename, Map<String, String> customHeaders) throws IOException {

        String page = ServerHelper.getHtmlFromAsset(context, filename);

        byte[] data = page.getBytes("UTF-8");
        sendResponseHeader(code, ServerHelper.CONTENT_TYPE.HTML, data.length, customHeaders);

        this.responseStream.write(data);
        this.responseStream.flush();
        this.responseStream.close();
    }

    /**
     * @param code     custom response code
     * @param filename filename for the html file to be sent .
     * @throws IOException throws exception if response channel closed .
     */
    public void sendHtmlFileResponse(int code, String filename) throws IOException {

        String page = ServerHelper.getHtmlFromAsset(context, filename);

        byte[] data = page.getBytes("UTF-8");
        sendResponseHeader(code, ServerHelper.CONTENT_TYPE.HTML, data.length);

        this.responseStream.write(data);
        this.responseStream.flush();
        this.responseStream.close();
    }

    /**
     * @param code         custom response code
     * @param filename     filename for the html file to be sent .
     * @param placeHolders data values to be replaced with placeholders in the file to be sent with the response .
     * @throws IOException throws exception if response channel closed .
     */
    public void sendHtmlFileResponse(int code, String filename, @NonNull Map<String, String> placeHolders) throws IOException {

        String page = ServerHelper.getHtmlFromAsset(context, filename);
        String value;
        for (String key : placeHolders.keySet()) {
            value = placeHolders.get(key);
            page = page.replace(key, (value != null ? value : ""));
        }

        byte[] data = page.getBytes("UTF-8");
        sendResponseHeader(code, ServerHelper.CONTENT_TYPE.HTML, data.length);

        this.responseStream.write(data);
        this.responseStream.flush();
        this.responseStream.close();
    }

    /**
     * @param code          custom response code
     * @param filename      filename for the html file to be sent .
     * @param placeHolders  data values to be replaced with placeholders in the file to be sent with the response .
     * @param customHeaders headers to be sent with the response .
     * @throws IOException throws exception if response channel closed .
     */
    public void sendHtmlFileResponse(int code, String filename, @NonNull Map<String, String> placeHolders, Map<String, String> customHeaders) throws IOException {

        String page = ServerHelper.getHtmlFromAsset(context, filename);
        String value;
        for (String key : placeHolders.keySet()) {
            value = placeHolders.get(key);
            page = page.replace(key, (value != null ? value : ""));
        }

        byte[] data = page.getBytes("UTF-8");
        sendResponseHeader(code, ServerHelper.CONTENT_TYPE.HTML, data.length, customHeaders);

        this.responseStream.write(data);
        this.responseStream.flush();
        this.responseStream.close();
    }

    /**
     * @param filename     filename for the html file to be sent .
     * @param contentType response content type .
     * @throws IOException throws exception if response channel closed .
     */
    public void sendResourceFileResponse(String filename, String contentType) throws IOException {

        BufferedInputStream file = ServerHelper.getResourceFromAsset(context, filename);
        sendResponseHeader(ServerHelper.RESPONSE_CODE.OK, contentType, file.available());

        int u;
        byte[] buffer = new byte[1024];
        while ((u = file.read(buffer, 0, buffer.length)) != -1) {
            this.responseStream.write(buffer, 0, u);
        }
        file.close();
        this.responseStream.flush();
        this.responseStream.close();
    }

    public Context getContext() {
        return this.context;
    }

    /**
     * @param image image to be sent .
     * @throws IOException throws exception if response channel closed .
     */
    // Response With Image (Response closed after image sent)
    public void sendImageResponse(@NonNull byte[] image) throws IOException {

        sendResponseHeader(ServerHelper.RESPONSE_CODE.OK, ServerHelper.CONTENT_TYPE.JPEG, image.length);

        responseStream.write(image);
        responseStream.flush();
        responseStream.close();
    }

    /**
     * @param sound sound to be sent .
     * @throws IOException throws exception if response channel closed .
     */
    public void sendSoundResponse(@NonNull byte[] sound) throws IOException {

        sendResponseHeader(ServerHelper.RESPONSE_CODE.OK, ServerHelper.CONTENT_TYPE.MPEG, sound.length);

        responseStream.write(sound);
        responseStream.flush();
        responseStream.close();
    }

    // Stream (Response not closed after image sent)
    private void streamImagesToResponse(byte[] image) throws IOException {

        responseStream.writeBytes("--KruscheUndPartnerPartG\r\n");
        responseStream.writeBytes("Content-Type: " + ServerHelper.CONTENT_TYPE.JPEG + "\r\n\r\n");

        responseStream.write(image);

        responseStream.writeBytes("\r\n\r\n");
        responseStream.flush();
    }

    private String getResponseDetails(int code) {
        switch (code) {
            case ServerHelper.RESPONSE_CODE.OK:
                return "OK";
            case ServerHelper.RESPONSE_CODE.BAD_REQUEST:
                return "Bad Request";
            case ServerHelper.RESPONSE_CODE.UNAUTHORIZED:
                return "Unauthorized";
            case ServerHelper.RESPONSE_CODE.FORBIDDEN:
                return "Forbidden";
            case ServerHelper.RESPONSE_CODE.NOT_FOUND:
                return "Not Found";
            case ServerHelper.RESPONSE_CODE.METHOD_NOT_ALLOWED:
                return "Method Not Allowed";
            case ServerHelper.RESPONSE_CODE.INTERNAL_SERVER_ERROR:
                return "Internal Server Error";
            default:
                return "UnKnown";
        }
    }

    private void sendResponseHeader(int code, String contentType, long contentLength) throws IOException {

        String sb = "HTTP/1.1 " + code + " " + getResponseDetails(code) + "\r\n" +
                "Date: " + new Date().toString() + "\r\n" +
                ServerHelper.MAIN_HEADERS.CONTENT_TYPE + ": " + contentType + "\r\n" +
                ((contentLength != -1) ? ServerHelper.MAIN_HEADERS.CONTENT_LENGTH + ": " + contentLength + "\r\n\r\n" : "\r\n");
        this.responseStream.write(sb.getBytes());
    }

    private void sendResponseHeader(int code, String contentType, long contentLength, @NonNull Map<String, String> customHeaders) throws IOException {

        String sb = "HTTP/1.1 " + code + " " + getResponseDetails(code) + "\r\n" +
                "Date: " + new Date().toString() + "\r\n";

        for (String key : customHeaders.keySet()) {
            sb += key + ": " + customHeaders.get(key) + "\r\n";
        }

        sb += ServerHelper.MAIN_HEADERS.CONTENT_TYPE + ": " + contentType + "\r\n" +
                ((contentLength != -1) ? ServerHelper.MAIN_HEADERS.CONTENT_LENGTH + ": " + contentLength + "\r\n\r\n" : "\r\n");
        this.responseStream.write(sb.getBytes());
    }

    public void sendSocketHansShakeHeader(@NonNull Map<String, String> headers) throws IOException {

        String secSocketKey = "";
        for (String head : headers.keySet()) {
            if (head.equals(ServerHelper.MAIN_HEADERS.SEC_WEB_SOCKET_KEY)) {
                secSocketKey = headers.get(head);
            }
        }

        secSocketKey = secSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"; // adding the "magic string"

        secSocketKey = ServerHelper.encodeString(secSocketKey);

        String header = "HTTP/1.1 101 Web Socket Protocol Handshake\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Sec-WebSocket-Accept:" + secSocketKey + "\r\n\r\n";
        //printStream.print(header);

        this.responseStream.write(header.getBytes());
    }
}
