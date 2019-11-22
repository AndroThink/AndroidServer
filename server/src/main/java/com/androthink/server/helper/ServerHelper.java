package com.androthink.server.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ServerHelper {

    public static char API_VERSION = '1';

    public void setApiVersion(char version){API_VERSION = version;}

    public class MAIN_HEADERS{
        public static final String CONTENT_TYPE = "content-type";
        public static final String AUTHORIZATION = "authorization";
        public static final String CONTENT_LENGTH = "content-length";
        public static final String SEC_WEB_SOCKET_KEY = "sec-websocket-key";
        public static final String SEC_WEB_SOCKET_ACCEPT = "Sec-WebSocket-Accept";
        public static final String CONTENT_FORM_DATA_BOUNDARY = "boundary";
    }

    public class METHOD{
        public static final String GET = "get";
        public static final String PUT = "put";
        public static final String POST = "post";
        public static final String DELETE = "delete";
    }

    public class RESPONSE_CODE{
        public static final int OK = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int METHOD_NOT_ALLOWED = 405;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    public class CONTENT_TYPE {
        public static final String PDF = "application/pdf";
        public static final String JSON = "application/json";

        public static final String FORM_DATA = "multipart/form-data";
        public static final String FORM_URL_ENCODED_DATA = "application/x-www-form-urlencoded";

        public static final String HTML = "text/html";
        public static final String CSS = "text/css";
        public static final String TEXT = "text/plain";

        public static final String TEXT_JAVA_SCRIPT = "text/javascript";
        public static final String JAVA_SCRIPT = "application/javascript";

        public static final String PNG  = "image/png";
        public static final String GIF  = "image/gif";
        public static final String JPEG = "image/jpeg";

        public static final String MPEG = "audio/mpeg";
    }

    @NonNull
    public static byte[] getFileFromAssets(@NonNull Context context,String filename)throws IOException{
        InputStream is = context.getAssets().open(filename);
        return toBytes(is);
    }

    @NonNull
    public static byte[] getFileFromRaw(@NonNull Context context,@RawRes int imageRawId) throws IOException{
        InputStream image = context.getResources().openRawResource(imageRawId);
        return toBytes(image);
    }

    @NonNull
    public static String getHtmlFromAsset(@NonNull Context context,String filename) throws IOException {
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        reader.close();
        return builder.toString();
    }

    public static BufferedInputStream getResourceFromAsset(@NonNull Context context,String filename)throws IOException {
       return new BufferedInputStream(context.getAssets().open(filename));
    }

    @NonNull
    public static byte[] convertBitmapToByte(@NonNull Bitmap bitmap,int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap resizedBitmap(@NonNull Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();

        return resizedBitmap;
    }

    @NonNull
    public static String generateRandomId() {
        return encodeString(UUID.randomUUID().toString());
    }

    public static String encodeString(@NonNull String input) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
            byte[] inputBytes = input.getBytes();
            byte[] hashBytes = digest.digest(inputBytes);
            return Base64.encodeToString(hashBytes, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return input;
    }

    @NonNull
    private static byte[] toBytes(@NonNull InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        inputStream.close();

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
}

