package com.dzenm.naughty;

import android.annotation.SuppressLint;
import android.content.Context;

import com.dzenm.naughty.http.HttpInterceptor;

import okhttp3.Interceptor;

/**
 * @author dzenm
 * <p>
 * 第一, 请确保添加一下依赖库
 * implementation 'androidx.recyclerview:recyclerview:1.1.0'
 * implementation 'com.squareup.okhttp3:okhttp:3.14.0'
 * <p>
 * 第二, 在Okhttp中添加Interceptor
 * Naughty.get(this)
 */
public class Naughty {

    @SuppressLint("StaticFieldLeak")
    private static volatile Naughty sInstance;

    /**
     * 是否是debug模式
     */
    private boolean isDebug = false;

    public static Naughty getInstance() {
        if (sInstance == null) {
            synchronized (Naughty.class) {
                if (sInstance == null) {
                    sInstance = new Naughty();
                }
            }
        }
        return sInstance;
    }

    public Naughty setDebug(boolean debug) {
        isDebug = debug;
        return this;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public HttpInterceptor get(Context context) {
        return new HttpInterceptor(context);
    }

}
