package com.dzenm.lib.ui;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpBean implements Parcelable {

    private int id;
    private String currentTime;
    private int loadingState;                   // 1-START, 2-RUNNING, 3-STOP

    private String protocol;
    private String method;
    private String url;
    private String requestSize;
    private String requestBody;
    private Map<String, String> requestHeaders = new LinkedHashMap<>();

    private String status;
    private String time;                        // 请求所用的时间: 3211ms
    private String responseSize;                // 请求返回的大小: 524-byte
    private boolean fromDiskCache;              // 是否从缓存获取
    private int connectionId;                   // 连接的Id
    private String message;                     // 请求返回的结果
    private String responseBody;                // 请求返回的结果
    private Map<String, String> responseHeaders = new LinkedHashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrentTime() {
        return TextUtils.isEmpty(currentTime) ? "00:00:00" : currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public int getLoadingState() {
        return loadingState;
    }

    public void setLoadingState(int loadingState) {
        this.loadingState = loadingState;
    }

    public String getProtocol() {
        return TextUtils.isEmpty(protocol) ? "unknown protocol" : protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getMethod() {
        return TextUtils.isEmpty(method) ? "unknown method" : method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return TextUtils.isEmpty(url) ? "unknown url" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestSize() {
        long size = TextUtils.isEmpty(requestSize) ? 0 : Long.parseLong(requestSize);
        return formatSize(size);
    }

    public void setRequestSize(String requestSize) {
        this.requestSize = requestSize;
    }

    public String getRequestBody() {
        return TextUtils.isEmpty(requestBody) ? "(no body)" : requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders == null ? new LinkedHashMap<String, String>() : requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Map<String, String> getRequest() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Request Protocol", getProtocol());
        map.put("Request Method", getMethod());
        map.put("Request URL", getUrl());
        map.put("Content-Length", getRequestSize());
        if (requestHeaders.size() != 0) {
            map.putAll(requestHeaders);
        }
        map.put("Request Body", getRequestBody());
        return map;
    }

    public String getStatus() {
        return TextUtils.isEmpty(status) ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return (TextUtils.isEmpty(time) ? "0" : time) + " ms";
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResponseSize() {
        long size = TextUtils.isEmpty(responseSize) ? 0 : Long.parseLong(responseSize);
        return formatSize(size);
    }

    public void setResponseSize(String responseSize) {
        this.responseSize = responseSize;
    }

    public boolean isFromDiskCache() {
        return fromDiskCache;
    }

    public void setFromDiskCache(boolean fromDiskCache) {
        this.fromDiskCache = fromDiskCache;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseBody() {
        return TextUtils.isEmpty(responseBody) ? "(no body)" : responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders == null ? new LinkedHashMap<String, String>() : responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public Map<String, String> getResponse() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Status Code", getStatus());
        map.put("Duration Time", getTime());
        map.put("Content-Length", getResponseSize());
        if (responseHeaders.size() != 0) {
            map.putAll(responseHeaders);
        }
        map.put("Response Body", getResponseBody());
        return map;
    }

    public static Creator<HttpBean> getCREATOR() {
        return CREATOR;
    }

    public HttpBean() {

    }

    protected HttpBean(Parcel in) {
        id = in.readInt();
        currentTime = in.readString();

        protocol = in.readString();
        method = in.readString();
        url = in.readString();
        requestSize = in.readString();
        requestBody = in.readString();
        requestHeaders = in.readHashMap(HashMap.class.getClassLoader());

        status = in.readString();
        time = in.readString();
        responseSize = in.readString();
        fromDiskCache = in.readByte() != 0;
        connectionId = in.readInt();
        message = in.readString();
        responseBody = in.readString();
        responseHeaders = in.readHashMap(HashMap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(currentTime);

        dest.writeString(protocol);
        dest.writeString(method);
        dest.writeString(url);
        dest.writeString(requestSize);
        dest.writeString(requestBody);
        dest.writeMap(requestHeaders);

        dest.writeString(status);
        dest.writeString(time);
        dest.writeString(responseSize);
        dest.writeByte((byte) (fromDiskCache ? 1 : 0));
        dest.writeInt(connectionId);
        dest.writeString(message);
        dest.writeString(responseBody);
        dest.writeMap(responseHeaders);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HttpBean> CREATOR = new Creator<HttpBean>() {
        @Override
        public HttpBean createFromParcel(Parcel in) {
            return new HttpBean(in);
        }

        @Override
        public HttpBean[] newArray(int size) {
            return new HttpBean[size];
        }
    };

    private String formatSize(long size) {
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return size + " B";
        } else {
            size = size / 1024;
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候，接下去以此类推
        if (size < 1024) {
            return size + " KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            // 因为如果以MB为单位的话，要保留最后1位小数，
            // 因此，把此数乘以100之后再取余
            size = size * 100;
            return (size / 100) + "." + (size % 100) + " MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return (size / 100) + "." + (size % 100) + " GB";
        }
    }

}
