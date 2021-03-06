package com.dzenm.naughty.http.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.dzenm.naughty.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author dzenm
 * 2020/8/4
 */
public class HttpBean implements Parcelable {

    private int id = 0;
    private String currentTime = "";
    private int loadingState;                   // 1-START, 2-RUNNING, 3-STOP

    private String protocol = "";               // 请求协议:
    private String method = "";                 // 请求方法: GET
    private String requestUrl = "";             // 请求url:  https://baidu.com
    private String requestSize = "";            // 请求数据大小:  7-byte
    private String requestBody = "";            // 请求携带的内容
    private Map<String, String> requestHeaders = new LinkedHashMap<>();

    private String status = "";
    private String time = "";                   // 请求所用的时间: 3211ms
    private String responseSize = "";           // 请求返回的大小: 524-byte
    private String responseUrl = "";            // 请求返回的url
    private boolean fromDiskCache = false;      // 是否从缓存获取
    private int connectionId = 0;               // 连接的Id
    private String message = "";                // 请求返回的结果
    private String responseBody = "";           // 请求返回的结果
    private Map<String, String> responseHeaders = new LinkedHashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrentTime() {
        return TextUtils.isEmpty(currentTime) ? "00:00:00 SSS" : StringUtils.formatDate(currentTime);
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

    public String getRequestUrl() {
        return TextUtils.isEmpty(requestUrl) ? "unknown url" : requestUrl;
    }

    public void setRequestUrl(String url) {
        this.requestUrl = url;
    }

    public String getRequestSize() {
        long size = TextUtils.isEmpty(requestSize) ? 0 : Long.parseLong(requestSize);
        return StringUtils.formatFileSize(size);
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
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Map<String, String> getRequest() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Request Protocol", getProtocol());
        map.put("Request Method", getMethod());
        map.put("Request URL", getRequestUrl());
        map.put("Content-Length", getRequestSize());
        if (requestHeaders.size() != 0) {
            map.putAll(requestHeaders);
        }

        map.put("Request Body", getRequestBody());
        return map;
    }

    public String getStatus() {
        return TextUtils.isEmpty(status) ? "-1" : status;
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
        return StringUtils.formatFileSize(size);
    }

    public void setResponseSize(String responseSize) {
        this.responseSize = responseSize;
    }

    public String getResponseUrl() {
        return TextUtils.isEmpty(responseUrl) ? "unknown url" : responseUrl;
    }

    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
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
        map.put("Response Url", getResponseUrl());
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
        requestUrl = in.readString();
        requestSize = in.readString();
        requestBody = in.readString();
        requestHeaders = in.readHashMap(HashMap.class.getClassLoader());

        status = in.readString();
        time = in.readString();
        responseSize = in.readString();
        responseUrl = in.readString();
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
        dest.writeString(requestUrl);
        dest.writeString(requestSize);
        dest.writeString(requestBody);
        dest.writeMap(requestHeaders);

        dest.writeString(status);
        dest.writeString(time);
        dest.writeString(responseSize);
        dest.writeString(responseUrl);
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
}
