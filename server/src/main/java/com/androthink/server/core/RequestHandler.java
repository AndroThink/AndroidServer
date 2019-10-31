package com.androthink.server.core;

import android.util.Log;

import com.androthink.server.helper.ServerHelper;
import com.androthink.server.model.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class RequestHandler {

    private final BufferedReader requestStream;
    private final Request request;

    static RequestHandler getInstance(Request request,BufferedReader requestStream){
        return new RequestHandler(request,requestStream);
    }

    private RequestHandler(Request request,BufferedReader requestStream) {
        this.requestStream = requestStream;
        this.request = request;
    }

    Request extract() throws IOException ,JSONException{
        String headerLine = requestStream.readLine(); // ===> POST / HTTP/1.1

        if(headerLine == null)
            return request;

        Scanner scanner =   new Scanner(headerLine);

        this.request.setMethod(scanner.next().toLowerCase());     //===> POST
        this.request.setRoutePath(scanner.next().toLowerCase()); // ===> /
        this.request.setProtocol(scanner.next());               // ===> HTTP/1.1

        scanner.close();

        // Headers ...
        Map<String,String> requestHeaders = new HashMap<>();

        while((headerLine = requestStream.readLine()).length() != 0){
            scanner = new Scanner(headerLine);

            String key = scanner.next().replace(":","");
            StringBuilder value = new StringBuilder();

            while (scanner.hasNext()){ value.append(scanner.next()); }

            requestHeaders.put(key.toLowerCase(), value.toString());

            scanner.close();
        }

        // Review Content Type & if Form-Data ==> get Boundary
        String contentTypeHeader = requestHeaders.get(ServerHelper.MAIN_HEADERS.CONTENT_TYPE);
        if(contentTypeHeader != null && contentTypeHeader.toLowerCase().contains(ServerHelper.CONTENT_TYPE.FORM_DATA)) {
            // multipart/form-data; boundary=--------------------------809476465016676300543428
            String[] data = contentTypeHeader.split(";");
            requestHeaders.put(ServerHelper.MAIN_HEADERS.CONTENT_TYPE, data[0]);
            requestHeaders.put(ServerHelper.MAIN_HEADERS.CONTENT_FORM_DATA_BOUNDARY, data[1].replace("boundary=", ""));
        }

        this.request.setHeaders(requestHeaders);

        // Payload
        extractPayload();

        return request;
    }

    private void extractPayload() throws IOException,JSONException{
        StringBuilder payload = new StringBuilder();
        while(requestStream.ready()){
            payload.append((char) requestStream.read());
        }
        String contentType = request.getHeaders().get(ServerHelper.MAIN_HEADERS.CONTENT_TYPE);

        if(contentType != null){
            Object requestPayload = null;

            switch (contentType) {
                case ServerHelper.CONTENT_TYPE.TEXT:
                    requestPayload = payload.toString();
                    break;
                case ServerHelper.CONTENT_TYPE.JSON:
                    requestPayload = new JSONObject(payload.toString());
                    break;
                case ServerHelper.CONTENT_TYPE.FORM_URL_ENCODED_DATA:
                    String[] formParams = payload.toString().split("&");
                    if(formParams.length>0) {
                        requestPayload = new JSONObject();
                        for (String formParam : formParams) {
                            String[] details = formParam.split("=");
                            if(details.length == 2) {
                                ((JSONObject) requestPayload).put(details[0], details[1]);
                            }
                        }
                    }
                    break;
                case ServerHelper.CONTENT_TYPE.FORM_DATA: {
                    String boundary = request.getHeaders().get(ServerHelper.MAIN_HEADERS.CONTENT_FORM_DATA_BOUNDARY);

                    if (boundary != null) {
                        String[] params = payload.toString().split(boundary);

                        if (params.length > 1) {

                            requestPayload = new JSONObject();

                            String param;
                            for (int i = 0; i < params.length - 1; i++) {
                                param = params[i];
                                if (param.isEmpty() || param.equals("--"))
                                    continue;

                                Scanner scanner = new Scanner(param);

                                String key = scanner.next().replace(":", ""); // Content-Disposition
                                String content = scanner.next().replace(";", ""); // form-data
                                Log.e("RequestPayload", "Key : " + key + " , Content : " + content);

                                String name = scanner.next().replace("name=\"", "").replace("\"", "");

                                // Todo -- Handle Files ..

                                StringBuilder value = new StringBuilder();
                                String tempValue;
                                while (scanner.hasNext()) {
                                    tempValue = scanner.next();
                                    if (!tempValue.equals("--"))
                                        value.append(tempValue);
                                }

                                scanner.close();

                                ((JSONObject) requestPayload).put(name, value.toString());
                            }
                        }
                    }
                }
                break;
            }

            request.setPayload(requestPayload);
        }
    }
}
