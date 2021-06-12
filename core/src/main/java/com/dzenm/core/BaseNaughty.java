package com.dzenm.core;

import android.content.Context;

import okhttp3.Interceptor;

/**
 * @author dzenm
 */
public abstract class BaseNaughty {

    /**
     * 是否是debug模式
     */
    protected boolean isDebug = true;

    public BaseNaughty setDebug(boolean debug) {
        isDebug = debug;
        return this;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public Interceptor get(Context context) {
        return new BaseInterceptor();
    }
}