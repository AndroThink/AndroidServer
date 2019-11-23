package com.androthink.server.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ServerHelper {

    public static char API_VERSION = '1';

    public void setApiVersion(char version) {
        API_VERSION = version;
    }

    public class MAIN_HEADERS {
        public static final String CONTENT_TYPE = "content-type";
        public static final String AUTHORIZATION = "authorization";
        public static final String CONTENT_LENGTH = "content-length";
        public static final String SEC_WEB_SOCKET_KEY = "sec-websocket-key";
        public static final String SEC_WEB_SOCKET_ACCEPT = "Sec-WebSocket-Accept";
        public static final String CONTENT_FORM_DATA_BOUNDARY = "boundary";
    }

    public class METHOD {
        public static final String GET = "get";
        public static final String PUT = "put";
        public static final String POST = "post";
        public static final String DELETE = "delete";
    }

    public class RESPONSE_CODE {
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

        public static final String PNG = "image/png";
        public static final String GIF = "image/gif";
        public static final String JPEG = "image/jpeg";

        public static final String MPEG = "audio/mpeg";
    }

    /**
     * Load files from raw folder.
     *
     * @param filename filename of the file .
     * @param isUTF8   is utf8 or not .
     * @return byte[] value of File
     * @throws java.io.IOException if error occurs
     */
    @NonNull
    public static byte[] loadFileFromAsset(@NonNull Context context, String filename, boolean isUTF8) throws IOException {
        return isUTF8 ? toUTF8Bytes(context.getAssets().open(filename)) : toBytes(context.getAssets().open(filename));
    }

    /**
     * Load files from raw folder.
     *
     * @param rawId raw resource id of the file .
     * @param isUTF8   is utf8 or not .
     * @return byte[] value of File
     * @throws java.io.IOException if error occurs
     */
    @NonNull
    public static byte[] loadFileFromRaw(@NonNull Context context, @RawRes int rawId, boolean isUTF8) throws IOException {
        return isUTF8 ? toUTF8Bytes(context.getResources().openRawResource(rawId)) : toBytes(context.getResources().openRawResource(rawId));
    }

    /**
     * Load files from storage.
     *
     * @param filename which to be converted to string
     * @param isUTF8   is utf8 or not .
     * @return byte[] value of File
     * @throws java.io.IOException if error occurs
     */
    @NonNull
    public static byte[] loadFileAsBytes(String filename, boolean isUTF8) throws IOException {
        return isUTF8 ? toUTF8Bytes(new FileInputStream(filename)) : toBytes(new FileInputStream(filename));
    }

    @NonNull
    public static byte[] convertBitmapToByte(@NonNull Bitmap bitmap, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap convertByteToBitmap(byte[] imageByte) {
        if (imageByte == null)
            return null;
        return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
    }

    public static Bitmap convertDrawableToBitmap(@NonNull Context context, @DrawableRes int drawableRes) throws Resources.NotFoundException {
        return BitmapFactory.decodeResource(context.getResources(), drawableRes);
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
        final int bufferSize = 1024;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        inputStream.close();
        return byteBuffer.toByteArray();
    }

    @NonNull
    private static byte[] toUTF8Bytes(@NonNull InputStream inputStream) throws IOException {
        final int bufferSize = 1024;

        BufferedInputStream is = new BufferedInputStream(inputStream, bufferSize);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bufferSize);

        byte[] bytes = new byte[bufferSize];
        int read, count = 0;

        while ((read = is.read(bytes)) != -1) {
            if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                byteArrayOutputStream.write(bytes, 3, read - 3);
            } else {
                byteArrayOutputStream.write(bytes, 0, read);
            }
            count += read;
        }

        inputStream.close();
        is.close();

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes toConvert
     * @return hexValue
     */
    @NonNull
    public static String bytesToHex(@NonNull byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            int intVal = aByte & 0xff;
            if (intVal < 0x10) result.append("0");
            result.append(Integer.toHexString(intVal).toUpperCase());
        }
        return result.toString();
    }
}

