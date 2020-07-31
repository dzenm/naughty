package com.dzenm.core;

import android.content.Context;

import okhttp3.Interceptor;

/**
 * @author dzenm
 */
public abstract class BaseNaughty {

    /**
     * 是否显示悬浮窗
     */
    protected boolean isShowing = false;

    /**
     * 是否是debug模式
     */
    protected boolean isDebug = true;

    /**
     * 是否显示通知提示
     */
    protected boolean isShowNotification = true;

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

    /**
     * 显示悬浮窗
     */
    public void show() {

    }

    /**
     * 隐藏悬浮窗
     */
    public void dismiss() {

    }

    /**
     * @see #isShowNotification
     */
    public boolean isShowNotification() {
        return isShowNotification;
    }

    /**
     * @see #isShowNotification()
     */
    public BaseNaughty setShowNotification(boolean showNotification) {
        isShowNotification = showNotification;
        return this;
    }

}