package com.dzenm.naughty;

import android.annotation.SuppressLint;

import com.dzenm.core.BaseNaughty;

/**
 * @author dzenm
 * <p>
 * 第一, 请确保添加一下依赖库
 * implementation 'androidx.recyclerview:recyclerview:1.1.0'
 * implementation 'com.squareup.okhttp3:okhttp:3.14.0'
 * <p>
 * 第二, 在Okhttp中添加Interceptor
 * builder.addInterceptor(Naughty.newInstance().setDebug(true).get(this));
 */
public class Naughty extends BaseNaughty {

    @SuppressLint("StaticFieldLeak")
    private static volatile Naughty sInstance;

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
}
