package com.dzenm.naughty.http;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dzenm.core.BaseInterceptor;
import com.dzenm.naughty.Naughty;
import com.dzenm.naughty.R;
import com.dzenm.naughty.service.NaughtyBroadcast;
import com.dzenm.naughty.ui.http.HttpBean;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @author dzenm
 * 2020/8/4
 */
public class HttpInterceptor extends BaseInterceptor {

    private static final String TAG = HttpInterceptor.class.getSimpleName();
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private Naughty mNaughty;
    private Context mContext;

    private final AtomicInteger mNextRequestId = new AtomicInteger(0);

    public HttpInterceptor(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (mNaughty == null) {
            mNaughty = Naughty.getInstance();
        }
        Request request = chain.request();
        if (mNaughty.isDebug()) {
            Log.d(TAG, "request hashCode: " + request.hashCode());

            // 保存请求及返回的数据
            HttpBean bean = new HttpBean();
            mNaughty.add(bean);

            // 请求的ID
            bean.setId(mNextRequestId.getAndIncrement());

            // 请求开始的时间及状态
            bean.setCurrentTime(getCurrentTime());
            bean.setLoadingState(Naughty.START);
            setRequestState(bean);

            // 请求内容
            RequestBody requestBody = request.body();
            boolean hasRequestBody = requestBody != null;

            // 请求协议类型
            Connection connection = chain.connection();
            Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

            // 保存请求的数据
            bean.setProtocol(protocol.toString());
            bean.setRequestUrl(request.url().toString());
            bean.setMethod(request.method());

            Map<String, String> requestHeader = new LinkedHashMap<>();
            Headers requestHeaders = request.headers();
            // 请求头
            for (int i = 0, count = requestHeaders.size(); i < count; i++) {
                requestHeader.put(requestHeaders.name(i), requestHeaders.value(i));
            }
            bean.setRequestHeaders(requestHeader);
            // 请求体
            if (hasRequestBody && !bodyEncoded(request.headers())) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                bean.setRequestSize(String.valueOf(buffer.size()));
                if (isPlaintext(buffer) && charset != null) {
                    bean.setRequestBody(buffer.readString(charset));
                } else {
                    bean.setRequestBody("binary: " + buffer.size() + "-byte body omitted)");
                }
            } else {
                bean.setRequestBody("");
            }

            // 等待响应
            bean.setLoadingState(Naughty.RUNNING);
            setRequestState(bean);

            Response response;
            long tookMs;
            long startNs = System.nanoTime();
            try {
                response = chain.proceed(request);
            } catch (Exception e) {
                tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
                bean.setStatus("");
                bean.setMessage("");
                bean.setTime(String.valueOf(tookMs));
                bean.setFromDiskCache(false);
                bean.setConnectionId(connection == null ? 0 : connection.hashCode());

                bean.setResponseSize("");
                bean.setResponseBody(e.getMessage());

                bean.setLoadingState(Naughty.STOP);
                setRequestState(bean);
                throw e;
            }

            tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

            // 保存返回的数据
            bean.setStatus(String.valueOf(response.code()));
            bean.setMessage(response.message());
            bean.setMessage(response.request().url().toString());
            bean.setTime(String.valueOf(tookMs));
            bean.setResponseUrl(response.request().url().toString());
            bean.setFromDiskCache(response.cacheResponse() != null);
            bean.setConnectionId(connection == null ? 0 : connection.hashCode());

            Map<String, String> responseHeader = new LinkedHashMap();
            Headers responseHeaders = response.headers();
            for (int i = 0, count = responseHeaders.size(); i < count; i++) {
                responseHeader.put(responseHeaders.name(i), responseHeaders.value(i));
            }
            bean.setResponseHeaders(responseHeader);

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                // 返回的请求体内容
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);     // Buffer the entire body.
                Buffer buffer = source.getBuffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                bean.setResponseSize(String.valueOf(buffer.size()));
                if (!isPlaintext(buffer)) {
                    bean.setResponseBody("binary: " + buffer.size() + "-byte body omitted)");
                    return response;
                }
                if (charset != null) {
                    bean.setResponseBody(buffer.clone().readString(charset));
                }
            } else {
                bean.setResponseBody("");
            }

            bean.setLoadingState(Naughty.STOP);
            setRequestState(bean);
            return response;
        }
        return chain.proceed(request);
    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    /**
     * 设置请求的状态
     *
     * @param bean 请求保存的数据
     */
    private void setRequestState(HttpBean bean) {
        Naughty.OnRequestListener listener = mNaughty.getOnRequestListener();
        if (listener != null) {
            listener.onInterceptor(bean, mNaughty.indexOf(bean));
        }
        if (checkNotificationEnabled(mContext) && mNaughty.isShowNotification()) {
            Log.d(TAG, "show notification");
            String status = bean.getStatus();
            status = mNaughty.isHttpFinished(bean.getLoadingState()) ? status : "...";
            createNotification(mContext, bean.getId(), status, bean.getRequestUrl());
        }
    }

    /**
     * 检查是否打开通知开关
     *
     * @param context 上下文
     * @return 是否打开通知开关
     */
    private boolean checkNotificationEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * 创建一条新的通知
     *
     * @param id     通知的ID
     * @param status 通知显示的状态
     * @param url    通知显示的URL
     */
    private void createNotification(Context context, int id, String status, String url) {
        // 创建通知渠道实例并为它设置属性
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;
        String channelId = String.valueOf(id);                  // 通知渠道的ID
        Uri sound = null;                                       // 通知的声音

        // 构建NotificationChannel实例
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HTTP请求";                // 用户可以看到的通知渠道的名字
            String description = "一个HTTP请求提示的通知";  // 用户可看到的通知描述
            NotificationChannel channel = new NotificationChannel(
                    channelId, name, NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(description);            // 配置通知渠道的属性
            channel.enableLights(true);                     // 设置通知出现时的闪光灯
            channel.setLightColor(Color.RED);               // 设置通知出现的闪光灯颜色
            channel.setSound(sound, null);   // 设置通知时的声音
            channel.enableVibration(true);                  // 设置通知出现时的震动
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 100});
            manager.createNotificationChannel(channel);     // 在notificationManager中创建通知渠道
        }

        // 点击通知之后要发送的广播
        Intent intent = new Intent(context, NaughtyBroadcast.class);
        intent.setAction(NaughtyBroadcast.ACTION_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, 0
        );

        String text = status + "  " + Uri.parse(url).getPath();
        Notification notification = new NotificationCompat.Builder(context, channelId)
                // 设置优先级
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)                        // 设置自动取消
                .setSmallIcon(R.drawable.ic_network)        // 设置小图标
                .setContentTitle("HTTP Request For Debug")  // 设置标题
                .setContentText(text)                       // 设置内容
                .setSound(sound)                            // 设置通知提示音, 8.0以上使用NotificationChannel设置
                .setContentIntent(pendingIntent)            // 设置点击跳转事件
                // 设置振动, 需要添加权限<uses-permission android:name="android.permission.VIBRATE"/>
                .setVibrate(new long[]{0, 1000, 1000, 1000})
                // 设置前置LED灯进行闪烁, 第一个为颜色值, 第二个为亮的时长, 第三个为暗的时长
                .setLights(Color.GREEN, 1000, 1000)
                .setDefaults(NotificationCompat.DEFAULT_ALL)// 使用默认效果, 根据手机当前环境播放铃声、振动
                .setShowWhen(true)                          // 设置是否显示时间
                .setWhen(System.currentTimeMillis())        // 设置时间
                .build();
        manager.notify(id, notification);
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

}
