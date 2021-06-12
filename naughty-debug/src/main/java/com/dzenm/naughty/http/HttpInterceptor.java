package com.dzenm.naughty.http;

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
import com.dzenm.naughty.http.model.HttpBean;
import com.dzenm.naughty.service.NaughtyBroadcast;
import com.dzenm.naughty.util.SettingUtils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
 * <p>
 * Http请求拦截器，将请求的数据保存，使用通知显示请求的内容
 */
public class HttpInterceptor extends BaseInterceptor {

    private static final String TAG = HttpInterceptor.class.getSimpleName();
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private final Context mContext;
    private final AtomicInteger mNextRequestId = new AtomicInteger(0);

    public HttpInterceptor(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Log.d(TAG, "request hashCode: " + request.hashCode());
        if (!Naughty.getInstance().isDebug() && !SettingUtils.isEnabledHttpInterceptor(mContext)) {
            return chain.proceed(request);
        }

        // 保存请求及返回的数据
        HttpBean bean = new HttpBean();
        Naughty.getInstance().add(bean);

        // 请求的ID
        bean.setId(mNextRequestId.getAndIncrement());
        // 请求开始的时间及状态
        bean.setCurrentTime(String.valueOf(System.currentTimeMillis()));

        //=======================================开始请求============================================
        updateRequestState(bean, Naughty.START);

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

        // 请求头
        Map<String, String> requestHeader = new LinkedHashMap<>();
        Headers requestHeaders = request.headers();
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
        }

        //=======================================等待响应============================================
        updateRequestState(bean, Naughty.RUNNING);

        Response response;
        long tookMs;
        long startNs = System.nanoTime();
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            bean.setTime(String.valueOf(tookMs));
            bean.setResponseBody(e.getMessage());
            //=======================================请求异常========================================
            updateRequestState(bean, Naughty.STOP);
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

        Map<String, String> responseHeader = new LinkedHashMap<>();
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
        }

        //=======================================请求结束============================================
        updateRequestState(bean, Naughty.STOP);
        return response;
    }

    /**
     * 更新请求的状态
     *
     * @param bean  请求保存的数据
     * @param state 请求的状态
     */
    private void updateRequestState(HttpBean bean, int state) {
        bean.setLoadingState(state);
        Naughty.getInstance().update(bean);
        // 检查通知权限是否打开, 通知开关是否打开
        if (NotificationManagerCompat.from(mContext).areNotificationsEnabled()
                && SettingUtils.isEnabledNotification(mContext)) {
            Log.d(TAG, "show notification");
            String status = Naughty.getInstance().isHttpFinished(bean.getLoadingState())
                    ? bean.getStatus() : "...";
            String title = status + "  " + Uri.parse(bean.getRequestUrl()).getPath();
            createNotification(mContext, bean.getId(), title);
        }
    }

    /**
     * 创建一条新的通知
     *
     * @param id    通知的ID
     * @param title 通知显示的标题
     */
    private void createNotification(Context context, int id, String title) {
        // 创建通知渠道实例并为它设置属性
        NotificationManager manager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        String channelId = String.valueOf(id);                  // 通知渠道的ID
        Uri sound = null;                                       // 通知的声音

        // 构建NotificationChannel实例
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HTTP请求";                   // 用户可以看到的通知渠道的名字
            String description = "HTTP请求提示的通知";         // 用户可看到的通知描述
            int level = SettingUtils.getNotificationLevel(context);
            int importance;
            if (level == 0) {
                importance = NotificationManager.IMPORTANCE_HIGH;
            } else if (level == 1) {
                importance = NotificationManager.IMPORTANCE_MIN;
            } else {
                importance = NotificationManager.IMPORTANCE_DEFAULT;
            }
            Log.d(TAG, "notification importance: " + importance);
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);                // 配置通知渠道的属性
            channel.enableLights(true);                         // 设置通知出现时的闪光灯
            channel.setLightColor(Color.RED);                   // 设置通知出现的闪光灯颜色
            if (SettingUtils.isEnabledNotificationSoundAndVibration(context)) {
                channel.setSound(sound, null);   // 设置通知时的声音
                channel.enableVibration(true);                  // 设置通知出现时的震动
                channel.setVibrationPattern(new long[]{0, 180, 80, 120});
            } else {
                channel.enableVibration(false);                 // 设置通知出现时的震动
            }
            channel.enableLights(true);                         // 是否开启指示灯（是否在桌面icon右上角展示小红点）
            channel.setLightColor(Color.RED);                   // 设置指示灯颜色
            channel.setBypassDnd(true);                         // 设置绕过免打扰模式
            // 设置是否应在锁定屏幕上显示此频道的通知
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setShowBadge(true);                         // 设置是否在长按桌面图标时显示此渠道的通知
            manager.createNotificationChannel(channel);
        }

        // 点击通知之后要发送的广播
        Intent intent = new Intent(context, NaughtyBroadcast.class);
        intent.setAction(NaughtyBroadcast.NAUGHTY_ACTION_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, 0
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);// 设置优先级
        builder.setAutoCancel(true);                        // 设置自动取消
        builder.setSmallIcon(R.drawable.ic_network);        // 设置小图标
        // 设置标题
        builder.setContentTitle(title);
        builder.setContentIntent(pendingIntent);            // 设置点击跳转事件
        if (!SettingUtils.isEnabledNotificationSoundAndVibration(context)) {
            builder.setSound(sound);                        // 设置通知时的声音
            // 设置振动, 需要添加权限<uses-permission android:name="android.permission.VIBRATE"/>
            builder.setVibrate(null);
            builder.setVibrate(new long[]{0});
        }
        builder.setShowWhen(true);                          // 设置是否显示时间
        builder.setWhen(System.currentTimeMillis());        // 设置时间
        manager.notify(id, builder.build());
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
